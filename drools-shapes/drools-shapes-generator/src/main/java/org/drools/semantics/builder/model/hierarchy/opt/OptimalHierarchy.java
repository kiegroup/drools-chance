package org.drools.semantics.builder.model.hierarchy.opt;


import org.drools.planner.api.domain.solution.PlanningEntityCollectionProperty;
import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
import org.drools.planner.core.solution.Solution;
import org.drools.semantics.builder.model.ConceptImplProxy;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.w3._2002._07.owl.Thing;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

public class OptimalHierarchy implements Solution<HardAndSoftScore> {

    private Collection<Concept> availableConcepts;
    private Collection<PropertyRelation> availableProperties;

    private Concept top;

    private LinkedHashMap<String, ConceptImplProxy> inheritances;

    private HardAndSoftScore score;


    public OptimalHierarchy( OntoModel model ) {
        availableConcepts = Collections.unmodifiableCollection( model.getConcepts() );
        availableProperties = Collections.unmodifiableCollection( model.getProperties() );

        top = model.getConcept( Thing.IRI );

        inheritances = new LinkedHashMap<String, ConceptImplProxy>( availableConcepts.size() );

        for ( Concept c  : availableConcepts ) {
            ConceptImplProxy x = new ConceptImplProxy( c );
            inheritances.put(x.getIri(), x);
        }
    }


    public OptimalHierarchy( OptimalHierarchy opt ) {
        this.availableConcepts = opt.availableConcepts;
        this.availableProperties = opt.availableProperties;

        inheritances = new LinkedHashMap<String, ConceptImplProxy>();
        for ( String key : opt.inheritances.keySet() ) {
            inheritances.put( key, opt.inheritances.get( key ).clone() );
        }

        score = opt.score;
    }


    @PlanningEntityCollectionProperty
    public Collection<ConceptImplProxy> getCons() {
        return inheritances.values();
    }

    public LinkedHashMap<String, ConceptImplProxy> getInheritances() {
        return inheritances;
    }


    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }


    public Collection<? extends Object> getProblemFacts() {
        return availableConcepts;
    }

    public Solution<HardAndSoftScore> cloneSolution() {
        return new OptimalHierarchy( this );
    }


    public Collection<Concept> getAvailableConcepts() {
        return availableConcepts;
    }


    public Collection<PropertyRelation> getAvailableProperties() {
        return availableProperties;
    }


    public Concept getTop() {
        return top;
    }

    public void setTop(Concept top) {
        this.top = top;
    }

    public ConceptImplProxy getCon( String iri ) {
        return inheritances.get( iri );
    }


    @Override
    public String toString() {
        String s = "Optimized Hierarchy ( " + score + " ) \n";
        for ( ConceptImplProxy con : inheritances.values() ) {
            s += "\t " + con + "\n";
        }
        return s;
    }



    public void updateModel( OntoModel model ) {

        for ( ConceptImplProxy con : getCons() ) {
            Concept x = con.getConcept();
            Concept sup = con.getChosenSuper().getConcept();
            x.setChosenSuperConcept( sup );
//            x.setChosenSuper( sup.getIri() );
            sup.getChosenSubConcepts().add( x );
            x.setChosenProperties( con.getChosenProperties() );
        }

    }
}
