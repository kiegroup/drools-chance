/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms.workinprogress.vocabularies;

import org.drools.drools_shapes.terms.ConceptDescriptor;

public interface MultiTaxonomy extends Thesaurus, Taxonomy, org.drools.drools_shapes.terms.MultiTaxonomy {

    public void addSubConceptOf( ConceptDescriptor child, ConceptDescriptor property, ConceptDescriptor parent );
}
