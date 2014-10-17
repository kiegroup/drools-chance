package org.drools.chance.rule.constraint;


import org.drools.chance.degree.Degree;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;

public interface ImperfectAlphaConstraint extends AlphaNodeFieldConstraint, ImperfectConstraint {

    public Degree match( InternalFactHandle factHandle,
                         InternalWorkingMemory workingMemory,
                         ContextEntry context );

}
