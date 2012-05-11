package org.drools.chance.reteoo;


import org.drools.chance.core.util.IntHashMap;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.common.EventFactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class ChanceEventFactHandle extends EventFactHandle implements ChanceFactHandle {

    private IntHashMap<Evaluation> cachedEvaluations = new IntHashMap<Evaluation>();

    public ChanceEventFactHandle() {
    }

    public ChanceEventFactHandle(int id, Object object, long recency, long timestamp, long duration, WorkingMemoryEntryPoint wmEntryPoint) {
        super(id, object, recency, timestamp, duration, wmEntryPoint);
    }

    public Evaluation getCachedEvaluation( int key, boolean compensateNodeSkips ) {
        Evaluation eval = cachedEvaluations.get( key );
        if ( compensateNodeSkips && eval == null ) {
            eval = new MockEvaluation( key, SimpleDegree.TRUE );
            addEvaluation( key, eval );
        }
        return eval;
    }

    public Evaluation getCachedEvaluation( int key ) {
        return cachedEvaluations.get( key );
    }

    public boolean isEvaluationCached( int key ) {
        return cachedEvaluations.containsKey( key );
    }

    public void addEvaluation( int key, Evaluation eval ) {
        if ( ! cachedEvaluations.containsKey( key ) ) {
            cachedEvaluations.put( key, eval );
        } else {
            cachedEvaluations.get( key ).merge( eval );
        }
    }

    public void setEvaluation( int key, Evaluation eval ) {
        cachedEvaluations.put( key, eval );
    }
}
