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

package org.drools.semantics.builder.model.inference;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.utils.NameUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class AbstractModelInferenceStrategy implements ModelInferenceStrategy {



    public OntoModel buildModel( String name,
                                 OWLOntology ontoDescr,
                                 OntoModel.Mode mode,
                                 Map<InferenceTask, Resource> theory,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens,
                                 ClassLoader classLoader ) {

        StatefulKnowledgeSession kSession = buildKnowledgeSession( theory );

        OntoModel baseModel = ModelFactory.newModel( name, mode );
        baseModel.setOntology( ontoDescr );
        baseModel.setClassLoader( classLoader );

        baseModel.setDefaultPackage(NameUtils.namespaceURIToPackage(ontoDescr.getOntologyID().getOntologyIRI().toString()) );
        baseModel.setDefaultNamespace( ontoDescr.getOntologyID().getOntologyIRI().toString() );

        kSession.fireAllRules();

        kSession.insert( ontoDescr );
        kSession.fireAllRules();

        OntoModel latticeModel = buildClassLattice( ontoDescr, kSession, theory, baseModel, axiomGens );

        latticeModel.sort();

        OntoModel propertyModel = buildProperties( ontoDescr, kSession, theory, latticeModel );

        propertyModel.sort();

        OntoModel populatedModel = buildIndividuals( ontoDescr, kSession, theory, propertyModel );

        populatedModel.reassignConceptCodes();

        populatedModel.buildAreaTaxonomy();

        populatedModel.getMode().getProcessor().process( populatedModel );

        reportSessionStatus( kSession );

        return populatedModel;
    }



    protected abstract OntoModel buildProperties( OWLOntology ontoDescr, StatefulKnowledgeSession kSession, Map<InferenceTask, Resource> theory, OntoModel hierachicalModel );


    protected abstract OntoModel buildIndividuals( OWLOntology ontoDescr, StatefulKnowledgeSession kSession, Map<InferenceTask, Resource> theory, OntoModel hierachicalModel );


    protected abstract OntoModel buildClassLattice( OWLOntology ontoDescr,
                                                    StatefulKnowledgeSession kSession,
                                                    Map<InferenceTask, Resource> theory,
                                                    OntoModel baseModel,
                                                    List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators );


    protected abstract InferredOntologyGenerator initReasoner( StatefulKnowledgeSession kSession, OWLOntology ontoDescr, List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators );


    private StatefulKnowledgeSession buildKnowledgeSession(Map<InferenceTask, Resource> theory) {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for ( InferenceTask task : theory.keySet() ) {
            kbuilder.add( theory.get( task ), ResourceType.DRL );
            if ( kbuilder.hasErrors() ) {
                throw new RuntimeException( kbuilder.getErrors().toString() );
            }
        }
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase.newStatefulKnowledgeSession();
    }


    private void reportSessionStatus(StatefulKnowledgeSession kSession) {

        System.err.println( "----------------------- WM " + kSession.getObjects().size() + " --------------------------");
        for ( Object o : kSession.getObjects() ) {
            System.err.println("\t" + o );
        }
        System.err.println( "----------------------------------------------------------------");

    }


}
