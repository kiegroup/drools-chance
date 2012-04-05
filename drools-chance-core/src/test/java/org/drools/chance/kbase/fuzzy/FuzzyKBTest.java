package org.drools.chance.kbase.fuzzy;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.chance.builder.*;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.factmodel.ClassBuilderFactory;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.fail;
import static org.junit.Assert.*;

public class FuzzyKBTest {

    private StatefulKnowledgeSession kSession;


    @BeforeClass
    public static void setFactories() {
        ChanceStrategyFactory.initDefaults();
        ClassBuilderFactory.setBeanClassBuilderService(new ChanceBeanBuilderImpl());
        ClassBuilderFactory.setTraitBuilderService( new ChanceTraitBuilderImpl() );
        ClassBuilderFactory.setTraitProxyBuilderService( new ChanceTripleProxyBuilderImpl() );
        ClassBuilderFactory.setPropertyWrapperBuilderService( new ChanceTriplePropertyWrapperClassBuilderImpl() );
        ClassBuilderFactory.setEnumClassBuilderService( new ChanceEnumBuilderImpl() );

    }

    @Before
    public void setUp() throws Exception {
        TraitFactory.reset();
        TraitFactory.clearStore();
        initObjects();
    }

    private void initObjects() throws Exception {

        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( ChanceStrategyFactory.getChanceKBuilderConfiguration() );
        kBuilder.add( new ClassPathResource( "org/drools/chance/fuzzy/testFuzzyFacts.drl" ), ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        kSession = kBase.newStatefulKnowledgeSession();
        kSession.setGlobal( "list", new ArrayList() );
        kSession.fireAllRules();

    }



    @Test
    public void testFuzzy() {

    }
}
