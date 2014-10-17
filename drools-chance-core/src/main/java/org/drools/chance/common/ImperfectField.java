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

package org.drools.chance.common;

import org.drools.chance.degree.Degree;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.DistributionStrategies;

public interface ImperfectField<T> {

    public void setValue(T value);


    public void setValue(T value, boolean update);


    public void setValue( T value, Degree deg, String... params );


    /**
     * Setter. Overrides the previous distribution, if any)
     * @param dist A distribution over a bean's field domain
     */
    public void setValue(Distribution<T> dist);

    /**
     * Setter
     * @param dist A distribution over a bean's field domain
     * @param update if true, the new distribution dist will be merged with the current distribution, according to
     * this instance's strategies. If false, the new distribution will override the previous one.
     */
    public void setValue(Distribution<T> dist, boolean update);

    /**
     * Predicate.
     * @return true if the current field has already been set, or is still to be initialized
     */
    public boolean isSet();


    /**
     * Getter
     * @return converts the current distribution to a crisp value (according to the current strategies "toCrispValue").
     * The value returned belongs to the same domain the distribution is defined on (e.g. an Distribution<String> will
     * return a String when crispified)
     */
    public T getCrisp();


    /**
     * Tries to access a "previous" value of this field. Every "set" operation on the field counts
     * as a step.
     *
     * @param time a non-positive integer value.
     * @return The value the field had before the last #time set operations, if possible.
     * 0 corresponds to the current value.
     * @throws IndexOutOfBoundsException
     */
    public Distribution<T> getPast(int time) throws IndexOutOfBoundsException;


    /**
     * @return the current distribution for this field
     */
    public Distribution<T> getCurrent();


    /**
     * Getter
     * @return the current Strategies used to manipulate this field's distribution
     */
    public DistributionStrategies<T> getStrategies();


    /**
     * updates the current distribution, merging it with the provided one, using the current strategies.
     * Convenience method that calls "setValue" with update set to true
     * @param fieldBit the new distribution to merge with the current one
     */
    public void update(Distribution<T> fieldBit);

    /**
     * updates the current distribution, by converting the provided value into a (degenerate) distribution
     * and merging it with the current one.
     * (In many cases, this operation could still return a degenerate distribution centered on value)
     * @param value the value to merge with the current distribution
     */
    public void update(T value);


    /**
     * Updates the current distribution, converting the provided value into a (semi-degenerate) distribution
     * and merging it with the current one.
     * @param deg
     */
    public void update( T value, Degree deg, String... params );

    /**
     * Updates the current distribution, converting the provided value into a (semi-degenerate) distribution
     * and subtracting it from the current one.
     * @param deg
     */
    public void remove( T value, Degree deg, String... params );


    public boolean isNormalized();

    public void normalize();

}
