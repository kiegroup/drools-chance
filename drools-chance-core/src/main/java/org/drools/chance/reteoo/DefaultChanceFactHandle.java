package org.drools.chance.reteoo;


import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.chance.core.util.IntHashMap;
import org.drools.common.DefaultFactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class DefaultChanceFactHandle extends DefaultFactHandle implements ChanceFactHandle {

    private IntHashMap<Evaluation> cachedEvaluations = new IntHashMap<Evaluation>();

    public DefaultChanceFactHandle() {
    }

    public DefaultChanceFactHandle(int id, Object object) {
        super(id, object);
    }

    public DefaultChanceFactHandle(int id, Object object, long recency, WorkingMemoryEntryPoint wmEntryPoint) {
        super(id, object, recency, wmEntryPoint);
    }

    public DefaultChanceFactHandle(int id, int identityHashCode, Object object, long recency, WorkingMemoryEntryPoint wmEntryPoint) {
        super(id, identityHashCode, object, recency, wmEntryPoint);
    }

    public DefaultChanceFactHandle(int id, String wmEntryPointId, int identityHashCode, int objectHashCode, long recency, Object object) {
        super(id, wmEntryPointId, identityHashCode, objectHashCode, recency, object);
    }

    public DefaultChanceFactHandle(String externalFormat) {
        super(externalFormat);
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

}
