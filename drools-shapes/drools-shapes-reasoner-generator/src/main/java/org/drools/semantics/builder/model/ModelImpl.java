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


import java.util.List;
import java.util.Set;

public abstract class ModelImpl implements OntoModel {


    private OntoModel innerModel;

    
    public String getName() {
        return innerModel.getName();
    }
    
    public void setName( String name ) {
        innerModel.setName( name );
    }


    public void initFromBaseModel( OntoModel base ) {
        this.innerModel = base;
    }



    public String getPackage() {
        return innerModel.getPackage();
    }

    public void setPackage(String pack) {
        innerModel.setPackage( pack );
    }


    public List<Concept> getConcepts() {
        return innerModel.getConcepts();
    }

    public Concept getConcept( String id ) {
        return innerModel.getConcept( id );
    }

    public void addConcept( Concept con ) {
        innerModel.addConcept( con );
    }

    public Concept removeConcept( Concept con ) {
        return innerModel.removeConcept( con );
    }




    public Set<SubConceptOf> getSubConcepts() {
        return innerModel.getSubConcepts();
    }

    public void addSubConceptOf( SubConceptOf sub ) {
        innerModel.addSubConceptOf( sub );
    }

    public boolean removeSubConceptOf( SubConceptOf sub ) {
        return innerModel.removeSubConceptOf( sub );
    }

    public SubConceptOf getSubConceptOf( String sub, String sup ) {
        return innerModel.getSubConceptOf( sub, sup );
    }



    public Set<PropertyRelation> getProperties() {
        return innerModel.getProperties();
    }

    public void addProperty( PropertyRelation rel ) {
        innerModel.addProperty( rel );
    }

    public PropertyRelation removeProperty( PropertyRelation rel ) {
        return innerModel.removeProperty( rel );
    }

    public PropertyRelation getProperty( String iri ) {
        return innerModel.getProperty( iri );
    }


    @Override
    public String toString() {
        return innerModel.toString();
    }




    protected abstract String traitsToString();


    public void sort() {
        innerModel.sort();
    }


    public void flatten() {
        innerModel.flatten();
    }

    public void elevate() {
        innerModel.elevate();
    }

    public boolean isFlat() {
        return innerModel.isFlat();
    }

}
