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

import org.drools.ClassObjectFilter;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.informer.Answer;
import org.drools.informer.MultipleChoiceQuestion;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.Variable;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.*;


public class HumanTaskTest {


    StatefulKnowledgeSession kSession;
    String drl1 = "org/drools/informer/interaction/interaction.drl";
    String drl2 = "org/drools/informer/interaction/humanTask.drl";

    String drl3 = "org/drools/informer/interaction/humanTaskTest.drl";


    @Before
    public void setupSession() {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );

        kBuilder.add( new ClassPathResource( drl1 ), ResourceType.DRL );
        kBuilder.add( new ClassPathResource( drl2 ), ResourceType.DRL );
        kBuilder.add( new ClassPathResource( drl3 ), ResourceType.DRL );

        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );
        kSession = kBase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

    }

    @Test
    public void testTaskStateTransitionByQuestionnaire() {

        kSession.insert( "complexTask" );
        kSession.fireAllRules();

        FactType taskClass = kSession.getKnowledgeBase().getFactType("org.drools.informer.interaction", "InteractiveTask");
        FactType txHolderClass = kSession.getKnowledgeBase().getFactType("org.drools.informer.interaction", "TaskTransitionHolder");

        Object iTask = kSession.getObjects( new ClassObjectFilter( taskClass.getFactClass() ) ).iterator().next();
        Object iTxHolder = kSession.getObjects( new ClassObjectFilter( txHolderClass.getFactClass() ) ).iterator().next();

        String taskId = (String) taskClass.get( iTask, "taskId" );
        String tsId = (String) taskClass.get( iTask, "surveyableTxFId" );
        assertNotNull( taskId );
        assertNotNull( tsId );
        assertEquals( tsId, txHolderClass.get( iTxHolder, "questionnaireId" ) );
        assertEquals( taskId, txHolderClass.get( iTxHolder, "taskId" ) );

        MultipleChoiceQuestion transitions = (MultipleChoiceQuestion) kSession.getQueryResults( "getItem", "transition", tsId, Variable.v ).iterator().next().get( "$item" );
        MultipleChoiceQuestion owners = (MultipleChoiceQuestion) kSession.getQueryResults( "getItem", "owner", tsId, Variable.v ).iterator().next().get( "$item" );

        assertEquals( "CREATED", taskClass.get( iTask, "state" ).toString()  );
        assertEquals( null, taskClass.get( iTask, "owner" ) );
        assertEquals( 2, ((List) taskClass.get( iTask, "potentialOwners" )).size()  );
        assertEquals( true, taskClass.get( iTask, "surveyableTx" ) );
        assertEquals( false, taskClass.get( iTask, "surveyableState" ) );
        assertEquals( 2, owners.getNumOfPossibleAnswers() );
        assertEquals( 4, transitions.getNumOfPossibleAnswers() );
        assertArrayEquals( new String[] { "ACTIVATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues() );

//        report( kSession, System.err );
        System.out.println("-----");
        kSession.insert( new Answer( "transition", tsId, "ACTIVATE" ) );
        kSession.fireAllRules();


        assertEquals( "READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( null, taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "START", "SUSPEND", "CLAIM", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());



        kSession.insert( new Answer( "transition", tsId, "SUSPEND" ) );
        kSession.fireAllRules();

        assertEquals( "SUSPENDED_READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( null, taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "RESUME" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "RESUME" ) );
        kSession.fireAllRules();

        assertEquals( "READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( null, taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "START", "SUSPEND", "CLAIM", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "owner", tsId, "zxyads" ) );
        kSession.fireAllRules();

        assertEquals( "READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( null, taskClass.get( iTask, "owner" ) );
        assertEquals( null, txHolderClass.get( iTxHolder, "owner" ) );
        assertArrayEquals( new String[] { "START", "SUSPEND", "CLAIM", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "FORWARD" ) );
        kSession.fireAllRules();

        assertEquals( "READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( null, taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "START", "SUSPEND", "CLAIM", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());




        kSession.insert( new Answer( "owner", tsId, "dsotty" ) );
        kSession.fireAllRules();

        assertEquals( "READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( null, taskClass.get( iTask, "owner" ) );
        assertEquals( "dsotty", txHolderClass.get( iTxHolder, "owner" ) );
        assertArrayEquals( new String[] { "START", "SUSPEND", "CLAIM", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "FORWARD" ) );
        kSession.fireAllRules();

        assertEquals( "READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "dsotty", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "START", "SUSPEND", "CLAIM", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "CLAIM" ) );
        kSession.fireAllRules();

        assertEquals( "RESERVED", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "dsotty", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "SUSPEND", "START", "REVOKE", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "SUSPEND" ) );
        kSession.fireAllRules();

        assertEquals( "SUSPENDED_RESERVED", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "dsotty", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "RESUME" }, transitions.getPossibleAnswersValues());



        kSession.insert( new Answer( "transition", tsId, "RESUME" ) );
        kSession.fireAllRules();

        assertEquals( "RESERVED", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "dsotty", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "SUSPEND", "START", "REVOKE", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "REVOKE" ) );
        kSession.fireAllRules();

        assertEquals( "READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "dsotty", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "START", "SUSPEND", "CLAIM", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());



        kSession.insert( new Answer( "owner", tsId, "davide" ) );
        kSession.fireAllRules();

        kSession.insert( new Answer( "transition", tsId, "CLAIM" ) );
        kSession.fireAllRules();

        assertEquals( "RESERVED", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "SUSPEND", "START", "REVOKE", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "owner", tsId, "dsotty" ) );
        kSession.fireAllRules();

        kSession.insert( new Answer( "transition", tsId, "DELEGATE" ) );
        kSession.fireAllRules();

        assertEquals( "RESERVED", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "dsotty", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "SUSPEND", "START", "REVOKE", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "START" ) );
        kSession.fireAllRules();

        assertEquals( "IN_PROGRESS", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "dsotty", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "SUSPEND", "STOP", "COMPLETE", "FAIL", "DELEGATE", "REVOKE", "FORWARD", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "owner", tsId, "davide" ) );
        kSession.fireAllRules();

        kSession.insert( new Answer( "transition", tsId, "FORWARD" ) );
        kSession.fireAllRules();

        assertEquals( "READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "START", "SUSPEND", "CLAIM", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());



        kSession.insert( new Answer( "transition", tsId, "START" ) );
        kSession.fireAllRules();

        assertEquals( "IN_PROGRESS", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "SUSPEND", "STOP", "COMPLETE", "FAIL", "DELEGATE", "REVOKE", "FORWARD", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "SUSPEND" ) );
        kSession.fireAllRules();

        assertEquals( "SUSPENDED_IN_PROGRESS", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "RESUME" }, transitions.getPossibleAnswersValues());

        kSession.insert( new Answer( "transition", tsId, "RESUME" ) );
        kSession.fireAllRules();

        assertEquals( "IN_PROGRESS", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "SUSPEND", "STOP", "COMPLETE", "FAIL", "DELEGATE", "REVOKE", "FORWARD", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());



        kSession.insert( new Answer( "transition", tsId, "STOP" ) );
        kSession.fireAllRules();

        assertEquals( "RESERVED", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "SUSPEND", "START", "REVOKE", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "START" ) );
        kSession.fireAllRules();

        assertEquals( "IN_PROGRESS", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "SUSPEND", "STOP", "COMPLETE", "FAIL", "DELEGATE", "REVOKE", "FORWARD", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "COMPLETE" ) );
        kSession.fireAllRules();

        assertEquals( "COMPLETED", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { }, transitions.getPossibleAnswersValues());

        kSession.retract( kSession.getFactHandle( iTask ) );
        kSession.fireAllRules();

        report( kSession, System.err );

        assertEquals( 0, kSession.getObjects().size() );

    }




    @Test
    public void testTaskStateSimpleTransitionByQuestionnaire() {

        kSession.insert( "simpleTask" );
        kSession.fireAllRules();

        FactType taskClass = kSession.getKnowledgeBase().getFactType("org.drools.informer.interaction", "InteractiveTask");
        FactType txHolderClass = kSession.getKnowledgeBase().getFactType("org.drools.informer.interaction", "TaskTransitionHolder");

        Object iTask = kSession.getObjects( new ClassObjectFilter( taskClass.getFactClass() ) ).iterator().next();
        Object iTxHolder = kSession.getObjects( new ClassObjectFilter( txHolderClass.getFactClass() ) ).iterator().next();

        String taskId = (String) taskClass.get( iTask, "taskId" );
        String tsId = (String) taskClass.get( iTask, "surveyableTxFId" );
        assertNotNull( taskId );
        assertNotNull( tsId );
        assertEquals( tsId, txHolderClass.get( iTxHolder, "questionnaireId" ) );
        assertEquals( taskId, txHolderClass.get( iTxHolder, "taskId" ) );

        MultipleChoiceQuestion transitions = (MultipleChoiceQuestion) kSession.getQueryResults( "getItem", "transition", tsId, Variable.v ).iterator().next().get( "$item" );
        MultipleChoiceQuestion owners = (MultipleChoiceQuestion) kSession.getQueryResults( "getItem", "owner", tsId, Variable.v ).iterator().next().get( "$item" );

        assertEquals( "RESERVED", taskClass.get( iTask, "state" ).toString()  );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertEquals( null, taskClass.get( iTask, "potentialOwners" ) );
        assertEquals( true, taskClass.get( iTask, "surveyableTx" ) );
        assertEquals( false, taskClass.get( iTask, "surveyableState" ) );
        assertEquals( 0, owners.getNumOfPossibleAnswers() );
        assertEquals( 1, transitions.getNumOfPossibleAnswers() );
        assertArrayEquals( new String[] { "START" }, transitions.getPossibleAnswersValues() );


        kSession.insert( new Answer( "transition", tsId, "START" ) );
        kSession.fireAllRules();

        assertEquals( "IN_PROGRESS", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "STOP", "COMPLETE", "FAIL" }, transitions.getPossibleAnswersValues());


        kSession.insert( new Answer( "transition", tsId, "COMPLETE" ) );
        kSession.fireAllRules();

        assertEquals( "COMPLETED", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { }, transitions.getPossibleAnswersValues());

        kSession.retract( kSession.getFactHandle( iTask ) );
        kSession.fireAllRules();

        report( kSession, System.err );

        assertEquals( 0, kSession.getObjects().size() );

    }




    private void report( StatefulKnowledgeSession kSession, PrintStream out ) {
        out.println(" -------------------------------- " + kSession.getObjects().size() + " ----------------------------" );
        for ( Object o : kSession.getObjects() ) {
            out.println( "**" +  o );
        }
        out.println(" -------------------------------- " + kSession.getObjects().size() + " ----------------------------" );
    }

}
