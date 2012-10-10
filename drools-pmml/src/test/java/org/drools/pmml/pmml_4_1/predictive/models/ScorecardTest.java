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

import java.util.Iterator;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

        FactType scoreCardType = getKbase().getFactType( "org.drools.scorecards.example", "ScoreCard" );
        assertNotNull( scoreCardType );

        assertEquals( 1, kSession.getObjects( new ClassObjectFilter( scoreCardType.getFactClass() ) ).size() );
        Object scoreCard = kSession.getObjects( new ClassObjectFilter( scoreCardType.getFactClass() ) ).iterator().next();

        assertEquals( "SampleScore", scoreCardType.get( scoreCard, "modelName" ) );
        assertEquals( 41.345, scoreCardType.get( scoreCard, "score" ) );

        Object x = scoreCardType.get( scoreCard, "ranking" );
        assertTrue( x instanceof LinkedHashMap );
        LinkedHashMap map = (LinkedHashMap) x;
        assertTrue( map.containsKey( "LX00") );
        assertTrue( map.containsKey( "RES") );
        assertTrue( map.containsKey( "CX2" ) );
        assertEquals( -1.0, map.get( "LX00" ) );
        assertEquals( -10.0, map.get( "RES" ) );
        assertEquals( -30.0, map.get( "CX2" ) );

        Iterator iter = map.keySet().iterator();
        assertEquals( "LX00", iter.next() );
        assertEquals( "RES", iter.next() );
        assertEquals( "CX2", iter.next() );

    }




}
