package org.drools.chance.rule.constraint;


import org.drools.chance.evaluation.SimpleEvaluationImpl;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.rule.constraint.core.evaluators.ImperfectEvaluator;
import org.drools.chance.degree.Degree;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.VariableRestriction;
import org.drools.core.rule.constraint.EvaluatorConstraint;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.kie.api.runtime.rule.FactHandle;

import java.util.UUID;

public class ImperfectEvaluatorConstraint extends EvaluatorConstraint
        implements ImperfectAlphaConstraint, ImperfectBetaConstraint {

    private int refNodeId;

    private String label;

    private boolean cutting;

    public boolean isCutting() {
        return cutting;
    }

    public void setCutting(boolean cutting) {
        this.cutting = cutting;
    }


    public ImperfectEvaluatorConstraint( String label ) {
        setLabel( label );
    }

    public ImperfectEvaluatorConstraint( FieldValue field, Evaluator evaluator, InternalReadAccessor extractor, String label ) {
        super( field, evaluator, extractor );
        setLabel( label );
    }

    public ImperfectEvaluatorConstraint( Declaration[] declarations, Evaluator evaluator, InternalReadAccessor extractor, String label ) {
        super( declarations, evaluator, extractor );
        setLabel( label );
    }

    public boolean isAllowed( InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        return match( handle, workingMemory, context ).toBoolean();
    }

    public boolean isAllowedCachedLeft( ContextEntry context, InternalFactHandle handle) {
        return matchCachedLeft( context, handle ).toBoolean();
    }

    public boolean isAllowedCachedRight( LeftTuple tuple, ContextEntry context ) {
        return matchCachedRight( tuple, context ).toBoolean();
    }


    public Degree match( InternalFactHandle factHandle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        ImperfectEvaluator evaluator = (ImperfectEvaluator) getEvaluator();
        Degree deg;

        if ( isLiteral() ) {
            deg = evaluator.match( workingMemory, rightReadAccessor, factHandle, field );
        } else {
            deg = evaluator.match(
                    workingMemory,
                    rightReadAccessor,
                    factHandle,
                    declarations[ 0 ].getExtractor(),
                    factHandle );
        }

        ((ChanceFactHandle) factHandle).addEvaluation( getOwningNodeId(),
                                                       new SimpleEvaluationImpl( getOwningNodeId(), null, deg, getLabel() ) );

        return deg;
    }


    public Degree matchCachedLeft( ContextEntry context, InternalFactHandle handle ) {
        Degree deg;
        if (isLiteral()) {
            deg = ( (ImperfectEvaluator) evaluator).match( ((LiteralContextEntry) context).workingMemory,
                                                           ((LiteralContextEntry) context).getFieldExtractor(),
                                                           handle,
                                                           field );
        } else {
            deg = ( (ImperfectEvaluator) evaluator ).matchCachedLeft( ( (VariableRestriction.VariableContextEntry) context ).workingMemory,
                                                                      (VariableRestriction.VariableContextEntry) context,
                                                                      handle );
        }

        ((ChanceFactHandle) handle).addEvaluation( getOwningNodeId(),
                                                   new SimpleEvaluationImpl( getOwningNodeId(), null, deg, getLabel() ) );

        return deg;
    }

    public Degree matchCachedRight(LeftTuple tuple, ContextEntry context) {
        Degree deg;
        InternalFactHandle handle = tuple.getHandle();
        if ( isLiteral() ) {
            deg = ((ImperfectEvaluator)evaluator).match( ((LiteralContextEntry) context).workingMemory,
                                                         ((LiteralContextEntry) context).getFieldExtractor(),
                                                         ((LiteralContextEntry) context).getFactHandle(),
                                                         field );
        } else {
            deg = ( (ImperfectEvaluator) evaluator ).matchCachedRight(( (VariableRestriction.VariableContextEntry) context ).workingMemory,
                                                                      (VariableRestriction.VariableContextEntry) context,
                                                                      tuple.get( declarations[ 0 ] ) );
        }

        ((ChanceFactHandle) handle ).addEvaluation( getOwningNodeId(),
                                                       new SimpleEvaluationImpl( getOwningNodeId(), null, deg, getLabel() ) );

        return deg;
    }

    public int getNodeId() {
        return refNodeId;
    }

    public void setNodeId( int id ) {
        refNodeId = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label != null ? label : UUID.randomUUID().toString();
    }


    @Override
    public ImperfectEvaluatorConstraint clone() {
        ImperfectEvaluatorConstraint iec;
        if (isLiteral()) {
            iec = new ImperfectEvaluatorConstraint( field, evaluator, rightReadAccessor, label );
        } else {
            Declaration[] clonedDeclarations = new Declaration[ declarations.length ];
            System.arraycopy( declarations, 0, clonedDeclarations, 0, declarations.length );
            iec = new ImperfectEvaluatorConstraint( clonedDeclarations, evaluator, rightReadAccessor, label );
        }
        iec.setCutting( this.cutting );
        iec.setLabel( this.label );
        iec.setOwningNodeId( this.getOwningNodeId() );
        return iec;
    }

    @Override
    public String toString() {
        return "ImperfectEvaluatorConstraint{"
                + this.getRightReadAccessor().getNativeReadMethodName()
                + this.getEvaluator().getOperator().getOperatorString()
                + ( this.getField() != null ? this.getField() : this.getRequiredDeclarations() )
        + "}";
    }
}
