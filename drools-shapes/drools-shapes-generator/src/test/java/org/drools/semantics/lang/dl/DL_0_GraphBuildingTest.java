/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.lang.dl;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Collection;
import java.util.PriorityQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * This is a sample class to launch a rule.
 */
@SuppressWarnings("restriction")
public class DL_0_GraphBuildingTest {



    @Test
    @Ignore("needs abductive")
    public void testSequentialCreation() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        kieFileSystem.write( kieServices.getResources().newClassPathResource( "fuzzyDL/testLatticeBuilding.drl" ).setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        kieBuilder.buildAll();

        if ( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) ) {
            fail( kieBuilder.getResults().getMessages( Message.Level.ERROR ).toString() );
        }

        KieBaseConfiguration rbC = kieServices.newKieBaseConfiguration();
            rbC.setOption( EqualityBehaviorOption.EQUALITY );
        KieBase knowledgeBase = kieServices.newKieContainer( kieBuilder.getKieModule().getReleaseId() ).newKieBase( rbC );
        KieSession kSession = knowledgeBase.newKieSession();

        kSession.fireAllRules();

        System.err.println( reportWMObjects( kSession ) );

        FactType type = knowledgeBase.getFactType("org.drools.semantics.test","SubConceptOf");
        Collection facts = kSession.getObjects( new ClassObjectFilter( type.getFactClass() ) );

        assertEquals( 12, facts.size() );

        try {
            facts.contains( newSC( type, "A", "B") );
            facts.contains( newSC( type, "A", "C") );
            facts.contains( newSC( type, "B", "E") );
            facts.contains( newSC( type, "C", "E") );
            facts.contains( newSC( type, "D", "B") );
            facts.contains( newSC( type, "E", "All") );
            facts.contains( newSC( type, "F", "A") );
            facts.contains( newSC( type, "F", "D") );
            facts.contains( newSC( type, "G", "B") );
            facts.contains( newSC( type, "G", "C") );
            facts.contains( newSC( type, "H", "G") );
            facts.contains( newSC( type, "I", "All") );
        } catch (IllegalAccessException e) {
            fail( e.getMessage() );
        } catch (InstantiationException e) {
            fail( e.getMessage() );
        }


    }

    private Object newSC(FactType type, String a, String b) throws IllegalAccessException, InstantiationException {
        Object o = type.newInstance();
        type.set(o, "subject", a);
        type.set(o, "object", b);
        return o;
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
        while (! queue.isEmpty())
            ans += queue.poll();
        ans += " ---------------- END WM -----------\n";
        return ans;
    }




}