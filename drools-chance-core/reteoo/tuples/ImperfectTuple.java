package org.drools.chance.reteoo.tuples;


import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;

public interface ImperfectTuple {
    
    public Evaluation getEvaluation();

    public Evaluation getCachedEvaluation( int idx );

    public void addEvaluation( Evaluation eval );

    public void setEvaluation( Evaluation eval );

    public Degree getDegree();

    public int getSourceId();
    
}

