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
		return valueSorMap.keySet().iterator().next();
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
