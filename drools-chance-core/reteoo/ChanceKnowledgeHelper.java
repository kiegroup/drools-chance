package org.drools.chance.reteoo;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.chance.ChanceHelper;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.factmodel.ImperfectTraitProxy;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
//import org.drools.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.factmodel.traits.LogicalTypeInconsistencyException;
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

    public <T, K> T don( Thing<K> core, Class<T> trait ) {
        return don( core.getCore(), trait, SimpleDegree.TRUE );
    }

    public <T, K> T don( K core, Class<T> trait ) {
        return don( core, trait, SimpleDegree.TRUE );
    }

    public <T, K> T don( Thing<K> core, Class<T> trait, Degree deg ) {
        return don( core.getCore(), trait, deg );
    }

    public <T, K> T don( K core, Class<T> trait, Degree deg ) {
        return don( core, trait, deg, false );
    }

    public <T, K> T don( K core, Class<T> trait, Degree deg, boolean logical ) {
        if ( core instanceof Thing && ( (Thing) core ).getCore() != core ) {
            return don( ((Thing) core).getCore(), trait, deg, logical );
        }
        try {
            T thing = applyTrait( core, trait, deg, logical );
            return thing;
        } catch ( LogicalTypeInconsistencyException ltie ) {
            ltie.printStackTrace();
            return null;
        }
    }

    protected <T> void configureTrait( T thing, Object value ) {
        ((ImperfectTraitProxy) thing).setDegree( (Degree) value );
    }



}
