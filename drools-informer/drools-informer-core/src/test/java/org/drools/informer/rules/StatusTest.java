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
package org.drools.informer.rules;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.informer.*;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.Variable;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;


public class StatusTest {

    private KnowledgeBase knowledgeBase;

    @Before
    public void setUp() throws Exception {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/informer-changeset.xml"), ResourceType.CHANGE_SET);
        if (knowledgeBuilder.hasErrors()) {
            System.err.println(knowledgeBuilder.getErrors());
        }
        assertFalse(knowledgeBuilder.hasErrors());
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
    }

    @Test
    public void testProgress() {
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        try {
            Questionnaire questionnaire = new Questionnaire("questionnaire");
			Group group1 = new Group("group1");
			Group group2 = new Group("group2");

            Question question1 = new Question("question1");
			    question1.setAnswerType(Question.QuestionType.TYPE_TEXT);
            Question question2 = new Question("question2");
			    question2.setAnswerType(Question.QuestionType.TYPE_TEXT);
            Question question3 = new Question("question3");
			    question3.setAnswerType(Question.QuestionType.TYPE_TEXT);
            Question question4 = new Question("question4");
			    question4.setAnswerType(Question.QuestionType.TYPE_TEXT);
            Question question5 = new Question("question5");
			    question5.setAnswerType(Question.QuestionType.TYPE_TEXT);



            questionnaire.setItems(new String[]{group1.getId(),group2.getId()});
            group1.setItems(new String[]{question1.getId(), question2.getId(), question3.getId() });
            group2.setItems(new String[]{question4.getId(), question5.getId() });


            knowledgeSession.insert( questionnaire );
            knowledgeSession.insert( group1 );
            knowledgeSession.insert( group2 );

            knowledgeSession.insert( question1 );
            knowledgeSession.insert( question2 );
            knowledgeSession.insert( question3 );
            knowledgeSession.insert( question4 );
            knowledgeSession.insert( question5 );
            knowledgeSession.fireAllRules();

            assertEquals( 0,
                          knowledgeSession.getQueryResults("progress", questionnaire.getId(), Variable.v).iterator().next().get( "$percent" ) );

            knowledgeSession.insert(new Answer(question1.getId(), "X"));
            knowledgeSession.fireAllRules();

            assertEquals( 20,
                          knowledgeSession.getQueryResults("progress", questionnaire.getId(), Variable.v).iterator().next().get( "$percent" ) );

            knowledgeSession.insert( new Answer( question4.getId(), "Y" ) );
            knowledgeSession.fireAllRules();

            assertEquals( 40,
                          knowledgeSession.getQueryResults("progress", questionnaire.getId(), Variable.v).iterator().next().get( "$percent" ) );

        } finally {
            knowledgeSession.dispose();
        }
    }

}

