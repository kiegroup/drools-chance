package org.drools.chance.reteoo.tuples;


import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RuleTerminalNodeLeftTuple;

public class ImperfectRuleTerminalNodeLeftTuple extends RuleTerminalNodeLeftTuple implements ImperfectTuple {

    private Evaluation evaluation;

    public ImperfectRuleTerminalNodeLeftTuple() {
    }

    public ImperfectRuleTerminalNodeLeftTuple(InternalFactHandle factHandle, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        super(factHandle, sink, leftTupleMemoryEnabled);
    }

    public ImperfectRuleTerminalNodeLeftTuple(LeftTuple leftTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        super(leftTuple, sink, leftTupleMemoryEnabled);
    }

    public ImperfectRuleTerminalNodeLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink) {
        super(leftTuple, rightTuple, sink);
    }

    public ImperfectRuleTerminalNodeLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        super(leftTuple, rightTuple, sink, leftTupleMemoryEnabled);
    }

    public ImperfectRuleTerminalNodeLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTuple currentLeftChild, LeftTuple currentRightChild, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        super(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public Evaluation getCachedEvaluation( int idx ) {
        return evaluation.getNodeId() == idx ? evaluation : null;
    }

    public void setEvaluation( Evaluation evaluation ) {
        this.evaluation = evaluation;
    }

    public void addEvaluation( Evaluation evaluation ) {
        if ( this.evaluation.getNodeId() == evaluation.getNodeId() ) {
            this.evaluation = evaluation;
        } else {
//        this.evaluation = evaluation;
            throw new UnsupportedOperationException( "Should not be called, no more evals at RuleTerminalNode?" );
        }
    }

    public Degree getDegree() {
        return getEvaluation().getDegree();
    }

    public int getSourceId() {
        return getEvaluation().getNodeId();
    }
}
