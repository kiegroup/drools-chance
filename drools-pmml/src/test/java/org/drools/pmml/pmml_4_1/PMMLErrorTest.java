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

package org.drools.pmml.pmml_4_1;


import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PMMLErrorTest {

    @Test
    public void testErrorDuringGenration() {
        String pmlm = "<PMML version=\"4.1\" xsi:schemaLocation=\"http://www.dmg.org/PMML-4_1 http://www.dmg.org/v4-1/pmml-4-1.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.dmg.org/PMML-4_1\">\n" +
                      "  <Header copyright=\"opensource\" description=\"test\">\n" +
                      "    <Application name=\"handmade\" version=\"1.0\"/>\n" +
                      "    <Annotation>notes here</Annotation>\n" +
                      "    <Timestamp>now</Timestamp>\n" +
                      "  </Header>\n" +
                      "<IllegalModel>\n" +
                      "</IllegalModel>" +
                      "</PMML>";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( pmlm.getBytes() ),ResourceType.PMML );

        System.out.print( kbuilder.getErrors() );
        assertTrue( kbuilder.hasErrors() );

        String pmml = "<PMML version=\"4.1\" xsi:schemaLocation=\"http://www.dmg.org/PMML-4_1 http://www.dmg.org/v4-1/pmml-4-1.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.dmg.org/PMML-4_1\">\n" +
                      "  <Header copyright=\"opensource\" description=\"test\">\n" +
                      "    <Application name=\"handmade\" version=\"1.0\"/>\n" +
                      "    <Annotation>notes here</Annotation>\n" +
                      "    <Timestamp>now</Timestamp>\n" +
                      "  </Header>" +
                      "<DataDictionary>\n" +
                      " <DataField name=\"fld\" dataType=\"string\" optype=\"categorical\" />" +
                      "</DataDictionary>\n" +
                      "</PMML>";

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add( ResourceFactory.newByteArrayResource( pmml.getBytes() ),ResourceType.PMML );

        System.out.print( kbuilder2.getErrors() );
        assertFalse( kbuilder2.hasErrors() );

    }

}
