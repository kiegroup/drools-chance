package org.drools.semantics.builder.model.hierarchy;


import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.hierarchy.opt.OptimalHierarchy;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.solver.XStreamXmlSolverFactory;

public class OptimizedModelProcessor implements ModelHierarchyProcessor {

    public void process( OntoModel model ) {

        SolverFactory solverFactory = new XStreamXmlSolverFactory();
        ((XStreamXmlSolverFactory) solverFactory).configure( "org/drools/semantics/builder/model/hierarchy/hier_joined_config.xml" );

        Solver solver = solverFactory.buildSolver();

        OptimalHierarchy problem = new OptimalHierarchy( model );

        solver.solve( problem );

        OptimalHierarchy solvedHierarchy = (OptimalHierarchy) solver.getBestSolution();

        System.out.println( "\n\n\n\n ********************************************** \n\n\n" );
        System.out.println( " Final solution :" );
        System.out.println( solvedHierarchy );
        System.out.println( "\n\n\n\n ********************************************** \n\n\n" );

        solvedHierarchy.updateModel( model );


    }

}
