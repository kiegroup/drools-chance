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

package org.drools.chance.constraints.core.connectives.impl.godel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.drools.chance.constraints.core.connectives.IConnectiveCore;
import org.drools.chance.constraints.core.connectives.impl.LOGICCONNECTIVES;
import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;


public class AndTest extends TestCase {


    IConnectiveCore and = org.drools.chance.constraints.core.connectives.impl.godel.And.getInstance();

    public AndTest(String name) {
        super(name);
    }


    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testType() throws Exception {
        assertEquals(LOGICCONNECTIVES.AND,and.getType());
    }

    public void testGetInstance() throws Exception {
        And and2=org.drools.chance.constraints.core.connectives.impl.godel.And.getInstance();

        assertNotNull(and2);
        assertEquals(and,and2);
    }



    public void testEvalBinarySimple() throws Exception {
        IDegree s1 = new SimpleDegree(0.2);
        IDegree s2 = new SimpleDegree(0.6);

        IDegree s0 = and.eval(s1, s2);

        assertNotNull(s0);
        assertTrue(s0 instanceof SimpleDegree);
        assertEquals(new SimpleDegree(0.2),s0);
    }


    public void testEvalBinaryInterval() throws Exception {
        IDegree s1 = new IntervalDegree(0.3,0.7);
        IDegree s2 = new IntervalDegree(0.45, 0.5);

        IDegree s0 = and.eval(s1,s2);

        assertNotNull(s0);
        assertTrue(s0 instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.3,0.5),s0);
    }



    public void testEvalUnary() throws Exception {
        IDegree s1 = new SimpleDegree(0.3);
        IDegree s0 = and.eval(s1);

        assertNotNull(s0);
        assertEquals(s0.getClass(),s1.getClass());
        assertEquals(s0,s1);
    }



    public void testEvalNarySimple() throws Exception {
        IDegree s1 = new SimpleDegree(0.2);
        IDegree s2 = new SimpleDegree(0.6);
        IDegree s3 = new SimpleDegree(0.1);

        IDegree s0 = and.eval(s1, s2, s3);

        assertNotNull(s0);
        assertTrue(s0 instanceof SimpleDegree);
        assertEquals(new SimpleDegree(0.1),s0);
    }




    public void testEvalNaryInterval() throws Exception {
        IDegree s1 = new IntervalDegree(0.3,0.7);
        IDegree s2 = new IntervalDegree(0.45, 0.5);
        IDegree s3 = new IntervalDegree(0.1, 0.9);

        IDegree s0 = and.eval(s1,s2,s3);

        assertNotNull(s0);
        assertTrue(s0 instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.1,0.5),s0);
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
        assertFalse(and.isUnary());
    }


    public void testIsBinary() throws Exception {
        assertTrue(and.isBinary());
    }


    public void testIsNary() throws Exception {
        assertTrue(and.isNary());
    }



    public static Test suite() {
        return new TestSuite(AndTest.class);
    }
}
