package org.drools.semantics.builder.model.hierarchy.opt;


import org.drools.planner.core.heuristic.selector.move.factory.MoveListFactory;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;
import org.drools.semantics.builder.model.ConceptImplProxy;
import org.drools.semantics.builder.model.Concept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssignSuperMoveFactory implements MoveListFactory {

    private Concept thing;

    public List<Move> createMoveList(Solution solution) {
        OptimalHierarchy hier = (OptimalHierarchy) solution;
        Set<Move> moveSet = new HashSet<Move>();


        thing = hier.getTop();

        for ( ConceptImplProxy con : hier.getCons() ) {

//            if ( !Thing.IRI.equals( con.getIri() ) ) {
//                for ( ConProxy candidate : hier.getInheritances() ) {
//                    if ( con != candidate ) {
//                        moveSet.add( new AssignDomainMove( con, candidate ) );
//                    }
//                }
//            }


            addSuperConcepts( con, con.getConcept(), moveSet, hier );

//            if ( con.getConcept().getSuperConcepts().size() > 1 ) {
//                addSuperConcepts( con, con.getConcept(), moveSet, hier );
//            }
        }

        System.out.println( "Creating move List " + moveSet.size() );
        return new ArrayList( moveSet );
    }

    private void addSuperConcepts( ConceptImplProxy base, Concept con, Set<Move> moveList, OptimalHierarchy hier ) {
        for ( Concept x : con.getSuperConcepts() ) {
            AssignDomainMove move = new AssignDomainMove( base, hier.getCon( x.getIri() ) );
//                move.setVerbose( true );
            moveList.add( move );
            addSuperConcepts( base, x, moveList, hier );
        }
    }


    public static class AssignDomainMove implements Move {

        private ConceptImplProxy con;
        private ConceptImplProxy next;

        private boolean verbose = false;

        public boolean isVerbose() {
            return verbose;
        }

        public void setVerbose(boolean verbose) {
            this.verbose = verbose;
        }

        public AssignDomainMove( ConceptImplProxy con, ConceptImplProxy next ) {
            this.con = con;
            this.next = next;
        }

        public boolean isMoveDoable( ScoreDirector scoreDirector ) {
//            if ( verbose ) { System.out.println( "Checking doability of " + this.toString() ); }
//            return next != prev && ! isSubConceptOf( next, con );
            return con.getChosenSuper() == null || ( ! next.getIri().equals( con.getChosenSuper().getIri() ) );
        }

        private boolean isSubConceptOf( Concept c1, Concept c2 ) {
            if ( c2.getChosenSubConcepts().isEmpty() ) {
                return false;
            }
            if ( c2.getChosenSubConcepts().contains( c1 ) ) {
                return true;
            }
            for ( Concept child : c2.getChosenSubConcepts() ) {
                if ( isSubConceptOf( c1, child ) ) {
                    return true;
                }
            }
            return false;
        }

        public Move createUndoMove( ScoreDirector scoreDirector ) {
            OptimalHierarchy hier = (OptimalHierarchy) scoreDirector.getWorkingSolution();
            AssignDomainMove move = new AssignDomainMove( con, hier.getCon( con.getChosenSuper().getIri() ) );
//                move.setVerbose( true );
            return move;
        }

        public void doMove( ScoreDirector scoreDirector ) {
            scoreDirector.beforeVariableChanged( con, "chosenSuper" );
            OptimalHierarchy hier = (OptimalHierarchy) scoreDirector.getWorkingSolution();
            ConceptImplProxy prev = hier.getCon( con.getChosenSuper().getIri() );

            if ( verbose ) {
                System.out.println( "Setting " + next.getIri() + " as super of " + con.getIri() + ( con.getChosenSuper() != null ? " in place of " + con.getChosenSuper().getIri() : " " ) );
            }
            if ( prev != null ) {
                for ( String key : prev.getAvailablePropertiesVirtual().keySet() ) {
                    if ( ! con.getChosenProperties().containsKey( key ) && ! next.getAvailablePropertiesVirtual().containsKey( key ) ) {
                        con.getChosenProperties().put( key, prev.getAvailablePropertiesVirtual().get( key ) );
                    }
                }
            }

            con.setChosenSuper( next );
//
//  next.getChosenSubConcepts().add( con );
            for ( String key : next.getAvailablePropertiesVirtual().keySet() ) {
                if ( con.getChosenProperties().containsKey( key ) ) {
                    if ( verbose ) { System.out.println(" \tRemoving inherited property " + key +  " from concept " + con.getIri() ); }
                    con.getChosenProperties().remove( key );
                }
            }


            if ( verbose ) {
                System.out.println( "Done Setting " + next.getIri() + " as super of " + con.getIri() + ( prev != null ? " in place of " + prev.getIri() : " " ) );
                System.out.println( con );
            }

            scoreDirector.afterVariableChanged( con, "chosenSuper" );
        }

        public Collection<? extends Object> getPlanningEntities() {
            return Collections.singleton( con );
        }

        public Collection<? extends Object> getPlanningValues() {
            return Arrays.asList( next );
        }

        @Override
        public String toString() {
            return con.getIri() + " => " + next.getIri();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AssignDomainMove that = (AssignDomainMove) o;

            if (con != null ? !con.equals(that.con) : that.con != null) return false;
            if (next != null ? !next.equals(that.next) : that.next != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = con != null ? con.hashCode() : 0;
            result = 31 * result + (next != null ? next.hashCode() : 0);
            return result;
        }
    }
}
