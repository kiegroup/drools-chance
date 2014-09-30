package org.drools.beliefs.provenance;

import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.semantics.Literal;
import org.jboss.drools.provenance.RuleActivationImpl;
import org.w3.ns.prov.Activity;
import org.w3.ns.prov.ActivityImpl;
import org.w3.ns.prov.Agent;
import org.w3.ns.prov.Entity;
import org.w3.ns.prov.EntityImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProvenanceBeliefSetImpl<M extends ProvenanceBelief<M>>
        extends JTMSBeliefSetImpl<M>
        implements ProvenanceBeliefSet<M> {

    private List<Activity> provenance = new ArrayList<Activity>();

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
        Object tgt = node.getLogicalDependency().getObject();

        ActivityImpl activity = new ActivityImpl();
        activity.withEndedAtTime( new Date() );
        activity.withName( new Literal( UUID.randomUUID().toString() ) );


        Entity entity = new EntityImpl();
        activity.addGenerated( entity );

        Agent rule = new RuleActivationImpl();


        this.provenance.add( activity );
    }

    public List<Activity> getProvenance() {
        return provenance;
    }


    @Override
    public Collection<? extends Activity> getGeneratingActivities() {
        return provenance;
    }
}
