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
package org.drools.informer.load.spreadsheet.sections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.informer.load.spreadsheet.SpreadsheetItem;
import org.drools.informer.load.spreadsheet.SpreadsheetRow;

/**
 * Contains the rows associated with a section of data within the spreadsheet. Will
 * also contain the header row for that section, enabling the user to decypher the data elements. 
 * 
 * Contains a column depth map, which maps repeated occurrences of a column name (based
 * on column number as the key) and to what depth (starting with 1) that instance of the column is at.
 * 
 * @author Derek Rendall
 */
public class SpreadsheetSection implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sectionHeadingString;
	private List<SpreadsheetRow> sectionRows = new ArrayList<SpreadsheetRow>();
	private SpreadsheetRow headerRow;
	private Map<Integer, String> columnHeadingMap = new HashMap<Integer, String>();
	private Map<Integer, Integer> columnDepthMap = new HashMap<Integer, Integer>();
	private boolean processed;
	private String sheetName;
	
	/**
	 * New section, as identified by a new header row. Process the header row into columns,
	 * setting up all the necessary mappings.
	 * 
	 * @param sheetName
	 * @param sectionHeadingString
	 * @param headerRow
	 */
	public SpreadsheetSection(String sheetName, String sectionHeadingString, SpreadsheetRow headerRow) {
		super();
		if (headerRow == null) {
			throw new IllegalArgumentException("Null header row for " + sheetName + " " + sectionHeadingString);
		}
		this.sheetName = sheetName;
		this.sectionHeadingString = sectionHeadingString.toUpperCase();
		this.headerRow = headerRow;
		for (Iterator<SpreadsheetItem> heading = headerRow.getRowItems().iterator(); heading.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) heading.next();
			columnHeadingMap.put(new Integer(item.getColumn()), item.toString());
		}
		for (int i = 0; i < headerRow.getRowItems().size(); i++) {
			SpreadsheetItem item = headerRow.getRowItems().get(i);
			int count = 1;
			String tempStr = item.toString().toUpperCase();
			for (int j = 0; j < i; j++) {
				SpreadsheetItem previousItem = headerRow.getRowItems().get(j);
				if (previousItem.toString().toUpperCase().equals(tempStr)) {
					count++;
				}
			}
			columnDepthMap.put(new Integer(item.getColumn()), new Integer(count));
		}
	}

	public String getSectionHeadingString() {
		return sectionHeadingString;
	}

	public List<SpreadsheetRow> getSectionRows() {
		return sectionRows;
	}
	

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public String getSheetName() {
		return sheetName;
	}

	public SpreadsheetRow getHeaderRow() {
		return headerRow;
	}
	
	/**
	 * As processing row, may find that its a new section, in which case create and return that new section.
	 * 
	 *  Used by {@link SpreadsheetSectionSplitter}
	 * 
	 * @param row
	 * @param sectionHeadingStrings
	 * @return
	 */
	public SpreadsheetSection processSectionRow(String sheetName, SpreadsheetRow row, List<String> sectionHeadingStrings) {
		if (row.getRowItems().size() == 0) {
			return null;
		}
		for (Iterator<String> header = sectionHeadingStrings.iterator(); header.hasNext();) {
			String string = (String) header.next();
			if (row.getRowItems().get(0).toString().toUpperCase().startsWith(string)) {
				SpreadsheetSection newSection = new SpreadsheetSection(sheetName, row.getRowItems().get(0).toString().toUpperCase(), row);
				row.setHeaderRow(true);
				return newSection;
			}
		}
		
		sectionRows.add(row);
		return null;
	}
	
	/**
	 * @param column
	 * @return
	 */
	public String getHeaderStringForColumn(int column) {
		return columnHeadingMap.get(new Integer(column));
	}

	/**
	 * @param column
	 * @return Depth of the column, for repeated columns. Starts at 1
	 */
	public Integer getHeaderDepthForColumn(int column) {
		return columnDepthMap.get(new Integer(column));
	}

	@Override
	public String toString() {
		return String.format("headingString: %s, sheetName: %s, row: %d", sectionHeadingString, sheetName, (headerRow == null) ? 0 : headerRow.getRowNumber() + 1);
	}
}
