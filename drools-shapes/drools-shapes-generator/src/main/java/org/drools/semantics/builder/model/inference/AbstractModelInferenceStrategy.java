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

import org.drools.semantics.builder.DLFactoryConfiguration;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.utils.NameUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.util.List;
import java.util.Map;

public abstract class AbstractModelInferenceStrategy implements ModelInferenceStrategy {



    public OntoModel buildModel( String name,
                                 OWLOntology ontoDescr,
                                 DLFactoryConfiguration conf,
                                 Map<InferenceTask, Resource> theory,
                                 ClassLoader classLoader ) {

        KieSession kSession = buildKnowledgeSession( theory );

        OntoModel baseModel = ModelFactory.newModel( name, conf.getMode() );
        baseModel.setOntology( ontoDescr );
        baseModel.setClassLoader( classLoader );

        baseModel.setDefaultPackage(NameUtils.namespaceURIToPackage(ontoDescr.getOntologyID().getOntologyIRI().toString()) );
        baseModel.setDefaultNamespace( ontoDescr.getOntologyID().getOntologyIRI().toString() );

        kSession.fireAllRules();

        kSession.insert( ontoDescr );
        kSession.fireAllRules();

        OntoModel latticeModel = buildClassLattice( ontoDescr, kSession, theory, baseModel, conf );

        latticeModel.sort();

        OntoModel propertyModel = buildProperties( ontoDescr, kSession, theory, latticeModel, conf );

        propertyModel.sort();

        OntoModel populatedModel = buildIndividuals( ontoDescr, kSession, theory, propertyModel, conf );

        populatedModel.reassignConceptCodes();

        populatedModel.buildAreaTaxonomy();

        populatedModel.getMode().getProcessor().process( populatedModel );

        reportSessionStatus( kSession );

        return populatedModel;
    }


    protected abstract OntoModel buildProperties( OWLOntology ontoDescr, KieSession kSession, Map<InferenceTask, Resource> theory, OntoModel hierachicalModel, DLFactoryConfiguration conf );


    protected abstract OntoModel buildIndividuals( OWLOntology ontoDescr, KieSession kSession, Map<InferenceTask, Resource> theory, OntoModel hierachicalModel, DLFactoryConfiguration conf );


    protected abstract OntoModel buildClassLattice( OWLOntology ontoDescr,
                                                    KieSession kSession,
                                                    Map<InferenceTask, Resource> theory,
                                                    OntoModel baseModel,
                                                    DLFactoryConfiguration conf );


    protected abstract OWLReasoner initReasoner( KieSession kSession, OWLOntology ontoDescr );


    private KieSession buildKnowledgeSession( Map<InferenceTask, Resource> theory ) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        
        for ( InferenceTask task : theory.keySet() ) {
            kfs.write( theory.get( task ).setResourceType( org.kie.api.io.ResourceType.DRL ) );
        }
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        kieBuilder.buildAll();
        if ( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) ) {
            throw new RuntimeException( kieBuilder.getResults().getMessages( Message.Level.ERROR ).toString() );
        }

        KieBase kieBase = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() ).getKieBase();        
        return kieBase.newKieSession();
    }


    private void reportSessionStatus(KieSession kSession) {

        System.err.println( "----------------------- WM " + kSession.getObjects().size() + " --------------------------");
        for ( Object o : kSession.getObjects() ) {
            System.err.println("\t" + o );
        }
        System.err.println( "----------------------------------------------------------------");

    }


}
