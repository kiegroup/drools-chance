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

import org.apache.poi.hssf.util.CellReference;

/**
 * Utility class to enable a standard way of referring to a cell. 
 * 
 * @author Derek Rendall
 */
public class CellIdentifier implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int rowIdentifier;
	private int columnIdentifier;
	private CellReference cellRef;
	
	public CellIdentifier(String sheetName, int row, int column) {
		super();
		this.rowIdentifier = row;
		this.columnIdentifier = column;
		this.cellRef = new CellReference(sheetName, row, column, false, false);
	}
	
	public int getRowIdentifier() {
		return rowIdentifier;
	}

	public int getColumnIdentifier() {
		return columnIdentifier;
	}


	public String getCellIdentifier() {
		return cellRef.formatAsString();
	}
	
	public String toString() {
		return cellRef.toString();
	}
}
