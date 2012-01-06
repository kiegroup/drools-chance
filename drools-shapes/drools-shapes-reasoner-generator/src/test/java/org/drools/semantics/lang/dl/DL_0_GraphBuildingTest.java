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

import org.drools.ClassObjectFilter;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.definition.type.FactType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Test;

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
    public void testSequentialCreation() {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            knowledgeBuilder.add( new ClassPathResource( "testLatticeBuilding.drl" ), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }
        RuleBaseConfiguration rbC = new RuleBaseConfiguration();
            rbC.setAssertBehaviour( AssertBehaviour.EQUALITY );
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( rbC );
            knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        StatefulKnowledgeSession kSession = knowledgeBase.newStatefulKnowledgeSession();

            kSession.fireAllRules();

//        System.err.println( reportWMObjects( kSession ) );

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
        while (! queue.isEmpty())
            ans += queue.poll();
        ans += " ---------------- END WM -----------\n";
        return ans;
    }




}