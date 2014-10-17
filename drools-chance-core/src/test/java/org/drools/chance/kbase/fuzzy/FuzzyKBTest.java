package org.drools.chance.kbase.fuzzy;

import org.drools.chance.Chance;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.rule.constraint.core.connectives.factories.fuzzy.linguistic.FuzzyConnectiveFactory;
import org.drools.chance.kbase.AbstractChanceTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class FuzzyKBTest extends AbstractChanceTest {

    @BeforeClass
    public static void setFactories() {

        Chance.initialize();

        ChanceStrategyFactory.setDefaultFactory( new FuzzyConnectiveFactory() );

    }

    @Test
    public void testFuzzyIs() {

        KieSession kSession = initBasicChanceTest( "org/drools/chance/fuzzy/testFuzzyIs.drl" );
        Map map = (Map) kSession.getGlobal( MAP );

        assertEquals( 2, map.size() );
        assertTrue( map.containsKey( "X" ) );
        assertTrue( map.containsKey( "Y" ) );

        assertEquals( 0.65,  (Double) map.get( "X" ), 1e-6 );
        assertEquals( 0.65,  (Double) map.get( "Y" ), 1e-6 );

    }


    @Test
    public void testFuzzyFacts() {
        KieSession kSession = initBasicChanceTest( "org/drools/chance/fuzzy/testFuzzyFacts.drl" );
        Map map = (Map) kSession.getGlobal( MAP );


        assertEquals( 2, map.size() );
        assertTrue( map.containsKey( "X" ) );
        assertTrue( map.containsKey( "Y" ) );

        assertEquals( 0.15,  (Double) map.get( "X" ), 1e-6 );
        assertEquals( 0.5,  (Double) map.get( "Y" ), 1e-6 );

    }


    @Test
    public void testFuzzyPattern() {
        KieSession kSession = initBasicChanceTest( "org/drools/chance/fuzzy/testFuzzyPattern.drl" );
        Map map = (Map) kSession.getGlobal( MAP );



        assertEquals( 3, map.size() );
        assertTrue( map.containsKey( "X" ) );
        assertTrue( map.containsKey( "Y" ) );
        assertTrue( map.containsKey( "Z" ) );

        assertEquals( 0.5,  (Double) map.get( "X" ), 1e-6 );
        assertEquals( 0.5,  (Double) map.get( "Y" ), 1e-6 );
        assertEquals( 0.35, (Double) map.get( "Z" ), 1e-6 );

    }
    
    @Test
    public void testFuzzySpam() {
        KieSession kSession = initBasicChanceTest( "org/drools/chance/fuzzy/testFuzzySpam.drl" );
        kSession.insert( new SpamResponse() );
        kSession.fireAllRules();
        Map map = (Map) kSession.getGlobal( MAP );
    }
}
