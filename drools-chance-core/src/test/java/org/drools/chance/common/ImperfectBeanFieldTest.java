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
import org.drools.chance.common.fact.Price;
import org.drools.chance.common.fact.Weight;
import org.drools.chance.common.trait.ImpBean;
import org.drools.chance.common.trait.ImpBeanLegacyProxy;
import org.drools.chance.common.trait.LegacyBean;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;
import org.drools.chance.distribution.fuzzy.linguistic.LinguisticImperfectField;
import org.drools.core.util.Iterator;
import org.drools.core.util.Triple;
import org.drools.core.util.TripleImpl;
import org.drools.core.util.TripleStore;
import org.drools.definition.type.FactType;
import org.drools.factmodel.ClassBuilderFactory;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.Variable;
import org.junit.*;

import java.lang.reflect.Method;
import java.util.Collection;

import static junit.framework.Assert.*;


public class ImperfectBeanFieldTest {


    private ImpBean beanHand;

    private ImpBean traitHand;

    private Object traitDrl;
    private FactType traitDrlClass;

    private Object beanDrl;
    private FactType beanDrlClass;


    private Class javaCheese = ImpBean.Cheese.class;
    private ImpBean.Cheese javaCheddar = new ImpBean.Cheese("cheddar");
    private FactType drlCheese = null;
    private Object drlCheddar = null;



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
        //TODO
        TraitFactory.clearStore();
        initObjects();
    }


    private void initObjects() throws Exception {
        TripleStore store = TraitFactory.getStore();

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

//        logStore( store );

        beanDrlClass = kBase.getFactType( "org.drools.chance.test", "BeanImp" );
        assertNotNull( beanDrlClass );

        Collection coll1 = kSession.getObjects( new ClassObjectFilter( beanDrlClass.getFactClass() ) );
        assertTrue( coll1 != null && ! coll1.isEmpty() && coll1.size() == 1);

        beanDrl = coll1.iterator().next();

        traitDrlClass = kBase.getFactType( "org.drools.chance.test", "ImpTrait" );
        assertNotNull( traitDrlClass );

        Collection<Object> coll2 = kSession.getObjects(new ClassObjectFilter(traitDrlClass.getFactClass()));
        assertTrue( coll2 != null && ! coll2.isEmpty() && coll2.size() == 1);

        traitDrl = coll2.iterator().next();


        FactType geez = kBase.getFactType( "org.drools.chance.test", "Cheese" );
        drlCheese = geez;
        drlCheddar = geez.newInstance();
        geez.set( drlCheddar, "name", "cheddar" );
        assertNotNull( drlCheese );



        assertEquals( 28, store.size() );

    }

    private void logStore(TripleStore store) {
        Collection c = store.getAll( new TripleImpl( Variable.v, Variable.v, Variable.v ) );
        for ( Object t : c ) {
            Triple tri = (Triple) t;

            if ( ! (((Triple) t).getInstance() instanceof LegacyBean )) {
                System.out.println( ">> " + t );
            }
        }
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

        checkConsistency();
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

        checkConsistency();
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

        checkConsistency();
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

        checkConsistency();
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

        checkConsistency();
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

        checkConsistency();
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
        invokeUpdate( beanDrl, "name", fld1, IImperfectField.class );
        invokeUpdate( traitDrl, "name", fld2, IImperfectField.class );

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

        checkConsistency();

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
        invokeUpdate( beanDrl, "nameDistr", d1, IDistribution.class );
        invokeUpdate( traitDrl, "nameDistr", d2, IDistribution.class );

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

        checkConsistency();

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
        invokeUpdate( beanDrl, "nameValue", s1 );
        invokeUpdate( traitDrl, "nameValue", s2 );

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

        checkConsistency();

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

        checkConsistency();

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

        checkConsistency();
    }


    @Test
    public void testProbabilityOnBoolean_getValueFlag() {
        checkReturnType( beanHand.getClass(), "getFlagValue", Boolean.class );
        checkReturnType( traitHand.getClass(), "getFlagValue", Boolean.class );
        checkReturnType( beanDrl.getClass(), "getFlagValue", Boolean.class );
        checkReturnType( traitDrl.getClass(), "getFlagValue", Boolean.class);

        assertNotNull( beanHand.getFlagValue() );
        assertNotNull( traitHand.getFlagValue() );
        assertNotNull( beanDrlClass.get( beanDrl, "flagValue" ) );
        assertNotNull( traitDrlClass.get( traitDrl, "flagValue" ) );

        System.out.println( beanHand.getFlagValue() );
        System.out.println( traitHand.getFlagValue() );
        System.out.println( beanDrlClass.get(beanDrl, "flagValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "flagValue" ) );

        assertEquals( Boolean.TRUE, beanHand.getFlagValue() );
        assertEquals( Boolean.TRUE, traitHand.getFlagValue());
        assertEquals( Boolean.TRUE, beanDrlClass.get( beanDrl, "flagValue" ) );
        assertEquals( Boolean.TRUE, traitDrlClass.get(traitDrl, "flagValue" ) );

        checkConsistency();
    }



    @Test
    public void testProbabilityOnBoolean_setFlag() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setFlag", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setFlag", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setFlag", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setFlag", IImperfectField.class);

        IDistributionStrategies<Boolean> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", Boolean.class );
        IImperfectField<Boolean> fld = new ImperfectField<Boolean>( builder, builder.parse( "true/0.83, bob/0.17" ) );

        beanHand.setFlag( fld );
        traitHand.setFlag( fld );
        beanDrlClass.set( beanDrl, "flag", fld );
        traitDrlClass.set( traitDrl, "flag", fld );

        assertSame( fld, beanHand.getFlag() );
        assertSame( fld, traitHand.getFlag() );
        assertSame( fld, beanDrlClass.get(beanDrl, "flag") );
        assertSame( fld, traitDrlClass.get(traitDrl, "flag") );

        System.out.println( beanHand.getFlag() );
        System.out.println( traitHand.getFlag() );
        System.out.println( beanDrlClass.get(beanDrl, "flag") );
        System.out.println( traitDrlClass.get(traitDrl, "flag") );

        assertEquals( Boolean.TRUE, ((IImperfectField) beanHand.getFlag()).getCrisp() );
        assertEquals( Boolean.TRUE, ((IImperfectField) traitHand.getFlag()).getCrisp() );
        assertEquals( Boolean.TRUE, ((IImperfectField) beanDrlClass.get(beanDrl, "flag")).getCrisp() );
        assertEquals( Boolean.TRUE, ((IImperfectField) traitDrlClass.get(traitDrl, "flag")).getCrisp() );

        assertEquals( 0.83, fld.getCurrent().getDegree( Boolean.TRUE ).getValue(), 1e-16 );

        checkConsistency();
    }

    //
//
    @Test
    public void testProbabilityOnBoolean_setFlagDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setFlagDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setFlagDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setFlagDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setFlagDistr", IDistribution.class);

        IDistributionStrategies<Boolean> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", Boolean.class );

        IDistribution<Boolean> d1 = builder.parse( "true/0.4, false/0.6" );
        IDistribution<Boolean> d2 = builder.parse( "true/0.35, false/0.65" );
        IDistribution<Boolean> d3 = builder.parse("true/0.1, false/0.9");
        IDistribution<Boolean> d4 = builder.parse( "true/0.8, false/0.2" );


        beanHand.setFlagDistr(d1);
        traitHand.setFlagDistr(d2);
        beanDrlClass.set( beanDrl, "flagDistr", d3 );
        traitDrlClass.set( traitDrl, "flagDistr", d4 );

        System.out.println( beanHand.getFlagDistr() );
        System.out.println( traitHand.getFlagDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "flagDistr" ) );
        System.out.println( traitDrlClass.get(traitDrl, "flagDistr" ) );


        assertSame( d1, beanHand.getFlagDistr() );
        assertSame( d2, traitHand.getFlagDistr() );
        assertSame( d3, beanDrlClass.get(beanDrl, "flagDistr") );
        assertSame( d4, traitDrlClass.get(traitDrl, "flagDistr") );


        assertEquals( Boolean.FALSE, ((IImperfectField) beanHand.getFlag()).getCrisp() );
        assertEquals( Boolean.FALSE, ((IImperfectField) traitHand.getFlag()).getCrisp() );
        assertEquals( Boolean.FALSE, ((IImperfectField) beanDrlClass.get(beanDrl, "flag")).getCrisp() );
        assertEquals( Boolean.TRUE, ((IImperfectField) traitDrlClass.get(traitDrl, "flag")).getCrisp() );

        checkConsistency();
    }


    @Test
    public void testProbabilityOnBoolean_setFlagValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setFlagValue", Boolean.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setFlagValue", Boolean.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setFlagValue", Boolean.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setFlagValue", Boolean.class);

        beanHand.setFlagValue( Boolean.TRUE );
        traitHand.setFlagValue( Boolean.FALSE );
        beanDrlClass.set( beanDrl, "flagValue", Boolean.TRUE );
        traitDrlClass.set( traitDrl, "flagValue", Boolean.FALSE );

        System.out.println( beanHand.getFlagValue() );
        System.out.println( traitHand.getFlagValue() );
        System.out.println( beanDrlClass.get(beanDrl, "flagValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "flagValue" ) );

        assertEquals( Boolean.TRUE, ((IImperfectField) beanHand.getFlag()).getCrisp() );
        assertEquals( Boolean.FALSE, ((IImperfectField) traitHand.getFlag()).getCrisp() );
        assertEquals( Boolean.TRUE, ((IImperfectField) beanDrlClass.get(beanDrl, "flag")).getCrisp() );
        assertEquals( Boolean.FALSE, ((IImperfectField) traitDrlClass.get(traitDrl, "flag")).getCrisp() );

        assertEquals( 1.0, ((IDistribution) beanHand.getFlagDistr()).getDegree( Boolean.TRUE ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitHand.getFlagDistr()).getDegree( Boolean.FALSE ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) beanDrlClass.get(beanDrl, "flagDistr")).getDegree( Boolean.TRUE ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitDrlClass.get(traitDrl, "flagDistr")).getDegree( Boolean.FALSE ).getValue(), 1e-16 );

        checkConsistency();
    }


    @Test
    public void testProbabilityOnBoolean_updateFlag() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateFlag", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateFlag", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateFlag", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateFlag", IImperfectField.class);

        IDistributionStrategies<Boolean> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", Boolean.class );
        IImperfectField<Boolean> fld1 = new ImperfectField<Boolean>( builder, builder.parse( "true/0.3, false/0.7" ) );
        IImperfectField<Boolean> fld2 = new ImperfectField<Boolean>( builder, builder.parse( "true/0.75, false/0.25" ) );

        beanHand.updateFlag( fld1 );
        traitHand.updateFlag( fld2 );
        invokeUpdate( beanDrl, "flag", fld1, IImperfectField.class );
        invokeUpdate( traitDrl, "flag", fld2, IImperfectField.class );

        assertNotSame( fld1, beanHand.getFlag() );
        assertNotSame( fld2, traitHand.getFlag() );
        assertNotSame( fld1, beanDrlClass.get(beanDrl, "flag") );
        assertNotSame( fld2, traitDrlClass.get(traitDrl, "flag") );

        System.out.println( beanHand.getFlag() );
        System.out.println( traitHand.getFlag() );
        System.out.println( beanDrlClass.get(beanDrl, "flag") );
        System.out.println( traitDrlClass.get(traitDrl, "flag") );

        assertEquals( 0.45, beanHand.getFlag().getCurrent().getDegree( true ).getValue(), 1e-2 );
        assertEquals( 0.55, beanHand.getFlag().getCurrent().getDegree( false ).getValue(), 1e-2 );

        assertEquals( 0.75, traitHand.getFlag().getCurrent().getDegree( true ).getValue(), 1e-16 );

        assertEquals( 0.5625, ((IImperfectField) beanDrlClass.get(beanDrl, "flag" )).getCurrent().getDegree( true ).getValue(), 1e-16 );
        assertEquals( 0.4375, ((IImperfectField) beanDrlClass.get(beanDrl, "flag" )).getCurrent().getDegree( false ).getValue(), 1e-16 );

        assertEquals( 0.75, ((IImperfectField) traitDrlClass.get(traitDrl, "flag")).getCurrent().getDegree( true ).getValue(), 1e-16 );

        checkConsistency();
    }


    @Test
    public void testProbabilityOnBoolean_updateFlagDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateFlagDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateFlagDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateFlagDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateFlagDistr", IDistribution.class);

        IDistributionStrategies<Boolean> builder = ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", Boolean.class );
        IDistribution<Boolean> d1 = new ImperfectField<Boolean>( builder, builder.parse( "true/0.3, false/0.7" ) ).getCurrent();
        IDistribution<Boolean> d2 = new ImperfectField<Boolean>( builder, builder.parse( "true/0.75, false/0.25" ) ).getCurrent();

        beanHand.updateFlagDistr( d1 );
        traitHand.updateFlagDistr( d2 );
        invokeUpdate( beanDrl, "flagDistr", d1, IDistribution.class );
        invokeUpdate( traitDrl, "flagDistr", d2, IDistribution.class );

        assertNotSame( d1, beanHand.getFlagDistr() );
        assertNotSame( d2, traitHand.getFlagDistr() );
        assertNotSame( d1, beanDrlClass.get(beanDrl, "flagDistr") );
        assertNotSame( d2, traitDrlClass.get(traitDrl, "flagDistr") );

        System.out.println( beanHand.getFlagDistr() );
        System.out.println( traitHand.getFlagDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "flagDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "flagDistr") );

        assertEquals( 0.45, beanHand.getFlag().getCurrent().getDegree( true ).getValue(), 1e-2 );
        assertEquals( 0.55, beanHand.getFlag().getCurrent().getDegree( false ).getValue(), 1e-2 );

        assertEquals( 0.75, traitHand.getFlag().getCurrent().getDegree( true ).getValue(), 1e-16 );

        assertEquals( 0.5625, ((IImperfectField) beanDrlClass.get(beanDrl, "flag" )).getCurrent().getDegree( true ).getValue(), 1e-16 );
        assertEquals( 0.4375, ((IImperfectField) beanDrlClass.get(beanDrl, "flag" )).getCurrent().getDegree( false ).getValue(), 1e-16 );

        assertEquals( 0.75, ((IImperfectField) traitDrlClass.get(traitDrl, "flag")).getCurrent().getDegree( true ).getValue(), 1e-16 );

        checkConsistency();
    }




    @Test
    public void testProbabilityOnBoolean_updateFlagValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateFlagValue", Boolean.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateFlagValue", Boolean.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateFlagValue", Boolean.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateFlagValue", Boolean.class);

        Boolean s1 = true;
        Boolean s2 = false;

        beanHand.updateFlagValue( s1 );
        traitHand.updateFlagValue( s2 );
        invokeUpdate( beanDrl, "flagValue", s1 );
        invokeUpdate( traitDrl, "flagValue", s2 );

        assertEquals( s1, beanHand.getFlagValue() );
        assertEquals( s2, traitHand.getFlagValue() );
        assertEquals( s1, beanDrlClass.get(beanDrl, "flagValue") );
        assertEquals( s2, traitDrlClass.get(traitDrl, "flagValue") );

        System.out.println( beanHand.getFlagValue() );
        System.out.println( traitHand.getFlagValue() );
        System.out.println( beanDrlClass.get(beanDrl, "flagValue") );
        System.out.println( traitDrlClass.get(traitDrl, "flagValue") );

        assertEquals( 1.0, beanHand.getFlag().getCurrent().getDegree( true ).getValue(), 1e-16 );

        assertEquals( 1.0, traitHand.getFlag().getCurrent().getDegree( false ).getValue(), 1e-16 );

        assertEquals( 1.0, ((IImperfectField) beanDrlClass.get(beanDrl, "flag")).getCurrent().getDegree( true ).getValue(), 1e-16 );
        assertEquals( 0.0, ((IImperfectField) beanDrlClass.get(beanDrl, "flag")).getCurrent().getDegree( false ).getValue(), 1e-16 );

        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get(traitDrl, "flag")).getCurrent().getDegree( false ).getValue(), 1e-16 );

        checkConsistency();
    }










    @Test
    public void testProbabilityOnInteger_getAge() {

        checkReturnType( beanHand.getClass(), "getAge", IImperfectField.class );
        checkReturnType( traitHand.getClass(), "getAge", IImperfectField.class );
        checkReturnType( beanDrl.getClass(), "getAge", IImperfectField.class );
        checkReturnType( traitDrl.getClass(), "getAge", IImperfectField.class );

        assertNotNull( beanHand.getAge() );
        assertNotNull( traitHand.getAge() );
        assertNotNull( beanDrlClass.get(beanDrl, "age") );
        assertNotNull( traitDrlClass.get(traitDrl, "age") );

        System.out.println( beanHand.getAge() );
        System.out.println( traitHand.getAge() );
        System.out.println( beanDrlClass.get(beanDrl, "age") );
        System.out.println( traitDrlClass.get(traitDrl, "age") );

        assertEquals( 20, ((IImperfectField) beanHand.getAge()).getCrisp() );
        assertEquals( null, ((IImperfectField) traitHand.getAge()).getCrisp() );
        assertEquals( 20, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCrisp() );
        assertEquals( null, ((IImperfectField) traitDrlClass.get(traitDrl, "age")).getCrisp() );

        checkConsistency();
    }

    @Test
    public void testProbabilityOnInteger_getDistributionAge() {
        checkReturnType( beanHand.getClass(), "getAgeDistr", IDistribution.class );
        checkReturnType( traitHand.getClass(), "getAgeDistr", IDistribution.class );
        checkReturnType( beanDrl.getClass(), "getAgeDistr", IDistribution.class );
        checkReturnType( traitDrl.getClass(), "getAgeDistr", IDistribution.class);

        assertNotNull( beanHand.getAgeDistr() );
        assertNotNull( traitHand.getAgeDistr() );
        assertNotNull( beanDrlClass.get( beanDrl, "ageDistr" ) );
        assertNotNull( traitDrlClass.get( traitDrl, "ageDistr" ) );

        System.out.println( beanHand.getAgeDistr() );
        System.out.println( traitHand.getAgeDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "ageDistr" ) );
        System.out.println( traitDrlClass.get(traitDrl, "ageDistr" ) );

        assertEquals( 0.15, ((IDistribution) beanHand.getAgeDistr()).getDegree(19).getValue(), 1e-2 );
        assertEquals( 0.0, ((IDistribution) traitHand.getAgeDistr()).getDegree(23).getValue(), 1e-16 );
        assertEquals( 0.3, ((IDistribution) beanDrlClass.get(beanDrl, "ageDistr")).getDegree(18).getValue(), 2e-2 );
        assertEquals( 0.0, ((IDistribution) traitDrlClass.get(traitDrl, "ageDistr")).getDegree(23).getValue(), 1e-16 );

        checkConsistency();
    }



    @Test
    public void testProbabilityOnInteger_getValueAge() {
        checkReturnType( beanHand.getClass(), "getAgeValue", Integer.class );
        checkReturnType( traitHand.getClass(), "getAgeValue", Integer.class );
        checkReturnType( beanDrl.getClass(), "getAgeValue", Integer.class );
        checkReturnType( traitDrl.getClass(), "getAgeValue", Integer.class);

        assertNotNull( beanHand.getAgeValue() );
        assertNull( traitHand.getAgeValue() );
        assertNotNull( beanDrlClass.get( beanDrl, "ageValue" ) );
        assertNull( traitDrlClass.get( traitDrl, "ageValue" ) );

        System.out.println( beanHand.getAgeValue() );
        System.out.println( traitHand.getAgeValue() );
        System.out.println( beanDrlClass.get(beanDrl, "ageValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "ageValue" ) );

        assertEquals( Integer.valueOf( 20 ), beanHand.getAgeValue() );
        assertEquals( null, traitHand.getAgeValue());
        assertEquals( Integer.valueOf( 20 ), beanDrlClass.get( beanDrl, "ageValue" ) );
        assertEquals( null, traitDrlClass.get(traitDrl, "ageValue" ) );

        checkConsistency();
    }



    @Test
    public void testProbabilityOnInteger_setAge() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setAge", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setAge", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setAge", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setAge", IImperfectField.class);

        IDistributionStrategies<Integer> builder = ChanceStrategyFactory.buildStrategies( "probability", "dirichlet", "simple", Integer.class );
        IImperfectField<Integer> fld = new ImperfectField<Integer>( builder, builder.parse( "18/0.4, 20/0.6" ) );

        beanHand.setAge( fld );
        traitHand.setAge( fld );
        beanDrlClass.set( beanDrl, "age", fld );
        traitDrlClass.set( traitDrl, "age", fld );

        assertSame( fld, beanHand.getAge() );
        assertSame( fld, traitHand.getAge() );
        assertSame( fld, beanDrlClass.get(beanDrl, "age") );
        assertSame( fld, traitDrlClass.get(traitDrl, "age") );

        System.out.println( beanHand.getAge() );
        System.out.println( traitHand.getAge() );
        System.out.println( beanDrlClass.get(beanDrl, "age") );
        System.out.println( traitDrlClass.get(traitDrl, "age") );

        assertEquals( 20, ((IImperfectField) beanHand.getAge()).getCrisp() );
        assertEquals( 20, ((IImperfectField) traitHand.getAge()).getCrisp() );
        assertEquals( 20, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCrisp() );
        assertEquals( 20, ((IImperfectField) traitDrlClass.get(traitDrl, "age")).getCrisp() );

        assertEquals( 0.4, fld.getCurrent().getDegree( 18 ).getValue(), 1e-16 );
        assertEquals( 0.6, fld.getCurrent().getDegree( 20 ).getValue(), 1e-16 );
        assertEquals( 0.0, fld.getCurrent().getDegree( 19 ).getValue(), 1e-16 );

        checkConsistency();
    }



    @Test
    public void testProbabilityOnInteger_setAgeDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setAgeDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setAgeDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setAgeDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setAgeDistr", IDistribution.class);

        IDistributionStrategies<Integer> builder = ChanceStrategyFactory.buildStrategies( "probability", "dirichlet", "simple", Integer.class );

        IDistribution<Integer> d1 = builder.parse( "18/0.4, 20/0.6" );
        IDistribution<Integer> d2 = builder.parse( "19/0.35, 21/0.65" );
        IDistribution<Integer> d3 = builder.parse("20/0.1, 22/0.9");
        IDistribution<Integer> d4 = builder.parse( "21/0.8, 23/0.2" );


        beanHand.setAgeDistr(d1);
        traitHand.setAgeDistr(d2);
        beanDrlClass.set( beanDrl, "ageDistr", d3 );
        traitDrlClass.set( traitDrl, "ageDistr", d4 );

        System.out.println( beanHand.getAgeDistr() );
        System.out.println( traitHand.getAgeDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "ageDistr" ) );
        System.out.println( traitDrlClass.get(traitDrl, "ageDistr" ) );


        assertSame( d1, beanHand.getAgeDistr() );
        assertSame( d2, traitHand.getAgeDistr() );
        assertSame( d3, beanDrlClass.get(beanDrl, "ageDistr") );
        assertSame( d4, traitDrlClass.get(traitDrl, "ageDistr") );


        assertEquals( 20, ((IImperfectField) beanHand.getAge()).getCrisp() );
        assertEquals( 21, ((IImperfectField) traitHand.getAge()).getCrisp() );
        assertEquals( 22, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCrisp() );
        assertEquals( 21, ((IImperfectField) traitDrlClass.get(traitDrl, "age")).getCrisp() );

        checkConsistency();
    }


    @Test
    public void testProbabilityOnInteger_setAgeValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setAgeValue", Integer.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setAgeValue", Integer.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setAgeValue", Integer.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setAgeValue", Integer.class);

        beanHand.setAgeValue( 33 );
        traitHand.setAgeValue( 26 );
        beanDrlClass.set( beanDrl, "ageValue", 14 );
        traitDrlClass.set( traitDrl, "ageValue", 18 );

        System.out.println( beanHand.getAgeValue() );
        System.out.println( traitHand.getAgeValue() );
        System.out.println( beanDrlClass.get(beanDrl, "ageValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "ageValue" ) );

        assertEquals( 33, ((IImperfectField) beanHand.getAge()).getCrisp() );
        assertEquals( 26, ((IImperfectField) traitHand.getAge()).getCrisp() );
        assertEquals( 14, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCrisp() );
        assertEquals( 18, ((IImperfectField) traitDrlClass.get(traitDrl, "age")).getCrisp() );

        assertEquals( 1.0, ((IDistribution) beanHand.getAgeDistr()).getDegree( 33 ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitHand.getAgeDistr()).getDegree( 26 ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) beanDrlClass.get(beanDrl, "ageDistr")).getDegree( 14 ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitDrlClass.get(traitDrl, "ageDistr")).getDegree( 18 ).getValue(), 1e-16 );

        checkConsistency();
    }






    @Test
    public void testProbabilityOnInteger_updateAge() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateAge", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateAge", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateAge", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateAge", IImperfectField.class);

        IDistributionStrategies<Integer> builder = ChanceStrategyFactory.buildStrategies( "probability", "dirichlet", "simple", Integer.class );
        IImperfectField<Integer> fld1 = new ImperfectField<Integer>( builder, builder.parse( "18/0.3, 19/0.7" ) );
        IImperfectField<Integer> fld2 = new ImperfectField<Integer>( builder, builder.parse( "16/0.5, 19/0.25, 23/0.25") );

        beanHand.updateAge( fld1 );
        traitHand.updateAge( fld2 );
        invokeUpdate( beanDrl, "age", fld1,IImperfectField.class );
        invokeUpdate( traitDrl, "age", fld2, IImperfectField.class );

        assertNotSame( fld1, beanHand.getAge() );
        assertNotSame( fld2, traitHand.getAge() );
        assertNotSame( fld1, beanDrlClass.get(beanDrl, "age") );
        assertNotSame( fld2, traitDrlClass.get(traitDrl, "age") );

        System.out.println( beanHand.getAge() );
        System.out.println( traitHand.getAge() );
        System.out.println( beanDrlClass.get(beanDrl, "age") );
        System.out.println( traitDrlClass.get(traitDrl, "age") );

        assertEquals( 0.66, beanHand.getAge().getCurrent().getDegree( 19 ).getValue(), 1e-2 );
        assertEquals( 0.03, beanHand.getAge().getCurrent().getDegree( 20 ).getValue(), 1e-2 );

        assertEquals( 0.25, traitHand.getAge().getCurrent().getDegree( 23 ).getValue(), 1e-2 );

        assertEquals( 0.66, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCurrent().getDegree( 19 ).getValue(), 1e-2 );
        assertEquals( 0.30, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCurrent().getDegree( 18 ).getValue(), 1e-2 );

        assertEquals( 0.25, ((IImperfectField) traitDrlClass.get(traitDrl, "age")).getCurrent().getDegree( 23 ).getValue(), 1e-2 );

        checkConsistency();
    }






    @Test
    public void testProbabilityOnInteger_updateAgeDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateAgeDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateAgeDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateAgeDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateAgeDistr", IDistribution.class);

        IDistributionStrategies<Integer> builder = ChanceStrategyFactory.buildStrategies( "probability", "dirichlet", "simple", Integer.class );
        IDistribution<Integer> d1 = builder.parse( "18/0.3, 19/0.7" );
        IDistribution<Integer> d2 = builder.parse( "16/0.5, 19/0.25, 23/0.25");

        beanHand.updateAgeDistr( d1 );
        traitHand.updateAgeDistr( d2 );
        invokeUpdate( beanDrl, "ageDistr", d1, IDistribution.class );
        invokeUpdate( traitDrl, "ageDistr", d2, IDistribution.class );

        assertNotSame( d1, beanHand.getAgeDistr() );
        assertNotSame( d2, traitHand.getAgeDistr() );
        assertNotSame( d1, beanDrlClass.get(beanDrl, "ageDistr") );
        assertNotSame( d2, traitDrlClass.get(traitDrl, "ageDistr") );

        System.out.println( beanHand.getAgeDistr() );
        System.out.println( traitHand.getAgeDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "ageDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "ageDistr") );

        assertEquals( 0.66, beanHand.getAge().getCurrent().getDegree( 19 ).getValue(), 1e-2 );
        assertEquals( 0.03, beanHand.getAge().getCurrent().getDegree( 20 ).getValue(), 1e-2 );

        assertEquals( 0.25, traitHand.getAge().getCurrent().getDegree( 23 ).getValue(), 1e-2 );

        assertEquals( 0.66, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCurrent().getDegree( 19 ).getValue(), 1e-2 );
        assertEquals( 0.30, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCurrent().getDegree( 18 ).getValue(), 1e-2 );

        assertEquals( 0.25, ((IImperfectField) traitDrlClass.get(traitDrl, "age")).getCurrent().getDegree( 23 ).getValue(), 1e-2 );

        checkConsistency();
    }




    @Test
    public void testProbabilityOnInteger_updateAgeValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateAgeValue", Integer.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateAgeValue", Integer.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateAgeValue", Integer.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateAgeValue", Integer.class);

        Integer s1 = 19;
        Integer s2 = 23;

        beanHand.updateAgeValue( s1 );
        traitHand.updateAgeValue( s2 );
        invokeUpdate( beanDrl, "ageValue", s1 );
        invokeUpdate( traitDrl, "ageValue", s2 );

        assertEquals( s1, beanHand.getAgeValue() );
        assertEquals( s2, traitHand.getAgeValue() );
        assertEquals( s1, beanDrlClass.get(beanDrl, "ageValue") );
        assertEquals( s2, traitDrlClass.get(traitDrl, "ageValue") );

        System.out.println( beanHand.getAgeValue() );
        System.out.println( traitHand.getAgeValue() );
        System.out.println( beanDrlClass.get(beanDrl, "ageValue") );
        System.out.println( traitDrlClass.get(traitDrl, "ageValue") );

        assertEquals( 0.94, beanHand.getAge().getCurrent().getDegree( 19 ).getValue(), 1e-2 );

        assertEquals( 1.0, traitHand.getAge().getCurrent().getDegree( 23 ).getValue(), 1e-16 );

        assertEquals( 0.94, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCurrent().getDegree( 19 ).getValue(), 1e-2 );
        assertEquals( 0.02, ((IImperfectField) beanDrlClass.get(beanDrl, "age")).getCurrent().getDegree( 18 ).getValue(), 1e-2 );

        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get(traitDrl, "age")).getCurrent().getDegree( 23 ).getValue(), 1e-16 );

        checkConsistency();
    }


















    private Object newDrlCheese( String name ) {
        try {
            Object c = drlCheese.newInstance();
            drlCheese.set( c, "name", name );
            return c;
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
        return null;
    }


    @Test
    public void testProbabilityOnCheese_getLikes() {

        checkReturnType( beanHand.getClass(), "getLikes", IImperfectField.class );
        checkReturnType( traitHand.getClass(), "getLikes", IImperfectField.class );
        checkReturnType( beanDrl.getClass(), "getLikes", IImperfectField.class );
        checkReturnType( traitDrl.getClass(), "getLikes", IImperfectField.class );

        assertNotNull( beanHand.getLikes() );
        assertNotNull( traitHand.getLikes() );
        assertNotNull( beanDrlClass.get(beanDrl, "likes") );
        assertNotNull( traitDrlClass.get(traitDrl, "likes") );

        System.out.println( beanHand.getLikes() );
        System.out.println( traitHand.getLikes() );
        System.out.println( beanDrlClass.get(beanDrl, "likes") );
        System.out.println( traitDrlClass.get(traitDrl, "likes") );

        assertEquals( javaCheddar, ((IImperfectField) beanHand.getLikes()).getCrisp() );
        assertEquals( null, ((IImperfectField) traitHand.getLikes()).getCrisp() );
        assertEquals( drlCheddar, ((IImperfectField) beanDrlClass.get(beanDrl, "likes")).getCrisp() );
        assertEquals( null, ((IImperfectField) traitDrlClass.get(traitDrl, "likes")).getCrisp() );

        checkConsistency();
    }

    @Test
    public void testProbabilityOnCheese_getDistributionLikes() {
        checkReturnType( beanHand.getClass(), "getLikesDistr", IDistribution.class );
        checkReturnType( traitHand.getClass(), "getLikesDistr", IDistribution.class );
        checkReturnType( beanDrl.getClass(), "getLikesDistr", IDistribution.class );
        checkReturnType( traitDrl.getClass(), "getLikesDistr", IDistribution.class);

        assertNotNull( beanHand.getLikesDistr() );
        assertNotNull( traitHand.getLikesDistr() );
        assertNotNull( beanDrlClass.get( beanDrl, "likesDistr" ) );
        assertNotNull( traitDrlClass.get( traitDrl, "likesDistr" ) );

        System.out.println( beanHand.getLikesDistr() );
        System.out.println( traitHand.getLikesDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "likesDistr" ) );
        System.out.println( traitDrlClass.get(traitDrl, "likesDistr" ) );

        assertEquals( 0.6, ((IDistribution) beanHand.getLikesDistr()).getDegree( javaCheddar ).getValue(), 1e-16 );
        assertEquals( 0.0, ((IDistribution) traitHand.getLikesDistr()).getDegree( javaCheddar ).getValue(), 1e-16 );
        assertEquals( 0.6, ((IDistribution) beanDrlClass.get(beanDrl, "likesDistr")).getDegree( drlCheddar ).getValue(), 1e-16 );
        assertEquals( 0.0, ((IDistribution) traitDrlClass.get(traitDrl, "likesDistr")).getDegree( drlCheddar ).getValue(), 1e-16 );

        checkConsistency();
    }



    @Test
    public void testProbabilityOnCheese_getValueLikes() {
        checkReturnType( beanHand.getClass(), "getLikesValue", javaCheese );
        checkReturnType( traitHand.getClass(), "getLikesValue", javaCheese );
        checkReturnType( beanDrl.getClass(), "getLikesValue", drlCheese.getFactClass() );
        checkReturnType( traitDrl.getClass(), "getLikesValue", drlCheese.getFactClass() );

        assertNotNull( beanHand.getLikesValue() );
        assertNull( traitHand.getLikesValue() );
        assertNotNull( beanDrlClass.get( beanDrl, "likesValue" ) );
        assertNull( traitDrlClass.get( traitDrl, "likesValue" ) );

        System.out.println( beanHand.getLikesValue() );
        System.out.println( traitHand.getLikesValue() );
        System.out.println( beanDrlClass.get(beanDrl, "likesValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "likesValue" ) );

        assertEquals( javaCheddar, beanHand.getLikesValue() );
        assertEquals( null, traitHand.getLikesValue());
        assertEquals( drlCheddar, beanDrlClass.get( beanDrl, "likesValue" ) );
        assertEquals( null, traitDrlClass.get(traitDrl, "likesValue" ) );

        checkConsistency();
    }




    @Test
    public void testProbabilityOnCheese_setLikes() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setLikes", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setLikes", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setLikes", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setLikes", IImperfectField.class);

        IDistributionStrategies javaBuilder = ChanceStrategyFactory.buildStrategies( "probability", "basic", "simple", ImpBean.Cheese.class );
        IImperfectField fld1 = new ImperfectField( javaBuilder, javaBuilder.parse( "cheddar/0.6" ) );

        IDistributionStrategies drlBuilder = ChanceStrategyFactory.buildStrategies( "probability", "basic", "simple", drlCheese.getFactClass() );
        IImperfectField fld2 = new ImperfectField( drlBuilder, drlBuilder.parse( "cheddar/0.6" ) );


        beanHand.setLikes( fld1 );
        traitHand.setLikes( fld1 );
        beanDrlClass.set( beanDrl, "likes", fld2 );
        traitDrlClass.set( traitDrl, "likes", fld2 );

        assertSame( fld1, beanHand.getLikes() );
        assertSame( fld1, traitHand.getLikes() );
        assertSame( fld2, beanDrlClass.get(beanDrl, "likes") );
        assertSame( fld2, traitDrlClass.get(traitDrl, "likes") );

        System.out.println( beanHand.getLikes() );
        System.out.println( traitHand.getLikes() );
        System.out.println( beanDrlClass.get(beanDrl, "likes") );
        System.out.println( traitDrlClass.get(traitDrl, "likes") );

        assertEquals( javaCheddar, ((IImperfectField) beanHand.getLikes()).getCrisp() );
        assertEquals( javaCheddar, ((IImperfectField) traitHand.getLikes()).getCrisp() );
        assertEquals( drlCheddar, ((IImperfectField) beanDrlClass.get(beanDrl, "likes")).getCrisp() );
        assertEquals( drlCheddar, ((IImperfectField) traitDrlClass.get(traitDrl, "likes")).getCrisp() );

        assertEquals( 0.6, fld1.getCurrent().getDegree( javaCheddar ).getValue(), 1e-16 );
        assertEquals( 0.6, fld2.getCurrent().getDegree( drlCheddar ).getValue(), 1e-16 );

        checkConsistency();
    }



    @Test
    public void testProbabilityOnCheese_setLikesDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setLikesDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setLikesDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setLikesDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setLikesDistr", IDistribution.class);

        IDistributionStrategies javaBuilder = ChanceStrategyFactory.buildStrategies( "probability", "basic", "simple", javaCheese );

        IDistributionStrategies drlBuilder = ChanceStrategyFactory.buildStrategies( "probability", "basic", "simple", drlCheese.getFactClass() );

        IDistribution d1 = javaBuilder.parse( "cheddar/0.6" );
        IDistribution d2 = javaBuilder.parse( "mozzarella/0.3" );
        IDistribution d3 = drlBuilder.parse( "brie/0.8" );
        IDistribution d4 = drlBuilder.parse( "stilton/0.51" );


        beanHand.setLikesDistr(d1);
        traitHand.setLikesDistr(d2);
        beanDrlClass.set( beanDrl, "likesDistr", d3 );
        traitDrlClass.set( traitDrl, "likesDistr", d4 );

        System.out.println( beanHand.getLikesDistr() );
        System.out.println( traitHand.getLikesDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "likesDistr" ) );
        System.out.println( traitDrlClass.get(traitDrl, "likesDistr" ) );


        assertSame( d1, beanHand.getLikesDistr() );
        assertSame( d2, traitHand.getLikesDistr() );
        assertSame( d3, beanDrlClass.get(beanDrl, "likesDistr") );
        assertSame( d4, traitDrlClass.get(traitDrl, "likesDistr") );

        Object c3 = newDrlCheese( "brie" );
        Object c4 = newDrlCheese( "stilton" );

        assertEquals( new ImpBean.Cheese( "cheddar" ), ((IImperfectField) beanHand.getLikes()).getCrisp() );
        assertEquals( new ImpBean.Cheese( "mozzarella" ), ((IImperfectField) traitHand.getLikes()).getCrisp() );
        assertEquals( c3, ((IImperfectField) beanDrlClass.get(beanDrl, "likes")).getCrisp() );
        assertEquals( c4, ((IImperfectField) traitDrlClass.get(traitDrl, "likes")).getCrisp() );

        checkConsistency();
    }


    @Test
    public void testProbabilityOnCheese_setLikesValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setLikesValue", javaCheese );
        checkFirstAndOnlyArgType(traitHand.getClass(), "setLikesValue", javaCheese );
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setLikesValue", drlCheese.getFactClass() );
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setLikesValue", drlCheese.getFactClass() );

        ImpBean.Cheese c1 = new ImpBean.Cheese("emmental");
        ImpBean.Cheese c2 = new ImpBean.Cheese("gorgonzola");
        Object c3 = newDrlCheese( "pecorino" );
        Object c4 = newDrlCheese( "scamorza" );

        beanHand.setLikesValue( c1 );
        traitHand.setLikesValue( c2 );
        beanDrlClass.set( beanDrl, "likesValue", c3 );
        traitDrlClass.set( traitDrl, "likesValue", c4 );

        System.out.println( beanHand.getLikesValue() );
        System.out.println( traitHand.getLikesValue() );
        System.out.println( beanDrlClass.get(beanDrl, "likesValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "likesValue" ) );

        assertEquals( c1, ((IImperfectField) beanHand.getLikes()).getCrisp() );
        assertEquals( c2, ((IImperfectField) traitHand.getLikes()).getCrisp() );
        assertEquals( c3, ((IImperfectField) beanDrlClass.get(beanDrl, "likes")).getCrisp() );
        assertEquals( c4, ((IImperfectField) traitDrlClass.get(traitDrl, "likes")).getCrisp() );

        assertEquals( 1.0, ((IDistribution) beanHand.getLikesDistr()).getDegree( c1 ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitHand.getLikesDistr()).getDegree( c2 ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) beanDrlClass.get(beanDrl, "likesDistr")).getDegree( c3 ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) traitDrlClass.get(traitDrl, "likesDistr")).getDegree( c4 ).getValue(), 1e-16 );

        checkConsistency();
    }






    @Test
    public void testProbabilityOnCheese_updateLikes() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateLikes", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateLikes", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateLikes", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateLikes", IImperfectField.class);


        IDistributionStrategies javaBuilder = ChanceStrategyFactory.buildStrategies( "probability", "basic", "simple", ImpBean.Cheese.class );
        IImperfectField fld1 = new ImperfectField( javaBuilder, javaBuilder.parse( "cheddar/0.5" ) );

        IDistributionStrategies drlBuilder = ChanceStrategyFactory.buildStrategies( "probability", "basic", "simple", drlCheese.getFactClass() );
        IImperfectField fld2 = new ImperfectField( drlBuilder, drlBuilder.parse( "stilton/0.8" ) );

        beanHand.updateLikes( fld1 );
        traitHand.updateLikes( fld1 );
        invokeUpdate( beanDrl, "likes", fld2, IImperfectField.class );
        invokeUpdate( traitDrl, "likes", fld2, IImperfectField.class );

        assertNotSame( fld1, beanHand.getLikes() );
        assertNotSame( fld2, traitHand.getLikes() );
        assertNotSame( fld1, beanDrlClass.get(beanDrl, "likes") );
        assertNotSame( fld2, traitDrlClass.get(traitDrl, "likes") );

        System.out.println( beanHand.getLikes() );
        System.out.println( traitHand.getLikes() );
        System.out.println( beanDrlClass.get(beanDrl, "likes") );
        System.out.println( traitDrlClass.get(traitDrl, "likes") );

        assertEquals( 0.8, beanHand.getLikes().getCurrent().getDegree( new ImpBean.Cheese("cheddar") ).getValue(), 1e-16);

        assertEquals( 0.5, traitHand.getLikes().getCurrent().getDegree( new ImpBean.Cheese("cheddar") ).getValue(), 1e-16 );

        assertEquals( 0.12, ((IImperfectField) beanDrlClass.get(beanDrl, "likes")).getCurrent().getDegree( newDrlCheese( "cheddar" ) ).getValue(), 1e-16 );

        assertEquals( 0.2, ((IImperfectField) traitDrlClass.get(traitDrl, "likes")).getCurrent().getDegree( newDrlCheese( "cheddar" ) ).getValue(), 1e-16 );

        checkConsistency();
    }






    @Test
    public void testProbabilityOnCheese_updateLikesDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateLikesDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateLikesDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateLikesDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateLikesDistr", IDistribution.class);

        IDistributionStrategies javaBuilder = ChanceStrategyFactory.buildStrategies( "probability", "basic", "simple", ImpBean.Cheese.class );
        IDistribution d1 = javaBuilder.parse( "cheddar/0.5" );

        IDistributionStrategies drlBuilder = ChanceStrategyFactory.buildStrategies( "probability", "basic", "simple", drlCheese.getFactClass() );
        IDistribution d2 = drlBuilder.parse( "stilton/0.8" );


        beanHand.updateLikesDistr( d1 );
        traitHand.updateLikesDistr( d1 );
        invokeUpdate( beanDrl, "likesDistr", d2, IDistribution.class );
        invokeUpdate( traitDrl, "likesDistr", d2, IDistribution.class );

        assertNotSame( d1, beanHand.getLikesDistr() );
        assertNotSame( d1, traitHand.getLikesDistr() );
        assertNotSame( d2, beanDrlClass.get(beanDrl, "likesDistr") );
        assertNotSame( d2, traitDrlClass.get(traitDrl, "likesDistr") );

        System.out.println( beanHand.getLikesDistr() );
        System.out.println( traitHand.getLikesDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "likesDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "likesDistr") );

        assertEquals( 0.2, beanHand.getLikes().getCurrent().getDegree( new ImpBean.Cheese("stilton") ).getValue(), 1e-16);

        assertEquals( 0.5, traitHand.getLikes().getCurrent().getDegree( new ImpBean.Cheese("stilton") ).getValue(), 1e-16 );

        assertEquals( 0.88, ((IImperfectField) beanDrlClass.get(beanDrl, "likes")).getCurrent().getDegree( newDrlCheese( "stilton" ) ).getValue(), 1e-16 );

        assertEquals( 0.8, ((IImperfectField) traitDrlClass.get(traitDrl, "likes")).getCurrent().getDegree( newDrlCheese( "stilton" ) ).getValue(), 1e-16 );

        checkConsistency();
    }




    @Test
    public void testProbabilityOnCheese_updateLikesValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "updateLikesValue", javaCheese );
        checkFirstAndOnlyArgType(traitHand.getClass(), "updateLikesValue", javaCheese );
        checkFirstAndOnlyArgType(beanDrl.getClass(), "updateLikesValue", drlCheese.getFactClass() );
        checkFirstAndOnlyArgType(traitDrl.getClass(), "updateLikesValue", drlCheese.getFactClass() );

        ImpBean.Cheese c1 = new ImpBean.Cheese( "pecorino" );
        Object c2 = newDrlCheese( "mozzarella" );

        beanHand.updateLikesValue( c1 );
        traitHand.updateLikesValue( c1 );
        invokeUpdate( beanDrl, "likesValue", c2 );
        invokeUpdate( traitDrl, "likesValue", c2 );

        assertTrue( c1.equals( beanHand.getLikesValue() ) );
        assertTrue( c1.equals( traitHand.getLikesValue() ) );
        assertTrue( c2.equals( beanDrlClass.get(beanDrl, "likesValue") ) );
        assertTrue( c2.equals( traitDrlClass.get(traitDrl, "likesValue") ) );

        System.out.println( beanHand.getLikesValue() );
        System.out.println( traitHand.getLikesValue() );
        System.out.println( beanDrlClass.get(beanDrl, "likesValue") );
        System.out.println( traitDrlClass.get(traitDrl, "likesValue") );

        assertEquals( 1.0, beanHand.getLikes().getCurrent().getDegree( c1 ).getValue(), 1e-16 );

        assertEquals( 1.0, traitHand.getLikes().getCurrent().getDegree( c1 ).getValue(), 1e-16 );

        assertEquals( 1.0, ((IImperfectField) beanDrlClass.get(beanDrl, "likes")).getCurrent().getDegree( c2 ).getValue(), 1e-16 );

        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get(traitDrl, "likes")).getCurrent().getDegree( c2 ).getValue(), 1e-16 );

        assertEquals( c1, beanHand.getLikesValue() );
        assertEquals( c1, traitHand.getLikesValue() );
        assertEquals( c2, beanDrlClass.get(beanDrl, "likesValue") );
        assertEquals( c2, traitDrlClass.get(traitDrl, "likesValue") );

        checkConsistency();
    }



















    @Test
    public void testFuzzyOnDoubleSupport_getWeight() {

        checkReturnType( beanHand.getClass(), "getWeight", Double.class );
        checkReturnType( traitHand.getClass(), "getWeight", Double.class );
        checkReturnType( beanDrl.getClass(), "getWeight", Double.class );
        checkReturnType( traitDrl.getClass(), "getWeight", Double.class );

        assertNotNull( beanHand.getWeight() );
        assertNotNull( traitHand.getWeight() );
        assertNotNull( beanDrlClass.get(beanDrl, "weight") );
        assertNotNull( traitDrlClass.get(traitDrl, "weight") );

        System.out.println( beanHand.getWeight() + " << " + beanHand.getBodyDistr() );
        System.out.println( traitHand.getWeight() + " << " + traitHand.getBodyDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "weight") + " << " + beanDrlClass.get(beanDrl, "bodyDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "weight") + " << " + traitDrlClass.get(traitDrl, "bodyDistr") );

        assertEquals( 65.0, beanHand.getWeight(), 1e-16 );
        assertEquals(0.65, beanHand.getBodyDistr().getDegree(Weight.FAT).getValue(), 1e-16);
        assertEquals( Weight.FAT, beanHand.getBodyValue() );

        assertEquals( 65.0, traitHand.getWeight(), 1e-16 );
        assertEquals( 0.65, traitHand.getBodyDistr().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( Weight.FAT, traitHand.getBodyValue() );

        assertEquals( 65.0, ( (Double) beanDrlClass.get( beanDrl, "weight" ) ), 1e-16 );
        assertEquals( 0.65, ( (IDistribution) beanDrlClass.get( beanDrl, "bodyDistr" ) ).getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( Weight.FAT, beanDrlClass.get( beanDrl, "bodyValue" ) );

        assertEquals( 65.0, ( (Double) traitDrlClass.get( traitDrl, "weight" ) ), 1e-16 );
        assertEquals( 0.65, ( (IDistribution) traitDrlClass.get( traitDrl, "bodyDistr" ) ).getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( Weight.FAT, traitDrlClass.get( traitDrl, "bodyValue" ) );

        checkConsistency();
    }


    @Test
    public void testFuzzyOnDoubleSupport_setWeight() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setWeight", Double.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setWeight", Double.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setWeight", Double.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setWeight", Double.class);

        beanHand.setWeight( 68.0 );
        traitHand.setWeight( 68.0 );
        beanDrlClass.set( beanDrl, "weight", 68.0 );
        traitDrlClass.set( traitDrl, "weight", 68.0 );

        assertNotNull( beanHand.getWeight() );
        assertNotNull( traitHand.getWeight() );
        assertNotNull( beanDrlClass.get(beanDrl, "weight") );
        assertNotNull( traitDrlClass.get(traitDrl, "weight") );

        System.out.println( beanHand.getWeight() + " << " + beanHand.getBodyDistr() );
        System.out.println( traitHand.getWeight() + " << " + traitHand.getBodyDistr() );
        System.out.println( beanDrlClass.get( beanDrl, "weight" ) + " << " + beanDrlClass.get( beanDrl, "bodyDistr" ) );
        System.out.println( traitDrlClass.get( traitDrl, "weight" ) + " << " + traitDrlClass.get( traitDrl, "bodyDistr" ) );

        assertEquals( 68.0, beanHand.getWeight(), 1e-16 );
        assertEquals( 0.68, beanHand.getBodyDistr().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( Weight.FAT, beanHand.getBodyValue() );

        assertEquals( 68.0, traitHand.getWeight(), 1e-16 );
        assertEquals( 0.68, traitHand.getBodyDistr().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( Weight.FAT, traitHand.getBodyValue() );

        assertEquals( 68.0, (Double) beanDrlClass.get( beanDrl, "weight" ), 1e-16 );
        assertEquals( 0.68, ( (IDistribution<Weight>) beanDrlClass.get( beanDrl, "bodyDistr" ) ).getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( Weight.FAT, beanDrlClass.get( beanDrl, "bodyValue" ) );

        assertEquals( 68.0, (Double) traitDrlClass.get( traitDrl, "weight" ), 1e-16 );
        assertEquals( 0.68, ( (IDistribution<Weight>) traitDrlClass.get( traitDrl, "bodyDistr" ) ).getDegree(Weight.FAT).getValue(), 1e-16 );
        assertEquals( Weight.FAT, traitDrlClass.get( traitDrl, "bodyValue" ) );

        checkConsistency();
    }


    @Test
    public void testFuzzyOnDouble_getBody() {

        checkReturnType( beanHand.getClass(), "getBody", IImperfectField.class );
        checkReturnType( traitHand.getClass(), "getBody", IImperfectField.class );
        checkReturnType( beanDrl.getClass(), "getBody", IImperfectField.class );
        checkReturnType( traitDrl.getClass(), "getBody", IImperfectField.class );

        assertNotNull( beanHand.getBody() );
        assertNotNull( traitHand.getBody() );
        assertNotNull( beanDrlClass.get(beanDrl, "body") );
        assertNotNull( traitDrlClass.get(traitDrl, "body") );

        System.out.println( beanHand.getBody() );
        System.out.println( traitHand.getBody() );
        System.out.println( beanDrlClass.get(beanDrl, "body") );
        System.out.println( traitDrlClass.get(traitDrl, "body") );

        assertEquals( Weight.FAT, ((IImperfectField) beanHand.getBody()).getCrisp() );
        assertEquals( Weight.FAT, ((IImperfectField) traitHand.getBody()).getCrisp() );
        assertEquals( Weight.FAT, ((IImperfectField) beanDrlClass.get(beanDrl, "body")).getCrisp() );
        assertEquals( Weight.FAT, ((IImperfectField) traitDrlClass.get(traitDrl, "body")).getCrisp() );

        assertEquals( 0.35, beanHand.getBody().getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( 0.35, traitHand.getBody().getCurrent().getDegree( Weight.SLIM ).getValue() );
        assertEquals( 0.35, ((IImperfectField) beanDrlClass.get(beanDrl, "body")).getCurrent().getDegree( Weight.SLIM ).getValue() );
        assertEquals( 0.35, ((IImperfectField) traitDrlClass.get(traitDrl, "body")).getCurrent().getDegree( Weight.SLIM ).getValue() );

        checkConsistency();

    }

    @Test
    public void testFuzzyOnDouble_getBodyDistr() {

        checkReturnType( beanHand.getClass(), "getBodyDistr", IDistribution.class );
        checkReturnType( traitHand.getClass(), "getBodyDistr", IDistribution.class );
        checkReturnType( beanDrl.getClass(), "getBodyDistr", IDistribution.class );
        checkReturnType( traitDrl.getClass(), "getBodyDistr", IDistribution.class );

        assertNotNull( beanHand.getBodyDistr() );
        assertNotNull( traitHand.getBodyDistr() );
        assertNotNull( beanDrlClass.get(beanDrl, "bodyDistr") );
        assertNotNull( traitDrlClass.get(traitDrl, "bodyDistr") );

        System.out.println( beanHand.getBodyDistr() );
        System.out.println( traitHand.getBodyDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "bodyDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "bodyDistr") );

        assertEquals( 0.35, beanHand.getBodyDistr().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( 0.35, traitHand.getBodyDistr().getDegree( Weight.SLIM ).getValue() );
        assertEquals( 0.35, ((IDistribution) beanDrlClass.get(beanDrl, "bodyDistr")).getDegree( Weight.SLIM ).getValue() );
        assertEquals( 0.35, ((IDistribution) traitDrlClass.get(traitDrl, "bodyDistr")).getDegree( Weight.SLIM ).getValue() );

        checkConsistency();

    }

    @Test
    public void testFuzzyOnDouble_getBodyValue() {
        checkReturnType( beanHand.getClass(), "getBodyValue", Weight.class );
        checkReturnType( traitHand.getClass(), "getBodyValue", Weight.class );
        checkReturnType( beanDrl.getClass(), "getBodyValue", Weight.class );
        checkReturnType( traitDrl.getClass(), "getBodyValue", Weight.class);

        assertNotNull( beanHand.getBodyValue() );
        assertNotNull( traitHand.getBodyValue() );
        assertNotNull( beanDrlClass.get( beanDrl, "bodyValue" ) );
        assertNotNull( traitDrlClass.get( traitDrl, "bodyValue" ) );

        System.out.println( beanHand.getBodyValue() );
        System.out.println( traitHand.getBodyValue() );
        System.out.println( beanDrlClass.get(beanDrl, "bodyValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "bodyValue" ) );

        assertEquals( Weight.FAT, beanHand.getBodyValue() );
        assertEquals( Weight.FAT, traitHand.getBodyValue());
        assertEquals( Weight.FAT, beanDrlClass.get( beanDrl, "bodyValue" ) );
        assertEquals( Weight.FAT, traitDrlClass.get(traitDrl, "bodyValue" ) );

        checkConsistency();
    }



    @Test
    public void testFuzzyOnDouble_setBody() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setBody", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setBody", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setBody", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setBody", IImperfectField.class);

        IDistributionStrategies<Weight> strats = ChanceStrategyFactory.<Weight>buildStrategies(
                "fuzzy",
                "linguistic",
                "simple",
                Weight.class);
        IDistributionStrategies<Double> subStrats = ChanceStrategyFactory.<Double>buildStrategies(
                "possibility",
                "linguistic",
                "simple",
                Double.class);
        IImperfectField<Weight> fld = new LinguisticImperfectField<Weight,Double>( strats, subStrats, "FAT/0.4, SLIM/0.6" );

        beanHand.setBody( fld );
        traitHand.setBody( fld );
        beanDrlClass.set( beanDrl, "body", fld );
        traitDrlClass.set( traitDrl, "body", fld );

        assertEquals( fld, beanHand.getBody() );
        assertEquals( fld, traitHand.getBody() );
        assertEquals( fld, beanDrlClass.get(beanDrl, "body") );
        assertEquals( fld, traitDrlClass.get(traitDrl, "body") );

        System.out.println( beanHand.getBody() );
        System.out.println( traitHand.getBody() );
        System.out.println( beanDrlClass.get(beanDrl, "body") );
        System.out.println( traitDrlClass.get(traitDrl, "body") );

        assertEquals( Weight.SLIM, ((IImperfectField) beanHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) beanDrlClass.get(beanDrl, "body")).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitDrlClass.get(traitDrl, "body")).getCrisp() );

        assertEquals( 0.4, fld.getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );

        assertEquals( 45, beanHand.getWeight(), 0.1 );
        assertEquals( 45, traitHand.getWeight(), 0.1 );
        assertEquals( 45, (Double) beanDrlClass.get( beanDrl, "weight"), 0.1 );
        assertEquals( 45, (Double) traitDrlClass.get( traitDrl, "weight"), 0.1 );

        checkConsistency();
    }


    @Test
    public void testFuzzyOnDouble_setBodyDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setBodyDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setBodyDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setBodyDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setBodyDistr", IDistribution.class);

        IDistributionStrategies<Weight> strats = ChanceStrategyFactory.<Weight>buildStrategies(
                "fuzzy",
                "linguistic",
                "simple",
                Weight.class);
        IDistributionStrategies<Double> subStrats = ChanceStrategyFactory.<Double>buildStrategies(
                "possibility",
                "linguistic",
                "simple",
                Double.class);
        IDistribution<Weight> d = strats.parse( "FAT/0.4, SLIM/0.6" );

        beanHand.setBodyDistr( d );
        traitHand.setBodyDistr( d );
        beanDrlClass.set( beanDrl, "bodyDistr", d );
        traitDrlClass.set( traitDrl, "bodyDistr", d );

        assertEquals( d, beanHand.getBodyDistr() );
        assertEquals( d, traitHand.getBodyDistr() );
        assertEquals( d, beanDrlClass.get(beanDrl, "bodyDistr") );
        assertEquals( d, traitDrlClass.get(traitDrl, "bodyDistr") );

        System.out.println( beanHand.getBodyDistr() );
        System.out.println( traitHand.getBodyDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "bodyDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "bodyDistr") );

        assertEquals( Weight.SLIM, ((IImperfectField) beanHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) beanDrlClass.get(beanDrl, "body")).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitDrlClass.get(traitDrl, "body")).getCrisp() );

        assertEquals( 0.4, d.getDegree( Weight.FAT ).getValue(), 1e-16 );

        assertEquals( 45, beanHand.getWeight(), 0.1 );
        assertEquals( 45, traitHand.getWeight(), 0.1 );
        assertEquals( 45, (Double) beanDrlClass.get( beanDrl, "weight"), 0.1 );
        assertEquals( 45, (Double) traitDrlClass.get( traitDrl, "weight"), 0.1 );

        checkConsistency();
    }




    @Test
    public void testFuzzyOnDouble_setBodyValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setBodyValue", Weight.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setBodyValue", Weight.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setBodyValue", Weight.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setBodyValue", Weight.class);

        Weight d = Weight.SLIM;

        beanHand.setBodyValue( d );
        traitHand.setBodyValue( d );
        beanDrlClass.set( beanDrl, "bodyValue", d );
        traitDrlClass.set( traitDrl, "bodyValue", d );

        assertEquals( d, beanHand.getBodyValue() );
        assertEquals( d, traitHand.getBodyValue() );
        assertEquals( d, beanDrlClass.get(beanDrl, "bodyValue") );
        assertEquals( d, traitDrlClass.get(traitDrl, "bodyValue") );

        System.out.println( beanHand.getBodyValue() );
        System.out.println( traitHand.getBodyValue() );
        System.out.println( beanDrlClass.get(beanDrl, "bodyValue") );
        System.out.println( traitDrlClass.get(traitDrl, "bodyValue") );

        assertEquals( Weight.SLIM, ((IImperfectField) beanHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) beanDrlClass.get(beanDrl, "body")).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitDrlClass.get(traitDrl, "body")).getCrisp() );

        assertEquals( 0.0, beanHand.getBodyDistr().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 1.0, beanHand.getBodyDistr().getDegree( Weight.SLIM ).getValue(), 1e-16 );

        assertEquals( 0.0, traitHand.getBodyDistr().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 1.0, traitHand.getBodyDistr().getDegree( Weight.SLIM ).getValue(), 1e-16 );

        assertEquals( 0.0, ((IDistribution) beanDrlClass.get(beanDrl, "bodyDistr")).getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) beanDrlClass.get(beanDrl, "bodyDistr")).getDegree( Weight.SLIM ).getValue(), 1e-16 );

        assertEquals( 0.0,  ((IDistribution) traitDrlClass.get(traitDrl, "bodyDistr")).getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 1.0,  ((IDistribution) traitDrlClass.get(traitDrl, "bodyDistr")).getDegree( Weight.SLIM ).getValue(), 1e-16 );

        assertEquals( 33.3, beanHand.getWeight(), 0.1 );
        assertEquals( 33.3, traitHand.getWeight(), 0.1 );
        assertEquals( 33.3, (Double) beanDrlClass.get( beanDrl, "weight"), 0.1 );
        assertEquals( 33.3, (Double) traitDrlClass.get( traitDrl, "weight"), 0.1 );

        checkConsistency();
    }


    @Test
    public void testFuzzyOnDouble_updateBody() {

        checkFirstAndOnlyArgType( beanHand.getClass(), "updateBody", IImperfectField.class );
        checkFirstAndOnlyArgType( traitHand.getClass(), "updateBody", IImperfectField.class );
        checkFirstAndOnlyArgType( beanDrl.getClass(), "updateBody", IImperfectField.class );
        checkFirstAndOnlyArgType( traitDrl.getClass(), "updateBody", IImperfectField.class );

        IDistributionStrategies<Weight> strats = ChanceStrategyFactory.<Weight>buildStrategies(
                "fuzzy",
                "linguistic",
                "simple",
                Weight.class);
        IDistributionStrategies<Double> subStrats = ChanceStrategyFactory.<Double>buildStrategies(
                "possibility",
                "linguistic",
                "simple",
                Double.class);
        IImperfectField<Weight> fld = new LinguisticImperfectField<Weight,Double>( strats, subStrats, "FAT/0.2, SLIM/0.8" );

        beanHand.updateBody( fld );
        traitHand.updateBody( fld );
        invokeUpdate( beanDrl, "body", fld, IImperfectField.class );
        invokeUpdate( traitDrl, "body", fld, IImperfectField.class );

        System.out.println( beanHand.getBody() );
        System.out.println( traitHand.getBody() );
        System.out.println( beanDrlClass.get(beanDrl, "body") );
        System.out.println( traitDrlClass.get(traitDrl, "body") );

        assertEquals( Weight.SLIM, ((IImperfectField) beanHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) beanDrlClass.get(beanDrl, "body")).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitDrlClass.get(traitDrl, "body")).getCrisp() );

        assertEquals( 0.65, beanHand.getBody().getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 0.8, beanHand.getBody().getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, beanHand.getBodyValue() );
        assertEquals( 48, beanHand.getWeight(), 0.25 );


        assertEquals( 0.65, traitHand.getBody().getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 0.8, traitHand.getBody().getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, traitHand.getBodyValue() );
        assertEquals( 48, traitHand.getWeight(), 0.25 );

        assertEquals( 0.65, ((IImperfectField) beanDrlClass.get( beanDrl, "body" )).getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 0.8, ((IImperfectField) beanDrlClass.get( beanDrl, "body" )).getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, ((Weight) beanDrlClass.get( beanDrl, "bodyValue" )) );
        assertEquals( 48, ((Double) beanDrlClass.get( beanDrl, "weight" )), 0.25 );

        assertEquals( 0.65, ((IImperfectField) traitDrlClass.get( traitDrl, "body" )).getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 0.8, ((IImperfectField) traitDrlClass.get( traitDrl, "body" )).getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, ((Weight) traitDrlClass.get( traitDrl, "bodyValue" )) );
        assertEquals( 48, ((Double) traitDrlClass.get( traitDrl, "weight" )), 0.25 );

        checkConsistency();
    }




    @Test
    public void testFuzzyOnDouble_updateBodyDistr() {

        checkFirstAndOnlyArgType( beanHand.getClass(), "updateBodyDistr", IDistribution.class );
        checkFirstAndOnlyArgType( traitHand.getClass(), "updateBodyDistr", IDistribution.class );
        checkFirstAndOnlyArgType( beanDrl.getClass(), "updateBodyDistr", IDistribution.class );
        checkFirstAndOnlyArgType( traitDrl.getClass(), "updateBodyDistr", IDistribution.class );

        IDistributionStrategies<Weight> strats = ChanceStrategyFactory.<Weight>buildStrategies(
                "fuzzy",
                "linguistic",
                "simple",
                Weight.class);
        IDistributionStrategies<Double> subStrats = ChanceStrategyFactory.<Double>buildStrategies(
                "possibility",
                "linguistic",
                "simple",
                Double.class);
        IDistribution<Weight> fld = strats.parse( "FAT/0.2, SLIM/0.8" );

        beanHand.updateBodyDistr( fld );
        traitHand.updateBodyDistr( fld );
        invokeUpdate( beanDrl, "bodyDistr", fld, IDistribution.class );
        invokeUpdate( traitDrl, "bodyDistr", fld, IDistribution.class );

        System.out.println( beanHand.getBody() );
        System.out.println( traitHand.getBody() );
        System.out.println( beanDrlClass.get(beanDrl, "body") );
        System.out.println( traitDrlClass.get(traitDrl, "body") );

        assertEquals( Weight.SLIM, ((IImperfectField) beanHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) beanDrlClass.get(beanDrl, "body")).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitDrlClass.get(traitDrl, "body")).getCrisp() );

        assertEquals( 0.65, beanHand.getBody().getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 0.8, beanHand.getBody().getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, beanHand.getBodyValue() );
        assertEquals( 48, beanHand.getWeight(), 0.25 );


        assertEquals( 0.65, traitHand.getBody().getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 0.8, traitHand.getBody().getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, traitHand.getBodyValue() );
        assertEquals( 48, traitHand.getWeight(), 0.25 );

        assertEquals( 0.65, ((IImperfectField) beanDrlClass.get( beanDrl, "body" )).getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 0.8, ((IImperfectField) beanDrlClass.get( beanDrl, "body" )).getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, ((Weight) beanDrlClass.get( beanDrl, "bodyValue" )) );
        assertEquals( 48, ((Double) beanDrlClass.get( beanDrl, "weight" )), 0.25 );

        assertEquals( 0.65, ((IImperfectField) traitDrlClass.get( traitDrl, "body" )).getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 0.8, ((IImperfectField) traitDrlClass.get( traitDrl, "body" )).getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, ((Weight) traitDrlClass.get( traitDrl, "bodyValue" )) );
        assertEquals( 48, ((Double) traitDrlClass.get( traitDrl, "weight" )), 0.25 );

        checkConsistency();
    }

    @Test
    public void testFuzzyOnDouble_updateBodyValue() {

        checkFirstAndOnlyArgType( beanHand.getClass(), "updateBodyValue", Weight.class );
        checkFirstAndOnlyArgType( traitHand.getClass(), "updateBodyValue", Weight.class );
        checkFirstAndOnlyArgType( beanDrl.getClass(), "updateBodyValue", Weight.class );
        checkFirstAndOnlyArgType( traitDrl.getClass(), "updateBodyValue", Weight.class );

        Weight w = Weight.SLIM;

        beanHand.updateBodyValue( w );
        traitHand.updateBodyValue( w );
        invokeUpdate( beanDrl, "bodyValue", w );
        invokeUpdate( traitDrl, "bodyValue", w );

        System.out.println( beanHand.getBody() );
        System.out.println( traitHand.getBody() );
        System.out.println( beanDrlClass.get(beanDrl, "body") );
        System.out.println( traitDrlClass.get(traitDrl, "body") );

        assertEquals( Weight.SLIM, ((IImperfectField) beanHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitHand.getBody()).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) beanDrlClass.get(beanDrl, "body")).getCrisp() );
        assertEquals( Weight.SLIM, ((IImperfectField) traitDrlClass.get(traitDrl, "body")).getCrisp() );

        assertEquals( 0.65, beanHand.getBody().getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 1.0, beanHand.getBody().getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, beanHand.getBodyValue() );
        assertEquals( 46.5, beanHand.getWeight(), 0.25 );


        assertEquals( 0.65, traitHand.getBody().getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 1.0, traitHand.getBody().getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, traitHand.getBodyValue() );
        assertEquals( 46.5, traitHand.getWeight(), 0.25 );

        assertEquals( 0.65, ((IImperfectField) beanDrlClass.get( beanDrl, "body" )).getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IImperfectField) beanDrlClass.get( beanDrl, "body" )).getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, ((Weight) beanDrlClass.get( beanDrl, "bodyValue" )) );
        assertEquals( 46.5, ((Double) beanDrlClass.get( beanDrl, "weight" )), 0.25 );

        assertEquals( 0.65, ((IImperfectField) traitDrlClass.get( traitDrl, "body" )).getCurrent().getDegree( Weight.FAT ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get( traitDrl, "body" )).getCurrent().getDegree( Weight.SLIM ).getValue(), 1e-16 );
        assertEquals( Weight.SLIM, ((Weight) traitDrlClass.get( traitDrl, "bodyValue" )) );
        assertEquals( 46.5, ((Double) traitDrlClass.get( traitDrl, "weight" )), 0.25 );

        checkConsistency();
    }




















    @Test
    public void testFuzzyOnIntegerSupport_getBucks() {

        checkReturnType( beanHand.getClass(), "getBucks", Integer.class );
        checkReturnType( traitHand.getClass(), "getBucks", Integer.class );
        checkReturnType( beanDrl.getClass(), "getBucks", Integer.class );
        checkReturnType( traitDrl.getClass(), "getBucks", Integer.class );

        assertNotNull( beanHand.getPrice() );
        assertNotNull( traitHand.getPrice() );
        assertNotNull( beanDrlClass.get(beanDrl, "bucks") );
        assertNotNull( traitDrlClass.get(traitDrl, "bucks") );

        System.out.println( beanHand.getPrice() + " << " + beanHand.getBodyDistr() );
        System.out.println( traitHand.getPrice() + " << " + traitHand.getBodyDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "bucks") + " << " + beanDrlClass.get(beanDrl, "priceDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "bucks") + " << " + traitDrlClass.get(traitDrl, "priceDistr") );

        assertEquals( 0, (int) beanHand.getBucks() );
        assertEquals( 0.0, beanHand.getPriceDistr().getDegree( Price.BLOODY_HELL).getValue(), 1e-16 );
        assertEquals( Price.BLOODY_HELL, beanHand.getPriceValue() );

        assertEquals( 0, (int) traitHand.getBucks() );
        assertEquals( 0.0, traitHand.getPriceDistr().getDegree( Price.EXPENSIVE ).getValue(), 1e-16 );
        assertEquals( Price.BLOODY_HELL, traitHand.getPriceValue() );

        assertEquals( 0, ( (Integer) beanDrlClass.get( beanDrl, "bucks" ) ).intValue() );
        assertEquals( 0.0, ( (IDistribution) beanDrlClass.get( beanDrl, "priceDistr" ) ).getDegree( Price.CHEAP ).getValue() );
        assertEquals( Price.BLOODY_HELL, beanDrlClass.get( beanDrl, "priceValue" ) );

        assertEquals( 0, ( (Integer) traitDrlClass.get( traitDrl, "bucks" ) ).intValue() );
        assertEquals( 0.0, ( (IDistribution) traitDrlClass.get( traitDrl, "priceDistr" ) ).getDegree( Price.REASONABLE ).getValue() );
        assertEquals( Price.BLOODY_HELL, traitDrlClass.get( traitDrl, "priceValue" ) );

        checkConsistency();
    }


    @Test
    public void testFuzzyOnIntegerSupport_setPrice() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setBucks", Integer.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setBucks", Integer.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setBucks", Integer.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setBucks", Integer.class);

        beanHand.setBucks( 37 );
        traitHand.setBucks( 37 );
        beanDrlClass.set( beanDrl, "bucks", 37 );
        traitDrlClass.set( traitDrl, "bucks", 37 );

        assertNotNull( beanHand.getBucks() );
        assertNotNull( traitHand.getBucks() );
        assertNotNull( beanDrlClass.get(beanDrl, "bucks") );
        assertNotNull( traitDrlClass.get(traitDrl, "bucks") );

        System.out.println( beanHand.getBucks() + " << " + beanHand.getPriceDistr() );
        System.out.println( traitHand.getBucks() + " << " + traitHand.getPriceDistr() );
        System.out.println( beanDrlClass.get( beanDrl, "bucks" ) + " << " + beanDrlClass.get( beanDrl, "priceDistr" ) );
        System.out.println( traitDrlClass.get( traitDrl, "bucks" ) + " << " + traitDrlClass.get( traitDrl, "priceDistr" ) );

        assertEquals( 37, beanHand.getBucks(), 1e-16 );
        assertEquals( 0.3, beanHand.getPriceDistr().getDegree( Price.CHEAP ).getValue(), 1e-16 );
        assertEquals( Price.REASONABLE, beanHand.getPriceValue() );

        assertEquals( 37, traitHand.getBucks(), 1e-16 );
        assertEquals( 0.3, traitHand.getPriceDistr().getDegree( Price.CHEAP ).getValue(), 1e-16 );
        assertEquals( Price.REASONABLE, traitHand.getPriceValue() );

        assertEquals( 37, ((Integer) beanDrlClass.get( beanDrl, "bucks" )).intValue() );
        assertEquals( 0.3, ( (IDistribution<Price>) beanDrlClass.get( beanDrl, "priceDistr" ) ).getDegree( Price.CHEAP ).getValue(), 1e-16 );
        assertEquals( Price.REASONABLE, beanDrlClass.get( beanDrl, "priceValue" ) );

        assertEquals( 37, ((Integer) traitDrlClass.get( traitDrl, "bucks" )).intValue() );
        assertEquals( 0.3, ( (IDistribution<Price>) traitDrlClass.get( traitDrl, "priceDistr" ) ).getDegree(Price.CHEAP).getValue(), 1e-16 );
        assertEquals( Price.REASONABLE, traitDrlClass.get( traitDrl, "priceValue" ) );

        checkConsistency();
    }


    @Test
    public void testFuzzyOnInteger_getBody() {

        checkReturnType( beanHand.getClass(), "getPrice", IImperfectField.class );
        checkReturnType( traitHand.getClass(), "getPrice", IImperfectField.class );
        checkReturnType( beanDrl.getClass(), "getPrice", IImperfectField.class );
        checkReturnType( traitDrl.getClass(), "getPrice", IImperfectField.class );

        assertNotNull( beanHand.getPrice() );
        assertNotNull( traitHand.getPrice() );
        assertNotNull( beanDrlClass.get(beanDrl, "price") );
        assertNotNull( traitDrlClass.get(traitDrl, "price") );

        System.out.println( beanHand.getPrice() );
        System.out.println( traitHand.getPrice() );
        System.out.println( beanDrlClass.get(beanDrl, "price") );
        System.out.println( traitDrlClass.get(traitDrl, "price") );

        assertEquals( Price.BLOODY_HELL, ((IImperfectField) beanHand.getPrice()).getCrisp() );
        assertEquals( Price.BLOODY_HELL, ((IImperfectField) traitHand.getPrice()).getCrisp() );
        assertEquals( Price.BLOODY_HELL, ((IImperfectField) beanDrlClass.get(beanDrl, "price")).getCrisp() );
        assertEquals( Price.BLOODY_HELL, ((IImperfectField) traitDrlClass.get(traitDrl, "price")).getCrisp() );

        assertEquals( 0.0, beanHand.getPrice().getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.0, traitHand.getPrice().getCurrent().getDegree( Price.BLOODY_HELL ).getValue() );
        assertEquals( 0.0, ((IImperfectField) beanDrlClass.get(beanDrl, "price")).getCurrent().getDegree( Price.BLOODY_HELL ).getValue() );
        assertEquals( 0.0, ((IImperfectField) traitDrlClass.get(traitDrl, "price")).getCurrent().getDegree( Price.BLOODY_HELL ).getValue() );

        checkConsistency();

    }

    @Test
    public void testFuzzyOnInteger_getPriceDistr() {

        checkReturnType( beanHand.getClass(), "getPriceDistr", IDistribution.class );
        checkReturnType( traitHand.getClass(), "getPriceDistr", IDistribution.class );
        checkReturnType( beanDrl.getClass(), "getPriceDistr", IDistribution.class );
        checkReturnType( traitDrl.getClass(), "getPriceDistr", IDistribution.class );

        assertNotNull( beanHand.getPriceDistr() );
        assertNotNull( traitHand.getPriceDistr() );
        assertNotNull( beanDrlClass.get(beanDrl, "priceDistr") );
        assertNotNull( traitDrlClass.get(traitDrl, "priceDistr") );

        System.out.println( beanHand.getPriceDistr() );
        System.out.println( traitHand.getPriceDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "priceDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "priceDistr") );

        assertEquals( 0.0, beanHand.getPriceDistr().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.0, traitHand.getPriceDistr().getDegree( Price.BLOODY_HELL ).getValue() );
        assertEquals( 0.0, ((IDistribution) beanDrlClass.get(beanDrl, "priceDistr")).getDegree( Price.BLOODY_HELL ).getValue() );
        assertEquals( 0.0, ((IDistribution) traitDrlClass.get(traitDrl, "priceDistr")).getDegree( Price.BLOODY_HELL ).getValue() );

        checkConsistency();

    }

    @Test
    public void testFuzzyOnInteger_getPriceValue() {
        checkReturnType( beanHand.getClass(), "getPriceValue", Price.class );
        checkReturnType( traitHand.getClass(), "getPriceValue", Price.class );
        checkReturnType( beanDrl.getClass(), "getPriceValue", Price.class );
        checkReturnType( traitDrl.getClass(), "getPriceValue", Price.class);

        assertNotNull( beanHand.getPriceValue() );
        assertNotNull( traitHand.getPriceValue() );
        assertNotNull( beanDrlClass.get( beanDrl, "priceValue" ) );
        assertNotNull( traitDrlClass.get( traitDrl, "priceValue" ) );

        System.out.println( beanHand.getPriceValue() );
        System.out.println( traitHand.getPriceValue() );
        System.out.println( beanDrlClass.get(beanDrl, "priceValue" ) );
        System.out.println( traitDrlClass.get(traitDrl, "priceValue" ) );

        assertEquals( Price.BLOODY_HELL, beanHand.getPriceValue() );
        assertEquals( Price.BLOODY_HELL, traitHand.getPriceValue());
        assertEquals( Price.BLOODY_HELL, beanDrlClass.get( beanDrl, "priceValue" ) );
        assertEquals( Price.BLOODY_HELL, traitDrlClass.get(traitDrl, "priceValue" ) );

        checkConsistency();
    }



    @Test
    public void testFuzzyOnInteger_setPrice() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setPrice", IImperfectField.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setPrice", IImperfectField.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setPrice", IImperfectField.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setPrice", IImperfectField.class);

        IDistributionStrategies<Price> strats = ChanceStrategyFactory.<Price>buildStrategies(
                "fuzzy",
                "linguistic",
                "simple",
                Price.class);
        IDistributionStrategies<Integer> subStrats = ChanceStrategyFactory.<Integer>buildStrategies(
                "possibility",
                "linguistic",
                "simple",
                Integer.class);
        IImperfectField<Price> fld = new LinguisticImperfectField<Price,Integer>( strats, subStrats, "EXPENSIVE/0.4, CHEAP/0.7" );

        beanHand.setPrice( fld );
        traitHand.setPrice( fld );
        beanDrlClass.set( beanDrl, "price", fld );
        traitDrlClass.set( traitDrl, "price", fld );

        assertEquals( fld, beanHand.getPrice() );
        assertEquals( fld, traitHand.getPrice() );
        assertEquals( fld, beanDrlClass.get(beanDrl, "price") );
        assertEquals( fld, traitDrlClass.get(traitDrl, "price") );

        System.out.println( beanHand.getPrice() );
        System.out.println( traitHand.getPrice() );
        System.out.println( beanDrlClass.get(beanDrl, "price") );
        System.out.println( traitDrlClass.get(traitDrl, "price") );

        assertEquals( Price.CHEAP, ((IImperfectField) beanHand.getPrice()).getCrisp() );
        assertEquals( Price.CHEAP, ((IImperfectField) traitHand.getPrice()).getCrisp() );
        assertEquals( Price.CHEAP, ((IImperfectField) beanDrlClass.get(beanDrl, "price")).getCrisp() );
        assertEquals( Price.CHEAP, ((IImperfectField) traitDrlClass.get(traitDrl, "price")).getCrisp() );

        assertEquals( 0.4, fld.getCurrent().getDegree( Price.EXPENSIVE ).getValue(), 1e-16 );

        assertEquals( 38, beanHand.getBucks().intValue() );
        assertEquals( 38, traitHand.getBucks().intValue() );
        assertEquals( 38, ( (Integer) beanDrlClass.get( beanDrl, "bucks")).intValue() );
        assertEquals( 38, ( (Integer) traitDrlClass.get( traitDrl, "bucks")).intValue() );

        checkConsistency();
    }


    @Test
    public void testFuzzyOnInteger_setBodyDistr() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setBodyDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setBodyDistr", IDistribution.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setBodyDistr", IDistribution.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setBodyDistr", IDistribution.class);

        IDistributionStrategies<Price> strats = ChanceStrategyFactory.<Price>buildStrategies(
                "fuzzy",
                "linguistic",
                "simple",
                Price.class);
        IDistributionStrategies<Integer> subStrats = ChanceStrategyFactory.<Integer>buildStrategies(
                "possibility",
                "linguistic",
                "simple",
                Integer.class);
        IDistribution<Price> d = strats.parse( "REASONABLE/0.4, INEXPENSIVE/0.6" );

        beanHand.setPriceDistr( d );
        traitHand.setPriceDistr( d );
        beanDrlClass.set( beanDrl, "priceDistr", d );
        traitDrlClass.set( traitDrl, "priceDistr", d );

        assertEquals( d, beanHand.getPriceDistr() );
        assertEquals( d, traitHand.getPriceDistr() );
        assertEquals( d, beanDrlClass.get(beanDrl, "priceDistr") );
        assertEquals( d, traitDrlClass.get(traitDrl, "priceDistr") );

        System.out.println( beanHand.getPriceDistr() );
        System.out.println( traitHand.getPriceDistr() );
        System.out.println( beanDrlClass.get(beanDrl, "priceDistr") );
        System.out.println( traitDrlClass.get(traitDrl, "priceDistr") );

        assertEquals( Price.INEXPENSIVE, ((IImperfectField) beanHand.getPrice()).getCrisp() );
        assertEquals( Price.INEXPENSIVE, ((IImperfectField) traitHand.getPrice()).getCrisp() );
        assertEquals( Price.INEXPENSIVE, ((IImperfectField) beanDrlClass.get(beanDrl, "price")).getCrisp() );
        assertEquals( Price.INEXPENSIVE, ((IImperfectField) traitDrlClass.get(traitDrl, "price")).getCrisp() );

        assertEquals( 0.4, d.getDegree( Price.REASONABLE ).getValue(), 1e-16 );

        assertEquals( 21, beanHand.getBucks().intValue() );
        assertEquals( 21, traitHand.getBucks().intValue() );
        assertEquals( 21, (Integer) beanDrlClass.get( beanDrl, "bucks"), 0.1 );
        assertEquals( 21, (Integer) traitDrlClass.get( traitDrl, "bucks"), 0.1 );

        checkConsistency();
    }




    @Test
    public void testFuzzyOnInteger_setPriceValue() {

        checkFirstAndOnlyArgType(beanHand.getClass(), "setPriceValue", Price.class);
        checkFirstAndOnlyArgType(traitHand.getClass(), "setPriceValue", Price.class);
        checkFirstAndOnlyArgType(beanDrl.getClass(), "setPriceValue", Price.class);
        checkFirstAndOnlyArgType(traitDrl.getClass(), "setPriceValue", Price.class);

        Price d = Price.EXPENSIVE;

        beanHand.setPriceValue( d );
        traitHand.setPriceValue( d );
        beanDrlClass.set( beanDrl, "priceValue", d );
        traitDrlClass.set( traitDrl, "priceValue", d );

        assertEquals( d, beanHand.getPriceValue() );
        assertEquals( d, traitHand.getPriceValue() );
        assertEquals( d, beanDrlClass.get(beanDrl, "priceValue") );
        assertEquals( d, traitDrlClass.get(traitDrl, "priceValue") );

        System.out.println( beanHand.getPriceValue() );
        System.out.println( traitHand.getPriceValue() );
        System.out.println( beanDrlClass.get(beanDrl, "priceValue") );
        System.out.println( traitDrlClass.get(traitDrl, "priceValue") );

        assertEquals( Price.EXPENSIVE, ((IImperfectField) beanHand.getPrice()).getCrisp() );
        assertEquals( Price.EXPENSIVE, ((IImperfectField) traitHand.getPrice()).getCrisp() );
        assertEquals( Price.EXPENSIVE, ((IImperfectField) beanDrlClass.get(beanDrl, "price")).getCrisp() );
        assertEquals( Price.EXPENSIVE, ((IImperfectField) traitDrlClass.get(traitDrl, "price")).getCrisp() );

        assertEquals( 0.0, beanHand.getPriceDistr().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 1.0, beanHand.getPriceDistr().getDegree( Price.EXPENSIVE ).getValue(), 1e-16 );

        assertEquals( 0.0, traitHand.getPriceDistr().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 1.0, traitHand.getPriceDistr().getDegree( Price.EXPENSIVE ).getValue(), 1e-16 );

        assertEquals( 0.0, ((IDistribution) beanDrlClass.get(beanDrl, "priceDistr")).getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IDistribution) beanDrlClass.get(beanDrl, "priceDistr")).getDegree( Price.EXPENSIVE ).getValue(), 1e-16 );

        assertEquals( 0.0,  ((IDistribution) traitDrlClass.get(traitDrl, "priceDistr")).getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 1.0,  ((IDistribution) traitDrlClass.get(traitDrl, "priceDistr")).getDegree( Price.EXPENSIVE ).getValue(), 1e-16 );

        assertEquals( 50, beanHand.getBucks().intValue() );
        assertEquals( 50, traitHand.getBucks().intValue() );
        assertEquals( 50, (Integer) beanDrlClass.get( beanDrl, "bucks"), 0.1 );
        assertEquals( 50, (Integer) traitDrlClass.get( traitDrl, "bucks"), 0.1 );

        checkConsistency();
    }


    @Test
    public void testFuzzyOnInteger_updatePrice() {

        checkFirstAndOnlyArgType( beanHand.getClass(), "updatePrice", IImperfectField.class );
        checkFirstAndOnlyArgType( traitHand.getClass(), "updatePrice", IImperfectField.class );
        checkFirstAndOnlyArgType( beanDrl.getClass(), "updatePrice", IImperfectField.class );
        checkFirstAndOnlyArgType( traitDrl.getClass(), "updatePrice", IImperfectField.class );

        IDistributionStrategies<Price> strats = ChanceStrategyFactory.<Price>buildStrategies(
                "fuzzy",
                "linguistic",
                "simple",
                Price.class);
        IDistributionStrategies<Integer> subStrats = ChanceStrategyFactory.<Integer>buildStrategies(
                "possibility",
                "linguistic",
                "simple",
                Integer.class);
        IDistribution<Price> d = strats.parse( "REASONABLE/0.1, INEXPENSIVE/0.1" );

        beanHand.setPriceDistr( d );
        traitHand.setPriceDistr( d );
        beanDrlClass.set( beanDrl, "priceDistr", d );
        traitDrlClass.set( traitDrl, "priceDistr", d );


        IImperfectField<Price> fld = new LinguisticImperfectField<Price,Integer>( strats, subStrats, "BLOODY_HELL/0.2, REASONABLE/0.2, EXPENSIVE/0.2, CHEAP/0.2, INEXPENSIVE/0.4" );

        beanHand.updatePrice( fld );
        traitHand.updatePrice( fld );
        invokeUpdate( beanDrl, "price", fld, IImperfectField.class );
        invokeUpdate( traitDrl, "price", fld, IImperfectField.class );

        System.out.println( beanHand.getPrice() );
        System.out.println( traitHand.getPrice() );
        System.out.println( beanDrlClass.get(beanDrl, "price") );
        System.out.println( traitDrlClass.get(traitDrl, "price") );

        assertEquals( Price.INEXPENSIVE, ((IImperfectField) beanHand.getPrice()).getCrisp() );
        assertEquals( Price.INEXPENSIVE, ((IImperfectField) traitHand.getPrice()).getCrisp() );
        assertEquals( Price.INEXPENSIVE, ((IImperfectField) beanDrlClass.get(beanDrl, "price")).getCrisp() );
        assertEquals( Price.INEXPENSIVE, ((IImperfectField) traitDrlClass.get(traitDrl, "price")).getCrisp() );

        assertEquals( 0.2, beanHand.getPrice().getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.4, beanHand.getPrice().getCurrent().getDegree( Price.INEXPENSIVE ).getValue(), 1e-16 );
        assertEquals( Price.INEXPENSIVE, beanHand.getPriceValue() );
        assertEquals( 42, beanHand.getBucks().intValue() );


        assertEquals( 0.2, traitHand.getPrice().getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.4, traitHand.getPrice().getCurrent().getDegree( Price.INEXPENSIVE ).getValue(), 1e-16 );
        assertEquals( Price.INEXPENSIVE, traitHand.getPriceValue() );
        assertEquals( 42, traitHand.getBucks().intValue() );

        assertEquals( 0.2, ((IImperfectField) beanDrlClass.get( beanDrl, "price" )).getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.4, ((IImperfectField) beanDrlClass.get( beanDrl, "price" )).getCurrent().getDegree( Price.INEXPENSIVE ).getValue(), 1e-16 );
        assertEquals( Price.INEXPENSIVE, ((Price) beanDrlClass.get( beanDrl, "priceValue" )) );
        assertEquals( 42, ((Integer) beanDrlClass.get( beanDrl, "bucks" )).intValue() );

        assertEquals( 0.2, ((IImperfectField) traitDrlClass.get( traitDrl, "price" )).getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.4, ((IImperfectField) traitDrlClass.get( traitDrl, "price" )).getCurrent().getDegree( Price.INEXPENSIVE ).getValue(), 1e-16 );
        assertEquals( Price.INEXPENSIVE, ((Price) traitDrlClass.get( traitDrl, "priceValue" )) );
        assertEquals( 42, ((Integer) traitDrlClass.get( traitDrl, "bucks" )).intValue() );

        checkConsistency();
    }




    @Test
    public void testFuzzyOnInteger_updateBodyDistr() {

        checkFirstAndOnlyArgType( beanHand.getClass(), "updateBodyDistr", IDistribution.class );
        checkFirstAndOnlyArgType( traitHand.getClass(), "updateBodyDistr", IDistribution.class );
        checkFirstAndOnlyArgType( beanDrl.getClass(), "updateBodyDistr", IDistribution.class );
        checkFirstAndOnlyArgType( traitDrl.getClass(), "updateBodyDistr", IDistribution.class );

        IDistributionStrategies<Price> strats = ChanceStrategyFactory.<Price>buildStrategies(
                "fuzzy",
                "linguistic",
                "simple",
                Price.class);

        IDistribution<Price> d = strats.parse( "REASONABLE/0.1, INEXPENSIVE/0.1" );

        beanHand.setPriceDistr( d );
        traitHand.setPriceDistr( d );
        beanDrlClass.set( beanDrl, "priceDistr", d );
        traitDrlClass.set( traitDrl, "priceDistr", d );



        IDistribution<Price> fld = strats.parse( "BLOODY_HELL/0.2, REASONABLE/0.2, EXPENSIVE/0.2, CHEAP/0.2, INEXPENSIVE/0.4" );

        beanHand.updatePriceDistr( fld );
        traitHand.updatePriceDistr( fld );
        invokeUpdate( beanDrl, "priceDistr", fld, IDistribution.class );
        invokeUpdate( traitDrl, "priceDistr", fld, IDistribution.class );

        System.out.println( beanHand.getBody() );
        System.out.println( traitHand.getBody() );
        System.out.println( beanDrlClass.get(beanDrl, "price") );
        System.out.println( traitDrlClass.get(traitDrl, "price") );

        assertEquals( Price.INEXPENSIVE, ((IImperfectField) beanHand.getPrice()).getCrisp() );
        assertEquals( Price.INEXPENSIVE, ((IImperfectField) traitHand.getPrice()).getCrisp() );
        assertEquals( Price.INEXPENSIVE, ((IImperfectField) beanDrlClass.get(beanDrl, "price")).getCrisp() );
        assertEquals( Price.INEXPENSIVE, ((IImperfectField) traitDrlClass.get(traitDrl, "price")).getCrisp() );

        assertEquals( 0.2, beanHand.getPrice().getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.4, beanHand.getPrice().getCurrent().getDegree( Price.INEXPENSIVE ).getValue(), 1e-16 );
        assertEquals( Price.INEXPENSIVE, beanHand.getPriceValue() );
        assertEquals( 42, beanHand.getBucks().intValue() );

        assertEquals( 0.2, traitHand.getPrice().getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.4, traitHand.getPrice().getCurrent().getDegree( Price.INEXPENSIVE ).getValue(), 1e-16 );
        assertEquals( Price.INEXPENSIVE, traitHand.getPriceValue() );
        assertEquals( 42, traitHand.getBucks().intValue() );

        assertEquals( 0.2, ((IImperfectField) beanDrlClass.get( beanDrl, "price" )).getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.4, ((IImperfectField) beanDrlClass.get( beanDrl, "price" )).getCurrent().getDegree( Price.INEXPENSIVE ).getValue(), 1e-16 );
        assertEquals( Price.INEXPENSIVE, ((Price) beanDrlClass.get( beanDrl, "priceValue" )) );
        assertEquals( 42, ((Integer) beanDrlClass.get( beanDrl, "bucks" )).intValue() );

        assertEquals( 0.2, ((IImperfectField) traitDrlClass.get( traitDrl, "price" )).getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 0.4, ((IImperfectField) traitDrlClass.get( traitDrl, "price" )).getCurrent().getDegree( Price.INEXPENSIVE ).getValue(), 1e-16 );
        assertEquals( Price.INEXPENSIVE, ((Price) traitDrlClass.get( traitDrl, "priceValue" )) );
        assertEquals( 42, ((Integer) traitDrlClass.get( traitDrl, "bucks" )).intValue() );

        checkConsistency();
    }



    @Test
    public void testFuzzyOnInteger_updatePriceValue() {

        checkFirstAndOnlyArgType( beanHand.getClass(), "updatePriceValue", Price.class );
        checkFirstAndOnlyArgType( traitHand.getClass(), "updatePriceValue", Price.class );
        checkFirstAndOnlyArgType( beanDrl.getClass(), "updatePriceValue", Price.class );
        checkFirstAndOnlyArgType( traitDrl.getClass(), "updatePriceValue", Price.class );

        IDistributionStrategies<Price> strats = ChanceStrategyFactory.<Price>buildStrategies(
                "fuzzy",
                "linguistic",
                "simple",
                Price.class);

        IDistribution<Price> d = strats.parse( "BLOODY_HELL/0.1, INEXPENSIVE/0.1" );

        beanHand.setPriceDistr( d );
        traitHand.setPriceDistr( d );
        beanDrlClass.set( beanDrl, "priceDistr", d );
        traitDrlClass.set( traitDrl, "priceDistr", d );

        Price w = Price.CHEAP;

        beanHand.updatePriceValue( w );
        traitHand.updatePriceValue( w );
        invokeUpdate( beanDrl, "priceValue", w );
        invokeUpdate( traitDrl, "priceValue", w );

        System.out.println( beanHand.getPrice() );
        System.out.println( traitHand.getPrice() );
        System.out.println( beanDrlClass.get(beanDrl, "price") );
        System.out.println( traitDrlClass.get(traitDrl, "price") );

        assertEquals( Price.CHEAP, ((IImperfectField) beanHand.getPrice()).getCrisp() );
        assertEquals( Price.CHEAP, ((IImperfectField) traitHand.getPrice()).getCrisp() );
        assertEquals( Price.CHEAP, ((IImperfectField) beanDrlClass.get(beanDrl, "price")).getCrisp() );
        assertEquals( Price.CHEAP, ((IImperfectField) traitDrlClass.get(traitDrl, "price")).getCrisp() );

        assertEquals( 0.1, beanHand.getPrice().getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 1.0, beanHand.getPrice().getCurrent().getDegree( Price.CHEAP ).getValue(), 1e-16 );
        assertEquals( Price.CHEAP, beanHand.getPriceValue() );
        assertEquals( 41, beanHand.getBucks().intValue() );

        assertEquals( 0.1, traitHand.getPrice().getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 1.0, traitHand.getPrice().getCurrent().getDegree( Price.CHEAP ).getValue(), 1e-16 );
        assertEquals( Price.CHEAP, traitHand.getPriceValue() );
        assertEquals( 41, traitHand.getBucks().intValue() );

        assertEquals( 0.1, ((IImperfectField) beanDrlClass.get( beanDrl, "price" )).getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IImperfectField) beanDrlClass.get( beanDrl, "price" )).getCurrent().getDegree( Price.CHEAP ).getValue(), 1e-16 );
        assertEquals( Price.CHEAP, ((Price) beanDrlClass.get( beanDrl, "priceValue" )) );
        assertEquals( 41, ((Integer) beanDrlClass.get( beanDrl, "bucks" )).intValue() );

        assertEquals( 0.1, ((IImperfectField) traitDrlClass.get( traitDrl, "price" )).getCurrent().getDegree( Price.BLOODY_HELL ).getValue(), 1e-16 );
        assertEquals( 1.0, ((IImperfectField) traitDrlClass.get( traitDrl, "price" )).getCurrent().getDegree( Price.CHEAP ).getValue(), 1e-16 );
        assertEquals( Price.CHEAP, ((Price) traitDrlClass.get( traitDrl, "priceValue" )) );
        assertEquals( 41, ((Integer) traitDrlClass.get( traitDrl, "bucks" )).intValue() );


        checkConsistency();
    }














    public void checkConsistency() {

        assertEquals( beanHand.getName().getCrisp(), beanHand.getNameValue() );
        assertEquals( beanHand.getAge().getCrisp(), beanHand.getAgeValue() );
        assertEquals( beanHand.getFlag().getCrisp(), beanHand.getFlagValue() );
        assertEquals( beanHand.getLikes().getCrisp(), beanHand.getLikesValue() );
        assertEquals( beanHand.getBody().getCrisp(), beanHand.getBodyValue() );
        assertEquals( beanHand.getPrice().getCrisp(), beanHand.getPriceValue() );

        assertEquals( traitHand.getName().getCrisp(), traitHand.getNameValue() );
        assertEquals( traitHand.getAge().getCrisp(), traitHand.getAgeValue() );
        assertEquals( traitHand.getFlag().getCrisp(), traitHand.getFlagValue() );
        assertEquals( traitHand.getLikes().getCrisp(), traitHand.getLikesValue() );
        assertEquals( traitHand.getBody().getCrisp(), traitHand.getBodyValue() );
        assertEquals( traitHand.getPrice().getCrisp(), traitHand.getPriceValue() );

        assertEquals( ((IImperfectField) beanDrlClass.get( beanDrl, "name" )).getCrisp(), beanDrlClass.get( beanDrl, "nameValue" ) );
        assertEquals( ((IImperfectField) beanDrlClass.get( beanDrl, "age" )).getCrisp(), beanDrlClass.get( beanDrl, "ageValue" ) );
        assertEquals( ((IImperfectField) beanDrlClass.get( beanDrl, "flag" )).getCrisp(), beanDrlClass.get( beanDrl, "flagValue" ) );
        assertEquals( ((IImperfectField) beanDrlClass.get( beanDrl, "likes" )).getCrisp(), beanDrlClass.get( beanDrl, "likesValue" ) );
        assertEquals( ((IImperfectField) beanDrlClass.get( beanDrl, "body" )).getCrisp(), beanDrlClass.get( beanDrl, "bodyValue" ) );
        assertEquals( ((IImperfectField) beanDrlClass.get( beanDrl, "price" )).getCrisp(), beanDrlClass.get( beanDrl, "priceValue" ) );

        assertEquals( ((IImperfectField) traitDrlClass.get( traitDrl, "name" )).getCrisp(), traitDrlClass.get( traitDrl, "nameValue" ) );
        assertEquals( ((IImperfectField) traitDrlClass.get( traitDrl, "age" )).getCrisp(), traitDrlClass.get( traitDrl, "ageValue" ) );
        assertEquals( ((IImperfectField) traitDrlClass.get( traitDrl, "flag" )).getCrisp(), traitDrlClass.get( traitDrl, "flagValue" ) );
        assertEquals( ((IImperfectField) traitDrlClass.get( traitDrl, "likes" )).getCrisp(), traitDrlClass.get( traitDrl, "likesValue" ) );
        assertEquals( ((IImperfectField) traitDrlClass.get( traitDrl, "body" )).getCrisp(), traitDrlClass.get( traitDrl, "bodyValue" ) );
        assertEquals( ((IImperfectField) traitDrlClass.get( traitDrl, "price" )).getCrisp(), traitDrlClass.get( traitDrl, "priceValue" ) );

    }













    private void invokeUpdate( Object bean, String name, Object arg ) {
        invokeUpdate( bean, name, arg, arg.getClass() );
    }


    private void invokeUpdate( Object bean, String name, Object arg, Class argClass ) {
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






}
