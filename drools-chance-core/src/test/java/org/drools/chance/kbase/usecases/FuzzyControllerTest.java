package org.drools.chance.kbase.usecases;

import org.drools.chance.Chance;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.rule.constraint.core.connectives.factories.fuzzy.linguistic.FuzzyConnectiveFactory;
import org.drools.chance.kbase.AbstractChanceTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.util.Map;

import static junit.framework.Assert.*;

public class FuzzyControllerTest extends AbstractChanceTest {

    @BeforeClass
    public static void setFactories() {
        Chance.initialize();
        ChanceStrategyFactory.setDefaultFactory( new FuzzyConnectiveFactory() );
    }

    @Test
    @Ignore
    public void testFuzzyExample1() throws InterruptedException {

        KieSession kSession = initBasicChanceTest( "org/drools/chance/fuzzy/basicFuzzyControllerTest.drl" );
        Map map = (Map) kSession.getGlobal( MAP );

        Thread.sleep( 5000 );

        System.err.println( map );
        assertEquals( 3, map.size() );

        for ( Object o : map.keySet() ) {
            assertTrue( o instanceof Double );
            long k = Math.round( (Double) o );
            assertTrue( k == 230 || k == 279 || k == 315 );

        }

    }
}
