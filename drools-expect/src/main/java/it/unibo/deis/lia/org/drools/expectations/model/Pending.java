package it.unibo.deis.lia.org.drools.expectations.model;

import it.unibo.deis.lia.org.drools.expectations.Expectations;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

import java.io.Serializable;
import java.util.List;

@Role( Role.Type.EVENT )
@PropertyReactive
public class Pending implements Serializable {

    private static final long serialVersionUID = -669101176444937215L;
    @Key
    @Position( 0 )
    private String label;

    @Key
    @Position( 1 )
    private long actId;

    @Position( 2 )
    private List tuple;

    public Pending( String label, long actId, List tuple ) {
        this.label = label;
        this.actId = actId;
        this.tuple = tuple;
    }

    public Pending( long actId, String label ) {
        this.actId = actId;
        this.label = label;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Pending ) ) return false;

        Pending pending = (Pending) o;

        if ( actId != pending.actId ) return false;
        if ( !label.equals( pending.label ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + (int) ( actId ^ ( actId >>> 32 ) );
        return result;
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

    @Override
    public String toString() {
        return "Pending{" +
               "label='" + label + '\'' +
               //", act=" + act +
               ", actId=" + actId +
               '}';
    }

}
