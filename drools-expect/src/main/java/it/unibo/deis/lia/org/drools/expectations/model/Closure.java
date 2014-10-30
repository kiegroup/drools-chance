package it.unibo.deis.lia.org.drools.expectations.model;

import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;

@Role( Role.Type.EVENT )
public class Closure {

    @Position( 0 )
    private String label;

    public Closure( String label ) {
        this.label = label;
    }

    public Closure() {
        this.label = "*";
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Closure ) ) return false;

        Closure closure = (Closure) o;

        if ( label != null ? !label.equals( closure.label ) : closure.label != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Closure{" +
               "label='" + label + '\'' +
               '}';
    }
}
