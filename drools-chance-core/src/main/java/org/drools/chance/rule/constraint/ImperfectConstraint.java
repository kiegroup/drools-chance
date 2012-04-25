package org.drools.chance.rule.constraint;

import org.drools.spi.Constraint;


public interface ImperfectConstraint extends Constraint {
    
    public String getLabel();

    public void setLabel( String label );

}
