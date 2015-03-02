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
import org.drools.semantics.builder.model.hierarchy.DatabaseModelProcessor;
import org.drools.semantics.builder.model.hierarchy.FlatModelProcessor;
import org.drools.semantics.builder.model.hierarchy.HierarchicalModelProcessor;
import org.drools.semantics.builder.model.hierarchy.ModelHierarchyProcessor;
import org.drools.semantics.builder.model.hierarchy.NullModelProcessor;
import org.drools.semantics.builder.model.hierarchy.OptimizedModelProcessor;
import org.drools.semantics.builder.model.hierarchy.VariantModelProcessor;
import org.drools.semantics.util.area.AreaTxn;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Set;

public interface OntoModel extends Cloneable {


    public static enum Mode  {
        HIERARCHY( new HierarchicalModelProcessor() ),
        FLAT( new FlatModelProcessor() ),
        VARIANT( new VariantModelProcessor() ),
        OPTIMIZED( new OptimizedModelProcessor() ),
        DATABASE( new DatabaseModelProcessor() ),
        NONE( new NullModelProcessor() );

        private ModelHierarchyProcessor processor;

        Mode( ModelHierarchyProcessor prox ) {
            processor = prox;
        }

        public ModelHierarchyProcessor getProcessor() {
            return processor;
        }
    }


    public OWLOntology getOntology();

    public void setOntology( OWLOntology onto );

    public String getDefaultPackage();
        
    public void setDefaultPackage( String pack );

    public Set<String> getAllPackageNames();

    public String getName();
        
    public void setName( String name );

    public String getDefaultNamespace();

    public void setDefaultNamespace( String ns );
    
    

    public List<Concept> getConcepts();

    public Concept getConcept( String id );

    public void addConcept( Concept con );

    public Concept removeConcept( Concept con );


    public Set<Individual> getIndividuals();
    
    public void addIndividual( Individual i );
    
    public Individual removeIndividual( Individual i );


    public Set<SubConceptOf> getSubConcepts();

    public void addSubConceptOf( SubConceptOf sub );

    public SubConceptOf getSubConceptOf( String sub, String sup );

    public boolean removeSubConceptOf( SubConceptOf sub );



    public Set<PropertyRelation> getProperties();

    public PropertyRelation addProperty( PropertyRelation rel );

    public PropertyRelation removeProperty( PropertyRelation rel );

    public PropertyRelation getProperty( String iri );


    public void sort();

    public boolean isHierarchyConsistent();

    public Mode getMode();


    public ClassLoader getClassLoader();

    public void setClassLoader( ClassLoader classLoader );


    public void reassignConceptCodes();

    public CodedHierarchy<Concept> getConceptHierarchy();

    public void buildAreaTaxonomy();

    public AreaTxn<Concept,PropertyRelation> getAreaTaxonomy();

    /* interface only - do not extend drools Thing */
    public boolean isStandalone();

    public void setStandalone( boolean standalone );

    /* interface only - expose only the minimal functionalities */
    public boolean isMinimal();

    public void setMinimal( boolean minimal );
}
