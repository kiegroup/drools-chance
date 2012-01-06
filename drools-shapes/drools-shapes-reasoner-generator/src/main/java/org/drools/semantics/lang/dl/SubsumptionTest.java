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
public class SubsumptionTest implements DLTest {

	private String superClass;
	private String subClass;
	private boolean tau = true;
	
	private MinimizationProblem problem;

	public SubsumptionTest(String subClass, String superClass, boolean tau) {
		super();
		this.problem = new MinimizationProblem();
		this.superClass = superClass;
		this.subClass = subClass;
		this.tau = tau;		
	}
	
	public SubsumptionTest(SubsumptionGoal goal) {
		super();
		this.problem = new MinimizationProblem();
		this.superClass = goal.getSuperClass();
		this.subClass = goal.getSubClass();
		this.tau = goal.isTau();
	}
	
	
	
	
	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}
	public String getSuperClass() {
		return superClass;
	}
	public void setSubClass(String subClass) {
		this.subClass = subClass;
	}
	public String getSubClass() {
		return subClass;
	}
	
	

	public void setTau(boolean tau) {
		this.tau = tau;
	}


	public boolean isTau() {
		return tau;
	}


	public void setProblem(MinimizationProblem problem) {
		this.problem = problem;
	}


	public MinimizationProblem getProblem() {
		return problem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((subClass == null) ? 0 : subClass.hashCode());
		result = prime * result
				+ ((superClass == null) ? 0 : superClass.hashCode());
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
		SubsumptionTest other = (SubsumptionTest) obj;
		if (subClass == null) {
			if (other.subClass != null)
				return false;
		} else if (!subClass.equals(other.subClass))
			return false;
		if (superClass == null) {
			if (other.superClass != null)
				return false;
		} else if (!superClass.equals(other.superClass))
			return false;
		if (tau != other.tau)
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "SubsumptionTest{" +
                "superClass='" + superClass + '\'' +
                ", subClass='" + subClass + '\'' +
                ", tau=" + tau +
                '}';
    }
}
