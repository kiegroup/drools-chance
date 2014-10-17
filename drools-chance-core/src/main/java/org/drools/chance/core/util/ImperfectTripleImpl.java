package org.drools.chance.core.util;

import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.core.util.Triple;
import org.drools.core.util.TripleImpl;


public class ImperfectTripleImpl extends TripleImpl implements Triple {

    private Degree degree = SimpleDegree.TRUE;

    public ImperfectTripleImpl( Object instance, String property, Object value ) {
        super(instance, property, value);
    }

    public ImperfectTripleImpl( Object instance, Object property, Object value ) {
        super(instance, property, value);
    }

    public ImperfectTripleImpl( Object instance, String property, Object value, Degree degree ) {
        super(instance, property, value);
        this.degree = degree;
    }

    public ImperfectTripleImpl( Object instance, Object property, Object value, Degree degree ) {
        super(instance, property, value);
        this.degree = degree;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }
}
