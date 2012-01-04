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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.drools.informer.load.spreadsheet.SpreadsheetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the data for the workbook, split into the constituent sheets. 
 * 
 * @author Derek Rendall
 */
public class WorkbookData {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkbookData.class);
	
	private HashMap<String, SpreadsheetData>data = new HashMap<String, SpreadsheetData>();
	private List<String> sheetList = new ArrayList<String>(20);
	
	
	/**
	 * Open and load the workbook sheets. Note: any sheet with an "!" in the name will be ignored.
	 * 
	 * @param filename
	 * @return
	 */
	public boolean loadWorkbook(String filename) {
		try {
			logger.debug("\n\n\nPROCESSING FILE: " + filename);
			InputStream inp = new FileInputStream(filename);
			HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				HSSFSheet sheet = wb.getSheetAt(i);
				SpreadsheetData sheetData = new SpreadsheetData(sheet);
				String sheetName = sheet.getSheetName();
				if (sheetName.indexOf("!") >= 0) {
					logger.debug("Ignoring sheet named: " + sheetName);
					continue;
				}
				data.put(sheetName, sheetData);
				sheetList.add(sheetName);
			}
			inp.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Cater for finding a current element, by spreadsheet reference.
	 * 
	 * Handles references to alternative sheets in the workbook.
	 * 
	 * @param currentSheet
	 * @param cellReference
	 * @return
	 */
	public SpreadsheetItem getItem(String currentSheet, String cellReference) {
		int pos = cellReference.indexOf('!');
		String tempSheet = currentSheet;
		if (pos > 0) {
			tempSheet = cellReference.substring(0, pos);
		}
		else {
			cellReference = tempSheet + "!" + cellReference;
		}
		return data.get(tempSheet).getItem(cellReference);
	}

	public List<String> getSheetCellList(String sheet) {
		return data.get(sheet).getCellList();
	}
	
	public List<String> getSheetList() {
		return sheetList;
	}
	
	/**
	 * Will be needed for formula processing
	 * 
	 * <b> not currently implemented </b>
	 * 
	 * @param currentSheet
	 * @param cellReference
	 * @return
	 */
	public static List<String> convertCellRangeReferencesToExplicitCellReferences(String currentSheet, String cellReference) {
		throw new UnsupportedOperationException("convertCellRangeReferencesToExplicitCellReferences not yet supported");
	}

	public SpreadsheetData getSheet(String sheetName) {
		return data.get(sheetName);
	}
}
