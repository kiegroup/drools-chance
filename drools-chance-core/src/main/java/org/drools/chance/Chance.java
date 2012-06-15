package org.drools.chance;


import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.rule.builder.ChanceConstraintBuilderFactory;
import org.drools.chance.rule.builder.ChanceMVELDumper;
import org.drools.chance.rule.constraint.core.connectives.factories.fuzzy.mvl.ManyValuedConnectiveFactory;
import org.drools.chance.rule.constraint.core.evaluators.HoldsEvaluatorDefinition;
import org.drools.chance.rule.constraint.core.evaluators.ImperfectBaseEvaluatorDefinition;
import org.drools.chance.rule.constraint.core.evaluators.IsAEvaluatorDefinition;
import org.drools.chance.rule.constraint.core.evaluators.linguistic.IsEvaluatorDefinition;
import org.drools.chance.core.util.ImperfectTripleFactory;
import org.drools.chance.factmodel.*;
import org.drools.chance.reteoo.ChanceFactHandleFactory;
import org.drools.chance.reteoo.ChanceFieldFactory;
import org.drools.chance.reteoo.builder.*;
import org.drools.chance.rule.builder.ChanceRulePatternBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.factmodel.ClassBuilderFactory;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.factmodel.traits.TraitProxy;
import org.drools.reteoo.ReteooComponentFactory;
import org.drools.rule.builder.DroolsCompilerComponentFactory;
import org.drools.rule.builder.GroupElementBuilder;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper;
import org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilder;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.mvel2.Macro;

public class Chance {




    public static void initialize() {

        ChanceStrategyFactory.initDefaults();

        JavaDialect.setPatternBuilder( new ChanceRulePatternBuilder() );
        JavaDialect.setGEBuilder( new ChanceRuleGroupElementBuilder() );
        JavaDialect.reinitBuilder();

        MVELDialect.setPatternBuilder( new ChanceRulePatternBuilder() );
        MVELDialect.setGEBuilder( new ChanceRuleGroupElementBuilder() );
        MVELDialect.reinitBuilder();

        JavaRuleBuilderHelper.setConsequenceTemplate( "chanceRule.mvel" );
        MVELConsequenceBuilder.macros.put( "chance",
                new Macro() {
                    public String doMacro() {
                        return "((org.drools.chance.ChanceHelper) drools)";
                    }
                } );


        ClassBuilderFactory.setBeanClassBuilderService(new ChanceBeanBuilderImpl());
        ClassBuilderFactory.setTraitBuilderService( new ChanceTraitBuilderImpl() );
        ClassBuilderFactory.setTraitProxyBuilderService( new ChanceTripleProxyBuilderImpl() );
        ClassBuilderFactory.setPropertyWrapperBuilderService( new ChanceTriplePropertyWrapperClassBuilderImpl() );
        ClassBuilderFactory.setEnumClassBuilderService( new ChanceEnumBuilderImpl() );

        DroolsCompilerComponentFactory.setConstraintBuilderFactoryProvider( new ChanceConstraintBuilderFactory() );
        DroolsCompilerComponentFactory.setExpressionProcessor( new ChanceMVELDumper() );

        ReteooComponentFactory.setHandleFactoryProvider( new ChanceFactHandleFactory() );
        ReteooComponentFactory.setNodeFactoryProvider( new ChanceNodeFactory() );
        ReteooComponentFactory.setRuleBuilderProvider( new ChanceRuleBuilderFactory() );
        ReteooComponentFactory.setAgendaFactory( new ChanceAgendaFactory() );
        ReteooComponentFactory.setFieldDataFactory( new ChanceFieldFactory() );
        ReteooComponentFactory.setTripleFactory( new ImperfectTripleFactory() );
        ReteooComponentFactory.setKnowledgeHelperFactory( new ChanceKnowledgeHelperFactory() );
        ReteooComponentFactory.setLogicTransformer( new ChanceLogicTransformer() );

        ChanceStrategyFactory.setDefaultFactory( new ManyValuedConnectiveFactory() );

        TraitFactory.proxyBaseClass = ImperfectTraitProxy.class;

    }



    public static void restoreDefaults() {


        JavaDialect.setPatternBuilder( new PatternBuilder() );
        JavaDialect.setGEBuilder( new GroupElementBuilder() );
        JavaDialect.reinitBuilder();

        MVELDialect.setPatternBuilder( new PatternBuilder() );
        MVELDialect.setGEBuilder( new GroupElementBuilder() );
        MVELDialect.reinitBuilder();

        JavaRuleBuilderHelper.setConsequenceTemplate( "javaRule.mvel" );


        ClassBuilderFactory.setDefaultBeanClassBuilderService();
        ClassBuilderFactory.setDefaultTraitBuilderService();
        ClassBuilderFactory.setDefaultTraitProxyBuilderService();
        ClassBuilderFactory.setDefaultPropertyWrapperBuilderService();
        ClassBuilderFactory.setDefaultEnumClassBuilderService();

        DroolsCompilerComponentFactory.setDefaultConstraintBuilderFactoryProvider();
        DroolsCompilerComponentFactory.setDefaultExpressionProcessor();

        ReteooComponentFactory.setDefaultHandleFactoryProvider();
        ReteooComponentFactory.setDefaultNodeFactoryProvider();
        ReteooComponentFactory.setDefaultRuleBuilderProvider();
        ReteooComponentFactory.setDefaultAgendaFactory();
        ReteooComponentFactory.setDefaultFieldFactory();
        ReteooComponentFactory.setDefaultTripleFactory();
        ReteooComponentFactory.setDefaultKnowledgeHelperFactory();
        ReteooComponentFactory.setDefaultLogicTransformer();

        TraitFactory.proxyBaseClass = TraitProxy.class;

    }


    public static KnowledgeBuilderConfiguration getChanceKBuilderConfiguration() {
        return getChanceKBuilderConfiguration( new PackageBuilderConfiguration() );
    }

    public static KnowledgeBuilderConfiguration getChanceKBuilderConfiguration( KnowledgeBuilderConfiguration baseConf ) {
        PackageBuilderConfiguration pbc = (PackageBuilderConfiguration) baseConf;

        pbc.getEvaluatorRegistry().addEvaluatorDefinition( new IsEvaluatorDefinition() );
        pbc.getEvaluatorRegistry().addEvaluatorDefinition( new IsAEvaluatorDefinition() );
        pbc.getEvaluatorRegistry().addEvaluatorDefinition( new HoldsEvaluatorDefinition() );
        pbc.getEvaluatorRegistry().addEvaluatorDefinition( new ImperfectBaseEvaluatorDefinition() );

        return pbc;
    }


    public static KnowledgeBaseConfiguration getChanceKnowledgeBaseConfiguration( KnowledgeBaseConfiguration baseConf ) {
        RuleBaseConfiguration rbc = (RuleBaseConfiguration) baseConf;
        rbc.addActivationListener( "query", new ChanceQueryActivationListenerFactory() );
        return rbc;
    }

    public static KnowledgeBaseConfiguration getChanceKnowledgeBaseConfiguration() {
        return getChanceKnowledgeBaseConfiguration(KnowledgeBaseFactory.newKnowledgeBaseConfiguration() );
    }



    public static KnowledgeSessionConfiguration getChanceKnowledgeSessionConfiguration( KnowledgeSessionConfiguration baseConf ) {
        return baseConf;
    }

    public static KnowledgeSessionConfiguration getChanceKnowledgeSessionConfiguration( ) {
        return getChanceKnowledgeSessionConfiguration( KnowledgeBaseFactory.newKnowledgeSessionConfiguration() );
    }

}
