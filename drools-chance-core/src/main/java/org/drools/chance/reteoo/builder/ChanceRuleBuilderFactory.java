package org.drools.chance.reteoo.builder;

import org.drools.core.reteoo.RuleBuilderFactory;
import org.drools.core.reteoo.builder.ReteooRuleBuilder;

public class ChanceRuleBuilderFactory implements RuleBuilderFactory {
    
    public ReteooRuleBuilder newRuleBuilder() {
        return new ChanceRuleBuilder();
    }
    
}
