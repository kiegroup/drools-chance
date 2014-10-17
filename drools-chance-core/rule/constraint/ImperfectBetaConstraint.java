package org.drools.chance.rule.constraint;


import org.drools.chance.degree.Degree;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.ContextEntry;
import org.drools.spi.BetaNodeFieldConstraint;

public interface ImperfectBetaConstraint extends BetaNodeFieldConstraint, ImperfectConstraint {

    public Degree matchCachedLeft( ContextEntry context, InternalFactHandle handle );

    public Degree matchCachedRight( LeftTuple context, ContextEntry tuple );
    
    public int getNodeId();

    public void setNodeId( int id );
}
