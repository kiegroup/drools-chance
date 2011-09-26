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

package org.drools.chance.degree.simple;

import junit.framework.TestCase;
import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.interval.IntervalDegree;
import org.junit.Test;


public class SimpleDegreeTest extends TestCase {

    @Test
    public void testSetValue() throws Exception {
        SimpleDegree x = new SimpleDegree(0.43);
        x.setValue(0.6349);
        assertEquals(0.6349,x.getValue());
    }

    @Test
    public void testGetValue() throws Exception {
        SimpleDegree x = new SimpleDegree(0.43);
        assertEquals(0.43,x.getValue());
    }

    @Test
    public void testHashCode() throws Exception {
        SimpleDegree s1 = new SimpleDegree(0.1);
        SimpleDegree s2 = new SimpleDegree(0.2);
        SimpleDegree s3 = new SimpleDegree(0.1);

        assertEquals(s1.hashCode(),s1.hashCode());
        assertEquals(s1.hashCode(),s3.hashCode());
        assertNotSame(s1.hashCode(),s2.hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        SimpleDegree s1 = new SimpleDegree(0.1);
        SimpleDegree s2 = new SimpleDegree(0.2);
        SimpleDegree s3 = new SimpleDegree(0.1);

        assertTrue(s1.equals(s1));
        assertTrue(s1.equals(s3));
        assertFalse(s1.equals(s2));
        assertFalse(s1.equals(null));
    }

    @Test
    public void testTrue() throws Exception {
        assertEquals(1.0,SimpleDegree.TRUE.getValue());

        assertEquals(1.0, new SimpleDegree(0.2).True().getValue());
    }

    @Test
    public void testFalse() throws Exception {
        assertEquals(0.0,SimpleDegree.FALSE.getValue());

        assertEquals(0.0,new SimpleDegree(0.2).False().getValue());
    }

    @Test
    public void testUnknown() throws Exception {
        assertEquals(0.0,new SimpleDegree(0.2).Unknown().getValue());
    }

    @Test
    public void testToBoolean() throws Exception {
        assertFalse(new SimpleDegree(0.0).toBoolean());

        assertTrue(new SimpleDegree(1.0).toBoolean());

        assertTrue(new SimpleDegree(0.8).toBoolean());
    }

    @Test
    public void testAsSimpleDegree() throws Exception {
        SimpleDegree x = new SimpleDegree(0.3);
        assertEquals(x,x.asSimpleDegree());
    }

    @Test
    public void testToString() throws Exception {
        SimpleDegree x = new SimpleDegree(0.3);
        assertNotNull(x.toString());
        assertTrue(x.toString().length() > 0);
        assertTrue(x.toString().contains("0.3"));
    }

    @Test
    public void testGetConfidence() throws Exception {
        SimpleDegree x = new SimpleDegree(0.3);
        assertEquals(1.0,x.getConfidence());
    }

    @Test
    public void testCompareTo() throws Exception {
        SimpleDegree x = new SimpleDegree(0.3);
        SimpleDegree y = new SimpleDegree(0.4);
        SimpleDegree z = new SimpleDegree(0.2);

        assertEquals(-1,x.compareTo(y));
        assertEquals(0,x.compareTo(x));
        assertEquals(1,x.compareTo(z));
    }

    @Test
    public void testAsIntervalDegree() throws Exception {
        SimpleDegree x = new SimpleDegree(0.3);
        IntervalDegree i = x.asIntervalDegree();
        assertEquals(0.3,i.getTau());
        assertEquals(1.0-0.3,i.getPhi());
    }

    @Test
    public void testConstructor() throws Exception {
        SimpleDegree x = new SimpleDegree(0.3);

        try {
            SimpleDegree z = new SimpleDegree(-0.4);
            fail();
        } catch (IllegalArgumentException iae) { }

        try {
            SimpleDegree z = new SimpleDegree(3.1);
            fail();
        } catch (IllegalArgumentException iae) { }
    }




    @Test
    public void testSum() throws Exception {
        SimpleDegree s1 = new SimpleDegree(0.6);
        SimpleDegree s2 = new SimpleDegree(0.3);
        SimpleDegree s3 = new SimpleDegree(0.9);

        IDegree s4 = s1.sum(s2);
        IDegree s5 = s1.sum(s3);

        assertEquals(new SimpleDegree(0.6),s1);
        assertEquals(new SimpleDegree(0.3),s2);
        assertEquals(new SimpleDegree(0.9),s3);

        assertEquals(s4,s3);
        assertEquals(new SimpleDegree(1.0),s5);
    }

    @Test
    public void testMul() throws Exception {
        SimpleDegree s1 = new SimpleDegree(0.6);
        SimpleDegree s2 = new SimpleDegree(0.3);
        SimpleDegree s3 = new SimpleDegree(0.9);

        IDegree s4 = s1.mul(s2);
        IDegree s5 = s1.mul(s3);

        assertEquals(new SimpleDegree(0.6),s1);
        assertEquals(new SimpleDegree(0.3),s2);
        assertEquals(new SimpleDegree(0.9),s3);

        assertEquals(new SimpleDegree(0.18),s4);
        assertEquals(new SimpleDegree(0.54),s5);
    }

    @Test
    public void testDiv() throws Exception {
        SimpleDegree s1 = new SimpleDegree(0.6);
        SimpleDegree s2 = new SimpleDegree(0.3);
        SimpleDegree s3 = new SimpleDegree(0.9);
        SimpleDegree s0 = new SimpleDegree(0.0);

        IDegree s4 = s2.div(s1);
        IDegree s5 = s3.div(s1);
        IDegree s9 = s1.div(s0);

        assertEquals(new SimpleDegree(0.6),s1);
        assertEquals(new SimpleDegree(0.3),s2);
        assertEquals(new SimpleDegree(0.9),s3);

        assertEquals(new SimpleDegree(0.5),s4);
        assertEquals(new SimpleDegree(1.0),s5);
        assertEquals(new SimpleDegree(0.0),s9);
    }

    @Test
    public void testSub() throws Exception {
        SimpleDegree s1 = new SimpleDegree(0.6);
        SimpleDegree s2 = new SimpleDegree(0.3);
        SimpleDegree s3 = new SimpleDegree(0.9);

        IDegree s4 = s1.sub(s2);
        IDegree s5 = s1.sub(s3);

        assertEquals(new SimpleDegree(0.6),s1);
        assertEquals(new SimpleDegree(0.3),s2);
        assertEquals(new SimpleDegree(0.9),s3);

        assertEquals(new SimpleDegree(0.3),s4);
        assertEquals(new SimpleDegree(0.0),s5);
    }

    @Test
    public void testMax() throws Exception {
        SimpleDegree s1 = new SimpleDegree(0.6);
        SimpleDegree s2 = new SimpleDegree(0.3);

        IDegree s4 = s1.max(s2);

        assertEquals(new SimpleDegree(0.6),s1);
        assertEquals(new SimpleDegree(0.3),s2);

        assertEquals(s1,s4);
    }

    @Test
    public void testMin() throws Exception {
        SimpleDegree s1 = new SimpleDegree(0.6);
        SimpleDegree s2 = new SimpleDegree(0.3);

        IDegree s4 = s1.min(s2);

        assertEquals(new SimpleDegree(0.6),s1);
        assertEquals(new SimpleDegree(0.3),s2);

        assertEquals(s2,s4);
    }

    @Test
    public void testFromConst() throws Exception {
        SimpleDegree s1 = new SimpleDegree(0.6);

        IDegree s2 = s1.fromConst(0.33);

        assertEquals(new SimpleDegree(0.33),s2);
    }



}
