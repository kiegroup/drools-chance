package org.drools.chance.kbase.probability;

import org.drools.chance.Chance;
import org.drools.chance.degree.Degree;
import org.drools.chance.kbase.AbstractChanceTest;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class GaussianTest extends AbstractChanceTest {



    @BeforeClass
    public static void setFactories() {
        Chance.initialize();
    }


    @Test
    public void testGauss() {

        StatefulKnowledgeSession kSession = initBasicChanceTest( "org/drools/chance/probability/testGaussian.drl" );
        Map res = (Map) kSession.getGlobal( MAP );

        Degree deg = (Degree) res.get( "x" );
        assertEquals( 0.6826, deg.getValue(), 1e-4 );

    }

}
