package org.drools.chance.reteoo.builder;

import org.drools.rule.*;

import java.util.Iterator;


public class ChanceLogicTransformer extends LogicTransformer {

    public ChanceLogicTransformer() {
        super();
    }


    // do not split Or
    protected GroupElement[] splitOr( final GroupElement cloned ) {
        return new GroupElement[] { cloned };
    }


}
