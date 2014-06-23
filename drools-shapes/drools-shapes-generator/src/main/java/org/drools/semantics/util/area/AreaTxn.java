package org.drools.semantics.util.area;


import java.util.Collection;
import java.util.Set;


public interface AreaTxn<C,P> {

    public Collection<Set<P>> getAreaKeys();

    public Area<C,P> getArea( Set<P> key );

    public boolean hasArea( Set<P> key );

    public Collection<Area<C,P>> getAreas();
}
