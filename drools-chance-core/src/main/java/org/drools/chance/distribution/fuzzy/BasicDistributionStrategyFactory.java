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

package org.drools.chance.distribution.fuzzy;

import org.drools.chance.distribution.BasicDistributionStrategy;
import org.drools.chance.distribution.IDistributionStrategies;
import org.drools.chance.distribution.IDistributionStrategyFactory;


/**
 * Level II factory for discrete probability distributions
 * @param <T>
 */
public class BasicDistributionStrategyFactory<T> implements IDistributionStrategyFactory<T> {


    private static final String KIND = "fuzzy";
    private static final String TYPE = "basic";



    //private DiscreteDistributionStrategy<T> instance = new DiscreteDistributionStrategy<T>();



	public <T> IDistributionStrategies buildStrategies(String degreeType, Class<T> domainType) {

        return new BasicDistributionStrategy<T>(degreeType, domainType);
	}




    public String getImp_Kind() {
        return KIND;
    }

    public String getImp_Model() {
        return TYPE;
    }


}
