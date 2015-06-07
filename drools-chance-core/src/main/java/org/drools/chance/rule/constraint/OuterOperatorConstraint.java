package org.drools.chance.rule.constraint;


import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.CompositeEvaluation;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.chance.evaluation.OuterOperatorEvaluation;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.util.bitmask.OpenBitSet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class OuterOperatorConstraint extends ImperfectOperatorConstraint implements ImperfectBetaConstraint {

    public OuterOperatorConstraint( int arity, ConnectiveCore conn, String label ) {
        super( arity, conn, label );
    }

    @Override
    public boolean isAllowedCachedLeft( ContextEntry context, InternalFactHandle handle ) {
        return match( handle, context );
    }

    @Override
    public boolean isAllowedCachedRight( LeftTuple tuple, ContextEntry context ) {
        return match( tuple.getHandle(), context );
    }

    @Override
    public boolean isAllowed( InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        return match( handle, context );
    }

    protected boolean match( InternalFactHandle handle, ContextEntry context ) {
        OuterOperatorEvaluation evaluation = new OuterOperatorEvaluation( getOwningNodeId(),
                                                                          null,
                                                                          null,
                                                                          getConnective(),
                                                                          new Evaluation[ arity ],
                                                                          getLabel() );
        ((ChanceFactHandle) handle).addEvaluation( getOwningNodeId(), evaluation );
        return true;
    }

    public ConstraintType getType() {
        return ConstraintType.BETA;
    }


    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
    }

    @Override
    public OuterOperatorConstraint clone() {
        OuterOperatorConstraint out = new OuterOperatorConstraint( this.arity, this.connective, this.label );
        out.setType( this.getType() );
        return out;
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        super.readExternal( in );
    }


    @Override
    public Degree matchCachedLeft( ContextEntry context, InternalFactHandle handle ) {
        throw new IllegalStateException( "This method should not have been called" );
    }

    @Override
    public Degree matchCachedRight( LeftTuple context, ContextEntry tuple ) {
        throw new IllegalStateException( "This method should not have been called" );
    }

}
