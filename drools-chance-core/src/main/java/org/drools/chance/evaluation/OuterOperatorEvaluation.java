package org.drools.chance.evaluation;

import org.drools.chance.degree.Degree;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;

public class OuterOperatorEvaluation extends CompositeEvaluation {
    public OuterOperatorEvaluation( int nodeId, Degree degree, ConnectiveCore op, Evaluation[] children ) {
        super( nodeId, degree, op, children );
    }

    public OuterOperatorEvaluation( int nodeId, String expression, Degree degree, ConnectiveCore op, Evaluation[] children ) {
        super( nodeId, expression, degree, op, children );
    }

    public OuterOperatorEvaluation( int nodeId, String expression, Degree degree, ConnectiveCore op, Evaluation[] children, String label ) {
        super( nodeId, expression, degree, op, children, label );
    }

    @Override
    public String toString() {
        return "Outer " + super.toString();
    }

    @Override
    public boolean isOuter() {
        return true;
    }

    public Evaluation attach( Evaluation other ) {
        OuterOperatorEvaluation op = this;
        while ( op.getNext() != null && op.getNext().isOuter() ) {
            op = (OuterOperatorEvaluation) op.getNext();
        }
        Evaluation after = op.getNext();
        op.setNext( other );
        other.setNext( after );
        return this;
    }
}
