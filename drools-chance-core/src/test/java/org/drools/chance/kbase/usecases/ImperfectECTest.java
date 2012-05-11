package org.drools.chance.kbase.usecases;

import org.drools.chance.Chance;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.kbase.AbstractChanceTest;
import org.drools.chance.rule.constraint.core.connectives.factories.fuzzy.linguistic.FuzzyConnectiveFactory;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.time.SessionPseudoClock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

public class ImperfectECTest extends AbstractChanceTest {


    @BeforeClass
    public static void setFactories() {
        Chance.initialize();
    }

    @Before
    public void setUp() throws Exception {
        TraitFactory.reset();
    }


    @Test
    public void testEC() throws InterruptedException {
        String[] drls = new String[] {  "org/drools/chance/ec/testEC.drl" };
        StatefulKnowledgeSession kSession = initBasicChanceTest( drls );
        Map map = (Map) kSession.getGlobal( MAP );

        kSession.fireAllRules();

        kSession.getWorkingMemoryEntryPoint( "hour" ).insert(20.5);
        kSession.fireAllRules();

        kSession.getWorkingMemoryEntryPoint( "hour" ).insert(21.0);
        kSession.fireAllRules();

        kSession.insert( "check" );
        kSession.fireAllRules();



        assertEquals(1, map.size());
        assertEquals( 0.75 , (Double) map.get( "EC" ), 1e-6 );
    }



    @Test
    public void testEC2() throws InterruptedException {
        Random rand = new Random();
        rand.setSeed(123456);

        ChanceStrategyFactory.setDefaultFactory( new FuzzyConnectiveFactory() );
        
        
        String[] drls = new String[] { "org/drools/chance/ec/EC_Fuzzy.drl", "org/drools/chance/ec/testEC2.drl" };
        StatefulKnowledgeSession kSession = initTimedChanceTest(drls, false);
        SessionPseudoClock clock = (SessionPseudoClock) kSession.getSessionClock();

        Map map = (Map) kSession.getGlobal( MAP );
        kSession.setGlobal( "clock", clock );

        kSession.fireAllRules();

        int T;                
        
        T = 20;
        for ( int j = 0; j < T; j++ ) {
            clock.advanceTime( 1, TimeUnit.SECONDS );
            System.out.println( "Time is now " + clock.getCurrentTime() );
            kSession.getWorkingMemoryEntryPoint( "handY" ).insert( 5.0*j + rand.nextGaussian() );
            kSession.fireAllRules();
        }
        
        T = 10;
        for ( int j = 0; j < T; j++ ) {
            clock.advanceTime(1, TimeUnit.SECONDS);
            System.out.println( "Time is now " + clock.getCurrentTime() );
            kSession.getWorkingMemoryEntryPoint( "handY" ).insert(100.0 + 10 * rand.nextGaussian());
            kSession.fireAllRules();
        }

        T = 12;
        for ( int j = 0; j < T; j++ ) {
            clock.advanceTime( 1, TimeUnit.SECONDS );
            System.out.println( "Time is now " + clock.getCurrentTime() );
            kSession.getWorkingMemoryEntryPoint( "handY" ).insert(100.0 - 5.0 * j + rand.nextGaussian());
            kSession.fireAllRules();
        }


        System.err.println( reportWMObjects( kSession ) );

        kSession.insert( "check" );
        kSession.fireAllRules();

    }









}
