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

package org.drools.pmml_4_0.predictive.models;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.InputStreamResource;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.drools.pmml_4_0.descr.SupportVectorMachine;
import org.drools.pmml_4_0.descr.SupportVectorMachineModel;
import org.drools.pmml_4_0.descr.VectorDictionary;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;

public class SVMTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml_4_0/test_svm.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Test
    //@Ignore
    public void testSVM() throws Exception {
        setKSession(getModelSession(source1,false));
        setKbase(getKSession().getKnowledgeBase());

//        getKSession().fireAllRules();  //init model

//        InputStream is = new FileInputStream("C:\\Users\\Stefano\\Desktop\\Progetto AI\\DRL.drl");
//
       KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//       kBuilder.add( new InputStreamResource( is ), ResourceType.DRL );
//
//        if ( kBuilder.hasErrors() ) {
//            System.err.println( kBuilder.getErrors().toString() );
//            throw new Exception("NO!");
//        }
//
//        is.close();

//        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
//        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );
        StatefulKnowledgeSession kSession = getKSession();
        kSession.fireAllRules();  //init model




        kSession.getWorkingMemoryEntryPoint( "in_X" ).insert(0.23);
        kSession.getWorkingMemoryEntryPoint( "in_Y" ).insert(0.75);
        kSession.fireAllRules();


        kSession.getWorkingMemoryEntryPoint( "in_X" ).insert(0.85);
        kSession.fireAllRules();
        System.err.println( reportWMObjects(kSession)  );

        kSession.getWorkingMemoryEntryPoint( "in_Y" ).insert(-0.12);
        kSession.fireAllRules();

        kSession.getWorkingMemoryEntryPoint( "in_X" ).insert(7.85);
        kSession.fireAllRules();


    }




}
