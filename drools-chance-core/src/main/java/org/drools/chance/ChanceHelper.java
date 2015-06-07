package org.drools.chance;


import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.spi.KnowledgeHelper;

public interface ChanceHelper extends KnowledgeHelper {


    public Degree getDegree();
    
    public Degree getDegree( String label );

    public Evaluation getEvaluation();
    
    public Evaluation getEvaluation( String label );

}

