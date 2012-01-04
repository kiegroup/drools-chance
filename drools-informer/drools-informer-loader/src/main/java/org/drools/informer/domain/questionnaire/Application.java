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

/**
 * An object that holds the Application level data extracted from the spreadsheet.
 * 
 * It is used as a starting point/container for all pages, tables, globals and imports.
 * As such it is passed as a key attribute to the methods that write the drl files, to cater
 * for searches etc across all pages looking for a specific element etc.
 * 
 * @author Derek Rendall
 */
public class Application {
	
	private String id;
	private String applicationClass;
	private String applicationName;
	private String completionAction;
	private String activePage;
	private String note;
	/** If true, required fields etc will stop the user on the page until problem is fixed up */
	private String actionValidation = "false";
	private String markupAllowed = "false";
	private List<Page> pageList = new ArrayList<Page>();
	private List<String> imports = new ArrayList<String>();
	
	/** PageElements (Impacts Only) defined prior to pages */
	private List<PageElement> globalElements = new ArrayList<PageElement>();
	
	/** Tables contain the Possible Answer data elements for Multiple Choice Questions */
	private Map<String, LookupTable> listTables = new HashMap<String, LookupTable>();
	
	/** 
	 * A list of placeholder values to indicate that the creation of the base 
	 * impact fact does not need to be repeated 
	 */
	private List<String> initiatedAlternateImpacts = new ArrayList<String>();

	
	public Application() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getActionValidation() {
		return actionValidation;
	}

	public void setActionValidation(String actionValidation) {
		this.actionValidation = actionValidation;
	}

	public LookupTable getLookupTable(String key) {
		return listTables.get(key);
	}

	public void addLookupTable(LookupTable table) {
		this.listTables.put(table.getId(), table);
	}

	public void addGlobalElement(PageElement element) {
		globalElements.add(element);
	}

	public List<PageElement> getGlobalElements() {
		return globalElements;
	}

	public void setApplicationClass(String applicationClass) {
		this.applicationClass = applicationClass;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public void setCompletionAction(String completionAction) {
		this.completionAction = completionAction;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getApplicationClass() {
		return applicationClass;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getCompletionAction() {
		return completionAction;
	}
		
	public String getActivePage() {
		return activePage;
	}

	public void setActivePage(String activePage) {
		this.activePage = activePage;
	}

	public String getMarkupAllowed() {
		return markupAllowed;
	}

	public void setMarkupAllowed(String markupAllowed) {
		this.markupAllowed = markupAllowed;
	}

	public void addImport(String name) {
		imports.add(name);
	}
	
	public List<String> getImports() {
		return imports;
	}

	public void addPage(Page thePage) {
		//System.out.println("addPage for " + name + " type: " + thePage.getType() + " getDisplayAfter: " + String.valueOf(thePage.getDisplayAfter()));
		pageList.add(thePage);
	}
	
	public List<Page> getPageList() {
		return pageList;
	}
	
	/**
	 * This returns a comma separated list of the items (pages) for the Questionnaire.
	 * The list uses the pages in the order found, but checks the displayed after 
	 * attribute of the page to adjust the order, to make sure that the list is
	 * complete with all pages (visible or hidden, but not branch pages) in the
	 * expected order.
	 * 
	 * @return
	 */
	public String getItemList() {
		if (pageList.isEmpty()) {
			throw new IllegalStateException("You must have at least one page");
		}
		
		boolean found = false;
		List<String> orderedPages = new ArrayList<String>();
		for (int i = 0; i < pageList.size(); i++) {
			Page pg = pageList.get(i);
			String pageName = pg.getId();
			if (!pg.isBranchedPage()) {
				if (pg.getDisplayAfter() != null) {
					//System.out.println("Page " + pageName + " displayed after " + pg.getDisplayAfter());
					int pos = orderedPages.indexOf(pg.getDisplayAfter());
					if ((pos < 0) || (pos == (orderedPages.size() - 1))) {
						orderedPages.add(pageName);
					}
					else {
						orderedPages.add(pos + 1, pageName);
					}
				}
				else {
					orderedPages.add(pageName);
				}
				found = true;
			}
		}
		if (!found) {
			throw new IllegalStateException("You must have at least one non branched page to start with");
		}
		
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < orderedPages.size(); i++) {
			String pageName = orderedPages.get(i);
			str = str.append((i > 0) ? ", \"" : "\"").append(pageName).append("\"");
		}
		
		return str.toString();
	}
	
	/**
	 * Finds the element on any of the pages.
	 * 
	 * Particularly useful for reused elements or logic reference lookups.
	 * 
	 * @param id
	 * @return
	 */
	public PageElement findPageElement(String id) {
		if ((id == null) || (id.length() == 0)) {
			return null;
		}
		for (Iterator<PageElement> iterator = globalElements.iterator(); iterator.hasNext();) {
			PageElement element = (PageElement) iterator.next();
			if (element.getId().equals(id)) {
				return element;
			}
		}
		for (Iterator<Page> iterator = pageList.iterator(); iterator.hasNext();) {
			Page pg = iterator.next();
			PageElement element = pg.findElementOnThisPage(id);
			if (element != null) {
				return element;
			}
		}
		return null;
	}
	
	/**
	 * For each page, assign the tables to the elements that refer to the table.
	 * 
	 * Called after spreadsheet loaded and prior to writing out drl files.
	 */
	public void processTableEntries() {		
		for (Iterator<Page> i = pageList.iterator(); i.hasNext();) {
			Page pg = i.next();
			pg.assignTables(listTables);
		}
		
	}
	

	/**
	 * Used when creating a new fact for this Impact. The first element that
	 * refers to this impact should also create the fact. Each element thereafter
	 * should only use the fact, not create it.
	 * 
	 * @param id
	 * @return true if this has not been added before
	 */
	public boolean addNewAlternateImpact(String id) {
		if (initiatedAlternateImpacts.contains(id)) {
			//System.out.println("Already used AlternateImpact Id: " + id);
			return false;
		}
		initiatedAlternateImpacts.add(id);
		//System.out.println("Not used AlternateImpact Id: " + id);
		return true;
	}
}
