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
package org.drools.informer.domain.questionnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.informer.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object that holds the Page level data extracted from the spreadsheet.
 * 
 * The core contents is a list of {@link PageElement} objects representing items on the page.
 * 
 * The initial element is a Group representing the page.
 * 
 * The Spreadsheet types of Page and Branch map to {@link Group}
 * 
 * There are three ways of navigating the pages elements:
 * <ul>
 * <li>An array of the elements</li>
 * <li>a parent -> child relationship stored in the elements</li>
 * <li>a map of element names and their associated position in the element array</li>
 * </ul>
 * 
 * @author Derek Rendall
 */
public class Page {
	
	private static final Logger logger = LoggerFactory.getLogger(Page.class);
	
	/** Normal pages may or may not have conditions that dictate when they are visible */
	public static final String PAGE_TYPE_NORMAL = "Normal";
	/** Branch pages are ones that are displayed when an Answer record is created */
	public static final String PAGE_TYPE_BRANCH = "Branch";
	
	private String id;
	private String initialState;
	private String type;
	private String sheetName;
	private String label;
	private String displayAfter;
	
	// processing variables
	private PageElement parentPageElement;
	
	private List<PageElement> elements = new ArrayList<PageElement>();
	private Map<String, Integer> elementLookup = new HashMap<String, Integer>();
	

	/**
	 * Sets up the base entry in the element list. If the element passed in has a logic
	 * condition then this is a Conditional page. All branched pages should have logic. 
	 * 
	 * @param sheetName
	 * 			For possible reference in messages and/or rule names
	 * @param element
	 * 			The Group that represents the page.
	 * @param currentPage
	 * 			To default the display order (the new page cpomes after this page).
	 */
	public Page(String sheetName, PageElement element, Page currentPage) {
		super();
		this.sheetName = sheetName;
		id = element.getId();
		initialState = (element.getLogicElement() == null) ? "Visible" : "Hidden";
		type = element.getPageType();
		parentPageElement = element;
		displayAfter = element.getPostLabel();
		element.setPostLabel(null);
		//logger.debug("Creating page " + id + " for sheet: " + sheetName + " with initialState " +initialState);
	    addElement(element);
		if ((getDisplayAfter() == null) && (!isVisible()) && (currentPage != null)) {
			setDisplayAfter(currentPage.getId());
		}
	}

	public String getId() {
		return id;
	}
	
	public String getSheetName() {
		return sheetName;
	}

	public String getInitialState() {
		return initialState;
	}

	public List<PageElement> getElements() {
		return elements;
	}

	public boolean isVisible() {
		if (initialState.toUpperCase().startsWith("V")) {
			return true;
		}
		return false;
	}

	public String getType() {
		return type;
	}

	public PageElement getParentPageElement() {
		return parentPageElement;
	}

	public String getDisplayAfter() {
		return displayAfter;
	}

	public void setDisplayAfter(String displayAfter) {
		this.displayAfter = displayAfter;
	}

	public boolean isBranchedPage () {
		return getType().equals(PAGE_TYPE_BRANCH);
	}
	
	protected void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Adds to list of page elements, and to element lookup list.
	 * Will also take note of any need to assign a lookup table to this element
	 * at some point later.
	 * 
	 * @param element
	 * 			If this is a repeating element then it is not added to the list. Such elements 
	 * 			should not be looked up, as only the original master element is actually "real".
	 * 			However, they will be added to the list of the parent elements children, so that 
	 * 			they get added to a list of items (and thus displayed).
	 */
	public void addElement(PageElement element) {
		if (element.isARepeatingElement()) {
			logger.debug("Info: ignoring request to add repeating element " + getId() + " to page");
			return;
		}
		if (element.getLookupTableId() != null) {
			elementLookup.put(element.getLookupTableId(), new Integer(elements.size()));
		}
		elementLookup.put(element.getId(), new Integer(elements.size()));
		elements.add(element);
	}

	/**
	 * Used when needing to lookup elements for logic element references and other element lookups.
	 * 
	 * @param id
	 * @return
	 */
	public PageElement findElementOnThisPage(String id) {
		Integer key = elementLookup.get(id);
		if (key == null) {
			// not on this page - maybe on another
			return null;
		}
		return elements.get(key.intValue());
	}
	
	/**
	 * At the end of loading the spreadsheet, this should be called to set up
	 * all the table entries prior to writing the DRL files.
	 * 
	 * @param tables
	 * 			Stored in the application, passed to each page in turn to set the association.
	 */
	public void assignTables(Map<String, LookupTable> tables) {
		for (Iterator<PageElement> i = elements.iterator(); i.hasNext();) {
			PageElement pageElement = (PageElement) i.next();
			if ((pageElement.getLookupTableId() != null) && (pageElement.getLookupTable() == null)) {
				LookupTable table = tables.get(pageElement.getLookupTableId());
				if (table != null) {
					pageElement.setLookupTable(table);
				}
				else {
					throw new IllegalArgumentException("No known table for " + pageElement.getLookupTableId());
				}
			}
		}
	}
}
