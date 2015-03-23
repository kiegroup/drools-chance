/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms;


import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.HierarchyEncoderImpl;
import org.drools.drools_shapes.terms.ConceptDescriptor;
import org.drools.drools_shapes.terms.MemberRange;
import org.drools.shapes.terms.operations.internal.TermsInferenceService;
import org.drools.shapes.terms.workinprogress.vocabularies.Taxonomy;
import org.omg.spec.cts2.ConceptDomainBindingRead;
import org.omg.spec.cts2.ConceptDomainCatalogReadService;
import org.omg.spec.cts2.EntityDescriptionQueryService;
import org.omg.spec.cts2.MapEntryReadService;
import org.omg.spec.cts2.ResolvedValueSetResolution;
import org.omg.spec.cts2.ValueSetCatalogRead;
import org.omg.spec.cts2.ValueSetDefinitionRead;
import org.omg.spec.cts2.ValueSetDefinitionResolution;

import java.util.List;

public class InternalTermsImpl implements TermsInferenceService, Taxonomy {

    public static final String KIND = "mem";

    HierarchyEncoder kb = new HierarchyEncoderImpl();

    @Override
    public void addSubConceptOf( ConceptDescriptor child, ConceptDescriptor parent ) {
        
    }

    @Override
    public void addEntity( ConceptDescriptor code ) {
        throw new UnsupportedOperationException( "Use addSubConceptOf instead" );
    }

    @Override
    public List<MemberRange> getMember() {
        throw new UnsupportedOperationException( "Use addSubConceptOf instead" );
    }

    @Override
    public void setMember( List<MemberRange> value ) {
        throw new UnsupportedOperationException( "Use addSubConceptOf instead" );
    }

    @Override
    public ConceptDomainCatalogReadService conceptDomainCatalogRead() {
        return null;
    }

    @Override
    public ConceptDomainBindingRead conceptDomainBindingRead() {
        return null;
    }

    @Override
    public MapEntryReadService mapEntryRead() {
        return null;
    }

    @Override
    public EntityDescriptionQueryService entityDescriptionQuery() {
        return null;
    }

    @Override
    public ResolvedValueSetResolution resolvedValueSetResolution() {
        return null;
    }

    @Override
    public ValueSetCatalogRead valueSetCatalogRead() {
        return null;
    }

    @Override
    public ValueSetDefinitionResolution valueSetDefinitionResolution() {
        return null;
    }

    @Override
    public ValueSetDefinitionRead valueSetDefinitionRead() {
        return null;
    }
}
