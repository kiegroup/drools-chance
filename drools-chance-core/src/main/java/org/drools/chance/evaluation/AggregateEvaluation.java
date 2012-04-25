package org.drools.chance.evaluation;


public interface AggregateEvaluation extends DelayedEvaluation {

    public void notifyChange( Evaluation child );
}
