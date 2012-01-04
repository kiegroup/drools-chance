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
package org.drools.informer.domain.questionnaire;

import java.util.ArrayList;
import java.util.List;

import org.drools.informer.Group;
import org.drools.informer.MultipleChoiceQuestion;
import org.drools.informer.Note;
import org.drools.informer.Question;
import org.drools.informer.InvalidAnswer;
import org.drools.informer.domain.questionnaire.conditions.ConditionClause;
import org.drools.informer.domain.questionnaire.conditions.PageElementCondition;
import org.drools.informer.domain.questionnaire.framework.PageElementConstants;

/**
 * The key domain object holding the details for Tohu related objects such as 
 * <ul>
 * <li>{@link Group}</li>
 * <li>{@link Question}</li>
 * <li>{@link MultipleChoiceQuestion}</li>
 * <li>{@link Note}</li>
 * <li>{@link Question}</li>
 * </ul>
 * 
 * The Spreadsheet Types are 
 * <ul>
 * <li>Group</li>
 * <li>Question</li>
 * <li>MultipleChoiceQuestion</li>
 * <li>Note</li>
 * <li>Page (maps to {@link Group})</li>
 * <li>Branch (maps to {@link Group})</li>
 * <li>Impact (maps to {@link Question})</li>
 * <li>FunctionalImpact (maps to combination of accumulate function and {@link Question})</li>
 * <li>AlternateImpact (maps to {@link Question})</li>
 * <li>Validation (generates {@link InvalidAnswer})</li>
 * <li>Reuse (does a lookup on the original element by id - will be repeated in the UI)</li>
 * </ul>
 * 
 * Each element can have a {@link PageElementCondition} which holds one or more 
 * {@link ConditionClause} entries that describe logic elements that control show/hide or validation logic.
 * 
 * The elements are linked in a tree structure (parent->child), which is used to control
 * groupings for layout etc. Also, each child knows what it's older sibling is and/or its parent.
 * This can then be used by Validation entries to know what to Question to associate the InvalidAnswer with.
 * 
 * Although the one <object holds the data for all the Tohu objects, the writing of the DRL file will need to translate
 * method names (for example, using "name" instead of "preLabel" when relating to a Question instead of an Item).
 * 
 * @author Derek Rendall
 */
public class PageElement implements PageElementConstants {

	private String id;
	private List<String> groupIds;
	/** What Object type to create as a Tohu rule object - will be mapped in the setter */
	private String type;
	private String lookupTableId;
	private boolean required;
	private String fieldType;
	/** For Question this will be the name, for Validation this will be the Message */
	private String preLabel;
	/** For pages, this will be the insertAfter page value, for Question this will be the reason */
	private String postLabel;
	private String defaultValueStr;
	private List<String> styles = new ArrayList<String>();
	private LookupTable lookupTable;
	private boolean aPageElement;
	private String pageType;
	/** Spreadsheet Type holds what the original type was. The Type will be mapped to the Tohu object when the setter is called */
	private String spreadsheetType;
	private PageElementCondition displayCondition;
	private PageElementCondition currentValidationCondition;
	/** category applies for Question and Item, and can be used to select a group of objects, especially useful for calculations */
	private String category;
	/** Depth is used to control nesting of elements */
	private int depth;
	/** Row number provides a useful rule identification variation to avoid duplicate names in DRL file */
	private int rowNumber;
	
	/** 
	 * Working value - especially for lines where only has the logic element, in which case the 
	 * logic will be inserted into the previous element, and this element will be "thrown away".
	 */
	private ConditionClause logicElement;
	
	protected List<PageElement> children = new ArrayList<PageElement>();
	protected PageElement parent;
	protected PageElement previousSibling;
	
	
	public PageElement() {
		super();
	}

	/**
	 * @param id
	 * 			Cannot have a space in the value
	 * @param depth
	 * @param rowNumber
	 */
	public void setId(String id, int depth, int rowNumber) {
		id = id.trim();
		if (id.indexOf(" ") >= 0) {
			throw new IllegalArgumentException("You cannot have a space in an id [" + id + "]");
		}
		this.id = id;
		this.depth = depth;
		this.rowNumber = rowNumber;
	}

	public PageElement getPreviousSibling() {
		return previousSibling;
	}

	public void setPreviousSibling(PageElement previousSibling) {
		this.previousSibling = previousSibling;
	}

	public boolean isAFunctionImpactItem() {
		return (spreadsheetType != null) && (spreadsheetType.equalsIgnoreCase(ITEM_TYPE_FUNCTION_IMPACT));
	}

	public boolean isAnAlternateImpactItem() {
		return (spreadsheetType != null) && (spreadsheetType.equalsIgnoreCase(ITEM_TYPE_ALTERNATE_IMPACT));
	}

	public boolean isAQuestionType() {
		if (getType().equals(ITEM_TYPE_QUESTION) || (getType().equals(ITEM_TYPE_MULTI_CHOICE_Q))) {
			return true;
		}
		return false;
	}
	
	public String getSpreadsheetType() {
		return spreadsheetType;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	/**
	 * @param type
	 * 			Will be stored in spreadsheetType, and then cast to the right Tohu object type
	 * 			For example, a Page will be turned into a Group.
	 */
	public void setType(String type) {
		String tempType = type.toUpperCase();
		spreadsheetType = type;
		if (tempType.equals("PAGE")) {
			aPageElement = true;
			this.type = "Group";
			pageType = "Normal";
		}
		else if (tempType.equals("BRANCH")) {
			aPageElement = true;
			this.type = "Group";
			pageType = "Branch";
		}
		else if (tempType.equalsIgnoreCase(ITEM_TYPE_NORMAL_IMPACT) || tempType.equalsIgnoreCase(ITEM_TYPE_FUNCTION_IMPACT) || tempType.equalsIgnoreCase(ITEM_TYPE_ALTERNATE_IMPACT)) {
			this.type = ITEM_TYPE_DATA_ITEM;
		}
		else {
			this.type = type;
		}
	}

	public PageElementCondition getDisplayCondition() {
		return displayCondition;
	}

	public void setDisplayCondition(PageElementCondition displayCondition) {
		this.displayCondition = displayCondition;
	}

	public boolean isAGroupType() {
		if (getType().equals(ITEM_TYPE_GROUP)) {
			return true;
		}
		return false;
	}
	
	public boolean isANoteType() {
		if (getType().equals(ITEM_TYPE_NOTE)) {
			return true;
		}
		return false;
	}
	
	public boolean isAnImpactType() {
		if (getType().equals(ITEM_TYPE_DATA_ITEM)) {
			return true;
		}
		return false;
	}
	
	public boolean isAPageElement() {
		return aPageElement;
	}
	
	public boolean isABranchedPage() {
		return aPageElement && pageType.equals("Branch");
	}

	public int getDepth() {
		return depth;
	}

	public String getPageType() {
		return pageType;
	}
	
	/**
	 * Will traverse previousSiblings (the norm) and then parent looking for a Question (or subclass).
	 * Used by the validation elements to associate an InvalidAnswer with.
	 * 
	 * @return
	 */
	public PageElement findPreviousQuestion() {
		if (getPreviousSibling() != null) {
			if (getPreviousSibling().isAQuestionType()) {
				return getPreviousSibling();
			}
			return getPreviousSibling().findPreviousQuestion();
		}
		if (getParent() != null) {
			if (getParent().isAQuestionType()) {
				return getParent();
			}
			return getParent().findPreviousQuestion();
		}
		return null;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<PageElement> getChildren() {
		return children;
	}
	

	public String getLookupTableId() {
		return lookupTableId;
	}

	public void setLookupTableId(String lookupTableId) {
		this.lookupTableId = lookupTableId;
	}

	/**
	 * Will add the child, set the child's previous sibling (if any) and the child's parent.
	 * 
	 * @param child
	 */
	public void addChild(PageElement child) {
		//System.out.println("Adding child " + child.getId() + child.getDepth() + " to " + getId() + getDepth());
		if (children.size() > 0) {
			child.setPreviousSibling(children.get(children.size() - 1));
		}
		this.children.add(child);
		child.setParent(this);
	}

	protected void setParent(PageElement parent) {
		this.parent = parent;
	}

	public PageElement getParent() {
		return parent;
	}

	public LookupTable getLookupTable() {
		return lookupTable;
	}

	public void setLookupTable(LookupTable lookupTable) {
		this.lookupTable = lookupTable;
	}

	public boolean isRequired() {
		return required;
	}

	public String getDefaultValueStr() {
		return defaultValueStr;
	}

	public List<String> getStyles() {
		return styles;
	}

	public PageElementCondition getCurrentValidationCondition() {
		return currentValidationCondition;
	}

	public ConditionClause getLogicElement() {
		return logicElement;
	}
	
	public void setLogicElement(ConditionClause substituteLogicElement) {
		if (logicElement != null) {
			throw new IllegalArgumentException("You cannot replace the logic element");
		}
		logicElement = substituteLogicElement;
	}
	
	public void setLogicDependsOnItemId(String value) {
		if (logicElement == null) {
			logicElement = new ConditionClause();
		}
		logicElement.setItemId(value);
	}

	public void setLogicAttribute(String value) {
		if (logicElement == null) {
			logicElement = new ConditionClause();
		}
		logicElement.setItemAttribute(value);
	}

	public void setLogicOperation(String value) {
		if (logicElement == null) {
			logicElement = new ConditionClause();
		}
		logicElement.setOperation(value);
	}

	public void setLogicValue(String value) {
		if (logicElement == null) {
			logicElement = new ConditionClause();
		}
		logicElement.setValue(value);
	}

	public void addGroupId(String groupId) {
		if (groupIds == null) {
			groupIds = new ArrayList<String>();
		}
		if (groupIds.contains(groupId)) {
			return;
		}
		groupIds.add(groupId);
	}

	public String getPreLabel() {
		return preLabel;
	}

	public void setPreLabel(String preLabel) {
		this.preLabel = preLabel;
	}

	public String getPostLabel() {
		return postLabel;
	}

	public void setPostLabel(String postLabel) {
		this.postLabel = postLabel;
	}

	public boolean isARepeatingElement() {
		if ((getType() == null) || (!getType().equals(ITEM_TYPE_REUSE))) {
			return false;
		}
		return true;
	}

	public boolean isAValidationElement() {
		if ((getType() == null) || (!getType().equals(ITEM_TYPE_VALIDATION))) {
			return false;
		}
		return true;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public void setRequired(String requiredStr) {
		if ((requiredStr != null) && (requiredStr.toUpperCase().startsWith("Y"))) {
			this.required = true;
		}
		else {
			this.required = false;
		}
	}

	public void setDefaultValueStr(String defaultValueStr) {
		this.defaultValueStr = defaultValueStr;
	}
	
	public void addStyle(String style) {
		styles.add(style);
	}
}
