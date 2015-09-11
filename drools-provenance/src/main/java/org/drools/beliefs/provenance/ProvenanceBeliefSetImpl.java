package org.drools.beliefs.provenance;

import com.clarkparsia.empire.annotation.RdfsClass;
import org.drools.beliefs.provenance.annotations.Display;
import org.drools.beliefs.provenance.annotations.Evidence;
import org.drools.beliefs.provenance.templates.TemplateRegistry;
import org.drools.core.InitialFact;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.simple.SimpleBeliefSet;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.metadata.*;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Collect;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PatternSource;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.Activation;
import org.drools.core.util.Drools;
import org.drools.semantics.Literal;
import org.jboss.drools.provenance.*;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;
import org.w3.ns.prov.Activity;
import org.w3.ns.prov.ActivityImpl;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProvenanceBeliefSetImpl
        extends SimpleBeliefSet
        implements ProvenanceBeliefSet {

    private Map<String,Activity> provenance = new ConcurrentHashMap<String, Activity>();

    private static RuleEngine DROOLS_ENGINE = new org.jboss.drools.provenance.RuleEngineImpl()
            .withIdentifier( new Literal( "JBoss Drools " + Drools.getFullVersion() ) );

    public ProvenanceBeliefSetImpl( BeliefSystem beliefSystem, InternalFactHandle rootHandle ) {
        super(beliefSystem, rootHandle);
    }


    public void recordActivity( MetaCallableTask task, Activation activation, boolean positiveAssertion ) {
        Activity activity = this.createActivity(task, activation, positiveAssertion);
        if ( activity == null ) {
            // no activity was actually performed
            return;
        }

        processActivities( activity );
    }

    private void processActivities( Activity activity ) {
        addActivity( activity );

        Iterator relateds = activity.getRelated().iterator();
        while ( relateds.hasNext() ) {
            Object x = relateds.next();
            if ( x instanceof Activity ) {
                processActivities( (Activity) x );
            }
        }
    }


    private void addActivity( Activity activity ) {
        String key = activity.getGenerated().get( 0 ).getIdentifier().get( 0 ).getLit().toString();

        Activity previous = this.provenance.get( key );
        if ( previous != null ) {
            activity.addWasInformedBy( previous );
        }
        this.provenance.put( key, activity );
    }

    protected Activity createActivity( MetaCallableTask mct, Activation activation, boolean positiveAssertion ) {
        WorkingMemoryTask task = (WorkingMemoryTask) mct;
        if ( ! task.getTargetId().equals( MetadataContainer.getIdentifier( getFactHandle().getObject() ) ) ) {
            task = ((ModifyLiteral) task.getSetters()).getInverse( fh.getObject() );
        }
        if ( task.kind() == MetaCallableTask.KIND.MODIFY && task.getSetters().getSetterChain() == null ) {
            // no activity was actually performed on this related object
            return null;
        }
        return toActivity( task,
                           activation,
                           positiveAssertion );
    }

    protected Activity toActivity( WorkingMemoryTask task, Activation justifier, boolean positiveAssertion ) {
        Activity activity = null;

        Instance subject = getTarget( task );
        switch ( task.kind() ) {
            case ASSERT:
                activity = newAssert( task, subject, justifier );
                break;
            case DON:
                activity = newDon( task, subject, justifier );
                break;
            case MODIFY:
                activity = newModify( task, subject, justifier );
                break;
            case DELETE:
                activity = newRetraction( task, subject, justifier );
                break;
            case SHED:
                activity = newDerogation( task, subject, justifier );
                break;
            default:
                activity = new ActivityImpl();
        }

        if ( justifier.getRule().getMetaData().containsKey( Display.class.getName() ) ) {
            Map<String,Object> context = buildContext( justifier );
            applyDecorations( subject, activity, justifier, context );
        }

        return activity;
    }




    private void addCommonInfo( Activity activity, WorkingMemoryTask task, Activation rule ) {
        activity.addIdentifier( new Literal( rule.getActivationNumber() ) );
        activity.addIdentifier( new Literal( task.getUri().toString() ) );
        activity.addEndedAtTime( new Date( ((ProvenanceBeliefSystem) getBeliefSystem()).now() ) );

        activity.addWasAssociatedWith( DROOLS_ENGINE );
        activity.addAccrualMethod( newRule( rule ) );
    }

    private Map<String, Object> buildContext( Activation justifier ) {
        Map map = new HashMap();
        String currentConsequenceName = justifier.getConsequence().getName();
        Map<String, Declaration> declarationMap = justifier.getSubRule().getInnerDeclarations(currentConsequenceName);
        for ( String declaration :justifier.getSubRule().getOuterDeclarations().keySet() ) {
            if (declarationMap.containsKey(declaration)) {
                map.put(declaration, justifier.getDeclarationValue(declaration));
            }
        }

        // TODO this may need to be recursively invoked due to nestable conditional elements
        LeftTuple tuple = justifier.getTuple();
        List<RuleConditionElement> ces = justifier.getRule().getLhs().getChildren();
        Collections.reverse( ces );
        for ( RuleConditionElement element : ces ) {
            if( element instanceof Pattern && ((Pattern) element).getSource() != null ) {
                PatternSource ps = ( (Pattern) element ).getSource();
                if ( ps instanceof Collect ) {
                    Collect collect = (Collect) ps;
                    List<?> facts = (List) tuple.getHandle().getObject();
                    for ( Declaration dec : collect.getInnerDeclarations().values() ) {
                        List<Object> vals = new ArrayList<Object>( facts.size() );
                        for ( Object o : facts ) {
                            vals.add( dec.getValue( ((ProvenanceBeliefSystem)this.getBeliefSystem()).getEp().getInternalWorkingMemory(), o ) );
                        }
                        map.put( dec.getBindingName(), vals );
                    }
                }
            }
            tuple = tuple.getParent();
        }

        return TemplateRegistry.sanitize( map );
    }

    private void applyDecorations( Instance subject, Activity activity, Activation justifier, Map<String,Object> context ) {
        List<RuleConditionElement> ruleElements = new ArrayList( justifier.getRule().getLhs().getChildren() );
        Collections.reverse( ruleElements );

        for ( int j = 0; j < justifier.getObjects().size(); j++ ) {
            Object o = justifier.getObjects().get( j );
            if ( o instanceof InitialFact ) {
                continue;
            }


            RuleConditionElement element = ruleElements.get( j );
            if(element instanceof Pattern && ((Pattern) element).getSource() != null) {
                for(RuleConditionElement nestedElement : ((Pattern) element).getSource().getNestedElements()) {
                    if (isEvidence( nestedElement )) {

                        int k = 0;
                        for (Object listObj : (List) o) {
                            Instance source = new InstanceImpl().withIdentifier(new Literal(MetadataContainer.getIdentifier(listObj)));

                            subject.addWasDerivedFrom(source);
                            activity.addUsed(source);

                            decorateEvidence(source, justifier.getRule(), nestedElement, adjustContext( context, nestedElement.getInnerDeclarations(), k++ ) );
                        }
                    }
                }
            } else {
                if (isEvidence( element )) {
                    Instance source = new InstanceImpl().withIdentifier(new Literal(MetadataContainer.getIdentifier(o)));

                    subject.addWasDerivedFrom(source);
                    activity.addUsed(source);

                    decorateEvidence(source, justifier.getRule(), element, context);
                }
            }
        }

        decorateActivity( activity, subject, justifier, context );
    }

    private Map<String,Object> adjustContext( Map<String,Object> context, Map<String,Declaration> innerDeclarations, int idx ) {
        Map<String, Object> clonedContext = new HashMap<String,Object>( context );
        for ( String key : innerDeclarations.keySet() ) {
            key = TemplateRegistry.sanitize( key );
            clonedContext.put( key, ((List) context.get( key )).get( idx ) );
        }
        return clonedContext;
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

        if ( templateRef != null || inlineTemplate != null ) {
            CompiledTemplate compiled = TemplateRegistry.getInstance().compileAndCache( (inlineTemplate != null && inlineTemplate.length() > 0) ?
                                                                                        justifier.getRule().getName() : templateRef,
                                                                                        inlineTemplate );

            String mainText = (String) TemplateRuntime.execute( compiled, context );
            Narrative narrative = new NarrativeImpl().withNarrativeText( mainText );
            activity.addDisplaysAs( narrative );
        }
    }

    private boolean isEvidence( RuleConditionElement ruleConditionElement ) {
        return ruleConditionElement instanceof Pattern &&
               ( (Pattern) ruleConditionElement ).getAnnotations().containsKey( Evidence.class.getName() );
    }

    private void decorateEvidence( Instance source, RuleImpl rule, RuleConditionElement rce, Map<String,Object> context ) {
        AnnotationDefinition evidenceDef = ((Pattern) rce).getAnnotations().get( Evidence.class.getName() );

        if ( evidenceDef != null && evidenceDef.getPropertyValue( "value" ) != null ) {
            AnnotationDefinition[] displays = (AnnotationDefinition[]) evidenceDef.getPropertyValue( "value" );
            for ( int displayIndex = 0; displayIndex < displays.length; displayIndex++ ) {
                AnnotationDefinition display = displays[displayIndex];

                String type = (String) display.getPropertyValue( "type" );

                String inlineTemplate = (String) display.getPropertyValue( "value" );
                String templateRef = (String) display.getPropertyValue( "template" );

                int index = ( (Pattern) rce ).getIndex();

                CompiledTemplate compiled = TemplateRegistry.getInstance().compileAndCache( inlineTemplate != null ? ( rule.getName() + "_" + index + "_" + displayIndex ): templateRef,
                                                                                            inlineTemplate );

                try {
                    Object comp = TemplateRuntime.execute(compiled, context);
                    String text = comp.toString();
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

    private Rule newRule( Activation rule ) {
        Rule match = new org.jboss.drools.provenance.RuleImpl();
        match.addIdentifier( new Literal( rule.getRule().getPackageName() ) );
        match.addIdentifier( new Literal( rule.getRule().getName() ) );
        return match;
    }

    private Activity newDerogation( WorkingMemoryTask task, Instance subject, Activation rule ) {
        Shed shed = (Shed) task;
        Activity activity = new DerogationImpl()
                .withInvalidated( new TypificationImpl()
                                          .withHadPrimarySource( subject )
                                          .withValue( new Literal( getClassIdentifier( shed.getTrait() ) ) ) );
        addCommonInfo( activity, task, rule );
        return activity;
    }

    private Activity newRetraction( WorkingMemoryTask task, Instance subject, Activation rule ) {
        Activity activity = new RetractionImpl()
                .withInvalidated( subject );
        addCommonInfo( activity, task, rule );
        return activity;
    }

    private Activity newModify( WorkingMemoryTask task, Instance subject, Activation rule ) {
        Modify modify = (Modify) task;
        ModifyTask setter = modify.getSetterChain();

        Activity activity = null;
        Activity last = null;
        while ( setter != null ) {
            Property prop = new PropertyImpl().withHadPrimarySource( subject )
                    .withIdentifier( new Literal( setter.getProperty().getKey().toString() ) );

            if ( setter.getProperty().isManyValued() ) {
                for ( Object o : (Collection) setter.getValue() ) {
                    prop.addValue( setter.getProperty().isDatatype() ? new Literal( o.toString() ) : new Literal( toRef( o ) ) );
                }
            } else {
                prop.addValue( setter.getProperty().isDatatype() ? new Literal( setter.getValue().toString() ) : new Literal( toRef( setter.getValue() ) ) );
            }

            Modification setting = newModification( setter.getMode() );
            setting.addGenerated( prop );
            addCommonInfo( setting, task, rule );
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



    private String toRef( Object value ) {
        if ( value instanceof Identifiable ) {
            return  ( (Identifiable) value ).getId().toString();
        } else {
            return value.toString();
        }

    }

    private Modification newModification( Lit mode ) {
        switch ( mode ) {
            case SET: return new SettingImpl();
            case REMOVE: return new RemovalImpl();
            case ADD: return new AdditionImpl();
        }
        throw new IllegalStateException( "Unrecognized modification mode " + mode );
    }

    private Activity newDon( WorkingMemoryTask task, Instance subject, Activation rule ) {
        Don don = (Don) task;
        Activity activity = new RecognitionImpl()
                .withGenerated( new TypificationImpl()
                                        .withHadPrimarySource( subject )
                                        .withIdentifier( new Literal( don.getUri().toString() ) )
                                        .withValue( new Literal( getClassIdentifier( don.getTrait() ) ) ) );
        addCommonInfo( activity, task, rule );
        return activity;
    }

    private Activity newAssert( WorkingMemoryTask task, Instance subject, Activation rule ) {
        NewInstance newInstance = (NewInstance) task;
        Activity activity = new AssertionImpl()
                .withGenerated( subject );
        addCommonInfo( activity, task, rule );

        Activity tip = activity;
        if ( newInstance.getInstanceClass().isInterface() ) {
            Activity typing = new RecognitionImpl().withGenerated( new TypificationImpl()
                                                                           .withHadPrimarySource( subject )
                                                                           .withIdentifier( new Literal( DonLiteral.createURI( subject.getId().toString(), newInstance.getInstanceClass() ) ) )
                                                                           .withValue( new Literal( getClassIdentifier( newInstance.getInstanceClass() ) ) ) );
            addCommonInfo( typing, task, rule );
            activity.addRelated( typing );
            tip = typing;
        }

        if ( newInstance.getInitArgs() != null ) {
            Activity setters = newModify( newInstance.getInitArgs(), subject, rule );
            setters.addEndedAtTime( new Date() );
            tip.addRelated( setters );
        }
        return activity;
    }


    public Map<String,Activity> getProvenance() {
        return provenance;
    }

    public Collection<? extends Activity> getGeneratingActivities() {
        return provenance.values();
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
