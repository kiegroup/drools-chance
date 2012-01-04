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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Derek Rendall
 */
public class QuestionnaireTest {

	@Test
	public void testBranch() {
		Questionnaire q = new Questionnaire();
		q.setItems(new String[] { "a", "b", "c" });
		q.setActiveItem("a");
		q.setCompletionAction("default");
		q.navigationBranch(new String[] { "x","y","z" }, "y");
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationBranch(new String[] { "1","2","3" }, "3");
		assertArrayEquals(new String[] { "1","2","3" }, q.getItems());
		assertEquals("3", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "a","b","c" }, q.getItems());
		assertEquals("a", q.getActiveItem());
		assertEquals("default", q.getCompletionAction());
	}

	@Test
	public void testBranchWithAction() {
		Questionnaire q = new Questionnaire();
		q.setItems(new String[] { "a", "b", "c" });
		q.setActiveItem("a");
		q.setCompletionAction("default");
		q.navigationBranch(new String[] { "x","y","z" }, "y", "action1");
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals("action1", q.getCompletionAction());
		q.navigationBranch(new String[] { "1","2","3" }, "3", "action2");
		assertArrayEquals(new String[] { "1","2","3" }, q.getItems());
		assertEquals("3", q.getActiveItem());
		assertEquals("action2", q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals("action1", q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "a","b","c" }, q.getItems());
		assertEquals("a", q.getActiveItem());
		assertEquals("default", q.getCompletionAction());
	}

	@Test
	public void testBranchInvalidEmpty() {
		Questionnaire q = new Questionnaire();
		q.setItems(new String[] { "a", "b", "c" });
		q.setActiveItem("a");
		q.setCompletionAction("default");
		try {
		q.navigationBranch(null, "y", "action1");
		fail("no items on new branch");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testReturnToFar() {
		Questionnaire q = new Questionnaire();
		q.setItems(new String[] { "a", "b", "c" });
		q.setActiveItem("a");
		q.setCompletionAction("default");
		q.navigationBranch(new String[] { "x","y","z" }, "y");
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationBranch(new String[] { "1","2","3" }, "3");
		assertArrayEquals(new String[] { "1","2","3" }, q.getItems());
		assertEquals("3", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "x","y","z" }, q.getItems());
		assertEquals("y", q.getActiveItem());
		assertEquals(Questionnaire.COMPLETION_ACTION_RETURN, q.getCompletionAction());
		q.navigationReturn();
		assertArrayEquals(new String[] { "a","b","c" }, q.getItems());
		assertEquals("a", q.getActiveItem());
		assertEquals("default", q.getCompletionAction());
		try {
			q.navigationReturn();
			fail("no more items to pop");
		} catch (IllegalStateException e) {
			// expected
		}
	}

}
