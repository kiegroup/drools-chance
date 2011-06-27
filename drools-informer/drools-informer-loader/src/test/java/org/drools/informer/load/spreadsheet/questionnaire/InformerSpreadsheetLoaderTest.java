/*
 * Copyright 2009 Solnet Solutions Limited (http://www.solnetsolutions.co.nz/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.informer.load.spreadsheet.questionnaire;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;
import org.drools.informer.load.questionnaire.InformerSpreadsheetLoader;

import java.io.File;

/**
 * TODO Need to flesh out actual tests as opposed to just creating DRL files from test spreadsheets.
 * 
 * @author Derek Rendall
 */
public class InformerSpreadsheetLoaderTest {

	@Test  @Ignore
	public void testProcessSimpleFile() {
		InformerSpreadsheetLoader loader = new InformerSpreadsheetLoader();
		byte[] compiled = loader.compileFile("SampleDecisionTreeSimple.xls");

         System.out.println(new String(compiled));

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ByteArrayResource(compiled),ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors());
        }


        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

        for (Object o : kSession.getObjects()) {
            System.err.println(o);
        }

        //assertTrue(buildKnowledge(compiled));


		
	}

    @Test  @Ignore
	public void testProcessComplexFile() {
		InformerSpreadsheetLoader loader = new InformerSpreadsheetLoader();
        byte[] compiled = loader.compileFile("SampleDecisionTreeComplex.xls");


        System.out.println(new String(compiled));

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ByteArrayResource(compiled),ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors());
        }

        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

        for (Object o : kSession.getObjects()) {
            System.err.println(o);
        }


	}




    private boolean buildKnowledge(byte[] compiled) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(new ClassPathResource("org/drools/informer/informer-changeset.xml"),ResourceType.CHANGE_SET);
        kbuilder.add(new ByteArrayResource(compiled), ResourceType.DRL);

        for (KnowledgeBuilderError kbe : kbuilder.getErrors()) {
            System.err.println(kbe);
        }

        assertEquals(0,kbuilder.getErrors().size());



        return (kbuilder.getErrors().size() == 0);
    }



}
