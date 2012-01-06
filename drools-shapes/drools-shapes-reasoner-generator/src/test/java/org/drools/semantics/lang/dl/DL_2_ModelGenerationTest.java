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
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.DRLModel;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.compilers.ModelCompiler;
import org.drools.semantics.builder.model.compilers.ModelCompilerFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;

import static org.junit.Assert.*;


/**
 * This is a sample class to launch a rule.
 */
@SuppressWarnings("restriction")
public class DL_2_ModelGenerationTest {



    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();

    @Test
    public void testLoad() {
        String source = "kmr2/kmr2_miniExample.manchester";

        org.drools.io.Resource res = ResourceFactory.newClassPathResource(source);

        OWLOntology ontoDescr = factory.parseOntology( res );

        assertNotNull( ontoDescr );
    }




    @Test
    public void testDiamondModelGenerationExternal() {
        String source = "diamond.manchester";
        org.drools.io.Resource res = ResourceFactory.newClassPathResource( source );

        OntoModel results;

        factory.setInferenceStrategy( DLFactory.INFERENCE_STRATEGY.EXTERNAL );
        results = factory.buildModel( "diamond", res, KnowledgeBaseFactory.newKnowledgeBase().newStatefulKnowledgeSession() );

        checkDiamond( results );

    }



    @Test
    public void testDiamondModelInternal() {

        String source = "diamond.manchester";
        org.drools.io.Resource res = ResourceFactory.newClassPathResource( source );

        OntoModel results;


        factory.setInferenceStrategy(DLFactory.INFERENCE_STRATEGY.INTERNAL);
        results = factory.buildModel( "diamond", res, KnowledgeBaseFactory.newKnowledgeBase().newStatefulKnowledgeSession() );

        checkDiamond( results );

    }






    @Test
    public void testPropertiesGenerationExternal() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();

        String source = "DLex6.manchester";
        org.drools.io.Resource res = ResourceFactory.newClassPathResource( source );

        factory.setInferenceStrategy( DLFactory.INFERENCE_STRATEGY.EXTERNAL );
        OntoModel results = factory.buildModel( "ex6", res, kSession );


        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler(ModelFactory.CompileTarget.DRL);
        DRLModel drlModel = (DRLModel) compiler.compile( results );

        check( results, drlModel );
    }



    private void checkDiamond(OntoModel results) {
        assertTrue(results.getSubConceptOf("<_Right>", "<_Top>") != null || results.getSubConceptOf("<_Right>", "<_C0>") != null);
        assertTrue(results.getSubConceptOf("<_Left>", "<_Top>") != null || results.getSubConceptOf("<_Left>", "<_C0>") != null);

        assertNotNull(results.getSubConceptOf("<_Low>", "<_Left>"));
        assertNotNull(results.getSubConceptOf("<_Low>", "<_Right>"));
        assertNotNull(results.getSubConceptOf("<_Bottom>", "<_Low>"));

    }


















    @Test
    @Ignore //fails due to changes in the data structs, not yet updated
    public void testPropertiesGenerationInternal() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();

        String source = "DLex6.manchester";
        org.drools.io.Resource res = ResourceFactory.newClassPathResource( source );

        factory.setInferenceStrategy( DLFactory.INFERENCE_STRATEGY.INTERNAL );
        OntoModel results = factory.buildModel( "ex6", res, kSession );


        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler(ModelFactory.CompileTarget.DRL);
        DRLModel drlModel = (DRLModel) compiler.compile( results );

        check( results, drlModel );
    }

    protected void check( OntoModel results, DRLModel drlModel ) {

        System.out.println( "\n\n\n\n\n\n\n\n\n\n\n\n\n\n" );
        System.out.println( results );
        System.out.println(" -------------------------------");
        System.out.println( drlModel.getDRL() );
        System.out.println(" -------------------------------");


        assertNotNull( results.getConcept( "<_A>") );
        assertNotNull( results.getConcept( "<_B>") );
        assertNotNull( results.getConcept( "<_C>") );
        assertNotNull( results.getConcept( "<_D>") );
        assertNotNull( results.getConcept( "<MyPropRange>") );
        assertNotNull( results.getConcept( "<YourPropDomain>") );
        assertNotNull( results.getConcept( "<ZimpleDomain>") );

        assertNotNull( results.getSubConceptOf( "<YourPropDomain>", "<_B>" ) );
        assertNotNull( results.getSubConceptOf( "<_C>", "<MyPropRange>" ) );
        assertNotNull( results.getSubConceptOf( "<YourPropDomain>", "<_C>" ) );
        assertNotNull( results.getSubConceptOf( "<_D>", "<MyPropRange>" ) );
        assertNotNull( results.getSubConceptOf( "<_D>", "<ZimpleDomain>" ) );
        assertNotNull( results.getSubConceptOf( "<_B>", "<ZimpleDomain>" ) );

        assertEquals( 3, results.getProperties().size() );
        assertNotNull( results.getProperty( "<_myProp>" ) );
        assertNotNull(results.getProperty("<_yourProp>"));
        assertNotNull( results.getProperty( "<_zimple>" ) );
        assertTrue(results.getProperty("<_myProp>").getSubject().equals("<_A>"));
        assertTrue(results.getProperty("<_myProp>").getObject().equals("<MyPropRange>"));
        assertTrue( results.getProperty( "<_yourProp>" ).getSubject().equals( "<YourPropDomain>" ) );
        assertTrue( results.getProperty( "<_yourProp>" ).getObject().equals( "<_D>" ) );
        assertTrue( results.getProperty( "<_zimple>" ).getSubject().equals( "<ZimpleDomain>" ) );
        assertTrue( results.getProperty( "<_zimple>" ).getObject().equals( "<http://www.w3.org/2001/XMLSchema#int>" ) );


        assertTrue(
                results.getConcept("<_C>").getSuperConcepts().contains(results.getConcept("<MyPropRange>"))
        );
        assertTrue(
                results.getConcept( "<_D>" ).getSuperConcepts().contains( results.getConcept( "<MyPropRange>" ) )
        );
        assertTrue(
                results.getConcept( "<YourPropDomain>" ).getSuperConcepts().contains( results.getConcept( "<_C>" ) )
        );
        assertTrue(
                results.getConcept( "<YourPropDomain>" ).getSuperConcepts().contains( results.getConcept( "<_B>" ) )
        );

        assertTrue(
                results.getConcept( "<YourPropDomain>").getProperties().containsValue(
                        results.getProperty("<_yourProp>")
                )
        );
        assertTrue(
                results.getConcept( "<_A>").getProperties().containsValue(
                        results.getProperty("<_myProp>")
                )
        );

        assertTrue(
                results.getConcept("<YourPropDomain>").getProperties().get(
                        "<_yourProp>"
                ).getTarget().equals(results.getConcept("<_D>"))
                );
        assertTrue(
                results.getConcept( "<_A>").getProperties().get(
                        "<_myProp>"
                ).getTarget().equals( results.getConcept( "<MyPropRange>" ) )
        );

        assertTrue(
                results.getConcept( "<ZimpleDomain>").getProperties().get(
                        "<_zimple>"
                ).getTarget().equals( new Concept( "<http://www.w3.org/2001/XMLSchema#int>", "java.lang.Integer" ) )
        );




    }





    @Test
    public void testComplexAnonymous() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();


        String source = "DLex8.manchester";
        org.drools.io.Resource res = ResourceFactory.newClassPathResource( source );

        factory.setInferenceStrategy( DLFactory.INFERENCE_STRATEGY.EXTERNAL );
        OntoModel results = factory.buildModel( "ex8", res, kSession );


        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler(ModelFactory.CompileTarget.DRL);
        DRLModel drlModel = (DRLModel) compiler.compile( results );

        System.out.println( drlModel.getDRL() );
    }




}