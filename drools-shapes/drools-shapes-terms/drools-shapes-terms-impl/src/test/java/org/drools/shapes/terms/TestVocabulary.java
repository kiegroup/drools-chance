/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms;

import cts2.mayo.edu.terms_metamodel.terms.ConceptDescriptor;

import java.net.URI;

public class TestVocabulary {

    public static final String codeSystemName = "FOO";
    public static final String codeSystem = "1.2.3.4.5.6.00000.abc";
    public static final URI codeSystemURI = URI.create( "http://my.foo.org/foo/temp/" );

    public static final ConceptDescriptor SELF = TestCodeFactory.get().buildCode( codeSystemURI, codeSystem, codeSystemName, null, null );

    static {
        TestVocabularyCatalog.get().register( codeSystem, SELF );
    }

    public static final ConceptDescriptor GestationalDiabetes = TestCodeFactory.get().buildCode( "99.1.2.3", "gestational diabetes", codeSystem, codeSystemName );
    public static final ConceptDescriptor Diabetes = TestCodeFactory.get().buildCode( "99.1.2", "diabetes", codeSystem, codeSystemName );
    public static final ConceptDescriptor EndocrineSystemDisease = TestCodeFactory.get().buildCode( "99.1", "endocrine system disease", codeSystem, codeSystemName );
    public static final ConceptDescriptor Disease = TestCodeFactory.get().buildCode( "99", "disease", codeSystem, codeSystemName );


    public static final ConceptDescriptor AcuteDisease = TestCodeFactory.get().buildCode( "2704003", "acute disease", codeSystem, codeSystemName );

}

