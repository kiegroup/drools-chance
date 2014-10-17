package org.drools.chance.evaluation;


import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;

public class DelayedEvaluationImpl extends SimpleEvaluationImpl implements DelayedEvaluation {


    public DelayedEvaluationImpl( int nodeId ) {
        super( nodeId, SimpleDegree.TRUE );
    }

    public void merge( Evaluation other ) {
        boolean diff = ! this.getDegree().equals( other.getDegree() );

        setDegree( other.getDegree() );
        setExpression( other.getExpression() );
        setLabel( other.getLabel() );

        if ( getParent() != null && diff ) {
            getParent().notifyChange( this );
        }
    }

    @Override
    public String toString() {
        return "??"  + "@" + getNodeId() + "// DelayedEvaluation{" +
                + getNodeId() + ") :[ " + getExpression() + "] >> " + getDegree();
    }
}
