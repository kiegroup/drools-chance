package org.drools.chance.common;

import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;


/**
 * Implementation of AbstractImperfectField storing a single distribution.
 *
 * @param <T> the domain over which the distribution is defined
 *
 * @author sotty, doncat
 */
public class ImperfectField<T> extends AbstractImperfectField<T> {

    /**
     * The distribution. One distribution exists for one field in a bean.
     * If the field has type T, the distribution is an IDistribution<T>
     */
    private IDistribution<T> distr;

    /**
     * No-value constructor. Initializes the distribution to the non-informative prior
     * over the concrete domain, delegating to the strategies.
     * @param strategies    the strategies to manipulate the distribution
     */
    public ImperfectField(IDistributionStrategies<T> strategies){
        super(strategies);
        setValue(strategies.newDistribution());
    }

    /**
     * Constructor, builds the initial distribution parsing a String representation of a distribution.
     * The actual parsing is delegated to the strategies
     * @param strategies the strategies to manipulate the distribution
     * @param distrAsString the initial distribution, serialized as String
     */
    public ImperfectField(IDistributionStrategies<T> strategies, String distrAsString){
        super(strategies);
        setValue(strategies.parse(distrAsString));
    }

    /**
     * Constructor, builds the field from an initial distribution
     * @param strategies the strategies to manipulate the distribution
     * @param distr0 the initial distribution
     */
    public ImperfectField(IDistributionStrategies<T> strategies, IDistribution<T> distr0){
        super(strategies);
        setValue(distr0);
    }



    public void setValue(IDistribution<T> dist, boolean update) {
        if (update)
            getStrategies().merge(distr,dist);
        else
            this.distr = dist;
    }

    public IDistribution<T> getPast(int time) throws IndexOutOfBoundsException {
        if (time != 0 )
            throw new IndexOutOfBoundsException("History not supported on this field");
        return distr;
    }


    public IDistribution<T> getCurrent(){
        return distr;
    }


    public boolean isSet() {
        return distr != null;
    }


}
