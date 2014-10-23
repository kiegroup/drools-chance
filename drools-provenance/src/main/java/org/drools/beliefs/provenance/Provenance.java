package org.drools.beliefs.provenance;

import org.w3.ns.prov.Activity;

import java.util.Collection;

public interface Provenance {

    boolean hasProvenanceFor( Object o );

    Collection<? extends Activity> describeProvenance( Object o );

    public long now();
}
