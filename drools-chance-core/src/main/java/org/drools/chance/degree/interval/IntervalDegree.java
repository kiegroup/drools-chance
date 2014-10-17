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

package org.drools.chance.degree.interval;

import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;


/**
 * Class that implements an uncertain degree, specified using an interval [low,upp].
 * Thus, supports a simple form of second-order imperfection.
 *
 * Still experimental
 */
public class IntervalDegree implements Degree {

    private static final double EPSILON = 1e-6;

	private double tau;
	private double phi;

	public static final Degree TRUE = new IntervalDegree(1,1);
	public static final Degree FALSE = new IntervalDegree(0,0);
	public static final Degree UNKNOWN = new IntervalDegree(0,1);



    public IntervalDegree() { }

	public IntervalDegree(double low, double upp) {
		this.setTau(low);
		this.setPhi(1.0 - upp);
	}



	public SimpleDegree asSimpleDegree() {
		return new SimpleDegree(getTau());
	}


	public double getValue() {
		return getTau();
	}

    public void setValue(double d) {
        setTau( d );
        setPhi( 1.0 - d );
    }

    // coherent with simpleDegree, under CWA
	public boolean toBoolean() {
		return getTau() > 0;
	}


	public String toString() {
		return "["+getLow()+","+getUpp()+"]";
	}


	/**
	 * @param phi the phi to set
	 */
	protected void setPhi(double phi) {
		this.phi = phi;
	}



	/**
	 * @return the phi
	 */
	public double getPhi() {
		return phi;
	}



	/**
	 * @param tau the tau to set
	 */
	protected void setTau(double tau) {
		this.tau = tau;
	}



	/**
	 * @return the tau
	 */
	public double getTau() {
		return tau;
	}


	public double getLow() {
		return getTau();
	}


	public double getUpp() {
		return 1.0 - getPhi();
	}




	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(phi);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(tau);
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
		IntervalDegree other = (IntervalDegree) obj;
        return Math.abs(this.phi - other.phi) <= EPSILON
                &&
               Math.abs(this.tau - other.tau) <= EPSILON;
    }



	public double getConfidence() {
		return phi+tau;
	}



	public IntervalDegree asIntervalDegree() {
		return this;
	}



    public boolean isComparableTo(Degree o) {
        IntervalDegree other = o.asIntervalDegree();

		return ( (this.getUpp() < other.getLow())
                   ||
                 (this.getLow() > other.getUpp()));
    }

	public int compareTo(Degree o) {
		IntervalDegree other = o.asIntervalDegree();

		if (this.getUpp() < other.getLow()) return -1;
		if (this.getLow() > other.getUpp()) return 1;
        if ((Math.abs(this.phi - other.phi) < EPSILON)
                &&
            (Math.abs(this.tau - other.tau) < EPSILON))
		return 0;

        return -99;
	}



	public Degree False() {
		return FALSE;
	}



	public Degree True() {
		return TRUE;
	}



	public Degree Unknown() {
		return UNKNOWN;
	}






    public Degree sum(Degree other) {
        double l = Math.min(1.0, this.getLow()+other.asIntervalDegree().getLow());
        double u = Math.min(1.0, this.getUpp()+other.asIntervalDegree().getUpp());

       return new IntervalDegree(l,u);
    }

    public Degree mul(Degree other) {
        return new IntervalDegree(
                this.getLow()*other.asIntervalDegree().getLow(),
                this.getUpp()*other.asIntervalDegree().getUpp()
        );
    }


    public Degree div(Degree other) {
        double d1 = other.asIntervalDegree().getUpp();
        double l = d1 != 0 ? Math.min(1.0, this.getLow()/d1) : 1.0;

        double d2 = other.asIntervalDegree().getLow();
        double u = d2 != 0 ? Math.min(1.0, this.getUpp()/d2) : 1.0;

       return new IntervalDegree(l,u);
    }

    public Degree sub(Degree other) {
        double l = Math.max(0.0, this.getLow() - other.asIntervalDegree().getUpp());
        double u = Math.max(0.0, this.getUpp() - other.asIntervalDegree().getLow());

       return new IntervalDegree(l,u);
    }


    public Degree max(Degree other) {
        return new IntervalDegree(
                Math.max(this.getLow(), other.asIntervalDegree().getLow()),
                Math.max(this.getUpp(), other.asIntervalDegree().getUpp())
        );
    }

    public Degree min(Degree other) {
        return new IntervalDegree(
                Math.min(this.getLow(),other.asIntervalDegree().getLow()),
                Math.min(this.getUpp(),other.asIntervalDegree().getUpp())
        );
    }

    public Degree fromConst(double number) {
        return new IntervalDegree(number,number);
    }

    public Degree fromString(String val) {
        val = val.replace("[","").replace("]","");
        int pos = val.indexOf(",");
        return new IntervalDegree( Double.parseDouble( val.substring( 0, pos - 1 ) ), Double.parseDouble( val.substring( pos + 1 ) ) );
    }
    
    public Degree fromBoolean( boolean val ) {
        return fromBooleanLiteral( val );
    }
    
    public static Degree fromBooleanLiteral( boolean val ) {
        return val ? TRUE : FALSE;
    }


}
