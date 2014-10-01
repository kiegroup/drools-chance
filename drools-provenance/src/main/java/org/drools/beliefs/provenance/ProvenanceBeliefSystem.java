package org.drools.beliefs.provenance;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSet;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSMode;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.common.BeliefSystemFactory;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.metadata.Don;
import org.drools.core.metadata.MetaCallableTask;
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.metadata.Modify;
import org.drools.core.metadata.NewInstance;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.runtime.beliefs.Mode;
import org.w3.ns.prov.Activity;

import java.util.Collection;

public class ProvenanceBeliefSystem<M extends ProvenanceBelief<M>>
        extends JTMSBeliefSystem<M>
        implements Provenance {

    public final ProvenanceBelief<M> PROV = new ProvenanceBelief( this );

    protected ProvenanceBeliefSystem( NamedEntryPoint ep, TruthMaintenanceSystem tms ) {
        super( ep, tms );
    }

    @Override
    public void delete( LogicalDependency<M> node, BeliefSet<M> beliefSet, PropagationContext context ) {
        super.delete( node, beliefSet, context );
    }

    @Override
    public void insert( LogicalDependency<M> node, BeliefSet<M> beliefSet, PropagationContext context, ObjectTypeConf typeConf ) {
        super.insert( node, beliefSet, context, typeConf );

        if ( node.getObject() instanceof MetaCallableTask ) {

            defEP.getEntryPointNode().retractObject( beliefSet.getFactHandle(),
                                                     context,
                                                     typeConf,
                                                     defEP.getInternalWorkingMemory() );
            MetaCallableTask task = (MetaCallableTask) node.getObject();
            switch ( task.kind() ) {
                case ASSERT : executeNew( (NewInstance) task, node );
                    break;
                case MODIFY : executeModify( (Modify) task, node );
                    break;
                case DON    : executeDon( (Don) task, node );
                    break;
                default:
                    throw new UnsupportedOperationException( "Unrecognized Meta TASK type" );
            }

            } else {
            }
        }

    private void executeDon( Don don, LogicalDependency<M> node ) {
        defEP.getTraitHelper().don( node.getJustifier(),
                                    don.getCore(),
                                    don.getTrait(),
                                    null,
                                    false );
    }

    private void executeModify( Modify modify, LogicalDependency<M> node ) {
        Object target = modify.getTarget();
        modify.call();

        defEP.update( (InternalFactHandle) defEP.getFactHandle( target ),
                      true,
                      target,
                      modify.getModificationMask(),
                      modify.getModificationClass(),
                      node.getJustifier() );
    }

    private void executeNew( NewInstance newInstance, LogicalDependency<M> node ) {
        if ( newInstance.isInterface() ) {
            newInstance.setInstantiatorFactory( TraitFactory.getTraitBuilderForKnowledgeBase( defEP.getKnowledgeBase() ).getInstantiatorFactory() );
            Object target = newInstance.callUntyped();

            defEP.getTraitHelper().don( node.getJustifier(),
                                        target,
                                        newInstance.getInstanceClass(),
                                        newInstance.getInitArgs(),
                                        false );
        } else {
            Object target = newInstance.call();
            defEP.insert( target, JTMSBeliefSetImpl.MODE.POSITIVE, false, false, node.getJustifier().getRule(), node.getJustifier() );
        }
    }

    public LogicalDependency<M> newLogicalDependency( Activation<M> activation,
                                                      BeliefSet<M> beliefSet,
                                                      Object object,
                                                      Object value ) {
        ProvenanceBelief<M> mode = new ProvenanceBelief<M>( this );
        SimpleLogicalDependency dep =  new SimpleLogicalDependency(activation, beliefSet, object, mode );
        mode.setLogicalDependency( dep );
        return dep;
    }

    public BeliefSet<M> newBeliefSet( InternalFactHandle fh ) {
        return new ProvenanceBeliefSetImpl( this, fh );
    }

    protected ProvenanceBeliefSet<M> getProvenanceBS( Object o ) {
        EqualityKey key = getTruthMaintenanceSystem().get( o );
        if ( key == null || key.getStatus() == EqualityKey.STATED || ! ( key.getBeliefSet() instanceof ProvenanceBeliefSetImpl ) ) {
            return null;
        }
        return (ProvenanceBeliefSet<M>) key.getBeliefSet();
    }

    @Override
    public boolean hasProvenanceFor( Object o ) {
        return getProvenanceBS( o ) != null;
    }

    @Override
    public Collection<? extends Activity> describeProvenance( Object o ) {
        return getProvenanceBS( o ).getGeneratingActivities();
    }
}
