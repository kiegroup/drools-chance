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

package org.drools.semantics.lang.dl;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.model.OntoModel;
import org.junit.Ignore;
import org.junit.Test;


/**
 * This is a sample class to launch a rule.
 */
@SuppressWarnings("restriction")
public class DL_5_KMR_ClassHierarchyTest  {

    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();
    

    @Test
    @Ignore // need to check and update after refactor
    public void testHierarchyFromClassesInternal() {
        String source = "DLex7.manchester";
        Resource res = ResourceFactory.newClassPathResource(source);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();

        factory.setInferenceStrategy( DLFactory.INFERENCE_STRATEGY.INTERNAL );
        OntoModel results = factory.buildModel( "ex7", res, kSession );
        System.out.println(results);
    }


    @Test
    public void testHierarchyFromClassesExternal() {
        String source = "DLex7.manchester";
        Resource res = ResourceFactory.newClassPathResource(source);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();

        factory.setInferenceStrategy( DLFactory.INFERENCE_STRATEGY.EXTERNAL );
        OntoModel results = factory.buildModel( "ex7", res, kSession );
        System.out.println(results);
    }



}