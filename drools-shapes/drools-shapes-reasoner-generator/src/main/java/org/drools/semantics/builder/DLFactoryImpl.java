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
import org.drools.semantics.builder.model.inference.ModelInferenceStrategy;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DLFactoryImpl implements DLFactory {


    private static DLFactoryImpl singleton;



    public static final List<InferredAxiomGenerator<? extends OWLAxiom>> defaultAxiomGenerators = Collections.unmodifiableList(
            new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>(
                    Arrays.asList(
                            new InferredClassAssertionAxiomGenerator(),
                            new InferredDataPropertyCharacteristicAxiomGenerator(),
                            new InferredEquivalentClassAxiomGenerator(),
                            new InferredEquivalentDataPropertiesAxiomGenerator(),
                            new InferredEquivalentObjectPropertyAxiomGenerator(),
                            new InferredInverseObjectPropertiesAxiomGenerator(),
                            new InferredObjectPropertyCharacteristicAxiomGenerator(),
                            new InferredPropertyAssertionGenerator(),
                            new InferredSubClassAxiomGenerator(),
                            new InferredSubDataPropertyAxiomGenerator(),
                            new InferredSubObjectPropertyAxiomGenerator()
                    ) ) );

    public static final List<ModelInferenceStrategy.InferenceTask> defaultInferenceTasks = Collections.unmodifiableList(
            new ArrayList<ModelInferenceStrategy.InferenceTask>(
                    Arrays.asList(
                            ModelInferenceStrategy.InferenceTask.COMMON,
//                        ModelInferenceStrategy.InferenceTask.TABLEAU,
//                        ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_BUILD_AND_PRUNE,
//                        ModelInferenceStrategy.InferenceTask.PROPERTY_MATCH
                            ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_PRUNE
                    ) ) );


    private ModelInferenceStrategy strategy = new DelegateInferenceStrategy();

    public DLFactoryImpl() {

    }

    public static DLFactoryImpl getInstance() {
        if ( singleton == null ) {
            singleton = new DLFactoryImpl();
        }
        return singleton;
    }



    public OWLOntology parseOntology( Resource resource ) {
        return parseOntology( new Resource[] { resource } );
    }

    public OWLOntology parseOntology( Resource[] resources ) {
        try {

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
            config.setMissingOntologyHeaderStrategy( OWLOntologyLoaderConfiguration.MissingOntologyHeaderStrategy.IMPORT_GRAPH );

            OWLOntology onto = null;
            for ( Resource res : resources ) {
                OWLOntologyDocumentSource source = new StreamDocumentSource( res.getInputStream() );
                onto = manager.loadOntologyFromOntologyDocument( source, config );
            }

            return onto;
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





    /**
     * Builds an ontology-driven model from a DL resource, using a kSession
     * @param res
     * @return
     */
    private OntoModel doBuildModel( String name,
                                  Resource[] res,
                                  OntoModel.Mode mode,
                                  List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens,
                                  List<ModelInferenceStrategy.InferenceTask> tasks ) {
        OWLOntology ontoDescr = DLFactoryImpl.getInstance().parseOntology( res );

        Map<ModelInferenceStrategy.InferenceTask, Resource> theory = new LinkedHashMap<ModelInferenceStrategy.InferenceTask, Resource>();

//        if ( tasks.contains( ModelInferenceStrategy.InferenceTask.COMMON ) ) {
            ClassPathResource common =  new ClassPathResource( "FALC_CommonVisitor.drl" );
            common.setResourceType( ResourceType.DRL );
            theory.put( ModelInferenceStrategy.InferenceTask.COMMON, common );
//        }

        if ( tasks.contains( ModelInferenceStrategy.InferenceTask.TABLEAU ) ) {
            ClassPathResource visitor =  new ClassPathResource( "FALC_TableauBuilderVisitor.drl" );
            visitor.setResourceType( ResourceType.DRL );

            String tableau = DLFactoryImpl.getInstance().buildTableauRules( ontoDescr,
                    new Resource[] { common, visitor });
            ByteArrayResource tableauRules = new ByteArrayResource( tableau.getBytes() );
            tableauRules.setResourceType( ResourceType.DRL );

            theory.put( ModelInferenceStrategy.InferenceTask.TABLEAU, tableauRules );
        }

        if ( tasks.contains( ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_BUILD_AND_PRUNE ) ) {
            ClassPathResource classBuilder = new ClassPathResource( "FALC_ModelLatticeFullVisitor.drl" );
            classBuilder.setResourceType( ResourceType.DRL );
            theory.put( ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_BUILD_AND_PRUNE, classBuilder );
        }

        if ( tasks.contains( ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_PRUNE ) ) {
            ClassPathResource classPruner = new ClassPathResource( "FALC_ModelLatticeSimplePrune.drl" );
            classPruner.setResourceType( ResourceType.DRL );
            theory.put( ModelInferenceStrategy.InferenceTask.CLASS_LATTICE_PRUNE, classPruner );
        }

        if ( tasks.contains( ModelInferenceStrategy.InferenceTask.PROPERTY_MATCH ) ) {
            ClassPathResource propertyBuilder = new ClassPathResource( "FALC_ModelPropertyVisitor.drl" );
            propertyBuilder.setResourceType( ResourceType.DRL );
            theory.put( ModelInferenceStrategy.InferenceTask.PROPERTY_MATCH, propertyBuilder );
        }



        return strategy.buildModel(name,
                ontoDescr,
                mode,
                theory,
                axiomGens);

    }





    public OntoModel buildModel( String name, Resource res, OntoModel.Mode mode ) {
        return buildModel( name,
                new Resource[] { res },
                mode,
                defaultAxiomGenerators );
    }

    public OntoModel buildModel( String name, Resource[] res, OntoModel.Mode mode ) {
        return buildModel( name,
                res,
                mode,
                defaultAxiomGenerators );
    }

    public OntoModel buildModel( String name, Resource res, OntoModel.Mode mode, List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens ) {
        return buildModel( name,
                new Resource[] { res },
                mode,
                axiomGens );
    }

    public OntoModel buildModel( String name, Resource[] res, OntoModel.Mode mode, List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens ) {
        return buildModel( name,
                res,
                mode,
                axiomGens,
                defaultInferenceTasks );
    }

    public OntoModel buildModel( String name, Resource res, OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens, List<ModelInferenceStrategy.InferenceTask> tasks  ) {
        return buildModel( name,
                new Resource[] { res },
                mode,
                axiomGens,
                tasks );
    }

    public OntoModel buildModel( String name, Resource[] res, OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens, List<ModelInferenceStrategy.InferenceTask> tasks  ) {
        return doBuildModel( name,
                res,
                mode,
                axiomGens,
                tasks );
    }







}
