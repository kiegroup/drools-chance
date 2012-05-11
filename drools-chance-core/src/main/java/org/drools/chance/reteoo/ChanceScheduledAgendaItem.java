package org.drools.chance.reteoo;

import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.reteoo.tuples.ImperfectTuple;
import org.drools.common.ScheduledAgendaItem;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.spi.PropagationContext;


public class ChanceScheduledAgendaItem extends ScheduledAgendaItem implements ChanceAgendaItem {

    private Evaluation evaluation;

    public ChanceScheduledAgendaItem( int i, LeftTuple tuple, ChanceAgenda chanceAgenda, PropagationContext context, RuleTerminalNode rtn ) {
        super( i, tuple, chanceAgenda, context, rtn );
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


    @Override
    public String toString() {
        return "ChanceScheduledAgendaItem{" +
                "evaluation=" + evaluation +
                "} " + super.toString();
    }
}
