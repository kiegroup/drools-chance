package org.drools.beliefs.provenance;

import org.drools.core.factmodel.traits.Entity;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.metadata.Identifiable;

import java.net.URI;

@Traitable
public class IdentifiableEntity extends Entity implements Identifiable {
    protected URI key;

    public IdentifiableEntity( String s ) {
        super( s );
    }

    public URI getUri() {
        if ( key == null ) {
            key = URI.create( getId() );
        }
        return key;
    }
}
