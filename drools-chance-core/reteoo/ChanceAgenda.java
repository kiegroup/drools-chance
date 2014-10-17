package org.drools.chance.reteoo;

import org.drools.base.SequentialKnowledgeHelper;
import org.drools.common.*;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.spi.PropagationContext;


public class ChanceAgenda extends DefaultAgenda {

    public ChanceAgenda() {
        super();
    }

    public ChanceAgenda( InternalRuleBase ruleBase ) {
        super( ruleBase );
    }

    public ChanceAgenda( InternalRuleBase ruleBase, boolean initMain ) {
        super( ruleBase, initMain );
    }

    @Override
    public AgendaItem createAgendaItem( LeftTuple tuple,
                                        int salience,
                                        PropagationContext context,
                                        RuleTerminalNode rtn ) {
        return new ChanceActivation( activationCounter++,
                tuple,
                salience,
                context,
                rtn );
    }


    public ScheduledAgendaItem createScheduledAgendaItem( final LeftTuple tuple,
                                                          final PropagationContext context,
                                                          final RuleTerminalNode rtn) {
        return new ChanceScheduledAgendaItem( activationCounter++,
                tuple,
                this,
                context,
                rtn );
    }



}
