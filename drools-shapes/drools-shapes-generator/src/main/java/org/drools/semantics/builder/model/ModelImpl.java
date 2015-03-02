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


import org.drools.core.util.CodedHierarchy;
import org.drools.semantics.lang.dl.Property;
import org.drools.semantics.util.area.AreaTxn;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Set;

public abstract class ModelImpl implements OntoModel {


    private OntoModel innerModel;


    public OWLOntology getOntology() {
        return innerModel.getOntology();
    }

    public void setOntology( OWLOntology onto ) {
        innerModel.setOntology( onto );
    }

    public String getName() {
        return innerModel.getName();
    }

    public void setName( String name ) {
        innerModel.setName( name );
    }


    public void initFromBaseModel( OntoModel base ) {
        this.innerModel = base;
    }



    public String getDefaultPackage() {
        return innerModel.getDefaultPackage();
    }

    public void setDefaultPackage(String pack) {
        innerModel.setDefaultPackage( pack );
    }

    public Set<String> getAllPackageNames() {
        return innerModel.getAllPackageNames();
    }

    public String getDefaultNamespace() {
        return innerModel.getDefaultNamespace();
    }

    public void setDefaultNamespace( String namespace ) {
        innerModel.setDefaultNamespace( namespace );
    }


    public Set<Individual> getIndividuals() {
        return innerModel.getIndividuals();
    }

    public void addIndividual( Individual i ) {
        innerModel.addIndividual( i );
    }

    public Individual removeIndividual( Individual i ) {
        return innerModel.removeIndividual( i );
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

    public PropertyRelation addProperty( PropertyRelation rel ) {
        return innerModel.addProperty( rel );
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


    public Mode getMode() {
        return innerModel.getMode();
    }

    public boolean isHierarchyConsistent() {
        return innerModel.isHierarchyConsistent();
    }

    public ClassLoader getClassLoader() {
        return innerModel.getClassLoader();
    }

    public void setClassLoader( ClassLoader classLoader ) {
        innerModel.setClassLoader( classLoader );
    }

    public void reassignConceptCodes() {
        innerModel.reassignConceptCodes();
    }

    public CodedHierarchy<Concept> getConceptHierarchy() {
        return innerModel.getConceptHierarchy();
    }

    public void buildAreaTaxonomy() {
        innerModel.buildAreaTaxonomy();
    }

    public AreaTxn<Concept,PropertyRelation> getAreaTaxonomy() {
        return innerModel.getAreaTaxonomy();
    }

    @Override
    public boolean isStandalone() {
        return innerModel.isStandalone();
    }

    @Override
    public void setStandalone( boolean standalone ) {
        innerModel.setStandalone( standalone );
    }

    @Override
    public boolean isMinimal() {
        return innerModel.isMinimal();
    }

    @Override
    public void setMinimal( boolean minimal ) {
        innerModel.setMinimal( minimal );
    }
}
