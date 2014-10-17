package org.drools.chance.rule.constraint;


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


    public ImperfectEvaluatorConstraint( ) {
    }

    public ImperfectEvaluatorConstraint( FieldValue field, Evaluator evaluator, InternalReadAccessor extractor ) {
        super( field, evaluator, extractor );
    }

    public ImperfectEvaluatorConstraint( Declaration[] declarations, Evaluator evaluator, InternalReadAccessor extractor ) {
        super( declarations, evaluator, extractor );
    }


    public Degree match( InternalFactHandle factHandle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        ImperfectEvaluator evaluator = (ImperfectEvaluator) getEvaluator();

        if ( isLiteral() ) {
            return evaluator.match( workingMemory, rightReadAccessor, factHandle, field );
        }

        return evaluator.match(
                workingMemory,
                rightReadAccessor,
                factHandle,
                declarations[0].getExtractor(),
                factHandle );
    }


    public Degree matchCachedLeft( ContextEntry context, InternalFactHandle handle ) {
        if (isLiteral()) {
            return ( (ImperfectEvaluator) evaluator).match( ((LiteralContextEntry) context).workingMemory,
                    ((LiteralContextEntry) context).getFieldExtractor(),
                    handle,
                    field );
        }

        return ( (ImperfectEvaluator) evaluator).matchCachedLeft( ((VariableRestriction.VariableContextEntry) context).workingMemory,
                (VariableRestriction.VariableContextEntry) context,
                handle );
    }

    public Degree matchCachedRight(LeftTuple tuple, ContextEntry context) {
        if ( isLiteral() ) {
            return ((ImperfectEvaluator)evaluator).match( ((LiteralContextEntry) context).workingMemory,
                    ((LiteralContextEntry) context).getFieldExtractor(),
                    ((LiteralContextEntry) context).getFactHandle(),
                    field );
        }

        return ((ImperfectEvaluator)evaluator).matchCachedRight(
                ((VariableRestriction.VariableContextEntry) context).workingMemory,
                (VariableRestriction.VariableContextEntry) context,
                tuple.get(declarations[0]) );
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

    public void setLabel(String label) {
        this.label = label;
    }

}
