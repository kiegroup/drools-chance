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


import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.AbstractVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;

import java.util.ArrayList;
import java.util.List;

import static choco.Choco.*;


public class ConstraintFactory {


    private enum Operator { EQ, LE, GE, LEQ, GEQ, NEQ };

    private static List<Pair<CPModel,Integer>> varCounter = new ArrayList<Pair<CPModel,Integer>>();




    public static RealVariable newUnitIntervalVariable(Object var, MinimizationProblem p) {
        return newUnitIntervalVariable(var, 0.0, 1.0, p);
    }


    public static RealVariable newUnitIntervalVariable(Object var, double low, double upp, MinimizationProblem p) {
        String varName = format(var,p);
        AbstractVariable v1 = p.getVariable( varName );
        if ( v1 instanceof  IntegerVariable ) {
            System.err.println("What went wrong");
        }
        RealVariable v = (RealVariable) v1;
        if ( v == null ) {
            v = makeRealVar( varName, low, upp );            
            p.registerVar( varName, v );
        }
        v.setLowB( Math.max( v.getLowB(), low ) );
        v.setUppB( Math.min( v.getUppB(), upp ) );
        return v;
    }





    public static IntegerVariable newBooleanVariable(Object var, MinimizationProblem p) {
        String varName = format(var,p);
        IntegerVariable v = (IntegerVariable) p.getVariable( varName );
        if ( v == null ) {
            v = makeBooleanVar( varName );
            p.registerVar( varName, v );
        }
        return v;
    }


    public static RealVariable newBooleanRealVariable(Object var, MinimizationProblem p) {

        IntegerVariable x = newBooleanVariable(var, p);

        RealVariable Y = castIntToRealVariable(x, p);

        return Y;
    }



    private static RealVariable castIntToRealVariable(IntegerVariable y, MinimizationProblem p) {
        RealVariable Y = newUnitIntervalVariable( "S"+y, p );
        p.addConstraint( eq( y, Y ) );

        return Y;
    }


    private static String format(Object var, MinimizationProblem p) {

//        return "x"+(var.toString().hashCode() % 10000);
        return "x_"+var;
    }




//	public static void addAndConstraint(Object[] vars, Object y, Object l, Problem prob) {
//				
//		addUnique(ConstraintFactory.buildConstraint(" 1.0*"+l+" +1.0*"+y,Operator.LE, 1.0));		
//		String terms = "";
//		for (int j = 0; j < vars.length; j++) {
//			// xj <= 1 - y
//			addUnique(ConstraintFactory.buildConstraint(" 1.0*"+vars[j]+" +1.0*"+y,Operator.LE, 1.0));
//			terms += "  1.0*" + vars[j];
//		}		
//		addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l+" +1.0*"+y,Operator.EQ, vars.length -1));
//	}

    public static void addAndConstraint(RealVariable[] vars, IntegerVariable y, RealVariable l, MinimizationProblem prob) {

//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+l+" -1.0*"+y,Operator.LE, 0.0), prob);

        RealVariable Y = castIntToRealVariable(y, prob);
        prob.addConstraint( leq(l, Y) );

        RealExpressionVariable terms = vars[0];
        for (int j = 1; j < vars.length; j++) {
            terms = plus(terms, vars[j]);
        }

//        addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l,Operator.LE, vars.length -1), prob);
        prob.addConstraint( leq( terms, plus( l, vars.length - 1.0 ) ) );

//        addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l+" -1.0*"+y,Operator.GE, vars.length -2), prob);
        prob.addConstraint( geq( terms, plus( plus( l, Y ), vars.length - 2.0 ) ) );

    }









//	public static void addOrConstraint(Object[] vars, Object y, Object l, Problem prob) {			
//		String terms = "";
//		for (int j = 0; j < vars.length; j++) {			
//			terms += "  1.0*" + vars[j];
//		}
//				
//		addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l,Operator.EQ, 0), prob);
//		
//	}




    public static void addOrConstraint(RealVariable[] vars, IntegerVariable y, RealVariable l, MinimizationProblem prob) {
//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+l+" -1.0*"+y,Operator.GE, 0.0), prob);
        RealVariable Y = castIntToRealVariable(y, prob);
        prob.addConstraint( geq(l, Y) );

        RealExpressionVariable terms = vars[0];
        for (int j = 1; j < vars.length; j++) {
            terms = plus( terms, vars[j] );
        }

//        addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l,Operator.GE, 0), prob);
        prob.addConstraint( geq(terms, l) );

//        addUnique(ConstraintFactory.buildConstraint(terms+" -1.0*"+l+" -1.0*"+y,Operator.LE, 0), prob);
        prob.addConstraint( leq(terms, plus(l, Y)) );
    }
////	







    public static void addComplementConstraint(RealVariable v1, RealVariable v2, MinimizationProblem prob) {
//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+v1+" +1.0*"+v2, Operator.EQ, 1.0), prob);
        prob.addConstraint( eq( v1, minus( 1.0, v2 ) ) );
    }

    public static void addEqualityConstraint(RealVariable v1, RealVariable v2, MinimizationProblem prob) {
//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+v1+" -1.0*"+v2,Operator.EQ, 0.0), prob);
        prob.addConstraint( eq( v1, v2 ) );
    }

    public static void addImplicationConstraint(RealVariable xA, RealVariable xB, RealVariable l, MinimizationProblem prob) {
//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+l+" -1.0*"+xA+" 1.0*"+xB,Operator.LE,0.0), prob);
        prob.addConstraint( leq( plus( l, xB ), xA ) );
    }

    public static void addLBConstraint(RealVariable x, RealVariable l, MinimizationProblem prob) {
//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+x+" -1.0*"+l,Operator.GE,0), prob);
        prob.addConstraint( geq( x, l ) );
    }

    public static void addNumericLBConstraint(RealVariable x, Number tau, MinimizationProblem prob) {
//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+x,Operator.GE,tau), prob);
        prob.addConstraint( geq( x, tau.doubleValue() ) );
    }

    public static void addUBConstraint(RealVariable x, RealVariable l, MinimizationProblem prob) {
//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+x+" +1.0*"+l,Operator.LE,1.0), prob);
        prob.addConstraint( leq( x, minus(1.0, l) ) );
    }

    public static void addNumericUBConstraint(RealVariable x, Number phi, MinimizationProblem prob) {
//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+x,Operator.LE,phi), prob);
        prob.addConstraint( leq( x, phi.doubleValue() ) );
    }


    // ( prop && klass ) <= father
    public static void addExistConstraint(RealVariable prop, RealVariable klass, RealVariable father, MinimizationProblem prob) {
//        addUnique(ConstraintFactory.buildConstraint(" 1.0*"+prop+" +1.0*"+klass+" -1.0*"+father,Operator.LE, 1), prob);
        prob.addConstraint( leq( plus( prop, klass ), plus( father, 1.0 ) ) );
    }

    // ( prop => klass ) >= father
    public static void addForallConstraint(RealVariable prop, RealVariable klass, IntegerVariable y, RealVariable father, MinimizationProblem prob) {
        RealVariable Y = newUnitIntervalVariable( "S"+y, prob );

//        addUnique(ConstraintFactory.buildConstraint(" -1.0*"+father+" -1.0*"+prop+" +1.0*"+klass,Operator.GE,-1), prob);
        prob.addConstraint( geq( klass, minus( plus( father, prop ), 1.0 ) ) );
    }


    private static void addUnique( Constraint c, MinimizationProblem prob ) {
        // Constraint does not redefine equals :(

        //if (! prob.getConstraints().contains(c))
        //	prob.add(c);
//		for (Constraint con : prob.getConstraints()) {
//			if (con.toString().equals(c.toString()))
//				return;
//		}
        prob.addConstraint( c );

    }

    private static class Pair<T, K> {
        private T key;
        private K value;

        private Pair(T key, K value) {
            this.key = key;
            this.value = value;
        }

        public T getKey() {
            return key;
        }

        public void setKey(T key) {
            this.key = key;
        }


        @Override
        public String toString() {
            return "Pair{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
            if (value != null ? !value.equals(pair.value) : pair.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        public K getValue() {
            return value;
        }

        public void setValue(K value) {
            this.value = value;
        }
    }
}
 