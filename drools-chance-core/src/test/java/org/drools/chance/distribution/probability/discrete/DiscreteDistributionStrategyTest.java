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

package org.drools.chance.distribution.probability.discrete;

import org.drools.chance.degree.DegreeType;
import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.simple.SimpleDegree;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DiscreteDistributionStrategyTest {


    private static DiscreteDistribution<String> sortMapDistrCurr = new DiscreteDistribution<String>();
    private static DiscreteDistribution<String> sortMapDistrNew = new DiscreteDistribution<String>();
    private static DiscreteDistributionStrategy<String> DisDisStrategy=new DiscreteDistributionStrategy<String>( DegreeType.SIMPLE ,String.class);

    @Test
    public void testMerge() throws Exception {
        sortMapDistrCurr.put("one", new SimpleDegree(0.2));
        sortMapDistrCurr.put("two", new SimpleDegree(0.5));
        sortMapDistrCurr.put("three", new SimpleDegree(0.3));

        sortMapDistrNew.put("one", new SimpleDegree(0.3));
        sortMapDistrNew.put("two", new SimpleDegree(0.4));
        sortMapDistrNew.put("three", new SimpleDegree(0.3));

        DiscreteDistribution<String> ret=(DiscreteDistribution<String>) DisDisStrategy.merge(sortMapDistrCurr, sortMapDistrNew);

        //System.out.println(ret.getDegree("one").getValue());
        assertEquals((0.06/0.35),ret.getDegree("one").getValue(),1e-12);
        assertEquals((0.2/0.35),ret.getDegree("two").getValue(),1e-12);
        assertEquals((0.09/0.35),ret.getDegree("three").getValue(),1e-12);

    }

    @Test
    public void testNewDistribution() throws Exception {

    }

    @Test
    public void testToCrispValue() throws Exception {
        sortMapDistrCurr.put("one", new SimpleDegree(0.2));
        sortMapDistrCurr.put("two", new SimpleDegree(0.5));
        sortMapDistrCurr.put("three", new SimpleDegree(0.3));

        assertEquals("two",DisDisStrategy.toCrispValue(sortMapDistrCurr));
    }

    @Test
    public void testSample() throws Exception {

    }

    @Test
    public void testToDistribution() throws Exception {

    }

    @Test
    public void testParse() throws Exception {
        DegreeTypeRegistry.getSingleInstance().registerDegreeType(DegreeType.SIMPLE, SimpleDegree.class);

        DiscreteDistribution<String> dd= (DiscreteDistribution<String>) DisDisStrategy.parse("john/0.3, mark/0.7");
        assertEquals(0.3,dd.getDegree("john").getValue(),0);
        assertEquals(0.7,dd.getDegree("mark").getValue(),0);


        DiscreteDistributionStrategy<Integer> DisDisStrategyInt=new DiscreteDistributionStrategy<Integer>(DegreeType.SIMPLE,Integer.class);
        DiscreteDistribution<Integer> ddI= (DiscreteDistribution<Integer>) DisDisStrategyInt.parse("5/0.3, 6/0.7");
        assertEquals(0.3,ddI.getDegree(5).getValue(),0);
        assertEquals(0.7,ddI.getDegree(6).getValue(),0);



    }

    @Test
    public void testNewParametricDistribution() throws Exception {

    }
}
