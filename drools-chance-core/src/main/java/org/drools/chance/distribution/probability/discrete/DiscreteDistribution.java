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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.chance.degree.IDegree;
import org.drools.chance.distribution.IDiscreteProbabilityDistribution;
import org.drools.chance.utils.ValueSortedMap;

public class DiscreteDistribution<T> implements IDiscreteProbabilityDistribution<T> {

	private ValueSortedMap<T, IDegree> valueSorMap = new ValueSortedMap<T, IDegree>();

	public DiscreteDistribution() {
		super();
	}

	public DiscreteDistribution(Collection<T> values,
			Collection<IDegree> probabilities) {
		Iterator<T> vIter = values.iterator();
		Iterator<IDegree> dIter = probabilities.iterator();

		while (vIter.hasNext())
			valueSorMap.put(vIter.next(), dIter.next());
	}

	public void put(T value, IDegree prob) {
		valueSorMap.put(value, prob);
	}

	public T getBest() {
		return valueSorMap.isEmpty() ? null : valueSorMap.keySet().iterator().next();
	}

	public IDegree getDegree(T value) {
		return valueSorMap.get(value);
	}

	public Number domainSize() {
		return valueSorMap.size();
	}

    public int size() {
        return valueSorMap.size();
    }

	public Map<T, IDegree> getDistribution() {
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




    public Iterator<T> iterator() {
        return valueSorMap.keySet().iterator();
    }
}