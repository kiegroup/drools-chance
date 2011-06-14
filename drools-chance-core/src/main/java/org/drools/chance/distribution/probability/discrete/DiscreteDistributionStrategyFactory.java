package org.drools.chance.distribution.probability.discrete;

import org.drools.chance.distribution.IDistributionStrategies;
import org.drools.chance.distribution.IDistributionStrategyFactory;


/**
 * Level II factory for discrete probability distributions
 * @param <T>
 */
public class DiscreteDistributionStrategyFactory<T> implements IDistributionStrategyFactory<T> {


    private static final String KIND = "probability";
    private static final String TYPE = "discrete";



    //private DiscreteDistributionStrategy<T> instance = new DiscreteDistributionStrategy<T>();



	public <T> IDistributionStrategies buildStrategies(String degreeType, Class<T> domainType) {

        return new DiscreteDistributionStrategy<T>(degreeType, domainType);
	}




    public String getImp_Kind() {
        return KIND;
    }

    public String getImp_Model() {
        return TYPE;
    }


}
