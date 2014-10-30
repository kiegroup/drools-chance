package it.unibo.deis.lia.org.drools.expectations.model;

import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;

import java.util.List;

@Role( Role.Type.EVENT )
@PropertyReactive
public class Viol {

    // id of the initial activation
    @Key
    private long originId;

    // expectation's label
    @Key
    private String label;

    // id of the activation which generated the expectation
    @Key
    private long expId;

    // id of the activation generating the violation
    @Key
    private long actId;

    // tuple which generated the expectation
    private List pending;

    // has the violation been compensated?
    private boolean compensated;

    // name of the rule the expectation is declared within
    private String ruleName;

    public Viol( long originId, String label, long expId, long actId, List pending, boolean compensated, String ruleName ) {
        this.originId = originId;
        this.label = label;
        this.expId = expId;
        this.actId = actId;
        this.pending = pending;
        this.compensated = compensated;
        this.ruleName = ruleName;
    }

    public Viol( long originId, String label, long expId, long actId ) {
        this.originId = originId;
        this.label = label;
        this.expId = expId;
        this.actId = actId;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Viol ) ) return false;

        Viol viol = (Viol) o;

        if ( actId != viol.actId ) return false;
        if ( expId != viol.expId ) return false;
        if ( originId != viol.originId ) return false;
        if ( !label.equals( viol.label ) ) return false;

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

    public boolean isCompensated() {
        return compensated;
    }

    public void setCompensated( boolean compensated ) {
        this.compensated = compensated;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName( String ruleName ) {
        this.ruleName = ruleName;
    }

    @Override
    public String toString() {
        return "Viol{" +
               "originId=" + originId +
               ", label='" + label + '\'' +
               ", expId=" + expId +
               ", actId=" + actId +
               ", pending=" + pending +
               ", compensated=" + compensated +
               ", ruleName='" + ruleName + '\'' +
               '}';
    }
}




