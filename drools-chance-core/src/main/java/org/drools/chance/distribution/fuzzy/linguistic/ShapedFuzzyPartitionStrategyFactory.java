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

package org.drools.chance.distribution.fuzzy.linguistic;

import org.drools.chance.distribution.IDistributionStrategies;
import org.drools.chance.distribution.IDistributionStrategyFactory;
import org.drools.chance.distribution.probability.dirichlet.DirichletDistributionStrategy;

public class ShapedFuzzyPartitionStrategyFactory<T> implements
		IDistributionStrategyFactory<T> {

	
	public <T> IDistributionStrategies<T> buildStrategies(String degreeType, Class<T> domainType) {
		return new ShapedFuzzyPartitionStrategy(degreeType, domainType);
	}

	public String getImp_Kind() {
		return "fuzzy";
	}

	public String getImp_Model() {
		return "linguistic";
	}

}