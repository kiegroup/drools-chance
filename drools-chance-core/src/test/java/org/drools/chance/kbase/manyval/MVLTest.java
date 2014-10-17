package org.drools.chance.kbase.manyval;

import org.drools.chance.Chance;
import org.drools.chance.kbase.AbstractChanceTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class MVLTest extends AbstractChanceTest {



    @BeforeClass
    public static void setFactories() {
        Chance.initialize();
    }


    @Test
    public void testConnectives() {

        KieSession kSession = initBasicChanceTest( "org/drools/chance/manyval/testConnectives.drl" );
        Map res = (Map) kSession.getGlobal( MAP );

        assertTrue( res.containsKey( "X" ) );
        assertTrue( res.containsKey( "Y" ) );
        assertTrue( res.containsKey( "Z" ) );

        assertEquals( 0.65,  (Double) res.get("X"), 1e-6 );
        assertEquals( 0.35,  (Double) res.get("Y"), 1e-6 );
        assertEquals( 0.455, (Double) res.get("Z"), 1e-6 );


    }

}
