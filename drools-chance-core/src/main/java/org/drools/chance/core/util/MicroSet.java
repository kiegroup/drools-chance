package org.drools.chance.core.util;

import java.lang.reflect.Array;
import java.util.*;

public class MicroSet<T> implements Set<T> {

    private T value;

    public MicroSet( T value ) {
        this.value = value;
    }

    public int size() {
        return value != null ? 1 : 0;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean contains( Object o ) {
        return value == null ? o == null : value.equals( o );
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int counter = 0;

            public boolean hasNext() {
                return counter == 0;
            }

            public T next() {
                counter++;
                return value;
            }

            public void remove() {
                counter++;
                value = null;
            }
        };
    }

    public Object[] toArray() {
        Object[] ans = new Object[1];
            ans[0] = value;
        return ans;
    }

    public <T> T[] toArray(T[] a) {
        T[] array=(T[]) Array.newInstance( value.getClass(), 1 );
        return array;
    }


    public boolean add(T t) {
        value = t;
        return true;
    }

    public boolean remove(Object o) {
        if ( value != null && value.equals( o ) ) {
            value = null;
            return true;
        }
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        if ( c.size() > 1 ) { 
            return false; 
        }
        return contains( c.iterator().next() );
    }

    public boolean addAll(Collection<? extends T> c) {
        if ( c.size() > 1 ) {
            throw new UnsupportedOperationException( "Set can contain only 1 element, collection has " + c.size() );
        }
        add( c.iterator().next() );
        return true;
    }

    public boolean retainAll(Collection<?> c) {
        if ( ! c.contains( value ) ) {
            value = null;
        }
        return true;
    }

    public boolean removeAll(Collection<?> c) {
        if ( c.size() > 1 ) {
            throw new UnsupportedOperationException( "Set can contain only 1 element, collection has " + c.size() );
        }
        return remove( c.iterator().next() );        
    }

    public void clear() {
        value = null;
    }
}
