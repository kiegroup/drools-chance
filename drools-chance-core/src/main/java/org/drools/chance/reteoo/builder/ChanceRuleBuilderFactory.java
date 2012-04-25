package org.drools.chance.reteoo.builder;

import org.drools.reteoo.RuleBuilder;
import org.drools.reteoo.RuleBuilderFactory;


public class ChanceRuleBuilderFactory implements RuleBuilderFactory {
    
    public RuleBuilder newRuleBuilder() {
        return new ChanceRuleBuilder();
    }
    
}
