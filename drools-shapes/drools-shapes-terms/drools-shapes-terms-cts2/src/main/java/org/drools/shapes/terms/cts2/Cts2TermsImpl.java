package org.drools.shapes.terms.cts2;

import edu.mayo.cts2.framework.service.profile.Cts2Profile;
import edu.mayo.cts2.framework.service.profile.conceptdomain.ConceptDomainReadService;
import edu.mayo.cts2.framework.service.profile.conceptdomainbinding.ConceptDomainBindingReadService;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQueryService;
import edu.mayo.cts2.framework.service.profile.mapentry.MapEntryReadService;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.ResolvedValueSetResolutionService;
import edu.mayo.cts2.framework.service.profile.valueset.ValueSetReadService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionReadService;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.ValueSetDefinitionResolutionService;
import edu.mayo.cts2.framework.service.provider.ServiceProvider;
import org.drools.shapes.terms.operations.internal.TermsInferenceService;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * OMG(R) CTS2 REST-Based ValueSetProcessor.
 */
public class Cts2TermsImpl implements TermsInferenceService {

    public static final String KIND = "cts2";

    private ServiceProvider serviceProvider;

    public Cts2TermsImpl() {
        this( null );
    }

    public Cts2TermsImpl( ServiceProvider serviceProvider ) {
        super();
        if( serviceProvider == null ) {
            ServiceLoader serviceLoader = ServiceLoader.load(ServiceProvider.class);
            Iterator<ServiceProvider> providers = serviceLoader.iterator();
            if(providers.hasNext()) {
                this.serviceProvider = providers.next();
                if( providers.hasNext() ) {
                    throw new ExceptionInInitializerError("More than one CTS2 ServiceProvider found.");
                }
            } else {
                throw new ExceptionInInitializerError("Could not find a CTS2 ServiceProvider.");
            }
        } else {
            this.serviceProvider = serviceProvider;
        }
    }

    @Override
    public ConceptDomainReadService conceptDomainCatalogRead() {
        return this.getCts2Service(ConceptDomainReadService.class);
    }

    @Override
    public ConceptDomainBindingReadService conceptDomainBindingRead() {
        return this.getCts2Service(ConceptDomainBindingReadService.class);
    }

    @Override
    public MapEntryReadService mapEntryRead() {
        return this.getCts2Service(MapEntryReadService.class);
    }

    @Override
    public EntityDescriptionQueryService entityDescriptionQuery() {
        return this.getCts2Service(EntityDescriptionQueryService.class);
    }

    @Override
    public ResolvedValueSetResolutionService resolvedValueSetResolution() {
        return this.getCts2Service(ResolvedValueSetResolutionService.class);
    }

    @Override
    public ValueSetReadService valueSetCatalogRead() {
        return this.getCts2Service(ValueSetReadService.class);
    }

    @Override
    public ValueSetDefinitionResolutionService valueSetDefinitionResolution() {
        return this.getCts2Service(ValueSetDefinitionResolutionService.class);
    }

    @Override
    public ValueSetDefinitionReadService valueSetDefinitionRead() {
        return this.getCts2Service(ValueSetDefinitionReadService.class);
    }

    protected <T extends Cts2Profile> T getCts2Service(Class<T> serviceClass) {
        T service = this.serviceProvider.getService(serviceClass);

        if(service == null) {
            throw new RuntimeException("Could not find CTS2 service for class: " + serviceClass.getName());
        }

        return service;
    }
}