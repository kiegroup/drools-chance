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

import org.drools.chance.degree.DegreeType;

/**
 * Interface for Distribution Level II factories
 * This factory is responsible for the creation of the Stragies/Factory
 * defining a specific type of imperfect distribution and its algorithms
 *
 */
public interface DistributionStrategyFactory<T> {

    /**
     * Factory method
     * @param <T>
     * @return
     */

    public <T> DistributionStrategies<T> buildStrategies( DegreeType degreeType, Class<T> priorType);

    /**
     * The kind of imperfection modelled
     * --> fuzzy, probability, belief, ...
     * @return
     */
    public ImpKind getImp_Kind();


    /**
     * The type of distribution implemented
     * --> discrete, fuzzyset, Gaussian, Dirichlet, ...
     * @return
     */
    public ImpType getImp_Model();

}
