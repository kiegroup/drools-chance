package org.drools.chance.distribution.probability.gaussian;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class RealNumberSet<T extends Number> implements Set<Number> {

    public int size() {
        return Integer.MAX_VALUE;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean contains( Object o ) {
        return o instanceof Number;
    }

    public Iterator iterator() {
        return null;
    }

    public Object[] toArray() {
        return new Object[] {};
    }

    public boolean add( Number number ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean remove( Object o ) {
        return false;
    }

    public boolean containsAll( Collection<?> objects ) {
        return false;
    }

    public boolean addAll( Collection collection ) {
        return false;
    }

    public boolean retainAll( Collection<?> objects ) {
        return false;
    }

    public boolean removeAll( Collection<?> objects ) {
        return false;
    }

    public void clear() {

    }

    public Object[] toArray( Object[] objects ) {
        return new Object[ 0 ];
    }
};

