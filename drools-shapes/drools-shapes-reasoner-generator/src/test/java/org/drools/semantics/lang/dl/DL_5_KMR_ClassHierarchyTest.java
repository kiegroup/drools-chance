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

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.model.OntoModel;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


/**
 * This is a sample class to launch a rule.
 */
@SuppressWarnings("restriction")
public class DL_5_KMR_ClassHierarchyTest  {

    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();
    

    @Test
    public void testHierarchyFromClassesExternal() {

        String source = "DLex7.manchester";
        Resource res = ResourceFactory.newClassPathResource( source );

        OntoModel results = factory.buildModel( "ex7", res, OntoModel.Mode.HIERARCHY );

        System.out.println(results);

        assertEquals( 4, results.getProperties().size() );
        assertEquals( "<_Test>", results.getProperty( "<_zimple3>" ).getSubject() );
        assertEquals( "<http://www.w3.org/2001/XMLSchema#int>", results.getProperty( "<_zimple3>" ).getObject() );
        assertEquals( "<http://jboss.org/drools/semantics/Zimple2Domain>", results.getProperty( "<_zimple2>" ).getSubject() );
        assertEquals( "<http://www.w3.org/2001/XMLSchema#int>", results.getProperty( "<_zimple2>" ).getObject() );
        assertEquals( "<http://jboss.org/drools/semantics/Zimple1Domain>", results.getProperty( "<_zimple1>" ).getSubject() );
        assertEquals( "<http://www.w3.org/2001/XMLSchema#int>", results.getProperty( "<_zimple1>" ).getObject() );
        assertEquals( "<_Test>", results.getProperty( "<_zimple1Integer>" ).getSubject() );
        assertEquals( "<http://www.w3.org/2001/XMLSchema#int>", results.getProperty( "<_zimple1Integer>" ).getObject() );
        assertEquals( 1, results.getProperty( "<_zimple1Integer>" ).getMaxCard().intValue() );

        // restricted props contains the prop itself, too
        assertEquals( 2, results.getProperty( "<_zimple1>" ).getRestrictedProperties().size() );

        assertEquals( 7, results.getConcepts().size() );
        assertNotNull( results.getConcept( "<http://www.w3.org/2002/07/owl#Thing>" ) );
        assertNotNull( results.getConcept( "<http://jboss.org/drools/semantics/Zimple1Domain>" ) );
        assertNotNull( results.getConcept( "<http://jboss.org/drools/semantics/Zimple2Domain>" ) );
        assertNotNull( results.getConcept( "<_Test3>" ) );
        assertNotNull( results.getConcept( "<_Test2>" ) );
        assertNotNull( results.getConcept( "<_Test>" ) );
        assertNotNull( results.getConcept( "<_Fact>" ) );

        assertEquals( 7, results.getSubConcepts().size() );
        assertNotNull( results.getSubConceptOf( "<http://jboss.org/drools/semantics/Zimple1Domain>", "<http://www.w3.org/2002/07/owl#Thing>" ) );
        assertNotNull( results.getSubConceptOf( "<http://jboss.org/drools/semantics/Zimple2Domain>", "<http://jboss.org/drools/semantics/Zimple1Domain>" ) );
        assertNotNull( results.getSubConceptOf( "<_Test2>", "<http://jboss.org/drools/semantics/Zimple2Domain>" ) );
        assertNotNull( results.getSubConceptOf( "<_Test>", "<http://jboss.org/drools/semantics/Zimple2Domain>" ) );
        assertNotNull( results.getSubConceptOf( "<_Test3>", "<http://jboss.org/drools/semantics/Zimple1Domain>" ) );
        assertNotNull( results.getSubConceptOf( "<_Test>", "<_Fact>" ) );
        assertNotNull( results.getSubConceptOf( "<_Fact>", "<http://www.w3.org/2002/07/owl#Thing>" ) );


    }



}