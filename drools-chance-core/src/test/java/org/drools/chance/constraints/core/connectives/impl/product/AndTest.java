package org.drools.chance.constraints.core.connectives.impl.product;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.drools.chance.constraints.core.connectives.IConnectiveCore;
import org.drools.chance.constraints.core.connectives.impl.LOGICCONNECTIVES;
import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;


public class AndTest extends TestCase {


    IConnectiveCore and = org.drools.chance.constraints.core.connectives.impl.product.And.getInstance();

    public AndTest(String name) {
        super(name);
    }


    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testGetInstance() throws Exception {
        And and2 = org.drools.chance.constraints.core.connectives.impl.product.And.getInstance();

        assertNotNull(and2);
        assertEquals(and,and2);
    }

    public void testType() throws Exception {
        assertEquals(LOGICCONNECTIVES.AND,and.getType());
    }



    public void testEvalBinarySimple() throws Exception {
        IDegree s1 = new SimpleDegree(0.8);
        IDegree s2 = new SimpleDegree(0.6);

        IDegree s0 = and.eval(s1, s2);

        assertNotNull(s0);
        assertTrue(s0 instanceof SimpleDegree);
        assertEquals(new SimpleDegree(0.48),s0);
    }


    public void testEvalBinaryInterval() throws Exception {
        IDegree s1 = new IntervalDegree(0.6,0.7);
        IDegree s2 = new IntervalDegree(0.4, 0.5);

        IDegree s0 = and.eval(s1,s2);

        assertNotNull(s0);
        assertTrue(s0 instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.24,0.35),s0);
    }



    public void testEvalUnary() throws Exception {
        IDegree s1 = new SimpleDegree(0.3);
        IDegree s0 = and.eval(s1);

        assertNotNull(s0);
        assertEquals(s0.getClass(),s1.getClass());
        assertEquals(s0,s1);
    }



    public void testEvalNarySimple() throws Exception {
        IDegree s1 = new SimpleDegree(0.9);
        IDegree s2 = new SimpleDegree(0.9);
        IDegree s3 = new SimpleDegree(0.9);

        IDegree s0 = and.eval(s1, s2, s3);

        assertNotNull(s0);
        assertTrue(s0 instanceof SimpleDegree);
        assertEquals(new SimpleDegree(0.729),s0);
    }




    public void testEvalNaryInterval() throws Exception {
        IDegree s1 = new IntervalDegree(0.9,1.0);
        IDegree s2 = new IntervalDegree(0.8, 0.9);
        IDegree s3 = new IntervalDegree(0.8, 0.8);

        IDegree s0 = and.eval(s1,s2,s3);

        assertNotNull(s0);
        assertTrue(s0 instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.576,0.72),s0);
    }


    public void testNeutralInterval() throws Exception {
        IDegree s1 = new IntervalDegree(0.3,0.7);
        IDegree s2 = IntervalDegree.TRUE;

        IDegree s0 = and.eval(s1,s2);

        assertNotNull(s0);
        assertTrue(s0 instanceof IntervalDegree);
        assertEquals(s0,s1);
    }

    public void testNeutralSimple() throws Exception {
        IDegree s1 = new SimpleDegree(0.4);
        IDegree s2 = SimpleDegree.TRUE;

        IDegree s0 = and.eval(s1,s2);

        assertNotNull(s0);
        assertTrue(s0 instanceof SimpleDegree);
        assertEquals(s0,s1);
    }



    public void testIsUnary() throws Exception {
        assertEquals(false,and.isUnary());
    }


    public void testIsBinary() throws Exception {
        assertEquals(true,and.isBinary());
    }


    public void testIsNary() throws Exception {
        assertEquals(true,and.isNary());
    }



    public static Test suite() {
        return new TestSuite(AndTest.class);
    }
}
