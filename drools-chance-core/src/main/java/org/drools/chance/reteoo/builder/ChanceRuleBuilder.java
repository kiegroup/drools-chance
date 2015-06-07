package org.drools.chance.reteoo.builder;

import org.drools.core.reteoo.builder.ReteooRuleBuilder;

public class ChanceRuleBuilder extends ReteooRuleBuilder {

    public ChanceRuleBuilder() {
        this.utils.addBuilder( ChanceGroupElement.class,
                new ChanceGroupElementBuilder() );
    }

}
