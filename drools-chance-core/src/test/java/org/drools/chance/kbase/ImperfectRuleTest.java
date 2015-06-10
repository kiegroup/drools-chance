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

package org.drools.chance.kbase;

import org.drools.chance.Chance;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.util.*;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;


public class ImperfectRuleTest extends AbstractChanceTest {




    @BeforeClass
    public static void setFactories() {
        Chance.initialize();
    }

    @Test
    public void testSingleRestrictions() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testSingleRestrictions.drl");
        Map res = (Map) kSession.getGlobal( MAP );

        assertTrue( res.containsKey( "X" ) );
        assertTrue( res.containsKey( "Y" ) );
        assertTrue( res.containsKey( "W" ) );
        assertTrue( res.containsKey( "Z" ) );

        assertEquals( 1.0, (Double) res.get( "X" ), 1e-6 );
        assertEquals( 0.7, (Double) res.get( "Y" ), 1e-6 );
        assertEquals( 1.0, (Double) res.get( "W" ), 1e-6 );
        assertEquals( 1.0, (Double) res.get( "Z" ), 1e-6 );

    }


    @Test
    public void testSimpleOperatorsAlpha() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testSimpleOperatorsAlpha.drl");
        Map res = (Map) kSession.getGlobal( MAP );


        assertTrue( res.containsKey( "X" ) );
        assertTrue( res.containsKey( "Y" ) );
        assertTrue( res.containsKey( "Z" ) );

        assertEquals( 0.7, (Double) res.get( "X" ), 1e-6 );
        assertEquals( 0.8, (Double) res.get( "Y" ), 1e-6 );
        assertEquals( 0.3, (Double) res.get( "Z" ), 1e-6 );
    }

    @Test
    public void testVerySimpleOperatorsAlpha() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testVerySimpleOperatorsAlpha.drl");
        Map res = (Map) kSession.getGlobal( MAP );

        assertTrue( res.containsKey( "X" ) );

        assertEquals( 0.2, (Double) res.get( "X" ), 1e-6 );
    }

    @Test
    public void testMixedOperatorsAlpha() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testMixedOperatorsAlpha.drl");
        Map res = (Map) kSession.getGlobal( MAP );

        assertTrue( res.containsKey( "X" ) );

        assertEquals( 0.7, (Double) res.get( "X" ), 1e-6 );
    }



    @Test
    public void testCompositeRestrictions() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testCompositeRestrictions.drl");
        Map res = (Map) kSession.getGlobal( MAP );


        assertTrue( res.containsKey( "X" ) );
        assertTrue( res.containsKey( "Y" ) );
        assertTrue( res.containsKey( "Z" ) );

        assertEquals( 1.0 , (Double) res.get( "X" ), 1e-6 );
        assertEquals( 0.52, (Double) res.get( "Y" ), 1e-6 );
        assertEquals( 0.052, (Double) res.get( "Z" ), 1e-6 );
    }




    @Test
    public void testEvaluatorInRules() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/evaluator/testEvaluatorsInRules.drl");
        Map res = (Map) kSession.getGlobal( MAP );


        assertTrue( res.containsKey( "X" ) );
        assertTrue( res.containsKey( "Y" ) );
        assertTrue( res.containsKey( "Z" ) );
        assertTrue( res.containsKey( "W" ) );

        assertEquals( 0.65, (Double) res.get("X"), 1e-6 );
        assertEquals( 1.0,  (Double) res.get("Y"), 1e-6 );
        assertEquals( 1.0,  (Double) res.get("Z"), 1e-6 );
        assertEquals( 1.0,  (Double) res.get("W"), 1e-6 );


    }




    @Test
    public void testInlineRestrictions() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testInlineRestrictions.drl");
        Map res = (Map) kSession.getGlobal( MAP );

        assertTrue( res.containsKey( "X" ) );
        assertTrue( res.containsKey( "Y" ) );
        assertTrue( res.containsKey( "Z" ) );

        assertEquals( 1.0 , (Double) res.get( "X" ), 1e-6 );
        assertEquals( 1.0, (Double) res.get( "Y" ), 1e-6 );
        assertEquals( 0.568, (Double) res.get( "Z" ), 1e-6 );
    }



    @Test
    public void testSingleBindings() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testSingleBindings.drl");
        Map res = (Map) kSession.getGlobal( MAP );


        assertTrue( res.containsKey( "X" ) );
        assertTrue( res.containsKey( "Y" ) );

        assertEquals( 0.7 , (Double) res.get( "X" ), 1e-6 );
        assertEquals( 1.0 , (Double) res.get( "Y" ), 1e-6 );
    }


    @Test
    public void testMixedRestrictions() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testMixedRestrictions.drl");
        Map res = (Map) kSession.getGlobal( MAP );


        assertTrue( res.containsKey( "A" ) );
        assertFalse( res.containsKey( "B" ) );

        assertEquals( 1.0 , (Double) res.get( "A" ), 1e-6 );
    }



    @Test
    public void testVariableRestrictions() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testVariableRestrictions.drl");
        Map res = (Map) kSession.getGlobal( MAP );


        assertTrue( res.containsKey( "X" ) );

        assertEquals( 0.7    , (Double) res.get( "X" ), 1e-6 );
        assertEquals( 1.0    , (Double) res.get( "Y" ), 1e-6 );
        assertEquals( 0.5985 , (Double) res.get( "Z" ), 1e-6 );
    }



    @Test
    public void testBetaJoins() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testBetaJoins.drl");
        Map res = (Map) kSession.getGlobal( MAP );

        assertEquals( 6, res.size() );

        assertTrue( res.containsKey( "Xx1" ) );
        assertTrue( res.containsKey( "Xx2" ) );
        assertTrue( res.containsKey( "Yx1" ) );
        assertTrue( res.containsKey( "Yx2" ) );
        assertTrue( res.containsKey( "Zx1" ) );
        assertTrue( res.containsKey( "Zx2" ) );

        assertEquals( 0.6    , (Double) res.get( "Xx1" ), 1e-6 );
        assertEquals( 0.6    , (Double) res.get( "Xx2" ), 1e-6 );
        assertEquals( 1.0    , (Double) res.get( "Yx1" ), 1e-6 );
        assertEquals( 1.0    , (Double) res.get( "Yx2" ), 1e-6 );
        assertEquals( 1.0    , (Double) res.get( "Zx1" ), 1e-6 );
        assertEquals( 1.0    , (Double) res.get( "Zx2" ), 1e-6 );
    }


    @Test
    public void testBetaJoin() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testBetaJoin.drl");
        Map res = (Map) kSession.getGlobal( MAP );

        assertEquals( 2, res.size() );

        assertTrue( res.containsKey( "Xx1" ) );
        assertTrue( res.containsKey( "Xx2" ) );

        assertEquals( 0.6    , (Double) res.get( "Xx1" ), 1e-6 );
        assertEquals( 0.6    , (Double) res.get( "Xx2" ), 1e-6 );
    }




    @Test
    public void testComplexBetaJoins() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testComplexBetaJoins.drl");
        Map res = (Map) kSession.getGlobal( MAP );

        assertEquals( 4, res.size() );

        assertTrue( res.containsKey( "Xx1" ) );
        assertTrue( res.containsKey( "Xx2" ) );
        assertTrue( res.containsKey( "Yx1" ) );
        assertTrue( res.containsKey( "Yx2" ) );

        assertEquals( 0.7    , (Double) res.get( "Xx1" ), 1e-6 );
        assertEquals( 0.7    , (Double) res.get( "Xx2" ), 1e-6 );
        assertEquals( 0.6    , (Double) res.get( "Yx1" ), 1e-6 );
        assertEquals( 0.6    , (Double) res.get( "Yx2" ), 1e-6 );
    }




    @Test
    public void testAndOrGroupElements() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testAndOrGroupElements.drl");
        Map res = (Map) kSession.getGlobal( MAP );

        assertEquals( 4, res.size() );

        assertTrue( res.containsKey( "X" ) );
        assertTrue( res.containsKey( "Y" ) );
        assertTrue( res.containsKey( "W" ) );
        assertTrue( res.containsKey( "Z" ) );

        assertEquals( 0.189   , (Double) res.get( "X" ), 1e-6 );
        assertEquals( 0.63    , (Double) res.get( "Y" ), 1e-6 );
        assertEquals( 0.97    , (Double) res.get( "W" ), 1e-6 );
        assertEquals( 0.97    , (Double) res.get( "Z" ), 1e-6 );
    }




    @Test
    public void testEvaluationLabels() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testEvaluationLabels.drl");
        Map res = (Map) kSession.getGlobal( MAP );

        for ( Object key : res.keySet() ) {
            System.out.println( key + " >> " + res.get( key ) );
        }

        assertEquals( 0.55   , (Double) res.get( "X" ), 1e-6 );
        assertEquals( 0.95   , (Double) res.get( "betaJoin" ), 1e-6 );
        assertEquals( 0.7    , (Double) res.get( "patternAnd" ), 1e-6 );
        assertEquals( 0.55   , (Double) res.get( "outerAnd" ), 1e-6 );
        assertEquals( 0.85   , (Double) res.get( "innerAnd" ), 1e-6 );
        assertEquals( 1.0    , (Double) res.get( "Imperson" ), 1e-6 );
        assertEquals( 0.7    , (Double) res.get( "impIs" ), 1e-6 );
        assertEquals( 0.9    , (Double) res.get( "notFrank" ), 1e-6 );

        assertEquals( 8, res.size() );

    }


    @Test
    @Ignore
    public void testQueries() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testQueries.drl");
        Map res = (Map) kSession.getGlobal( MAP );


        System.err.println( res );
//
        //assertEquals( 1.0   , (Double) res.get( "Xx1" ), 1e-6 );
        assertEquals( 0.7   , (Double) res.get( "Yx1" ), 1e-6 );
        //assertEquals( 0.4   , (Double) res.get( "Zx1" ), 1e-6 );
//
        assertEquals( 3, res.size() );

    }


    @Test
    public void testPositional() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testPositional.drl");
        Map res = (Map) kSession.getGlobal( MAP );


        System.err.println( res );

        assertEquals( 1.0    , (Double) res.get( "X" ), 1e-6 );

    }

    @Test
    @Ignore
    public void testImperfectTrait() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/evaluator/testImperfectTrait.drl");
        Map map = (Map) kSession.getGlobal( MAP );


        assertEquals( 3, map.size() );
        assertTrue( map.containsKey( "X" ) );
        assertTrue( map.containsKey( "Y" ) );
        assertTrue( map.containsKey( "Z" ) );
        //
        assertEquals( 0.75,  (Double) map.get( "X" ), 1e-6 );
        assertEquals( 0.75,  (Double) map.get( "Y" ), 1e-6 );
        assertEquals( 0.375, (Double) map.get( "Z" ), 1e-6 );

    }


    @Test
    public void testFrom() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testFrom.drl");
        Map map = (Map) kSession.getGlobal( MAP );

        kSession.fireAllRules();

        System.out.println( map );

        assertEquals( 1, map.size() );
        assertTrue( map.containsKey( "X" ) );
        assertEquals( 0.052,  (Double) map.get( "X" ), 1e-6 );

    }


    @Test
    @Ignore
    public void testMVELMatchesOperator() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testMVELMatchesOperator.drl");
        Map res = (Map) kSession.getGlobal( MAP );


        assertTrue( res.containsKey( "X" ) );

        assertEquals( 1.0 , (Double) res.get( "X" ), 1e-6 );

    }



    @Test
    @Ignore( "Not implemented yet" )
    public void testExists() {
        KieSession kSession = initBasicChanceTest("org/drools/chance/testExists.drl");
        Map map = (Map) kSession.getGlobal( MAP );

        kSession.fireAllRules();

        kSession.insert( 0.4 );
        kSession.fireAllRules();

        kSession.insert( 0.3 );
        kSession.fireAllRules();

        kSession.insert( 0.5 );
        kSession.fireAllRules();

        kSession.insert( 0.2 );
        kSession.fireAllRules();

        kSession.insert( -0.5 );
        kSession.fireAllRules();

        kSession.insert( 0.9 );
        kSession.fireAllRules();

        System.out.println( map );

//        assertEquals( 1, map.size() );
//        assertTrue( map.containsKey( "X" ) );
//        assertEquals( 0.052,  (Double) map.get( "X" ), 1e-6 );

    }



    @Test
    public void testAlphaConnective() {
        KieSession kSession = initBasicChanceTest( "org/drools/chance/testAlphaConnectives.drl" );
        Map map = (Map) kSession.getGlobal( MAP );

        kSession.fireAllRules();

        System.out.println( map );

        assertEquals( 1, map.size() );
        assertTrue( map.containsKey( "X" ) );
        assertEquals( 0.6,  (Double) map.get( "X" ), 1e-6 );

    }

}
