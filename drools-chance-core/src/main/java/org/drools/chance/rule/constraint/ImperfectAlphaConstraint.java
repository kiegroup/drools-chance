package org.drools.chance.rule.constraint;

import org.drools.chance.degree.Degree;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.AlphaNodeFieldConstraint;

public interface ImperfectAlphaConstraint extends AlphaNodeFieldConstraint, ImperfectConstraint {

    public Degree match( InternalFactHandle factHandle,
                         InternalWorkingMemory workingMemory,
                         ContextEntry context );

}
