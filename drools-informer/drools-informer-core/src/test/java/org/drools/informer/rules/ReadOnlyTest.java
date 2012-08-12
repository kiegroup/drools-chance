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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.informer.Group;
import org.drools.informer.Question;
import org.drools.informer.Questionnaire;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Damon
 * 
 */
public class ReadOnlyTest {

	private KnowledgeBase knowledgeBase;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/Active.drl"), ResourceType.DRL);
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/Queries.drl"), ResourceType.DRL);
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/ReadOnly.drl"), ResourceType.DRL);
        System.err.println( knowledgeBuilder.getErrors() );
		assertFalse(knowledgeBuilder.hasErrors());
		knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
	}

	@Test
	public void testReadOnly() {
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		try {
			Questionnaire questionnaire = new Questionnaire("questionnaire");
			Group group1 = new Group("group1");
			group1.setPresentationStyles(new String[]{"readonly"});
			Group group2 = new Group("group2");
			Group group3 = new Group("group3");
			group3.setPresentationStyles(new String[]{"readonly"});
			Group group3a = new Group("group3a");
			Group group4 = new Group("group4");
			Group group4a = new Group("group4a");
			Group group4b = new Group("group4b");
			group4b.setPresentationStyles(new String[]{"readonly"});
			Question question1 = new Question("question1");
			question1.setAnswerType(Question.QuestionType.TYPE_TEXT);

            questionnaire.setItems(new String[]{group1.getId(),group2.getId(),group3.getId(), group4.getId()});
            group1.setItems(new String[]{question1.getId()});
            group2.setItems(new String[]{question1.getId()});
            group3.setItems(new String[]{group3a.getId()});
            group3a.setItems(new String[]{question1.getId()});
            group4.setItems(new String[]{group4a.getId(),group4b.getId()});
            group4a.setItems(new String[]{question1.getId()});
            group4b.setItems(new String[]{question1.getId()});



			FactHandle handleQuestionnaire = knowledgeSession.insert(questionnaire);
			knowledgeSession.insert(group1);
			knowledgeSession.insert(group2);
			knowledgeSession.insert(group3);
			knowledgeSession.insert(group3a);
			knowledgeSession.insert(group4);
			knowledgeSession.insert(group4a);
			knowledgeSession.insert(group4b);
			knowledgeSession.insert(question1);
			knowledgeSession.fireAllRules();

            for ( Object o : knowledgeSession.getObjects() ) {
                System.out.println( o );
            }

			assertEquals(true, isReadOnlyInherited(question1));

            System.out.println( "-------------------------------------------" );

			questionnaire.setActiveItem(group1.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();

			assertEquals(true, isReadOnlyInherited(question1));

            System.out.println( "-------------------------------------------" );

			questionnaire.setActiveItem(group2.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
            assertEquals(false, isReadOnlyInherited(question1));

			questionnaire.setActiveItem(group3.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			assertEquals(true, isReadOnlyInherited(question1));

			questionnaire.setActiveItem(group4.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			assertEquals(true, isReadOnlyInherited(question1));
		} finally {
			knowledgeSession.dispose();
		}
	}

	private boolean isReadOnlyInherited(Question question) {
		return question.getPresentationStyles() != null && Arrays.asList(question.getPresentationStyles()).contains("readonly-inherited");
	}
}
