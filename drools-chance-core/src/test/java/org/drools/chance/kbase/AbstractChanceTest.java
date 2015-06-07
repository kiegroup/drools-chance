package org.drools.chance.kbase;

import org.drools.chance.Chance;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.ClassPathResource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.agent.KnowledgeAgentFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.utils.KieHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static junit.framework.Assert.fail;


public class AbstractChanceTest {

    public static final String MAP = "map";

    protected KieSession initBasicChanceTest( String drlFilePath ) {
        return initBasicChanceTest( new String[] { drlFilePath } );
    }

    protected KieBase getChanceEnabledKieBase( Resource... resources ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(
                Chance.getChanceKBuilderConfiguration()
        );

        for ( Resource res : resources ) {
            knowledgeBuilder.add( res, res.getResourceType() != null ? res.getResourceType() : ResourceType.DRL );
        }
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }

        KieBase kieBase = KnowledgeBaseFactory.newKnowledgeBase(
                Chance.getChanceKnowledgeBaseConfiguration()
        );
        TraitFactory.setMode( VirtualPropertyMode.TRIPLES, kieBase );

        ( ( KnowledgeBase ) kieBase ).addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );


        return kieBase;
    }

    protected KieSession getChanceEnabledKieSession( Resource... resources ) {
        KieBase kieBase = getChanceEnabledKieBase( resources );
        KieSession kSession = kieBase.newKieSession(
                Chance.getChanceKnowledgeSessionConfiguration(), null
        );
        return kSession;
    }

    protected KieSession initBasicChanceTest( String[] drlPaths ) {
        Resource[] resources = new Resource[ drlPaths.length ];
        for ( int j = 0; j < drlPaths.length; j++ ) {
            resources[ j ] = new ClassPathResource( drlPaths[ j ] );
        }

        KieSession kieSession = getChanceEnabledKieSession( resources );

        Map res = new HashMap();
        kieSession.setGlobal( "map", res );
        kieSession.fireAllRules();

        return kieSession;
    }


    /*
    protected KieSession initBasicChanceTest( String[] drls ) {
        KieHelper kieHelper = new KieHelper();

        for ( String s : drls ) {
            kieHelper.addContent( s, ResourceType.DRL );
        }
        Results results = kieHelper.verify();
        if ( results.hasMessages( Message.Level.ERROR ) ) {
            fail( results.getMessages( Message.Level.ERROR ).toString() );
        }

        KieSession kSession = kieHelper.build(
                Chance.getChanceKnowledgeBaseConfiguration()
        ).newKieSession();

        Map res = new HashMap();
        kSession.setGlobal( "map", res );
        kSession.fireAllRules();

        return kSession;
    }
    */


    public String reportWMObjects(KieSession session) {
        PriorityQueue<String> queue = new PriorityQueue<String>();
        for (FactHandle fh : session.getFactHandles()) {
            Object o;
            if (fh instanceof EventFactHandle ) {
                EventFactHandle efh = (EventFactHandle) fh;
                queue.add("\t " + efh.getStartTimestamp() + "\t" + efh.getObject().toString() + "\n");
            } else {
                o = ((DefaultFactHandle) fh).getObject();
                queue.add("\t " + o.toString() + "\n");
            }

        }
        String ans = " ---------------- WM " + session.getObjects().size() + " --------------\n";
        while (! queue.isEmpty()) {
            Object o = queue.poll();
            ans += o;
        }
        ans += " ---------------- END WM -----------\n";
        return ans;
    }
}
