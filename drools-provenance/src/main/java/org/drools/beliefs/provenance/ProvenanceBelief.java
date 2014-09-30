package org.drools.beliefs.provenance;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl;
import org.drools.core.beliefsystem.jtms.JTMSMode;


public class ProvenanceBelief<M extends ProvenanceBelief<M>>
       extends JTMSMode<M>
       implements ModedAssertion<M> {

    public ProvenanceBelief( ProvenanceBeliefSystem<M> beliefSystem ) {
        super( JTMSBeliefSetImpl.MODE.POSITIVE.getId(), beliefSystem );
    }

}
