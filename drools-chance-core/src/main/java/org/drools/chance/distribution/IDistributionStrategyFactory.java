package org.drools.chance.distribution;

/**
 * Interface for Distribution Level II factories
 * This factory is responsible for the creation of the Stragies/Factory
 * defining a specific type of imperfect distribution and its algorithms
 *
 */
public interface IDistributionStrategyFactory<T> {

    /**
     * Factory method
     * @param <T>
     * @return
     */

    public <T> IDistributionStrategies<T> buildStrategies(String degreeType, Class<T> priorType);

    /**
     * The kind of imperfection modelled
     * --> fuzzy, probability, belief, ...
     * @return
     */
    public String getImp_Kind();


    /**
     * The type of distribution implemented
     * --> discrete, fuzzyset, Gaussian, Dirichlet, ...
     * @return
     */
    public String getImp_Model();

}
