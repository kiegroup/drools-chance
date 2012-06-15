package org.drools.chance.rule.builder;


import org.drools.rule.builder.ConstraintBuilder;
import org.drools.rule.builder.ConstraintBuilderFactory;

public class ChanceConstraintBuilderFactory implements ConstraintBuilderFactory {

    private static ConstraintBuilder cBuilder = new ChanceMVELConstraintBuilder();

    public ConstraintBuilder newConstraintBuilder() {
        return cBuilder;
    }
}
