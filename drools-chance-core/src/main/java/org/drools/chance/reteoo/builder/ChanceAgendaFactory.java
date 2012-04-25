package org.drools.chance.reteoo.builder;


import org.drools.chance.reteoo.ChanceAgenda;
import org.drools.common.AgendaFactory;
import org.drools.common.DefaultAgenda;
import org.drools.common.InternalRuleBase;

public class ChanceAgendaFactory implements AgendaFactory {

    public DefaultAgenda createAgenda( InternalRuleBase ruleBase, boolean initMain ) {
        return new ChanceAgenda( ruleBase, initMain );
    }

    public DefaultAgenda createAgenda( InternalRuleBase ruleBase ) {
        return new ChanceAgenda( ruleBase );
    }
}
