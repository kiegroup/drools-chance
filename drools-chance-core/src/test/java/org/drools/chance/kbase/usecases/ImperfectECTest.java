package org.drools.chance.kbase.usecases;

import org.drools.chance.Chance;
import org.drools.chance.kbase.AbstractChanceTest;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.*;

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
        String[] drls = new String[] { "org/drools/chance/ec/EC_Lite.drl", "org/drools/chance/ec/testEC.drl" };
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
}
