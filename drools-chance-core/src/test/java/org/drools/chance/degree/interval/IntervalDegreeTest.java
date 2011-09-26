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

package org.drools.chance.degree.interval;

import junit.framework.TestCase;
import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.junit.Test;

public class IntervalDegreeTest extends TestCase {




    @Test
    public void testAsSimpleDegree() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);

        SimpleDegree s = x.asSimpleDegree();
        assertEquals(0.35, s.getValue());
    }

    @Test
    public void testGetValue() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);

        assertEquals(0.35,x.getValue());
    }

    @Test
    public void testToBoolean() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);

        assertTrue(x.toBoolean());

        IntervalDegree y = new IntervalDegree(0,0.62);

        assertFalse(y.toBoolean());

    }

    @Test
    public void testToString() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);
        assertNotNull(x.toString());
        assertTrue(x.toString().length() > 0);
        assertTrue(x.toString().contains("0.35"));
        assertTrue(x.toString().contains("0.8"));
    }

    @Test
    public void testGetPhi() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);
        assertEquals(1-0.8,x.getPhi());
    }

    @Test
    public void testGetTau() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);
        assertEquals(0.35,x.getTau());
    }

    @Test
    public void testGetLow() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);
        assertEquals(0.35,x.getLow());
    }

    @Test
    public void testGetUpp() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);
        assertEquals(0.8,x.getUpp());
    }

    @Test
    public void testHashCode() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);
        IntervalDegree y = new IntervalDegree(0.22,0.4);
        IntervalDegree z = new IntervalDegree(0.35,0.8);

        assertEquals(x.hashCode(),x.hashCode());
        assertEquals(x.hashCode(),z.hashCode());
        assertNotSame(x.hashCode(),y.hashCode());

    }

    @Test
    public void testEquals() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);
        IntervalDegree y = new IntervalDegree(0.22,0.4);
        IntervalDegree z = new IntervalDegree(0.35,0.8);

        assertTrue(x.equals(x));
        assertFalse(x.equals(null));
        assertTrue(x.equals(z));
        assertFalse(x.equals(y));
    }

    @Test
    public void testGetConfidence() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.7);
        assertEquals(0.65,x.getConfidence());
    }

    @Test
    public void testAsIntervalDegree() throws Exception {
        IntervalDegree x = new IntervalDegree(0.35,0.8);
        IntervalDegree y = x.asIntervalDegree();
        assertEquals(x,y);
    }


    @Test
    public void testIsComparable() throws Exception {
        IntervalDegree x = new IntervalDegree(0.0,0.4);
        IntervalDegree y = new IntervalDegree(0.6,0.9);
        IntervalDegree z = new IntervalDegree(0.3, 0.75);

        assertTrue(x.isComparableTo(y));
        assertTrue(y.isComparableTo(x));

        assertFalse(x.isComparableTo(z));
        assertFalse(z.isComparableTo(x));
        assertFalse(y.isComparableTo(z));
        assertFalse(z.isComparableTo(y));
    }

    @Test
    public void testCompareTo() throws Exception {
        IntervalDegree x = new IntervalDegree(0.0,0.4);
        IntervalDegree y = new IntervalDegree(0.6,0.9);
        IntervalDegree z = new IntervalDegree(0.3, 0.75);
        IntervalDegree w = new IntervalDegree(0.0,0.4);


        assertEquals(-1,x.compareTo(y));
        assertEquals(1,y.compareTo(x));
        assertEquals(0,x.compareTo(x));
        assertEquals(0,x.compareTo(w));

        assertEquals(-99,x.compareTo(z));
        assertEquals(-99,z.compareTo(x));
        assertEquals(-99,y.compareTo(z));
        assertEquals(-99,z.compareTo(y));
    }

    @Test
    public void testFalse() throws Exception {
        assertEquals(new IntervalDegree(0.2,0.4).False(),new IntervalDegree(0,0));
    }

    @Test
    public void testTrue() throws Exception {
        assertEquals(new IntervalDegree(0.2,0.4).True(),new IntervalDegree(1,1));
    }

    @Test
    public void testUnknown() throws Exception {
        assertEquals(new IntervalDegree(0.2,0.4).Unknown(),new IntervalDegree(0,1));
    }

    @Test
    public void testSum() throws Exception {
        IntervalDegree x = new IntervalDegree(0.0,0.4);
        IntervalDegree y = new IntervalDegree(0.6,0.9);
        IntervalDegree z = new IntervalDegree(0.6, 0.75);
        IntervalDegree n = new IntervalDegree(0.0, 0.0);


        IDegree s = x.sum(y);
        assertTrue(s instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.6,1),s);

        IDegree t = y.sum(z);
        assertTrue(t instanceof IntervalDegree);
        assertEquals(new IntervalDegree(1,1),t);


        assertEquals(new IntervalDegree(0.0,0.4),x);
        assertEquals(new IntervalDegree(0.6,0.9),y);
        assertEquals(new IntervalDegree(0.6,0.75),z);



        IDegree m = y.sum(n);
        assertTrue(m instanceof IntervalDegree);
        assertEquals(m,y);
    }

    @Test
    public void testMul() throws Exception {
        IntervalDegree x = new IntervalDegree(0.0,0.4);
        IntervalDegree y = new IntervalDegree(0.6,0.9);
        IntervalDegree z = new IntervalDegree(0.6, 0.8);
        IntervalDegree n = new IntervalDegree(1.0, 1.0);

        IDegree s = x.mul(y);
        assertTrue(s instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.0,0.36),s);

        IDegree t = y.mul(z);
        assertTrue(t instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.36,0.72),t);


        assertEquals(new IntervalDegree(0.0,0.4),x);
        assertEquals(new IntervalDegree(0.6,0.9),y);
        assertEquals(new IntervalDegree(0.6,0.8),z);


        IDegree m = y.mul(n);
        assertTrue(m instanceof IntervalDegree);
        assertEquals(m,y);
    }


    @Test
    public void testSub() throws Exception {
        IntervalDegree x = new IntervalDegree(0.0,0.4);
        IntervalDegree y = new IntervalDegree(0.6,0.9);
        IntervalDegree z = new IntervalDegree(0.6, 0.8);
        IntervalDegree n = new IntervalDegree(0.0, 0.0);

        IDegree s = y.sub(x);
        assertTrue(s instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.2,0.9),s);

        IDegree t = z.sub(y);
        assertTrue(t instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.0,0.2),t);


        assertEquals(new IntervalDegree(0.0,0.4),x);
        assertEquals(new IntervalDegree(0.6,0.9),y);
        assertEquals(new IntervalDegree(0.6,0.8),z);

        IDegree m = y.sub(n);
        assertTrue(m instanceof IntervalDegree);
        assertEquals(m,y);
    }



    @Test
    public void testDiv() throws Exception {
        IntervalDegree x = new IntervalDegree(0.0,0.4);
        IntervalDegree y = new IntervalDegree(0.6,0.9);
        IntervalDegree z = new IntervalDegree(0.6, 0.8);
        IntervalDegree n = new IntervalDegree(1.0, 1.0);
        IntervalDegree v = new IntervalDegree(0.0, 0.0);

        IDegree s = x.div(y);
        assertTrue(s instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0,0.4/0.6),s);

        IDegree t = z.div(y);
        assertTrue(t instanceof IntervalDegree);
        assertEquals(new IntervalDegree(2.0/3.0,1.0),t);

        assertEquals(new IntervalDegree(0.0,0.4),x);
        assertEquals(new IntervalDegree(0.6,0.9),y);
        assertEquals(new IntervalDegree(0.6,0.8),z);


        IDegree m = y.div(n);
        assertTrue(m instanceof IntervalDegree);
        assertEquals(m,y);

        IDegree u = y.div(v);
        assertTrue(u instanceof IntervalDegree);
        assertEquals(u,n);

    }




    @Test
    public void testMax() throws Exception {
        IntervalDegree x = new IntervalDegree(0.0,0.4);
        IntervalDegree y = new IntervalDegree(0.6,0.9);
        IntervalDegree z = new IntervalDegree(0.6, 0.8);
        IntervalDegree n = new IntervalDegree(0.0, 0.0);

        IDegree s = x.max(y);
        assertTrue(s instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.6,0.9),s);

        IDegree t = z.max(y);
        assertTrue(t instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.6,0.9),t);

        assertEquals(new IntervalDegree(0.0,0.4),x);
        assertEquals(new IntervalDegree(0.6,0.9),y);
        assertEquals(new IntervalDegree(0.6,0.8),z);


        IDegree m = y.max(n);
        assertTrue(m instanceof IntervalDegree);
        assertEquals(m,y);

    }




    @Test
    public void testMin() throws Exception {
        IntervalDegree x = new IntervalDegree(0.0,0.4);
        IntervalDegree y = new IntervalDegree(0.6,0.9);
        IntervalDegree z = new IntervalDegree(0.6, 0.8);
        IntervalDegree n = new IntervalDegree(1.0, 1.0);

        IDegree s = x.min(y);
        assertTrue(s instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.0,0.4),s);

        IDegree t = z.min(y);
        assertTrue(t instanceof IntervalDegree);
        assertEquals(new IntervalDegree(0.6,0.8),t);

        assertEquals(new IntervalDegree(0.0,0.4),x);
        assertEquals(new IntervalDegree(0.6,0.9),y);
        assertEquals(new IntervalDegree(0.6,0.8),z);


        IDegree m = y.min(n);
        assertTrue(m instanceof IntervalDegree);
        assertEquals(m,y);

    }
}




