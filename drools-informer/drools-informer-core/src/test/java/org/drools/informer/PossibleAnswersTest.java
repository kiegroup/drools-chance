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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.drools.informer.MultipleChoiceQuestion.PossibleAnswer;
import org.junit.Test;


/**
 * @author Damon Horrell
 */
public class PossibleAnswersTest {

	@Test
	public void testSetPossibleAnswers() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswers(new MultipleChoiceQuestion.PossibleAnswer[] { new MultipleChoiceQuestion.PossibleAnswer(null, "select..."),
				new PossibleAnswer("a", "apple"), new MultipleChoiceQuestion.PossibleAnswer("b", "banana"), null,
				new PossibleAnswer("c", "carrot, cucumber, or cauliflower"), new PossibleAnswer("d"),
				new PossibleAnswer("e=?", "e=mc^2"), new PossibleAnswer("===", "=equals=") });
		assertArrayEquals(new PossibleAnswer[] { new PossibleAnswer(null, "select..."), new PossibleAnswer("a", "apple"),
				new PossibleAnswer("b", "banana"), new PossibleAnswer("c", "carrot, cucumber, or cauliflower"),
				new PossibleAnswer("d"), new PossibleAnswer("e=?", "e=mc^2"), new PossibleAnswer("===", "=equals=") }, question
				.getPossibleAnswers());
		assertEquals(
				"null=select...,a=apple,b=banana,c=carrot\\, cucumber\\, or cauliflower,d=,e\\=?=e\\=mc^2,\\=\\=\\==\\=equals\\=",
				question.getInternalPossibleAnswersAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPossibleAnswersAsString() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question
				.setPossibleAnswersAsString("null=select...,a=apple,b=banana,c=carrot\\, cucumber\\, or cauliflower,d=,e\\=?=e\\=mc^2,\\=\\=\\==\\=equals\\=");
		assertArrayEquals(new PossibleAnswer[] { new PossibleAnswer(null, "select..."), new PossibleAnswer("a", "apple"),
				new PossibleAnswer("b", "banana"), new PossibleAnswer("c", "carrot, cucumber, or cauliflower"),
				new PossibleAnswer("d"), new PossibleAnswer("e=?", "e=mc^2"), new PossibleAnswer("===", "=equals=") }, question
				.getPossibleAnswers());
		assertEquals(
				"null=select...,a=apple,b=banana,c=carrot\\, cucumber\\, or cauliflower,d=,e\\=?=e\\=mc^2,\\=\\=\\==\\=equals\\=",
				question.getInternalPossibleAnswersAsString());
	}

	@Test
	public void testSetPossibleAnswersWithIdContainingComma() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		try {
			question.setPossibleAnswers(new PossibleAnswer[] { new PossibleAnswer("a", "apple"),
					new PossibleAnswer("b", "banana"), null, new PossibleAnswer("c,d", "carrot, cucumber, or donkey") });
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testSetPossibleAnswersNull() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswers(null);
		assertArrayEquals(null, question.getPossibleAnswers());
		assertEquals(null, question.getInternalPossibleAnswersAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPossibleAnswersAsStringNull() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswersAsString(null);
		assertArrayEquals(null, question.getPossibleAnswers());
		assertEquals(null, question.getInternalPossibleAnswersAsString());
	}

	@Test
	public void testSetPossibleAnswersEmpty() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswers(new PossibleAnswer[0]);
		assertArrayEquals(null, question.getPossibleAnswers());
		assertEquals(null, question.getInternalPossibleAnswersAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPossibleAnswersAsStringEmpty() {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setPossibleAnswersAsString("");
		assertArrayEquals(null, question.getPossibleAnswers());
		assertEquals(null, question.getInternalPossibleAnswersAsString());
	}
	
	
	@Test
	public void testInsertAndRemovePossibleAnswer() {
		// Remember - building up using insert is inefficient. it 
		// is only for amending a list once created
		
		// insert tests
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		assertEquals(null, question.getInternalPossibleAnswersAsString());
		question.insertPossibleAnswer(new PossibleAnswer("b", "banana"), -10);
		assertEquals("b=banana", question.getInternalPossibleAnswersAsString());
		question.insertPossibleAnswer(new PossibleAnswer("d", "dock"), 10);
		assertEquals("b=banana,d=dock", question.getInternalPossibleAnswersAsString());
		question.insertPossibleAnswer(new PossibleAnswer("e", "egg"), 2);
		assertEquals("b=banana,d=dock,e=egg", question.getInternalPossibleAnswersAsString());
		question.insertPossibleAnswer(new PossibleAnswer("c", "carrot"), 1);
		assertEquals("b=banana,c=carrot,d=dock,e=egg", question.getInternalPossibleAnswersAsString());
		question.insertPossibleAnswer(new PossibleAnswer("a", "apple"), 0);
		assertEquals("a=apple,b=banana,c=carrot,d=dock,e=egg", question.getInternalPossibleAnswersAsString());
		
		// has tests
		assertTrue("Did not contain Apple", question.hasPossibleAnswer("a"));
		assertTrue("Did not contain Banana", question.hasPossibleAnswer("b"));
		assertTrue("Did not contain egg", question.hasPossibleAnswer("e"));
		assertFalse("Contains false entry [k]", question.hasPossibleAnswer("k"));
		
		// remove tests
		question.removePossibleAnswer("e");
		assertFalse("Contains removed entry [e]", question.hasPossibleAnswer("e"));
		question.insertPossibleAnswer(new PossibleAnswer("e=?", "e=mc^2"), 4);
		assertFalse("Contains removed entry [e] after inserting [e=?]", question.hasPossibleAnswer("e"));
		assertTrue("Did not contain entry [e=?]", question.hasPossibleAnswer("e=?"));
		question.removePossibleAnswer("a");
		assertFalse("Contains removed entry [a]", question.hasPossibleAnswer("a"));
		question.removePossibleAnswer("c");
		assertFalse("Contains removed entry [c]", question.hasPossibleAnswer("c"));
		question.removePossibleAnswer("e=?");
		assertEquals("b=banana,d=dock", question.getInternalPossibleAnswersAsString());
		
		// test removing chosen item
		question.setAnswerType(Question.QuestionType.TYPE_TEXT);
		question.setAnswer("b");
		question.insertPossibleAnswer(new PossibleAnswer("a", "apple"), 0);
		assertEquals("a=apple,b=banana,d=dock", question.getInternalPossibleAnswersAsString());
		assertTrue("Did not contain Apple", question.hasPossibleAnswer("a"));
		assertEquals("b", question.getAnswer());
		question.removePossibleAnswer("a");
		assertEquals("b", question.getAnswer());
		question.removePossibleAnswer("b");
		assertNull("The answer was not set to null when the associated possible answer was removed", question.getAnswer());
	}
	

}
