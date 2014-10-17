package org.drools.chance.rule.constraint;


import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.degree.Degree;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.Constraint;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class OperatorConstraint implements ImperfectAlphaConstraint {

    private ConnectiveCore connective;
    private int arity;
    private String label;


    private boolean cutting;

    public boolean isCutting() {
        return cutting;
    }

    public void setCutting(boolean cutting) {
        this.cutting = cutting;
    }

    public OperatorConstraint( int arity, ConnectiveCore conn, String label ) {
        this.arity = arity;
        this.connective = conn;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ConnectiveCore getConnective() {
        return connective;
    }

    public void setConnective(ConnectiveCore connective) {
        this.connective = connective;
    }

    public int getArity() {
        return arity;
    }

    public void setArity(int arity) {
        this.arity = arity;
    }

    public Degree match( InternalFactHandle factHandle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContextEntry createContextEntry() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
        return match( handle, workingMemory, context ).toBoolean();
    }

    @Override
    public AlphaNodeFieldConstraint cloneIfInUse() {
        return (AlphaNodeFieldConstraint) clone();
    }


    public Declaration[] getRequiredDeclarations() {
        return new Declaration[0];
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {

    }

    public Constraint clone() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConstraintType getType() {
        return ConstraintType.ALPHA;
    }

    public boolean isTemporal() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
