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


import org.drools.io.Resource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.inference.ModelInferenceStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;

import java.util.List;
import java.util.Map;

public interface DLFactory {


    public OWLOntology parseOntology( Resource resource );


    public OntoModel buildModel( String name,
                                 Resource res,
                                 OntoModel.Mode mode );

    public OntoModel buildModel( String name,
                                 Resource res,
                                 OntoModel.Mode mode,
                                 ClassLoader classLoader );


    public OntoModel buildModel( String name,
                                 Resource[] res,
                                 OntoModel.Mode mode );

    public OntoModel buildModel( String name,
                                 Resource[] res,
                                 OntoModel.Mode mode,
                                 ClassLoader lodaer );


    public OntoModel buildModel( String name,
                                 Resource res,
                                 OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens );

    public OntoModel buildModel( String name,
                                 Resource res,
                                 OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens,
                                 ClassLoader classLoader );


    public OntoModel buildModel( String name,
                                 Resource[] res,
                                 OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens );

    public OntoModel buildModel( String name,
                                 Resource[] res,
                                 OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens,
                                 ClassLoader classLoader );


    public OntoModel buildModel( String name,
                                 Resource res,
                                 OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens,
                                 List<ModelInferenceStrategy.InferenceTask> tasks );

    public OntoModel buildModel( String name,
                                 Resource res,
                                 OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens,
                                 List<ModelInferenceStrategy.InferenceTask> tasks,
                                 ClassLoader classLoader );


    public OntoModel buildModel( String name,
                                 Resource[] res,
                                 OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens,
                                 List<ModelInferenceStrategy.InferenceTask> tasks );

   public OntoModel buildModel( String name,
                                 Resource[] res,
                                 OntoModel.Mode mode,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGens,
                                 List<ModelInferenceStrategy.InferenceTask> tasks,
                                 ClassLoader loader );


}
