package org.drools.beliefs.provenance;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.ModedAssertion;
import org.w3.ns.prov.Activity;

import java.util.Collection;

public interface ProvenanceBeliefSet {

    Collection<? extends Activity> getGeneratingActivities();

}
