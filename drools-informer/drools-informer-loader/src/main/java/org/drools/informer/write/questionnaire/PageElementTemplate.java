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
import java.util.*;


import org.drools.informer.util.TemplateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.informer.Answer;
import org.drools.informer.domain.questionnaire.Application;
import org.drools.informer.domain.questionnaire.Page;
import org.drools.informer.domain.questionnaire.PageElement;
import org.drools.informer.domain.questionnaire.framework.ConditionConstants;
import org.drools.informer.domain.questionnaire.framework.ListEntryTuple;
import org.drools.informer.domain.questionnaire.framework.PageElementConstants;
import org.drools.informer.write.questionnaire.helpers.FieldTypeHelper;

/**
 * Write out an {@link PageElement} object to the {@link Page} drl file.
 * 
 * See {@link PageElement} for details on the types written.
 * 
 * Conditional display is propagated by each item having (at least) a condition that
 * their parent group is displayed.
 * 
 * If a Group has items that do not exist they are ignored. This removed the need for explicit
 * insert/remove code for page elements and other group items. One exception is for Branch pages
 * where there needs to be additional logic to control the jump off to a new set of pages. Note: the page's group
 * is enabled via the same logic as the rule that creates a new navigationBranch. This means that the group, and the
 * associated data, can exist after the branch is returned from. The navigationBranch does not
 * require any special "return" logic as that is handled automatically by Tohu framework.
 * 
 * Additional logic is also written for Functional and Alternate Impact (see {@link }) elements.
 * A functional impact is actually like a global fact that uses an accumulate function (sum, average, etc) to
 * provide the value.
 * 
 * An alternate function can have multiple elements, each one setting a mutually exclusive value. This means that the
 * actual object needs to be created (once) and each element simply updates the value. Note: the first
 * element for the Alternate Impact is used to specify the default value. 
 * 
 * @author Derek Rendall
 */
public class PageElementTemplate implements PageElementConstants, ConditionConstants {
	
	private static final Logger logger = LoggerFactory.getLogger(PageElementTemplate.class);
	
	protected PageElement element;
	
	public PageElementTemplate(PageElement element) {
		super();
		this.element = element;
	}
		
	
	/**
	 * Impact objects are actually {@link } objects.
	 * 
	 * @param itemId
	 * @return
	 */
	protected String checkType(String itemId) {
		if (itemId.equals(ITEM_TYPE_NORMAL_IMPACT)) {
			return ITEM_TYPE_DATA_ITEM;
		}
		return itemId;
	}
	
		
	/**
	 * Accumulate functions handled:
	 * <ul>
	 * <li><code>max</code></li>
	 * <li><code>min</code></li>
	 * <li><code>sum</code></li>
	 * <li><code>average</code></li>
	 * <li><code>count</code></li>
	 * </ul>
	 * 
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	protected void writeFunctionalImpact(Application application, TemplateManager tm, Formatter fmt)  {
		// TODO replace Logic Element with PageElementCondition
		if ((element.getLogicElement() == null)) {
			throw new IllegalArgumentException("You cannot have an empty logical element for a functional impact!");
		}
		
		String functionName = null;
		String opName = element.getLogicElement().getOperation();
		if (opName == null) {
			throw new IllegalArgumentException("You cannot have an empty logical operation for a functional impact!");
		}
		
		if (opName.equalsIgnoreCase(FUNCTION_MAX)) {
			functionName = FUNCTION_MAX;
		}
		else if (opName.equalsIgnoreCase(FUNCTION_MIN)) {
			functionName = FUNCTION_MIN;
		}
		else if (opName.equalsIgnoreCase(FUNCTION_AVERAGE)) {
			functionName = FUNCTION_AVERAGE;
		}
		else if (opName.equalsIgnoreCase(FUNCTION_SUM)) {
			functionName = FUNCTION_SUM;
		}
		else if (opName.equalsIgnoreCase(FUNCTION_COUNT)) {
			functionName = FUNCTION_COUNT;
		}
		if (functionName == null) {
			throw new IllegalArgumentException("Invalid operation " + opName + " for a functional impact!");
		}
		
		writeCreationOfAGlobalImpact(application, tm, fmt);
		
		String tempFactName = "Temp" + element.getId();
	    fmt.format("declare %s\n", tempFactName);
	    fmt.format("\tnumber : Number\n");
	    fmt.format("end\n\n");

	    fmt.format("rule \"Function %s\"\nno-loop\n", element.getId());
	    fmt.format("when\n");
	    fmt.format("\t$total : Number()\n");
	    fmt.format("\t\tfrom accumulate (%s(%s == \"%s\", answered == true, $value : %s),\n %s ( $value ) )\n", 
	    		checkType(element.getLogicElement().getItemId()),
	    		element.getLogicElement().getItemAttribute(),
	    		element.getLogicElement().getValue(),
	    		FieldTypeHelper.mapFieldTypeToBaseVariableName(element.getFieldType()),
	    		functionName);
	    fmt.format("then\n");
	    fmt.format("\t%s temp = new %s();\n", tempFactName, tempFactName);
	    fmt.format("\ttemp.setNumber($total);\n");
	    fmt.format("\tinsert(temp);\n");
	    fmt.format("end\n\n");
		
	    fmt.format("rule \"Assign %s\"\nno-loop\n", element.getId());
	    fmt.format("when\n");
	    fmt.format("\t$impact : %s(id == \"%s\");\n", ITEM_TYPE_DATA_ITEM, element.getId());
	    fmt.format("\t$v : %s();\n", tempFactName);
	    fmt.format("then\n");
	    fmt.format("\t$impact.setAnswer(new %s($v.getNumber().%s));\n", 
	    		FieldTypeHelper.mapFieldTypeToJavaClassName(element.getFieldType()), 
	    		FieldTypeHelper.mapFieldTypeToJavaNumberClassMethodName(element.getFieldType()));
	    fmt.format("\tretract($v);\n");
	    fmt.format("\tupdate($impact);\n");
	    fmt.format("end\n\n");		
	}





	/**
	 * Write the common attribute setting code for a Tohu related fact
	 * 
	 * @param application
	 * @param fmt
	 * @param showReason
	 * @return
	 * @throws IOException
	 */
	protected String writeCommonFactCreationCode(Application application, TemplateManager tm, Formatter fmt, boolean showReason)  {
        String varName = "a" + element.getType();

        HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("varName", varName);
            map.put("ansType",FieldTypeHelper.mapFieldTypeToQuestionType(element.getFieldType()));
            map.put("ansValue",FieldTypeHelper.formatValueStringAccordingToType(element.getDefaultValueStr(), element.getFieldType()));
            map.put("showReason",showReason);
            map.put("method",(element.isAQuestionType()) ? "PreLabel" : (element.isAnImpactType()) ? "Name" : "Label");

	    tm.applyTemplate("commonFactCreation.drlt",element,map,fmt);

        return varName;
	}




	/**
	 * Insert an Impact (a {@link })
	 * 
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	protected void writeCreationOfAGlobalImpact(Application application, TemplateManager tm, Formatter fmt)  {
        ByteArrayOutputStream subBaos = new ByteArrayOutputStream();
        Formatter slave = new Formatter(subBaos);

        String variableName = writeCommonFactCreationCode(application, tm, slave, false);
        HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("varName",variableName);
            map.put("body",new String(subBaos.toByteArray()));

	    tm.applyTemplate("globalImpact.drlt",element,map,fmt);
	}





	/**
	 * Conditional rule to logically insert an InvalidAnswer object attached to the previous question.
	 * 
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	public void writeValidationDRL(Application application, TemplateManager tm, Formatter fmt)  {
        String body;
        String message;
        PageElement baseQuestion;

        body = new WhenClauseTemplate(element).writeLogicSectionDRL(application, false);
        message = element.getPreLabel();
	    element.setPreLabel(null);
	    if (message == null) {
			message = "Invalid value";
			logger.debug("Warning - validation " + element.getId() + " has no validation message defined.");
		}

        baseQuestion = element.findPreviousQuestion();
	    if (baseQuestion == null) {
			throw new IllegalStateException("Validation " + element.getId() + " has no previous question to attach to.");
		}

        HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("body",body);
            map.put("message",message);
            map.put("baseId",baseQuestion.getId());


	    tm.applyTemplate("validation.drlt",element,map,fmt);

	}
	    




	/**
	 * For a group or a multiple choice question, write out the items.
	 * 
	 * If it is a multiple choice question, then list entries that have conditional logic are NOT written here.
	 * 
	 * Note: it is valid to have no list specified for a Multiple List Question, if the list is being set 
	 * by other means (such as via logic in one of the include files loaded into the Questionnaire drl).
	 * 
	 * @param application
	 * @param fmt
	 * @param possibleAnswers
	 * @throws IOException
	 */
	protected void writeSubItems(Application application, TemplateManager tm, Formatter fmt, String variableName, boolean possibleAnswers)  {
		if (possibleAnswers && ((element.getLookupTable() == null) || (element.getLookupTable().getEntries().size() == 0))) {
			// This is ok for a Lookup Object
			logger.debug("No entries for " + element.getId());
			return;
		}
		List<ListEntryTuple> entries = null;
		if (possibleAnswers) {
			entries = element.getLookupTable().getEntries();
		}
		else {
			if ((element.getChildren() == null) || (element.getChildren().size() == 0)) {
				throw new IllegalStateException("No children for group " + element.getId());
			}
			entries = new ArrayList<ListEntryTuple>();
			for (Iterator<PageElement> i = element.getChildren().iterator(); i.hasNext();) {
				PageElement e = (PageElement) i.next();
				if (e.isARepeatingElement()) {
					PageElement temp = application.findPageElement(e.getId());
					if (temp == null) {
						throw new IllegalArgumentException("A repeating element has no master element for id " + e.getId());
					}
					e = temp;
				}
				if (e.isAGroupType() || e.isAQuestionType() || e.isANoteType()) {
					entries.add(new ListEntryTuple(e.getId()));
				}
			}
			if (entries.size() == 0) {
				throw new IllegalStateException("No group, note or question children for group " + element.getId());
			}
		}

        HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("possibleAnswers",possibleAnswers);
            map.put("entries",entries);
            map.put("varName",variableName);

	    tm.applyTemplate("subItems.drlt",element,map,fmt);

	}






	/**
	 * In order to action a branch, the logic looks for an actual {@link Answer} object associated with the 
	 * Question. This only exists straight after the question value has changed, thus can be used to initiate the
	 * branch. For the Branch page's group, use the question's answer attribute as this will remain accessible
	 * after the Answer object has gone away.
	 * 
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	protected void writeInitiateBranchPageDRL(Application application, TemplateManager tm, Formatter fmt)  {
		if (!element.isABranchedPage())
            throw new IllegalArgumentException("Cannot process a normal page in writeInitiateBranchPageDRLFileContents :" + element.getId());

        String premise = new WhenClauseTemplate(element).writeLogicSectionDRL(application, true);

        HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("premise",premise);
            map.put("displayAfter", element.getPostLabel());

        element.setPostLabel(null);

	    tm.applyTemplate("branching.drlt",element,map,fmt);


	}






	/**
	 * The entry point for writing the element to file
	 * 
	 * @param application
	 * @param fmt
	 * @throws IOException
	 */
	public void compileContentsToDRL(Application application, TemplateManager tm, Formatter fmt) {
	    if (element.isARepeatingElement()) {
	    	logger.debug("Repeating item: " + element.getId());
    		// should have already been defined - don't want it defined again - just
    		// referred to again in the parent element, which should have already been done
    		return;
	    }
	    
	    if (element.isAFunctionImpactItem()) {
	    	logger.debug("Functional Impact");
	    	writeFunctionalImpact(application, tm, fmt);
	    	return;
	    }
	    
	    if (element.isAnAlternateImpactItem()) {
	    	logger.debug("Alternate Impact");
	    	if (application.addNewAlternateImpact(element.getId())) writeCreationOfAGlobalImpact(application, tm, fmt);
	    }
	    else if (element.isAnImpactType() && (element.getLogicElement() == null)) {
	    	writeCreationOfAGlobalImpact(application, tm, fmt);
	    	return;
	    }
	    else if (element.isAValidationElement()) {
	    	writeValidationDRL(application, tm, fmt);
	    	return;
	    }
	    
	    if (element.isABranchedPage()) {
	    	writeInitiateBranchPageDRL(application, tm, fmt);
	    }











	    String ruleName = element.getId();
	    if (element.isAnAlternateImpactItem()) {
	    	ruleName = ruleName + String.valueOf(element.getRowNumber());
	    }
	    
	    fmt.format("rule \"%s\"\ndialect \"mvel\"\nno-loop\n", ruleName);
	    
	    boolean useGroupIds = ((element.getGroupIds() != null) && (element.getGroupIds().size() > 0)) && (element.isAQuestionType() || !element.isRequired());
	    if (useGroupIds || (element.getDisplayCondition() != null)) {
		    fmt.format("when\n");
		    if (useGroupIds) {
			    fmt.format("\t$group : Group (");
		    	for (Iterator<String> i = element.getGroupIds().iterator(); i.hasNext();) {
					String id = (String) i.next();
			    	if (!id.startsWith("\"")) {
			    		id = "\"" + id + "\"";
			    	}
				    fmt.format("id == %s%s", id, i.hasNext() ? " || " : "");
				}
			    fmt.format(");\n");
		    }

		    if (element.getDisplayCondition() != null) {
		    	new WhenClauseTemplate(element).writeLogicSectionDRL(application, false);
		    }
	    }
	    

	    if (element.isAnAlternateImpactItem()) {
		    fmt.format("\taDataItem : %s (id == \"%s\")\n", ITEM_TYPE_DATA_ITEM, element.getId());
		    fmt.format("then\n");
	    	String tempStr = FieldTypeHelper.formatValueStringAccordingToType(element.getDefaultValueStr(), element.getFieldType());
		    fmt.format("\taDataItem.setAnswer(%s);\n", tempStr);
		    fmt.format("\tupdate(aDataItem);\n");
		}
	    else {
		    fmt.format("then\n");
	    
		    String variableName = writeCommonFactCreationCode(application, tm, fmt, true);
		    
		    if (!element.isAnImpactType()) {
			    if (element.getPostLabel() != null) {
				    fmt.format("\t%s.setPostLabel(\"%s\");\n", variableName, element.getPostLabel());
			    }
			    if ((element.getType().equals(ITEM_TYPE_MULTI_CHOICE_Q)) || (element.getType().equals(ITEM_TYPE_GROUP))) {
				    writeSubItems(application, tm, fmt, variableName, element.getType().equals(ITEM_TYPE_MULTI_CHOICE_Q));
			    }
			    if (!element.getStyles().isEmpty()) {
					boolean firstOne = true;
					boolean onlyOne = element.getStyles().size() == 1;
					String indent = (onlyOne) ? "" : "\t\t";
					String newLine = (onlyOne) ? "" : "\n";
				    fmt.format("\t%s.setPresentationStyles({", variableName);
					for (Iterator<String> i = element.getStyles().iterator(); i.hasNext();) {
						String style = (String) i.next();
						if (firstOne) {
							firstOne = false;
							fmt.format("%s", newLine);
						}
						else {
							fmt.format(",%s", newLine);
						}
						if ((style != null) && (!style.startsWith("\""))) {
							style = "\"" + style + "\"";
						}
						
						fmt.format("%s%s", indent, style);
					}
				    fmt.format("});\n");
				}
		    }
		    
		    fmt.format("\tinsertLogical(%s);\n", variableName);
	    }
	    
	    fmt.format("end\n\n");
	}
}
