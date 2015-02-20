package org.drools.shapes.terms;


import org.drools.drools_shapes.terms.Code;

/**
 * An abstraction around Value Set terminology service functionality.
 */
public interface Terms {

    /**
     * Check whether or not the given code is included in a Value Set,
     * which is identified by a URI.
     *
     * @param complexConcept
     * @param entity
     * @return
     */
    public boolean isEntityInSet( Code entity, Code complexConcept );

}
