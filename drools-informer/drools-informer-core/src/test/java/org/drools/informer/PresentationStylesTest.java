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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Damon Horrell
 */
public class PresentationStylesTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPresentationStyles() {
		Note Note = new Note();
		Note.setPresentationStyles(new String[] { "a", "b", null, "c" });
		assertArrayEquals(new String[] { "a", "b", "c" }, Note.getPresentationStyles());
		assertEquals("a,b,c", Note.getPresentationStylesAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPresentationStylesAsString() {
		Note Note = new Note();
		Note.setPresentationStylesAsString("a,b,c");
		assertArrayEquals(new String[] { "a", "b", "c" }, Note.getPresentationStyles());
		assertEquals("a,b,c", Note.getPresentationStylesAsString());
	}

	@Test
	public void testSetPresentationStylesContainingComma() {
		Note Note = new Note();
		try {
			Note.setPresentationStyles(new String[] { "a", "b,", "c" });
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPresentationStylesNull() {
		Note Note = new Note();
		Note.setPresentationStyles(null);
		assertArrayEquals(null, Note.getPresentationStyles());
		assertEquals(null, Note.getPresentationStylesAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetPresentationStylesAsStringNull() {
		Note Note = new Note();
		Note.setPresentationStylesAsString(null);
		assertArrayEquals(null, Note.getPresentationStyles());
		assertEquals(null, Note.getPresentationStylesAsString());
	}

}
