package org.drools.chance.kbase.probability;

import org.drools.chance.Chance;
import org.drools.chance.kbase.AbstractChanceTest;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ProbInferenceTest extends AbstractChanceTest {



    @BeforeClass
    public static void setFactories() {
        Chance.initialize();
    }

    @Before
    public void setUp() throws Exception {
        TraitFactory.reset();
    }


    @Test
    public void testBayes() {

        StatefulKnowledgeSession kSession = initBasicChanceTest( "org/drools/chance/probability/bayesTheorem.drl" );
        Map res = (Map) kSession.getGlobal( MAP );

    }

}
