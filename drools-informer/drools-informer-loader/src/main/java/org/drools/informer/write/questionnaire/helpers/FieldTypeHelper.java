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
package org.drools.informer.write.questionnaire.helpers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.informer.domain.questionnaire.framework.ConditionConstants;
import org.drools.informer.domain.questionnaire.framework.PageElementConstants;

/**
 * Bunch of utility methods to map Spreadsheet types and values to Tohu/Java types
 * and values.
 * 
 * @author Derek Rendall
 */
public class FieldTypeHelper implements PageElementConstants, ConditionConstants {

	private static final Logger logger = LoggerFactory.getLogger(FieldTypeHelper.class);
	
	/**
	 * If operation has quotes, remove quotes and then do no further mapping.
	 * If is "is", map to "=="
	 * If is "is not" map to "!="
	 * 
	 * @param op
	 * @return
	 * 			mapped/formatted operation string.
	 */
	public static String formatOperationString(String op) {
		if (op.startsWith("\"") && op.endsWith("\"") && (op.length() > 2)) {
			op = op.substring(1, op.length() - 2);
		}
		else {				
			if (op.toUpperCase().startsWith(OP_NOOP_UPPER)) {
				op = "";
			}
			else if (op.toUpperCase().startsWith(OP_IS_NOT_UPPER)) {
				op = "!=";
			}
			else if (op.toUpperCase().startsWith(OP_IS_UPPER)) {
				op = "==";
			}
		}
		return op;
	}

	
	/**
	 * Null or "empty" values get transformed to "null" string
	 * If there is a dot in the string, simply return it (is likely to be a method accessor)
	 * If the string refers to a previously used item variable, then replace with the "[item id].answer" string.
	 * 
	 * @param itemVariables
	 * @param valueString
	 * @return
	 */
	public static String formatValueStringInLogic(Map<String, String> itemVariables, String valueString) {
		if ((valueString == null) || valueString.toUpperCase().equals(VALUE_EMPTY_UPPER)) {
			return "null";
		}
		if (valueString.indexOf(".") > 0) {
			return valueString;
		}
		String varName = itemVariables.get(valueString);
		if (varName != null) {
			valueString = String.format("%s.answer", varName);
		}
		else if (!valueString.startsWith("\"")) {
			valueString = "\"" + valueString + "\"";
		}
		return valueString;
	}
    
	/**
	 * Add string, long and double decorations to the value.
	 * 
	 * For example, 0.0 when it is a double will return 0.0D
	 * 
	 * Note: for multiple default values for a field type of list, the value will need to be a
	 * single string with "||" separators between the values.
	 * 
	 * @param tempStr
	 * @param type
	 * @return
	 */
	public static String formatValueStringAccordingToType(String tempStr, String type) {
		if (tempStr == null) {
			return null;
		}
    	if (type.equals(FIELD_TYPE_TEXT) || type.equals(FIELD_TYPE_DATE) || type.equals(FIELD_TYPE_LIST)) {
    		if (!tempStr.startsWith("\"")) {
    			tempStr = "\"" + tempStr + "\"";
	    	}
    	}
    	else if (type.equals(FIELD_TYPE_NUMBER)){
    		if (!tempStr.endsWith("L")) {
    			tempStr = tempStr + "L";
	    	}
    	}
    	else if (type.equals(FIELD_TYPE_DECIMAL)){
    		if (!tempStr.endsWith("D")) {
    			tempStr = tempStr + "D";
	    	}
    	}
    	return tempStr;
	}

	/**
	 * Input the Questionnaire type (e.g. Text) and get back the Tohu type (e.g. Question.TYPE_TEXT).
	 * 
	 * @param theFieldType
	 * @return
	 */
	public static String mapFieldTypeToQuestionType(String theFieldType) {
		if ((theFieldType == null) || (theFieldType.equals(FIELD_TYPE_TEXT))) {
			return TYPE_TEXT;
		}
		
		if (theFieldType.equals(FIELD_TYPE_BOOLEAN)) {
			return TYPE_BOOLEAN;
		}
		
		if (theFieldType.equals(FIELD_TYPE_NUMBER)) {
			return TYPE_NUMBER;
		}
		
		if (theFieldType.equals(FIELD_TYPE_DECIMAL)) {
			return TYPE_DECIMAL;
		}
		
		if (theFieldType.equals(FIELD_TYPE_DATE)) {
			return TYPE_DATE;
		}
		
		if (theFieldType.equals(FIELD_TYPE_LIST)) {
			return TYPE_LIST;
		}
		
		logger.debug("Converting type: " + theFieldType + " to Text");
		return TYPE_TEXT;
	}
	
	/**
	 * If expecting a Number, then use the numberAnswer accessor to the Question or TohuDataItemObject
	 * 
	 * @param theFieldType
	 * @return
	 */
	public static String mapFieldTypeToBaseVariableName(String theFieldType) {
		if ((theFieldType == null) || (theFieldType.equals(FIELD_TYPE_TEXT))) {
			return "textAnswer";
		}
		
		if (theFieldType.equals(FIELD_TYPE_NUMBER)) {
			return "numberAnswer";
		}
		if (theFieldType.equals(FIELD_TYPE_DECIMAL)) {
			return "decimalAnswer";
		}
		if (theFieldType.equals(FIELD_TYPE_BOOLEAN)) {
			return "booleanAnswer";
		}
		if (theFieldType.equals(FIELD_TYPE_DATE)) {
			return "dateAnswer";
		}
		
		logger.debug("Converting type: " + theFieldType + " to Text");
		return "textAnswer";
	}
	
	/**
	 * Used in accumulator functions when need to create a temporary object of the right data type
	 * to assign to the global TohuDataItemObject
	 * 
	 * @param theFieldType
	 * @return
	 */
	public static String mapFieldTypeToJavaClassName(String theFieldType) {
		if ((theFieldType == null) || (theFieldType.equals(FIELD_TYPE_TEXT))) {
			return "String";
		}
		
		if (theFieldType.equals(FIELD_TYPE_NUMBER)) {
			return "Long";
		}
		if (theFieldType.equals(FIELD_TYPE_DECIMAL)) {
			return "Double";
		}
		if (theFieldType.equals(FIELD_TYPE_BOOLEAN)) {
			return "Boolean";
		}
		if (theFieldType.equals(FIELD_TYPE_DATE)) {
			return "Date";
		}
		
		logger.debug("Converting type : " + theFieldType + " to Java Class String");
		return "String";
	}
	
	/**
	 * For example, if type is Number then return longValue()
	 * 
	 * @param theFieldType
	 * @return
	 */
	public static String mapFieldTypeToJavaNumberClassMethodName(String theFieldType) {
		if ((theFieldType == null) || (theFieldType.equals(FIELD_TYPE_TEXT))) {
			return "toString()";
		}
		
		if (theFieldType.equals(FIELD_TYPE_NUMBER)) {
			return "longValue()";
		}
		if (theFieldType.equals(FIELD_TYPE_DECIMAL)) {
			return "doubleValue()";
		}
		
		logger.debug("Converting number method type: " + theFieldType + " to Text");
		return "toString()";
	}
}
