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

package org.drools.semantics.builder;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.impl.BaseResource;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.inference.DelegateInferenceStrategy;
import org.drools.semantics.builder.model.inference.InternalInferenceStrategy;
import org.drools.semantics.builder.model.inference.ModelInferenceStrategy;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DLFactoryImpl implements DLFactory {


    private static DLFactoryImpl singleton;

//    private static ModelInferenceStrategy strategy = new DelegateInferenceStrategy();
    private ModelInferenceStrategy strategy = new InternalInferenceStrategy();

    public DLFactoryImpl() {

    }

    public static DLFactoryImpl getInstance() {
        if ( singleton == null ) {
            singleton = new DLFactoryImpl();
        }
        return singleton;
    }

    public void setInferenceStrategy( DLFactory.INFERENCE_STRATEGY strategy ) {
        switch ( strategy ) {
            case INTERNAL: this.strategy = new InternalInferenceStrategy(); break;

            case EXTERNAL:
            default      : this.strategy = new DelegateInferenceStrategy();
        }
    }

    public void setExternalReasoner(SupportedReasoners externalReasoner) {
        if ( strategy instanceof DelegateInferenceStrategy ) {
            DelegateInferenceStrategy.setExternalReasoner( externalReasoner );
        }

    }


    public OWLOntology parseOntology( Resource resource ) {
        try {

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();


            return manager.loadOntologyFromOntologyDocument( resource.getInputStream() );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }




    public String buildTableauRules( OWLOntology ontologyDescr, Resource[] visitor ) {

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for ( Resource res : visitor ) {
            knowledgeBuilder.add( res, ((BaseResource) res).getResourceType() );
        }
            if ( knowledgeBuilder.hasErrors() ) {
                System.err.println( knowledgeBuilder.getErrors().toString() );
                return null;
            }
        KnowledgeBase tabKB = knowledgeBuilder.newKnowledgeBase();

        if (tabKB != null) {
            StatefulKnowledgeSession ksession = tabKB.newStatefulKnowledgeSession();

            StringBuilder out = new StringBuilder();
            ksession.setGlobal( "out", out );
            ksession.setGlobal( "registry", DLTemplateManager.getTableauRegistry( DLTemplateManager.DLFamilies.FALC ) );

            ksession.fireAllRules();

            ksession.insert( ontologyDescr );
            ksession.fireAllRules();

            String tableauRules = out.toString();

            ksession.dispose();

            return tableauRules;
        }
        return null;
    }







    public OntoModel buildModel( String name, OWLOntology ontoDescr, Map<ModelInferenceStrategy.InferenceTask, Resource> theory ) {

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase("modelGenerator");
            StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        return buildModel( name, ontoDescr, theory, kSession );

    }

    private OntoModel buildModel( String name, OWLOntology ontoDescr, Map<ModelInferenceStrategy.InferenceTask, Resource> theory, StatefulKnowledgeSession kSession) {
        return strategy.buildModel( name, ontoDescr, theory, kSession );
    }


    /**
     * Builds an ontology-driven model from a DL resource, using a kSession
     * @param res
     * @param kSession
     * @return
     */
    public OntoModel buildModel( String name, Resource res, StatefulKnowledgeSession kSession ) {
        OWLOntology ontoDescr = DLFactoryImpl.getInstance().parseOntology( res );


        ClassPathResource common =  new ClassPathResource( "FALC_CommonVisitor.drl" );
            common.setResourceType( ResourceType.DRL );
        ClassPathResource visitor =  new ClassPathResource( "FALC_TableauBuilderVisitor.drl" );
            visitor.setResourceType( ResourceType.DRL );

        String tableau = DLFactoryImpl.getInstance().buildTableauRules( ontoDescr,
                                                                    new Resource[] { common, visitor });
        ByteArrayResource tableauRules = new ByteArrayResource( tableau.getBytes() );
            tableauRules.setResourceType( ResourceType.DRL );

        System.err.println(tableau);
        System.err.println(" ******************************** Tableau rules ready, now infer model *************************************** ");


//        ClassPathResource classBuilder = new ClassPathResource( "FALC_ModelGeneratorVisitor.drl" );
//            classBuilder.setResourceType( ResourceType.DRL );

        ClassPathResource classBuilder = new ClassPathResource( "FALC_ModelLatticeFullVisitor.drl" );
            classBuilder.setResourceType( ResourceType.DRL );
        ClassPathResource classPruner = new ClassPathResource( "FALC_ModelLatticeSimplePrune.drl" );
            classPruner.setResourceType( ResourceType.DRL );
        ClassPathResource propertyBuilder = new ClassPathResource( "FALC_ModelPropertyVisitor.drl" );
            propertyBuilder.setResourceType( ResourceType.DRL );


        Map<ModelInferenceStrategy.InferenceTask, Resource> theory = new LinkedHashMap<ModelInferenceStrategy.InferenceTask, Resource>();
        theory.put( ModelInferenceStrategy.InferenceTask.COMMON, common );
        theory.put( ModelInferenceStrategy.InferenceTask.TABLEAU, tableauRules );
        theory.put( ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_BUILD_AND_PRUNE, classBuilder );
        theory.put( ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_PRUNE, classPruner );
        theory.put( ModelInferenceStrategy.InferenceTask.PROPERTY_MATCH, propertyBuilder );

        OntoModel results = DLFactoryImpl.getInstance().buildModel( name,
                                                            ontoDescr,
                                                            theory,
                                                            kSession );

        results.setPackage( ontoDescr.getOntologyID().getOntologyIRI().toString()  );

        return results;
    }






    public OntoModel buildModel( String name, Resource res ) {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();

        setInferenceStrategy( DLFactory.INFERENCE_STRATEGY.EXTERNAL );

        return buildModel( name, res, kSession );
    }











}
