package org.drools.chance;


import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.core.util.ImperfectTripleFactory;
import org.drools.chance.factmodel.*;
import org.drools.chance.rule.constraint.core.connectives.factories.fuzzy.mvl.ManyValuedConnectiveFactory;
import org.drools.chance.rule.constraint.core.evaluators.HoldsEvaluatorDefinition;
import org.drools.chance.rule.constraint.core.evaluators.linguistic.IsEvaluatorDefinition;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.rule.builder.DroolsCompilerComponentFactory;
import org.drools.compiler.rule.builder.dialect.java.JavaRuleBuilderHelper;
import org.drools.compiler.rule.builder.dialect.mvel.MVELConsequenceBuilder;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.evaluators.IsAEvaluatorDefinition;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.reteoo.KieComponentFactory;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.mvel2.Macro;

public class Chance {


    public static void initialize() {

        ChanceStrategyFactory.initDefaults();

        /*
        JavaDialect.setPatternBuilder( new ChanceRulePatternBuilder() );
        JavaDialect.setGEBuilder( new ChanceRuleGroupElementBuilder() );
        JavaDialect.reinitBuilder();

        MVELDialect.setPatternBuilder( new ChanceRulePatternBuilder() );
        MVELDialect.setGEBuilder( new ChanceRuleGroupElementBuilder() );
        MVELDialect.reinitBuilder();
        */

        //JavaRuleBuilderHelper.setConsequenceTemplate( "chanceRule.mvel" );

        MVELConsequenceBuilder.macros.put( "chance",
                new Macro() {
                    public String doMacro() {
                        return "((org.drools.chance.ChanceHelper) drools)";
                    }
                } );


        ChanceStrategyFactory.setDefaultFactory( new ManyValuedConnectiveFactory() );

    }



    public static void restoreDefaults() {
        /*
        JavaDialect.setPatternBuilder( new PatternBuilder() );
        JavaDialect.setGEBuilder( new GroupElementBuilder() );
        JavaDialect.reinitBuilder();

        MVELDialect.setPatternBuilder( new PatternBuilder() );
        MVELDialect.setGEBuilder( new GroupElementBuilder() );
        MVELDialect.reinitBuilder();
        */

        JavaRuleBuilderHelper.setConsequenceTemplate( "javaRule.mvel" );
    }


    public static KnowledgeBuilderConfiguration getChanceKBuilderConfiguration() {
        return getChanceKBuilderConfiguration( new KnowledgeBuilderConfigurationImpl() );
    }

    public static KnowledgeBuilderConfiguration getChanceKBuilderConfiguration( KnowledgeBuilderConfiguration baseConf ) {
        /*
        PackageBuilderConfiguration pbc = (PackageBuilderConfiguration) baseConf;

        pbc.getEvaluatorRegistry().addEvaluatorDefinition( new IsEvaluatorDefinition() );
        pbc.getEvaluatorRegistry().addEvaluatorDefinition( new IsAEvaluatorDefinition() );
        pbc.getEvaluatorRegistry().addEvaluatorDefinition( new HoldsEvaluatorDefinition() );
        pbc.getEvaluatorRegistry().addEvaluatorDefinition( new ImperfectBaseEvaluatorDefinition() );

        DroolsCompilerComponentFactory dcf = new DroolsCompilerComponentFactory();
        dcf.setConstraintBuilderFactoryProvider( new ChanceConstraintBuilderFactory() );
        dcf.setExpressionProcessor( new ChanceMVELDumper() );
        dcf.setFieldDataFactory( new ChanceFieldFactory() );

        ClassBuilderFactory cbf = new ClassBuilderFactory();
        cbf.setBeanClassBuilder( new ChanceBeanBuilderImpl() );
        cbf.setTraitBuilder( new ChanceTraitBuilderImpl() );
        cbf.setTraitProxyBuilder( new ChanceTripleProxyBuilderImpl() );
        cbf.setPropertyWrapperBuilder( new ChanceTriplePropertyWrapperClassBuilderImpl() );
        cbf.setEnumClassBuilder( new ChanceEnumBuilderImpl() );

        pbc.setClassBuilderFactory( cbf );

        pbc.setComponentFactory( dcf );
        */
        return baseConf;
    }




    public static KieBaseConfiguration getChanceKnowledgeBaseConfiguration( KieBaseConfiguration baseConf ) {
        RuleBaseConfiguration rbc = (RuleBaseConfiguration) baseConf;

        KieComponentFactory rcf = new KieComponentFactory();

        //rbc.addActivationListener( "query", new ChanceQueryActivationListenerFactory() );
        /*
        rcf.setHandleFactoryProvider( new ChanceFactHandleFactory() );
        rcf.setNodeFactoryProvider( new ChanceNodeFactory() );
        rcf.setRuleBuilderProvider( new ChanceRuleBuilderFactory() );
        rcf.setAgendaFactory( new ChanceAgendaFactory() );
        rcf.setFieldDataFactory( new ChanceFieldFactory() );
        rcf.setTripleFactory( new ImperfectTripleFactory() );
        rcf.setKnowledgeHelperFactory( new ChanceKnowledgeHelperFactory() );
        rcf.setLogicTransformerFactory( new ChanceLogicTransformerFactory() );
        rcf.setBaseTraitProxyClass( ImperfectTraitProxy.class );
        */

        ClassBuilderFactory cbf = new ClassBuilderFactory();
        cbf.setBeanClassBuilder( new ChanceBeanBuilderImpl() );
        cbf.setTraitBuilder( new ChanceTraitBuilderImpl() );
        cbf.setTraitProxyBuilder( new ChanceTripleProxyBuilderImpl() );
        cbf.setPropertyWrapperBuilder( new ChanceTriplePropertyWrapperClassBuilderImpl() );
        cbf.setEnumClassBuilder( new ChanceEnumBuilderImpl() );

        rcf.setClassBuilderFactory( cbf );

        rbc.setComponentFactory( rcf );

        return rbc;
    }

    public static KieBaseConfiguration getChanceKnowledgeBaseConfiguration() {
        return getChanceKnowledgeBaseConfiguration( KnowledgeBaseFactory.newKnowledgeBaseConfiguration() );
    }



    public static KieSessionConfiguration getChanceKnowledgeSessionConfiguration( KieSessionConfiguration baseConf ) {
        return baseConf;
    }

    public static KieSessionConfiguration getChanceKnowledgeSessionConfiguration( ) {
        return getChanceKnowledgeSessionConfiguration( KnowledgeBaseFactory.newKnowledgeSessionConfiguration() );
    }

}
