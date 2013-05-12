package org.drools.semantics;

import org.drools.factmodel.traits.Entity;
import org.drools.factmodel.traits.Traitable;

@Traitable
public class NamedIndividual extends Entity implements NamedEntity {

    public NamedIndividual() {
        super();
    }

    public NamedIndividual( String name ) {
        super( name );
    }

    public String getName() {
        return getId();
    }

    public void setName( String name ) {
        if ( getName() == null ) { setId( name ); }
    }
}
