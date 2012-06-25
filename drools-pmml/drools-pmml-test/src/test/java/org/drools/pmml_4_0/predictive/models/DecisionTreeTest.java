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


import org.drools.definition.type.FactType;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.StringTokenizer;

import static org.junit.Assert.assertNotNull;

public class DecisionTreeTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml_4_0/test_tree_simple.xml";
    private static final String source2 = "org/drools/pmml_4_0/test_tree_missing.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";




    @Test
    public void testSimpleTree() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );
        StatefulKnowledgeSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKnowledgeBase().getFactType( packageName, "Fld5" );
        
        kSession.getWorkingMemoryEntryPoint( "in_Fld1" ).insert( 30.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Fld2" ).insert( 60.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Fld3" ).insert( "false" );
        kSession.getWorkingMemoryEntryPoint( "in_Fld4" ).insert( "optA" );

        kSession.fireAllRules();

        checkFirstDataFieldOfTypeStatus( tgt, true, false, "TreeTest", "tgtZ" );

        System.err.println( reportWMObjects( kSession ) );
    }



    @Test
    public void testMissingTree() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );
        StatefulKnowledgeSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKnowledgeBase().getFactType( packageName, "Fld5" );

        kSession.getWorkingMemoryEntryPoint( "in_Fld1" ).insert( 45.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Fld2" ).insert( 60.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Fld3" ).insert( "optA" );

        kSession.fireAllRules();



        System.err.println( reportWMObjects( kSession ) );
    }



}
