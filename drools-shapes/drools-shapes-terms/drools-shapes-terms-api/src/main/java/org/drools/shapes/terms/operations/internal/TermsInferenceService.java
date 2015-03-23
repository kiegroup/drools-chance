/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.shapes.terms.operations.internal;

import CTS_PIM.ComputationalModel.ConceptDomainBindingServices.ConceptDomainBindingRead.ConceptDomainBindingReadService;
import CTS_PIM.ComputationalModel.ConceptDomainCatalogServices.ConceptDomainCatalogRead.ConceptDomainCatalogReadService;
import CTS_PIM.ComputationalModel.EntityDescriptionServices.EntityDescriptionQuery.EntityDescriptionQueryService;
import CTS_PIM.ComputationalModel.MapEntryServices.MapEntryRead.MapEntryReadService;
import CTS_PIM.ComputationalModel.ValueSetCatalogServices.ValueSetCatalogRead.ValueSetCatalogReadService;
import CTS_PIM.ComputationalModel.ValueSetDefinitionServices.ResolvedValueSetServices.ResolvedValueSetResolution;
import CTS_PIM.ComputationalModel.ValueSetDefinitionServices.ValueSetDefinitionRead.ValueSetDefinitionReadService;
import CTS_PIM.ComputationalModel.ValueSetDefinitionServices.ValueSetDefinitionResolution.ValueSetDefinitionResolution;

public interface TermsInferenceService {


    public ConceptDomainCatalogReadService conceptDomainCatalogRead();

    public ConceptDomainBindingReadService conceptDomainBindingRead();

    public MapEntryReadService mapEntryRead();

    public EntityDescriptionQueryService entityDescriptionQuery();

    public ResolvedValueSetResolution resolvedValueSetResolution();

    public ValueSetCatalogReadService valueSetCatalogRead();

    public ValueSetDefinitionResolution valueSetDefinitionResolution();

    public ValueSetDefinitionReadService valueSetDefinitionRead();

}
