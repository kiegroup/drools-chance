package org.drools.chance.core.util;


import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;

public class ImperfectTripleFactory implements TripleFactory {

    public Triple newTriple( Object subject, String predicate, Object object ) {
        return new ImperfectTripleImpl( subject, predicate, object );
    }

    public Triple newTriple( Object subject, Object predicate, Object object ) {
        return new ImperfectTripleImpl( subject, predicate, object );
    }
}
