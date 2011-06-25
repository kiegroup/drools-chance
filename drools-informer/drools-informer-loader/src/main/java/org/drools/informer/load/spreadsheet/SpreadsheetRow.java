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
package org.drools.informer.load.spreadsheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 *  Contains the data for a spreadsheet row.
 * 
 *  Will differentiate between a header row and a normal row.
 *  
 *  Provides utility methods to help map cell values from normal rows given a header row to work with.
 * 
 * @author Derek Rendall
 */
public class SpreadsheetRow implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int rowNumber;
	private boolean headerRow;
	private Map<Integer, Integer> headerRowColumns;
	
	private List<SpreadsheetItem> rowItems = new ArrayList<SpreadsheetItem>();
	
	public SpreadsheetRow(int row) {
		super();
		this.rowNumber = row;
	}
	
	/**
	 * If headerRow has already been set, then will also add mapping value for item (column number to heading string).
	 * 
	 * @param item
	 */
	public void addRowItem(SpreadsheetItem item) {
		if (headerRow) {
			headerRowColumns.put(new Integer(item.getColumn()), new Integer(rowItems.size()));
		}
		rowItems.add(item);
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public List<SpreadsheetItem> getRowItems() {
		return rowItems;
	}

	public boolean isHeaderRow() {
		return headerRow;
	}

	/**
	 * If called (with true) after all cells set, will create the lookup map for the header row 
	 * @param headerRow
	 */
	public void setHeaderRow(boolean headerRow) {
		if (this.headerRow && headerRow) {
			return;
		}
		this.headerRow = headerRow;
		if (!this.headerRow) {
			headerRowColumns = null;
		}
		else {
			headerRowColumns = new HashMap<Integer, Integer>(rowItems.size());
			int count = 0;
			for (Iterator<SpreadsheetItem> iterator = rowItems.iterator(); iterator.hasNext();) {
				SpreadsheetItem item = (SpreadsheetItem) iterator.next();
				headerRowColumns.put(new Integer(item.getColumn()), new Integer(count));
				count++;
			}
		}
	}
	
	/**
	 * Utility method used by classes processing non header rows to identify what attribute this item is.
	 * Performs a lookup on column position, thus the ordering of columns can vary between spreadsheets,
	 * although the first column must always be the one that identifies a section.
	 * 
	 * @param column
	 * @return
	 */
	public SpreadsheetItem getHeaderEntryForColumn(int column) {
		if (!headerRow) {
			throw new UnsupportedOperationException("Cannot access a header column when the record is not a header column");
		}
		Integer i = headerRowColumns.get(new Integer(column));
		if (i == null) {
			return null;
		}
		return rowItems.get(i.intValue());
	}
	
	/**
	 * Used for comparisons, to identify the type of the column. 
	 * 
	 * @param column
	 * @return
	 */
	public String getHeaderTextForColumnInUpperCase(int column) {
		SpreadsheetItem item = getHeaderEntryForColumn(column);
		return item.getSpreadsheetCell().toString().toUpperCase();
	}
	
	/**
	 * Used to see if there is actually any data in the row, or is it just comments etc.
	 * 
	 * @return
	 */
	public int firstColumnWithAnEntry() {
		if (rowItems.size() == 0) {
			return -1;
		}
		return rowItems.get(0).getColumn();
	}
	
	/**
	 * Ignore leading comments (i.e. that have no column heading).
	 * 
	 * @param headerRow
	 * @return
	 */
	public SpreadsheetItem itemForFirstHeaderColumn(SpreadsheetRow headerRow) {
		int firstColumn = headerRow.firstColumnWithAnEntry();
		for (Iterator<SpreadsheetItem> iterator = rowItems.iterator(); iterator.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) iterator.next();
			if (item.getColumn() == firstColumn) {
				return item;
			}
		}
		return null;
	}
}
