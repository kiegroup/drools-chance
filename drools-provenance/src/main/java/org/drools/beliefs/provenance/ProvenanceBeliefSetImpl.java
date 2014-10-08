package org.drools.beliefs.provenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clarkparsia.empire.annotation.RdfsClass;
import org.drools.beliefs.provenance.annotations.Display;
import org.drools.beliefs.provenance.annotations.Evidence;
import org.drools.beliefs.provenance.templates.TemplateRegistry;
import org.drools.core.InitialFact;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.metadata.Don;
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.metadata.Modify;
import org.drools.core.metadata.ModifyTask;
import org.drools.core.metadata.NewInstance;
import org.drools.core.metadata.Shed;
import org.drools.core.metadata.WorkingMemoryTask;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.Activation;
import org.drools.core.util.Drools;
import org.drools.semantics.Literal;
import org.jboss.drools.provenance.AssertionImpl;
import org.jboss.drools.provenance.DerogationImpl;
import org.jboss.drools.provenance.Instance;
import org.jboss.drools.provenance.InstanceImpl;
import org.jboss.drools.provenance.Modification;
import org.jboss.drools.provenance.ModificationImpl;
import org.jboss.drools.provenance.Narrative;
import org.jboss.drools.provenance.NarrativeImpl;
import org.jboss.drools.provenance.PropertyImpl;
import org.jboss.drools.provenance.RecognitionImpl;
import org.jboss.drools.provenance.RetractionImpl;
import org.jboss.drools.provenance.Rule;
import org.jboss.drools.provenance.RuleEngine;
import org.jboss.drools.provenance.TypificationImpl;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;
import org.w3.ns.prov.Activity;
import org.w3.ns.prov.ActivityImpl;
import org.w3.ns.prov.Agent;

public class ProvenanceBeliefSetImpl<M extends ProvenanceBelief<M>>
        extends JTMSBeliefSetImpl<M>
        implements ProvenanceBeliefSet<M> {

    private List<Activity> provenance = new ArrayList<Activity>();

    private static RuleEngine DROOLS_ENGINE = new org.jboss.drools.provenance.RuleEngineImpl()
                                                .withIdentifier( new Literal( "JBoss Drools " + Drools.getFullVersion() ) );

    public ProvenanceBeliefSetImpl( BeliefSystem<M> beliefSystem, InternalFactHandle rootHandle ) {
        super( beliefSystem, rootHandle );
    }

    @Override
    public void add( M node ) {
        super.add( node );
        recordActivity( node, true );
    }

    @Override
    public void remove( M node ) {
        super.remove( node );
        recordActivity( node, false );
    }
    
    private void recordActivity( M node, boolean positiveAssertion ) {
    	Activity activity = this.createActivity( node, positiveAssertion );
    	
        this.provenance.add( activity );         
    }

    protected Activity createActivity( M node, boolean positiveAssertion ) {
        if ( node.getLogicalDependency().getObject() instanceof WorkingMemoryTask ) {
            return toActivity( (WorkingMemoryTask) node.getLogicalDependency().getObject(),
                               node.getLogicalDependency().getJustifier(),
                               positiveAssertion );
        }
        return null;
    }
    
    protected Activity toActivity( WorkingMemoryTask task, Activation<M> justifier, boolean positiveAssertion ) {
    	Activity activity = null;

        Instance subject = getTarget( task );
        switch ( task.kind() ) {
            case ASSERT:
                activity = newAssert( task, subject );
                break;
            case DON:
                activity = newDon( task, subject );
                break;
            case MODIFY:
                activity = newModify( task, subject );
                break;
            case DELETE:
                activity = newRetraction( task, subject );
                break;
            case SHED:
                activity = newDerogation( task, subject );
                break;
            default:
                activity = new ActivityImpl();
        }

        activity.addIdentifier( new Literal( task.getId() ) );
        activity.addEndedAtTime( new Date() );

        activity.addWasAssociatedWith( DROOLS_ENGINE );
        activity.addAccrualMethod( newRule( justifier.getRule() ) );

        if ( justifier.getRule().getMetaData().containsKey( Display.class.getName() ) ) {
            Map<String,Object> context = buildContext( justifier );
            applyDecorations( subject, activity, justifier, context );

        }

        return activity;
    }

    private Map<String, Object> buildContext( Activation<M> justifier ) {
        Map map = new HashMap();
        for ( String declaration : ( ( RuleTerminalNodeLeftTuple ) justifier).getSubRule().getOuterDeclarations().keySet() ) {
            map.put( declaration, justifier.getDeclarationValue( declaration ) );
        }
        return TemplateRegistry.sanitize( map );
    }

    private void applyDecorations( Instance subject, Activity activity, Activation justifier, Map<String,Object> context ) {
        for ( int j = 0; j < justifier.getObjects().size(); j++ ) {
            Object o = justifier.getObjects().get( j );
            if ( o instanceof InitialFact ) {
                continue;
            }

            if ( isEvidence( justifier.getRule().getLhs().getChildren().get( j ) ) ) {
                Instance source = new InstanceImpl().withIdentifier( new Literal( MetadataContainer.getIdentifier( o ) ) );

                subject.addWasDerivedFrom( source );
                activity.addUsed( source );

                decorateEvidence( source, justifier.getRule(), justifier.getRule().getLhs().getChildren().get( j ), context );
            }
        }

        decorateActivity( activity, subject, justifier, context );
    }

    private void decorateActivity( Activity activity, Instance subject, Activation justifier, Map<String, Object> context ) {
        Object display =  justifier.getRule().getMetaData().get( Display.class.getName() );

        String inlineTemplate = "";
        String templateRef = "";

        if ( display instanceof String ) {
            inlineTemplate = (String) display;
        } else if ( display instanceof Map ) {
            Map map = (Map) display;
            inlineTemplate = (String) map.get( "value" );
            templateRef = (String) map.get( "template" );
        }

        CompiledTemplate compiled = TemplateRegistry.getInstance().compileAndCache( inlineTemplate != null ? justifier.getRule().getName() : templateRef,
                                                                                    inlineTemplate );

        String mainText = (String) TemplateRuntime.execute( compiled, context );
        Narrative narrative = new NarrativeImpl().withNarrativeText( mainText );
        subject.addDisplaysAs( narrative );
    }

    private boolean isEvidence( RuleConditionElement ruleConditionElement ) {
        return ruleConditionElement instanceof Pattern &&
               ( (Pattern) ruleConditionElement ).getAnnotations().containsKey( Evidence.class.getName() );
    }

    private void decorateEvidence( Instance source, RuleImpl rule, RuleConditionElement rce, Map<String,Object> context ) {
        AnnotationDefinition evidenceDef = ((Pattern) rce).getAnnotations().get( Evidence.class.getName() );

        if ( evidenceDef != null && evidenceDef.getPropertyValue( "value" ) != null ) {
            AnnotationDefinition[] displays = (AnnotationDefinition[]) evidenceDef.getPropertyValue( "value" );
            for ( AnnotationDefinition display : displays ) {
                String type = (String) display.getPropertyValue( "type" );

                String inlineTemplate = (String) display.getPropertyValue( "value" );
                String templateRef = (String) display.getPropertyValue( "template" );

                int index = ( (Pattern) rce ).getIndex();
                CompiledTemplate compiled = TemplateRegistry.getInstance().compileAndCache( inlineTemplate != null ? ( rule.getName() + "_" + index ): templateRef,
                                                                                            inlineTemplate );

                try {
                    String text = (String) TemplateRuntime.execute( compiled, context );
                    text = text.trim();

                    Narrative narr = new NarrativeImpl();
                    narr.addNarrativeText( text );
                    narr.addNarrativeType( type );
                    source.addDisplaysAs( narr );

                    context.put( TemplateRegistry.sanitize( ((Pattern) rce).getDeclaration().getBindingName() + "_" + type ),
                                 text );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    System.err.println( "Unable to apply evidence template, decoration will not be set" );
                }
            }
        }

    }

    private Rule newRule( RuleImpl rule ) {
        Rule match = new org.jboss.drools.provenance.RuleImpl();
        match.addIdentifier( new Literal( rule.getPackageName() ) );
        match.addIdentifier( new Literal( rule.getName() ) );
        return match;
    }

    private Activity newDerogation( WorkingMemoryTask task, Instance subject ) {
        Shed shed = (Shed) task;
        Activity activity = new DerogationImpl()
                                        .withInvalidated( new TypificationImpl()
                                                                    .withHadPrimarySource( subject )
                                                                    .withValue( new Literal( getClassIdentifier( shed.getTrait() ) ) ) );
        return activity;
    }

    private Activity newRetraction( WorkingMemoryTask task, Instance subject ) {
        Activity activity = new RetractionImpl()
                                        .withInvalidated( subject );
        return activity;
    }

    private Activity newModify( WorkingMemoryTask task, Instance subject ) {
        Modify modify = (Modify) task;
        ModifyTask setter = modify.getSetterChain();

        Activity activity = null;
        Activity last = null;
        while ( setter != null ) {
            Modification setting = new ModificationImpl()
                                        .withGenerated( new PropertyImpl()
                                                                .withHadPrimarySource( subject )
                                                                .withIdentifier( new Literal( setter.getProperty().getKey().toString() ) )
                                                                .withValue( new Literal( setter.getValue().toString() ) ) );
            if ( activity == null ) {
                activity = setting;
                last = setting;
            } else {
                last.addRelated( setting );
                last = setting;
            }
            setter = setter.getNext();
        }
        return activity;
    }

    private Activity newDon( WorkingMemoryTask task, Instance subject ) {
        Don don = (Don) task;
        Activity activity = new RecognitionImpl()
                                        .withGenerated( new TypificationImpl()
                                                                .withHadPrimarySource( subject )
                                                                .withValue( new Literal( getClassIdentifier( don.getTrait() ) ) ) );
        return activity;
    }

    private Activity newAssert( WorkingMemoryTask task, Instance subject ) {
        NewInstance newInstance = (NewInstance) task;
        Activity activity = new AssertionImpl()
                                        .withGenerated( subject );
        if ( newInstance.getInitArgs() != null ) {
            Activity setters = newModify( newInstance.getInitArgs(), subject );
            setters.addEndedAtTime( new Date() );
            activity.addRelated( setters );
        }
        return activity;
    }


	public List<Activity> getProvenance() {
        return provenance;
    }

    public Collection<? extends Activity> getGeneratingActivities() {
        return provenance;
    }

    private Instance getTarget( WorkingMemoryTask task ) {
        return new InstanceImpl()
                        .withIdentifier( new Literal( task.getTargetId() ) );
    }

    private Object getClassIdentifier( Class trait ) {
        if ( trait.getAnnotation( RdfsClass.class ) != null ) {
            return ((RdfsClass) trait.getAnnotation( RdfsClass.class )).value();
        } else {
            return trait.getName();
        }
    }

}
