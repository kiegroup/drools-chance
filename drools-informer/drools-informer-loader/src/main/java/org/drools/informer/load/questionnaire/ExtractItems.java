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
package org.drools.informer.load.questionnaire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.informer.domain.questionnaire.Application;
import org.drools.informer.domain.questionnaire.Page;
import org.drools.informer.domain.questionnaire.PageElement;
import org.drools.informer.domain.questionnaire.conditions.ConditionClause;
import org.drools.informer.domain.questionnaire.conditions.PageElementCondition;
import org.drools.informer.load.spreadsheet.SpreadsheetItem;
import org.drools.informer.load.spreadsheet.SpreadsheetRow;
import org.drools.informer.load.spreadsheet.sections.SpreadsheetSection;

/**
 * Processes the Page element items for an Item Section on a Spreadsheet page (sheet), creating
 * the {@link PageElement} objects. Note that there can be multiple Tohu Pages (Group)
 * on a single Spreadsheet Page (Sheet). Pages can also be spread over multiple sheets (but
 * this will require specifying the appendAfter Page Id via the postLabel column).
 * 
 * A Spreadsheet Page can contain multiple item sections (which will be attached to the same page
 * if no new page is specified). Note: until the first page is defined, only Global Impact type
 * elements can be defined (as they do not require any attachment to a Group as they are never 
 * displayed in the UI).
 * 
 * By using multiple ItemId columns, the nested depth of an element can be obtained,
 * and this is used to provide the nesting structure for groups and other objects.
 * 
 * For very simple spreadsheets (single page), a page (Group) will be created
 * automatically, if one is not defined.
 * 
 * Groups will be created automatically where required to handle nested questions. You
 * may want to list the group explicitly if you want to specify a label or style for it.
 * 
 * Items will be created such that their inclusion depends on the parent group being
 * visible, which means that the Display Conditions are effectively propagated to all
 * child elements.
 * 
 * Relates to Questionnaire type Spreadsheets.
 *
 * @author Derek Rendall
 */
public class ExtractItems implements SpreadsheetSectionConstants {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtractItems.class);
	
	/** provides ability to lookup an element defined previously, if needed */
	private Application application;
	protected Page currentPage;
	protected int currentDepth = 1;
	/** Used to grab the parent group (up a depth level) to attach items to etc */
	private List<PageElement> currentElementsAtDepth = new ArrayList<PageElement>();
	/** When backtracking up a depth need to know what the outer page was, in case just finished dealing with a Branch page */
	private List<Page> currentPageAtDepth = new ArrayList<Page>();
	
	/** Useful for name identification */
	private String currentSheetName;
	
	public static final String ELEMENT_PAGE_UPPER = "PAGE";
	public static final String ELEMENT_BRANCH_UPPER = "BRANCH";
	
	/**
	 * 
	 * @param application
	 * @param currentPage
	 * 			Useful in cases where there are multiple Item sections, as they will be
	 * 			added to the "current" page. Will be null first time.
	 */
	public ExtractItems(Application application, Page currentPage) {
		super();
		this.application = application;
		this.currentPage = currentPage;
	}
		
	/**
	 * When we drop down a depth, we may need to create a group to contain the
	 * elements at the new depth.
	 * 
	 * @param element
	 */
	protected void createNormalIntermediateGroup(PageElement element) {
		PageElement currentParent = getElementAtDepth(currentDepth);
		PageElement newGroup = new PageElement();
		String tempStr = "_CHILDREN";
		logger.debug("Warning: creating new Intermediate Group: " + currentParent.getId()+tempStr + " current depth = " + currentDepth);
		newGroup.setId(currentParent.getId() + tempStr, currentDepth, element.getRowNumber());
		newGroup.setType("Group");

		if (!currentParent.isAGroupType()) {
			if (currentDepth == 1) {
				currentParent = currentPage.getParentPageElement();
			} 
			else {
				currentParent = getElementAtDepth(currentDepth - 1);
			}
		}
		currentPage.addElement(newGroup);
		currentParent.addChild(newGroup);
		currentElementsAtDepth.set(currentDepth - 1, newGroup);
	}
	
	/**
	 * Store the element at the depth, so we can access it for assigning parent and
	 * previous sibling information. Note: we can go down a couple of levels and then return to 
	 * adding more elements to a group.
	 * 
	 * @param element
	 */
	protected void setElementAtDepth(PageElement element) {
		
		int depth = element.getDepth();

		if ((depth < 1) || (depth > (currentElementsAtDepth.size() + 1))) {
			throw new IllegalArgumentException("Cannot set an element depth of " + String.valueOf(depth) + " when depth tree size is " + String.valueOf(currentElementsAtDepth.size()));
		}
		
		if (element.isAnImpactType()) {
			if (currentPage == null) {
				application.addGlobalElement(element);
				return;
			}
			if (depth == 1) {
				currentPage.getParentPageElement().addChild(element);
			}
			else {
				getElementAtDepth(depth - 1).addChild(element);
			}
			currentPage.addElement(element);
			return;
		}
		
		// now check to see if we need to add an automatic group
		// Note: depth == 1 will mean that there is a page group created, therefore no problem
		if ((depth > 1) && (depth > currentDepth) && (!getElementAtDepth(currentDepth).isAGroupType()) && (!element.isABranchedPage())) {
			//logger.debug("Item at current depth: " + getElementAtDepth(currentDepth).getId());
			createNormalIntermediateGroup(element);
		}
		
		if (!element.isAPageElement() && (currentElementsAtDepth.size() == 0)) {
			//logger.debug("About to create a default master page");
			PageElement masterElement = new PageElement();
			masterElement.setId("DefaultPage", 0, 0);
			masterElement.setType("Page");
			currentPage = new Page(currentSheetName, masterElement, currentPage);
			application.addPage(currentPage);
		}
		else if (element.isAPageElement()) {
			currentPage = new Page(currentSheetName, element, currentPage);
			application.addPage(currentPage);
		}
		
		// Add it into the right position on the working lists
		if (depth > currentElementsAtDepth.size()) {
			//logger.debug("Setting element " + element.getId() + " to depth " + currentElementsAtDepth.size() + 1);
			currentElementsAtDepth.add(element);
			currentPageAtDepth.add(currentPage);
		}
		else {
			//logger.debug("Setting element " + element.getId() + " at depth " + depth);
			currentElementsAtDepth.set(depth - 1, element);
			for (int i = (currentElementsAtDepth.size() - 1); i >= depth ; i--) {
				currentElementsAtDepth.remove(i);
				currentPageAtDepth.remove(i);
			}
			if (element.isAPageElement()) {
				currentPageAtDepth.set(depth - 1, currentPage);
			}
			else {
				currentPage = currentPageAtDepth.get(depth - 1);
			}
		}
		
		// Deal with linking elements on the page
		if (!element.isAPageElement()) {
			currentPage.addElement(element);
			PageElement tempElement = (depth == 1) ? currentPage.getParentPageElement() : getElementAtDepth(depth - 1);
			tempElement.addChild(element);
		}
		currentDepth = element.getDepth();
	}
	

	/**
	 * Depth starts at 1, so will access list at index of depth - 1
	 * 
	 * @param depth
	 * @return
	 */
	protected PageElement getElementAtDepth(int depth) {
		if ((depth < 1) || (depth > currentElementsAtDepth.size())) {
			return null;
		}
		return currentElementsAtDepth.get(depth - 1);
	}
	
	
	
	/**
	 * Will process a line of the spreadsheet. It will either be a new element, or a continuation of a 
	 * condition on the display/value of the previous element. In the latter case the {@link ConditionClause}
	 * will be extracted from the current element (line) and added to the previous (real) element.
	 * 
	 * @param section
	 * @return
	 * 			The current page, which may be a newer one than the one passed in. Callers
	 * 			can then pass into the next Item Section to be processed.
	 */
	public Page processSectionData(SpreadsheetSection section) {
		List<SpreadsheetRow> rows = section.getSectionRows();
		currentSheetName = section.getSheetName();
		PageElement lastRealElement = null;
		
		for (Iterator<SpreadsheetRow> rowIter = rows.iterator(); rowIter.hasNext();) {
			SpreadsheetRow spreadsheetRow = (SpreadsheetRow) rowIter.next();
			if (spreadsheetRow.getRowItems().size() == 0) {
				continue;
			}
			
			//logger.debug("Processing row " + spreadsheetRow.getRowNumber());
			
			PageElement element = extractPageElement(section, spreadsheetRow);
			
			//logger.debug("Processing line " + spreadsheetRow.getRowNumber() + " item id " + element.getId() + " depth " + String.valueOf(element.getDepth()));
			
			if (element.getId() != null){
				// ie not a display fact or impact extension
				setElementAtDepth(element);
				lastRealElement = element;
			}
			
			// Now deal with condition facts
			if ((element.getLogicElement() != null) && (!element.getLogicElement().isProcessed())) {
				if (lastRealElement.isAnImpactType()) {
					if (element.getId() != null) {
						if (element.getLogicElement() == null) {
							throw new IllegalArgumentException("You must specify a logic clause on an Impact " + element.getId());
						}
						// Will turn this into a global by not requiring the parent to be visible
						element.setRequired("Yes");
					}					
					processConditionClauseLine(lastRealElement, element, spreadsheetRow.getRowNumber());
				}
				else if (lastRealElement.isAValidationElement()) {
					processValidationClauseLine(lastRealElement, element, spreadsheetRow.getRowNumber());
				}
				else {
					processConditionClauseLine(lastRealElement, element, spreadsheetRow.getRowNumber());
				}
			}
		}
		
		return currentPage;
	}
	
	
	/**
	 * Maps the row cells to values in the {@link PageElement} object. Uses the section heading row to identify
	 * what each column (attribute) each cell represents. Order of columns is arbitrary, other than having 
	 * an Item Id as the first column.
	 * 
	 * If an Impact type has no logic associated, the code will look for a parent with logic, to control
	 * the creation/assigning of the logic for the impact. This results in writing the logic once.
	 * 
	 * @param section
	 * @param row
	 * @return
	 */
	protected PageElement extractPageElement(SpreadsheetSection section, SpreadsheetRow row) {
		SpreadsheetRow headings = section.getHeaderRow();
		PageElement element = new PageElement();
		for (Iterator<SpreadsheetItem> iterator = row.getRowItems().iterator(); iterator.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) iterator.next();
			String key = headings.getHeaderTextForColumnInUpperCase(item.getColumn());
			if (key == null) {
				// Comment item - ignore
				logger.debug("Ignoring value: " + item);
				continue;
			}
			String value = item.toString();
			if (key.startsWith(PAGE_ITEMS_UPPER)) {
				element.setId(value, section.getHeaderDepthForColumn(item.getColumn()), item.getRow());
				continue;
			}
			if (key.startsWith("SET")) {
				element.setDefaultValueStr(value);
				continue;
			}
			if (key.startsWith("STYLE")) {
				element.addStyle(value);
				continue;
			}
			if (key.startsWith("TYPE")) {
				element.setType(value);
				continue;
			}
			if (key.startsWith("REQUIRE")) {
				element.setRequired(value);
				continue;
			}
			if (key.startsWith("DATA")) {
				element.setFieldType(value);
				continue;
			}
			if (key.startsWith("PRE")) {
				element.setPreLabel(value);
				continue;
			}
			if (key.startsWith("POST")) {
				element.setPostLabel(value);
				continue;
			}
			if (key.startsWith("SELECTION")) {
				element.setLookupTableId(value);
				continue;
			}
			if (key.startsWith("CATEGORY")) {
				element.setCategory(value);
				continue;
			}
			if (key.startsWith("DEPENDS")) {
				element.setLogicDependsOnItemId(value);
				continue;
			}
			if (key.startsWith("ATTRIBUTE")) {
				element.setLogicAttribute(value);
				continue;
			}
			if (key.startsWith("OPERATION")) {
				element.setLogicOperation(value);
				continue;
			}
			if (key.startsWith("VALUE")) {
				element.setLogicValue(value);
				continue;
			}
			logger.debug("Unknown Section key: " + key);
		}
		if ((element.getId() != null) && (element.getType() == null)) {
			throw new IllegalArgumentException("Row " + String.valueOf(row.getRowNumber() + 1) + " has no type!");
		}
		if ((element.getType() != null) && (element.isAnImpactType()) && (element.getLogicElement() == null) && (currentPage != null)) {
			int depth = currentDepth;
			try {
				while (depth > 0) {
					PageElement temp = getElementAtDepth(depth);
					if ((temp != null) && (temp.getLogicElement() != null)) {
						element.setDisplayCondition((PageElementCondition)temp.getDisplayCondition().clone());
						element.setLogicElement(temp.getLogicElement());
						depth = 0;
					}
					depth--;
				}
				if (element.getLogicElement() == null) {
					if (currentPage.getParentPageElement().getLogicElement() != null) {
						element.setDisplayCondition((PageElementCondition)currentPage.getParentPageElement().getDisplayCondition().clone());
						element.setLogicElement(currentPage.getParentPageElement().getLogicElement());
					}
					else {
						throw new IllegalArgumentException("Row " + String.valueOf(row.getRowNumber() + 1) + " has a impact with no condition or parent with a condition!");
					}
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				throw new IllegalStateException(e.getMessage());
			}
		}
		
		return element;
	}
	
	
	/**
	 * Manage the logic associated with a Validation line.
	 * 
	 * @param masterElement
	 * 			If the current Element has an Item Id, then this will be the current element. Otherwise will be the
	 * 			previous real element - the one we want to attach the {@link ConditionClause} to.
	 * @param element
	 * 			Contains the {@link ConditionClause} that we need to create or add to the {@link PageElementCondition}.
	 * @param row
	 * 			Will be used to create rules that are uniquely named.
	 */
	protected void processValidationClauseLine(PageElement masterElement, PageElement element, int row) {
		// TODO handle repeated elements?
		ConditionClause le = element.getLogicElement();
		if (element.getId() != null) {
			// first line 
			String type = PageElementCondition.TYPE_VALIDATION;
			masterElement.setDisplayCondition(new PageElementCondition(type, masterElement.getId(), row));
		}
		masterElement.getDisplayCondition().addElement(le);
		le.setProcessed(true);
	}

	/**
	 * Manage the logic associated with a Non-validation related conditional line. If the
	 * logic relates to a Page or a Branch then a special version of {@link PageElementCondition} is
	 * created, containing relevant page information for creating the relevant rules.
	 * 
	 * @param masterElement
	 * 			If the current Element has an Item Id, then this will be the current element. Otherwise will be the
	 * 			previous real element - the one we want to attach the {@link ConditionClause} to.
	 * @param element
	 * 			Contains the {@link ConditionClause} that we need to create or add to the {@link PageElementCondition}.
	 * @param row
	 * 			Will be used to create rules that are uniquely named - especially for AlternateImpact.
	 */
	protected void processConditionClauseLine(PageElement masterElement, PageElement element, int row) {
		// TODO handle repeated elements?
		ConditionClause le = element.getLogicElement();
		if (element.getId() != null) {
			// first line 
			String type = PageElementCondition.TYPE_INCLUSION;
			if (masterElement.isAPageElement()) {
				//logger.debug("Processing page displayFact: " + value);
				masterElement.setDisplayCondition(new PageElementCondition(type, masterElement.getId(), row, currentPage.getId(), currentPage.isBranchedPage(), currentPage.getDisplayAfter()));
			}
			else if (masterElement.isAnAlternateImpactItem()) {
				le.setExplanation(element.getPostLabel());
				masterElement.setDisplayCondition(new PageElementCondition(type, masterElement.getId() + String.valueOf(row), row));
			}
			else {
				masterElement.setDisplayCondition(new PageElementCondition(type, masterElement.getId(), row));
			}
		}
		masterElement.getDisplayCondition().addElement(le);
		le.setProcessed(true);
	}
}
