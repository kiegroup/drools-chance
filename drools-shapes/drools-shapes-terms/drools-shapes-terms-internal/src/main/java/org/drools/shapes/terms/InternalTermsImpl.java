/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms;


import edu.mayo.cts2.framework.service.profile.conceptdomain.ConceptDomainReadService;
import edu.mayo.cts2.framework.service.profile.conceptdomainbinding.ConceptDomainBindingReadService;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQueryService;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryReadService;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetResolutionService;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetReadService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionReadService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionResolutionService;
import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.HierarchyEncoderImpl;
import org.drools.drools_shapes.terms.ConceptDescriptor;
import org.drools.drools_shapes.terms.MemberRange;
import org.drools.shapes.terms.operations.internal.TermsInferenceService;
import org.drools.shapes.terms.workinprogress.vocabularies.Taxonomy;

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
    public ConceptDomainReadService conceptDomainCatalogRead() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ConceptDomainBindingReadService conceptDomainBindingRead() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public MapEntryReadService mapEntryRead() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public EntityDescriptionQueryService entityDescriptionQuery() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ResolvedValueSetResolutionService resolvedValueSetResolution() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ValueSetReadService valueSetCatalogRead() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ValueSetDefinitionResolutionService valueSetDefinitionResolution() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ValueSetDefinitionReadService valueSetDefinitionRead() {
        throw new UnsupportedOperationException("not implemented");
    }
}
