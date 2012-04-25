package org.drools.chance.reteoo;

import org.drools.WorkingMemory;
import org.drools.base.SequentialKnowledgeHelper;
import org.drools.chance.ChanceHelper;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.factmodel.traits.Thing;
import org.drools.spi.KnowledgeHelper;


public class ChanceSequentialKnowledgeHelper extends SequentialKnowledgeHelper implements ChanceHelper {

    public ChanceSequentialKnowledgeHelper( WorkingMemory wm ) {
        super( wm );
    }

    public Degree getDegree() {
        return ((ChanceActivation) getActivation()).getDegree();
    }

    public Degree getDegree( String label ) {
        if ( label == null || label.isEmpty() ) {
            return getDegree();
        }
        return getEvaluation( label ).getDegree();
    }

    public Evaluation getEvaluation() {
        return ((ChanceActivation) getActivation()).getEvaluation();
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
