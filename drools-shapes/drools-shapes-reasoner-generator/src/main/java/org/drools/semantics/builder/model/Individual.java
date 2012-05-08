package org.drools.semantics.builder.model;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Individual {

    private String name;

    private String iri;

    private String type;

    private Map<String,Set<ValueTypePair>> propertyVals = new HashMap<String, Set<ValueTypePair>>();

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

    public void addPropertyVal( String propName, String lit, String type ) {
        Set<ValueTypePair> vals = propertyVals.get( propName );
        if ( vals == null ) {
            vals = new HashSet<ValueTypePair>();
            propertyVals.put( propName, vals );
        }
        vals.add( new ValueTypePair( lit, type ) );
    }

    public void setPropertyValues( String propName, Set<ValueTypePair> lits ) {
        propertyVals.put( propName, lits );
    }

    public Map<String, Set<ValueTypePair>> getPropertyVals() {
        return propertyVals;
    }

    public void setPropertyVals(Map<String, Set<ValueTypePair>> propertyVals) {
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

    public static class ValueTypePair {
        private String value;
        private String type;

        public ValueTypePair( String value, String type ) {
            this.value = value;
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ValueTypePair that = (ValueTypePair) o;

            if (type != null ? !type.equals(that.type) : that.type != null) return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "ValueTypePair{" +
                    "value='" + value + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
