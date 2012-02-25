/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.chance.distribution;

import org.drools.chance.degree.Degree;

import java.util.Map;
import java.util.Set;


/**
 * Companion and Level III factory to an Distribution<T>,
 * classes implementing this interface are meant to be a stub for all the algorithms
 * required to manipulate a specific type of distribution.
 *
 * Methods are overloaded to provide a default behaviour, as well as the possibility
 * to choose different strategies using either a String selctor or passing generic parameters
 * @param <T>
 */
public interface DistributionStrategies<T> {



    /**
     * Factory method
     * Converts a value to a (degenerate) distribution,
     * assigning all the probability/mass/belief/... to that value
     * @param value     the value used to generate a distribution
     * @return
     */
    Distribution<T> toDistribution(T value);
    Distribution<T> toDistribution(T value, String strategy);
    Distribution<T> toDistribution(T value, Object... params);


    /**
     * Factory method
     * Restores a distribution serialized as String
     * @param distrAsString
     * @return
     */
    Distribution<T> parse(String distrAsString);



    /**
     * Factory method
     * Creates a non-informative distribution on the domain
     * E.g. uniform probability, fuzzy set with uniform membership set to 1,
     * basic mass assignment with mass 1 assigned to the universe set, ...
     * @return
     */
    Distribution<T> newDistribution();

    /**
     * Factory method
     * Creates a non-informative distribution on the domain
     * E.g. uniform probability, fuzzy set with uniform membership set to 1,
     * basic mass assignment with mass 1 assigned to the universe set, ...
     * @param   focalElements
     * @return
     */
    Distribution<T> newDistribution(Set<T> focalElements);

    /**
     * Factory method
     * Creates a (discrete) distribution by enumeration
     * E.g. discrete probability, discrete fuzzy set, basic mass assignment, ...
     * @param elements
     * @return
     */
    Distribution<T> newDistribution(Map<? extends T, ? extends Degree> elements);




    /**
     * Converts a distribution on a domain to a crisp value on that domain.
     * E.g. maximum likelihood, maximum possibility, expected value
     * Unlike sample(), this method is meant to be deterministic for a given distribution
     * @param dist the distribution to convert
     * @return a representative value in the domain T
     */
    T toCrispValue(Distribution<T> dist);
    T toCrispValue(Distribution<T> dist, String strategy);
    T toCrispValue(Distribution<T> dist, Object... params);

    /**
     * Samples a distribution and returns an element of the domain
     * Unlike toCrispValue, this method is not deterministic
     * E.g. any sampling method
     * @param dist
     * @return
     */
    T sample(Distribution<T> dist);
    T sample(Distribution<T> dist, String strategy);
    T sample(Distribution<T> dist, Object... params);


    /**
     * Updates a distribution, merging it with another one (possibly partial)
     * e.g. fuzzy set operations (not due to logical connectives, even if equivalent in practice!),
     * bayesian conditioning, belief update, ...
     * @param current
     * @param newBit
     * @return the original distribution, updated
     */
    Distribution<T> merge(Distribution<T> current, Distribution<T> newBit);
    Distribution<T> merge(Distribution<T> current, Distribution<T> newBit, String strategy);
    Distribution<T> merge(Distribution<T> current, Distribution<T> newBit, Object... params);



    /**
     * Updates a distribution, merging it with another one (possibly partial)
     * e.g. fuzzy set operations (not due to logical connectives, even if equivalent in practice!),
     * bayesian conditioning, belief update, ...
     * @param current
     * @param newBit
     * @return a copy of the original distribution, updated
     */
    Distribution<T> mergeAsNew(Distribution<T> current, Distribution<T> newBit);
    Distribution<T> mergeAsNew(Distribution<T> current, Distribution<T> newBit, String strategy);
    Distribution<T> mergeAsNew(Distribution<T> current, Distribution<T> newBit, Object... params);



    /**
     * Updates a distribution, merging it with another one (possibly partial)
     * e.g. fuzzy set operations (not due to logical connectives, even if equivalent in practice!),
     * bayesian conditioning, belief update, ...
     * @param current
     * @param newBit
     * @return the original distribution, updated
     */
    Distribution<T> remove(Distribution<T> current, Distribution<T> newBit);
    Distribution<T> remove(Distribution<T> current, Distribution<T> newBit, String strategy);
    Distribution<T> remove(Distribution<T> current, Distribution<T> newBit, Object... params);



    /**
     * Updates a distribution, merging it with another one (possibly partial)
     * e.g. fuzzy set operations (not due to logical connectives, even if equivalent in practice!),
     * bayesian conditioning, belief update, ...
     * @param current
     * @param newBit
     * @return a copy of the original distribution, updated
     */
    Distribution<T> removeAsNew(Distribution<T> current, Distribution<T> newBit);
    Distribution<T> removeAsNew(Distribution<T> current, Distribution<T> newBit, String strategy);
    Distribution<T> removeAsNew(Distribution<T> current, Distribution<T> newBit, Object... params);



}
