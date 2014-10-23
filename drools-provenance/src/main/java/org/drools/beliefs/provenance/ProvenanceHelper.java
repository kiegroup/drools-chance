package org.drools.beliefs.provenance;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.common.BeliefSystemFactory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.factmodel.traits.InstantiatorFactory;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.rule.EntryPointId;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.runtime.KieSession;

import java.util.UUID;

public class ProvenanceHelper {

    public static Provenance getProvenance( KieSession kieSession, String entryPointId ) {
        BeliefSystem bs =( (NamedEntryPoint) kieSession.getEntryPoint( entryPointId ) ).getTruthMaintenanceSystem().getBeliefSystem();
        if ( bs instanceof Provenance ) {
            return (Provenance) bs;
        } else {
            throw new IllegalStateException( "Could not find a Provenance-capable Belief System in entry-point " + entryPointId + " : " + bs.getClass() );
        }
    }

    public static Provenance getProvenance( KieSession kieSession ) {
        return getProvenance( kieSession, EntryPointId.DEFAULT.getEntryPointId() );
    }

    public static KieBaseConfiguration getProvenanceEnabledKieBaseConfiguration() {
        RuleBaseConfiguration kieBaseConfiguration = new RuleBaseConfiguration(  );
        kieBaseConfiguration.setComponentFactory( new KieComponentFactory() {
            @Override
            public BeliefSystemFactory getBeliefSystemFactory() {
                return new ProvenanceBeliefSystemFactory();
            }
        });
        kieBaseConfiguration.getComponentFactory().getTraitFactory().setInstantiatorFactory( new InstantiatorFactory() {
            @Override
            public TraitableBean instantiate( Class<? extends Thing> trait, Object id ) {
                return new IdentifiableEntity( id.toString() );
            }

            @Override
            public Object createId( Class<?> klass ) {
                return UUID.randomUUID().toString();
            }
        } );
        kieBaseConfiguration.setOption( EqualityBehaviorOption.EQUALITY );
        return kieBaseConfiguration;
    }

    public static ProvenanceBeliefSystem install( NamedEntryPoint ep ) {
        return new ProvenanceBeliefSystem( ep, ep.getTruthMaintenanceSystem() );
    }
}