package org.drools.shapes.semantics;

import org.hl7.v3.CD;


/**
 * An abstraction around Value Set terminology service functionality.
 */
public interface ValueSetProcessor {

    /**
     * Check whether or not the given code is included in a Value Set,
     * which is identified by a URI.
     *
     * @param valueSetUri
     *  the URI of the Value Set to match
     * @param code
     *  the code to check
     * @return
     */
    public boolean inValueSet(String valueSetUri, CD code);

}
