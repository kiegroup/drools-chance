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

import org.drools.chance.degree.IDegree;


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
     * computes the degree for a given value
     * @param value the query value
     * @return the associated Degree
     */
    public IDegree get(T value);


    /**
     * Size of the domain, may be infinite
     * @return
     */
    public Number domainSize();









}
