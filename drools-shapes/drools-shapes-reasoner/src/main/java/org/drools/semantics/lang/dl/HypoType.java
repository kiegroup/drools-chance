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

package org.drools.semantics.lang.dl;

@Deprecated
public class HypoType  implements Cloneable {

	private Object subject;
	private String object;
	private boolean negated;
	private DLTest goal;
	
	public HypoType(Object subject, String object, boolean neg, DLTest goal) {
		this.subject = subject;
		this.object = object;
		this.negated = neg;
		this.setGoal(goal);
	}

	public void setGoal(DLTest goal) {
		this.goal = goal;
	}

	public DLTest getGoal() {
		return goal;
	}

	
	@Override
	public String toString() {
		return (isNegated() ? "--" : "") + getLabel();
	}
	
	
	
	public String getLabel() {
		return  getSubject()+":"+getObject();
	}
	
	
	

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	public boolean isNegated() {
		return negated;
	}

	

	public HypoType clone() {		
		return new HypoType(this.getSubject(), this.getObject(), this.isNegated(), this.getGoal());
	}

	
	public String toFullString() {
		return "HypoType [goal=" + goal + ", negated=" + negated
				+ ", getObject()=" + getObject() + ", getSubject()="
				+ getSubject() + "]";
	}

	public Object getSubject() {
		return subject;
	}

	public void setSubject(Object subject) {
		this.subject = subject;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((goal == null) ? 0 : goal.hashCode());
		result = prime * result + (negated ? 1231 : 1237);
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HypoType other = (HypoType) obj;
		if (goal == null) {
			if (other.goal != null)
				return false;
		} else if (!goal.equals(other.goal))
			return false;
		if (negated != other.negated)
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	
	
	
}
