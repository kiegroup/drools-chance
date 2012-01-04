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
package org.drools.informer.load.spreadsheet;

import org.apache.poi.ss.usermodel.Cell;
import org.drools.informer.load.spreadsheet.CellIdentifier;

/**
 * Contains the data for a spreadsheet cell. 
 * 
 * @author Derek Rendall
 */
public class SpreadsheetItem {
		
	private CellIdentifier cellIdentifier;
	private Cell spreadsheetCell;
	private String sheetName;
	private int column;
	private int row;	// often used for id purposes
	
	/**
	 * Extract a couple of data elements for easy reference, such as row and column
	 * 
	 * @param sheetName
	 * @param cell
	 */
	public SpreadsheetItem(String sheetName, Cell cell) {
		super();
		this.sheetName = sheetName;
		this.spreadsheetCell = cell;
		column = spreadsheetCell.getColumnIndex();
		row = spreadsheetCell.getRowIndex();
		this.cellIdentifier = new CellIdentifier(this.sheetName, spreadsheetCell.getRowIndex(), column);
	}

	public String getCellIdentifier() {
		return cellIdentifier.getCellIdentifier();
	}
	
	public String toString() {
		return spreadsheetCell.toString();
	}

	public String getSheetName() {
		return sheetName;
	}

	public Cell getSpreadsheetCell() {
		return spreadsheetCell;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
}
