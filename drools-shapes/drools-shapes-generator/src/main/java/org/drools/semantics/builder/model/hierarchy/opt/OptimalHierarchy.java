package org.drools.semantics.builder.model.hierarchy.opt;


import org.drools.semantics.builder.model.ConceptImplProxy;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.solution.Solution;
import org.w3._2002._07.owl.Thing;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

@PlanningSolution
public class OptimalHierarchy implements Solution<HardSoftScore> {

    private Collection<Concept> availableConcepts;
    private Collection<PropertyRelation> availableProperties;

    private Concept top;

    private LinkedHashMap<String, ConceptImplProxy> inheritances;

    private HardSoftScore score;


    public OptimalHierarchy() {

    }

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


    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }


    public Collection<? extends Object> getProblemFacts() {
        return availableConcepts;
    }

    public Solution<HardSoftScore> cloneSolution() {
        return new OptimalHierarchy( this );
    }


    @ValueRangeProvider(id = "cons")
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
