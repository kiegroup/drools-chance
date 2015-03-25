package org.drools.shapes.terms.evaluator;


import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.service.core.NameOrURIList;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.service.command.restriction.EntityDescriptionQueryServiceRestrictions;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntitiesFromAssociationsQuery;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import edu.mayo.cts2.framework.service.profile.mapentry.name.MapEntryReadId;
import edu.mayo.cts2.framework.service.profile.resolvedvalueset.name.ResolvedValueSetReadId;
import edu.mayo.cts2.framework.service.profile.valuesetdefinition.name.ValueSetDefinitionReadId;
import org.apache.commons.lang.StringUtils;
import org.drools.drools_shapes.terms.ConceptDescriptor;
import org.drools.shapes.terms.operations.TermsInference;
import org.drools.shapes.terms.operations.internal.TermsInferenceService;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DenotesEvaluatorImpl implements TermsInference {

    TermsInferenceService provider;

    public DenotesEvaluatorImpl( TermsInferenceService provider ) {
        this.provider = provider;
    }

    public boolean denotes( ConceptDescriptor left, ConceptDescriptor right, String leftPropertyUri ) {
        if ( isValueSet( right ) ) {
            // it is a valueSet
            return provider.resolvedValueSetResolution().contains( toResolvedValueSetId(right), asEntityNameOrURIList( left ) ).getEntry().length > 0;
        } else if ( isValueSetDefinition( right ) ) {
            // it is a value set definition
            return provider.valueSetDefinitionResolution().contains( toValueSetDefinitionId( right ), asEntityNameOrURIList( left )).getEntry().length > 0;
        } else {

            if ( sameConceptDomain( left, right, leftPropertyUri ) ) {
                return provider.entityDescriptionQuery().isEntityInSet( asEntityNameOrURI(left), descendants( right ), getReadContext() );
            } else {
                // no other cases supported for now
                return false;
            }
        }

    }

    private EntityDescriptionQuery descendants(ConceptDescriptor right) {
        EntityNameOrURI nameOrURI = new EntityNameOrURI();
        nameOrURI.setEntityName( ModelUtils.createScopedEntityName( right.getCode(), right.getCodeSystem() ) );

        return new HierarchyEntityDescriptionQueryImpl( nameOrURI );
    }

    private ResolvedValueSetReadId toResolvedValueSetId(ConceptDescriptor right) {
        return new ResolvedValueSetReadId( null, ModelUtils.nameOrUriFromName( right.getValueSet() ), null );
    }

    private ValueSetDefinitionReadId toValueSetDefinitionId(ConceptDescriptor conceptDescriptor ) {
        return new ValueSetDefinitionReadId( conceptDescriptor.getValueSet() );
    }

    private MapEntryReadId toMapEntryId(ConceptDescriptor conceptDescriptor) {
        throw new UnsupportedOperationException("not implemented");
    }

    private URI mapIntoCodeSystem( ConceptDescriptor left, ConceptDescriptor right ) {
        //todo:
        return null;
    }

    private boolean sameCodeSystem( ConceptDescriptor left, ConceptDescriptor right ) {
        return left.getCodeSystem().equals( right.getCodeSystem() );
    }

    private boolean sameConceptDomain( ConceptDescriptor left, ConceptDescriptor right, String leftPropertyURI ) {
        if ( leftPropertyURI == null ) {
            return true;
        }
        NameOrURI domainUri = asNameOrURI( URI.create( leftPropertyURI ) );
        if ( ! provider.conceptDomainCatalogRead().exists( domainUri, getReadContext() ) ) {
            return true;
        }

        //todo:

        return true;
    }

    private boolean isValueSetDefinition( ConceptDescriptor right ) {
        // TODO: ValueSetDefinitionReadService not up yet
        return false;
    }

    private boolean isValueSet( ConceptDescriptor right ) {
        return StringUtils.isNotBlank( right.getValueSet() );
    }

    private NameOrURIList asNameOrURIList( ConceptDescriptor... cds ) {
        NameOrURIList list = new NameOrURIList();

        List<NameOrURI> nameOrURIList = new ArrayList<NameOrURI>();
        for(ConceptDescriptor cd : cds) {
            NameOrURI nameOrURI = asNameOrURI(cd);
            nameOrURIList.add(nameOrURI);
        }

        return list;
    }

    private Set<EntityNameOrURI> asEntityNameOrURIList( ConceptDescriptor... cds ) {
        Set<EntityNameOrURI> nameOrURIList = new HashSet<EntityNameOrURI>();
        for(ConceptDescriptor cd : cds) {
            EntityNameOrURI nameOrURI = asEntityNameOrURI(cd);
            nameOrURIList.add(nameOrURI);
        }

        return nameOrURIList;
    }

    private NameOrURI asNameOrURI( ConceptDescriptor cd ) {
        NameOrURI nameOrURI = new NameOrURI();
        nameOrURI.setUri(cd.getUri().toString());

        return nameOrURI;
    }

    private NameOrURI asNameOrURI( URI uri ) {
        NameOrURI nameOrURI = new NameOrURI();
        nameOrURI.setUri(uri.toString());

        return nameOrURI;
    }

    private EntityNameOrURI asEntityNameOrURI( URI uri ) {
        EntityNameOrURI entityNameOrURI = new EntityNameOrURI();
        entityNameOrURI.setUri(uri.toString());

        return entityNameOrURI;
    }

    private EntityNameOrURI asEntityNameOrURI( ConceptDescriptor cd ) {
        EntityNameOrURI entityNameOrURI = new EntityNameOrURI();

        if(StringUtils.isNotBlank(cd.getCode())) {
            ScopedEntityName name = new ScopedEntityName();
            name.setName(cd.getCode());
            name.setNamespace(cd.getCodeSystem());
            entityNameOrURI.setEntityName(name);
        } else {
            entityNameOrURI.setUri(cd.getUri().toString());
        }

        return entityNameOrURI;
    }

    private ResolvedReadContext getReadContext() {
        return null;
    }

    private static class HierarchyEntityDescriptionQueryImpl implements EntityDescriptionQuery {

        private EntityNameOrURI entity;

        private HierarchyEntityDescriptionQueryImpl(EntityNameOrURI entity) {
            super();
            this.entity = entity;
        }

        @Override
        public EntitiesFromAssociationsQuery getEntitiesFromAssociationsQuery() {
            return null;
        }

        @Override
        public EntityDescriptionQueryServiceRestrictions getRestrictions() {
            EntityDescriptionQueryServiceRestrictions restrictions = new EntityDescriptionQueryServiceRestrictions();

            EntityDescriptionQueryServiceRestrictions.HierarchyRestriction hierarchyRestriction = new EntityDescriptionQueryServiceRestrictions.HierarchyRestriction();
            hierarchyRestriction.setHierarchyType(EntityDescriptionQueryServiceRestrictions.HierarchyRestriction.HierarchyType.DESCENDANTS);

            hierarchyRestriction.setEntity(this.entity);

            restrictions.setHierarchyRestriction(hierarchyRestriction);

            return restrictions;
        }

        @Override
        public Query getQuery() {
            return null;
        }

        @Override
        public Set<ResolvedFilter> getFilterComponent() {
            return null;
        }

        @Override
        public ResolvedReadContext getReadContext() {
            return null;
        }
    }

}
