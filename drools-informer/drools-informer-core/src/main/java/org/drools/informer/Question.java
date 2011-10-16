/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.informer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * Represents a question to be answered by a user.
 * </p>
 * 
 * <p>
 * <code>Question</code> has an <code>answerType</code> which must be one of:
 * </p>
 * 
 * <ul>
 * <li><code>text</code></li>
 * <li><code>number</code></li>
 * <li><code>decimal</code></li>
 * <li><code>boolean</code></li>
 * <li><code>date</code></li>
 * <li><code>list</code></li>
 * </ul>
 * 
 * <p>
 * or an extension of one of these using the notation <code>&lt;type&gt;.&lt;extension type&gt; </code> e.g. <code>text.url</code>
 * or <code>decimal.currency</code>.
 * </p>
 * 
 * <p>
 * The answer to a <code>Question</code> is maintained internally by the object. use <code>DomainModelAssociation</code> to map
 * the answers to a real domain model.
 * </p>
 * 
 * TODO the get/setListAnswer methods should be using String[] not String for consistency with all the other methods that deal
 * with lists of values (e.g. Group.get/setItems). The list is represented INTERNALLY as a string but that detail should not be
 * exposed outside of this class. Note that the setter method will need to be overloaded though with a String version for use by
 * the Tohu built-in rules because the internal representation is what is sent to the client via XML and so Question.drl needs to
 * handle it when it comes back. The String version of this setter should be marked as "internal use only". See get/setDateAnswer
 * as a comparison as dates are stored internally as strings but the methods expose them as Date. ListAnswer should follow this
 * same pattern.
 * 
 * @author Damon Horrell
 */
public class Question extends Item {

	private static final long serialVersionUID = 1L;

	private static final DateFormat DATE_TRANSPORT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private transient DateFormat dateFormatter = DATE_TRANSPORT_FORMAT;
    protected DateFormat getDateFormatter() {
        return dateFormatter;
    }
    public void setDateFormat(String dateFormat) {
        if (dateFormat != null && dateFormat.length() > 0) {
            this.dateFormatter = new SimpleDateFormat(dateFormat);
        }
    }



    public static enum QuestionType {
        TYPE_TEXT("text"), TYPE_NUMBER("number"), TYPE_DECIMAL("decimal"), TYPE_BOOLEAN("boolean"), TYPE_DATE("date"), TYPE_LIST("list");

        private String value;

        QuestionType(String val) {
            value = val;
        }

        public String getValue() {
            return value;
        }


    }



    private String preLabel;

    private String label;

	private String postLabel;

    private String reason;

	private boolean required;

	private QuestionType answerType;

	@AnswerField
	private String textAnswer;

	@AnswerField
	private Long numberAnswer;


	@AnswerField
	private BigDecimal decimalAnswer;

	@AnswerField
	private Boolean booleanAnswer;


	/**
	 * Dates are stored internally as strings so that they are transported to the client as just yyyy-mm-dd and not with the
	 * redundant time and timezone data on the end.
	 * 
	 * (The Java Date class is really DateTime and is mis-named. If there is ever a need to support TIME or DATETIME in the future
	 * then these should be defined as distinct types.)
	 */
	@AnswerField
	private String dateAnswer;

	/**
	 * List is stored as a delimited string
	 */
	@AnswerField
	private String listAnswer;


    @AnswerField
    private String lastAnswer;


    private boolean finalAnswer = false;



	public Question() {
	}

	public Question(String type) {
		super(type);
	}

	public Question(String type, String label) {
		super(type);
		this.preLabel = label;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isRequired() {
		return required;
	}

	/**
	 * If set to true then the Tohu built-in rules will create an <code>InvalidAnswer</code> if this question is not answered.
	 * 
	 * @param required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}


    public boolean isFinalAnswer() {
        return finalAnswer;
    }

    public void setFinalAnswer(boolean finalAnswer) {
        this.finalAnswer = finalAnswer;
    }




    public QuestionType getAnswerType() {
		return answerType;
	}

	public void setAnswerType(QuestionType answerType) {
		QuestionType previousBasicAnswerType = this.getBasicAnswerType();
		QuestionType basicAnswerType = answerType;
		if (basicAnswerType == null
				|| (!basicAnswerType.equals(QuestionType.TYPE_TEXT) && !basicAnswerType.equals(QuestionType.TYPE_NUMBER)
						&& !basicAnswerType.equals(QuestionType.TYPE_DECIMAL) && !basicAnswerType.equals(QuestionType.TYPE_BOOLEAN)
						&& !basicAnswerType.equals(QuestionType.TYPE_DATE) && !basicAnswerType.equals(QuestionType.TYPE_LIST))) {
			throw new IllegalArgumentException("answerType " + answerType + " is invalid");
		}
		this.answerType = answerType;
		if (!basicAnswerType.equals(previousBasicAnswerType)) {
			clearAnswer();
		}
	}

	/**
	 * Returns the basic answer type.
	 * 
	 * @return
	 */
	public QuestionType getBasicAnswerType() {
		return answerType;
	}

	private QuestionType answerTypeToBasicAnswerType(String answerType) {
		if (answerType == null) {
			return null;
		}
		int i = answerType.indexOf('.');
		if (i >= 0) {
			return QuestionType.valueOf(answerType.substring(0, i));
		}
		return QuestionType.valueOf(answerType);
	}


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLastAnswer() {
        return lastAnswer;
    }

    public void setLastAnswer(String lastAnswer) {
        this.lastAnswer = lastAnswer;
    }

    public String getTextAnswer() {
		checkType(QuestionType.TYPE_TEXT);
		return textAnswer;
	}

	public void setTextAnswer(String textAnswer) {
		checkType(QuestionType.TYPE_TEXT);
		this.textAnswer = textAnswer;
	}

	public Long getNumberAnswer() {
		checkType(QuestionType.TYPE_NUMBER);
		return numberAnswer;
	}

	public void setNumberAnswer(Long numberAnswer) {
		checkType(QuestionType.TYPE_NUMBER);
		this.numberAnswer = numberAnswer;
	}

	public BigDecimal getDecimalAnswer() {
		checkType(QuestionType.TYPE_DECIMAL);
		return decimalAnswer;
	}

	public void setDecimalAnswer(BigDecimal decimalAnswer) {
		checkType(QuestionType.TYPE_DECIMAL);
		this.decimalAnswer = decimalAnswer;
	}

	public Boolean getBooleanAnswer() {
		checkType(QuestionType.TYPE_BOOLEAN);
		return booleanAnswer;
	}

	public void setBooleanAnswer(Boolean booleanAnswer) {
		checkType(QuestionType.TYPE_BOOLEAN);
		this.booleanAnswer = booleanAnswer;
	}

	public Date getDateAnswer() {
		checkType(QuestionType.TYPE_DATE);
		try {
			return dateAnswer == null ? null : getDateFormatter().parse(dateAnswer);
		} catch (ParseException e) {
			// can't actually happen because we formatted the string in the first place
			throw new IllegalStateException();
		}
	}

	public void setDateAnswer(Date dateAnswer) {
		checkType(QuestionType.TYPE_DATE);
		this.dateAnswer = dateAnswer == null ? null : getDateFormatter().format(dateAnswer);
	}

	/**
	 * For internal use only.
	 * 
	 * @param dateAnswer
	 * @throws java.text.ParseException
	 */
	public void setDateAnswer(String dateAnswer) throws ParseException {
		checkType(QuestionType.TYPE_DATE);
		this.dateAnswer = dateAnswer == null ? null : getDateFormatter().format(getDateFormatter().parse(dateAnswer));
	}






	public String getListAnswer() {
		checkType(QuestionType.TYPE_LIST);
		return listAnswer;
	}

	public void setListAnswer(String listAnswer) {
		checkType(QuestionType.TYPE_LIST);
		this.listAnswer = listAnswer;
	}

	public List<String> getAnswerAsList() {
		checkType(QuestionType.TYPE_LIST);
		if (this.listAnswer == null) {
			return new ArrayList<String>();
		}
		return Arrays.asList(split(this.listAnswer, ","));
	}

	public void setAnswer(Object answer) {

		if (answerType == null) {
			throw new IllegalStateException("answerType has not been specified");
		}
        QuestionType basicAnswerType = getBasicAnswerType();

        setLastAnswer( (answer != null) ? answer.toString() : null);

		if (basicAnswerType.equals(QuestionType.TYPE_TEXT)) {
			setTextAnswer((String) answer);
		}
		if (basicAnswerType.equals(QuestionType.TYPE_NUMBER)) {
            if (answer != null) {
			    setNumberAnswer(((Number) answer).longValue());
            } else {
                setNumberAnswer(null);
            }

		}
		if (basicAnswerType.equals(QuestionType.TYPE_DECIMAL)) {
			setDecimalAnswer((BigDecimal) answer);
		}
		if (basicAnswerType.equals(QuestionType.TYPE_BOOLEAN)) {
			setBooleanAnswer((Boolean) answer);
		}
		if (basicAnswerType.equals(QuestionType.TYPE_DATE)) {
			setDateAnswer((Date) answer);
		}
		if (basicAnswerType.equals(QuestionType.TYPE_LIST)) {
			setListAnswer((String) answer);
		}
	}

    public void setAnswer(long l) {
        setNumberAnswer(l);
    }

    public void setAnswer(double d) {
        setDecimalAnswer(new BigDecimal(d));
    }

    public void setAnswer(boolean b) {
         setBooleanAnswer(b);
    }


	public Object getAnswer() {
		if (answerType == null) {
			throw new IllegalStateException("answerType has not been specified");
		}
		QuestionType basicAnswerType = getBasicAnswerType();
		if (basicAnswerType.equals(QuestionType.TYPE_TEXT)) {
			return textAnswer;
		}
		if (basicAnswerType.equals(QuestionType.TYPE_NUMBER)) {
			return numberAnswer;
		}
		if (basicAnswerType.equals(QuestionType.TYPE_DECIMAL)) {
			return decimalAnswer;
		}
		if (basicAnswerType.equals(QuestionType.TYPE_BOOLEAN)) {
			return booleanAnswer;
		}
		if (basicAnswerType.equals(QuestionType.TYPE_DATE)) {
			return getDateAnswer();
		}
		if (basicAnswerType.equals(QuestionType.TYPE_LIST)) {
			return listAnswer;
		}
		throw new IllegalStateException();
	}

	public boolean isAnswered() {
		return getAnswer() != null;
	}



    public void fit(String answerValue, QuestionType basicAnswerType) throws NumberFormatException, ParseException {
		if (answerValue == null) {
			setAnswer(null);
		} else if (basicAnswerType.equals(QuestionType.TYPE_TEXT)) {
			setTextAnswer(answerValue);
		} else if (basicAnswerType.equals(QuestionType.TYPE_NUMBER)) {
			setNumberAnswer(new Long(answerValue));
		} else if (basicAnswerType.equals(QuestionType.TYPE_DECIMAL)) {
			setDecimalAnswer(new BigDecimal(answerValue));
		} else if (basicAnswerType.equals(QuestionType.TYPE_BOOLEAN)) {
            if ( "true".equalsIgnoreCase( answerValue ) || "false".equalsIgnoreCase( answerValue ) ) {
			    setBooleanAnswer(new Boolean(answerValue));
            } else {
                throw new ParseException("Unable to parse " + answerValue + " as boolean" , -1);
            }
		} else if (basicAnswerType.equals(QuestionType.TYPE_DATE)) {
			setDateAnswer(answerValue);
		} else if (basicAnswerType.equals(QuestionType.TYPE_LIST)) {
			setListAnswer(answerValue);
		}

        setLastAnswer(answerValue);
    }


	/**
	 * Checks that the supplied answer type is correct.
	 * 
	 * @param answerType
	 */
	private void checkType(QuestionType answerType) {
		if (this.answerType == null) {
			throw new IllegalStateException("answerType has not been specified");
		}
		QuestionType basicAnswerType = getBasicAnswerType();
		if (!basicAnswerType.equals(answerType)) {
			throw new IllegalStateException("Supplied answer type " + answerType + " differs from the expected type "
					+ basicAnswerType + " for " + getId());
		}
	}

	/**
	 * Clears any previous answer (which may be of a different data type).
	 */
	private void clearAnswer() {
		textAnswer = null;
		numberAnswer = null;
		decimalAnswer = null;
		booleanAnswer = null;
		dateAnswer = null;
		listAnswer = null;
	}

	/**
	 * Splits some text into words delimited by the specified delimiter. Make public for use within rule logic.
	 * 
	 * Occurrences of the delimiter d within the text are expected to be escaped as \d
	 * 
	 * @param text
	 * @param delimiter
	 * @return
	 */
	public String[] split(String text, String delimiter) {
		List<String> result = new ArrayList<String>();
		String[] split = text.split(delimiter, -1);
		for (int i = 0; i < split.length; i++) {
		}
		int i = 0;
		String s = "";
		while (i < split.length) {
			boolean continues = split[i].endsWith("\\");
			if (continues) {
				s += split[i].substring(0, split[i].length() - 1) + delimiter;
			} else {
				s += split[i];
				result.add(s);
				s = "";
			}
			i++;
		}
		return result.toArray(new String[] {});
	}

    @Override
    public String toString() {
        return "Question{" +
                "preLabel='" + preLabel + '\'' +
                ", label='" + label + '\'' +
                ", postLabel='" + postLabel + '\'' +
                ", required=" + required +
                ", answerType=" + answerType +
                ", lastAnswer=" + lastAnswer +
                ", textAnswer='" + textAnswer + '\'' +
                ", numberAnswer=" + numberAnswer +
                ", decimalAnswer=" + decimalAnswer +
                ", booleanAnswer=" + booleanAnswer +
                ", dateAnswer='" + dateAnswer + '\'' +
                ", listAnswer='" + listAnswer + '\'' +
                "} " + super.toString();
    }

    /**
	 * Annotation used by the ChangeCollector to identify answer fields.
	 */
	@Retention(RUNTIME)
	@Target( { FIELD })
	public @interface AnswerField {
	}










}
