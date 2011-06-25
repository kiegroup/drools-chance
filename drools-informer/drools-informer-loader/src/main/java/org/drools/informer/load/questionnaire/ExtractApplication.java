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
package org.drools.informer.load.questionnaire;

import org.drools.informer.domain.questionnaire.Application;
import org.drools.informer.domain.questionnaire.LookupTable;
import org.drools.informer.domain.questionnaire.conditions.ConditionClause;
import org.drools.informer.load.spreadsheet.SpreadsheetItem;
import org.drools.informer.load.spreadsheet.SpreadsheetRow;
import org.drools.informer.load.spreadsheet.sections.SpreadsheetSection;
import org.drools.informer.load.questionnaire.SpreadsheetSectionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * Processes the application specific sections of the Spreadsheet, creating
 * the {@link Application} object and setting it's core values. Also processes
 * lookup table lists sections that are in the spreadsheet.
 * 
 * Relates to Questionnaire type Spreadsheets.
 *
 * @author Derek Rendall
 */
public class ExtractApplication implements SpreadsheetSectionConstants {
	// TODO all the validations - removing spaces, checking types etc
	
	private static final Logger logger = LoggerFactory.getLogger(ExtractApplication.class);
	
	private Application application = new Application();
	private SpreadsheetSection applicationSection;
	
	private List<SpreadsheetSection> tableSections = new ArrayList<SpreadsheetSection>();
	private LookupTable currentLookupTable;
	
	/**
	 * Take the Application and List sections, setting them aside for processing by this object.
	 * 
	 * @param sections
	 */
	public ExtractApplication(List<SpreadsheetSection> sections) {
		super();
		for (Iterator<SpreadsheetSection> iterator = sections.iterator(); iterator.hasNext();) {
			SpreadsheetSection spreadsheetSection = (SpreadsheetSection) iterator.next();
			if (spreadsheetSection.getSectionHeadingString().startsWith(APPLICATION_UPPER)) {
				applicationSection = spreadsheetSection;
			}
			else if (spreadsheetSection.getSectionHeadingString().startsWith(PAGE_LISTS_UPPER)) {
				tableSections.add(spreadsheetSection);
			}
		}
		if (applicationSection == null) {
			throw new IllegalArgumentException("There was no section heading with " + APPLICATION_UPPER + " found");
		}
	}

	/**
	 * Will process spreadsheet rows from application and table sections.
	 * 
	 * @return
	 * 			The {@link Application} object created from processing the application section.
	 */
	public Application processApp() {
		List<SpreadsheetRow> rows = applicationSection.getSectionRows();
		if (rows.isEmpty()) {
			return null;
		}
		for (Iterator<SpreadsheetRow> rowIter = rows.iterator(); rowIter.hasNext();) {
			SpreadsheetRow spreadsheetRow = (SpreadsheetRow) rowIter.next();
			processApplicationHeadingLine(applicationSection.getHeaderRow(), spreadsheetRow);
		}
		applicationSection.setProcessed(true);
		
		for (Iterator<SpreadsheetSection> i = tableSections.iterator(); i.hasNext();) {
			SpreadsheetSection ts = (SpreadsheetSection) i.next();
			rows = ts.getSectionRows();
			if (rows.isEmpty()) {
				continue;
			}
			for (Iterator<SpreadsheetRow> rowIter = rows.iterator(); rowIter.hasNext();) {
				SpreadsheetRow spreadsheetRow = (SpreadsheetRow) rowIter.next();
				processListLine(ts.getHeaderRow(), spreadsheetRow);
			}
			ts.setProcessed(true);
		}
				
		return application;
	}
	
	/**
	 * Process a spreadsheet line, using the appropriate header line. The header line contains references to the
	 * column title, so each cell can check what value it is for, without relying on
	 * specific column order (other than the first column, which is used to identify the section).
	 * 
	 * @param headings
	 * @param row
	 */
	protected void processApplicationHeadingLine(SpreadsheetRow headings, SpreadsheetRow row) {
		// TODO handle repeated elements?
		for (Iterator<SpreadsheetItem> iterator = row.getRowItems().iterator(); iterator.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) iterator.next();
			String key = headings.getHeaderTextForColumnInUpperCase(item.getColumn());
			if (key == null) {
				// Comment item - ignore
				continue;
			}
			String value = item.toString();
			if (key.startsWith(APPLICATION_UPPER)) {
				if (application.getId() != null) {
					throw new IllegalStateException("You cannot have two rows with an application id!");
				}
				application.setId(value);
			}
			else if (key.startsWith("BASE")) {
				application.setApplicationClass(value);
			}
			else if (key.equals("NAME")) {
				application.setApplicationName(value);
			}
			else if (key.equals("COMPLETION")) {
				application.setCompletionAction(value);
			}
			else if (key.startsWith("NOTE")) {
				application.setNote(value);
			}
			else if (key.startsWith("ACTIVE")) {
				application.setActivePage(value);
			}
			else if (key.startsWith("INCLUDE")) {
				application.addImport(value);
			}
			else if (key.startsWith("ACTION")) {
				application.setActionValidation(value);
			}
			else if (key.startsWith("MARKUP")) {
				application.setMarkupAllowed(value);
			}
			else {
				logger.debug("Unknown Application key: " + key);
			}
		}
	}
	
	
	/**
	 * Process a spreadsheet line, using the appropriate header line. The header line contains references to the
	 * column title, so each cell can check what value it is for, without relying on
	 * specific column order (other than the first column, which is used to identify the section).
	 * 
	 * Each line may have a condition associated with it, which means that the entry will be added/removed
	 * based on the associated logic.
	 * 
	 * @param headings
	 * @param row
	 */
	protected void processListLine(SpreadsheetRow headings, SpreadsheetRow row) {
		// TODO handle repeated elements?
		String itemValue = null, displayedValue = null;
		String itemName = null, attributeName = null, operation = null, rhs = null;
		for (Iterator<SpreadsheetItem> iterator = row.getRowItems().iterator(); iterator.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) iterator.next();
			String key = headings.getHeaderTextForColumnInUpperCase(item.getColumn());
			if (key == null) {
				// Comment item - ignore
				continue;
			}
			String value = item.toString();
			
			if (key.startsWith(PAGE_LISTS_UPPER)) {
				currentLookupTable = new LookupTable(value);
				application.addLookupTable(currentLookupTable);
				continue;
			}
			if (key.startsWith("ACTUAL")) {
				itemValue = value;
				continue;
			}

			if (key.startsWith("DISPLAY")) {
				displayedValue = value;
				continue;
			}
			
			if (key.startsWith("DEPENDS")) {
				itemName = value;
				continue;
			}

			if (key.startsWith("ATTRIBUTE")) {
				attributeName = value;
				continue;
			}

			if (key.startsWith("OP")) {
				operation = value;
				continue;
			}
			
			if (key.startsWith("VALUE")) {
				rhs = value;
				continue;
			}
			
			logger.debug("Unknown List key: " + key);
		}
		ConditionClause cc = null;
		if (itemName != null) {
			if (itemValue == null) {
				// TODO handle multiple lines
				throw new IllegalArgumentException("You cannot (yet) have more than one logic element for a list entry");
			}
			cc = new ConditionClause(itemName, attributeName, operation, rhs);
		}
		if (displayedValue == null) {
			currentLookupTable.addEntry(itemValue, cc);
		}
		else {
			currentLookupTable.addEntry(itemValue, displayedValue, cc);
		}
	}
}
