/*
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 * Copyright (c) 2008, Nationwide Health Information Network (NHIN) Connect.
 * All rights reserved.Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of the NHIN Connect Project nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *    END OF TERMS AND CONDITIONS
 */

package org.drools.chance.common;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.chance.builder.ChanceBeanBuilderImpl;
import org.drools.chance.builder.ChanceTraitBuilderImpl;
import org.drools.chance.builder.ChanceTriplePropertyWrapperClassBuilderImpl;
import org.drools.chance.builder.ChanceTripleProxyBuilderImpl;
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

    @BeforeClass
    public static void setFactories() {
        ChanceStrategyFactory.initDefaults();
        ClassBuilderFactory.setBeanClassBuilderService(new ChanceBeanBuilderImpl() );
        ClassBuilderFactory.setTraitBuilderService( new ChanceTraitBuilderImpl() );
        ClassBuilderFactory.setTraitProxyBuilderService( new ChanceTripleProxyBuilderImpl() );
        ClassBuilderFactory.setPropertyWrapperBuilderService( new ChanceTriplePropertyWrapperClassBuilderImpl() );

    }

    @Before
    public void setUp() throws Exception {
        TraitFactory.reset();
        TraitFactory.clearStore();
        initObjects();
    }


    private void initObjects() throws Exception {

        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ClassPathResource( "org/drools/chance/testImperfectRules.drl" ), ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
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
