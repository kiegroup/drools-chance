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

    public String get__IndividualName() {
        return getId();
    }

    public void set__IndividualName( String name ) {
        if ( get__IndividualName() == null ) { setId( name ); }
    }
}
