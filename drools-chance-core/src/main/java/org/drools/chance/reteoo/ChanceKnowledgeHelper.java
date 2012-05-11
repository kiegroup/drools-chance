package org.drools.chance.reteoo;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.chance.ChanceHelper;
import org.drools.chance.factmodel.ImperfectTraitProxy;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.factmodel.traits.Thing;


public class ChanceKnowledgeHelper extends DefaultKnowledgeHelper implements ChanceHelper {

    public ChanceKnowledgeHelper( WorkingMemory workingMemory ) {
        super( workingMemory );
    }

    public Degree getDegree() {
        return ((ChanceAgendaItem) getActivation()).getDegree();
    }

    public Degree getDegree( String label ) {
        if ( label == null || label.isEmpty() ) {
            return getDegree();
        }
        return getEvaluation( label ).getDegree();
    }

    public Evaluation getEvaluation() {
        return ((ChanceAgendaItem) getActivation()).getEvaluation();
    }

    public Evaluation getEvaluation( String label ) {
        if ( label == null || label.isEmpty() ) {
            return getEvaluation();
        }
        return getEvaluation().lookupLabel( label );
    }

    public <T, K> T don( Thing<K> core, Class<T> trait, Degree deg ) {
        return don( core.getCore(), trait, deg );
    }

    public <T, K> T don( K core, Class<T> trait, Degree deg ) {
        T thing = applyTrait(core, trait);

        ((ImperfectTraitProxy) thing).setDegree( deg );

        return doInsertTrait( thing, false );

    }





}
