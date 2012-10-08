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
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScorecardTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_1/test_scorecard.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_1.test";



    @Test
    public void testScorecard() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );
        StatefulKnowledgeSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        kSession.getWorkingMemoryEntryPoint( "in_Age" ).insert( 33.0 );
        kSession.getWorkingMemoryEntryPoint( "in_Occupation" ).insert( "SKYDIVER" );
        kSession.getWorkingMemoryEntryPoint( "in_ResidenceState" ).insert( "KN" );
        kSession.getWorkingMemoryEntryPoint( "in_ValidLicense" ).insert( true );

        kSession.fireAllRules();  //init model

        System.err.println( reportWMObjects( kSession ) );
    }




}
