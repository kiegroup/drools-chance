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

package org.drools.pmml.pmml_4_1.predictive.models;


import junit.framework.Assert;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.ChangeSetHelperImpl;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.conf.NewInstanceOption;
import org.drools.agent.conf.UseKnowledgeBaseClassloaderOption;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.pmml.pmml_4_1.DroolsAbstractPMMLTest;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.Variable;
import org.junit.Test;

import static org.junit.Assert.*;

public class CleanupTest extends DroolsAbstractPMMLTest {


    private static final String source1 = "org/drools/pmml/pmml_4_1//test_ann_iris_prediction.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_1//test_tree_simple.xml";
    private static final String source3 = "org/drools/pmml/pmml_4_1//test_regression.xml";
    private static final String source4 = "org/drools/pmml/pmml_4_1//test_clustering.xml";
    private static final String source5 = "org/drools/pmml/pmml_4_1//test_svm.xml";

    private static final String source9 = "org/drools/pmml/pmml_4_1//mock_cold.xml";

    private static final String packageName = "org.drools.pmml.pmml_4_1.test";




    @Test
    public void testCleanupANN() {
        StatefulKnowledgeSession kSession = loadModel( source1 );
        assertTrue( kSession.getObjects().size() > 0 );

        QueryResults qres = kSession.getQueryResults( "modelMarker", "Neuiris", Variable.v );
        assertEquals( 1, qres.size() );

        Object marker = qres.iterator().next().get( "$mm" );
        assertNotNull( marker );

        kSession.getWorkingMemoryEntryPoint( "enable_Neuiris" ).insert( Boolean.FALSE );
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );
    }


    @Test
    public void testReenableANN() {
        setKSession( loadModel( source1 ) );
        assertTrue( getKSession().getObjects().size() > 0 );

        QueryResults qres = getKSession().getQueryResults( "modelMarker", "Neuiris", Variable.v );
        assertEquals( 1, qres.size() );

        Object marker = qres.iterator().next().get( "$mm" );
        assertNotNull( marker );

        getKSession().getWorkingMemoryEntryPoint( "enable_Neuiris" ).insert( Boolean.FALSE );

        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));

        assertEquals( 1, getKSession().getObjects().size() );

        getKSession().getWorkingMemoryEntryPoint( "enable_Neuiris" ).insert( Boolean.TRUE );
        getKSession().fireAllRules();

        System.err.println( reportWMObjects(getKSession()) );

        getKSession().getWorkingMemoryEntryPoint("in_Feat2").insert(101);
        getKSession().getWorkingMemoryEntryPoint("in_PetalWid").insert(2);
        getKSession().getWorkingMemoryEntryPoint("in_Species").insert("virginica");
        getKSession().getWorkingMemoryEntryPoint("in_SepalWid").insert(30);
        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));

        Assert.assertEquals(24.0, queryIntegerField("OutSepLen", "Neuiris"));

        assertEquals( 60, getKSession().getObjects().size() );

    }



    @Test
    public void testCleanupDT() {
        StatefulKnowledgeSession kSession = loadModel( source2 );
        assertTrue( kSession.getObjects().size() > 0 );

        QueryResults qres = kSession.getQueryResults( "modelMarker", "TreeTest", Variable.v );
        assertEquals( 1, qres.size() );

        Object marker = qres.iterator().next().get( "$mm" );
        assertNotNull( marker );

        kSession.getWorkingMemoryEntryPoint( "enable_TreeTest" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );

    }

    @Test
    public void testCleanupRegression() {
        StatefulKnowledgeSession kSession = loadModel( source3 );
        assertTrue( kSession.getObjects().size() > 0 );

        QueryResults qres = kSession.getQueryResults( "modelMarker", "LinReg", Variable.v );
        assertEquals( 1, qres.size() );

        Object marker = qres.iterator().next().get( "$mm" );
        assertNotNull( marker );

        kSession.getWorkingMemoryEntryPoint( "enable_LinReg" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );

    }

    @Test
    public void testCleanupClustering() {
        StatefulKnowledgeSession kSession = loadModel( source4 );
        assertTrue( kSession.getObjects().size() > 0 );

        QueryResults qres = kSession.getQueryResults( "modelMarker", "CenterClustering", Variable.v );
        assertEquals( 1, qres.size() );

        Object marker = qres.iterator().next().get( "$mm" );
        assertNotNull( marker );

        kSession.getWorkingMemoryEntryPoint( "enable_CenterClustering" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );

    }

    @Test
    public void testCleanupSVM() {
        StatefulKnowledgeSession kSession = loadModel( source5 );
        assertTrue( kSession.getObjects().size() > 0 );

        QueryResults qres = kSession.getQueryResults( "modelMarker", "SVMXORModel", Variable.v );
        assertEquals( 1, qres.size() );

        Object marker = qres.iterator().next().get( "$mm" );
        assertNotNull( marker );

        kSession.getWorkingMemoryEntryPoint( "enable_SVMXORModel" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 1, kSession.getObjects().size() );

    }



    @Test
    public void testCleanupANNRulesWithIncrementalKA() {

        KnowledgeAgent kAgent = initIncrementalKA();
//        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source1 );
        res.setResourceType( ResourceType.PMML );
        ClassPathResource res2 = (ClassPathResource) ResourceFactory.newClassPathResource( source9 );
        res2.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);
        csAdd.addNewResource( res2 );

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 37, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.out.println( "************************ REMOVING resource 2 ");

        ChangeSetHelperImpl csRem2 = new ChangeSetHelperImpl();
        csRem2.addRemovedResource( res2 );
        kAgent.applyChangeSet( csRem2.getChangeSet() );

        kSession.fireAllRules();


        System.out.println(reportWMObjects(kSession));

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );


    }


    @Test
    public void testCleanupDTRulesWithIncrementalKA() {
        KnowledgeAgent kAgent = initIncrementalKA();
        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source2 );
        res.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );

    }

    @Test
    public void testCleanupClusteringRulesWithIncrementalKA() {
        KnowledgeAgent kAgent = initIncrementalKA();
        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source3 );
        res.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );
    }

    @Test
    public void testCleanupRegressionRulesWithIncrementalKA() {
        KnowledgeAgent kAgent = initIncrementalKA();
        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source4 );
        res.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );
    }

    @Test
    public void testCleanupSVMRulesWithIncrementalKA() {
        KnowledgeAgent kAgent = initIncrementalKA();
        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );

        KnowledgeBase kBase = kAgent.getKnowledgeBase();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source5 );
        res.setResourceType( ResourceType.PMML );
        csAdd.addNewResource(res);

        System.out.println( "************************ ADDING resources ");

        kAgent.applyChangeSet( csAdd.getChangeSet() );

        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );


        System.out.println( "************************ REMOVING resource 1 ");

        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
        csRem.addRemovedResource( res );
        kAgent.applyChangeSet( csRem .getChangeSet() );

        kSession.fireAllRules();

        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );

        System.err.println( reportWMObjects( kSession ) );
        assertEquals( 0, kSession.getObjects().size() );
    }





    private StatefulKnowledgeSession loadModel( String source ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );

        knowledgeBuilder.add( ResourceFactory.newClassPathResource( source ), ResourceType.PMML );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        StatefulKnowledgeSession kSession = knowledgeBase.newStatefulKnowledgeSession();

        kSession.fireAllRules();
        return kSession;
    }

    private KnowledgeAgent initIncrementalKA() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        kaConfig.setProperty( NewInstanceOption.PROPERTY_NAME, "false" );
        kaConfig.setProperty( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME, "true" );
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "testPmml", kbase, kaConfig );
        return kagent;
    }



}
