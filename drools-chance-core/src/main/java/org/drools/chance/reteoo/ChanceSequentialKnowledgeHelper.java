package org.drools.chance.reteoo;

import org.drools.chance.ChanceHelper;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.core.WorkingMemory;
import org.drools.core.base.SequentialKnowledgeHelper;
import org.drools.core.factmodel.traits.Thing;


public class ChanceSequentialKnowledgeHelper extends SequentialKnowledgeHelper implements ChanceHelper {

    public ChanceSequentialKnowledgeHelper( WorkingMemory wm ) {
        super( wm );
    }

    public Degree getDegree() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Degree getDegree( String label ) {
        if ( label == null || label.isEmpty() ) {
            return getDegree();
        }
        return getEvaluation( label ).getDegree();
    }

    public Evaluation getEvaluation() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Evaluation getEvaluation( String label ) {
        if ( label == null || label.isEmpty() ) {
            return getEvaluation();
        }
        return getEvaluation().lookupLabel( label );
    }

    public <T, K> T don(K core, Class<T> trait, Degree deg) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public <T, K> T don(Thing<K> core, Class<T> trait, Degree deg) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
