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
import org.drools.chance.builder.*;
import org.drools.chance.common.fact.BeanImp;
import org.drools.chance.common.trait.ImpBean;
import org.drools.chance.common.trait.ImpBeanLegacyProxy;
import org.drools.chance.common.trait.LegacyBean;
import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;
import org.drools.chance.distribution.IDistributionStrategyFactory;
import org.drools.definition.type.FactType;
import org.drools.factmodel.ClassBuilderFactory;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.*;

import java.lang.reflect.Method;
import java.util.Collection;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.*;


public class ImperfectBeanFieldTest {


    private ImpBean beanHand;

    private ImpBean traitHand;

    private Object traitDrl;
    private FactType traitDrlClass;

    private Object beanDrl;
    private FactType beanDrlClass;



    @BeforeClass
    public static void setFactories() {
        ChanceStrategyFactory.initDefaults();
        ClassBuilderFactory.setBeanClassBuilderService(new ChanceBeanBuilder() );
        ClassBuilderFactory.setTraitBuilderService( new ChanceTraitBuilder() );
        ClassBuilderFactory.setTraitProxyBuilderService( new ChanceTripleProxyBuilder() );
        ClassBuilderFactory.setPropertyWrapperBuilderService( new ChanceTriplePropertyWrapperClassBuilderImpl() );

    }

    @Before
    public void setUp() throws Exception {
        TraitFactory.reset();
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
    public void testProbabilityOnString_getName() {

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

        assertEquals( "philip", ((IImperfectField) beanHand.getName()).getCrisp() );
        assertEquals( "joe", ((IImperfectField) traitHand.getName()).getCrisp() );
        assertEquals( "philip", ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCrisp() );
        assertEquals( "joe", ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCrisp() );

    }

    @Test
    public void testProbabilityOnString_getDistributionName() {
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

        assertEquals( 0.7, ((IDistribution) beanHand.getNameDistr()).getDegree("philip").getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitHand.getNameDistr()).getDegree("joe").getValue(), 1e-16 );
        assertEquals( 0.7, ((IDistribution) beanDrlClass.get(beanDrl, "nameDistr")).getDegree("philip").getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitDrlClass.get(traitDrl, "nameDistr")).getDegree("joe").getValue(), 1e-16 );

    }



    @Test
    public void testProbabilityOnString_getValueName() {
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

        assertEquals( "philip", beanHand.getNameValue() );
        assertEquals( "joe", traitHand.getNameValue());
        assertEquals( "philip", beanDrlClass.get( beanDrl, "nameValue" ) );
        assertEquals( "joe", traitDrlClass.get(traitDrl, "nameValue" ) );
    }



    @Test
    public void testProbabilityOnString_setName() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setName", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setName", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setName", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setName", IImperfectField.class);

        IDistributionStrategies<String> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", String.class );
        IImperfectField<String> fld = new ImperfectField<String>( builder, builder.parse( "alice/0.4, bob/0.6" ) );

        beanHand.setName( fld );
        traitHand.setName( fld );
        beanDrlClass.set( beanDrl, "name", fld );
        traitDrlClass.set( traitDrl, "name", fld );

        assertSame( fld, beanHand.getName() );
        assertSame( fld, traitHand.getName() );
        assertSame( fld, beanDrlClass.get(beanDrl, "name") );
        assertSame( fld, traitDrlClass.get(traitDrl, "name") );

        System.out.println( beanHand.getName() );
        System.out.println( traitHand.getName() );
        System.out.println( beanDrlClass.get(beanDrl, "name") );
        System.out.println( traitDrlClass.get(traitDrl, "name") );

        assertEquals( "bob", ((IImperfectField) beanHand.getName()).getCrisp() );
        assertEquals( "bob", ((IImperfectField) traitHand.getName()).getCrisp() );
        assertEquals( "bob", ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCrisp() );
        assertEquals( "bob", ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCrisp() );

        assertEquals( 0.4, fld.getCurrent().getDegree( "alice" ).getValue(), 1e-16 );
    }



    @Test
    public void testProbabilityOnString_setNameDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setNameDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setNameDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setNameDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setNameDistr", IDistribution.class);

        IDistributionStrategies<String> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", String.class );

        IDistribution<String> d1 = builder.parse( "alice/0.4, bob/0.6" );
        IDistribution<String> d2 = builder.parse( "cindy/0.35, david/0.65" );
        IDistribution<String> d3 = builder.parse("earl/0.1, franz/0.9");
        IDistribution<String> d4 = builder.parse( "gary/0.8, homer/0.2" );


        beanHand.setNameDistr(d1);
        traitHand.setNameDistr(d2);
        beanDrlClass.set( beanDrl, "nameDistr", d3 );
        traitDrlClass.set( traitDrl, "nameDistr", d4 );

        System.out.println( beanHand.getNameDistr() );
        System.out.println( traitHand.getNameDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "nameDistr" ) );
        System.out.println( traitDrlClass.get(traitDrl, "nameDistr" ) );


        assertSame( d1, beanHand.getNameDistr() );
        assertSame( d2, traitHand.getNameDistr() );
        assertSame( d3, beanDrlClass.get(beanDrl, "nameDistr") );
        assertSame( d4, traitDrlClass.get(traitDrl, "nameDistr") );


        assertEquals( "bob", ((IImperfectField) beanHand.getName()).getCrisp() );
        assertEquals( "david", ((IImperfectField) traitHand.getName()).getCrisp() );
        assertEquals( "franz", ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCrisp() );
        assertEquals( "gary", ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCrisp() );

    }


    @Test
    public void testProbabilityOnString_setNameValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setNameValue", String.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setNameValue", String.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setNameValue", String.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setNameValue", String.class);

        beanHand.setNameValue( "alan" );
        traitHand.setNameValue( "bart" );
        beanDrlClass.set( beanDrl, "nameValue", "chris" );
        traitDrlClass.set( traitDrl, "nameValue", "donna" );

        System.out.println( beanHand.getNameValue() );
        System.out.println( traitHand.getNameValue() );
        System.out.println( beanDrlClass.get(beanDrl, "nameValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "nameValue" ) );

        assertEquals( "alan", ((IImperfectField) beanHand.getName()).getCrisp() );
        assertEquals( "bart", ((IImperfectField) traitHand.getName()).getCrisp() );
        assertEquals( "chris", ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCrisp() );
        assertEquals( "donna", ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCrisp() );

        assertEquals( 1.0, ((IDistribution) beanHand.getNameDistr()).getDegree("alan").getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitHand.getNameDistr()).getDegree("bart").getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) beanDrlClass.get(beanDrl, "nameDistr")).getDegree("chris").getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitDrlClass.get(traitDrl, "nameDistr")).getDegree("donna").getValue(), 1e-16 );

    }






    @Test
    public void testProbabilityOnString_updateName() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateName", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateName", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateName", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateName", IImperfectField.class);

        IDistributionStrategies<String> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", String.class );
        IImperfectField<String> fld1 = new ImperfectField<String>( builder, builder.parse( "philip/0.3, john/0.7" ) );
        IImperfectField<String> fld2 = new ImperfectField<String>( builder, builder.parse( "joe/0.5, john/0.25, james/0.25") );

        beanHand.updateName( fld1 );
        traitHand.updateName( fld2 );
        invokeUpdate( beanDrl, "name", IImperfectField.class, fld1 );
        invokeUpdate( traitDrl, "name", IImperfectField.class, fld2 );

        assertNotSame( fld1, beanHand.getName() );
        assertNotSame( fld2, traitHand.getName() );
        assertNotSame( fld1, beanDrlClass.get(beanDrl, "name") );
        assertNotSame( fld2, traitDrlClass.get(traitDrl, "name") );

        System.out.println( beanHand.getName() );
        System.out.println( traitHand.getName() );
        System.out.println( beanDrlClass.get(beanDrl, "name") );
        System.out.println( traitDrlClass.get(traitDrl, "name") );

        assertEquals( 0.5, beanHand.getName().getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
        assertEquals( 0.5, beanHand.getName().getCurrent().getDegree( "john" ).getValue(), 1e-16 );

        assertEquals( 1.0, traitHand.getName().getCurrent().getDegree( "joe" ).getValue(), 1e-16 );

        assertEquals( 0.5, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
        assertEquals( 0.5, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "john" ).getValue(), 1e-16 );

        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCurrent().getDegree( "joe" ).getValue(), 1e-16 );

    }






    @Test
    public void testProbabilityOnString_updateNameDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateNameDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateNameDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateNameDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateNameDistr", IDistribution.class);

        IDistributionStrategies<String> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", String.class );
        IDistribution<String> d1 = builder.parse( "philip/0.3, john/0.7" );
        IDistribution<String> d2 = builder.parse( "joe/0.5, john/0.25, james/0.25");

        beanHand.updateNameDistr( d1 );
        traitHand.updateNameDistr( d2 );
        invokeUpdate( beanDrl, "nameDistr", IDistribution.class, d1 );
        invokeUpdate( traitDrl, "nameDistr", IDistribution.class, d2 );

        assertNotSame( d1, beanHand.getNameDistr() );
        assertNotSame( d2, traitHand.getNameDistr() );
        assertNotSame( d1, beanDrlClass.get(beanDrl, "nameDistr") );
        assertNotSame( d2, traitDrlClass.get(traitDrl, "nameDistr") );

        System.out.println( beanHand.getNameDistr() );
        System.out.println( traitHand.getNameDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "nameDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "nameDistr") );

        assertEquals( 0.5, beanHand.getName().getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
        assertEquals( 0.5, beanHand.getName().getCurrent().getDegree( "john" ).getValue(), 1e-16 );

        assertEquals( 1.0, traitHand.getName().getCurrent().getDegree( "joe" ).getValue(), 1e-16 );

        assertEquals( 0.5, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
        assertEquals( 0.5, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "john" ).getValue(), 1e-16 );

        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCurrent().getDegree( "joe" ).getValue(), 1e-16 );

    }




    @Test
    public void testProbabilityOnString_updateNameValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateNameValue", String.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateNameValue", String.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateNameValue", String.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateNameValue", String.class);

        String s1 = "john";
        String s2 = "joe";

        beanHand.updateNameValue( s1 );
        traitHand.updateNameValue( s2 );
        invokeUpdate( beanDrl, "nameValue", String.class, s1 );
        invokeUpdate( traitDrl, "nameValue", String.class, s2 );

        assertEquals( s1, beanHand.getNameValue() );
        assertEquals( s2, traitHand.getNameValue() );
        assertEquals( s1, beanDrlClass.get(beanDrl, "nameValue") );
        assertEquals( s2, traitDrlClass.get(traitDrl, "nameValue") );

        System.out.println( beanHand.getNameValue() );
        System.out.println( traitHand.getNameValue() );
        System.out.println( beanDrlClass.get(beanDrl, "nameValue") );
        System.out.println( traitDrlClass.get(traitDrl, "nameValue") );

        assertEquals( 1.0, beanHand.getName().getCurrent().getDegree( "john" ).getValue(), 1e-16 );

        assertEquals( 1.0, traitHand.getName().getCurrent().getDegree( "joe" ).getValue(), 1e-16 );

        assertEquals( 0.0, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "john" ).getValue(), 1e-16 );

        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCurrent().getDegree( "joe" ).getValue(), 1e-16 );

    }


    
    
    
    
    
    
    
    
    
    
    






    
    
    

    @Test
    public void testProbabilityOnBoolean_getFlag() {

        checkReturnType( beanHand.getClass(), "getFlag", IImperfectField.class );
        checkReturnType( traitHand.getClass(), "getFlag", IImperfectField.class );
        checkReturnType( beanDrl.getClass(), "getFlag", IImperfectField.class );
        checkReturnType( traitDrl.getClass(), "getFlag", IImperfectField.class );

        assertNotNull( beanHand.getFlag() );
        assertNotNull( traitHand.getFlag() );
        assertNotNull( beanDrlClass.get(beanDrl, "flag") );
        assertNotNull( traitDrlClass.get(traitDrl, "flag") );

        System.out.println( beanHand.getFlag() );
        System.out.println( traitHand.getFlag() );
        System.out.println( beanDrlClass.get(beanDrl, "flag") );
        System.out.println( traitDrlClass.get(traitDrl, "flag") );

        assertEquals( true, ((IImperfectField) beanHand.getFlag()).getCrisp() );
        assertEquals( true, ((IImperfectField) traitHand.getFlag()).getCrisp() );
        assertEquals( true, ((IImperfectField) beanDrlClass.get(beanDrl, "flag")).getCrisp() );
        assertEquals( true, ((IImperfectField) traitDrlClass.get(traitDrl, "flag")).getCrisp() );

    }

    @Test
    public void testProbabilityOnBoolean_getDistributionFlag() {
        checkReturnType( beanHand.getClass(), "getFlagDistr", IDistribution.class );
        checkReturnType( traitHand.getClass(), "getFlagDistr", IDistribution.class );
        checkReturnType( beanDrl.getClass(), "getFlagDistr", IDistribution.class );
        checkReturnType( traitDrl.getClass(), "getFlagDistr", IDistribution.class);

        assertNotNull( beanHand.getFlagDistr() );
        assertNotNull( traitHand.getFlagDistr() );
        assertNotNull( beanDrlClass.get( beanDrl, "flagDistr" ) );
        assertNotNull( traitDrlClass.get( traitDrl, "flagDistr" ) );

        System.out.println( beanHand.getFlagDistr() );
        System.out.println( traitHand.getFlagDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "flagDistr" ) );
        System.out.println( traitDrlClass.get(traitDrl, "flagDistr" ) );

        assertEquals( 0.66, ((IDistribution) beanHand.getFlagDistr()).getDegree(true).getValue(), 1e-16 );
        assertEquals( 0.5, ((IDistribution) traitHand.getFlagDistr()).getDegree(true).getValue(), 1e-16 );
        assertEquals( 0.75, ((IDistribution) beanDrlClass.get(beanDrl, "flagDistr")).getDegree(true).getValue(), 1e-16 );
        assertEquals( 0.5, ((IDistribution) traitDrlClass.get(traitDrl, "flagDistr")).getDegree(true).getValue(), 1e-16 );

    }
//
//
//
//    @Test
//    public void testProbabilityOnString_getValueName() {
//        checkReturnType( beanHand.getClass(), "getNameValue", String.class );
//        checkReturnType( traitHand.getClass(), "getNameValue", String.class );
//        checkReturnType( beanDrl.getClass(), "getNameValue", String.class );
//        checkReturnType( traitDrl.getClass(), "getNameValue", String.class);
//
//        assertNotNull( beanHand.getNameValue() );
//        assertNotNull( traitHand.getNameValue() );
//        assertNotNull( beanDrlClass.get( beanDrl, "nameValue" ) );
//        assertNotNull( traitDrlClass.get( traitDrl, "nameValue" ) );
//
//        System.out.println( beanHand.getNameValue() );
//        System.out.println( traitHand.getNameValue() );
//        System.out.println( beanDrlClass.get(beanDrl, "nameValue" ) );
//        System.out.println( traitDrlClass.get(traitDrl, "nameValue" ) );
//
//        assertEquals( "philip", beanHand.getNameValue() );
//        assertEquals( "joe", traitHand.getNameValue());
//        assertEquals( "philip", beanDrlClass.get( beanDrl, "nameValue" ) );
//        assertEquals( "joe", traitDrlClass.get(traitDrl, "nameValue" ) );
//    }
//
//
//
//    @Test
//    public void testProbabilityOnString_setName() {
//
//        checkFirstAndOnlyArgType(beanHand.getClass(), "setName", IImperfectField.class);
//        checkFirstAndOnlyArgType(traitHand.getClass(), "setName", IImperfectField.class);
//        checkFirstAndOnlyArgType(beanDrl.getClass(), "setName", IImperfectField.class);
//        checkFirstAndOnlyArgType(traitDrl.getClass(), "setName", IImperfectField.class);
//
//        IDistributionStrategies<String> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", String.class );
//        IImperfectField<String> fld = new ImperfectField<String>( builder, builder.parse( "alice/0.4, bob/0.6" ) );
//
//        beanHand.setName( fld );
//        traitHand.setName( fld );
//        beanDrlClass.set( beanDrl, "name", fld );
//        traitDrlClass.set( traitDrl, "name", fld );
//
//        assertSame( fld, beanHand.getName() );
//        assertSame( fld, traitHand.getName() );
//        assertSame( fld, beanDrlClass.get(beanDrl, "name") );
//        assertSame( fld, traitDrlClass.get(traitDrl, "name") );
//
//        System.out.println( beanHand.getName() );
//        System.out.println( traitHand.getName() );
//        System.out.println( beanDrlClass.get(beanDrl, "name") );
//        System.out.println( traitDrlClass.get(traitDrl, "name") );
//
//        assertEquals( "bob", ((IImperfectField) beanHand.getName()).getCrisp() );
//        assertEquals( "bob", ((IImperfectField) traitHand.getName()).getCrisp() );
//        assertEquals( "bob", ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCrisp() );
//        assertEquals( "bob", ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCrisp() );
//
//        assertEquals( 0.4, fld.getCurrent().getDegree( "alice" ).getValue(), 1e-16 );
//    }
//
//
//
//    @Test
//    public void testProbabilityOnString_setNameDistr() {
//
//        checkFirstAndOnlyArgType(beanHand.getClass(), "setNameDistr", IDistribution.class);
//        checkFirstAndOnlyArgType(traitHand.getClass(), "setNameDistr", IDistribution.class);
//        checkFirstAndOnlyArgType(beanDrl.getClass(), "setNameDistr", IDistribution.class);
//        checkFirstAndOnlyArgType(traitDrl.getClass(), "setNameDistr", IDistribution.class);
//
//        IDistributionStrategies<String> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", String.class );
//
//        IDistribution<String> d1 = builder.parse( "alice/0.4, bob/0.6" );
//        IDistribution<String> d2 = builder.parse( "cindy/0.35, david/0.65" );
//        IDistribution<String> d3 = builder.parse("earl/0.1, franz/0.9");
//        IDistribution<String> d4 = builder.parse( "gary/0.8, homer/0.2" );
//
//
//        beanHand.setNameDistr(d1);
//        traitHand.setNameDistr(d2);
//        beanDrlClass.set( beanDrl, "nameDistr", d3 );
//        traitDrlClass.set( traitDrl, "nameDistr", d4 );
//
//        System.out.println( beanHand.getNameDistr() );
//        System.out.println( traitHand.getNameDistr() );
//        System.out.println( beanDrlClass.get(beanDrl, "nameDistr" ) );
//        System.out.println( traitDrlClass.get(traitDrl, "nameDistr" ) );
//
//
//        assertSame( d1, beanHand.getNameDistr() );
//        assertSame( d2, traitHand.getNameDistr() );
//        assertSame( d3, beanDrlClass.get(beanDrl, "nameDistr") );
//        assertSame( d4, traitDrlClass.get(traitDrl, "nameDistr") );
//
//
//        assertEquals( "bob", ((IImperfectField) beanHand.getName()).getCrisp() );
//        assertEquals( "david", ((IImperfectField) traitHand.getName()).getCrisp() );
//        assertEquals( "franz", ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCrisp() );
//        assertEquals( "gary", ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCrisp() );
//
//    }
//
//
//    @Test
//    public void testProbabilityOnString_setNameValue() {
//
//        checkFirstAndOnlyArgType(beanHand.getClass(), "setNameValue", String.class);
//        checkFirstAndOnlyArgType(traitHand.getClass(), "setNameValue", String.class);
//        checkFirstAndOnlyArgType(beanDrl.getClass(), "setNameValue", String.class);
//        checkFirstAndOnlyArgType(traitDrl.getClass(), "setNameValue", String.class);
//
//        beanHand.setNameValue( "alan" );
//        traitHand.setNameValue( "bart" );
//        beanDrlClass.set( beanDrl, "nameValue", "chris" );
//        traitDrlClass.set( traitDrl, "nameValue", "donna" );
//
//        System.out.println( beanHand.getNameValue() );
//        System.out.println( traitHand.getNameValue() );
//        System.out.println( beanDrlClass.get(beanDrl, "nameValue" ) );
//        System.out.println( traitDrlClass.get(traitDrl, "nameValue" ) );
//
//        assertEquals( "alan", ((IImperfectField) beanHand.getName()).getCrisp() );
//        assertEquals( "bart", ((IImperfectField) traitHand.getName()).getCrisp() );
//        assertEquals( "chris", ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCrisp() );
//        assertEquals( "donna", ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCrisp() );
//
//        assertEquals( 1.0, ((IDistribution) beanHand.getNameDistr()).getDegree("alan").getValue(), 1e-16 );
//        assertEquals( 1.0, ((IDistribution) traitHand.getNameDistr()).getDegree("bart").getValue(), 1e-16 );
//        assertEquals( 1.0, ((IDistribution) beanDrlClass.get(beanDrl, "nameDistr")).getDegree("chris").getValue(), 1e-16 );
//        assertEquals( 1.0, ((IDistribution) traitDrlClass.get(traitDrl, "nameDistr")).getDegree("donna").getValue(), 1e-16 );
//
//    }
//
//
//
//
//
//
//    @Test
//    public void testProbabilityOnString_updateName() {
//
//        checkFirstAndOnlyArgType(beanHand.getClass(), "updateName", IImperfectField.class);
//        checkFirstAndOnlyArgType(traitHand.getClass(), "updateName", IImperfectField.class);
//        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateName", IImperfectField.class);
//        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateName", IImperfectField.class);
//
//        IDistributionStrategies<String> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", String.class );
//        IImperfectField<String> fld1 = new ImperfectField<String>( builder, builder.parse( "philip/0.3, john/0.7" ) );
//        IImperfectField<String> fld2 = new ImperfectField<String>( builder, builder.parse( "joe/0.5, john/0.25, james/0.25") );
//
//        beanHand.updateName( fld1 );
//        traitHand.updateName( fld2 );
//        invokeUpdate( beanDrl, "name", IImperfectField.class, fld1 );
//        invokeUpdate( traitDrl, "name", IImperfectField.class, fld2 );
//
//        assertNotSame( fld1, beanHand.getName() );
//        assertNotSame( fld2, traitHand.getName() );
//        assertNotSame( fld1, beanDrlClass.get(beanDrl, "name") );
//        assertNotSame( fld2, traitDrlClass.get(traitDrl, "name") );
//
//        System.out.println( beanHand.getName() );
//        System.out.println( traitHand.getName() );
//        System.out.println( beanDrlClass.get(beanDrl, "name") );
//        System.out.println( traitDrlClass.get(traitDrl, "name") );
//
//        assertEquals( 0.5, beanHand.getName().getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
//        assertEquals( 0.5, beanHand.getName().getCurrent().getDegree( "john" ).getValue(), 1e-16 );
//
//        assertEquals( 1.0, traitHand.getName().getCurrent().getDegree( "joe" ).getValue(), 1e-16 );
//
//        assertEquals( 0.5, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
//        assertEquals( 0.5, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "john" ).getValue(), 1e-16 );
//
//        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCurrent().getDegree( "joe" ).getValue(), 1e-16 );
//
//    }
//
//
//
//
//
//
//    @Test
//    public void testProbabilityOnString_updateNameDistr() {
//
//        checkFirstAndOnlyArgType(beanHand.getClass(), "updateNameDistr", IDistribution.class);
//        checkFirstAndOnlyArgType(traitHand.getClass(), "updateNameDistr", IDistribution.class);
//        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateNameDistr", IDistribution.class);
//        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateNameDistr", IDistribution.class);
//
//        IDistributionStrategies<String> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", String.class );
//        IDistribution<String> d1 = builder.parse( "philip/0.3, john/0.7" );
//        IDistribution<String> d2 = builder.parse( "joe/0.5, john/0.25, james/0.25");
//
//        beanHand.updateNameDistr( d1 );
//        traitHand.updateNameDistr( d2 );
//        invokeUpdate( beanDrl, "nameDistr", IDistribution.class, d1 );
//        invokeUpdate( traitDrl, "nameDistr", IDistribution.class, d2 );
//
//        assertNotSame( d1, beanHand.getNameDistr() );
//        assertNotSame( d2, traitHand.getNameDistr() );
//        assertNotSame( d1, beanDrlClass.get(beanDrl, "nameDistr") );
//        assertNotSame( d2, traitDrlClass.get(traitDrl, "nameDistr") );
//
//        System.out.println( beanHand.getNameDistr() );
//        System.out.println( traitHand.getNameDistr() );
//        System.out.println( beanDrlClass.get(beanDrl, "nameDistr") );
//        System.out.println( traitDrlClass.get(traitDrl, "nameDistr") );
//
//        assertEquals( 0.5, beanHand.getName().getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
//        assertEquals( 0.5, beanHand.getName().getCurrent().getDegree( "john" ).getValue(), 1e-16 );
//
//        assertEquals( 1.0, traitHand.getName().getCurrent().getDegree( "joe" ).getValue(), 1e-16 );
//
//        assertEquals( 0.5, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
//        assertEquals( 0.5, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "john" ).getValue(), 1e-16 );
//
//        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCurrent().getDegree( "joe" ).getValue(), 1e-16 );
//
//    }
//
//
//
//
//    @Test
//    public void testProbabilityOnString_updateNameValue() {
//
//        checkFirstAndOnlyArgType(beanHand.getClass(), "updateNameValue", String.class);
//        checkFirstAndOnlyArgType(traitHand.getClass(), "updateNameValue", String.class);
//        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateNameValue", String.class);
//        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateNameValue", String.class);
//
//        String s1 = "john";
//        String s2 = "joe";
//
//        beanHand.updateNameValue( s1 );
//        traitHand.updateNameValue( s2 );
//        invokeUpdate( beanDrl, "nameValue", String.class, s1 );
//        invokeUpdate( traitDrl, "nameValue", String.class, s2 );
//
//        assertEquals( s1, beanHand.getNameValue() );
//        assertEquals( s2, traitHand.getNameValue() );
//        assertEquals( s1, beanDrlClass.get(beanDrl, "nameValue") );
//        assertEquals( s2, traitDrlClass.get(traitDrl, "nameValue") );
//
//        System.out.println( beanHand.getNameValue() );
//        System.out.println( traitHand.getNameValue() );
//        System.out.println( beanDrlClass.get(beanDrl, "nameValue") );
//        System.out.println( traitDrlClass.get(traitDrl, "nameValue") );
//
//        assertEquals( 1.0, beanHand.getName().getCurrent().getDegree( "john" ).getValue(), 1e-16 );
//
//        assertEquals( 1.0, traitHand.getName().getCurrent().getDegree( "joe" ).getValue(), 1e-16 );
//
//        assertEquals( 0.0, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "philip" ).getValue(), 1e-16 );
//        assertEquals( 1.0, ((IImperfectField) beanDrlClass.get(beanDrl, "name")).getCurrent().getDegree( "john" ).getValue(), 1e-16 );
//
//        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get(traitDrl, "name")).getCurrent().getDegree( "joe" ).getValue(), 1e-16 );
//
//    }

































    
    
    
    
    
    
    
    
    
    

    private void invokeUpdate(Object bean, String name, Class argClass, Object arg) {
        try {
            bean.getClass().getMethod( "update" + name.substring(0,1).toUpperCase() + name.substring(1),
                    argClass ).invoke( bean, arg );
        } catch( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }


    private void checkReturnType( Class source, String method, Class target ) {
        Class ret = null;
        try {
            ret = source.getMethod(method).getReturnType();
            assertTrue(ret.isAssignableFrom(target));
        } catch (NoSuchMethodException e) {
            fail( method + " missing or wrong returnType, expected " + target + " - found " + ret );
        }
    }

    private void checkFirstAndOnlyArgType( Class source, String method, Class target ) {
        Class[] args = null;
        try {
            Method m = source.getMethod(method, target);
            assertNotNull( m );
            args = m.getParameterTypes();
            assertTrue( args.length == 1 );
            assertTrue(args[0].equals(target));
            assertEquals(void.class, m.getReturnType());
        } catch ( NoSuchMethodException e ) {
            fail( method + " missing or wrong argType, expected " + target + " - found " + args[0] );
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
