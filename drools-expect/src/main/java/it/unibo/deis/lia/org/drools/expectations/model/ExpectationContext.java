package it.unibo.deis.lia.org.drools.expectations.model;

import org.drools.core.spi.Activation;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.runtime.rule.Match;

import java.io.Serializable;

@PropertyReactive
public class ExpectationContext implements Serializable {

    private static final long serialVersionUID = -3564358457726574602L;
    @Key
    @Position( 0 )
    private long ctxId;

    public ExpectationContext( long ctxId ) {
        this.ctxId = ctxId;
    }

    public ExpectationContext( Match match ) {
        this.ctxId = ( ( Activation ) match ).getActivationNumber();
    }

    public long getCtxId() {
        return ctxId;
    }

    public void setCtxId( long ctxId ) {
        this.ctxId = ctxId;
    }

    @Override
    public String toString() {
        return "ExpectationContext{" +
               "ctxId=" + ctxId +
               '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof ExpectationContext ) ) return false;

        ExpectationContext that = (ExpectationContext) o;

        if ( ctxId != that.ctxId ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) ( ctxId ^ ( ctxId >>> 32 ) );
    }
}
