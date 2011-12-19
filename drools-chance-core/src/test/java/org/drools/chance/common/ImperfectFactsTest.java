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
import org.drools.chance.builder.ChanceTraitBuilder;
import org.drools.chance.builder.ChanceTriplePropertyWrapperClassBuilderImpl;
import org.drools.chance.builder.ChanceTripleProxyBuilder;
import org.drools.core.util.Entry;
import org.drools.core.util.TripleStore;
import org.drools.event.rule.*;
import org.drools.factmodel.ClassBuilder;
import org.drools.factmodel.ClassBuilderFactory;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Iterator;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;


@Deprecated
public class ImperfectFactsTest {




    @Test
     @Ignore
    public void testImpFactGeneration() {


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



        Object o = kSession.getObjects().iterator().next();
        try {
            o.getClass().getMethod("getName").getReturnType().isAssignableFrom( IImperfectField.class );
        } catch (NoSuchMethodException e) {
            fail(" getName missing or wrong returnType ");
        }

        try {
            o.getClass().getMethod("getFlag").getReturnType().isAssignableFrom( IImperfectField.class );
        } catch (NoSuchMethodException e) {
            fail(" getFlag missing or wrong returnType ");
        }

        try {
            o.getClass().getMethod("getAge").getReturnType().isAssignableFrom( IImperfectField.class );
        } catch (NoSuchMethodException e) {
            fail(" getAge missing or wrong returnType ");
        }

        try {
            o.getClass().getMethod("getBody").getReturnType().isAssignableFrom( IImperfectField.class );
        } catch (NoSuchMethodException e) {
            fail(" getBody missing or wrong returnType ");
        }


        try {
            Method m = o.getClass().getMethod("getWeight");
            assertFalse( m.getReturnType().isAssignableFrom( IImperfectField.class ) );
        } catch (NoSuchMethodException e) {
            fail(" getWeight missing or wrong returnType ");
        }

        try {
            o.getClass().getMethod("getLikes").getReturnType().isAssignableFrom( IImperfectField.class );
        } catch (NoSuchMethodException e) {
            fail(" getBody missing or wrong returnType ");
        }


//declare Imperson
//	name    : String    = "john/0.3, philip/0.7"
//	                @Imperfect( kind="probability", type="discrete", degree="simple" )
//
//	flag    : Boolean    = "true/0.75, false/0.25"
//	                @Imperfect( kind="probability", type="discrete", degree="simple" )
//
//	age     : Integer   = "18/0.02, 19/0.01, 20/0.04"
//	                @Imperfect( kind="probability", type="dirichlet", degree="simple" )
//
//    body    : Weight    = "SLIM/0.6, FAT/0.4"
//                    @Imperfect( kind="fuzzy", type="linguistic", degree="simple", support="weight" )
//
//    weight  : Double
//
//end




        System.out.println("---- " + o );
    }






    @Test
    public void testImpTraitGeneration() {

        ChanceStrategyFactory.initDefaults();
        ClassBuilderFactory.setBeanClassBuilderService( new ChanceBeanBuilder() );

        ClassBuilderFactory.setTraitBuilderService( new ChanceTraitBuilder() );
        ClassBuilderFactory.setTraitProxyBuilderService( new ChanceTripleProxyBuilder() );
        ClassBuilderFactory.setPropertyWrapperBuilderService( new ChanceTriplePropertyWrapperClassBuilderImpl() );


        String source = "org/drools/chance/testImperfectTraits.drl";

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



        Iterator it = kSession.getObjects().iterator();
        Object o = null;
        while ( it.hasNext() ) {
            o = it.next();
            if ( o.getClass().getName().equals("defaultpkg.ImpBeandefaultpkgLegacyBeanProxy") ) {
                break;
            }

        }
//        for ( Method m : o.getClass().getDeclaredMethods() ) {
//            System.err.println( m );
//        }

//        TripleStore commonStore = TraitFactory.getStore();
//        for ( Entry t: commonStore.getTable() ) {
//            if ( t != null) {
//                System.out.println(" -- " + t );
//            }
//        }



        System.out.println("---- " + o );
    }


}
