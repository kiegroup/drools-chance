/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms;

import org.drools.drools_shapes.terms.ConceptDescriptor;

import java.net.URI;

public interface CodeFactory {

    public ConceptDescriptor buildCode( URI URI, String code, String codeName, String codeSystem, String codeSystemName );

    public ConceptDescriptor buildCode( String code, String codeName, String codeSystem, String codeSystemName );

    public ConceptDescriptor buildCode( String code, String codeName, ConceptDescriptor codeSystem );

}
