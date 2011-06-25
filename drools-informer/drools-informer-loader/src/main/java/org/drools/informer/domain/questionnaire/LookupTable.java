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
package org.drools.informer.domain.questionnaire;

import java.util.ArrayList;
import java.util.List;

import org.drools.informer.domain.questionnaire.conditions.ConditionClause;
import org.drools.informer.domain.questionnaire.framework.ListEntryTuple;

/**
 * An object that holds the (Value and Representation) pairs for a lookup table loaded from the spreadsheet.
 * 
 * The pairs are held in a {@link ListEntryTuple} object that allows for a single logic line to be
 * associated with an entry.
 * 
 * @author Derek Rendall
 */
public class LookupTable {

	private static final long serialVersionUID = 1L;
	public static final int TYPE_STRING = 0;
	public static final int TYPE_NUMBER = 1;
	
	private int type = 0;
	
	private String id;
	private List<ListEntryTuple> entries = new ArrayList<ListEntryTuple>();
	
	public LookupTable(String id) {
		super();
		this.id = id;
	}
	
	public void addEntry(String entryId) {
		entries.add(new ListEntryTuple(entryId));
	}

	public void addEntry(String entryId, String representation) {
		entries.add(new ListEntryTuple(entryId, representation));
	}

	public void addEntry(String entryId, ConditionClause clause) {
		entries.add(new ListEntryTuple(entryId, clause));
	}

	public void addEntry(String entryId, String representation, ConditionClause clause) {
		entries.add(new ListEntryTuple(entryId, representation, clause));
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public List<ListEntryTuple> getEntries() {
		return entries;
	}
	
	public void setEntries(List<ListEntryTuple> entries) {
		this.entries = entries;
	}
}
