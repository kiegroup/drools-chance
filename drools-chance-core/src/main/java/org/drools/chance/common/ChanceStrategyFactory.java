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
import org.drools.chance.constraints.core.connectives.IConnectiveFactory;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.DistributionStrategies;
import org.drools.chance.distribution.DistributionStrategyFactory;
import org.drools.chance.distribution.ImpKind;
import org.drools.chance.distribution.ImpType;


/**
 * Level I (master) factory.
 *
 * Given a KIND of imperfection --> fuzzy, probability, belief, ...
 * and a TYPE of distribution --> discrete, fuzzyset, Gaussian, Dirichlet, ...
 *
 * retrieves the appropriate DistributionStrategyFactory which is capable
 * of building the appropriate DistributionStrategies object, which in turn
 * acts a factory class for the IDistributions, in addition to providing
 * all the necessary methods.
 *
 * Any class implementing DistributionStrategyFactory must register itself
 * TODO Improve?
 * At the moment, with something like
 *   DistributionStrategyFactory subFactory =
 *              (DistributionStrategyFactory) //load class by name//.newInstance();
 *   ChanceStrategyFactory.register(subFactory.getImp_Kind(),factory.getImp_Model(),factory);
 *
 *
 * @param <T>
 */
public class ChanceStrategyFactory<T> {

    private static HashBasedTable<ImpKind,ImpType,DistributionStrategyFactory> cache
            = HashBasedTable.create();

    private static HashBasedTable<ImpKind,ImpType,IConnectiveFactory> cacheConnective= HashBasedTable.create();


    public static <T> DistributionStrategies buildStrategies(
            ImpKind kind, ImpType model, DegreeType degreeType, Class<T> domainType) {

        DistributionStrategyFactory<T> factory = cache.get(kind,model);
        return factory.<T>buildStrategies(degreeType, domainType);

    }

    public static <T> void register(ImpKind kind, ImpType model, DistributionStrategyFactory<T> factory) {
        cache.put(kind,model,factory);
    }




    public static void registerConnective(ImpKind kind,ImpType model, IConnectiveFactory connFac){
        cacheConnective.put(kind,model,connFac);

    }


    public static IConnectiveFactory getConnective(ImpKind kind,ImpType model){
        return cacheConnective.get(kind,true);
    }



    public static void initDefaults() {
        try {
            DistributionStrategyFactory factory = (DistributionStrategyFactory) Class.forName("org.drools.chance.distribution.probability.discrete.DiscreteDistributionStrategyFactory").newInstance();
            ChanceStrategyFactory.register(factory.getImp_Kind(), factory.getImp_Model(), factory);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            DistributionStrategyFactory factory = (DistributionStrategyFactory) Class.forName("org.drools.chance.distribution.probability.BasicDistributionStrategyFactory").newInstance();
            ChanceStrategyFactory.register(factory.getImp_Kind(), factory.getImp_Model(), factory);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            DistributionStrategyFactory factory2 = (DistributionStrategyFactory) Class.forName("org.drools.chance.distribution.probability.dirichlet.DirichletDistributionStrategyFactory").newInstance();
            ChanceStrategyFactory.register(factory2.getImp_Kind(), factory2.getImp_Model(), factory2);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            DistributionStrategyFactory factory3 = (DistributionStrategyFactory) Class.forName("org.drools.chance.distribution.fuzzy.linguistic.ShapedFuzzyPartitionStrategyFactory").newInstance();
            ChanceStrategyFactory.register(factory3.getImp_Kind(), factory3.getImp_Model(), factory3);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            DistributionStrategyFactory factory4 = (DistributionStrategyFactory) Class.forName("org.drools.chance.distribution.fuzzy.linguistic.LinguisticPossibilityDistributionStrategyFactory").newInstance();
            ChanceStrategyFactory.register(factory4.getImp_Kind(), factory4.getImp_Model(), factory4);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            DistributionStrategyFactory factory5 = (DistributionStrategyFactory) Class.forName("org.drools.chance.distribution.probability.BasicDistributionStrategyFactory").newInstance();
            ChanceStrategyFactory.register(factory5.getImp_Kind(), factory5.getImp_Model(), factory5);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            DistributionStrategyFactory factory6 = (DistributionStrategyFactory) Class.forName("org.drools.chance.distribution.fuzzy.BasicDistributionStrategyFactory").newInstance();
            ChanceStrategyFactory.register(factory6.getImp_Kind(), factory6.getImp_Model(), factory6);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            DistributionStrategyFactory factory7 = (DistributionStrategyFactory) Class.forName("org.drools.chance.distribution.belief.discrete.TBMStrategyFactory").newInstance();
            ChanceStrategyFactory.register(factory7.getImp_Kind(), factory7.getImp_Model(), factory7);
        } catch ( Exception e ) {
            e.printStackTrace();
        }




        DegreeTypeRegistry.getSingleInstance().registerDegreeType( DegreeType.SIMPLE, SimpleDegree.class);

        DegreeTypeRegistry.getSingleInstance().registerDegreeType( DegreeType.INTERVAL, IntervalDegree.class);
    }




}
