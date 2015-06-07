/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.chance.evaluation;

import org.drools.chance.core.util.IntHashMap;

public class Evaluations {

    private IntHashMap<Evaluation> cachedEvaluations = new IntHashMap<Evaluation>();

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
            cachedEvaluations.put( key, cachedEvaluations.get( key ).attach( eval ) );
        }
    }

    public void setEvaluation( int key, Evaluation eval ) {
        cachedEvaluations.put( key, eval );
    }
}
