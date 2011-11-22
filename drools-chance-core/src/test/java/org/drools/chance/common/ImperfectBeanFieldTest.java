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

import org.drools.ClassObjectFilter;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.chance.builder.ChanceBeanBuilder;
import org.drools.chance.builder.ChanceTraitBuilder;
import org.drools.chance.builder.ChanceTriplePropertyWrapperClassBuilderImpl;
import org.drools.chance.builder.ChanceTripleProxyBuilder;
import org.drools.chance.common.fact.BeanImp;
import org.drools.chance.common.trait.ImpBean;
import org.drools.chance.common.trait.ImpBeanLegacyProxy;
import org.drools.chance.common.trait.LegacyBean;
import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategyFactory;
import org.drools.definition.type.FactType;
import org.drools.factmodel.ClassBuilderFactory;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.*;


public class ImperfectBeanFieldTest {


    private ImpBean beanHand;

    private ImpBean traitHand;

    private Object traitDrl;
    private FactType traitDrlClass;

    private Object beanDrl;
    private FactType beanDrlClass;



    @Before
    public void setUp() throws Exception {
        ChanceStrategyFactory.initDefaults();
        ClassBuilderFactory.setBeanClassBuilderService(new ChanceBeanBuilder() );
        ClassBuilderFactory.setTraitBuilderService( new ChanceTraitBuilder() );
        ClassBuilderFactory.setTraitProxyBuilderService( new ChanceTripleProxyBuilder() );
        ClassBuilderFactory.setPropertyWrapperBuilderService( new ChanceTriplePropertyWrapperClassBuilderImpl() );

        initObjects();
    }


    private void initObjects() throws Exception {
        beanHand = new BeanImp( );

        traitHand = new ImpBeanLegacyProxy( new LegacyBean( "joe", 65.0 ), TraitFactory.getStore() );


        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ClassPathResource( "org/drools/chance/testImperfectFacts.drl" ), ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

        beanDrlClass = kBase.getFactType( "org.drools.chance.test", "BeanImp" );
        assertNotNull( beanDrlClass );

        Collection coll1 = kSession.getObjects( new ClassObjectFilter( beanDrlClass.getFactClass() ) );
        assertTrue( coll1 != null && ! coll1.isEmpty() && coll1.size() == 1);

        beanDrl = coll1.iterator().next();

        traitDrlClass = kBase.getFactType( "org.drools.chance.test", "ImpTrait" );
        assertNotNull( traitDrlClass );

        Collection coll2 = kSession.getObjects( new ClassObjectFilter( traitDrlClass.getFactClass() ) );
        assertTrue( coll2 != null && ! coll2.isEmpty() && coll2.size() == 1);

        traitDrl = coll2.iterator().next();


    }


    @After
    public void tearDown() throws Exception {

    }



    @Test
    public void testSetup() {

        System.out.println( "BEAN  - TARGET >>>" + beanHand );
        assertNotNull(beanHand);

        System.out.println( "TRAIT - TARGET >>>" + traitHand );
        assertNotNull(traitHand);

        System.out.println( "BEAN  - ACTUAL >>>" + beanDrl );
        assertNotNull( beanDrl );

        System.out.println( "TRAIT - ACTUAL >>>" + traitDrl );
        assertNotNull(traitDrl);


    }







    @Test
    public void testProbabilityOnString_getX() {
        checkReturnType( beanHand.getClass(), "getName", IImperfectField.class );
        checkReturnType( traitHand.getClass(), "getName", IImperfectField.class );
        checkReturnType( beanDrl.getClass(), "getName", IImperfectField.class );
        checkReturnType( traitDrl.getClass(), "getName", IImperfectField.class );

        assertNotNull( beanHand.getName() );
        assertNotNull( traitHand.getName() );
        assertNotNull( beanDrlClass.get(beanDrl, "name") );
        assertNotNull( traitDrlClass.get(traitDrl, "name") );

        System.out.println( beanHand.getName() );
        System.out.println( traitHand.getName() );
        System.out.println( beanDrlClass.get(beanDrl, "name") );
        System.out.println( traitDrlClass.get(traitDrl, "name") );

    }
    
    @Test
    public void testProbabilityOnString_getDistributionX() {
        checkReturnType( beanHand.getClass(), "getNameDistr", IDistribution.class );
        checkReturnType( traitHand.getClass(), "getNameDistr", IDistribution.class );
        checkReturnType( beanDrl.getClass(), "getNameDistr", IDistribution.class );
        checkReturnType( traitDrl.getClass(), "getNameDistr", IDistribution.class);
        
        assertNotNull( beanHand.getNameDistr() );
        assertNotNull( traitHand.getNameDistr() );
        assertNotNull( beanDrlClass.get( beanDrl, "nameDistr" ) );
        assertNotNull( traitDrlClass.get( traitDrl, "nameDistr" ) );

        System.out.println( beanHand.getNameDistr() );
        System.out.println( traitHand.getNameDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "nameDistr" ) );
        System.out.println( traitDrlClass.get(traitDrl, "nameDistr" ) );
    }



    @Test
    public void testProbabilityOnString_getValueX() {
        checkReturnType( beanHand.getClass(), "getNameValue", String.class );
        checkReturnType( traitHand.getClass(), "getNameValue", String.class );
        checkReturnType( beanDrl.getClass(), "getNameValue", String.class );
        checkReturnType( traitDrl.getClass(), "getNameValue", String.class);

        assertNotNull( beanHand.getNameValue() );
        assertNotNull( traitHand.getNameValue() );
        assertNotNull( beanDrlClass.get( beanDrl, "nameValue" ) );
        assertNotNull( traitDrlClass.get( traitDrl, "nameValue" ) );

        System.out.println( beanHand.getNameValue() );
        System.out.println( traitHand.getNameValue() );
        System.out.println( beanDrlClass.get(beanDrl, "nameValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "nameValue" ) );
    }










    private void checkReturnType( Class source, String method, Class target ) {
       try {
            assertTrue( source.getMethod(method).getReturnType().isAssignableFrom( target ) );
            assertTrue( source.getMethod(method).getReturnType().isAssignableFrom( target ) );
            assertTrue( source.getMethod(method).getReturnType().isAssignableFrom( target ) );
            assertTrue( source.getMethod(method).getReturnType().isAssignableFrom( target ) );
        } catch (NoSuchMethodException e) {
            fail( method + " missing or wrong returnType ");
        } 
    }
    


//    @Test
//    public  void testHandle() throws Exception {
//
//
//
//        System.out.println("We have a null bean");
//        Bean b = new Bean();
//
//        junit.framework.TestCase.assertNull(b.getAge());
//        junit.framework.TestCase.assertNull(b.getName());
//
//
//        System.out.println(b);
//        System.out.println("\n---------------------------");
//
//        System.out.println("Setting a handle. The handle has meta-field with prior probabilities: the bean now reflects that");
//        Bean_HandleGen bh = new Bean_HandleGen(b);
//
//
//        System.out.println(b);
//        System.out.println(bh);
//
//        junit.framework.TestCase.assertEquals(20, bh.getAgeValue().intValue());
//        junit.framework.TestCase.assertEquals("philip",bh.getFieldValue());
//
//        junit.framework.TestCase.assertEquals(20, b.getAge().intValue());
//        junit.framework.TestCase.assertEquals("philip",b.getName());
//
//
//        junit.framework.TestCase.assertEquals(50,b.getWeight(),1e-02);
//        junit.framework.TestCase.assertEquals(Weight.FAT,b.getBody());
//
//
//
//        System.out.println("---------------------------");
//        System.out.println("Now, setting/updating HANDLE values : field (name)=john, age ++=18, weight=20.0. NOT body...");
//        bh.setField("john");
//        bh.updateAge(18);
//        bh.setWeight(20.0);
//
//        junit.framework.TestCase.assertEquals(18, b.getAge().intValue());
//        junit.framework.TestCase.assertEquals("john",b.getName());
//
//        junit.framework.TestCase.assertEquals(18, bh.getAgeValue().intValue());
//        junit.framework.TestCase.assertEquals("john",bh.getFieldValue());
//
//
//
//        junit.framework.TestCase.assertEquals(20.0,b.getWeight());
//        junit.framework.TestCase.assertEquals(Weight.SLIM,b.getBody());
//        junit.framework.TestCase.assertEquals(0.8, bh.getBody().getDegree(Weight.SLIM).getValue());
//        junit.framework.TestCase.assertEquals(0.2,bh.getBody().getDegree(Weight.FAT).getValue());
//
//
//
//        System.out.println("yet, some merging was done ... (Notice body)");
//        System.out.println(b);
//        System.out.println(bh);
//
//        System.out.println("---------------------------");
//        System.out.println("Now, time to set body=FAT. linguistic. See weight... ");
//
//
//
//        bh.setBody(Weight.FAT);
//        junit.framework.TestCase.assertEquals(1.0,bh.getBody().getDegree(Weight.FAT).getValue());
//        junit.framework.TestCase.assertEquals(0.0,bh.getBody().getDegree(Weight.SLIM).getValue());
//
//        junit.framework.TestCase.assertEquals(100.0,b.getWeight(),1e-2);
//        junit.framework.TestCase.assertEquals(100.0,bh.getWeight(),1e-2);
//
//
//        System.out.println("After more changes:");
//        System.out.println(b);
//        System.out.println(bh);
//    }
//
//





}
