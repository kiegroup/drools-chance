package org.drools.semantics;

import org.drools.factmodel.traits.Trait;

@Trait
public interface Thing<K> extends com.clarkparsia.empire.SupportsRdfId, org.drools.factmodel.traits.Thing<K> {

    public String getName();

    public void setName( String s );

}
