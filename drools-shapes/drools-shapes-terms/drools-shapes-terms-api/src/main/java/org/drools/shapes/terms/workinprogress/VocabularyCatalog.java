/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms.workinprogress;

import edu.mayo.terms_metamodel.terms.ConceptDescriptor;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface VocabularyCatalog {

    public void register( String codeSystem, ConceptDescriptor code );

    public ConceptDescriptor resolve( String codeSystem );

    public URI resolveURI( String codeSystem );

    public String resolveURIAsString( String codeSystem );

    public URI resolveCode( String codeSystem, String code );
}
