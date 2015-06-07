package org.drools.chance.rule.constraint;


import org.drools.chance.evaluation.CompositeEvaluation;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.degree.Degree;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.util.bitmask.OpenBitSet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class InnerOperatorConstraint extends ImperfectOperatorConstraint implements ImperfectAlphaConstraint {

    private OpenBitSet betas;
    private OpenBitSet crisps;

    public InnerOperatorConstraint( int arity, ConnectiveCore conn, String label ) {
        super( arity, conn, label );
    }

    public boolean hasBetas() {
        return ! this.betas.isEmpty();
    }

    public void setBeta( int argIndex ) {
        if ( this.betas == null ) {
            betas = new OpenBitSet( arity );
        }
        this.betas.set( argIndex );
    }

    public boolean isAllowed( InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        CompositeEvaluation evaluation = new CompositeEvaluation( getOwningNodeId(),
                                                                  null,
                                                                  match( handle, workingMemory, context ),
                                                                  getConnective(),
                                                                  new Evaluation[ arity ],
                                                                  getLabel() );
        if ( betas != null ) {
            for ( int j = 0; j < arity; j++ ) {
                if ( betas.fastGet( j ) ) {
                    evaluation.setArgumentPlaceHolder( j, MockEvaluation.mock );
                }
            }
        }
        if ( crisps != null ) {
            for ( int j = 0; j < arity; j++ ) {
                if ( crisps.fastGet( j ) ) {
                     //&& betas != null && betas.fastGet( j )
                    evaluation.setArgumentPlaceHolder( j, MockEvaluation.tru );
                }
            }
        }

        (( ChanceFactHandle) handle).addEvaluation( getOwningNodeId(), evaluation );
        return true;
    }


    public Declaration[] getRequiredDeclarations() {
        return new Declaration[0];
    }

    public void replaceDeclaration( Declaration oldDecl, Declaration newDecl ) {
        throw new IllegalStateException( "Should not be called " );
    }

    public ConstraintType getType() {
        return ConstraintType.ALPHA;
    }


    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );

        out.writeObject( betas );
        out.writeObject( crisps );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        betas = (OpenBitSet) in.readObject();
        crisps = (OpenBitSet) in.readObject();
    }


    public void makeCrispArgument( int index ) {
        if ( crisps == null ) {
            crisps = new OpenBitSet( arity );
        }
        crisps.set( index );
    }

    @Override
    public boolean isAllowedCachedLeft( ContextEntry context, InternalFactHandle handle ) {
        throw new IllegalStateException( "This method should not have been called" );
    }

    @Override
    public boolean isAllowedCachedRight( LeftTuple tuple, ContextEntry context ) {
        throw new IllegalStateException( "This method should not have been called" );
    }

    @Override
    public InnerOperatorConstraint clone() {
        InnerOperatorConstraint inn = new InnerOperatorConstraint( this.arity, this.connective, this.label );
        inn.setType( this.getType() );
        inn.betas = this.betas;
        inn.crisps = this.crisps;
        return inn;
    }
}
