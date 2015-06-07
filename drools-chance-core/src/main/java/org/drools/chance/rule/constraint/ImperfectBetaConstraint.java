package org.drools.chance.rule.constraint;

import org.drools.chance.degree.Degree;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.BetaNodeFieldConstraint;

public interface ImperfectBetaConstraint extends BetaNodeFieldConstraint, ImperfectConstraint {

    public Degree matchCachedLeft( ContextEntry context, InternalFactHandle handle );

    public Degree matchCachedRight( LeftTuple context, ContextEntry tuple );
    
}
