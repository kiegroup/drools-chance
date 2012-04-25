package org.drools.chance.reteoo;


import org.drools.chance.evaluation.Evaluation;

public interface ChanceFactHandle {

    public Evaluation getCachedEvaluation( int key );

    public Evaluation getCachedEvaluation( int key, boolean compensateNodeSkips );

    public boolean isEvaluationCached( int key );

    public void addEvaluation( int key, Evaluation eval );


}
