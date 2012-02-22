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

import org.drools.*;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.type.FactType;
import org.drools.informer.Answer;
import org.drools.informer.MultipleChoiceQuestion;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.Variable;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;


public class TaskInteractionTest {


    StatefulKnowledgeSession kSession;
    String drl1 = "org/drools/informer/interaction/humanTask.drl";
    String drl2 = "org/drools/informer/interaction/interaction.drl";
    String drl3 = "org/drools/informer/interaction/quests.drl";

    String testDrl = "org/drools/informer/interaction/interactionTest.drl";


    @Before
    public void setupSession() {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );

        kBuilder.add( new ClassPathResource( drl1 ), ResourceType.DRL );
        kBuilder.add( new ClassPathResource( drl2 ), ResourceType.DRL );
        kBuilder.add( new ClassPathResource( drl3 ), ResourceType.DRL );
        kBuilder.add( new ClassPathResource( testDrl ), ResourceType.DRL );

        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }

        KnowledgeBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbconf.setOption( AssertBehaviorOption.EQUALITY );
        kbconf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase( kbconf );
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setProperty(ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.toExternalForm());
        KnowledgeSessionConfiguration ksessionConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        kSession = kBase.newStatefulKnowledgeSession( ksessionConf, null );

        kSession.setGlobal( "list", new ArrayList() );
        kSession.setGlobal( "taskLog", new ArrayList() );

        kSession.fireAllRules();

    }




    //TODO : Overarching task (interaction) with connected surveys

    //TODO : Overarching task without connected surveys, but externally triggered actions





    @Test
    public void testSurveyTaskWithInteractionWithoutAlerting() {

        kSession.insert( "surveytaskinter" );
        kSession.fireAllRules();


        String mainTaskId = getMainTaskQuestId( kSession );
        String questionId = getTransitionQuestion( kSession, mainTaskId );

        System.out.println( "@@@@@@@@@@@" + questionId );

        kSession.insert( new Answer( "transition", mainTaskId, "START" ) );
        kSession.fireAllRules();

        kSession.insert( new Answer( "question2", getSurveyId( "SurveyBean" ), "someValue" ) );
        kSession.fireAllRules();



        report( kSession, System.err );

//        assertEquals( Arrays.asList( "actor1", "actor2" ), kSession.getGlobal( "list" ) );

        System.err.println( kSession.getGlobal( "list" ) );
        System.err.println( kSession.getGlobal( "taskLog" ) );

        assertEquals( 8, ((List) kSession.getGlobal( "taskLog" )).size() );
        assertEquals( 0, kSession.getObjects().size() );

        kSession.dispose();
    }


    private String getTransitionQuestion(StatefulKnowledgeSession kSession, String mainTaskId) {
        return (String) kSession.getQueryResults( "getItemId", "transition", mainTaskId, Variable.v ).iterator().next().get( "$id" );
//        return "";
    }


    private String getSurveyId( String className ) {
        FactType sKlass = kSession.getKnowledgeBase().getFactType( "org.drools.informer.interaction", className );
        Collection beans = kSession.getObjects( new ClassObjectFilter( sKlass.getFactClass() ) );
        Object bean = beans.iterator().next();
        return (String) sKlass.get( bean, "questionnaireId" );
    }


    private String getMainTaskQuestId(StatefulKnowledgeSession kSession) {
        FactType itKlass = kSession.getKnowledgeBase().getFactType( "org.drools.informer.interaction", "InteractiveTask" );
        Collection interTasks = kSession.getObjects( new ClassObjectFilter( itKlass.getFactClass() ) );
        assertEquals( 1, interTasks.size() );
        Object mainTask = interTasks.iterator().next();
        return (String) itKlass.get( mainTask, "controlQuestId" );
    }


    @Test
    public void testInteractionWithDelegation() {


        Thread slave = new Thread() {
            public void run() {
                kSession.insert( "delegation test" );
                kSession.fireUntilHalt();
            }
        };

        slave.start();


        try {
            Thread.sleep( 3000 );
        } catch (InterruptedException e) {
            fail();
        }

        kSession.halt();

        try {
            slave.join();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        report( kSession, System.err );

        assertEquals( 0, kSession.getObjects().size() );
        assertEquals( Arrays.asList( "actor1", "actor2" ), kSession.getGlobal( "list" ) );

        assertEquals( 6, ((List) kSession.getGlobal( "taskLog" )).size() );


        System.err.println( kSession.getGlobal( "list" ) );
        System.err.println( kSession.getGlobal( "taskLog" ) );

        kSession.dispose();
    }




    @Test
    /**
     * Use case:
     *  where a Q is associated to a task.
     *  The Q is removed before the task is completed,
     *  so the task has to be updated accordingly
     */
    public void testSurveyTaskCancellation() {

        kSession.insert( "surveytask" );
        kSession.fireAllRules();

        kSession.insert( "removesurvey" );
        kSession.fireAllRules();


        report( kSession, System.err );
        System.err.println( kSession.getGlobal( "list" ) );
        System.err.println( kSession.getGlobal( "taskLog" ) );

        assertEquals( 0, kSession.getObjects().size() );

        assertEquals( 4, ((List) kSession.getGlobal( "taskLog" )).size() );

        kSession.dispose();
    }




    @Test
    /**
     * Use case:
     *  simple questionnaire, without any associated task
     *  The questionnaire is used to convey info into the WM
     *  The survey can also show info to the user.
     *
     *  The Q "lives" even after being completed
     */
    public void testStatelessSurvey() {

        kSession.insert( "survey" );
        kSession.fireAllRules();

        kSession.insert( new Answer( "question2", "testId", "someValue" ) );
        kSession.fireAllRules();


        report( kSession, System.err );
        assertEquals( 15, kSession.getObjects().size() );

        System.err.println( kSession.getGlobal( "list" ) );
        System.err.println( kSession.getGlobal( "taskLog" ) );


        assertEquals( 0, ((List) kSession.getGlobal( "taskLog" )).size() );
        assertEquals( 0, ((List) kSession.getGlobal( "list" )).size() );


        kSession.dispose();
    }



    @Test
    /**
     * Use case:
     *  "stateful" questionnaire + task
     *  The task monitors the Q's progress, which generates events
     *  to drive the task state
     */
    public void testSurveyTask() {

        kSession.insert( "surveytask" );
        kSession.fireAllRules();

        kSession.insert( new Answer( "question2", "testId", "someValue" ) );
        kSession.fireAllRules();


        report( kSession, System.err );
        System.err.println( kSession.getGlobal( "list" ) );
        System.err.println( kSession.getGlobal( "taskLog" ) );

        assertEquals( 0, kSession.getObjects().size() );

        assertEquals( 4, ((List) kSession.getGlobal( "taskLog" )).size() );


        kSession.dispose();
    }










    @Test
    public void testInteraction() {


        Thread slave = new Thread() {
            public void run() {
                kSession.insert( "interaction test" );
                kSession.fireUntilHalt();
            }
        };

        slave.start();


        try {
            Thread.sleep( 5000 );
        } catch (InterruptedException e) {
            fail();
        }

        kSession.halt();

        try {
            slave.join();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        report( kSession, System.err );
        assertEquals( 0, kSession.getObjects().size() );

        System.err.println( kSession.getGlobal( "list" ) );
        System.err.println( kSession.getGlobal( "taskLog" ) );

        assertEquals(Arrays.asList("actor1", "main1", "actor2", "alien2", "actor3"), kSession.getGlobal("list"));
        assertEquals( 14, ((List) kSession.getGlobal( "taskLog" )).size() );

        kSession.dispose();
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
        String tsId = (String) taskClass.get( iTask, "controlQuestId" );
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

//
        System.out.println("-----");
        kSession.insert( new Answer( "transition", tsId, "ACTIVATE" ) );
        kSession.fireAllRules();


        assertEquals( "READY", taskClass.get( iTask, "state" ).toString() );
        assertEquals( null, taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "START", "SUSPEND", "CLAIM", "FORWARD", "DELEGATE", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());



        kSession.insert( new Answer( "transition", tsId, "SUSPEND" ) );
        kSession.fireAllRules();
        report( kSession, System.err );
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
        assertEquals( "dsotty", taskClass.get(iTask, "owner") );
        System.out.println( Arrays.asList(transitions.getPossibleAnswersValues()) );
        assertArrayEquals(new String[]{"SUSPEND", "STOP", "COMPLETE", "FAIL", "DELEGATE", "REVOKE", "FORWARD", "EXIT", "SKIP", "ERROR"}, transitions.getPossibleAnswersValues());


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
        String tsId = (String) taskClass.get( iTask, "controlQuestId" );
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
        assertEquals( 4, transitions.getNumOfPossibleAnswers() );
        assertArrayEquals( new String[] { "START", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues() );


        kSession.insert( new Answer( "transition", tsId, "START" ) );
        kSession.fireAllRules();

        assertEquals( "IN_PROGRESS", taskClass.get( iTask, "state" ).toString() );
        assertEquals( "davide", taskClass.get( iTask, "owner" ) );
        assertArrayEquals( new String[] { "STOP", "COMPLETE", "FAIL", "EXIT", "SKIP", "ERROR" }, transitions.getPossibleAnswersValues());


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
