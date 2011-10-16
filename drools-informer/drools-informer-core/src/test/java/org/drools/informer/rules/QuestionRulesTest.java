/*
 * Copyright 2009 Solnet Solutions Limited (http://www.solnetsolutions.co.nz/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.informer.rules;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.drools.ClassObjectFilter;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.informer.*;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.informer.MultipleChoiceQuestion.PossibleAnswer;

import static org.junit.Assert.*;

/**
 * @author Damon
 *
 */
public class QuestionRulesTest {

    private static final Logger logger = LoggerFactory.getLogger(QuestionRulesTest.class);

    private KnowledgeBase knowledgeBase;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/Queries.drl"), ResourceType.DRL);
        knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/Question.drl"), ResourceType.DRL);
        if (knowledgeBuilder.hasErrors()) {
            logger.debug(Arrays.toString(knowledgeBuilder.getErrors().toArray()));
            System.err.println(knowledgeBuilder.getErrors());
        }
        assertFalse(knowledgeBuilder.hasErrors());
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
    }

    @Test
    public void testRetractPriorErrors() {
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        try {
            Question question1 = new Question("question1");
            question1.setAnswerType(Question.QuestionType.TYPE_TEXT);
            Question question2 = new Question("question2");
            question2.setAnswerType(Question.QuestionType.TYPE_TEXT);
            InvalidAnswer invalidAnswer1 = new InvalidAnswer("question1", "some reason");
            InvalidAnswer invalidAnswer2 = new InvalidAnswer("question2", "some other reason");
            Answer answer1 = new Answer("question1", "value");
            knowledgeSession.insert(question1);
            knowledgeSession.insert(question2);
            knowledgeSession.insert(invalidAnswer1);
            knowledgeSession.insert(invalidAnswer2);
            knowledgeSession.insert(answer1);
            knowledgeSession.fireAllRules();
            assertFalse(knowledgeSession.getObjects().contains(invalidAnswer1));
            assertTrue(knowledgeSession.getObjects().contains(invalidAnswer2));
        } finally {
            knowledgeSession.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCopyAnswersToQuestions() {
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        try {
            Question question1 = new Question("question1");
            question1.setAnswerType(Question.QuestionType.TYPE_TEXT );
            Question question2 = new Question("question2");
            question2.setAnswerType(Question.QuestionType.TYPE_NUMBER);
            Question question3 = new Question("question3");
            question3.setAnswerType(Question.QuestionType.TYPE_DECIMAL);
            Question question4 = new Question("question4");
            question4.setAnswerType(Question.QuestionType.TYPE_BOOLEAN);
            Question question5 = new Question("question5");
            question5.setAnswerType(Question.QuestionType.TYPE_DATE);
            Question question6 = new Question("question6");
            question6.setAnswerType(Question.QuestionType.TYPE_NUMBER );
            question6.setNumberAnswer(456L);
            Question question7 = new Question("question7");
            question7.setAnswerType(Question.QuestionType.TYPE_DATE);
            Question question8 = new Question("question8");
            question8.setAnswerType(Question.QuestionType.TYPE_NUMBER);
            question8.setNumberAnswer(789L);
            Answer answer1 = new Answer(question1.getId(), "value1");
            Answer answer2 = new Answer(question2.getId(), "123");
            Answer answer3 = new Answer(question3.getId(), "4.56");
            Answer answer4 = new Answer(question4.getId(), "true");
            Answer answer5 = new Answer(question5.getId(), "1971-05-30");
            Answer answer6 = new Answer(question6.getId(), "123x");
            Answer answer7 = new Answer(question7.getId(), "hello");
            Answer answer8 = new Answer(question8.getId(), null);
            knowledgeSession.insert(question1);
            knowledgeSession.insert(question2);
            knowledgeSession.insert(question3);
            knowledgeSession.insert(question4);
            knowledgeSession.insert(question5);
            knowledgeSession.insert(question6);
            knowledgeSession.insert(question7);
            knowledgeSession.insert(question8);
            knowledgeSession.insert(answer1);
            knowledgeSession.insert(answer2);
            knowledgeSession.insert(answer3);
            knowledgeSession.insert(answer4);
            knowledgeSession.insert(answer5);
            knowledgeSession.insert(answer6);
            knowledgeSession.insert(answer7);
            knowledgeSession.insert(answer8);
            knowledgeSession.fireAllRules();
            assertFalse(knowledgeSession.getObjects().contains(answer1));
            assertFalse(knowledgeSession.getObjects().contains(answer2));
            assertFalse(knowledgeSession.getObjects().contains(answer3));
            assertFalse(knowledgeSession.getObjects().contains(answer4));
            assertFalse(knowledgeSession.getObjects().contains(answer5));
            assertFalse(knowledgeSession.getObjects().contains(answer6));
            assertFalse(knowledgeSession.getObjects().contains(answer7));
            assertFalse(knowledgeSession.getObjects().contains(answer8));
            assertEquals("value1", question1.getAnswer());
            assertEquals(123L, question2.getAnswer());
            assertEquals(new BigDecimal("4.56"), question3.getAnswer());
            assertEquals(true, question4.getAnswer());
            assertEquals(new SimpleDateFormat("dd/MM/yyyy").parse("30/05/1971"), question5.getAnswer());
            assertEquals(456L, question6.getAnswer());
            assertEquals(null, question7.getAnswer());
            assertEquals(null, question8.getAnswer());
            Collection<?> invalidAnswers = new ArrayList(knowledgeSession.getObjects(new ClassObjectFilter(InvalidAnswer.class)));
            assertEquals(2, invalidAnswers.size());
            assertTrue(invalidAnswers.contains(new InvalidAnswer(question6.getId(), "This is not a valid number")));
            assertTrue(invalidAnswers.contains(new InvalidAnswer(question7.getId(), "This is not a valid date")));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            knowledgeSession.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRequiredQuestionsAnswered() {
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        try {
            Question question1 = new Question("question1");
            question1.setAnswerType(Question.QuestionType.TYPE_TEXT);
            question1.setRequired(true);
            Question question2 = new Question("question2");
            question2.setAnswerType(Question.QuestionType.TYPE_TEXT);
            question2.setRequired(true);
            Question question3 = new Question("question3");
            question3.setAnswerType(Question.QuestionType.TYPE_TEXT);
            Answer answer1 = new Answer(question1.getId(), "value1");
            knowledgeSession.insert(question1);
            knowledgeSession.insert(question2);
            knowledgeSession.insert(question3);
            knowledgeSession.fireAllRules();
            Collection<?> invalidAnswers = new ArrayList(knowledgeSession.getObjects(new ClassObjectFilter(InvalidAnswer.class)));
            assertEquals(2, invalidAnswers.size());
            assertTrue(invalidAnswers.contains(new InvalidAnswer(question1.getId(), "You must answer this question")));
            assertTrue(invalidAnswers.contains(new InvalidAnswer(question2.getId(), "You must answer this question")));
            knowledgeSession.insert(answer1);
            knowledgeSession.fireAllRules();
            assertFalse(knowledgeSession.getObjects().contains(answer1));
            invalidAnswers = new ArrayList(knowledgeSession.getObjects(new ClassObjectFilter(InvalidAnswer.class)));
            assertEquals(1, invalidAnswers.size());
            assertTrue(invalidAnswers.contains(new InvalidAnswer(question2.getId(), "You must answer this question")));
        } finally {
            knowledgeSession.dispose();
        }
    }

    @Test
    public void testRetractAnswers() {
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        try {
            Question question1 = new Question("question1");
            question1.setAnswerType(Question.QuestionType.TYPE_TEXT);
            Question question2 = new Question("question2");
            question2.setAnswerType(Question.QuestionType.TYPE_NUMBER);
            Answer answer1 = new Answer(question1.getId(), "value");
            Answer answer2 = new Answer(question2.getId(), "value");
            Answer answer3 = new Answer("question3UUID", "value");
            knowledgeSession.insert(question1);
            knowledgeSession.insert(question2);
            knowledgeSession.insert(answer1);
            knowledgeSession.insert(answer2);
            knowledgeSession.insert(answer3);
            knowledgeSession.fireAllRules();
            assertFalse(knowledgeSession.getObjects().contains(answer1));
            assertFalse(knowledgeSession.getObjects().contains(answer2));
            assertFalse(knowledgeSession.getObjects().contains(answer3));
        } finally {
            knowledgeSession.dispose();
        }
    }

    @Test
    public void testDiscardInvalidMultipleChoiceAnswer() {
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        try {
            MultipleChoiceQuestion question1 = new MultipleChoiceQuestion("question1");
            question1.setAnswerType(Question.QuestionType.TYPE_TEXT);
            question1.setPossibleAnswers(new PossibleAnswer[] { new PossibleAnswer(null, "select..."),
                    new PossibleAnswer("a", "apple"), new PossibleAnswer("b", "banana") });
            MultipleChoiceQuestion question2 = new MultipleChoiceQuestion("question2");
            question2.setAnswerType(Question.QuestionType.TYPE_TEXT);
            question2.setPossibleAnswers(new PossibleAnswer[] { new PossibleAnswer(null, "select..."),
                    new PossibleAnswer("a", "apple"), new PossibleAnswer("b", "banana") });
            MultipleChoiceQuestion question3 = new MultipleChoiceQuestion("question3");
            question3.setAnswerType(Question.QuestionType.TYPE_TEXT);
            question3.setPossibleAnswers(new PossibleAnswer[] { new PossibleAnswer(null, "select..."),
                    new PossibleAnswer("a", "apple"), new PossibleAnswer("b", "banana") });
            Answer answer1 = new Answer(question1.getId(), "a");
            Answer answer2 = new Answer(question2.getId(), "d");
            Answer answer3 = new Answer(question2.getId(), "null");
            knowledgeSession.insert(question1);
            knowledgeSession.insert(question2);
            knowledgeSession.insert(question3);
            knowledgeSession.insert(answer1);
            knowledgeSession.insert(answer2);
            knowledgeSession.insert(answer3);
            knowledgeSession.fireAllRules();
            assertEquals("a", question1.getAnswer());
            assertEquals(null, question2.getAnswer());
            assertEquals(null, question3.getAnswer());
        } finally {
            knowledgeSession.dispose();
        }
    }


    @Test
    public void testFinalAnswers() {
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        try {
            Questionnaire questionnaire = new Questionnaire("Test");
            Question fQuest = new Question("One Shot");
                fQuest.setAnswerType(Question.QuestionType.TYPE_TEXT);
                fQuest.setFinalAnswer(true);
            Question mQuest = new Question("Many Shot");
                mQuest.setAnswerType(Question.QuestionType.TYPE_TEXT);

            questionnaire.addItem(fQuest.getId());
            questionnaire.addItem(mQuest.getId());


            knowledgeSession.insert(questionnaire);
            knowledgeSession.insert(fQuest);
            knowledgeSession.insert(mQuest);
            knowledgeSession.fireAllRules();

            assertTrue(questionnaire.getItemList().contains(fQuest.getId()));
            assertTrue(questionnaire.getItemList().contains(mQuest.getId()));
            assertNull(fQuest.getLastAnswer());
            assertNull(mQuest.getLastAnswer());


            Answer ans1 = new Answer(fQuest.getId(),"x");
            Answer ans2 = new Answer(mQuest.getId(),"y");
            knowledgeSession.insert(ans1);
            knowledgeSession.fireAllRules();
            knowledgeSession.insert(ans2);
            knowledgeSession.fireAllRules();

            for ( Object o : knowledgeSession.getObjects() )
                            System.err.println(o);

            assertEquals("x", fQuest.getLastAnswer());
            assertTrue(questionnaire.getItemList().contains(fQuest.getId()));
            assertEquals("y", mQuest.getLastAnswer());
            assertTrue(questionnaire.getItemList().contains(mQuest.getId()));

        } finally {
            knowledgeSession.dispose();
        }

    }
}
