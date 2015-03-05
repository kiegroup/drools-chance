package org.drools.beliefs.provenance;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleBeliefSystem;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.BitMaskKey;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.metadata.Don;
import org.drools.core.metadata.MetaCallableTask;
import org.drools.core.metadata.Modify;
import org.drools.core.metadata.NewInstance;
import org.drools.core.metadata.WorkingMemoryTask;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.PropertySpecificUtil;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.BitMaskUtil;
import org.jboss.drools.provenance.Assertion;
import org.kie.api.time.SessionClock;
import org.kie.internal.runtime.beliefs.Mode;
import org.w3.ns.prov.Activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ProvenanceBeliefSystem
        extends SimpleBeliefSystem
        implements Provenance {

    protected ProvenanceBeliefSystem( NamedEntryPoint ep, TruthMaintenanceSystem tms ) {
        super( ep, tms );
    }

    @Override
    public void delete( LogicalDependency node, BeliefSet beliefSet, PropagationContext context ) {
        Object justified = node.getJustified();
        if ( justified instanceof ProvenanceBeliefSet && ((ProvenanceBeliefSetImpl) justified).getFactHandle().getObject() instanceof MetaCallableTask ) {
            // right now, provenance is never retracted. The task itself is not in the WM / BS / TMS, so do nothing
        } else {
            super.delete( node, beliefSet, context );
        }
    }

    public BeliefSet<SimpleMode> insert(LogicalDependency<SimpleMode> node,
                                        BeliefSet<SimpleMode> beliefSet,
                                        PropagationContext context,
                                        ObjectTypeConf typeConf) {
        return insert( node.getMode(), node.getJustifier().getRule(), node.getJustifier(), node.getObject(), beliefSet, context, typeConf );
    }

    @Override
    public BeliefSet insert( SimpleMode mode,
                             RuleImpl rule,
                             Activation activation,
                             Object payload,
                             BeliefSet beliefSet,
                             PropagationContext context,
                             ObjectTypeConf typeConf ) {

        if ( payload instanceof MetaCallableTask ) {

            MetaCallableTask task = (MetaCallableTask) payload;
            if ( beliefSet instanceof ProvenanceBeliefSet ) {
                EqualityKey k = beliefSet.getFactHandle().getEqualityKey();
                if ( k.getStatus() == EqualityKey.JUSTIFIED ) {
                    getEp().getTruthMaintenanceSystem().remove( k );
                }
            }

            Modify setters;
            switch ( task.kind() ) {
                case ASSERT :
                    beliefSet = ensureBeliefSetConsistency( beliefSet, executeNew( (NewInstance) task, activation, rule ) );
                    setters = ((NewInstance) task).getSetters();
                    break;
                case MODIFY : executeModify( (Modify) task, activation );
                    beliefSet = ensureBeliefSetConsistency( beliefSet, ( (Modify) task ).getTarget() );
                    setters = (Modify) task;
                    break;
                case DON    : executeDon( (Don) task, activation );
                    beliefSet = ensureBeliefSetConsistency( beliefSet, ( (Don) task ).getCore() );
                    setters = ((Don) task).getSetters();
                    break;
                default:
                    throw new UnsupportedOperationException( "Unrecognized Meta TASK type" );
            }

            if ( mode != null ) {
                beliefSet.add( mode );
            }
            ((ProvenanceBeliefSet) beliefSet).recordActivity( task, activation, true );

            if ( setters != null && setters.getAdditionalUpdates() != null ) {
                for ( Object extra : setters.getAdditionalUpdates() ) {
                    if ( extra instanceof TraitProxy ) {
                        extra = ((TraitProxy) extra).getObject();
                    }
                    EqualityKey ek = getTms().get( extra );

                    if ( ek != null ) {
                        if ( ek.getBeliefSet() == null ) {
                            ek.setBeliefSet( newBeliefSet( ek.getFactHandle() ) );
                        }
                        ek.getBeliefSet().add( mode );
                        ((ProvenanceBeliefSet)ek.getBeliefSet()).recordActivity( task, activation, true );
                    }
                }
            }

            // set the proper target on the dependency inside the mode
            if ( mode != null ) {
                SimpleLogicalDependency dep = (SimpleLogicalDependency) mode.getObject();
                dep.setObject( beliefSet.getFactHandle().getObject() );
            }

            return beliefSet;
        } else {
            return super.insert( mode, rule, activation, payload, beliefSet, context, typeConf );
        }


    }

    private BeliefSet ensureBeliefSetConsistency( BeliefSet beliefSet, Object core ) {
        if ( beliefSet.getFactHandle().getObject() != core ) {
            if ( core instanceof TraitProxy ) {
                core = ((TraitProxy) core).getObject();
            }
            EqualityKey ek = getTms().get( core );
            // update the belief set TODO even stated objects should be allowed to have a BS
            if ( ek.getBeliefSet() == null ) {
                BeliefSet bset = newBeliefSet( ek.getFactHandle() );
                ek.setBeliefSet( bset );
                return bset;
            } else {
                return ek.getBeliefSet();
            }
        }
        return beliefSet;
    }

    private void executeDon( Don don, Activation activation ) {
        getEp().getTraitHelper().don( activation,
                                    don.getCore(),
                                    don.getTrait(),
                                    null,
                                    false );
    }

    private void executeModify( Modify modify, Activation activation ) {
        Object target = modify.getTarget();
        modify.call( getEp().getKnowledgeBase() );

        getEp().update( (InternalFactHandle) getEp().getFactHandle( target ),
                   target,
                   modify.getModificationMask(),
                   modify.getModificationClass(),
                   activation );

        Object[] updates = modify.getAdditionalUpdates();
        if ( updates != null ) {
            for ( int j = 0; j < updates.length; j++ ) {
                getEp().update( (InternalFactHandle) getEp().getFactHandle( updates[ j ] ),
                           updates[ j ],
                           modify.getAdditionalUpdatesModificationMask( j ),
                           updates[ j ].getClass(),
                           activation );
            }
        }
    }

    private Object executeNew( NewInstance newInstance, Activation activation, RuleImpl rule ) {
        if ( newInstance.isInterface() ) {
            newInstance.setInstantiatorFactory( TraitFactory.getTraitBuilderForKnowledgeBase( getEp().getKnowledgeBase() ).getInstantiatorFactory() );
            Object target = newInstance.callUntyped();

            getEp().getTraitHelper().don( activation,
                                        target,
                                        newInstance.getInstanceClass(),
                                        newInstance.getInitArgs(),
                                        false );
            return target;
        } else {
            Object target = newInstance.call();
            getEp().insert( target,
                            false,
                            rule,
                            activation );
            return target;
        }
    }

    public BeliefSet newBeliefSet( InternalFactHandle fh ) {
        return new ProvenanceBeliefSetImpl( this, fh );
    }

    protected ProvenanceBeliefSet getProvenanceBS( Object o ) {
        EqualityKey key = getTruthMaintenanceSystem().get( o );
        if ( key == null || ! ( key.getBeliefSet() instanceof ProvenanceBeliefSetImpl ) ) {
            return null;
        }
        return (ProvenanceBeliefSet) key.getBeliefSet();
    }

    @Override
    public boolean hasProvenanceFor( Object o ) {
        return getProvenanceBS( o ) != null;
    }

    @Override
    public Collection<? extends Activity> describeProvenance( Object o ) {
        List<? extends Activity> history = new ArrayList<Activity>( getProvenanceBS( o ).getGeneratingActivities() );
        Collections.sort( history, new Comparator<Activity>() {
            @Override
            public int compare( Activity activity, Activity activity2 ) {
                int x = activity.getEndedAtTime().get( 0 ).compareTo( activity2.getEndedAtTime().get( 0 ) );
                if ( x == 0 ) {
                    return activity instanceof Assertion ? -1 : 0;
                }
                return x;
            }
        } );
        return history;
    }

    public long now() {
        return getEp().getInternalWorkingMemory().getSessionClock().getCurrentTime();
    }

    @Override
    public SimpleMode asMode( Object value ) {
        return (SimpleMode) value;
    }
}
