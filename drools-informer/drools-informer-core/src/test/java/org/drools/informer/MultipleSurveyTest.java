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

package org.drools.informer;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.QueryResult;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.Variable;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/23/11
 * Time: 12:42 PM
 */
public class MultipleSurveyTest {


    private static final Logger logger = LoggerFactory.getLogger(MultipleSurveyTest.class);

    private KnowledgeBase knowledgeBase;


    @Before
    public void setUp() throws Exception {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/informer-changeset.xml"), ResourceType.CHANGE_SET);
        knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/multiSurveyTest.drl"), ResourceType.DRL);

        logger.debug(Arrays.toString(knowledgeBuilder.getErrors().toArray()));
        System.err.println(Arrays.toString(knowledgeBuilder.getErrors().toArray()));

        
        
        assertFalse(knowledgeBuilder.hasErrors());
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
    }


    @Test
    public void testMultiple() {
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();
        ArrayList<String> list = new ArrayList<String>();
        ksession.setGlobal("list",list);

        ksession.insert("1");
        ksession.insert("2");

        ksession.fireAllRules();

        QueryResults results = ksession.getQueryResults("activeObjectsInContext", "1");
        assertEquals(2,results.size());

        QueryResults results2 = ksession.getQueryResults("getItemId", "Question 1", "1", Variable.v );
        assertEquals(1,results2.size());

        String qid = (String) results2.iterator().next().get("$id");
        assertNotNull(qid);

        ksession.insert(new Answer(qid,"xyz"));
        ksession.fireAllRules();


        assertTrue(list.contains("1"));
        assertFalse(list.contains("2"));


        for (Object o : ksession.getObjects()) {
            System.err.println(o);
        }

    }




}
