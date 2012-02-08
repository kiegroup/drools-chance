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

import java.util.*;

public class Concept implements Cloneable {

    @Position(0)    private     String                          iri;
    @Position(1)    private     String                          name;
    @Position(2)    private     Set<Concept>                    superConcepts;
    @Position(3)    private     Map<String, PropertyRelation>   properties;
    @Position(4)    private     Set<Concept>                    equivalentConcepts;    
    @Position(5)    private     List<PropertyRelation>          keys;
    @Position(6)    private     Set<Concept>                    subConcepts;
    @Position(7)    private     Map<String, PropertyRelation>   shadowProperties;
    @Position(8)    private     String                          chosenSuper;
    

    private     boolean                         primitive               = false;
    private     boolean                         abstrakt                = false;
    private     boolean                         anonymous               = false;



    public Concept(String iri, String name) {
        this.iri = iri;
        this.name = name;
        this.superConcepts = new HashSet();
        this.subConcepts = new HashSet();
        this.properties = new HashMap();
        this.shadowProperties = new HashMap();
        this.equivalentConcepts = new HashSet();
        this.keys = new ArrayList<PropertyRelation>();
    }

    public Concept(String iri, String name, Set superConcepts, Map properties, Set equivalentConcepts, Set subConcepts, Map shadowProperties ) {
        this.iri = iri;
        this.name = name;
        this.superConcepts = superConcepts != null ? superConcepts : new HashSet<Concept>();
        this.properties = properties != null ? properties : new HashMap<String, PropertyRelation>();
        this.shadowProperties = shadowProperties != null ? shadowProperties : new HashMap<String, PropertyRelation>();
        this.equivalentConcepts = equivalentConcepts != null ? equivalentConcepts : new HashSet<Concept>();
        this.subConcepts = subConcepts != null ? subConcepts : new HashSet<Concept>();
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
        return "Concept{" +
                "iri='" + iri + '\'' +
                ", name='" + name + '\'' +
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
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setSubConcepts(Set<Concept> subConcepts) {
        this.subConcepts = subConcepts;
    }

    public void setSuperConcepts(Set<Concept> superConcepts) {
        this.superConcepts = superConcepts;
    }

    public Map<String, PropertyRelation> getProperties() {
        return properties;
    }

    public void addProperty( String propIri, String propName, PropertyRelation prop ) {
        properties.put( propIri, prop );
    }

    public void removeProperty( String propIri ) {
        properties.remove( propIri );
    }


    public Concept getPropertyRange( String propIri ) {
        return ((PropertyRelation) properties.get( propIri )).getTarget();
    }


    public Set<Concept> getEquivalentConcepts() {
        return equivalentConcepts;
    }

    public void setEquivalentConcepts(Set<Concept> equivalentConcepts) {
        this.equivalentConcepts = equivalentConcepts;
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

    public String getChosenSuper() {
        return chosenSuper;
    }

    public void setChosenSuper(String chosenSuper) {
        this.chosenSuper = chosenSuper;
    }

    public boolean isInherited( String propIri ) {
        return properties.containsKey( propIri ) && properties.get( propIri ).getDomain().getIri().equals( this.getIri() );
    }


    public Map<String, PropertyRelation> getShadowProperties() {
        return shadowProperties;
    }

    public void setShadowProperties(Map<String, PropertyRelation> shadowProperties) {
        this.shadowProperties = shadowProperties;
    }

    public void addShadowProperty( String propIri, PropertyRelation shadow ) {
        this.shadowProperties.put( propIri, shadow );
    }

    public void setProperties(Map<String, PropertyRelation> properties) {
        this.properties = properties;
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


