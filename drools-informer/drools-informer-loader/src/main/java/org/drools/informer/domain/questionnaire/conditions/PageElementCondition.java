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
package org.drools.informer.domain.questionnaire.conditions;

import java.util.ArrayList;
import java.util.List;

import org.drools.informer.domain.questionnaire.framework.ConditionConstants;

/**
 * Holds the condition clauses for a Validation item or for hide/display Group/Question/Note.
 *
 * Multiple lines exist to provide "and" behavior. In order to get or type behavior, the recommendation is to
 * use the existence of an Impact (Question) as the controller, and then have multiple cases where that
 * object can be created. If the Impact uses a Category then multiple different objects with the same Category can be used.
 * Alternatively, a set of AlternativeImpact elements (with the same id) could be used. Preferrably the former.
 * 
 * @author Derek Rendall
 */
public class PageElementCondition implements ConditionConstants, Cloneable {

	public static final String TYPE_VALIDATION = "Validation";
	public static final String TYPE_INCLUSION = "Inclusion";
	
	
	protected String id;
	protected String idPrefix;
	protected List<ConditionClause> elements = new ArrayList<ConditionClause>();
	protected int rowNumber;
	protected String type;
	private String pageName;
	private boolean branchedPage;
	private String displayAfter;

	
	/**
	 * Use for non-page stuff.
	 * 
	 * @param type
	 * @param id
	 * @param rowNumber
	 */
	public PageElementCondition(String type, String id, int rowNumber) {
		this(type, id, rowNumber, null, false, null);
	}
	
	/**
	 * Use for Page and Branch conditions.
	 * 
	 * @param sheetName
	 * @param id
	 * @param rowNumber
	 * @param pageName
	 * @param branchedPage
	 * @param displayAfter
	 */
	public PageElementCondition(String sheetName, String id, int rowNumber, String pageName, boolean branchedPage, String displayAfter) {
		super();
		id = id.trim();
		if (id.indexOf(" ") >= 0) {
			throw new IllegalArgumentException("You cannot have a space in an id [" + id + "]");
		}
		if (type == null) {
			type = TYPE_INCLUSION;
		}
		this.id = id;
		this.idPrefix = type + "_" + String.valueOf(rowNumber);
		this.rowNumber = rowNumber;
		this.pageName = pageName;
		this.branchedPage = branchedPage;
		this.displayAfter = displayAfter;
	}
	

	/**
	  * The clone will be reset to be a non-branched display fact.
	  * This means that cloning an array of display facts for a branched page
	  * will get us the display facts for the data display group that we 
	  * automatically create.
	  * 
	  * @see java.lang.Object#clone()
	  */
	public Object clone () throws CloneNotSupportedException {
		PageElementCondition newFact = (PageElementCondition)super.clone();
		 pageName = null;
		 branchedPage = false;
		 displayAfter = null;
		 return newFact;
    }	
	 

	public String getId() {
		return id;
	}
	
	protected void setId(String id) {
		this.id = id;
	}

	public void addElement(ConditionClause element) {
		elements.add(element);
	}
		
	public int getRowNumber() {
		return rowNumber;
	}

	public String getIdPrefix() {
		return idPrefix;
	}

	public List<ConditionClause> getElements() {
		return elements;
	}

	public String getType() {
		return type;
	}

	public String getPageName() {
		return pageName;
	}

	public boolean isBranchedPage() {
		return branchedPage;
	}

	public String getDisplayAfter() {
		return displayAfter;
	}
	
	

}
