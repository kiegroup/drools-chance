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

import com.google.common.collect.HashBasedTable;
import org.drools.chance.distribution.IDistributionStrategies;
import org.drools.chance.distribution.IDistributionStrategyFactory;
import org.drools.chance.constraints.core.connectives.IConnectiveFactory;


/**
 * Level I (master) factory.
 *
 * Given a KIND of imperfection --> fuzzy, probability, belief, ...
 * and a TYPE of distribution --> discrete, fuzzyset, Gaussian, Dirichlet, ...
 *
 * retrieves the appropriate IDistributionStrategyFactory which is capable
 * of building the appropriate IDistributionStrategies object, which in turn
 * acts a factory class for the IDistributions, in addition to providing
 * all the necessary methods.
 *
 * Any class implementing IDistributionStrategyFactory must register itself
 * TODO Improve?
 * At the moment, with something like
 *   IDistributionStrategyFactory subFactory =
 *              (IDistributionStrategyFactory) //load class by name//.newInstance();
 *   StrategyFactory.register(subFactory.getImp_Kind(),factory.getImp_Model(),factory);
 *
 *
 * @param <T>
 */
public class StrategyFactory<T> {

    private static HashBasedTable<String,String,IDistributionStrategyFactory> cache
            = HashBasedTable.create();

    private static HashBasedTable<String,String,IConnectiveFactory> cacheConnective= HashBasedTable.create();


	public static <T> IDistributionStrategies buildStrategies(
            String kind, String model,String degreeType, Class<T> domainType) {

		 IDistributionStrategyFactory<T> factory = cache.get(kind,model);
        return factory.<T>buildStrategies(degreeType, domainType);

    }

    public static <T> void register(String kind, String model, IDistributionStrategyFactory<T> factory) {
        cache.put(kind,model,factory);
    }




    public static void registerConnective(String kind,String model, IConnectiveFactory connFac){
        cacheConnective.put(kind,model,connFac);

    }


    public static IConnectiveFactory getConnective(String kind,String model){
        return cacheConnective.get(kind,true);
    }


}
