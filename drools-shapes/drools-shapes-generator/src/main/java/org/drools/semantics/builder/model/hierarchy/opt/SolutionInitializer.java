package org.drools.semantics.builder.model.hierarchy.opt;


import org.drools.planner.core.move.Move;
import org.drools.planner.core.phase.custom.CustomSolverPhaseCommand;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.PropertyRelation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class SolutionInitializer implements CustomSolverPhaseCommand {

    public void changeWorkingSolution( ScoreDirector scoreDirector ) {

        OptimalHierarchy hier = (OptimalHierarchy) scoreDirector.getWorkingSolution();

        initialize( hier.getInheritances(), scoreDirector );

    }


    private void initialize( LinkedHashMap<String, Con> inheritances, ScoreDirector scoreDirector ) {

        for ( Con con : inheritances.values() ) {
            con.getChosenProperties().putAll( con.getConcept().getProperties() );

            for ( Concept sup : con.getConcept().getSuperConcepts() ) {
                for ( PropertyRelation pro : sup.getAvailableProperties() ) {
                    con.getChosenProperties().put( pro.getProperty(), pro );
                }
            }
        }

        for ( Con con : inheritances.values() ) {
            if ( ! con.getConcept().getSuperConcepts().isEmpty() ) {

                Iterator<Concept> iter = con.getConcept().getSuperConcepts().iterator();
                Concept next = iter.next();
                ConceptStrengthEvaluator comparator = new ConceptStrengthEvaluator();
                int score = -1;
                while ( iter.hasNext() ) {
                    Concept candidate = iter.next();
                    int x = comparator.compare( candidate, next );
                    if ( x > score ) {
                        score = x;
                        next = candidate;
                    }
                }

                scoreDirector.beforeEntityAdded( con );
                scoreDirector.beforeVariableChanged( con, "chosenSuper" );
                con.setChosenSuper( inheritances.get( next.getIri() ) );
                scoreDirector.afterEntityAdded( con );

                if ( inheritances.containsKey( next.getIri() ) ) {
                    Con supCon = inheritances.get( next.getIri() );
                    for ( String key : supCon.getAvailablePropertiesVirtual().keySet() ) {
                        if ( con.getChosenProperties().containsKey( key ) ) {
                            con.getChosenProperties().remove( key );
                        }
                        //                        con.getAvailableProperties().put( key, supCon.getAvailableProperties().get( key ) );
                    }

                }

                scoreDirector.afterVariableChanged( con, "chosenSuper" );

            } else {
                con.setChosenSuper( con );
            }
        }



    }

}
