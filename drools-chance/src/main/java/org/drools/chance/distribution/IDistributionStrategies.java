package org.drools.chance.distribution;

import java.util.Map;
import java.util.Set;

import org.drools.chance.degree.IDegree;


/**
 * Companion and Level III factory to an IDistribution<T>,
 * classes implementing this interface are meant to be a stub for all the algorithms
 * required to manipulate a specific type of distribution.
 *
 * Methods are overloaded to provide a default behaviour, as well as the possibility
 * to choose different strategies using either a String selctor or passing generic parameters
 * @param <T>
 */
public interface IDistributionStrategies<T> {



    /**
     * Factory method
     * Converts a value to a (degenerate) distribution,
     * assigning all the probability/mass/belief/... to that value
     * @param value     the value used to generate a distribution
     * @return
     */
	IDistribution<T> toDistribution(T value);
	IDistribution<T> toDistribution(T value, String strategy);
	IDistribution<T> toDistribution(T value, Object... params);


    /**
     * Factory method
     * Restores a distribution serialized as String
     * @param distrAsString
     * @return
     */
    IDistribution<T> parse(String distrAsString);



     /**
     * Factory method
     * Creates a non-informative distribution on the domain
     * E.g. uniform probability, fuzzy set with uniform membership set to 1,
     * basic mass assignment with mass 1 assigned to the universe set, ...
     * @return
     */
	IDistribution<T> newDistribution();

    /**
     * Factory method
     * Creates a non-informative distribution on the domain
     * E.g. uniform probability, fuzzy set with uniform membership set to 1,
     * basic mass assignment with mass 1 assigned to the universe set, ...
     * @param   focalElements
     * @return
     */
	IDistribution<T> newDistribution(Set<T> focalElements);

    /**
     * Factory method
     * Creates a (discrete) distribution by enumeration
     * E.g. discrete probability, discrete fuzzy set, basic mass assignment, ...
     * @param elements
     * @return
     */
	IDistribution<T> newDistribution(Map<? extends T, ? extends IDegree> elements);

    


    /**
     * Converts a distribution on a domain to a crisp value on that domain.
     * E.g. maximum likelihood, maximum possibility, expected value
     * Unlike sample(), this method is meant to be deterministic for a given distribution
     * @param dist the distribution to convert
     * @return a representative value in the domain T
     */
	T toCrispValue(IDistribution<T> dist);
	T toCrispValue(IDistribution<T> dist, String strategy);
	T toCrispValue(IDistribution<T> dist, Object... params);

    /**
     * Samples a distribution and returns an element of the domain
     * Unlike toCrispValue, this method is not deterministic
     * E.g. any sampling method
     * @param dist
     * @return
     */
    T sample(IDistribution<T> dist);
	T sample(IDistribution<T> dist, String strategy);
    T sample(IDistribution<T> dist, Object... params);


    /**
     * Updates a distribution, merging it with another one (possibly partial)
     * e.g. fuzzy set operations (not due to logical connectives, even if equivalent in practice!),
     * bayesian conditioning, belief update, ...
     * @param current
     * @param newBit
     * @return the original distribution, updated
     */
    IDistribution<T> merge(IDistribution<T> current, IDistribution<T> newBit);
    IDistribution<T> merge(IDistribution<T> current, IDistribution<T> newBit, String strategy);
	IDistribution<T> merge(IDistribution<T> current, IDistribution<T> newBit, Object... params);



    /**
     * Updates a distribution, merging it with another one (possibly partial)
     * e.g. fuzzy set operations (not due to logical connectives, even if equivalent in practice!),
     * bayesian conditioning, belief update, ...
     * @param current
     * @param newBit
     * @return a copy of the original distribution, updated
     */
    IDistribution<T> mergeAsNew(IDistribution<T> current, IDistribution<T> newBit);
    IDistribution<T> mergeAsNew(IDistribution<T> current, IDistribution<T> newBit, String strategy);
	IDistribution<T> mergeAsNew(IDistribution<T> current, IDistribution<T> newBit, Object... params);


}
