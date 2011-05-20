package org.drools.chance.distribution;

import java.util.Map;

import org.drools.chance.degree.IDegree;

public interface IDiscreteProbabilityDistribution<T> extends IDiscreteDomainDistribution<T>,
		IProbabilityDistribution<T> {
	
	public Map<T,IDegree> getDistribution();

}
