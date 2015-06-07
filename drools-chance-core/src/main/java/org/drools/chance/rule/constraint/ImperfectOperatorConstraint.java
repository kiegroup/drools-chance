package org.drools.chance.rule.constraint;


import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.CompositeEvaluation;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.util.bitmask.OpenBitSet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class ImperfectOperatorConstraint extends MutableTypeConstraint implements ImperfectConstraint {

    protected ConnectiveCore connective;
    protected int arity = -1;
    protected String label;
    protected boolean cutting;

    public boolean isCutting() {
        return cutting;
    }

    public void setCutting( boolean cutting ) {
        this.cutting = cutting;
    }

    protected ImperfectOperatorConstraint( int arity, ConnectiveCore conn, String label ) {
        this.arity = arity;
        this.connective = conn;
        this.label = label;

        // force node ownership
        this.setInUse();
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
        // operator evaluations are delayed
        return null;
    }

    public ContextEntry createContextEntry() {
        return new OperatorContextEntry();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Declaration[] getRequiredDeclarations() {
        return new Declaration[0];
    }

    public void replaceDeclaration( Declaration oldDecl, Declaration newDecl ) {
        throw new IllegalStateException( "Should not be called " );
    }

    public boolean isTemporal() {
        return false;
    }


    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( connective );
        out.writeInt( arity );
        out.writeObject( label );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        super.readExternal( in );

        connective = (ConnectiveCore) in.readObject();
        arity = in.readInt();
        label = (String) in.readObject();
    }

    public ImperfectOperatorConstraint cloneIfInUse( int forNode ) {
        ImperfectOperatorConstraint clone = (ImperfectOperatorConstraint) super.cloneIfInUse( forNode );
        clone.setOwningNodeId( forNode );
        return clone;
    }

    @Override
    public String toString() {
        return connective.toString() + "/"  + arity;
    }

    protected class OperatorContextEntry implements ContextEntry {
        @Override
        public ContextEntry getNext() {
            return null;
        }

        @Override
        public void setNext( ContextEntry entry ) {

        }

        @Override
        public void updateFromTuple( InternalWorkingMemory workingMemory, LeftTuple tuple ) {

        }

        @Override
        public void updateFromFactHandle( InternalWorkingMemory workingMemory, InternalFactHandle handle ) {

        }

        @Override
        public void resetTuple() {

        }

        @Override
        public void resetFactHandle() {

        }

        @Override
        public void writeExternal( ObjectOutput out ) throws IOException {

        }

        @Override
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {

        }
    }
}
