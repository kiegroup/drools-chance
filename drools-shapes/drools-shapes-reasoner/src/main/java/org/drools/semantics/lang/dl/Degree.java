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

import choco.kernel.model.variables.real.RealVariable;

@Deprecated
public class Degree implements Cloneable {

	private Object subject = null;
	private Double tau = null;
	private Double phi = null;
	private RealVariable value = null;
	
	
	
	public Degree(Object subject, Double tau, Double phi, RealVariable value) {
		super();
		this.subject = subject;
		this.tau = tau;
		this.phi = phi;
		this.value = value;
	}
	
	public Degree(Object subject, Double tau, Double phi) {
		this(subject,tau,phi,null);
	}
	
	public Degree(Object subject, Double tau) {
		this(subject,tau,1.0,null);
	}
	
	public Degree(Object subject, RealVariable value) {
		this(subject,null,null,value);
	}
	
	
	public Object getSubject() {
		return subject;
	}
	public void setSubject(Object subject) {
		this.subject = subject;
	}
	public Double getTau() {
		return tau;
	}
	public void setTau(Double tau) {
		this.tau = tau;
	}
	public Double getPhi() {
		return phi;
	}
	public void setPhi(Double phi) {
		this.phi = phi;
	}
	public RealVariable getValue() {
		return value;
	}
	public void setValue(RealVariable value) {
		this.value = value;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phi == null) ? 0 : phi.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((tau == null) ? 0 : tau.hashCode());
		result = prime * result + ((value == null) ? 0 : value.getName().hashCode());
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
		Degree other = (Degree) obj;
		if (phi == null) {
			if (other.phi != null)
				return false;
		} else if (!phi.equals(other.phi))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (tau == null) {
			if (other.tau != null)
				return false;
		} else if (!tau.equals(other.tau))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.getName().equals(other.value.getName()))
			return false;
		return true;
	}


	@Override
	public String toString() {
		if (value != null) {
			return value+"_"+subject;
		} else {
			return tau + " <= " + subject + " <= " + phi;
		}
	}
	


	public Degree clone() {
		return new Degree(this.getSubject(), this.getTau(), this.getPhi(), this.getValue());
	}
}
