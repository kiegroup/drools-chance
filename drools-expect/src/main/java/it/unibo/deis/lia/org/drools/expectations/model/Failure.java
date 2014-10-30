package it.unibo.deis.lia.org.drools.expectations.model;

import org.kie.api.definition.type.Role;

import java.util.List;

@Role( Role.Type.EVENT )
public class Failure {

    private long originId;

    private String ruleName;

    private List tuple;

    public Failure( long originId, String ruleName, List tuple ) {
        this.originId = originId;
        this.ruleName = ruleName;
        this.tuple = tuple;
    }

    public long getOriginId() {
        return originId;
    }

    public void setOriginId( long originId ) {
        this.originId = originId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName( String ruleName ) {
        this.ruleName = ruleName;
    }

    public List getTuple() {
        return tuple;
    }

    public void setTuple( List tuple ) {
        this.tuple = tuple;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Failure ) ) return false;

        Failure success = (Failure) o;

        if ( originId != success.originId ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) ( originId ^ ( originId >>> 32 ) );
    }

    @Override
    public String toString() {
        return "Failure{" +
               "originId=" + originId +
               ", ruleName='" + ruleName + '\'' +
               ", tuple=" + tuple +
               '}';
    }
}
