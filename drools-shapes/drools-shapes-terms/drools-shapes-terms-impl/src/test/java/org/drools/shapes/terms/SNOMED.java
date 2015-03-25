/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms;


import org.drools.drools_shapes.terms.ConceptDescriptor;

import java.net.URI;

public class SNOMED {

    public static final String codeSystemName = "SNOMED-CT";
    public static final String codeSystem = "sctid";
    public static final URI codeSystemURI = URI.create( "http://snomed.ct" );

    public static final ConceptDescriptor SELF = TestCodeFactory.get().buildCode( codeSystemURI, codeSystem, codeSystemName, null, null );

    static {
        TestVocabularyCatalog.get().register( codeSystem, SELF );
    }

    public static final ConceptDescriptor AcuteDisease = TestCodeFactory.get().buildCode( "2704003", "acute disease", codeSystem, codeSystemName );
    public static final ConceptDescriptor MedicalProblem = TestCodeFactory.get().buildCode( "33070002", "medical problem", codeSystem, codeSystemName );
    public static final ConceptDescriptor DiabetesTypeII = TestCodeFactory.get().buildCode( "44054006", "diabetes type II", codeSystem, codeSystemName );


}

