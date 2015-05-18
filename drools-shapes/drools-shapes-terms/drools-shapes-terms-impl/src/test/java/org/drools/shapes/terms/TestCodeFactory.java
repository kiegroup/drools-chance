/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms;

import edu.mayo.terms_metamodel.terms.ConceptDescriptor;
import org.drools.shapes.model.datatypes.CD;

import java.net.URI;

public class TestCodeFactory implements CodeFactory {

    private static final TestCodeFactory singleton = new TestCodeFactory();

    @Override
    public ConceptDescriptor buildCode( URI URI, String code, String codeName, String codeSystem, String codeSystemName ) {
        return new CD( URI, code, codeName, codeSystem, codeSystemName );
    }

    @Override
    public ConceptDescriptor buildCode( String code, String codeName, String codeSystem, String codeSystemName ) {
        return new CD( TestVocabularyCatalog.get().resolveCode( codeSystem, code ), code, codeName, codeSystem, codeSystemName );
    }

    @Override
    public ConceptDescriptor buildCode( String code, String codeName, ConceptDescriptor codeSystem ) {
        return new CD( TestVocabularyCatalog.get().resolveCode( codeSystem.getCode(), code ), code, codeName, codeSystem.getCode(), codeSystem.getDisplayName() );
    }

    public static CodeFactory get() {
        return singleton;
    }
}
