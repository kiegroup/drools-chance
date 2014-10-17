package org.drools.chance.reteoo;


import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.reteoo.tuples.ImperfectTuple;
import org.drools.common.AgendaItem;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.spi.PropagationContext;

public class ChanceActivation extends AgendaItem implements ChanceAgendaItem {
    
    private Evaluation evaluation;

    public ChanceActivation() {
    }

    public ChanceActivation( long activationNumber, LeftTuple tuple, int salience, PropagationContext context, RuleTerminalNode rtn ) {
        super(activationNumber, tuple, salience, context, rtn);
        evaluation = ((ImperfectTuple) tuple).getEvaluation();
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
    
    public Degree getDegree() {
        return getEvaluation() != null ? getEvaluation().getDegree() : null;
    }
}
