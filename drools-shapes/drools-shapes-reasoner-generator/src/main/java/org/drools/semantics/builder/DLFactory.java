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
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Map;

public interface DLFactory {


    public enum INFERENCE_STRATEGY { INTERNAL, EXTERNAL }
    public enum SupportedReasoners { HERMIT, PELLET }

    public void setInferenceStrategy( INFERENCE_STRATEGY strategy );
    public void setExternalReasoner( SupportedReasoners externalReasoner );




    public OWLOntology parseOntology( Resource resource );





    public String buildTableauRules( OWLOntology ontologyDescr, Resource[] visitor );






    public OntoModel buildModel( String name, OWLOntology ontoDescr, Map<ModelInferenceStrategy.InferenceTask, Resource> theory );

    public OntoModel buildModel( String name, Resource res, StatefulKnowledgeSession kSession );

    public OntoModel buildModel( String name, Resource res );


}
