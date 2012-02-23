package org.drools.semantics.builder.model;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Individual {

    private String name;

    private String iri;

    private String type;

    private Map<String,Set<String>> propertyVals = new HashMap<String, Set<String>>();

    public Individual( String name, String iri, String type ) {
        this.name = name;
        this.iri = iri;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }


    public void addPropertyVal() {

    }

    public void addPropertyVal( String propName, String lit ) {
        Set<String> vals = propertyVals.get( propName );
        if ( vals == null ) {
            vals = new HashSet<String>();
            propertyVals.put( propName, vals );
        }
        vals.add( lit );
    }

    public void setPropertyValues( String propName, Set<String> lits ) {
        propertyVals.put( propName, lits );
    }

    public Map<String, Set<String>> getPropertyVals() {
        return propertyVals;
    }

    public void setPropertyVals(Map<String, Set<String>> propertyVals) {
        this.propertyVals = propertyVals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Individual that = (Individual) o;

        if (iri != null ? !iri.equals(that.iri) : that.iri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return iri != null ? iri.hashCode() : 0;
    }
}
