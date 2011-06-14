package org.drools.chance.degree.simple;

import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.interval.IntervalDegree;


/**
 * Class that implements the concept of degree using a simple double value.
 * Useful for many semantics (probability, possibility, many-valued truth, confidence, belief, ...)
 */
public class SimpleDegree implements IDegree {

	private static final double EPSILON = 1e-12;

	public static final IDegree TRUE = new SimpleDegree(1);
	public static final IDegree FALSE = new SimpleDegree(0);


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


	public IDegree True() {
		return TRUE;
	}

	public IDegree False() {
		return FALSE;
	}

	/**
	 * Under the Closed World Assumption, Unknown = False
	 */
	public IDegree Unknown() {
		return FALSE;
	}



    public IDegree sum(IDegree sum) {
        double ret = Math.min( 1.0, this.getValue() + sum.getValue() );
        return new SimpleDegree(ret);
    }

    public IDegree mul(IDegree mul) {
        return new SimpleDegree( getValue()*mul.getValue() );
    }

    public IDegree div(IDegree div) {
        if (div.getValue() == 0) return Unknown();
        return new SimpleDegree( Math.min(1.0, getValue()/div.getValue()) );
    }

    public IDegree sub(IDegree sub) {
        double ret = Math.max( 0.0, this.getValue() - sub.getValue() );

        return new SimpleDegree(ret);
    }

    public IDegree max(IDegree comp) {
        return new SimpleDegree( Math.max( this.getValue(), comp.getValue()) );
    }

    public IDegree min(IDegree comp) {
        return new SimpleDegree( Math.min(this.getValue(), comp.getValue()) );
    }

    public IDegree fromConst(double number) {
       return new SimpleDegree(number);
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


	public int compareTo(IDegree arg0) {
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
