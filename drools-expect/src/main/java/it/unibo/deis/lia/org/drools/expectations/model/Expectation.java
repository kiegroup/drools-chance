package it.unibo.deis.lia.org.drools.expectations.model;

import it.unibo.deis.lia.org.drools.expectations.Expectations;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.factmodel.Fact;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

import java.io.Serializable;
import java.util.List;

@PropertyReactive
public class Expectation implements Serializable {

    private static final long serialVersionUID = 4773544980103621154L;
    // id of the initial activation
    @Key @Position( 0 )
    private long originId;

    // expectation label (from the rule)
    @Key @Position( 1 )
    private String label;

    // quick reference to act.id
    @Key @Position( 2 )
    private long actId;

    // tuple which generated the expectation (ref to act.tuple)
    @Position( 3 )
    private List tuple;

    // timestamp of the expectation's generation
    @Position( 4 )
    private long start;

    // (timestamp of the moment the expectation is fulfilled/violated) - start
    @Position( 5 )
    private long duration;

    // true if pending
    @Position( 6 )
    private boolean active;

    // name of the rule the expectation is declared within
    @Position( 7 )
    private String ruleName;

    @Position( 8 )
    private boolean fulfilled;

    public Expectation( long originId, String label, long actId, List tuple, long start, long duration, boolean active, String ruleName ) {
        this.originId = originId;
        this.label = label;
        this.actId = actId;
        this.tuple = tuple;
        this.start = start;
        this.duration = duration;
        this.active = active;
        this.ruleName = ruleName;
        this.fulfilled = false;
    }

    public Expectation( long originId, String label, long actId ) {
        this.originId = originId;
        this.label = label;
        this.actId = actId;
        this.fulfilled = false;
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

    public long getActId() {
        return actId;
    }

    public void setActId( long actId ) {
        this.actId = actId;
    }

    public List getTuple() {
        return tuple;
    }

    public void setTuple( List tuple ) {
        this.tuple = tuple;
    }

    public long getStart() {
        return start;
    }

    public void setStart( long start ) {
        this.start = start;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration( long duration ) {
        this.duration = duration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive( boolean active ) {
        this.active = active;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName( String ruleName ) {
        this.ruleName = ruleName;
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    public void setFulfilled( boolean fulfilled ) {
        this.fulfilled = fulfilled;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Expectation ) ) return false;

        Expectation that = (Expectation) o;

        if ( actId != that.actId ) return false;
        if ( originId != that.originId ) return false;
        if ( !label.equals( that.label ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) ( originId ^ ( originId >>> 32 ) );
        result = 31 * result + label.hashCode();
        result = 31 * result + (int) ( actId ^ ( actId >>> 32 ) );
        return result;
    }

    @Override
    public String toString() {
        return "Expectation{" +
               "originId=" + originId +
               ", label='" + label + '\'' +
               //", act=" + act +
               ", actId=" + actId +
               ", start=" + start +
               ", duration=" + duration +
               ", active=" + active +
               ", ruleName='" + ruleName + '\'' +
               ", fulfilled=" + fulfilled +
               '}';
    }

}
