package org.drools.chance.kbase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.chance.Chance;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.fail;


public class AbstractChanceTest {

    public static final String MAP = "map";

    protected StatefulKnowledgeSession initBasicChanceTest( String drl ) {
        return initBasicChanceTest( new String[] { drl } );
    }

    protected StatefulKnowledgeSession initBasicChanceTest( String[] drls ) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(Chance.getChanceKBuilderConfiguration());
        for ( String s : drls ) {
            kBuilder.add( new ClassPathResource( s ), ResourceType.DRL );
        }
        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase(Chance.getRuleBaseConfiguration());
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        Map res = new HashMap();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
        kSession.setGlobal( "map", res );
        kSession.fireAllRules();

        return kSession;
    }
}
