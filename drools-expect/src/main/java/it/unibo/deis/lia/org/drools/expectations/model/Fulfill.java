package it.unibo.deis.lia.org.drools.expectations.model;

import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;

import java.util.List;

@Role( Role.Type.EVENT )
@PropertyReactive
public class Fulfill {

    // id of the initial activation
    @Key
    @Position( 0 )
    private long originId;

    // expectation's label
    @Key
    @Position( 1 )
    private String label;

    // id of the activation which generated the expectation
    @Key
    @Position( 2 )
    private long expId;

    // id of the activation generating the fulfillment
    @Key
    @Position( 3 )
    private long actId;

    // tuple which generated the expectation
    private List pending;

    // typle which fulfilled the expectation
    private List tuple;

    // name of the rule the expectation is declared within
    private String ruleName;

    public Fulfill( long originId, String label, long expId, long actId, List pending, List tuple, String ruleName ) {
        this.originId = originId;
        this.label = label;
        this.expId = expId;
        this.actId = actId;
        this.pending = pending;
        this.tuple = tuple;
        this.ruleName = ruleName;
    }

    public Fulfill( long originId, String label, long expId, long actId ) {
        this.originId = originId;
        this.label = label;
        this.expId = expId;
        this.actId = actId;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Fulfill ) ) return false;

        Fulfill fulfill = (Fulfill) o;

        if ( actId != fulfill.actId ) return false;
        if ( expId != fulfill.expId ) return false;
        if ( originId != fulfill.originId ) return false;
        if ( !label.equals( fulfill.label ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) ( originId ^ ( originId >>> 32 ) );
        result = 31 * result + label.hashCode();
        result = 31 * result + (int) ( expId ^ ( expId >>> 32 ) );
        result = 31 * result + (int) ( actId ^ ( actId >>> 32 ) );
        return result;
    }

    public long getOriginId() {
        return originId;
    }

    public void setOriginId( long originId ) {
        this.originId = originId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public long getExpId() {
        return expId;
    }

    public void setExpId( long expId ) {
        this.expId = expId;
    }

    public long getActId() {
        return actId;
    }

    public void setActId( long actId ) {
        this.actId = actId;
    }

    public List getPending() {
        return pending;
    }

    public void setPending( List pending ) {
        this.pending = pending;
    }

    public List getTuple() {
        return tuple;
    }

    public void setTuple( List tuple ) {
        this.tuple = tuple;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName( String ruleName ) {
        this.ruleName = ruleName;
    }

    @Override
    public String toString() {
        return "Fulfill{" +
               "originId=" + originId +
               ", label='" + label + '\'' +
               ", expId=" + expId +
               ", actId=" + actId +
               //", tuple=" + tuple +
               ", ruleName='" + ruleName + '\'' +
               '}';
    }
}




