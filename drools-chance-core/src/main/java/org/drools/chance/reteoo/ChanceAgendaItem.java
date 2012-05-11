package org.drools.chance.reteoo;


import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;

public interface ChanceAgendaItem {

    public Evaluation getEvaluation();

    public void setEvaluation( Evaluation evaluation );

    public Degree getDegree();

}
