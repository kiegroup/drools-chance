package org.drools.chance.reteoo;


import org.drools.chance.core.util.IntHashMap;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.Evaluations;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.EventFactHandle;
import org.kie.api.runtime.rule.EntryPoint;

public class ChanceEventFactHandle extends EventFactHandle implements ChanceFactHandle {

    private Evaluations cachedEvaluations = new Evaluations();

    public ChanceEventFactHandle(int id, Object object, long recency, long timestamp, long duration, EntryPoint wmEntryPoint, boolean isTrait ) {
        super( id, object, recency, timestamp, duration, wmEntryPoint, isTrait );
    }

    public Evaluation getCachedEvaluation( int key ) {
        return cachedEvaluations.getCachedEvaluation( key );
    }

    public boolean isEvaluationCached( int key ) {
        return cachedEvaluations.isEvaluationCached( key );
    }

    public void addEvaluation( int key, Evaluation eval ) {
        cachedEvaluations.addEvaluation( key, eval );
    }

    public void setEvaluation( int key, Evaluation eval ) {
        cachedEvaluations.setEvaluation( key, eval );
    }
}
