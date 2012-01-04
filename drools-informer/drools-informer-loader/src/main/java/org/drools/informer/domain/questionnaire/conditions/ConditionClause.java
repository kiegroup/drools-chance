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
package org.drools.informer.domain.questionnaire.conditions;

/**
 * Represents one line of the condition clauses in the spreadsheet.
 *
 * @author Derek Rendall
 */
public class ConditionClause {
	
	protected String itemId;
	protected String itemAttribute;
	protected String operation;
	protected String value;
	protected String explanation;
	private boolean processed;
	
	public ConditionClause() {
		super();
	}
	
	public ConditionClause(String itemId, String itemAttribute, String operation, String value) {
		super();
		setItemId(itemId);
		setItemAttribute(itemAttribute);
		setOperation(operation);
		setValue(value);
	}

	public String getOperation() {
		return operation;
	}

	public String getItemId() {
		return itemId;
	}

	public String getItemAttribute() {
		return itemAttribute;
	}

	public String getValue() {
		return value;
	}

	public void setItemId(String itemId) {
		this.itemId = (itemId == null) ? null : itemId.trim();
		if (this.itemId.indexOf(" ") >= 0) {
			throw new IllegalArgumentException("You cannot have a space in an id [" + this.itemId + "]");
		}

	}

	public void setItemAttribute(String itemAttribute) {
		this.itemAttribute = (itemAttribute == null) ? null : itemAttribute.trim();
	}

	public void setOperation(String operation) {
		this.operation = (operation == null) ? null : operation.trim();
	}

	public void setValue(String value) {
		this.value = (value == null) ? null : value.trim();
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	public String toString() {
		return "Item id: " + itemId + " attribute: " + itemAttribute + " operation: " + operation + " value: " + value;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}


}
