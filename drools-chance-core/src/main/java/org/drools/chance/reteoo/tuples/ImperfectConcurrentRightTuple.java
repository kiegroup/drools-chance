package org.drools.chance.reteoo.tuples;

import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ConcurrentRightTuple;
import org.drools.reteoo.RightTupleSink;


public class ImperfectConcurrentRightTuple extends ConcurrentRightTuple {

    protected Evaluation evaluation;

    public ImperfectConcurrentRightTuple() {
    }

    public ImperfectConcurrentRightTuple( InternalFactHandle handle, RightTupleSink sink, Evaluation eval ) {
        super( handle, sink );
        this.evaluation = eval;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public Degree getDegree() {
        return getEvaluation().getDegree();
    }

    public int getSourceId() {
        return getEvaluation().getNodeId();
    }
}
