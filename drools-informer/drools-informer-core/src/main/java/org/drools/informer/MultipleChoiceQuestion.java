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
package org.drools.informer;


import org.mvel2.templates.util.io.StringBuilderStream;

import javax.swing.text.Position;
import java.util.*;

/**
 * <p>
 * An extension of <code>Question</code> which provides a list of possible answers. i.e. a multiple choice question.</code>
 * </p>
 * 
 * <p>
 * <code>presentationStyles</code> could be used to display the possible answers as e.g. radio buttons, or a drop down list.
 * </p>
 * 
 * @author Damon Horrell
 */
public class MultipleChoiceQuestion extends Question {

	private static final long serialVersionUID = 1L;

	/**
	 * Possible answers are represented internally as comma-delimited value/label pairs i.e. value1=label1,value2=label2,... for efficient XML transport.
	 * 
	 * Any commas within the labels are escaped to \,
	 * 
	 * Any equals sign within either the values or labels are escaped to \=
	 */
	private List<PossibleAnswer> possibleAnswers;


    private boolean singleAnswer;


	public MultipleChoiceQuestion() {
	}

	public MultipleChoiceQuestion(String type) {
		super(type);
	}

	public MultipleChoiceQuestion(String type, String label) {
		super(type, label);
	}



	
	protected List<PossibleAnswer> getListOfPossibleAnswers() {
//		List<PossibleAnswer> result = new ArrayList<PossibleAnswer>();
//		String[] split = split(possibleAnswers, ",");
//		for (int i = 0; i < split.length; i++) {
//			String s = split[i];
//			String[] valueLabel = split(s, "=");
//			String value = valueLabel[0];
//			if (value.equals("null")) {
//				value = null;
//			}
//			String label = valueLabel[1];
//			if (label.equals("")) {
//				label = null;
//			}
//			result.add(new PossibleAnswer(value, label));
//		}
//		return result;
        return possibleAnswers;
	}

    public int getNumOfPossibleAnswers() {
        if (possibleAnswers == null) {
            return 0;
        } else {
            return getListOfPossibleAnswers().size();
        }
    }

	/**
	 * Gets list of possible answers.
	 * 
	 * @return
	 */
	public PossibleAnswer[] getPossibleAnswers() {
		if (possibleAnswers == null) {
			return null;
		}
//		List<PossibleAnswer> result = getListOfPossibleAnswers();
		return possibleAnswers.toArray(new PossibleAnswer[] {});
	}
	
	protected String formatValue(String valueStr) {
		if (valueStr != null) {
			if (valueStr.contains(",")) {
				throw new IllegalArgumentException();
			}
			valueStr = valueStr.replace("=", "\\\\=");
		}
		return valueStr;
	}


    protected String outcCodeValue(String valueStr) {
		if (valueStr != null) {
			valueStr = valueStr.replace(",","\\,");
			valueStr = valueStr.replace("=", "\\=");
		}
		return valueStr;
	}



	/**
	 * Sets list of possible answers.
	 * 
	 * @param possibleAnswers
	 */
	public void setPossibleAnswers(PossibleAnswer[] possibleAnswers) {
		if (possibleAnswers == null || possibleAnswers.length == 0) {
			this.possibleAnswers = null;
		} else {
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < possibleAnswers.length; i++) {
//				if (possibleAnswers[i] != null) {
//					if (sb.length() > 0) {
//						sb.append(",");
//					}
//					String value = formatValue(possibleAnswers[i].value);
//					sb.append(value);
//					sb.append('=');
//					if (possibleAnswers[i].label != null) {
//						sb.append(possibleAnswers[i].label.replaceAll(",", "\\\\,").replaceAll("=", "\\\\="));
//					}
//				}
//			}
//			if (sb.length() > 0) {
//				this.possibleAnswers = sb.toString();
//			} else {
//				this.possibleAnswers = null;
//			}
            this.possibleAnswers = new ArrayList<PossibleAnswer>();
            for (int j = 0; j < possibleAnswers.length; j++) {
                PossibleAnswer pa = possibleAnswers[j];
                if (pa != null) {
                    if (pa.getValue() != null && pa.getValue().contains(",")) {
                        throw new IllegalArgumentException("Possible Answers with comma in values are not allowed :" + pa.getValue());
                    }
                    this.possibleAnswers.add(pa);
                }
            }
		}
	}

	/**
	 * Sets list of possible answers.
	 * 
	 * This method is provided to support the MVEL syntax in rules e.g.
	 * 
	 * <pre>
	 * question.setPossibleAnswers({
	 *   new PossibleAnswer(&quot;a&quot;, &quot;apple&quot;),
	 *   new PossibleAnswer(&quot;b&quot;, &quot;banana&quot;)
	 * });
	 * </pre>
	 * 
	 * @param possibleAnswers
	 */
	public void setPossibleAnswers(Object[] possibleAnswers) {
		if (possibleAnswers == null) {
			this.possibleAnswers = null;
		} else {
			setPossibleAnswers( Arrays.asList(possibleAnswers).toArray(new PossibleAnswer[] {}));
		}
	}


    public void setPossibleAnswersByValue(Collection<String> possibleAnswers) {
		if (possibleAnswers == null) {
			this.possibleAnswers = null;
		} else {
			this.possibleAnswers = new ArrayList<PossibleAnswer>();
            for ( String s : possibleAnswers ) {
                if ( s != null ) {
                    if ( s.contains(",") ) {
                        throw new IllegalArgumentException("Possible Answers with comma in values are not allowed :" + s);
                    }
                    this.possibleAnswers.add( new PossibleAnswer( s, s ) );
                }
            }
		}
	}


    public void setPossibleAnswersByValue(String[] possibleAnswers) {
		if (possibleAnswers == null) {
			this.possibleAnswers = null;
		} else {
			this.possibleAnswers = new ArrayList<PossibleAnswer>(possibleAnswers.length);
            for (String pas : possibleAnswers) {
                this.addPossibleAnswer(pas);
            }
		}
	}

    private void addPossibleAnswer(String value) {
        StringTokenizer st = new StringTokenizer(value,"=");
        String val = st.nextToken().trim().replace("\"","");
        String label = st.hasMoreTokens() ? st.nextToken().trim().replace("\"","") : val;
        PossibleAnswer pa = new PossibleAnswer(val,label);
        insertPossibleAnswer(pa,possibleAnswers.size());
    }

    /**
	 * Adds a possible answer.
	 * 
	 * This method is provided to support the dynamic alteration of possible answers.
	 * 
	 * <b>Do not use for creation of lists</b>. Instead use {@link #setPossibleAnswers(PossibleAnswer[])}
	 *
	 *
	 * @param theValue of the possibleAnswer
	 */
	public void removePossibleAnswer(String theValue) {
		PossibleAnswer pos = null;
		for (PossibleAnswer pa : possibleAnswers) {
			if ((pa.getValue() != null) && (pa.getValue().equals(theValue))) {
				pos = pa;
				break;
			}
		}
		if (pos != null) {
			if ((getAnswerType() != null) && (getAnswer() != null) && (getAnswer().equals(pos.getValue()))) {
				setAnswer(null);
			}
			possibleAnswers.remove(pos);
		}
	}

	/**
	 * Checks to see if there is a possible answer with the value passed in.
	 *
	 * This method is provided to support the dynamic alteration of possible answers.
	 * Uses String.indexOf internally.
	 *
	 * @param theValue of the possibleAnswer
	 * @return
	 */
	public boolean hasPossibleAnswer(String theValue) {
		PossibleAnswer mock = new PossibleAnswer(theValue,null);
        return possibleAnswers.contains(mock);
	}


	/**
	 * Removes a possible answer.
	 *
	 * This method is provided to support the dynamic alteration of possible answers.
	 *
	 * <b>Do not use for creation of lists</b>. Instead use {@link #setPossibleAnswers(PossibleAnswer[])}
	 *
	 *
	 * @param possibleAnswer
	 * @param atIndex If >= size of array then the answer is added to the end
	 */
	public void insertPossibleAnswer(PossibleAnswer possibleAnswer, int atIndex) {
		if (possibleAnswers == null) {
			// Really should be discouraged from doing this! Least efficient way of building up the list.
//			PossibleAnswer[] pa = new PossibleAnswer[1];
//			pa[0] = possibleAnswer;
//			setPossibleAnswers(pa);
            possibleAnswers = new ArrayList<PossibleAnswer>();
            possibleAnswers.add(possibleAnswer);
			return;
		}
		if (atIndex < 0) {
			atIndex = 0;
		}

		if (possibleAnswers.size() <= atIndex) {
			possibleAnswers.add(possibleAnswer);
		} else {
			possibleAnswers.add(atIndex, possibleAnswer);
		}
	}

	/**
	 * Gets list of possible answers as a comma delimited string.
	 *
	 * TODO this method can be removed if Guvnor can support array of custom classes. Even just String[] would allow {"a=apple",
	 * "b=banana"} which is slightly better.
	 *
	 * @return
	 * @deprecated
	 */
	public String getPossibleAnswersAsString() {
        if (possibleAnswers == null || possibleAnswers.size() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Iterator<PossibleAnswer> iter = possibleAnswers.iterator();
        while (iter.hasNext()) {
            PossibleAnswer pa = iter.next();
            sb.append(outcCodeValue(pa.getValue())).append("=").append(outcCodeValue(pa.getLabel()));
            if (iter.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
	}

    public String[] getPossibleAnswersValues() {
        if (possibleAnswers == null || possibleAnswers.size() == 0) {
            return new String[0];
        }

        String[] ans = new String[ possibleAnswers.size() ];
        for ( int j = 0; j < possibleAnswers.size(); j++ ) {
            ans[j] = possibleAnswers.get( j ).getValue();
        }
        return ans;
    }

    public List<String> getPossibleAnswersValuesAsList() {
        if (possibleAnswers == null || possibleAnswers.size() == 0) {
            return Collections.emptyList();
        }

        ArrayList<String> ans = new ArrayList<String>( possibleAnswers.size() );
        for ( int j = 0; j < possibleAnswers.size(); j++ ) {
            ans.add ( possibleAnswers.get( j ).getValue() );
        }
        return ans;
    }

	/**
	 * Gets list of item ids as a comma delimited string. Implemented for testing purpose only - package visibility
	 *
	 * @return
	 */
	String getInternalPossibleAnswersAsString() {
		return getPossibleAnswersAsString();
	}

	/**
	 * Sets list of possible answers as a comma-delimited string.
	 *
	 * TODO this method can be removed if Guvnor can support array of custom classes. Even just String[] would allow {"a=apple",
	 * "b=banana"} which is slightly better.
	 *
	 * @param possibleAnswers
	 * @deprecated
	 */
	public void setPossibleAnswersAsString(String possibleAnswers) {
		if (possibleAnswers == null || possibleAnswers.equals("")) {
			this.possibleAnswers = null;
            return;
		}

        this.possibleAnswers = new ArrayList<PossibleAnswer>();
        possibleAnswers = possibleAnswers.replace("\\,","\\\\\\;");
        possibleAnswers = possibleAnswers.replace("\\=","\\\\\\-");

        StringTokenizer tok = new StringTokenizer(possibleAnswers,",");
        while (tok.hasMoreTokens()) {
            String pa = tok.nextToken().replace("\\\\\\;",",");;

            StringTokenizer sub = new StringTokenizer(pa,"=");
                String value = sub.nextToken().replace("\\\\\\-","=");
                if ("null".equals(value)) {
                    value = null;
                }
                String label = sub.hasMoreTokens() ? sub.nextToken().replace("\\\\\\-","=") : "";

            PossibleAnswer ans = new PossibleAnswer(value,label);
            this.possibleAnswers.add(ans);
        }

	}


    public boolean isSingleAnswer() {
        return singleAnswer;
    }

    public void setSingleAnswer(boolean singleAnswer) {
        this.singleAnswer = singleAnswer;
    }


    public String[] getAnswerItems() {
		return split(String.valueOf(getAnswer()), ",");
    }

    /**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return "Multiple Choice " + super.toString() + " possibleAnswers=" + possibleAnswers;
	}

	public static class PossibleAnswer {

		private String value;

		private String label;

		public PossibleAnswer() {
		}

		public PossibleAnswer(String value) {
			this.value = value;
            this.label = "";
		}

		public PossibleAnswer(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PossibleAnswer that = (PossibleAnswer) o;

            if (value == null && that.value == null) {
                return label != null && label.equals(that.getLabel());
            }
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        /**
		 * @see Object#toString()
		 */
		@Override
		public String toString() {
			return value + "=" + label;
		}

	}
}
