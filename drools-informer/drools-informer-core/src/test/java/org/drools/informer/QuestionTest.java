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
package org.drools.informer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Damon Horrell
 */
public class QuestionTest {

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testUnknownAnswerType() {
		Question q = new Question();
		try {
			q.setAnswerType(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testAnswerTypeChanged() {
		Question q = new Question();
		q.setAnswerType(Question.QuestionType.TYPE_NUMBER);
		q.setNumberAnswer(1L);
		assertEquals(1, q.getNumberAnswer().longValue());
		q.setAnswerType(Question.QuestionType.TYPE_TEXT);
		q.setAnswerType(Question.QuestionType.TYPE_NUMBER);
		assertEquals(null, q.getNumberAnswer());
	}

	@Test
	public void testAnswerTypeChangedToSame() {
		Question q = new Question();
		q.setAnswerType(Question.QuestionType.TYPE_NUMBER);
		q.setNumberAnswer(1L);
		assertEquals(1, q.getNumberAnswer().longValue());
		q.setAnswerType(Question.QuestionType.TYPE_NUMBER);
		assertEquals(1, q.getNumberAnswer().longValue());
	}

	@Test
	public void testWrongAnswerType() {
		Question q = new Question();
		q.setAnswerType(Question.QuestionType.TYPE_NUMBER);
		try {
			q.setTextAnswer("hello");
			fail();
		} catch (IllegalStateException e) {
		}
		try {
			q.getDateAnswer();
			fail();
		} catch (IllegalStateException e) {
		}
	}

	@Test
	public void testSetAnswer() {
		Question q = new Question();
		try {
			q.setAnswer(123);
			fail();
		} catch (IllegalStateException e) {
		}
		q.setAnswerType(Question.QuestionType.TYPE_NUMBER);
		q.setAnswer(new Long(123));
		assertEquals(123, q.getNumberAnswer().longValue());
	}

	@Test
	public void testGetAnswer() {
		Question q = new Question();
		try {
			q.getAnswer();
			fail();
		} catch (IllegalStateException e) {
		}
		q.setAnswerType(Question.QuestionType.TYPE_DECIMAL);
		q.setDecimalAnswer(new BigDecimal("4.56"));
		assertEquals(new BigDecimal("4.56"), q.getAnswer());
	}

	@Test
	public void testSetAnswerCustomType() {
		Question q = new Question();
		try {
			q.setAnswer(123);
			fail();
		} catch (IllegalStateException e) {
		}
		q.setAnswerType(Question.QuestionType.TYPE_NUMBER );
		q.setAnswer(new Long(123));
		assertEquals(123, q.getNumberAnswer().longValue());
		q.setAnswer(null);
		assertEquals(null, q.getNumberAnswer());
	}

	@Test
	public void testGetAnswerCustomType() {
		Question q = new Question();
		try {
			q.getAnswer();
			fail();
		} catch (IllegalStateException e) {
		}
		q.setAnswerType(Question.QuestionType.TYPE_DECIMAL);
		q.setDecimalAnswer(new BigDecimal("4.56"));
		assertEquals(new BigDecimal("4.56"), q.getAnswer());
	}

	@Test
	public void testNewListType() {
		Question q = new Question();
		q.setAnswerType(Question.QuestionType.TYPE_LIST);
		q.setListAnswer("one||two");
		assertEquals("one||two", q.getListAnswer());
	}
	
	@Test
	public void testDate() throws ParseException {
		Question q = new Question();
		q.setAnswerType(Question.QuestionType.TYPE_DATE);
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		Date d = f.parse("1999-01-02");
		q.setAnswer(d);
		assertEquals(d, q.getAnswer());
		assertEquals(d, q.getDateAnswer());
		q.setAnswer(null);
		assertNull(q.getDateAnswer());
		q.setDateAnswer((String) null);
		assertNull(q.getDateAnswer());
		q.setDateAnswer((Date) null);
		assertNull(q.getDateAnswer());
	}
}
