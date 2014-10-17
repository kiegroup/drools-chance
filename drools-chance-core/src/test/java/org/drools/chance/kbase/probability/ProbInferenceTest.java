package org.drools.chance.kbase.probability;

import org.drools.chance.Chance;
import org.drools.chance.kbase.AbstractChanceTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ProbInferenceTest extends AbstractChanceTest {



    @BeforeClass
    public static void setFactories() {
        Chance.initialize();
    }


    @Test
    public void testBayes() {

        KieSession kSession = initBasicChanceTest( "org/drools/chance/probability/bayesTheorem.drl" );
        Map res = (Map) kSession.getGlobal( MAP );

    }

}
