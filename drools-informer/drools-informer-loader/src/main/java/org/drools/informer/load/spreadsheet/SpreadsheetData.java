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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Contains the data for a sheet in the workbook, split into the constituent items. 
 * 
 * Null/empty entries are ignored.
 * 
 * @author Derek Rendall
 */
public class SpreadsheetData {
	
	private HashMap<String, SpreadsheetItem>data = new HashMap<String, SpreadsheetItem>();
	private List<String> cellList = new ArrayList<String>(1000);
	private List<SpreadsheetRow> rows = new ArrayList<SpreadsheetRow>(12);
	private boolean processed;
	private SpreadsheetData parentSheet;
	private SpreadsheetItem firstItemOnSheet;
	private int keyColumn;
	private String sheetName;
	
	/**
	 * Will split the sheet from the workbook up into {@link SpreadsheetRow} and {@link SpreadsheetItem}
	 * 
	 * @param sheet
	 */
	public SpreadsheetData(HSSFSheet sheet) {
		super();
		sheetName = sheet.getSheetName();
		for (Row row : sheet) {
			int rowNumber = row.getRowNum();
			SpreadsheetRow rowItems = new SpreadsheetRow(rowNumber);
			rows.add(rowItems);
			for (Cell cell : row) {
				if ((cell == null) || (cell.getCellType() == Cell.CELL_TYPE_BLANK)) {
					// null check is just in case - should never be!
					continue;
				}
				if ((keyColumn > 0) && (cell.getColumnIndex() < keyColumn)) {
					// comments column
					continue;
				}
				SpreadsheetItem item = new SpreadsheetItem(sheet.getSheetName(), cell);
				if (firstItemOnSheet == null) {
					// The first cell item must be sheet identifier/heading - previous columns will be treated as comments
					// and thus ignored
					firstItemOnSheet = item;
					keyColumn = cell.getColumnIndex();
				}
				String id = item.getCellIdentifier();
				
				//System.out.println("Sheet:" + sheet.getSheetName() + ", id=" + id + ", toString=" + item.toString());
				data.put(id, item);
				rowItems.addRowItem(item);
				cellList.add(item.getCellIdentifier());
			}
		}
	}
	
	public String getSheetName() {
		return sheetName;
	}

	public SpreadsheetItem getFirstItemOnSheet() {
		return firstItemOnSheet;
	}

	public SpreadsheetItem getItem(String cellReference) {
		return data.get(cellReference);
	}

	public List<String> getCellList() {
		return cellList;
	}

	public List<SpreadsheetRow> getRows() {
		return rows;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public SpreadsheetData getParentSheet() {
		return parentSheet;
	}

	public void setParentSheet(SpreadsheetData parentSheet) {
		this.parentSheet = parentSheet;
	}

	public int getKeyColumn() {
		return keyColumn;
	}
}
