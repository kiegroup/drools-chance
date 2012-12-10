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
public class RecognitionTest implements DLTest {

	private Object subject;
	private String targetClass;
	private boolean tau = true;
	
	private MinimizationProblem problem;
	
	

	public RecognitionTest(Object subject, String tgtClass, boolean tau) {
		super();
		this.problem = new MinimizationProblem();
		this.setSubject(subject);
		this.targetClass = tgtClass;		
		this.tau = tau;
	}
	
	public RecognitionTest(Object subject, RecognitionGoal goal) {
		super();
		this.problem = new MinimizationProblem();
		this.setSubject(subject);
		this.setTargetClass(goal.getTargetClass());		
		this.tau = goal.isTau();
	}
	
	
	
	
	
	

	public void setTau(boolean tau) {
		this.tau = tau;
	}


	public boolean isTau() {
		return tau;
	}

	public String getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}





	public void setSubject(Object subject) {
		this.subject = subject;
	}





	public Object getSubject() {
		return subject;
	}

	@Override
	public String toString() {
		return "RecognitionTest [subject=" + subject + ", targetClass="
				+ targetClass + ", tau=" + tau + "]";
	}

	public MinimizationProblem getProblem() {
		return problem;
	}

	public void setProblem(MinimizationProblem problem) {
		this.problem = problem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result
				+ ((targetClass == null) ? 0 : targetClass.hashCode());
		result = prime * result + (tau ? 1231 : 1237);
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
		RecognitionTest other = (RecognitionTest) obj;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (targetClass == null) {
			if (other.targetClass != null)
				return false;
		} else if (!targetClass.equals(other.targetClass))
			return false;
		if (tau != other.tau)
			return false;
		return true;
	}
	
}
