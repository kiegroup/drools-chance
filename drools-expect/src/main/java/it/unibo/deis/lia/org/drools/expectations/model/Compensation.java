package it.unibo.deis.lia.org.drools.expectations.model;

import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;

import java.util.List;

@Role( Role.Type.EVENT )
public class Compensation {

    @Position( 0 )
    private String label;

    @Position( 1 )
    private long originId;

    @Position( 2 )
    private List tuple;

    public Compensation( String label, long originId, List tuple ) {
        this.label = label;
        this.originId = originId;
        this.tuple = tuple;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public List getTuple() {
        return tuple;
    }

    public void setTuple( List tuple ) {
        this.tuple = tuple;
    }

    public long getOriginId() {
        return originId;
    }

    public void setOriginId( long originId ) {
        this.originId = originId;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Compensation ) ) return false;

        Compensation that = (Compensation) o;

        if ( originId != that.originId ) return false;
        if ( !label.equals( that.label ) ) return false;
        if ( !tuple.equals( that.tuple ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + tuple.hashCode();
        result = 31 * result + (int) ( originId ^ ( originId >>> 32 ) );
        return result;
    }

    @Override
    public String toString() {
        return "Compensation{" +
               "label='" + label + '\'' +
               ", tuple=" + tuple +
               ", originId=" + originId +
               '}';
    }
}
