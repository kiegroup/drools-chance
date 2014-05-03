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

import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.inference.DelegateInferenceStrategy;
import org.drools.semantics.builder.model.inference.ModelInferenceStrategy;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DLFactoryImpl implements DLFactory {


    private static DLFactoryImpl singleton;





    private DLFactoryImpl() {

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



    /**
     * Builds an ontology-driven model from a DL resource, using a kSession
     *
     * @param res
     * @param classLoader
     * @return
     */
    private OntoModel doBuildModel( String name,
                                    Resource[] res,
                                    DLFactoryConfiguration conf,
                                    ClassLoader classLoader ) {
        OWLOntology ontoDescr = parseOntology( res );

        Map<ModelInferenceStrategy.InferenceTask, Resource> theory = new LinkedHashMap<ModelInferenceStrategy.InferenceTask, Resource>();

//        if ( tasks.contains( ModelInferenceStrategy.InferenceTask.COMMON ) ) {
            ClassPathResource common =  new ClassPathResource( "FALC_CommonVisitor.drl" );
            common.setResourceType( ResourceType.DRL );
            theory.put( ModelInferenceStrategy.InferenceTask.COMMON, common );
//        }

        if ( conf.getTasks().contains( ModelInferenceStrategy.InferenceTask.TABLEAU ) ) {
            ClassPathResource visitor =  new ClassPathResource( "FALC_TableauBuilderVisitor.drl" );
            visitor.setResourceType( ResourceType.DRL );

            String tableau = DLReasonerBuilderFactory.getBuilder().buildTableauRules( ontoDescr,
                    new Resource[] { common, visitor });
            ByteArrayResource tableauRules = new ByteArrayResource( tableau.getBytes() );
            tableauRules.setResourceType( ResourceType.DRL );

            theory.put( ModelInferenceStrategy.InferenceTask.TABLEAU, tableauRules );
        }

        return new DelegateInferenceStrategy().buildModel( name,
                ontoDescr,
                conf,
                theory,
                classLoader );

    }




    public OntoModel buildModel( String name, Resource res, DLFactoryConfiguration conf ) {
        return buildModel( name, new Resource[] { res }, conf, null );
    }

    public OntoModel buildModel( String name, Resource res, DLFactoryConfiguration conf, ClassLoader classLoader ) {
        return buildModel( name, new Resource[] { res }, conf, classLoader );
    }

    public OntoModel buildModel( String name, Resource[] res, DLFactoryConfiguration conf ) {
        return buildModel( name, res, conf, null );
    }

    public OntoModel buildModel( String name, Resource[] res, DLFactoryConfiguration conf, ClassLoader classLoader ) {
        return doBuildModel( name,
                res,
                conf,
                classLoader );
    }







}
