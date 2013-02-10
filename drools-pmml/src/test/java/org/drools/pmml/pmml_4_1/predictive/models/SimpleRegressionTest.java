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

package org.drools.pmml.pmml_4_1.predictive.models;


import org.drools.definition.type.FactType;
import org.drools.pmml.pmml_4_1.DroolsAbstractPMMLTest;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SimpleRegressionTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_1/test_regression.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_1/test_regression_clax.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_1.test";



    @After
    public void tearDown() {
        getKSession().dispose();
    }

    @Test
    public void testRegression() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );
        StatefulKnowledgeSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKnowledgeBase().getFactType( packageName, "Fld4" );

        kSession.getWorkingMemoryEntryPoint( "in_Fld1" ).insert( 0.9 );
        kSession.getWorkingMemoryEntryPoint( "in_Fld2" ).insert( 0.3 );
        kSession.getWorkingMemoryEntryPoint( "in_Fld3" ).insert( "x" );
        kSession.fireAllRules();

        double x = 0.5
                   + 5 * 0.9 * 0.9
                   + 2 * 0.3
                   - 3.0
                   + 0.4 * 0.9 * 0.3;
        x = 1.0 / ( 1.0 + Math.exp( -x ) );
        
        checkFirstDataFieldOfTypeStatus( tgt, true, false, "LinReg", x );

        System.err.println( reportWMObjects( kSession ) );
    }



    @Test
    public void testClassification() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );
        StatefulKnowledgeSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKnowledgeBase().getFactType( packageName, "Fld4" );

        kSession.getWorkingMemoryEntryPoint( "in_Fld1" ).insert( 1.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Fld2" ).insert( 1.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Fld3" ).insert( "x" );
        kSession.fireAllRules();

        System.err.println( reportWMObjects( kSession ) );

        checkFirstDataFieldOfTypeStatus( kSession.getKnowledgeBase().getFactType( packageName, "RegOut" ),
                                            true, false, "LinReg", "catC" );
        checkFirstDataFieldOfTypeStatus( kSession.getKnowledgeBase().getFactType( packageName, "RegProb" ),
                                            true, false, "LinReg", 0.709228 );
        checkFirstDataFieldOfTypeStatus( kSession.getKnowledgeBase().getFactType( packageName, "RegProbA" ),
                                            true, false, "LinReg", 0.010635 );


    }




}
