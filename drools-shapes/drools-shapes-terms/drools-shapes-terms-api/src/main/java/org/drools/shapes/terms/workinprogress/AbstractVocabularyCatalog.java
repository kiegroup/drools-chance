/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms.workinprogress;

import org.drools.drools_shapes.terms.ConceptDescriptor;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractVocabularyCatalog implements VocabularyCatalog {

    protected static final Map<String,ConceptDescriptor> entries = new ConcurrentHashMap<String,ConceptDescriptor>();

    public void register( String codeSystem, ConceptDescriptor code ) {
        entries.put( codeSystem, code );
    }

    public ConceptDescriptor resolve( String codeSystem ) {
        return entries.get( codeSystem );
    }

    public URI resolveURI( String codeSystem ) {
        return entries.containsKey( codeSystem ) ? resolve( codeSystem ).getUri() : null;
    }

    public String resolveURIAsString( String codeSystem ) {
        URI uri = resolveURI( codeSystem );
        return uri != null ? uri.toString() : null;
    }

    public URI resolveCode( String codeSystem, String code ) {
        return URI.create( resolveURIAsString( codeSystem ) + code );
    }


}
