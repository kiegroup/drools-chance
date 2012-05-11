package org.drools.chance.kbase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.chance.Chance;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.FactHandle;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static junit.framework.Assert.fail;


public class AbstractChanceTest {

    public static final String MAP = "map";

    protected StatefulKnowledgeSession initBasicChanceTest( String drl ) {
        return initBasicChanceTest( new String[] { drl } );
    }

    protected StatefulKnowledgeSession initBasicChanceTest( String[] drls ) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(
                Chance.getChanceKBuilderConfiguration()
        );
        for ( String s : drls ) {
            kBuilder.add( new ClassPathResource( s ), ResourceType.DRL );
        }
        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase(
                Chance.getChanceKnowledgeBaseConfiguration()
        );
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        Map res = new HashMap();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
        kSession.setGlobal( "map", res );
        kSession.fireAllRules();

        return kSession;
    }


    protected StatefulKnowledgeSession initTimedChanceTest( String[] drls, boolean useRealTimeClock ) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( Chance.getChanceKBuilderConfiguration( ) );
        for ( String s : drls ) {
            kBuilder.add( new ClassPathResource( s ), ResourceType.DRL );
        }
        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }


        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase( Chance.getChanceKnowledgeBaseConfiguration(  ) );
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );


        KnowledgeSessionConfiguration conf = Chance.getChanceKnowledgeSessionConfiguration();
        conf.setOption( useRealTimeClock ? ClockTypeOption.get("realtime") : ClockTypeOption.get("pseudo") );

        Map res = new HashMap();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession( conf, null );
        kSession.setGlobal( "map", res );
        kSession.fireAllRules();

        return kSession;
    }


    public String reportWMObjects(StatefulKnowledgeSession session) {
        PriorityQueue<String> queue = new PriorityQueue<String>();
        for (FactHandle fh : session.getFactHandles()) {
            Object o;
            if (fh instanceof EventFactHandle) {
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
