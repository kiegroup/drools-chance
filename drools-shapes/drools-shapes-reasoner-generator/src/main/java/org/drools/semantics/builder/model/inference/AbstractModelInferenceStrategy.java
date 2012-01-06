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
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.io.Resource;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Arrays;
import java.util.Map;

public abstract class AbstractModelInferenceStrategy implements ModelInferenceStrategy {



    public OntoModel buildModel( String name, OWLOntology ontoDescr,
                                          Map<InferenceTask, Resource> theory,
                                          StatefulKnowledgeSession kSession ) {

        addResource( kSession, theory.get( InferenceTask.COMMON ) );

        OntoModel baseModel = ModelFactory.newModel( name, ModelFactory.CompileTarget.BASE );
        kSession.fireAllRules();

        kSession.insert( ontoDescr );
        kSession.fireAllRules();

        OntoModel latticeModel = buildClassLattice( ontoDescr, kSession, theory, baseModel );

        latticeModel.sort();

        OntoModel propertyModel = buildProperties( ontoDescr, kSession, theory, latticeModel );

        propertyModel.sort();

        reportSessionStatus( kSession );

        return propertyModel;
    }


    protected abstract OntoModel buildProperties(OWLOntology ontoDescr, StatefulKnowledgeSession kSession, Map<InferenceTask, Resource> theory, OntoModel hierachicalModel);


    protected abstract OntoModel buildClassLattice(OWLOntology ontoDescr, StatefulKnowledgeSession kSession, Map<InferenceTask, Resource> theory, OntoModel baseModel);


    protected abstract void initReasoner( StatefulKnowledgeSession kSession, OWLOntology ontoDescr );






    protected void addResource( StatefulKnowledgeSession kSession, Resource res ) {
        KnowledgeBase kbase = kSession.getKnowledgeBase();

        ChangeSetImpl cs = new ChangeSetImpl();
            cs.setResourcesAdded( Arrays.asList( res ) );
        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
            kaConfig.setProperty("drools.agent.newInstance", "false");
        KnowledgeAgent kAgent = KnowledgeAgentFactory.newKnowledgeAgent(" adder ", kbase, kaConfig );
        kAgent.applyChangeSet(cs);
    }

    private void reportSessionStatus(StatefulKnowledgeSession kSession) {

        System.err.println( "----------------------- WM " + kSession.getObjects().size() + " --------------------------");
        for ( Object o : kSession.getObjects() ) {
            System.err.println("\t" + o );
        }
        System.err.println( "----------------------------------------------------------------");

    }


}
