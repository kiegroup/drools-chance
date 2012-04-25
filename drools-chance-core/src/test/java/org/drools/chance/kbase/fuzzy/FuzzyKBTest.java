package org.drools.chance.kbase.fuzzy;

import org.drools.chance.Chance;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.rule.constraint.core.connectives.factories.fuzzy.linguistic.FuzzyConnectiveFactory;
import org.drools.chance.kbase.AbstractChanceTest;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

    @Before
    public void setUp() throws Exception {
        TraitFactory.reset();
    }

    @Test
    public void testFuzzyIs() {

        StatefulKnowledgeSession kSession = initBasicChanceTest( "org/drools/chance/fuzzy/testFuzzyIs.drl" );
        Map map = (Map) kSession.getGlobal( MAP );

        assertEquals( 2, map.size() );
        assertTrue( map.containsKey( "X" ) );
        assertTrue( map.containsKey( "Y" ) );

        assertEquals( 0.65,  (Double) map.get( "X" ), 1e-6 );
        assertEquals( 0.65,  (Double) map.get( "Y" ), 1e-6 );

    }


    @Test
    public void testFuzzyFacts() {
        StatefulKnowledgeSession kSession = initBasicChanceTest( "org/drools/chance/fuzzy/testFuzzyFacts.drl" );
        Map map = (Map) kSession.getGlobal( MAP );


        assertEquals( 2, map.size() );
        assertTrue( map.containsKey( "X" ) );
        assertTrue( map.containsKey( "Y" ) );

        assertEquals( 0.15,  (Double) map.get( "X" ), 1e-6 );
        assertEquals( 0.5,  (Double) map.get( "Y" ), 1e-6 );

    }


    @Test
    public void testFuzzyPattern() {
        StatefulKnowledgeSession kSession = initBasicChanceTest( "org/drools/chance/fuzzy/testFuzzyPattern.drl" );
        Map map = (Map) kSession.getGlobal( MAP );



        assertEquals( 3, map.size() );
        assertTrue( map.containsKey( "X" ) );
        assertTrue( map.containsKey( "Y" ) );
        assertTrue( map.containsKey( "Z" ) );

        assertEquals( 0.5,  (Double) map.get( "X" ), 1e-6 );
        assertEquals( 0.5,  (Double) map.get( "Y" ), 1e-6 );
        assertEquals( 0.35, (Double) map.get( "Z" ), 1e-6 );

    }
}
