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

import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.ValueDegreePair;
import org.drools.chance.degree.simple.SimpleDegree;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;



public class TestDiscreteDistPerformance {

	private static int NUMADD=1000000;

	private static DiscreteDistributionTreeSet<String> treeSetDistr = new DiscreteDistributionTreeSet<String>();
	private static DiscreteDistribution<String> sortMapDistr = new DiscreteDistribution<String>();
	private static Random rand = new Random();



    @Test
    public void TestAddTree(){

        for(int i=0; i < NUMADD; i++)
            treeSetDistr.add(new ValueDegreePair<String>("add"+i, new SimpleDegree(0.1*i/NUMADD)));

        assertEquals(NUMADD, treeSetDistr.size());
        System.err.println("Done with "+ NUMADD);
    }


    @Test
    public void TestAddHashMap(){
        for(int i=0; i < NUMADD; i++)
            sortMapDistr.put("add"+i, new SimpleDegree(0.1*i/NUMADD));

        assertEquals(NUMADD, sortMapDistr.domainSize());
        System.err.println("Done with " + NUMADD);

    }


     @Test
    public void TestRefAddToList(){
         LinkedList<ValueDegreePair> list = new LinkedList<ValueDegreePair>();
        for(int i=0; i < NUMADD; i++)
            list.add(new ValueDegreePair<String>("add"+i, new SimpleDegree(0.1*i/NUMADD)));

        assertEquals(NUMADD, list.size());
        System.err.println("Done with "+ NUMADD);
    }


    @Test
    public void testRefHashTable() {
         Hashtable<String,IDegree> table = new Hashtable<String, IDegree>();
        for(int i=0; i < NUMADD; i++)
            table.put("add" + i, new SimpleDegree(0.1 * i / NUMADD));

        assertEquals(NUMADD, table.size());
        System.err.println("Done with "+ NUMADD);
    }






}
