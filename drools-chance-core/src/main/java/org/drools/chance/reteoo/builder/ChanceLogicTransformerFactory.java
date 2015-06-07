package org.drools.chance.reteoo.builder;

import org.drools.core.rule.LogicTransformer;
import org.drools.core.rule.LogicTransformerFactory;

public class ChanceLogicTransformerFactory implements LogicTransformerFactory {

    @Override
    public LogicTransformer getLogicTransformer() {
        return new ChanceLogicTransformer();
    }

}
