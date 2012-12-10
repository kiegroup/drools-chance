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


import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.AbstractVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.Solver;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MinimizationProblem {

    public static final double precision = 1e-2;

    private CPModel model;

    private RealVariable X;
    private RealVariable XNeg;

    private Map<Object, AbstractVariable> cache = new HashMap<Object, AbstractVariable>();

    public MinimizationProblem() {
        model = new CPModel();
        init();
    }


    public MinimizationProblem(CPModel model) {
        this.model = model;
        init();
    }

    private void init() {

        // Target : minimize X
        // 1-X is the NECessity of neg C	(C is the target class)
        // min X <=> maximize NEC --C
        // NEC --C <==> 1 - POSsibility of C
        // so, minimizing POS C
        // which is the best value for NEC C
        X = ConstraintFactory.newUnitIntervalVariable ( "X", this );
        model.addVariable( Options.V_OBJECTIVE, X );

        // can't have a degree with 1-X ?
        // just switch : nX == 1 - X
        XNeg = ConstraintFactory.newUnitIntervalVariable( "nX", this );
        ConstraintFactory.addComplementConstraint( X, XNeg, this );
    }


    public CPModel getModel() {
        return model;
    }

    public void setModel(CPModel model) {
        this.model = model;
    }

    public RealVariable getX() {
        return X;
    }

    public void setX(RealVariable x) {
        X = x;
    }

    public RealVariable getXNeg() {
        return XNeg;
    }

    public void setXNeg(RealVariable xneg) {
        XNeg = xneg;
    }



    public double solve( ) {

        Solver solver = new CPSolver();
        solver.setPrecision(precision);

        solver.read( model );
//        System.out.println("SOLVING " + model.pretty() );

        long now = new Date().getTime();

        solver.minimize( false );
        solver.solve();

        now = new Date().getTime() - now;


        double ans = solver.getVar( getX() ).getValue().getInf();

        System.out.println( "SOLUTION TIME IS " + now );

        return ans;

    }

    public void addConstraint(Constraint c) {
        model.addConstraint( c );
    }

    public AbstractVariable getVariable( Object varName ) {
        return cache.get( varName );
    }

    public void registerVar( Object varName, AbstractVariable var ) {
        cache.put( varName, var );
    }
}
