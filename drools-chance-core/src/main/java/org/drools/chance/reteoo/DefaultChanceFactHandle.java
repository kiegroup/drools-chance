package org.drools.chance.reteoo;


import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.Evaluations;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.chance.core.util.IntHashMap;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.kie.api.runtime.rule.EntryPoint;

public class DefaultChanceFactHandle extends DefaultFactHandle implements ChanceFactHandle {

    private Evaluations cachedEvaluations = new Evaluations();

    public DefaultChanceFactHandle( int id, Object object) {
        super( id, object );
    }

    public DefaultChanceFactHandle( int id, Object object, long recency, EntryPoint wmEntryPoint, boolean isTrait ) {
        super( id, object, recency, wmEntryPoint, isTrait );
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
