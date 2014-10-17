package org.drools.chance.reteoo.tuples;


import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.core.util.IntHashMap;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.BaseLeftTuple;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.RightTuple;

public class ImperfectLeftTuple extends BaseLeftTuple implements ImperfectTuple {

    protected IntHashMap<Evaluation> evaluations = new IntHashMap<Evaluation>();
    protected Evaluation lastEvaluation;

    public ImperfectLeftTuple() {
    }

    public ImperfectLeftTuple(InternalFactHandle factHandle, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        super(factHandle, sink, leftTupleMemoryEnabled);
    }

    public ImperfectLeftTuple(LeftTuple leftTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        super(leftTuple, sink, leftTupleMemoryEnabled);
    }

    public ImperfectLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink) {
        super(leftTuple, rightTuple, sink);
    }

    public ImperfectLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        super(leftTuple, rightTuple, sink, leftTupleMemoryEnabled);
    }

    public ImperfectLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTuple currentLeftChild, LeftTuple currentRightChild, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        super(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
    }

    public Evaluation getEvaluation() {
        return lastEvaluation;
    }

    public Evaluation getCachedEvaluation( int idx ) {
        return evaluations.get( idx );
    }

    public void setEvaluation( Evaluation evaluation ) {
        addEvaluation( evaluation );
    }

    public void addEvaluation( Evaluation evaluation ) {
        this.evaluations.put( evaluation.getNodeId(), evaluation );
        this.lastEvaluation = evaluation;
    }

    public Degree getDegree() {
        return getEvaluation().getDegree();
    }

    public int getSourceId() {
        return getEvaluation().getNodeId();
    }
}
