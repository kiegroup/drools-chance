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

import org.drools.io.Resource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.model.OntoModel;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Map;

public class InternalInferenceStrategy extends AbstractModelInferenceStrategy {


    protected OntoModel buildProperties(OWLOntology ontoDescr, StatefulKnowledgeSession kSession, Map<InferenceTask, Resource> theory, OntoModel latticeModel) {
        addResource( kSession, theory.get( InferenceTask.PROPERTY_MATCH ) );
        kSession.setGlobal("propertyModel", latticeModel);
        kSession.fireAllRules();
        return (OntoModel) kSession.getGlobal("propertyModel");
    }

    protected OntoModel buildClassLattice(OWLOntology ontoDescr, StatefulKnowledgeSession kSession, Map<InferenceTask, Resource> theory, OntoModel baseModel) {
        addResource( kSession, theory.get( InferenceTask.TABLEAU ) );
        initReasoner( kSession, ontoDescr );
        kSession.fireAllRules();

        addResource(kSession, theory.get(InferenceTask.CLASS_LATTICE_BUILD_AND_PRUNE));
        kSession.setGlobal("latticeModel", baseModel);
        kSession.fireAllRules();
        return (OntoModel) kSession.getGlobal("latticeModel");
    }

    protected void initReasoner( StatefulKnowledgeSession kSession, OWLOntology ontoDescr ) {

    }
}