package org.drools.chance.distribution;

import java.util.Map;

import org.drools.chance.degree.IDegree;

public interface IDiscretePossibilityDistribution<T> extends IDiscreteDomainDistribution<T>,
		IPossibilityDistribution<T> {
	
	
	
	public Map<T,IDegree> getDistribution();

}
