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

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.informer.Group;
import org.drools.informer.Item;
import org.drools.informer.Note;
import org.drools.informer.Questionnaire;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Damon
 * 
 *         TODO add tests for InvalidAnswer - should be active only if question is active (and exists of course)
 */
public class ActiveTest {

	private static final Logger logger = LoggerFactory.getLogger(ActiveTest.class);
	
	private KnowledgeBase knowledgeBase;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/Active.drl"), ResourceType.DRL);
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/Queries.drl"), ResourceType.DRL);
		logger.debug(Arrays.toString(knowledgeBuilder.getErrors().toArray()));
		assertFalse(knowledgeBuilder.hasErrors());
		knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
	}

	@Test
	public void testActiveObjects() {
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		try {
			Questionnaire questionnaire = new Questionnaire("questionnaire");
			Group group1 = new Group("group1");
			Group group2 = new Group("group2");
			Note note1 = new Note("note1");
			Note note2 = new Note("note2");
			Note note3 = new Note("note3");
			Note note4 = new Note("note4");
			Note note5 = new Note("note5");
			Note note6 = new Note("note6");

            questionnaire.setItems(new String[] { group1.getId(), group2.getId(), note4.getId() });
            group1.setItems(new String[] { note1.getId(), note2.getId() });
            group2.setItems(new String[] { note3.getId() });

			FactHandle handleQuestionnaire = knowledgeSession.insert(questionnaire);
			FactHandle handleGroup1 = knowledgeSession.insert(group1);
			knowledgeSession.insert(group2);
			knowledgeSession.insert(note1);
			knowledgeSession.insert(note2);
			knowledgeSession.insert(note3);
			knowledgeSession.insert(note4);
			knowledgeSession.insert(note5);
			knowledgeSession.insert(note6);
			knowledgeSession.fireAllRules();

			QueryResults queryResults = knowledgeSession.getQueryResults("activeObjects");
			assertArrayEquals(new String[] { "object" }, queryResults.getIdentifiers());
			Set<String> itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group1.getId(), group2.getId(), note1.getId(), note2.getId(),
					note3.getId(), note4.getId(), note5.getId(), note6.getId() })), itemIds);

			questionnaire.setActiveItem(group1.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group1.getId(), note1.getId(), note2.getId() })),
					itemIds);

			questionnaire.setActiveItem(group2.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group2.getId(), note3.getId() })), itemIds);

			questionnaire.setActiveItem(note4.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), note4.getId() })), itemIds);

			questionnaire.setActiveItem("unknown");
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId() })), itemIds);

			questionnaire.setActiveItem(group1.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			group1.setItems(new String[] { note1.getId(), note2.getId(), note5.getId() });
			knowledgeSession.update(handleGroup1, group1);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(
					new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group1.getId(), note1.getId(), note2.getId(), note5.getId() })),
					itemIds);

			group1.setItems(new String[] { note1.getId(), note2.getId() });
			knowledgeSession.update(handleGroup1, group1);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group1.getId(), note1.getId(), note2.getId() })),
					itemIds);
		} finally {
			knowledgeSession.dispose();
		}
	}

	@Test
	public void testDuplicateSameGroup() {
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		try {
			Questionnaire questionnaire = new Questionnaire("questionnaire");
			Group group1 = new Group("group1");
			Group group2 = new Group("group2");
			Note note1 = new Note("note1");
			Note note2 = new Note("note2");

            questionnaire.setItems(new String[] { group1.getId(), group2.getId() });
            group1.setItems(new String[] { note1.getId(), note1.getId() });
            group2.setItems(new String[] { note2.getId() });


            FactHandle handleQuestionnaire = knowledgeSession.insert(questionnaire);
			knowledgeSession.insert(group1);
			knowledgeSession.insert(group2);
			knowledgeSession.insert(note1);
			knowledgeSession.insert(note2);
			knowledgeSession.fireAllRules();

			QueryResults queryResults = knowledgeSession.getQueryResults("activeObjects");
			assertArrayEquals(new String[] { "object" }, queryResults.getIdentifiers());
			Set<String> itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays
					.asList(new String[] { questionnaire.getId(), group1.getId(), group2.getId(), note1.getId(), note2.getId() })), itemIds);

			questionnaire.setActiveItem(group1.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group1.getId(), note1.getId() })), itemIds);

			questionnaire.setActiveItem(group2.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group2.getId(), note2.getId() })), itemIds);

			questionnaire.setActiveItem("unknown");
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId() })), itemIds);
		} finally {
			knowledgeSession.dispose();
		}
	}

	@Test
	public void testDuplicateSamePage() {
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		try {
			Questionnaire questionnaire = new Questionnaire("questionnaire");
			Group group1 = new Group("group1");
			Group group2 = new Group("group2");
			Group group3 = new Group("group3");
			Group group4 = new Group("group4");
			Note note1 = new Note("note1");
			Note note2 = new Note("note2");
			FactHandle handleQuestionnaire = knowledgeSession.insert(questionnaire);

            questionnaire.setItems(new String[] { group1.getId(), group2.getId() });
            group1.setItems(new String[] { group3.getId(), group4.getId() });
            group2.setItems(new String[] { note2.getId() });
            group3.setItems(new String[] { note1.getId() });
            group4.setItems(new String[] { note1.getId() });


            knowledgeSession.insert(group1);
			knowledgeSession.insert(group2);
			knowledgeSession.insert(group3);
			knowledgeSession.insert(group4);
			knowledgeSession.insert(note1);
			knowledgeSession.insert(note2);
			knowledgeSession.fireAllRules();

			QueryResults queryResults = knowledgeSession.getQueryResults("activeObjects");
			assertArrayEquals(new String[] { "object" }, queryResults.getIdentifiers());
			Set<String> itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group1.getId(), group2.getId(), group3.getId(),
					group4.getId(), note1.getId(), note2.getId() })), itemIds);

			questionnaire.setActiveItem(group1.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays
					.asList(new String[] { questionnaire.getId(), group1.getId(), group3.getId(), group4.getId(), note1.getId() })), itemIds);

			questionnaire.setActiveItem(group2.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group2.getId(), note2.getId() })), itemIds);

			questionnaire.setActiveItem("unknown");
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId() })), itemIds);
		} finally {
			knowledgeSession.dispose();
		}
	}

	@Test
	public void testDuplicateDifferentPage() {
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		try {
			Questionnaire questionnaire = new Questionnaire("questionnaire");
			Group group1 = new Group("group1");
			Group group2 = new Group("group2");
			Note note1 = new Note("note1");
			Note note2 = new Note("note2");

            questionnaire.setItems(new String[]{group1.getId(), group2.getId()});
            group1.setItems(new String[] { note1.getId() });
            group2.setItems(new String[] { note1.getId(), note2.getId() });


            FactHandle handleQuestionnaire = knowledgeSession.insert(questionnaire);
			knowledgeSession.insert(group1);
			knowledgeSession.insert(group2);
			knowledgeSession.insert(note1);
			knowledgeSession.insert(note2);
			knowledgeSession.fireAllRules();

			QueryResults queryResults = knowledgeSession.getQueryResults("activeObjects");
			assertArrayEquals(new String[] { "object" }, queryResults.getIdentifiers());
			Set<String> itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays
					.asList(new String[] { questionnaire.getId(), group1.getId(), group2.getId(), note1.getId(), note2.getId() })), itemIds);

			questionnaire.setActiveItem(group1.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group1.getId(), note1.getId() })), itemIds);

			questionnaire.setActiveItem(group2.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group2.getId(), note1.getId(), note2.getId() })),
					itemIds);

			questionnaire.setActiveItem("unknown");
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId() })), itemIds);
		} finally {
			knowledgeSession.dispose();
		}
	}

	@Test
	public void testNavigationReturn() {
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		try {
			Questionnaire questionnaire = new Questionnaire("questionnaire");
			Group group1 = new Group("group1");
			Group group2 = new Group("group2");
			Group group3 = new Group("group3");
			Note note1 = new Note("note1");
			Note note2 = new Note("note2");
			Note note3 = new Note("note3");

            questionnaire.setItems(new String[] { group1.getId(), group2.getId() });
			questionnaire.setActiveItem(group1.getId());
            group1.setItems(new String[] { note1.getId() });
            group2.setItems(new String[] { note1.getId(), note2.getId() });
            group3.setItems(new String[]{note3.getId()});


            FactHandle handleQuestionnaire = knowledgeSession.insert(questionnaire);
            knowledgeSession.insert(group1);
			knowledgeSession.insert(group2);
			knowledgeSession.insert(group3);
			knowledgeSession.insert(note1);
			knowledgeSession.insert(note2);
			knowledgeSession.insert(note3);
			knowledgeSession.fireAllRules();

			QueryResults queryResults = knowledgeSession.getQueryResults("activeObjects");
			assertArrayEquals(new String[] { "object" }, queryResults.getIdentifiers());
			Set<String> itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[]{questionnaire.getId(), group1.getId(), note1.getId()})), itemIds);

			questionnaire.navigationBranch(new String[]{group3.getId()}, group3.getId());
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[]{questionnaire.getId(), group3.getId(), note3.getId()})), itemIds);

			questionnaire.setActiveItem(Questionnaire.COMPLETION_ACTION_RETURN);
			knowledgeSession.update(handleQuestionnaire, questionnaire);
			knowledgeSession.fireAllRules();
			queryResults = knowledgeSession.getQueryResults("activeObjects");
			itemIds = getItemIds(queryResults);
			assertEquals(new HashSet<String>(Arrays.asList(new String[] { questionnaire.getId(), group1.getId(), note1.getId() })), itemIds);
		} finally {
			knowledgeSession.dispose();
		}
	}

	@Test
	public void testAvailableItems() {
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		try {
			Questionnaire questionnaire = new Questionnaire("questionnaire");
			Group group2 = new Group("group2");
			Group group5 = new Group("group5");
			Group group6 = new Group("group6");

            questionnaire.setItems(new String[] { "group1UUID", group2.getId(), "group3UUID", "group4UUID", group5.getId(), group6.getId(), "group7UUID" });


            knowledgeSession.insert(questionnaire);
			knowledgeSession.insert(group2);
			knowledgeSession.insert(group5);
			knowledgeSession.insert(group6);
			knowledgeSession.fireAllRules();
            for (String s : new String[] { group2.getId(), group5.getId(), group6.getId() }) {
                assertTrue(questionnaire.getAvailableItemList().contains(s));
            }

		} finally {
			knowledgeSession.dispose();
		}
	}

	private Set<String> getItemIds(QueryResults queryResults) {
		Set<String> itemIds = new HashSet<String>();
		for (Iterator<QueryResultsRow> iterator = queryResults.iterator(); iterator.hasNext();) {
			QueryResultsRow row = iterator.next();
			Item item = (Item) row.get("object");
			itemIds.add(item.getId());
		}
		return itemIds;
	}
}
