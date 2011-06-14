package org.drools.chance.distribution.probability.dirichlet;

import org.drools.chance.distribution.IDistributionStrategies;
import org.drools.chance.distribution.IDistributionStrategyFactory;


/**
 * Level II factory for Dirichlet discrete probability distributions
 * @param <T>
 */
public class DirichletDistributionStrategyFactory<T> implements IDistributionStrategyFactory<T> {


    private static final String KIND = "probability";
    private static final String TYPE = "dirichlet";





	public <T> IDistributionStrategies buildStrategies(String degreeType, Class<T> domainType) {
        return new DirichletDistributionStrategy<T>(degreeType, domainType);
	}


    public String getImp_Kind() {
        return KIND;
    }

    public String getImp_Model() {
        return TYPE;
    }


}
