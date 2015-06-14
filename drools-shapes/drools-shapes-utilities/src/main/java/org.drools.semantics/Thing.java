package org.drools.semantics;

import com.clarkparsia.empire.SupportsRdfId;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.metadata.Internal;

@Trait
public interface Thing<K> extends com.clarkparsia.empire.SupportsRdfId, org.drools.core.factmodel.traits.Thing<K> {

    @Internal
    public String get__IndividualName();

    public void set__IndividualName( String name );

    public String getFullName();

    @Internal
    public SupportsRdfId.RdfKey getRdfId();
}
