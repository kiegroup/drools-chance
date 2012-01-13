/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.chance.constraints.core.connectives.impl.product;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.drools.chance.constraints.core.connectives.IConnectiveCore;
import org.drools.chance.constraints.core.connectives.impl.LOGICCONNECTIVES;
import org.drools.chance.degree.Degree;
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
        Degree s1 = new SimpleDegree(0.8);
        Degree s2 = new SimpleDegree(0.6);

        Degree s0 = and.eval(s1, s2);

        assertNotNull(s0);
        assertTrue(s0 instanceof SimpleDegree);
        assertEquals(new SimpleDegree(0.48),s0);
    }


    public void testEvalBinaryInterval() throws Exception {
        Degree s1 = new IntervalDegree(0.6,0.7);
        Degree s2 = new IntervalDegree(0.4, 0.5);

        Degree s0 = and.eval(s1,s2);

        assertNotNull(s0);
        assertTrue(s0 instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.24,0.35),s0);
    }



    public void testEvalUnary() throws Exception {
        Degree s1 = new SimpleDegree(0.3);
        Degree s0 = and.eval(s1);

        assertNotNull(s0);
        assertEquals(s0.getClass(),s1.getClass());
        assertEquals(s0,s1);
    }



    public void testEvalNarySimple() throws Exception {
        Degree s1 = new SimpleDegree(0.9);
        Degree s2 = new SimpleDegree(0.9);
        Degree s3 = new SimpleDegree(0.9);

        Degree s0 = and.eval(s1, s2, s3);

        assertNotNull(s0);
        assertTrue(s0 instanceof SimpleDegree);
        assertEquals(new SimpleDegree(0.729),s0);
    }




    public void testEvalNaryInterval() throws Exception {
        Degree s1 = new IntervalDegree(0.9,1.0);
        Degree s2 = new IntervalDegree(0.8, 0.9);
        Degree s3 = new IntervalDegree(0.8, 0.8);

        Degree s0 = and.eval(s1,s2,s3);

        assertNotNull(s0);
        assertTrue(s0 instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.576,0.72),s0);
    }


    public void testNeutralInterval() throws Exception {
        Degree s1 = new IntervalDegree(0.3,0.7);
        Degree s2 = IntervalDegree.TRUE;

        Degree s0 = and.eval(s1,s2);

        assertNotNull(s0);
        assertTrue(s0 instanceof IntervalDegree);
        assertEquals(s0,s1);
    }

    public void testNeutralSimple() throws Exception {
        Degree s1 = new SimpleDegree(0.4);
        Degree s2 = SimpleDegree.TRUE;

        Degree s0 = and.eval(s1,s2);

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
