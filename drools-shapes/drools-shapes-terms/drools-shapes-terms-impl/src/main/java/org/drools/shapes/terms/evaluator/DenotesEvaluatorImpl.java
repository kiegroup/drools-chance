package org.drools.shapes.terms.evaluator;

import org.drools.drools_shapes.terms.ConceptDescriptor;
import org.drools.shapes.terms.operations.TermsInference;
import org.drools.shapes.terms.operations.internal.TermsInferenceService;
import org.omg.spec.cts2.EntityReferenceList;
import org.omg.spec.cts2.MapEntry;
import org.omg.spec.cts2.NameOrURI;

import java.net.URI;

public class DenotesEvaluatorImpl implements TermsInference {

    TermsInferenceService provider;

    public DenotesEvaluatorImpl( TermsInferenceService provider ) {
        this.provider = provider;
    }

    public boolean denotes( ConceptDescriptor left, ConceptDescriptor right, String leftPropertyUri ) {
        if ( isValueSet( right ) ) {
            // it is a valueSet
            return ! provider.resolvedValueSetResolution().contains( right.getUri(), asSingletonList( left ) ).getEntry().isEmpty();
        } else if ( isValueSetDefinition( right ) ) {
            // it is a value set definition
            return ! provider.valueSetDefinitionResolution().contains( right.getUri(), asSingletonList( left ) ).getEntry().isEmpty();
        } else {
            URI leftUri = left.getUri();
            if ( ! sameCodeSystem( left, right ) ) {
                leftUri = mapIntoCodeSystem( left, right );
                if ( leftUri == null ) {
                    return false;
                }
            }

            if ( sameConceptDomain( left, right, leftPropertyUri ) ) {
                return provider.entityDescriptionQuery().isEntityInSet( descendants( right.getUri() ), leftUri );
            } else {
                // no other cases supported for now
                return false;
            }
        }

    }

    private URI mapIntoCodeSystem( ConceptDescriptor left, ConceptDescriptor right ) {
        MapEntry me = provider.mapEntryRead().read( asMapNameOrUri( left, right ), asNameOrURI( left ) );
        if ( me.getMapsTo().isEmpty() ) {
            return null;
        }
        return me.getMapsTo().iterator().next().getUri();
    }


    private boolean sameCodeSystem( ConceptDescriptor left, ConceptDescriptor right ) {
        return left.getCodeSystem().equals( right.getCodeSystem() );
    }

    private boolean sameConceptDomain( ConceptDescriptor left, ConceptDescriptor right, String leftPropertyURI ) {
        if ( leftPropertyURI == null ) {
            return true;
        }
        NameOrURI domainUri = asNameOrURI( URI.create( leftPropertyURI ) );
        if ( ! provider.conceptDomainCatalogRead().exists( domainUri ) ) {
            return true;
        }

        URI bindingsUri = provider.conceptDomainCatalogRead().read( domainUri ).getBindings();
        NameOrURI domainBindingURI = provider.conceptDomainBindingRead().readByURI( bindingsUri ).getBindingForNameOrURI();
        return ! provider.resolvedValueSetResolution().contains( domainBindingURI.getUri(), asSingletonList( right ) ).getEntry().isEmpty();
    }

    private boolean isValueSetDefinition( ConceptDescriptor right ) {
        return provider.valueSetDefinitionRead().exists( right.getUri() );
    }

    private boolean isValueSet( ConceptDescriptor right ) {
        return provider.valueSetCatalogRead().exists( asNameOrURI( right ) );
    }



    private EntityReferenceList asSingletonList( ConceptDescriptor cd ) {
        // TODO!!
        return null;
    }

    private NameOrURI asNameOrURI( ConceptDescriptor cd ) {
        // TODO!!
        return null;
    }

    private NameOrURI asNameOrURI( URI uri ) {
        // TODO!!
        return null;
    }

    private NameOrURI asMapNameOrUri( ConceptDescriptor left, ConceptDescriptor right ) {
        URI mapUri = URI.create( left.getUri() + "/map?tgt=" + right.getCodeSystem() );
        return null;
    }

    private URI descendants( URI entityURI ) {
        return URI.create( entityURI.toString() + "/descendants" );
    }
}
