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

import java.util.Iterator;
import java.util.List;

import org.drools.informer.load.questionnaire.ExtractItems;
import org.drools.informer.load.questionnaire.InformerSpreadsheetLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.informer.domain.questionnaire.Application;
import org.drools.informer.domain.questionnaire.Page;
import org.drools.informer.load.spreadsheet.SpreadsheetRow;
import org.drools.informer.load.spreadsheet.sections.SpreadsheetSection;


/**
 * Processes the Page related sections on a Spreadsheet page (sheet), currently
 * limited to the Item section. Note that there can be multiple sections
 * on a single Spreadsheet Page (Sheet). 
 * 
 * Relates to Questionnaire type Spreadsheets.
 * 
 * @author Derek Rendall
 */
public class ExtractPages implements SpreadsheetSectionConstants {
	
	// TODO all the validations - removing spaces, checking types etc

	private static final Logger logger = LoggerFactory.getLogger(ExtractPages.class);
	
	private Application application;
	private List<SpreadsheetSection> data;
	protected Page currentPage;
	protected String currentSheetName;
	
	
	public ExtractPages(List<SpreadsheetSection> theData, Application theApplication) {
		super();
		data = theData;
		application = theApplication;
	}
	

	/**
	 * Identify the Page related sections (Item) that we want to process, and initiate that.
	 * @return
	 * 			true if all sections processed OK
	 */
	public boolean processPages() {
		for (Iterator<SpreadsheetSection> iterator = data.iterator(); iterator.hasNext();) {
			SpreadsheetSection section = (SpreadsheetSection) iterator.next();
			if (section.isProcessed()) {
				continue;
			}
			
			if (!processSectionData(section)) {
				logger.debug("Failed to process section " + section.getSectionHeadingString() + " for sheet " + section.getSheetName());
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Process a (Item) section.
	 * 
	 * Each section has a heading row that is used to identify what field each cell relates to in the domain object.
	 * 
	 * @param section
	 * @return
	 */
	protected boolean processSectionData(SpreadsheetSection section) {
		List<SpreadsheetRow> rows = section.getSectionRows();
		currentSheetName = section.getSheetName();
		
		if (section.getSectionHeadingString().startsWith(PAGE_ITEMS_UPPER)) {
			currentPage = new ExtractItems(application, currentPage).processSectionData(section);
			if (currentPage == null) {
				logger.debug("Warning: no current page returned for section. " + section.toString());
				return true;
			}
			return true;
		}
		
		for (Iterator<SpreadsheetRow> rowIter = rows.iterator(); rowIter.hasNext();) {
			SpreadsheetRow spreadsheetRow = (SpreadsheetRow) rowIter.next();
			if (spreadsheetRow.getRowItems().size() == 0) {
				continue;
			}
			if (section.getSectionHeadingString().startsWith(InformerSpreadsheetLoader.SHEET_END)) {
				// ignore the rest of the rows, although should never have got here
				logger.debug("Did not expect to be processing a Sheet End section - ignoring!");
				break;
			}
			
		}
		
		return true;
	}
}
