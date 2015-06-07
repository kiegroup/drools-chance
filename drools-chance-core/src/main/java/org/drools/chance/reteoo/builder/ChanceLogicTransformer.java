package org.drools.chance.reteoo.builder;

import org.drools.core.rule.GroupElement;
import org.drools.core.rule.LogicTransformer;


public class ChanceLogicTransformer extends LogicTransformer {

    public ChanceLogicTransformer() {
        super();
    }

    // do not split Or
    protected GroupElement[] splitOr( final GroupElement cloned ) {
        return new GroupElement[] { cloned };
    }


}
