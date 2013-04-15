package org.drools.semantics.util.area;

import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.util.HierarchyEncoder;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface AreaTxn<C,P> {

    public Area<C,P> getArea( Set<P> key );

    public boolean hasArea( Set<P> key );
}
