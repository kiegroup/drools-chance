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

import org.drools.chance.core.util.ValueSortedMap;
import org.drools.chance.degree.Degree;
import org.drools.chance.distribution.DiscreteProbabilityDistribution;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DiscreteDistribution<T> implements DiscreteProbabilityDistribution<T> {

    private ValueSortedMap<T, Degree> valueSorMap = new ValueSortedMap<T, Degree>();
    private Degree falze;

    public DiscreteDistribution() {
        super();
    }

    public DiscreteDistribution(Collection<T> values,
                                Collection<Degree> probabilities) {
        Iterator<T> vIter = values.iterator();
        Iterator<Degree> dIter = probabilities.iterator();

        while (vIter.hasNext()) {
            put(vIter.next(), dIter.next());
        }


    }

    public void put(T value, Degree prob) {
        valueSorMap.put(value, prob);

        if ( falze == null ) {
            falze = prob.False();
        }
    }

    public T getBest() {
        return valueSorMap.isEmpty() ? null : valueSorMap.keySet().iterator().next();
    }

    public Degree getDegree(T value) {
        Degree deg =  valueSorMap.get(value);
        return deg != null ? deg : falze;
    }

    public Degree get(T value) {
        return getDegree( value );
    }

    public Number domainSize() {
        return valueSorMap.size();
    }

    public int size() {
        return valueSorMap.size();
    }

    public Map<T, Degree> getDistribution() {
        return valueSorMap;
    }

    public Set<T> getSupport() {
        return valueSorMap.keySet();
    }


    public String toString() {
        return "(Discrete) : {" + serialize() + "}";
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        Iterator<T> iter = valueSorMap.keySet().iterator();
        while (iter.hasNext()) {
            T elem = iter.next();
            sb.append(elem).append("/").append(getDegree(elem).getValue());
            if (iter.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }


    public boolean isDiscrete() {
        return true;
    }


    public Iterator<T> iterator() {
        return valueSorMap.keySet().iterator();
    }
}
