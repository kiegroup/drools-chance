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

package org.drools.chance.degree.simple;

import org.drools.chance.degree.Degree;
import org.drools.chance.degree.interval.IntervalDegree;


/**
 * Class that implements the concept of degree using a simple double value.
 * Useful for many semantics (probability, possibility, many-valued truth, confidence, belief, ...)
 */
public class SimpleDegree implements Degree {

	private static final double EPSILON = 1e-12;

	public static final Degree TRUE = new SimpleDegree(1);
	public static final Degree FALSE = new SimpleDegree(0);


	private double value;

	/**
	 * @param value the degree to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the degree
	 */
	public double getValue() {
		return value;
	}







	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleDegree other = (SimpleDegree) obj;
        return compareTo(other) == 0;
    }


	public Degree True() {
		return TRUE;
	}

	public Degree False() {
		return FALSE;
	}

	/**
	 * Under the Closed World Assumption, Unknown = False
	 */
	public Degree Unknown() {
		return FALSE;
	}



    public Degree sum(Degree sum) {
        double ret = Math.min( 1.0, this.getValue() + sum.getValue() );
        return new SimpleDegree(ret);
    }

    public Degree mul(Degree mul) {
        return new SimpleDegree( getValue()*mul.getValue() );
    }

    public Degree div(Degree div) {
        if (div.getValue() == 0) return Unknown();
        return new SimpleDegree( Math.min(1.0, getValue()/div.getValue()) );
    }

    public Degree sub(Degree sub) {
        double ret = Math.max( 0.0, this.getValue() - sub.getValue() );

        return new SimpleDegree(ret);
    }

    public Degree max(Degree comp) {
        return new SimpleDegree( Math.max( this.getValue(), comp.getValue()) );
    }

    public Degree min(Degree comp) {
        return new SimpleDegree( Math.min(this.getValue(), comp.getValue()) );
    }

    public Degree fromConst(double number) {
       return new SimpleDegree(number);
    }

    public Degree fromString(String number) {
       return new SimpleDegree( Double.parseDouble( number ) );
    }


    public SimpleDegree(double degree) {
        if (degree < 0 || degree > 1)
            throw new IllegalArgumentException(degree + "is not a valid SimpleDegree");
		this.value = degree;
	}

    public SimpleDegree(String degree) {
        this(new Double(degree));
	}



	public boolean toBoolean() {
		return value > 0;
	}

	public SimpleDegree asSimpleDegree() {
		return this;
	}


	public String toString() {
		return ""+value;
	}


	public double getConfidence() {
		return 1.0;
	}


	public int compareTo(Degree arg0) {
		if (arg0 == null) throw new NullPointerException("Comparing a SimpleDegree to a null Degree");
        double delta = value - arg0.getValue() ;

		if (delta > EPSILON)
			return 1;
		else if (Math.abs(delta) < EPSILON)
		    return 0;
		else return -1;

	}

	public IntervalDegree asIntervalDegree() {
		return new IntervalDegree(value,value);
	}

}
