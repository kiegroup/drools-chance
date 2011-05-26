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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Arrays;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.informer.Note;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.ConsequenceException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Damon
 * 
 */
public class ItemRulesTest {

	private static final Logger logger = LoggerFactory.getLogger(ItemRulesTest.class);
	
	private KnowledgeBase knowledgeBase;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/Item.drl"), ResourceType.DRL);
		if (knowledgeBuilder.hasErrors()) {
			logger.debug(Arrays.toString(knowledgeBuilder.getErrors().toArray()));
		}
		assertFalse(knowledgeBuilder.hasErrors());
		knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
	}

	@Test
	public void testUniqueItemId() {
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		try {
			Note note1 = new Note("note","label1");
                note1.forceId("id");
			Note note2 = new Note("note","label2");
                note2.forceId("id");
			knowledgeSession.insert(note1);
			knowledgeSession.insert(note2);
			knowledgeSession.fireAllRules();
			fail();
		} catch (ConsequenceException e) {
			if (e.getCause() instanceof IllegalStateException) {
				if (((IllegalStateException)e.getCause()).getMessage().equals("Duplicate item id: id")) {
					return;
				}
			}
			fail();
		} finally {
			knowledgeSession.dispose();
		}
	}
}
