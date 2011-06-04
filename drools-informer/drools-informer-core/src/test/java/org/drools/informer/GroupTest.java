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
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Damon Horrell
 */
public class GroupTest {

	@Test
	public void testSetItems() {
		Group group = new Group();
		group.setItems(new String[] { "a", "b", null, "c" });
		assertArrayEquals(new String[] { "a", "b", "c" }, group.getItems());
		assertEquals("a,b,c", group.getInternalItemsAsString());
	}

	@Test
	public void testRemoveItems() {
		Group group = new Group();
		group.setItems(new String[] { "a", "b", "c", "d" });
		assertEquals("a,b,c,d", group.getInternalItemsAsString());

		group.removeItem(null);
		assertEquals("a,b,c,d", group.getInternalItemsAsString());

		group.removeItem("e");
		assertEquals("a,b,c,d", group.getInternalItemsAsString());

		group.removeItem("c");
		assertEquals("a,b,d", group.getInternalItemsAsString());

		group.removeItem("d");
		assertEquals("a,b", group.getInternalItemsAsString());

		group.removeItem("a");
		assertEquals("b", group.getInternalItemsAsString());

		group.removeItem("b");
		Assert.assertNull(group.getInternalItemsAsString());
	}

	@Test
	public void testRemoveSimilarItems() {
		Group group = new Group();
		group.setItems(new String[] { "hello", "ell", "o", });
		assertEquals("hello,ell,o", group.getInternalItemsAsString());

		group.removeItem("ell");
		assertEquals("hello,o", group.getInternalItemsAsString());
	}

	@Test
	public void testAddItem() {
		Group group = new Group();
		group.addItem("a");
		assertArrayEquals(new String[] { "a" }, group.getItems());
		assertEquals("a", group.getInternalItemsAsString());
		group.addItem("b");
		assertArrayEquals(new String[] { "a", "b" }, group.getItems());
		assertEquals("a,b", group.getInternalItemsAsString());
		group.addItem(null);
		assertArrayEquals(new String[] { "a", "b" }, group.getItems());
		assertEquals("a,b", group.getInternalItemsAsString());
		group.addItem("c");
		assertArrayEquals(new String[] { "a", "b", "c" }, group.getItems());
		assertEquals("a,b,c", group.getInternalItemsAsString());
	}

	@Test
	public void testInsertItemBefore() {
		Group group = new Group();
		group.insertItem("b", "c");
		assertArrayEquals(new String[] { "b" }, group.getItems());
		assertEquals("b", group.getInternalItemsAsString());

		group.insertItem("a", "b");
		assertArrayEquals(new String[] { "a", "b" }, group.getItems());
		assertEquals("a,b", group.getInternalItemsAsString());

		group.insertItem("d", "e");
		assertArrayEquals(new String[] { "a", "b", "d" }, group.getItems());
		assertEquals("a,b,d", group.getInternalItemsAsString());

		group.insertItem("e", null);
		assertArrayEquals(new String[] { "a", "b", "d", "e" }, group.getItems());
		assertEquals("a,b,d,e", group.getInternalItemsAsString());

		group.insertItem("c", "d");
		assertArrayEquals(new String[] { "a", "b", "c", "d", "e" }, group.getItems());
		assertEquals("a,b,c,d,e", group.getInternalItemsAsString());
	}

	@Test
	public void testInsertItemBeforeSimilarNames() {
		Group group = new Group();
		group.setItems(new String[] { "hello", "ell", "o", });
		assertEquals("hello,ell,o", group.getInternalItemsAsString());

		group.insertItem("a", "ell");
		assertEquals("hello,a,ell,o", group.getInternalItemsAsString());
	}

	@Test
	public void testInsertDuplicateItemBeforeSimilarNames() {
		Group group = new Group();
		group.setItems(new String[] { "hello", "ell", "o", });
		assertEquals("hello,ell,o", group.getInternalItemsAsString());

		group.insertItem("o", "ell");
		assertEquals("hello,o,ell,o", group.getInternalItemsAsString());

		group.insertItem("hello", "a");
		assertEquals("hello,o,ell,o,hello", group.getInternalItemsAsString());
	}

	@Test
	public void testAppendItemAfter() {
		Group group = new Group();
		group.appendItem("a", "c");
		assertArrayEquals(new String[] { "a" }, group.getItems());
		assertEquals("a", group.getInternalItemsAsString());

		group.appendItem("b", "a");
		assertEquals("a,b", group.getInternalItemsAsString());

		group.appendItem("d", "e");
		assertEquals("a,b,d", group.getInternalItemsAsString());

		group.appendItem("e", null);
		assertEquals("a,b,d,e", group.getInternalItemsAsString());

		group.appendItem("c", "b");
		assertEquals("a,b,c,d,e", group.getInternalItemsAsString());

		group.appendItem("f", "e");
		assertEquals("a,b,c,d,e,f", group.getInternalItemsAsString());

		group.appendItem("z", "a");
		assertEquals("a,z,b,c,d,e,f", group.getInternalItemsAsString());
	}

	@Test
	public void testAddDuplicateItem() {
		Group group = new Group();
		group.setItems(new String[] { "a", "b", "c", "d" });
		assertEquals("a,b,c,d", group.getInternalItemsAsString());

		group.addItem("b");
		assertEquals("a,b,c,d,b", group.getInternalItemsAsString());

		// try {
		// // duplicate entry should fail
		// group.addItem("a");
		// fail();
		// } catch (IllegalArgumentException e) {
		// }
	}

	@Test
	public void testAppendItemAfterSimilarNames() {
		Group group = new Group();
		group.setItems(new String[] { "hello", "ell", "o", });
		assertEquals("hello,ell,o", group.getInternalItemsAsString());

		group.appendItem("a", "ell");
		assertEquals("hello,ell,a,o", group.getInternalItemsAsString());
	}

	@Test
	public void testAppendDuplicateItemAfterSimilarNames() {
		Group group = new Group();
		group.setItems(new String[] { "hello", "ell", "o", });
		assertEquals("hello,ell,o", group.getInternalItemsAsString());

		group.appendItem("ell", "o");
		assertEquals("hello,ell,o,ell", group.getInternalItemsAsString());

		group.appendItem("o", "hello");
		assertEquals("hello,o,ell,o,ell", group.getInternalItemsAsString());

		group.appendItem("hello", "a");
		assertEquals("hello,o,ell,o,ell,hello", group.getInternalItemsAsString());
	}


	@SuppressWarnings("deprecation")
	@Test
	public void testSetItemsAsString() {
		Group group = new Group();
		group.setItemsAsString("a,b,c");
		assertArrayEquals(new String[] { "a", "b", "c" }, group.getItems());
		assertEquals("a,b,c", group.getItemsAsString());
	}

	@Test
	public void testSetItemsContainingComma() {
		Group group = new Group();
		try {
			group.setItems(new String[] { "a", "b,", "c" });
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testAddItemContainingComma() {
		Group group = new Group();
		try {
			group.addItem("b,");
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testInsertItemContainingComma() {
		Group group = new Group();
		try {
			group.insertItem("a,", "b");
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testSetItemsNull() {
		Group group = new Group();
		group.setItems(null);
		assertArrayEquals(null, group.getItems());
		assertEquals(null, group.getInternalItemsAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetItemsAsStringNull() {
		Group group = new Group();
		group.setItemsAsString(null);
		assertArrayEquals(null, group.getItems());
		assertEquals(null, group.getItemsAsString());
	}

	@Test
	public void testSetEmptyItems() {
		Group group = new Group();
		group.setItems(new String[] { "", null, "" });
		assertArrayEquals(null, group.getItems());
		assertEquals(null, group.getInternalItemsAsString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSetEmptyItemsAsString() {
		Group group = new Group();
		group.setItemsAsString("");
		assertArrayEquals(null, group.getItems());
		assertEquals(null, group.getItemsAsString());
	}

	@Test
	public void testAddEmptyItem() {
		Group group = new Group();
		group.addItem("");
		group.addItem(null);
		assertArrayEquals(null, group.getItems());
		assertEquals(null, group.getInternalItemsAsString());
	}

	@Test
	public void testInsertEmptyItem() {
		Group group = new Group();
		group.insertItem(null, null);
		group.insertItem(null, "");
		group.insertItem("", null);
		group.insertItem("", "");
		assertArrayEquals(null, group.getItems());
		assertEquals(null, group.getInternalItemsAsString());
	}

}
