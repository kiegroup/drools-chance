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
package org.drools.informer.load.spreadsheet.sections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.informer.load.spreadsheet.SpreadsheetRow;
import org.drools.informer.load.questionnaire.InformerSpreadsheetLoader;
import org.drools.informer.load.spreadsheet.SpreadsheetData;
import org.drools.informer.load.spreadsheet.SpreadsheetItem;
import org.drools.informer.load.spreadsheet.WorkbookData;

/**
 * Contains the sections of data within the spreadsheet (see {@link SpreadsheetSection}.  
 * 
 * @author Derek Rendall
 */
public class SpreadsheetSectionSplitter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<SpreadsheetSection> sections = new ArrayList<SpreadsheetSection>();
	
	// Upper cased list of section headings - in first column with a value for a row
	private List<String> sectionHeadings = new ArrayList<String>();
	
	// Dummy section record - will not be added to list but will provide placeholder
	// to access instance methods to do processing
	private SpreadsheetSection currentSection = new SpreadsheetSection("Initialize", InformerSpreadsheetLoader.SHEET_END, new SpreadsheetRow(0));
	
	
	/**
	 * Expect to be passed a list of the section headings, which are the items expected to be the first 
	 * valid entries in a column that indicate that we have started a new section.
	 * 
	 * @param headings
	 */
	public SpreadsheetSectionSplitter(String[] headings) {
		super();
		for (int i = 0; i < headings.length; i++) {
			String string = (String) headings[i];
			sectionHeadings.add(string.toUpperCase());
		}
	}
	
	/**
	 * Thank you very much Microsoft for transforming lots of characters to obscure UTF values.
	 * 
	 * This method cleans a bit of that up. Currently handles:
	 * 
	 * <ul>
	 * <li>ellipses is converted to <code>&#8230;</code></li>
	 * </ul>
	 * 
	 * @param row
	 */
	public void cleanUpRowItemStrings(SpreadsheetRow row) {
		// TODO probably should make these values static etc at some point
		char c2 = 8230;
		String tempCharStr = new String(new char[] {c2});
		
		for (Iterator<SpreadsheetItem> iterator = row.getRowItems().iterator(); iterator.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) iterator.next();
			String tempStr = item.getSpreadsheetCell().toString();
			int pos = tempStr.indexOf(c2);

			if (pos >= 0) {
				tempStr = tempStr.replaceAll(tempCharStr, "&#8230;");
				item.getSpreadsheetCell().setCellValue(tempStr);
			}
		}

	}
	
	/**
	 * Identify and create section list. New sections can be returned from processing a row, so
	 * capture that and add new section to array of them.
	 * 
	 * @param wbData
	 * @return
	 */
	public List<SpreadsheetSection> splitIntoSections(WorkbookData wbData) {
		for (Iterator<String> iterator = wbData.getSheetList().iterator(); iterator.hasNext();) {
			String sheetName = (String) iterator.next();
			SpreadsheetData sheetData = wbData.getSheet(sheetName);
			if (sheetData.isProcessed() || (sheetData.getFirstItemOnSheet() == null) || 
					(sheetData.getFirstItemOnSheet().toString().toUpperCase().startsWith(InformerSpreadsheetLoader.SHEET_END)) ) {
				//System.out.println("Invalid sheet for: " + sheetName + ", processed: " + String.valueOf(sheetData.isProcessed()) + ", item: [" + String.valueOf(sheetData.getFirstItemOnSheet() + "]"));
				continue;
			}
			
			List<SpreadsheetRow> rows = sheetData.getRows();
			if (rows.isEmpty()) {
				sheetData.setProcessed(true);
				continue;
			}
			for (Iterator<SpreadsheetRow> rowIter = rows.iterator(); rowIter.hasNext();) {
				SpreadsheetRow spreadsheetRow = (SpreadsheetRow) rowIter.next();
				if (spreadsheetRow.getRowItems().size() == 0) {
					continue;
				}
				
				cleanUpRowItemStrings(spreadsheetRow);				
				
				SpreadsheetSection newSection = currentSection.processSectionRow(sheetName, spreadsheetRow, sectionHeadings);
				if (newSection != null) {
					//System.out.println("Found new section: " + newSection.getSectionHeadingString());
					if (newSection.getSectionHeadingString().toUpperCase().startsWith(InformerSpreadsheetLoader.SHEET_END)) {
						//System.out.println("Ignoring end section");
						break;
					}
					//System.out.println("Adding new section");
					sections.add(newSection);
					currentSection = newSection;
				}
			}
			sheetData.setProcessed(true);
		}
		
		return sections;
	}

}
