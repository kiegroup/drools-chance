package org.drools.chance.rule.constraint;


import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ImperfectAlphaConstraintAdapter implements ImperfectAlphaConstraint {

    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private AlphaNodeFieldConstraint innerConstraint;

    public ImperfectAlphaConstraintAdapter(AlphaNodeFieldConstraint innerConstraint) {
        this.innerConstraint = innerConstraint;
    }

    public Degree eval( InternalFactHandle factHandle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        return SimpleDegree.fromBooleanLiteral(isAllowed(factHandle, workingMemory, context));
    }

    public Degree match( InternalFactHandle factHandle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        return SimpleDegree.fromBooleanLiteral(isAllowed(factHandle, workingMemory, context));
    }

    public ContextEntry createContextEntry() {
        return innerConstraint.createContextEntry();
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        return innerConstraint.isAllowed( handle, workingMemory, context );
    }

    public Declaration[] getRequiredDeclarations() {
        return innerConstraint.getRequiredDeclarations();
    }

    public void replaceDeclaration( Declaration oldDecl, Declaration newDecl ) {
        innerConstraint.replaceDeclaration( oldDecl, newDecl );
    }

    public Constraint clone() {
        return innerConstraint.clone();
    }

    public ConstraintType getType() {
        return innerConstraint.getType();
    }

    public boolean isTemporal() {
        return innerConstraint.isTemporal();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        innerConstraint.writeExternal( out );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        innerConstraint.readExternal( in );
    }


}
