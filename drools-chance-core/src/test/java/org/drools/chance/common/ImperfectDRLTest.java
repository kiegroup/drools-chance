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
import org.drools.chance.Chance;
import org.drools.chance.factmodel.ChanceBeanBuilderImpl;
import org.drools.chance.factmodel.ChanceTraitBuilderImpl;
import org.drools.chance.factmodel.ChanceTriplePropertyWrapperClassBuilderImpl;
import org.drools.chance.factmodel.ChanceTripleProxyBuilderImpl;
import org.drools.factmodel.ClassBuilderFactory;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


public class ImperfectDRLTest {



    protected StatefulKnowledgeSession kSession;
    protected List<String> list = new ArrayList<String>();

    @Before
    public void setUp() throws Exception {
        TraitFactory.reset();
        initObjects();
    }


    private void initObjects() throws Exception {

        Chance.initialize();

        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( Chance.getChanceKBuilderConfiguration() );
        kBuilder.add( new ClassPathResource( "org/drools/chance/factmodel/testImperfectRules.drl" ), ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase( Chance.getChanceKnowledgeBaseConfiguration() );
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );
        kSession = kBase.newStatefulKnowledgeSession();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

    }




    @After
    public void tearDown() throws Exception {

    }




    @Test
    public void testRules() {
        assertTrue( list.contains( "OK" ) );
    }



}
