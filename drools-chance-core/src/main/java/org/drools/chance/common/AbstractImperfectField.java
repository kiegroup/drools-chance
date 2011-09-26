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

import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;

/**
 * Abstract class for the structures holding the imperfect information regarding a single bean's field.
 *
 * This structure mimics the field's value, actually storing a IDistribution over that field's domain.
 * i.e. if Person.age is the target field, this structure will e.g. hold one (or more) IDistribution<Integer>.
 * A IDistribution maps one or more elements of that domain to an IDegree. The semantics is determined on
 * a case-by-case basis. For example, it may be a discrete probability distribution, or a fuzzy set (possibility
 * distribution), or a basic mass assignment, etc...
 *
 * See ImperfectField for the rationale and ImperfectHistoryField
 *
 *
 */
public abstract class AbstractImperfectField<T> implements IImperfectField<T> {

    /**
     * Reference to a Strategy class holding all the algorithms for the manipulation of this field's distribution
     */
    protected IDistributionStrategies<T> strategies;


    /**
     * Basic constructor
     * @param strategies a reference to the Strategy class with the logic for handling this field's distribution
     */
       public AbstractImperfectField(IDistributionStrategies<T> strategies){
           this.strategies = strategies;
       }


    /**
     * "By value" setter. Creates a (degenerate) distribution with full degree assigned to the value
     * @param value the value used to create a degenerate distribution
     */
       public void setValue(T value) {
           setValue(strategies.toDistribution(value));
       }


         public void setValue(T value, boolean update) {
           setValue(strategies.toDistribution(value),update);
       }


    /**
     * Setter. Overrides the previous distribution, if any)
     * @param dist A distribution over a bean's field domain
     */
       public void setValue(IDistribution<T> dist) {
           setValue(dist, isSet());
       }

    /**
     * Setter
     * @param dist A distribution over a bean's field domain
     * @param update if true, the new distribution dist will be merged with the current distribution, according to
     * this instance's strategies. If false, the new distribution will override the previous one.
     */
       public abstract void setValue(IDistribution<T> dist, boolean update);

    /**
     * Predicate.
     * @return true if the current field has already been set, or is still to be initialized
     */
     public abstract boolean isSet();


    /**
     * Getter
     * @return converts the current distribution to a crisp value (according to the current strategies "toCrispValue").
     * The value returned belongs to the same domain the distribution is defined on (e.g. an IDistribution<String> will
     * return a String when crispified)
     */
       public T getCrisp(){
           return strategies.toCrispValue(getCurrent());
       }


    /**
     * Tries to access a "previous" value of this field. Every "set" operation on the field counts
     * as a step.
     *
     * @param time a non-positive integer value.
     * @return The value the field had before the last #time set operations, if possible.
     * 0 corresponds to the current value.
     * @throws IndexOutOfBoundsException
     */
       public abstract IDistribution<T> getPast(int time) throws IndexOutOfBoundsException;


    /**
     * @return the current distribution for this field
     */
       public abstract IDistribution<T> getCurrent();


    /**
     * Getter
     * @return the current Strategies used to manipulate this field's distribution
     */
       public IDistributionStrategies<T> getStrategies() {
           return strategies;
       }

       /**
        * Setter.
        * Do not change the strategy at runtime, unless you REALLY
        * know what you're doing. So, no public API for this
        * @param factory the new Strategies used to manipulate this field's distribution
        */
       protected void setStrategies(IDistributionStrategies<T> factory) {
           this.strategies = factory;
       }


    /**
     * updates the current distribution, merging it with the provided one, using the current strategies.
     * Convenience method that calls "setValue" with update set to true
     * @param fieldBit the new distribution to merge with the current one
     */
       public void update(IDistribution<T> fieldBit) {
           this.setValue(strategies.merge(getCurrent(), fieldBit),true);
       }

    /**
     * updates the current distribution, by converting the provided value into a (degenerate) distribution
     * and merging it with the current one.
     * (In many cases, this operation could still return a degenerate distribution centered on value)
     * @param value the value to merge with the current distribution
     */
       public void update(T value) {
           this.update(strategies.toDistribution(value));
       }
}
