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

package org.drools.chance.distribution.probability;

import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.*;


/**
 * Level II factory for discrete probability distributions
 * @param <T>
 */
public class BasicDistributionStrategyFactory<T> implements DistributionStrategyFactory<T> {


    public <T> DistributionStrategies buildStrategies(DegreeType degreeType, Class<T> domainType) {
        return new BasicDistributionStrategy<T>(degreeType, domainType, ChanceStrategyFactory.getConnectiveFactory(getImp_Kind(), getImp_Model()) );
    }




    public ImpKind getImp_Kind() {
        return ImpKind.PROBABILITY;
    }

    public ImpType getImp_Model() {
        return ImpType.BASIC;
    }


}
