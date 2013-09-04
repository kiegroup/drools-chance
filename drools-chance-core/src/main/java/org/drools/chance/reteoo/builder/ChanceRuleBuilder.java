package org.drools.chance.reteoo.builder;


import org.drools.reteoo.builder.*;
import org.drools.rule.*;

public class ChanceRuleBuilder extends ReteooRuleBuilder {

    public ChanceRuleBuilder() {
        this.utils = new BuildUtils();

        this.utils.addBuilder( ChanceGroupElement.class,
                new ChanceGroupElementBuilder() );
        this.utils.addBuilder( GroupElement.class,
                new GroupElementBuilder() );
        this.utils.addBuilder( Pattern.class,
                new ChancePatternBuilder() );
        this.utils.addBuilder( EvalCondition.class,
                new EvalBuilder() );
        this.utils.addBuilder( QueryElement.class,
                new QueryElementBuilder() );
        this.utils.addBuilder( From.class,
                new FromBuilder() );
        this.utils.addBuilder( Collect.class,
                new CollectBuilder() );
        this.utils.addBuilder( Accumulate.class,
                new AccumulateBuilder() );
        this.utils.addBuilder( Forall.class,
                new ForallBuilder() );
        this.utils.addBuilder( EntryPoint.class,
                new EntryPointBuilder() );
        this.utils.addBuilder( WindowReference.class,
                new WindowReferenceBuilder() );
    }

}
