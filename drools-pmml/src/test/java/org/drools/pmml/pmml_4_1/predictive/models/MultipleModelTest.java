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


import org.drools.ClassObjectFilter;
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
import org.drools.factmodel.traits.Thing;
import org.drools.informer.Questionnaire;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.io.impl.ClassPathResource;
import org.drools.pmml.pmml_4_1.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_1.ModelMarker;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.Variable;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MultipleModelTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_1//mock_ptsd.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_1//mock_cold.xml";
    private static final String source3 = "org/drools/pmml/pmml_4_1//mock_breastcancer.xml";
    private static final String source4 = "org/drools/pmml/pmml_4_1//test_svm.xml";

    private static final String packageName = "org.drools.pmml.pmml_4_1.test";






    @Test
    public void testInsertLogicalOnInitialFact() {

        String s1 = "package org.drools.test.agent;\n" +
                "import org.drools.pmml.pmml_4_1.ModelMarker; \n" +
                "rule \"Init 1\"\n" +
                "when\n" +
                "then\n" +
                "  insertLogical( new ModelMarker( \"x\", \"xx\" ) );\n" +
                "end\n" +
                "" +
                "rule MarkerLog\n" +
                "when\n" +
                " $m : ModelMarker()\n" +
                "then\n" +
                "System.out.println( $m );\n" +
                "end\n";


        String s2 = "package org.drools.test.agent;\n" +
                "import org.drools.pmml.pmml_4_1.ModelMarker; \n" +
                "rule \"Init 2\"\n" +
                "when\n" +
                "then\n" +
                "  insertLogical( new ModelMarker( \"y\", \"yy\" ) );\n" +
                "end";


        String s3 = "package org.drools.test.agent; \n" +
                "import org.drools.pmml.pmml_4_1.ModelMarker; \n" +
                "import org.drools.informer.ISurveyableTrait; \n" +
                "rule \"TraitX\"\n" +
                "salience 1000\n" +
                "when\n" +
                "    $mm : ModelMarker( modelName == \"x\" , enabled == true )\n" +
                "then\n" +
                "    ISurveyableTrait surv = don( $mm, ISurveyableTrait.class, true );\n" +
                "    modify ( surv ) {\n" +
                "        setQuestionnaireId( \"xid\" ),\n" +
                "        setStateful( false ),\n" +
                "        setSurveyEnabled( true );\n" +
                "    }\n" +
                "end\n";





        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );
        kbuilder.add( new ByteArrayResource( s3.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        kaConfig.setProperty( NewInstanceOption.PROPERTY_NAME, "false" );
        kaConfig.setProperty( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME, "true" );
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "testPmml", kbase, kaConfig );

        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

        System.out.println("---------------------------------------------------------------------");

        ByteArrayResource bres = (ByteArrayResource) ResourceFactory.newByteArrayResource( s1.getBytes() );
        bres.setResourceType( ResourceType.DRL );

        ChangeSetImpl cs = new ChangeSetImpl();
        cs.setResourcesAdded(Arrays.<Resource>asList(bres));
        kagent.applyChangeSet(cs);

        ksession.fireAllRules();

        System.out.println("---------------------------------------------------------------------");


        ByteArrayResource bres2 = (ByteArrayResource) ResourceFactory.newByteArrayResource( s2.getBytes() );
        bres2.setResourceType( ResourceType.DRL );

        ChangeSetImpl cs2 = new ChangeSetImpl();
        cs2.setResourcesAdded( Arrays.<Resource> asList( bres2 ) );
        kagent.applyChangeSet( cs2 );
        ksession.fireAllRules();


        System.out.println("---------------------------------------------------------------------");

        ksession.fireAllRules();

        System.out.println("---------------------------------------------------------------------");


        for ( Object o : ksession.getObjects() ) {
            System.out.println( "**" +  o );
        }

        assertEquals( 5, ksession.getObjects().size() );
        assertEquals( 2, ksession.getObjects(new ClassObjectFilter( ModelMarker.class )).size() );
        assertEquals( 2, ksession.getObjects(new ClassObjectFilter( Thing.class )).size() );

        ksession.dispose();
        kagent.dispose();
    }




    @Test
    public void testIncrementalBuilding() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );


        KnowledgeBuilder kb1 = KnowledgeBuilderFactory.newKnowledgeBuilder( kbase );
        kb1.add( ResourceFactory.newClassPathResource( source1 ), ResourceType.PMML );
        if ( kb1.hasErrors() ) { fail( kb1.getErrors().toString() ); }

        kbase.addKnowledgePackages( kb1.getKnowledgePackages() );


        KnowledgeBuilder kb2 = KnowledgeBuilderFactory.newKnowledgeBuilder( kbase );
        kb2.add( ResourceFactory.newClassPathResource( source2 ), ResourceType.PMML );
        if ( kb2.hasErrors() ) { fail( kb2.getErrors().toString() ); }

        kbase.addKnowledgePackages( kb2.getKnowledgePackages() );

        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 2, kSession.getObjects( new ClassObjectFilter( ModelMarker.class ) ).size() );
        assertEquals( 2, kSession.getObjects( new ClassObjectFilter( Questionnaire.class ) ).size() );
        assertEquals( 11, kSession.getObjects( new ClassObjectFilter( kSession.getKnowledgeBase().getFactType( packageName, "Synapse" ).getFactClass() ) ).size() );

        kSession.dispose();

    }







    
    @Test
    public void testKnowledgeAgentLoadingMultipleANN() throws Exception {
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


        StatefulKnowledgeSession kSession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        assertNotNull(kSession);

        ChangeSetHelperImpl cs;
        ClassPathResource res;

        cs = new ChangeSetHelperImpl();
        res = (ClassPathResource) ResourceFactory.newClassPathResource( source1 );
        res.setResourceType( ResourceType.PMML );
        cs.addNewResource( res );
        kagent.applyChangeSet( cs.getChangeSet() );
        kSession.fireAllRules();


        System.out.println( " \n\n\n DONE LOADING " + source1 + " \n\n\n " );

        QueryResults q1 = kSession.getQueryResults( "getQuestionnaireByType", "MockPTSD", Variable.v );
        assertEquals( 1, q1.size() );
        Questionnaire ptsdQ = (Questionnaire) q1.iterator().next().get( "$quest" );

        cs = new ChangeSetHelperImpl();
        res = (ClassPathResource) ResourceFactory.newClassPathResource( source2 );
        res.setResourceType( ResourceType.PMML );
        cs.addNewResource( res );
        kagent.applyChangeSet( cs.getChangeSet() );
        kSession.fireAllRules();

        System.out.println( " \n\n\n DONE LOADING " + source2 + " \n\n\n " );

        cs = new ChangeSetHelperImpl();
        res = (ClassPathResource) ResourceFactory.newClassPathResource( source3 );
        res.setResourceType( ResourceType.PMML );
        cs.addNewResource( res );
        kagent.applyChangeSet( cs.getChangeSet() );
        kSession.fireAllRules();

        System.out.println( " \n\n\n DONE LOADING " + source3 + " \n\n\n " );

        kSession.fireAllRules();

        QueryResults q2 = kSession.getQueryResults( "getQuestionnaireByType", "MockPTSD", Variable.v );
        assertEquals( 1, q2.size() );
        Questionnaire ptsdQ2 = (Questionnaire) q2.iterator().next().get( "$quest" );

        assertSame( ptsdQ, ptsdQ2 );

        System.err.println(reportWMObjects(kSession));

        assertEquals( 3, kSession.getObjects( new ClassObjectFilter( ModelMarker.class ) ).size() );
        assertEquals( 3, kSession.getObjects( new ClassObjectFilter( Questionnaire.class ) ).size() );
        assertEquals( 23, kSession.getObjects( new ClassObjectFilter( kSession.getKnowledgeBase().getFactType( packageName, "Synapse" ).getFactClass() ) ).size() );


        kSession.dispose();
        kagent.dispose();

    }





    @Test
    public void testKnowledgeAgentLoadingMix() throws Exception {
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

        StatefulKnowledgeSession kSession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
        assertNotNull(kSession);

        ChangeSetHelperImpl cs;
        ClassPathResource res;

        cs = new ChangeSetHelperImpl();
        res = (ClassPathResource) ResourceFactory.newClassPathResource( source1 );
        res.setResourceType( ResourceType.PMML );
        cs.addNewResource( res );
        kagent.applyChangeSet( cs.getChangeSet() );
        kSession.fireAllRules();


        System.out.println( " \n\n\n DONE LOADING " + source1 + " \n\n\n " );

        cs = new ChangeSetHelperImpl();
        res = (ClassPathResource) ResourceFactory.newClassPathResource( source4 );
        res.setResourceType( ResourceType.PMML );
        cs.addNewResource( res );
        kagent.applyChangeSet( cs.getChangeSet() );
        kSession.fireAllRules();


        System.out.println( " \n\n\n DONE LOADING " + source4 + " \n\n\n " );

        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 2, kSession.getObjects( new ClassObjectFilter( ModelMarker.class ) ).size() );
        assertEquals( 1, kSession.getObjects( new ClassObjectFilter( Questionnaire.class ) ).size() );
        assertEquals( 9, kSession.getObjects( new ClassObjectFilter( kSession.getKnowledgeBase().getFactType( packageName, "Synapse" ).getFactClass() ) ).size() );
        assertEquals( 4, kSession.getObjects( new ClassObjectFilter( kSession.getKnowledgeBase().getFactType( packageName, "SupportVector" ).getFactClass() ) ).size() );

        kSession.dispose();
        kagent.dispose();
    }


}
