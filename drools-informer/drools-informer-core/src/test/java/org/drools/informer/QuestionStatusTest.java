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

package org.drools.informer;


import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.builder.ResourceType;
import org.drools.core.util.Iterator;
import org.drools.informer.generator.annotations.QuestionMark;
import org.drools.io.Resource;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResultsRow;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class QuestionStatusTest {




    @Test
    public void testValidityStatus() throws NoSuchFieldException {

        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        kaConfig.setProperty("drools.agent.newInstance","false");
        KnowledgeAgent kAgent = KnowledgeAgentFactory.newKnowledgeAgent("testAnnotationKA",kaConfig);

        kAgent.setSystemEventListener( new PrintStreamSystemEventListener());

        ChangeSetImpl changeSet = new ChangeSetImpl();
        ClassPathResource res1 = new ClassPathResource("org/drools/informer/informer-changeset.xml");
        res1.setResourceType(ResourceType.CHANGE_SET);

        changeSet.setResourcesAdded(Arrays.asList((Resource) res1));

        kAgent.applyChangeSet(changeSet);




        StatefulKnowledgeSession kSession = kAgent.getKnowledgeBase().newStatefulKnowledgeSession();
        kSession.setGlobal("kAgent",kAgent);


        Person p1 = new Person("0001",null,18);
        kSession.insert(p1);
        kSession.fireAllRules();

        assertEquals( 5, kSession.getQueryResults("invalidAnswers").size() );
        assertEquals( 1, kSession.getQueryResults("answeredQuestions").size() );
        assertEquals( 0, kSession.getQueryResults("missingAnswers").size() );


        Answer ans = new Answer("age",p1.getQuestionnaireId(),"44");
        kSession.insert(ans);
        kSession.fireAllRules();

        assertEquals( 5, kSession.getQueryResults("invalidAnswers").size() );
        assertEquals( 1, kSession.getQueryResults("answeredQuestions").size() );
        assertEquals( 0, kSession.getQueryResults("missingAnswers").size() );



        Answer ans2 = new Answer("name",p1.getQuestionnaireId(),"joe");
        kSession.insert(ans2);
        kSession.fireAllRules();

        assertEquals( 4, kSession.getQueryResults("invalidAnswers").size() );
        assertEquals( 2, kSession.getQueryResults("answeredQuestions").size() );
        assertEquals( 0, kSession.getQueryResults("missingAnswers").size() );



        Answer ans3 = new Answer("age",p1.getQuestionnaireId(),"null");
        kSession.insert(ans3);
        kSession.fireAllRules();

        assertEquals( 4, kSession.getQueryResults("invalidAnswers").size() );
        assertEquals( 1, kSession.getQueryResults("answeredQuestions").size() );
        assertEquals( 1, kSession.getQueryResults("missingAnswers").size() );



        Answer ans4 = new Answer("hobbies",p1.getQuestionnaireId(),"Reading");
        kSession.insert(ans4);
        kSession.fireAllRules();

        assertEquals( 3, kSession.getQueryResults("invalidAnswers").size() );
        assertEquals( 2, kSession.getQueryResults("answeredQuestions").size() );
        assertEquals( 1, kSession.getQueryResults("missingAnswers").size() );



        Answer ans5 = new Answer( "hobbies", p1.getQuestionnaireId(), null );
        kSession.insert(ans5);
        kSession.fireAllRules();

        assertEquals( 4, kSession.getQueryResults("invalidAnswers").size() );
        assertEquals( 1, kSession.getQueryResults("answeredQuestions").size() );
        assertEquals( 1, kSession.getQueryResults("missingAnswers").size() );



        System.out.println(p1);

//        for ( Object o : kSession.getObjects() ) {
//            System.err.println(o);
//        }

//
//        java.util.Iterator<QueryResultsRow> iter1 = kSession.getQueryResults("invalidAnswers").iterator();
//        while ( iter1.hasNext() ) {
//            System.out.println( "IA " + iter1.next().get("$ia") );
//        }
//
//        java.util.Iterator<QueryResultsRow> iter2 = kSession.getQueryResults("answeredQuestions").iterator();
//        while ( iter2.hasNext() ) {
//            System.out.println( "AQ " + iter2.next().get("question") );
//        }




    }

}
