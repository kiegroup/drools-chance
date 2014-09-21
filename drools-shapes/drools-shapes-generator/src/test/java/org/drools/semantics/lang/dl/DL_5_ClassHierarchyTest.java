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

import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.DLFactoryConfiguration;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.w3._2002._07.owl.Thing;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


/**
 * This is a sample class to launch a rule.
 */
@SuppressWarnings("restriction")
public class DL_5_ClassHierarchyTest {

    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();


    @Test
    public void testHierarchyFromClassesExternal() {

        String source = "fuzzyDL/DLex7.owl";
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( source );

        OntoModel results = factory.buildModel( "ex7", res, DLFactoryConfiguration.newConfiguration( OntoModel.Mode.FLAT ) );

        System.out.println(results);

        String ns = "http://jboss.org/drools/semantics/Example6#";

        assertEquals( 4, results.getProperties().size() );
        assertEquals( "<_Test>", results.getProperty( "<_zimple3>" ).getSubject() );
        assertEquals( "<http://www.w3.org/2001/XMLSchema#int>", results.getProperty( "<_zimple3>" ).getObject() );
        assertEquals( "<" + ns + "Zimple2Domain>", results.getProperty( "<_zimple2>" ).getSubject() );
        assertEquals( "<http://www.w3.org/2001/XMLSchema#int>", results.getProperty( "<_zimple2>" ).getObject() );
        assertEquals( "<" + ns + "Zimple1Domain>", results.getProperty( "<_zimple1>" ).getSubject() );
        assertEquals( "<http://www.w3.org/2001/XMLSchema#int>", results.getProperty( "<_zimple1>" ).getObject() );
        assertEquals( "<_Test>", results.getProperty( "<_zimple1Integer>" ).getSubject() );
        assertEquals( "<http://www.w3.org/2001/XMLSchema#int>", results.getProperty( "<_zimple1Integer>" ).getObject() );
        assertEquals( 1, results.getProperty( "<_zimple1Integer>" ).getMaxCard().intValue() );

        // restricted props contains the prop itself, too
        assertEquals( 1, results.getProperty( "<_zimple1>" ).getRestrictedProperties().size() );

        assertEquals( 7, results.getConcepts().size() );
        assertNotNull( results.getConcept( "<http://www.w3.org/2002/07/owl#Thing>" ) );
        assertNotNull( results.getConcept( "<" + ns + "Zimple1Domain>" ) );
        assertNotNull( results.getConcept( "<" + ns + "Zimple2Domain>" ) );
        assertNotNull( results.getConcept( "<_Test3>" ) );
        assertNotNull( results.getConcept( "<_Test2>" ) );
        assertNotNull( results.getConcept( "<_Test>" ) );
        assertNotNull( results.getConcept( "<_Fact>" ) );

        assertEquals( 8, results.getSubConcepts().size() );
        assertNotNull( results.getSubConceptOf( "<" + ns + "Zimple1Domain>", "<http://www.w3.org/2002/07/owl#Thing>" ) );
        assertNotNull( results.getSubConceptOf( "<" + ns + "Zimple2Domain>", "<" + ns + "Zimple1Domain>" ) );
        assertNotNull( results.getSubConceptOf( "<_Test2>", "<" + ns + "Zimple2Domain>" ) );
        assertNotNull( results.getSubConceptOf( "<_Test>", "<" + ns + "Zimple2Domain>" ) );
        assertNotNull( results.getSubConceptOf( "<_Test3>", "<" + ns + "Zimple1Domain>" ) );
        assertNotNull( results.getSubConceptOf( "<_Test>", "<_Fact>" ) );
        assertNotNull( results.getSubConceptOf( "<_Fact>", "<http://www.w3.org/2002/07/owl#Thing>" ) );


        assertTrue( results.isHierarchyConsistent() );

    }



    @Test
    public void testBodyHierarchy() {
        String source = "ontologies/bodyParts.ttl";
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( source );

        OntoModel results = factory.buildModel( "diamond", res, DLFactoryConfiguration.newConfiguration( OntoModel.Mode.HIERARCHY ) );

        for( Concept con : results.getConcepts() ) {
            if ( ! "Thing".equals( con.getName() ) ) {
                assertEquals( 1, con.getSuperConcepts().size() );
            }
            assertNotNull(con.getChosenSuperConcept());
            if ( ! ( "Human".equals( con.getName() ) || ( "Joint".equals( con.getName() ) ) ) ) {
                assertEquals( 0, con.getProperties().size() );
                assertEquals( 0, con.getChosenProperties().size() );
            }
        }

        assertEquals( 2, results.getConcept( "<http://depict.lia.deis.unibo.it#Human>" ).getProperties().size() );
        assertEquals( 2, results.getConcept( "<http://depict.lia.deis.unibo.it#Human>" ).getChosenProperties().size() );
        assertEquals( 4, results.getConcept( "<http://depict.lia.deis.unibo.it#Joint>" ).getProperties().size() );
        assertEquals( 4, results.getConcept( "<http://depict.lia.deis.unibo.it#Joint>" ).getChosenProperties().size() );

        System.out.println(results);

    }

    @Test
    public void testDiamondFlattenedHierarchy() {
        String source = "ontologies/diamondProp.manchester.owl";
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( source );

        OntoModel results = factory.buildModel( "diamond", res, DLFactoryConfiguration.newConfiguration( OntoModel.Mode.FLAT ) );

        for( Concept con : results.getConcepts() ) {
            assertEquals( Thing.class.getName(), con.getChosenSuperConcept().getFullyQualifiedName() );
        }

        assertEquals( 0, results.getConcept( "<http://www.w3.org/2002/07/owl#Thing>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_Top>" ).getChosenProperties().size() );
        assertEquals( 2, results.getConcept( "<_C0>" ).getChosenProperties().size() );
        assertEquals( 1, results.getConcept( "<_C1>" ).getChosenProperties().size() );
        assertEquals( 1, results.getConcept( "<_C2>" ).getChosenProperties().size() );
        assertEquals( 1, results.getConcept( "<_C3>" ).getChosenProperties().size() );
        assertEquals( 4, results.getConcept( "<_Left>" ).getChosenProperties().size() );
        assertEquals( 4, results.getConcept( "<_Right>" ).getChosenProperties().size() );
        assertEquals( 7, results.getConcept( "<_Low>" ).getChosenProperties().size() );
        assertEquals( 9, results.getConcept( "<_Bottom>" ).getChosenProperties().size() );


        assertEquals( 0, results.getConcept( "<http://www.w3.org/2002/07/owl#Thing>" ).getProperties().size() );
        assertEquals( 0, results.getConcept( "<_Top>" ).getProperties().size() );
        assertEquals( 2, results.getConcept( "<_C0>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_C1>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_C2>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_C3>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_Left>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_Right>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_Low>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_Bottom>" ).getProperties().size() );

        System.out.println(results);

        assertTrue( results.isHierarchyConsistent() );
    }



    @Test
    public void testDiamondVariantHierarchy() {
        String source = "ontologies/diamondProp.manchester.owl";
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( source );

        OntoModel results = factory.buildModel( "diamond", res, DLFactoryConfiguration.newConfiguration( OntoModel.Mode.VARIANT ) );

        for( Concept con : results.getConcepts() ) {
            if ( ! ( "DiamondRoot".equals( con.getName() ) || "Thing".equals( con.getName() ) ) ) {
                assertEquals( "org.jboss.drools.semantics.diamond.DiamondRoot", con.getChosenSuperConcept().getFullyQualifiedName() );
            }
        }
        assertEquals( Thing.class.getName(), results.getConcept( "<http://jboss.org/drools/semantics/Diamond#DiamondRoot>" ).getChosenSuperConcept().getFullyQualifiedName() );
        assertEquals( Thing.class.getName(), results.getConcept( "<http://www.w3.org/2002/07/owl#Thing>" ).getChosenSuperConcept().getFullyQualifiedName() );


        assertEquals( 0, results.getConcept( "<http://www.w3.org/2002/07/owl#Thing>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_Top>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_C0>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_C1>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_C2>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_C3>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_Left>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_Right>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_Low>" ).getChosenProperties().size() );
        assertEquals( 0, results.getConcept( "<_Bottom>" ).getChosenProperties().size() );
        assertEquals( 9, results.getConcept( "<http://jboss.org/drools/semantics/Diamond#DiamondRoot>" ).getChosenProperties().size() );


        assertEquals( 0, results.getConcept( "<http://www.w3.org/2002/07/owl#Thing>" ).getProperties().size() );
        assertEquals( 0, results.getConcept( "<_Top>" ).getProperties().size() );
        assertEquals( 2, results.getConcept( "<_C0>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_C1>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_C2>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_C3>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_Left>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_Right>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_Low>" ).getProperties().size() );
        assertEquals( 1, results.getConcept( "<_Bottom>" ).getProperties().size() );

        System.out.println(results);

        assertTrue( results.isHierarchyConsistent() );

    }



    @Test
    public void testOptimizedHierarchyOnDiamond() {

        String source = "ontologies/diamondProp.manchester.owl";
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( source );

        OntoModel results = factory.buildModel( "diamond", res, DLFactoryConfiguration.newConfiguration( OntoModel.Mode.OPTIMIZED ) );

        assertTrue( results.isHierarchyConsistent() );

    }


    @Test
    public void testOptimizedHierarchyOnConYard() {

        String source = "ontologies/conyard.ttl";
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( source );

        OntoModel results = factory.buildModel( "conyard", res, DLFactoryConfiguration.newConfiguration( OntoModel.Mode.OPTIMIZED, DLFactoryConfiguration.liteAxiomGenerators ) );

        assertTrue( results.isHierarchyConsistent() );

    }

    @Test
    public void testOptimizedHierarchyOnRule() {

        String source = "ontologies/rule_merged.owl";
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( source );

        OntoModel results = factory.buildModel( "conyard", res, DLFactoryConfiguration.newConfiguration( OntoModel.Mode.OPTIMIZED, DLFactoryConfiguration.liteAxiomGenerators ) );

        assertTrue( results.isHierarchyConsistent() );

    }

    @Test
    public void testOptimizedHierarchyOnRuleWithExample() {

        String source = "ontologies/sem_rules.owl";
        Resource res = KieServices.Factory.get().getResources().newClassPathResource( source );

        OntoModel results = factory.buildModel( "conyard", res, DLFactoryConfiguration.newConfiguration( OntoModel.Mode.OPTIMIZED, DLFactoryConfiguration.liteAxiomGenerators ) );

        assertTrue( results.isHierarchyConsistent() );

    }


}