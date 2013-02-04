package org.drools.semantics.builder.model.hierarchy;


import org.drools.planner.config.SolverFactory;
import org.drools.planner.config.XmlSolverFactory;
import org.drools.planner.core.Solver;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.hierarchy.opt.ConProxy;
import org.drools.semantics.builder.model.hierarchy.opt.ConceptStrengthEvaluator;
import org.drools.semantics.builder.model.hierarchy.opt.OptimalHierarchy;

import java.util.Iterator;

public class OptimizedModelProcessor implements ModelHierarchyProcessor {

    public void process( OntoModel model ) {

        SolverFactory solverFactory = new XmlSolverFactory( "/org/drools/semantics/builder/model/hierarchy/hier_joined_config.xml" );

        Solver solver = solverFactory.buildSolver();

        OptimalHierarchy problem = new OptimalHierarchy( model );

        solver.setPlanningProblem( problem );

        solver.solve();

        OptimalHierarchy solvedHierarchy = (OptimalHierarchy) solver.getBestSolution();

        System.out.println( "\n\n\n\n ********************************************** \n\n\n" );
        System.out.println( " Final solution :" );
        System.out.println( solvedHierarchy );
        System.out.println( "\n\n\n\n ********************************************** \n\n\n" );

        solvedHierarchy.updateModel( model );


    }

}
