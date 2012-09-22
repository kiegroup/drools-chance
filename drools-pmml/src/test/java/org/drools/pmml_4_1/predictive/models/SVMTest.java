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

package org.drools.pmml_4_1.predictive.models;


import org.drools.definition.type.FactType;
import org.drools.pmml_4_1.DroolsAbstractPMMLTest;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SVMTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml_4_1/test_svm.xml";
    private static final String source2 = "org/drools/pmml_4_1/test_svm_1vN.xml";
    private static final String source3 = "org/drools/pmml_4_1/test_svm_1v1.xml";
    private static final String packageName = "org.drools.pmml_4_1.test";



    @Test
    public void testSVM() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );
        StatefulKnowledgeSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType ztype = kSession.getKnowledgeBase().getFactType( packageName, "Z" );
        assertNotNull( ztype );

        kSession.getWorkingMemoryEntryPoint( "in_X" ).insert( 0.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Y" ).insert( 0.0 );
        kSession.fireAllRules();
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "yes" );


        kSession.getWorkingMemoryEntryPoint( "in_X" ).insert( 0.23 );
        kSession.getWorkingMemoryEntryPoint( "in_Y" ).insert( 0.75 );
        kSession.fireAllRules();
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "no" );


        kSession.getWorkingMemoryEntryPoint( "in_X" ).insert( 0.85 );
        kSession.fireAllRules();
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "yes" );

        System.err.println( reportWMObjects(kSession)  );


        kSession.getWorkingMemoryEntryPoint( "in_Y" ).insert( -0.12 );
        kSession.fireAllRules();
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "no" );

        kSession.getWorkingMemoryEntryPoint( "in_X" ).insert( 7.85 );
        kSession.fireAllRules();
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "yes" );
    }



    @Test
    public void testSVM1vN() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );
        StatefulKnowledgeSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType ztype = kSession.getKnowledgeBase().getFactType( packageName, "Z" );
        assertNotNull( ztype );


        kSession.getWorkingMemoryEntryPoint( "in_X" ).insert( 0.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Y" ).insert( 0.0 );
        kSession.fireAllRules();

        System.err.println( reportWMObjects(kSession)  );

        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "no" );

    }

    @Test
    public void testSVM1v1() throws Exception {
        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );
        StatefulKnowledgeSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType ztype = kSession.getKnowledgeBase().getFactType( packageName, "Z" );
        assertNotNull( ztype );


        kSession.getWorkingMemoryEntryPoint( "in_X" ).insert( 0.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Y" ).insert( 0.0 );
        kSession.fireAllRules();

        System.err.println( reportWMObjects(kSession)  );

        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "yes" );


    }




}
