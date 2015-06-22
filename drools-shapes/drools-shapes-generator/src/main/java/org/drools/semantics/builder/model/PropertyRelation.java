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

import org.drools.semantics.utils.NameUtils;
import org.kie.api.definition.type.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertyRelation extends Relation implements Cloneable {

    @Position(3)
    protected String name;

    private Integer minCard = 0;
    private Integer maxCard = null;
    private Concept target = null;
    private Concept domain = null;
    private Concept addableTarget = null;



    private boolean restricted = false;
    private boolean inherited = false;
    private boolean simple = false;
    private boolean attribute;
    private PropertyRelation baseProperty;
    private PropertyRelation inverse;
    private List<PropertyRelation> restrictedProperties = new ArrayList<PropertyRelation>();

    private Set<List<PropertyRelation>> chains = new HashSet<List<PropertyRelation>>();

    private boolean functional;


    public PropertyRelation( String subject, String property, String object, String name ) {
        this.subject = subject;
        this.property = property;
        this.object = object;
        this.name = name;
        baseProperty = this;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getMinCard() {
        int x =  minCard;
        for ( List<PropertyRelation> chain : chains ) {
            int m = 1;
            for ( PropertyRelation rel : chain ) {
                m = m * rel.getMinCard();
            }
            x = Math.max( x, m );
        }
        return x;
    }

    public void setMinCard(Integer minCard) {
        this.minCard = minCard;
    }

    public Integer getMaxCard() {
        if ( isFunctional() ) {
            return 1;
        }
        Integer x = maxCard;
        
            for ( List<PropertyRelation> chain : chains ) {
                Integer m = 1;
                for ( PropertyRelation rel : chain ) {
                    if ( rel.getMaxCard() == null ) {
                        m = null;
                        break;
                    }
                    m = m * rel.getMaxCard();
                }
                if ( m != null ) {
                    x = (x == null) ? m : Math.min( x, m );
                }
            }
        return x;
    }

    public void setMaxCard(Integer maxCard) {
        this.maxCard = maxCard;
    }


    public Concept getTarget() {
        return target;
    }

    public void setTarget(Concept target) {
        this.target = target;
    }

    public Concept getDomain() {
        return domain;
    }

    public void setDomain(Concept domain) {
        this.domain = domain;
    }

    public String toString() {
        return name;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public String getIri() {
        return property.replace( "<", "" ).replace( ">", "" );
    }


    public PropertyRelation clone() {
        PropertyRelation rel = new PropertyRelation( this.subject, this.property, this.object, this.name );
        rel.minCard = this.minCard;
        rel.maxCard = this.maxCard;
        rel.target = this.target;
        rel.domain = this.domain;
        rel.addableTarget = this.addableTarget;

        rel.restricted = this.restricted;
        rel.inherited = this.inherited;
        rel.simple = this.simple;
        rel.inverse = this.inverse;
        rel.functional = this.functional;
        rel.attribute = this.attribute;

        rel.baseProperty = this.baseProperty;
        rel.restrictedProperties = new ArrayList<PropertyRelation>( this.restrictedProperties );
        rel.chains = new HashSet<List<PropertyRelation>>( this.chains );

        return rel;
    }

    public PropertyRelation getBaseProperty() {
        if ( baseProperty == this ) {
            return this;
        } else {
            return baseProperty.getBaseProperty();
        }
    }

    public PropertyRelation getImmediateBaseProperty() {
        return baseProperty;
    }

    public void setBaseProperty(PropertyRelation baseProperty) {
        this.baseProperty = baseProperty;
        baseProperty.addRestrictedChild( this );
    }

    private void addRestrictedChild(PropertyRelation propertyRelation) {
        this.restrictedProperties.add( propertyRelation );
    }


    public boolean isCollection() {
        return getMaxCard() == null || getMaxCard() > 1;
    }

    public boolean isSimpleBoolean() {
        return getTarget().getName().equals("xsd:boolean") && ! isCollection();
    }

    public String getTypeName() {
        return NameUtils.map( getTarget().getName(), true );
    }

    public String getGetter( boolean enhanceNames ) {
        return NameUtils.getter( getName(), isSimpleBoolean() ? "boolean" : Boolean.class.getName(), getMaxCard(), enhanceNames );
    }

    public String getSetter( boolean enhanceNames ) {
        return NameUtils.setter( getName(), getMaxCard(), enhanceNames );
    }



    public String toFullString() {
        return "Property{" +
                "subject='" + subject + '\'' +
                ", property='" + property + '\'' +
                ", object='" + object + '\'' +
                ", name='" + name + '\'' +
                "," + minCard  +" .. " + maxCard +
                '}';
    }


    public boolean mirrors( PropertyRelation that ) {
        if ( this == that) return true;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (object != null ? !object.equals(that.object) : that.object != null) return false;
        if (property != null ? !property.equals(that.property) : that.property != null) return false;
        if (minCard != null ? !minCard.equals(that.minCard) : that.minCard != null) return false;
        if (maxCard != null ? !maxCard.equals(that.maxCard) : that.maxCard != null) return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyRelation that = (PropertyRelation) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (object != null ? !object.equals(that.object) : that.object != null) return false;
        if (property != null ? !property.equals(that.property) : that.property != null) return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public boolean isTransient() {
        return isChain() || isRestricted();
    }

    public void addPropertyChain(List<PropertyRelation> chain) {
        chains.add( chain );
    }

    public boolean isChain() {
        return chains.size() > 0;
    }

    public Set<List<PropertyRelation>> getChains() {
        return chains;
    }


    public boolean isReadOnly() {
        //return isChain();
        return isSimple() && isRestricted();
    }
    

    public boolean isLocalImplementationFor( Concept con ) {
        return con != null && con.getImplementingCon() != null && con.getImplementingCon().getChosenProperties().containsKey( this.property );
    }

    public boolean isInheritedFor( Concept con ) {
        return con != null && ! con.getIri().equals( domain.getIri() );
    }

    public boolean isInheritedFor( String conIri ) {
        return conIri != null && ! conIri.equals( domain.getIri() );
    }

    public void restrictTargetTo( Concept target ) {
        setAddableTarget(target);
        setObject( target.getIri() );
        for ( PropertyRelation sub : restrictedProperties ) {
            Concept restrTarget = sub.getTarget();
            if ( ! restrTarget.equals( target ) ) {
                if ( ! restrTarget.getSuperConcepts().contains( target ) ) {
                    sub.restrictTargetTo( target );
                }
            }
        }
        
    }

    public List<PropertyRelation> getRestrictedProperties() {
        return restrictedProperties;
    }

    public Concept getAddableTarget() {
        return addableTarget == null ? target : addableTarget;
    }

    public void setAddableTarget(Concept addableTarget) {
        this.addableTarget = addableTarget;
    }

    public PropertyRelation getInverse() {
        return inverse;
    }

    public void setInverse(PropertyRelation inverse) {
        this.inverse = inverse;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setFunctional( boolean functional ) {
        this.functional = functional;
        if ( functional ) {
            setSimple( true );
        }
    }

    public boolean isAttribute() {
        return attribute;
    }

    public void setAttribute( boolean attribute ) {
        this.attribute = attribute;
    }
}


