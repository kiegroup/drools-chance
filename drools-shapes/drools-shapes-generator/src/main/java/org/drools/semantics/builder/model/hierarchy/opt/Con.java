package org.drools.semantics.builder.model.hierarchy.opt;


import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.PropertyRelation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@PlanningEntity
public class Con implements Cloneable {

    private String iri;
    private Concept concept;
    private Con     chosenSuper;
    private Map<String, PropertyRelation> chosenProperties;
    private Set<String> neededProperties;


    protected Con( Concept con, Con sup ) {
        this.iri = con.getIri();
        this.chosenSuper = sup;
        this.concept = con;
        this.chosenProperties = new HashMap<String, PropertyRelation>();
        this.neededProperties = new HashSet<String>();

        Set<PropertyRelation> pros = con.getAvailableProperties();
        for ( PropertyRelation pro : pros ) {
            neededProperties.add( pro.getProperty() );
        }
    }

    protected Con( Con con ) {
        this.iri = con.getIri();
        this.chosenSuper = con.getChosenSuper();
        this.concept = con.getConcept();
        this.chosenProperties = new HashMap<String, PropertyRelation>( con.getChosenProperties() );
        this.neededProperties = con.getNeededProperties();
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    @PlanningVariable( strengthComparatorClass = ConceptStrengthEvaluator.class )
    @ValueRange( type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "cons" )
    public Con getChosenSuper() {
        return chosenSuper;
    }

    public void setChosenSuper(Con dom) {
        this.chosenSuper = dom;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Con con = (Con) o;

        if (iri != null ? !iri.equals(con.iri) : con.iri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return iri != null ? iri.hashCode() : 0;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public Map<String, PropertyRelation> getChosenProperties() {
        return chosenProperties;
    }

    public void setChosenProperties(Map<String, PropertyRelation> chosenProperties) {
        this.chosenProperties = chosenProperties;
    }

    public Con clone() {
        return new Con( this );
    }

    @Override
    public String toString() {
        String s = "Con{ iri='" + iri + "\' child of " + chosenSuper.getIri() + "\n";
        s += "\t\t chosen " + chosenProperties.size() + "\t" + chosenProperties.keySet() + "\n";
        s += "\t\t  avail " + getAvailablePropertiesVirtual().size() + "\t" + getAvailablePropertiesVirtual().keySet() + "\n";
        s += "\t\t needed " + neededProperties.size() + "\t" + neededProperties;
        return s;
    }

    public Map<String, PropertyRelation> getAvailablePropertiesVirtual() {
        Map<String, PropertyRelation> virtual = new HashMap<String, PropertyRelation>( chosenProperties );
        if ( chosenSuper != null && chosenSuper != this ) {
            virtual.putAll( chosenSuper.getAvailablePropertiesVirtual() );
        }
        return virtual;
    }

    public Set<String> getNeededProperties() {
        return neededProperties;
    }

}
