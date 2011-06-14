package org.drools.chance.distribution.fuzzy.linguistic;

import org.drools.chance.distribution.IDistributionStrategies;
import org.drools.chance.distribution.IDistributionStrategyFactory;

public class LinguisticPossibilityDistributionStrategyFactory<T extends ILinguistic> implements
		IDistributionStrategyFactory<ILinguistic> {

	
	public <T> IDistributionStrategies<T> buildStrategies(String degreeType, Class<T> domainType) {
		return new LinguisticPossibilityDistributionStrategy(degreeType, domainType);
	}

	public String getImp_Kind() {
		return "possibility";
	}

	public String getImp_Model() {
		return "linguistic";
	}

}
