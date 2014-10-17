package org.drools.chance.kbase;

import org.drools.chance.Chance;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static junit.framework.Assert.fail;


public class AbstractChanceTest {

    public static final String MAP = "map";

    protected KieSession initBasicChanceTest( String drl ) {
        return initBasicChanceTest( new String[] { drl } );
    }

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
