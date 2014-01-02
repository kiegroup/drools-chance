/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.builder.model;

import org.drools.definition.type.Position;
import org.drools.semantics.utils.NameUtils;
import org.semanticweb.owlapi.model.IRI;

import java.util.*;


public class Concept implements Cloneable {

    @Position(0)    private     IRI                             iri;
    @Position(1)    private     String                          name;
    @Position(7)    private     String                          namespace;
    @Position(9)    private     String                          pack;
    @Position(8)    private     BitSet                          typeCode;
    @Position(11)   private     BitSet                          areaCode;


    @Position(2)    private     Set<Concept>                    superConcepts;
    @Position(6)    private     Set<Concept>                    subConcepts;
    @Position(4)    private     Set<Concept>                    equivalentConcepts;

    @Position(3)    private     Map<String, PropertyRelation>   properties;
    @Position(5)    private     List<PropertyRelation>          keys;

    @Position(10)   private     ConceptImplProxy                implementingCon;

    public enum Resolution { NONE, CLASS, IFACE, ENUM ; }

    private     boolean                         primitive               = false;
    private     boolean                         abstrakt                = false;
    private     boolean                         anonymous               = false;
    private     boolean                         resolved                = false;
    private     Resolution                      resolvedAs              = Resolution.NONE;
    private     boolean                         shadowed                = false;



    public Concept( IRI iri, String name, boolean primitive ) {
        this.iri = iri;
        this.name = primitive ? name : NameUtils.compactUpperCase( name );
        this.pack = NameUtils.namespaceURIToPackage( iri.getStart() );
        this.superConcepts = new HashSet();
        this.subConcepts = new HashSet();
        this.properties = new HashMap();
        this.equivalentConcepts = new HashSet();
        this.keys = new ArrayList<PropertyRelation>();
        this.primitive = primitive;
        this.namespace = iri.getStart();
        this.implementingCon = new ConceptImplProxy( this );
    }

    @Override
    public String toString() {
        return name;
    }


    public String toFullString() {
        String supers = "[";
        for ( Object o : superConcepts ) {
            Concept con = (Concept) o;
            supers += con.iri + ",";
        }
        supers += "]";
        return "Concept{" + ( resolved ? " --- " : " +++ " ) +
                "iri='" + iri + '\'' +
                ", name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                supers +
//                ", properties=" + properties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Concept concept = (Concept) o;

        if (iri != null ? !iri.equals(concept.iri) : concept.iri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return iri != null ? iri.hashCode() : 0;
    }


    public String getIri() {
        return iri.toQuotedString();
    }

    public void setIri( String iri ) {
        this.iri = IRI.create( iri );
    }

    public void setIri( IRI iri ) {
        this.iri = iri;
    }


    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getPackage() {
        return pack;
    }

    public void setPackage( String pack ) {
        this.pack = pack;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace( String namespace ) {
        this.namespace = namespace;
    }

    public String getFullyQualifiedName() {
        if ( ! isPrimitive() && pack != null ) {
            return pack + "." + name;
        } else {
            return name;
        }
    }

    public BitSet getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(BitSet typeCode) {
        this.typeCode = typeCode;
    }

    public void setAreaCode(BitSet areaCode) {
        this.areaCode = areaCode;
    }

    public BitSet getAreaCode() {
        return areaCode;
    }





    public Set<Concept> getSuperConcepts() {
        return superConcepts;
    }

    public void addSuperConcept(Concept concept) {
        superConcepts.add( concept );
        concept.getSubConcepts().add( this );
    }

    public Set<Concept> getSubConcepts() {
        return subConcepts;
    }

    public Set<Concept> getEquivalentConcepts() {
        return equivalentConcepts;
    }





    public Map<String, PropertyRelation> getProperties() {
        return properties;
    }

    public void addProperty( String propIri, String propName, PropertyRelation prop ) {
        properties.put( propIri, prop );
    }

    public PropertyRelation getProperty( String propIri ) {
        return properties.get( propIri );
    }

    public void removeProperty( String propIri ) {
        properties.remove( propIri );
    }

    public Concept getPropertyRange( String propIri ) {
        return properties.get( propIri ).getTarget();
    }

    public List<PropertyRelation> getKeys() {
        List<PropertyRelation> keys = new ArrayList<PropertyRelation>( this.keys );
        for ( Concept sup : superConcepts ) {
            keys.addAll( sup.getKeys() );
        }
        return keys;
    }

    public void setKeys(List<PropertyRelation> keys) {
        this.keys = keys;
    }

    public void addKey( String key ){
        PropertyRelation k = lookupProperty(this, key);
        if ( ! keys.contains( k ) ) {
            keys.add( k );
        }
    }

    public PropertyRelation lookupProperty( String key ) {
        return lookupProperty( this, key );
    }

    protected PropertyRelation lookupProperty( Concept con, String key ) {
        PropertyRelation rel = con.getProperties().get(key);
        if ( rel != null ) {
            return rel;
        } else {
            for ( Concept sup : con.getSuperConcepts() ) {
                rel = lookupProperty( sup, key );
                if ( rel != null ) {
                    return rel;
                }
            }
        }
        return null;
    }


    public Set<PropertyRelation> getEffectiveProperties() {
        Set<PropertyRelation> ans = new HashSet<PropertyRelation>();
        for ( PropertyRelation prop : getProperties().values() ) {
            ans.add( prop );

            PropertyRelation current = prop;
            do {
                if ( current.isRestricted() ) {
                    ans.add( current.getImmediateBaseProperty() );
                }
                current = current.getImmediateBaseProperty();
            } while ( current != current.getBaseProperty() );
        }
        return ans;
    }

    public Set<PropertyRelation> getEffectiveBaseProperties() {
        Set<PropertyRelation> ans = new HashSet<PropertyRelation>();
        Set<PropertyRelation> eff = getAvailableProperties();

        for ( PropertyRelation prop : eff ) {
            ans.add( prop.getBaseProperty() );
        }

        return ans;
    }

    public Set<PropertyRelation> getAvailableProperties() {
        Set<PropertyRelation> ans = new HashSet<PropertyRelation>();

        for ( PropertyRelation prop : getProperties().values() ) {
            ans.add( prop );
        }
        for ( Concept sup : getSuperConcepts() ) {
            ans.addAll( sup.getAvailableProperties() );
        }

        return ans;
    }


    public ConceptImplProxy getImplementingCon() {
        return implementingCon;
    }

    public void setImplementingCon(ConceptImplProxy implementingCon) {
        this.implementingCon = implementingCon;
    }




    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public boolean isAbstrakt() {
        return abstrakt;
    }

    public void setAbstrakt(boolean abstrakt) {
        this.abstrakt = abstrakt;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isInherited( String propIri ) {
        return properties.containsKey( propIri ) && properties.get( propIri ).getDomain().getIri().equals( this.getIri() );
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public Resolution getResolvedAs() {
        return resolvedAs;
    }

    public void setResolvedAs(Resolution resolvedAs) {
        this.resolvedAs = resolvedAs;
    }

    public boolean isShadowed() {
        return shadowed;
    }

    public void setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
    }





    public Concept getChosenSuperConcept() {
        return implementingCon.getChosenSuper().getConcept();
    }

    public void setChosenSuperConcept( Concept chosenSuperConcept ) {
        this.implementingCon.setChosenSuper( chosenSuperConcept.getImplementingCon() );
    }
//
    public Set<Concept> getChosenSubConcepts() {
        Set<Concept> subs = new HashSet<Concept>();
        for ( ConceptImplProxy con : implementingCon.getChosenSubs() ) {
            subs.add( con.getConcept() );
        }
        return subs;
    }
//
    public void setChosenSubConcepts( Set<Concept> chosenSubConcepts ) {
        Set<ConceptImplProxy> subs = new HashSet<ConceptImplProxy>();
        for ( Concept con : chosenSubConcepts ) {
            subs.add( con.getImplementingCon() );
        }
        implementingCon.setChosenSubs( subs );
    }
//
    public Map<String, PropertyRelation> getChosenProperties() {
        return implementingCon.getChosenProperties();
    }
//
    public void setChosenProperties(Map<String, PropertyRelation> targetProperties) {
        this.implementingCon.setChosenProperties( targetProperties );
    }







    public Concept clone() {
        Concept con = new Concept( iri, name, primitive );

        con.getSuperConcepts().addAll( getSuperConcepts() );
        con.getProperties().putAll( getProperties() );
        con.getEquivalentConcepts().addAll( getEquivalentConcepts() );
        con.getKeys().addAll( getKeys() );
        con.getSubConcepts().addAll( getSubConcepts() );
        con.setPackage( getPackage() );
        con.setNamespace( getNamespace() );
        con.setTypeCode( getTypeCode() );
        ConceptImplProxy impl = getImplementingCon().clone();
        con.setImplementingCon( impl );
        impl.setConcept( con );

        return con;
    }

    public static class Range {
        private Concept     concept;
        private Integer     minCard = 1;
        private Integer     maxCard = null;

        public Range(Concept concept, Integer minCard, Integer maxCard) {
            this.concept = concept;
            this.minCard = minCard;
            this.maxCard = maxCard;
        }

        public Range(Concept concept) {
            this.concept = concept;
        }

        public Concept getConcept() {
            return concept;
        }

        public void setConcept(Concept concept) {
            this.concept = concept;
        }

        public Integer getMinCard() {
            return minCard;
        }

        public void setMinCard(Integer minCard) {
            this.minCard = minCard;
        }

        public Integer getMaxCard() {
            return maxCard;
        }

        public void setMaxCard(Integer maxCard) {
            this.maxCard = maxCard;
        }

        @Override
        public String toString() {
            return "Range{" +
                    "concept=" + concept +
                    ", minCard=" + minCard +
                    ", maxCard=" + maxCard +
                    '}';
        }
    }


}


