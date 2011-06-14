package org.drools.chance.distribution;

import org.drools.chance.degree.IDegree;

import java.util.Set;


/**
 * Interface for all classes implementing the concept of "distribution" over a generic domain
 * A distribution is assumed to be a map Value -> Degree
 * @param <T>
 */
public interface IDistribution<T>  {

    /**
     * computes the degree for a given value
     * @param value the query value
     * @return the associated Degree
     */
	public IDegree getDegree(T value);


    /**
     * Size of the domain, may be infinite
     * @return
     */
    public Number domainSize();









}
