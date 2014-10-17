package org.drools.chance.factmodel;


import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.common.ImperfectField;
import org.drools.chance.common.ImperfectFieldImpl;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.ImpKind;
import org.drools.chance.distribution.ImpType;
import org.drools.core.factmodel.traits.TraitProxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class ImperfectTraitProxy extends TraitProxy implements Externalizable {
    
    protected ImperfectField<Boolean> holds;

    protected ImperfectTraitProxy() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }

    public ImperfectField<Boolean> getHolds() {
        if ( holds == null ) {
            System.err.println( "Had to create holds" );
            holds = new ImperfectFieldImpl<Boolean>(
                    ChanceStrategyFactory.<Boolean>buildStrategies(
                            ImpKind.PROBABILITY,
                            ImpType.BASIC,
                            DegreeType.SIMPLE,
                            Boolean.class),
                    "true/1.0"
            );
        }
        return holds;
    }

    public void setDegree( Degree deg ) {
        getHolds().setValue(true, deg);
    }
    
    public Degree isA() {
        return getHolds().getCurrent().getDegree( true );
    }

}
