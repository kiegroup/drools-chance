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

package org.drools.chance.distribution.fuzzy.linguistic;

import de.lab4inf.fuzzy.FuzzyAlphaCutPartition;
import de.lab4inf.fuzzy.UniqueFuzzyPartition;
import org.drools.chance.degree.Degree;
import org.drools.chance.distribution.ContinuousPossibilityDistribution;

import java.util.Iterator;
import java.util.Map;

public class LinguisticPossibilityDistribution<T extends Number>
        implements ContinuousPossibilityDistribution<Number> {

    private FuzzyAlphaCutPartition cutPart;
    private Degree master;

    public LinguisticPossibilityDistribution(Map<Linguistic<Number>,Degree> map) {
        UniqueFuzzyPartition.clearPartitionNames();
        cutPart = new FuzzyAlphaCutPartition(map.keySet().iterator().getClass().getName());
        Iterator<Linguistic<Number>> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            Linguistic ling = iter.next();
            cutPart.add(ling.getLabel(),ling.getSet());
            cutPart.set(ling.getLabel(),map.get(ling).getValue());
            if (master == null) master = map.get(ling);
        }
    }


    public Degree getDegree(Number value) {
        double[] mus = cutPart.fuzzyfy(value.doubleValue());
        double max = mus[0];
        for (int j = 1; j < mus.length; j++)
            max = Math.max(max,mus[j]);
        return master.fromConst(max);
    }


    public Degree get(Number value) {
        return getDegree( value );
    }


    public Number domainSize() {
        return Double.POSITIVE_INFINITY;
    }


    public FuzzyAlphaCutPartition getPartition() {
        return cutPart;
    }


}