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

import org.drools.definition.type.Modifies;
import org.drools.definition.type.PropertyReactive;

import java.util.*;

/**
 * <p>
 * Represents any arbitrary way of grouping items. e.g.
 * </p>
 *
 * <ul>
 * <li>sections within a page</li>
 * <li>a multi-column layout</li>
 * <li>a group of items which are all centre-aligned</li>
 * </ul>
 *
 * <p>
 * How a particular <code>Group</code> is rendered to screen is determined solely by <code>presentationStyles</code>.
 * </p>
 *
 * @author Damon Horrell
 */
@PropertyReactive
public class Group extends Item {

    public static final String COMMA_SEPARATOR = ",";

    private static final long serialVersionUID = 1L;

    private String label;

    /**
     * Items are represented internally as a comma-delimited string for efficient XML transport.
     */
    private ArrayList<String> items;

    public Group() {
        this.items = new ArrayList<String>();
    }


    public Group(String type) {
        super(type);
        this.items = new ArrayList<String>();
    }

    public Group(String type, String label) {
        super(type);
        this.label = label;
        this.items = new ArrayList<String>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets list of item ids.
     *
     * @return
     */
    public String[] getItems() {
        if (items.size() == 0) {
            return null;
        }
        return items.toArray(new String[items.size()]);
    }

    public List<String> getItemList() {
        return items;
    }

    /**
     * @param itemId
     * @return
     */
    protected boolean validItemId(String itemId) {
        if ((itemId == null) || (itemId.length() == 0)) {
            return false;
        }

        if (itemId.contains(COMMA_SEPARATOR)) {
            throw new IllegalArgumentException();
        }
        return true;
    }

    /**
     * Sets list of item ids.
     *
     * @param items
     */
    @Modifies( "items" )
    public void setItems(String[] items) {
        this.items.clear();
        if (items != null) {
            for (String s : items) {
                addItem(s);
            }
        }
    }

    /**
     * Adds itemId to the existing list. Duplicates allowed. Null will be ignored
     *
     * @param itemId
     *            - cannot contain a comma
     */
    @Modifies( "items" )
    public void addItem(String itemId) {
        if (validItemId(itemId)) {
            this.items.add(itemId);
        }
    }

    /**
     * Adds itemId to the existing list before the position of the second item. If the second item is not in the list, it will be
     * added on the end. Value will NOT be trimmed.
     *
     * @param itemId
     *            The value to insert - cannot contain a comma. Duplicates allowed. Null will be ignored
     * @param beforeItemId
     *            The entry before which the new item is to be inserted
     */
    @Modifies( "items" )
    public void insertItem(String itemId, String beforeItemId) {
        if ((beforeItemId == null) || (beforeItemId.length() == 0)) {
            addItem(itemId);
        } else if (validItemId(itemId)) {
            int pos = this.items.indexOf(beforeItemId);
            if (pos < 0) {
                addItem(itemId);
            } else {
                this.items.add(pos, itemId);
            }
        }
    }

    /**
     * Adds itemId to the existing list after the position of the second item. If the second item is not in the list, it will be
     * added on the end. Value will NOT be trimmed.
     *
     * @param itemId
     *            The value to insert - cannot contain a comma. Duplicates allowed. Null will be ignored
     * @param afterItemId
     *            The entry after which the new item is to be inserted
     */
    @Modifies( "items" )
    public void appendItem(String itemId, String afterItemId) {
        if ((afterItemId == null) || (afterItemId.length() == 0)) {
            addItem(itemId);
        } else if (validItemId(itemId)) {
            int pos = this.items.indexOf(afterItemId);
            if ((pos < 0) || ((pos + 1) == this.items.size())) {
                this.items.add(itemId);
            } else {
                this.items.add(pos + 1, itemId);
            }
        }
    }

    /**
     * Removes itemId from the existing list. If it is the only items, will (re)set the list of items to null.
     *
     * @param itemId
     *            The value to remove. Ignore if null or doesn't exist
     * @return The index of the removed item, or -1 if not found
     */
    @Modifies( "items" )
    public int removeItem(String itemId) {
        if (validItemId(itemId)) {
            int pos = items.indexOf(itemId);
            if (pos >= 0) {
                this.items.remove(itemId);
                return pos;
            }
        }
        return -1;
    }

    /**
     * Sets list of item ids.
     *
     * This method is provided to support the MVEL syntax in rules e.g.
     * <p>
     * <code>group.setItems({"a", "b"});</code>
     * </p>
     *
     * @param items
     */
    @Modifies( "items" )
    public void setItems(Object[] items) {
        if (items == null) {
            this.items = null;
        } else {
            this.items.clear();
            for (Object item : items) {
                this.items.add(item.toString());
            }
        }
    }

    /**
     * Gets list of item ids as a comma delimited string.
     *
     * TODO this method can be removed when Guvnor supports String[]
     *
     * @return
     * @deprecated
     */
    public String getItemsAsString() {
        if (this.items.size() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        java.util.Iterator<String> iter = this.items.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append(COMMA_SEPARATOR);
            }
        }
        return sb.toString();
    }

    /**
     * Gets list of item ids as a comma delimited string. Implemented for testing purpose only - package visibility
     *
     * @return
     */
    String getInternalItemsAsString() {
        return getItemsAsString();
    }

    /**
     * Set list of item ids as a comma-delimited string.
     *
     * TODO this method can be removed when Guvnor supports String[]
     *
     * Note: setItemsAsString("") silently converts the value to null
     *
     * @param items
     * @deprecated
     */
    @Modifies( "items" )
    public void setItemsAsString(String items) {
        this.items.clear();
        if (items != null) {
            StringTokenizer tok = new StringTokenizer(items,COMMA_SEPARATOR);
            while (tok.hasMoreTokens()) {
                this.items.add(tok.nextToken());
            }
        }
    }

    @Override
    public String toString() {
        return "Group{" +
                "label='" + label + '\'' +
                ", items=" + items +
                "} " + super.toString();
    }


    @Modifies( { "presentationStyles", "stylesList" } )
    public void addPresentationStyle(String presentationStyle) {
        super.addPresentationStyle(presentationStyle);
    }

    @Modifies( { "presentationStyles", "stylesList" } )
    public void removePresentationStyle(String presentationStyle) {
        super.removePresentationStyle(presentationStyle);
    }
}
