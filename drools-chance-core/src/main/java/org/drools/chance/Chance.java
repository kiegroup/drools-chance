package org.drools.chance;


import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.core.util.ImperfectTripleFactory;
import org.drools.chance.factmodel.*;
import org.drools.chance.reteoo.ChanceFactHandleFactory;
import org.drools.chance.reteoo.ChanceFieldFactory;
import org.drools.chance.reteoo.builder.ChanceKnowledgeHelperFactory;
import org.drools.chance.reteoo.builder.ChanceLogicTransformerFactory;
import org.drools.chance.reteoo.builder.ChanceRuleBuilderFactory;
import org.drools.chance.reteoo.builder.ChanceRuleGroupElementBuilder;
import org.drools.chance.rule.builder.ChanceConstraintBuilderFactory;
import org.drools.chance.rule.builder.ChanceOperators;
import org.drools.chance.rule.builder.ChanceRulePatternBuilder;
import org.drools.chance.rule.constraint.core.connectives.factories.fuzzy.mvl.ManyValuedConnectiveFactory;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.rule.builder.DroolsCompilerComponentFactory;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.compiler.rule.builder.dialect.java.JavaRuleBuilderHelper;
import org.drools.compiler.rule.builder.dialect.mvel.MVELConsequenceBuilder;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.reteoo.KieComponentFactory;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.mvel2.Macro;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

public class Chance {


    public static void initialize() {

        ChanceStrategyFactory.initDefaults();

        JavaDialect.setGeBuilder( new ChanceRuleGroupElementBuilder() );
        JavaDialect.setPatternBuilder( new ChanceRulePatternBuilder() );
        JavaDialect.reinitBuilder();

        MVELDialect.setGeBuilder( new ChanceRuleGroupElementBuilder() );
        MVELDialect.setPatternBuilder( new ChanceRulePatternBuilder() );
        MVELDialect.reinitBuilder();





        TemplateRegistry registry = JavaRuleBuilderHelper.getRuleTemplateRegistry( null );
        registry.addNamedTemplate( "consequenceMethod",
                                   TemplateCompiler.compileTemplate( Chance.class.getResourceAsStream(
                                           "/org/drools/rule/builder/dialect/java/chanceRule.mvel"
                                   ) ) );
        TemplateRuntime.execute( registry.getNamedTemplate( "consequenceMethod" ), null, registry );

        MVELConsequenceBuilder.macros.put( "chance",
                new Macro() {
                    public String doMacro() {
                        return "((org.drools.chance.ChanceHelper) drools)";
                    }
                } );


        ChanceStrategyFactory.setDefaultFactory( new ManyValuedConnectiveFactory() );

    }



    public static void restoreDefaults() {

        JavaDialect.setPatternBuilder( new PatternBuilder() );
        JavaDialect.reinitBuilder();

        MVELDialect.setPatternBuilder( new PatternBuilder() );
        MVELDialect.reinitBuilder();

        /*
        JavaDialect.setGEBuilder( new GroupElementBuilder() );
        MVELDialect.setGEBuilder( new GroupElementBuilder() );
        */

        JavaRuleBuilderHelper.setConsequenceTemplate( "javaRule.mvel" );
    }


    public static KnowledgeBuilderConfiguration getChanceKBuilderConfiguration() {
        return getChanceKBuilderConfiguration( new KnowledgeBuilderConfigurationImpl() );
    }

    public static KnowledgeBuilderConfiguration getChanceKBuilderConfiguration( KnowledgeBuilderConfiguration baseConf ) {

        KnowledgeBuilderConfigurationImpl pbc = (KnowledgeBuilderConfigurationImpl) baseConf;

        for ( EvaluatorDefinition def : ChanceOperators.getImperfectEvaluatorDefinitions() ) {
            pbc.getEvaluatorRegistry().addEvaluatorDefinition( def );
        }

        DroolsCompilerComponentFactory dcf = new DroolsCompilerComponentFactory();

        dcf.setConstraintBuilderFactoryProvider( new ChanceConstraintBuilderFactory() );
        dcf.setFieldDataFactory( new ChanceFieldFactory() );

        /*

        dcf.setExpressionProcessor( new ChanceMVELDumper() );

        */

        ClassBuilderFactory cbf = new ClassBuilderFactory();
        cbf.setBeanClassBuilder( new ChanceBeanBuilderImpl() );
        cbf.setTraitBuilder( new ChanceTraitBuilderImpl() );
        cbf.setTraitProxyBuilder( new ChanceTripleProxyBuilderImpl() );
        cbf.setPropertyWrapperBuilder( new ChanceTriplePropertyWrapperClassBuilderImpl() );
        cbf.setEnumClassBuilder( new ChanceEnumBuilderImpl() );

        pbc.setClassBuilderFactory( cbf );

        pbc.setComponentFactory( dcf );

        return baseConf;
    }




    public static KieBaseConfiguration getChanceKnowledgeBaseConfiguration( KieBaseConfiguration baseConf ) {
        RuleBaseConfiguration rbc = (RuleBaseConfiguration) baseConf;

        KieComponentFactory rcf = new KieComponentFactory();

        //rbc.addActivationListener( "query", new ChanceQueryActivationListenerFactory() );

        /*
        rcf.setNodeFactoryProvider( new ChanceNodeFactory() );

        rcf.setAgendaFactory( new ChanceAgendaFactory() );



        */

        rcf.setRuleBuilderProvider( new ChanceRuleBuilderFactory() );
        rcf.setHandleFactoryProvider( new ChanceFactHandleFactory() );
        rcf.setKnowledgeHelperFactory( new ChanceKnowledgeHelperFactory() );
        rcf.setBaseTraitProxyClass( ImperfectTraitProxy.class );

        rcf.setFieldDataFactory( new ChanceFieldFactory() );
        rcf.setTripleFactory( new ImperfectTripleFactory() );

        ClassBuilderFactory cbf = new ClassBuilderFactory();
        cbf.setBeanClassBuilder( new ChanceBeanBuilderImpl() );
        cbf.setTraitBuilder( new ChanceTraitBuilderImpl() );
        cbf.setTraitProxyBuilder( new ChanceTripleProxyBuilderImpl() );
        cbf.setPropertyWrapperBuilder( new ChanceTriplePropertyWrapperClassBuilderImpl() );
        cbf.setEnumClassBuilder( new ChanceEnumBuilderImpl() );

        rcf.setClassBuilderFactory( cbf );
        rcf.setLogicTransformerFactory( new ChanceLogicTransformerFactory() );

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
