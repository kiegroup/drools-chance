package org.drools.semantics.builder.model.hierarchy.opt;


import org.drools.semantics.builder.model.ConceptImplProxy;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.PropertyRelation;
import org.optaplanner.core.impl.phase.custom.CustomSolverPhaseCommand;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class SolutionInitializer implements CustomSolverPhaseCommand {

    public void changeWorkingSolution( ScoreDirector scoreDirector ) {

        OptimalHierarchy hier = (OptimalHierarchy) scoreDirector.getWorkingSolution();

        initialize( hier.getInheritances(), scoreDirector );

    }


    private void initialize( LinkedHashMap<String, ConceptImplProxy> inheritances, ScoreDirector scoreDirector ) {

        for ( ConceptImplProxy con : inheritances.values() ) {
            con.getChosenProperties().putAll( con.getConcept().getProperties() );

            for ( Concept sup : con.getConcept().getSuperConcepts() ) {
                for ( PropertyRelation pro : sup.getAvailableProperties() ) {
                    con.getChosenProperties().put( pro.getProperty(), pro );
                }
            }
        }

        for ( ConceptImplProxy con : inheritances.values() ) {
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
                    ConceptImplProxy supCon = inheritances.get( next.getIri() );
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
