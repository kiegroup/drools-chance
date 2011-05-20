package org.drools.chance.distribution.fuzzy.linguistic;

import org.drools.chance.distribution.IDistributionStrategies;
import org.drools.chance.distribution.IDistributionStrategyFactory;
import org.drools.chance.distribution.probability.dirichlet.DirichletDistributionStrategy;

public class ShapedFuzzyPartitionStrategyFactory<T> implements
		IDistributionStrategyFactory<T> {

	
	public <T> IDistributionStrategies<T> buildStrategies(String degreeType, Class<T> domainType) {
		return new ShapedFuzzyPartitionStrategy(degreeType, domainType);
	}

	public String getImp_Kind() {
		return "fuzzy";
	}

	public String getImp_Model() {
		return "linguistic";
	}

}
