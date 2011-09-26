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

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.chance.builder.ChanceBeanBuilder;
import org.drools.chance.builder.ChanceBuilder;
import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.IDistributionStrategyFactory;
import org.drools.factmodel.ClassBuilderFactory;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertNotNull;


public class ImperfectFactsTest {



    @Before
    public void setUp() throws Exception {
        IDistributionStrategyFactory factory = (IDistributionStrategyFactory) Class.forName("org.drools.chance.distribution.probability.discrete.DiscreteDistributionStrategyFactory").newInstance();
        StrategyFactory.register(factory.getImp_Kind(), factory.getImp_Model(), factory);


        IDistributionStrategyFactory factory2 = (IDistributionStrategyFactory) Class.forName("org.drools.chance.distribution.probability.dirichlet.DirichletDistributionStrategyFactory").newInstance();
        StrategyFactory.register(factory2.getImp_Kind(), factory2.getImp_Model(), factory2);


        IDistributionStrategyFactory factory3 = (IDistributionStrategyFactory) Class.forName("org.drools.chance.distribution.fuzzy.linguistic.ShapedFuzzyPartitionStrategyFactory").newInstance();
        StrategyFactory.register(factory3.getImp_Kind(), factory3.getImp_Model(), factory3);

        IDistributionStrategyFactory factory4 = (IDistributionStrategyFactory) Class.forName("org.drools.chance.distribution.fuzzy.linguistic.LinguisticPossibilityDistributionStrategyFactory").newInstance();
        StrategyFactory.register(factory4.getImp_Kind(), factory3.getImp_Model(), factory4);


        DegreeTypeRegistry.getSingleInstance().registerDegreeType("simple",SimpleDegree.class);
    }


    @Test
    public void testFactGeneration() {

        ClassBuilderFactory.setBeanClassBuilderService( new ChanceBeanBuilder() );

        String source = "org/drools/chance/testImperfectFacts.drl";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource res = ResourceFactory.newClassPathResource(source);
            assertNotNull(res);
        kbuilder.add(res, ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
        kb.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession kSession = kb.newStatefulKnowledgeSession();

        kSession.fireAllRules();

        Collection c = kSession.getObjects();

        System.out.println("----");
    }
}
