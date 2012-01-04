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
package org.drools.informer.write.questionnaire;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.informer.domain.questionnaire.Application;
import org.drools.informer.domain.questionnaire.PageElement;
import org.drools.informer.domain.questionnaire.conditions.ConditionClause;
import org.drools.informer.domain.questionnaire.conditions.PageElementCondition;
import org.drools.informer.domain.questionnaire.framework.ConditionConstants;
import org.drools.informer.write.questionnaire.helpers.FieldTypeHelper;

/**
 * Writes out the condition clauses for an element.
 * 
 * While all values are "allowed", there are some key mappings:
 * <ul>
 * <li>Operation <code>is</code> maps to <code>==</code></li>
 * <li>Operation <code>is not</code> maps to <code>!=</code></li>
 * <li>Item Attribute <code>answer</code> will add in a condition for <code>Answer</code> object existence for Branch objects</li>
 * <li>Operation <code>eval</code>will treat the Item Id as an eval command (adding outside <code>eval()</code> if required</li>
 * <li>Value <code>empty</code> is mapped to null</li>
 * <li>Quoted strings are not usually mapped</li>
 * <li>Quotes are usually added to values</li>
 * </ul>
 * 
 * 
 * @author Derek Rendall
 */
public class WhenClauseTemplate implements ConditionConstants {
	
	private PageElementCondition condition;
	private PageElement element;

	public WhenClauseTemplate(PageElement element) {
		super();
		this.element = element;
		this.condition = element.getDisplayCondition();
	}
	
    
	public String writeLogicSectionDRL(Application application, boolean splitQuestionValuesIntoQuestionAndAnswer)  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Formatter fmt = new Formatter(baos);

        PageElement lastItem = null;

		int count = 0;
		boolean alreadySplitAnswerOut = false;
		
		if (condition == null) {
			throw new IllegalStateException("There is no display condition for " + element.getId() + " " + (element.getRowNumber() + 1));
		}
		if (condition.getElements() == null) {
			throw new IllegalStateException("There is no display condition elements for " + element.getId() + " " + (element.getRowNumber() + 1));
		}

		Map<String, String> itemVariables = new HashMap<String, String>();
		
		for (Iterator<ConditionClause> i = condition.getElements().iterator(); i.hasNext();) {
			ConditionClause element = (ConditionClause) i.next();
			String seperator = ", ";
			count++;
			
			// TODO handle referenced objects? ie "emailObject : email"
			String itemId = element.getItemId();
			String attributeName = element.getItemAttribute();
			String valueString = element.getValue();
			String op = element.getOperation();
			
			if (op == null) {
				op = "==";
			}
			boolean sameItem = ((lastItem == null) || (!lastItem.getId().equals(itemId))) ? false : true;
			
			if (!sameItem) {
				if (lastItem != null) {
					lastItem = null;
					fmt.format(");\n");
				}
			}
			
			PageElement pgElement = (sameItem) ? lastItem : application.findPageElement(itemId);
			//System.out.println("itemId = " + itemId +" pgElement = " + pgElement);
			
			if (op.toUpperCase().startsWith(OP_IS_CHANGED_TO_UPPER)) {
				if (!alreadySplitAnswerOut) splitQuestionValuesIntoQuestionAndAnswer = true;
				op = OP_IS_UPPER;
			}
			else if (op.toUpperCase().startsWith(OP_IS_CHANGED_FROM_UPPER)) {
				if (!alreadySplitAnswerOut) splitQuestionValuesIntoQuestionAndAnswer = true;
				op = OP_IS_NOT_UPPER;
			}
						
			if (pgElement != null) {
				if (lastItem == null) {
					String varName = String.format("pe%d", count);
					itemVariables.put(itemId, varName);
					if (!itemId.startsWith("\"")) {
						itemId = "\"" + itemId + "\"";
					}
					fmt.format("\t%s : %s(id == %s ", varName, pgElement.getType(), itemId);
					lastItem = pgElement;
				}
				
				if (op.toUpperCase().startsWith(OP_MAPPED_TO_VALUE_UPPER)) {
					fmt.format("%s%s : %s", seperator, attributeName, valueString);
					continue;
				}
			}
			else {
				if (op.toUpperCase().startsWith(OP_EVAL_UPPER)) {
					if (!itemId.startsWith("eval")) {
						itemId = "eval(" + itemId + ")";
					}
					fmt.format("\t%s %s;\n", itemId, (valueString == null) ? "" : valueString);
					continue;
				}
				else {
					fmt.format("\t%s",itemId);
				}
			}
			
			if (attributeName.equals("answer") && splitQuestionValuesIntoQuestionAndAnswer) {
				fmt.format(" );\n\tAnswer (questionId == pe%d.id ", count);
				attributeName = "value";
				// only want one action to generate the branching action
				splitQuestionValuesIntoQuestionAndAnswer = false;
				alreadySplitAnswerOut = true;
			}

			op = FieldTypeHelper.formatOperationString(op);
			
			valueString = FieldTypeHelper.formatValueStringInLogic(itemVariables, valueString);

			fmt.format("%s%s %s %s", seperator, attributeName, op, valueString);
			if (lastItem == null) {
				fmt.format("\n");
			}
		}
		
		// clean up at end
		if (lastItem != null) {
			lastItem = null;
			fmt.format(");\n");
		}

        return new String(baos.toByteArray());
	}
}
