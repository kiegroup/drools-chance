package org.drools.chance.common;


import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;

public class ImperfectFieldValue<T> extends ImperfectField<T> {


    public ImperfectFieldValue(IDistributionStrategies<T> tiDistributionStrategies) {
        super(tiDistributionStrategies);
    }

    public ImperfectFieldValue(IDistributionStrategies<T> tiDistributionStrategies, String distrAsString) {
        super(tiDistributionStrategies, distrAsString);
    }

    public ImperfectFieldValue(IDistributionStrategies<T> tiDistributionStrategies, IDistribution<T> distr0) {
        super(tiDistributionStrategies, distr0);
    }


}
