/*
 * Copyright 2013 JBoss Inc
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

package org.drools.shapes.xsd;

import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.w3._2001.xmlschema.Schema;
import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;

import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class TopElementsTest {

    private static OWLOntology onto;
    private static OWLOntologyManager manager;
    private static OWLDataFactory factory;
    private static String tns;

    @BeforeClass
    public static void parse() {

        Xsd2Owl converter = Xsd2OwlImpl.getInstance();

        URL url = converter.getSchemaURL("org/drools/shapes/xsd/topElements.xsd");
        Schema x = converter.parse( url );
        tns = x.getTargetNamespace() + "#";

        onto = converter.transform( x, url, true, true );

        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
    }


    @Test
    public void testTopElementGroups() {

        try {
            String px = tns;

            OWLDatatype string = OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING );
            OWLDatatype floot = OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_FLOAT );
            OWLDatatype intx = OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT );

            OWLDatatype simpleElementType = factory.getOWLDatatype( IRI.create( tns, "SimpleElementType" ) );
            OWLDatatype simpleElement = factory.getOWLDatatype( IRI.create( tns, "SimpleElement" ) );
            OWLDatatype strElement = factory.getOWLDatatype( IRI.create( tns, "StrElement" ) );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( simpleElementType ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDatatypeDefinitionAxiom(simpleElement, simpleElementType) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDatatypeDefinitionAxiom(simpleElementType, floot) ) );

            assertTrue( onto.containsAxiom( factory.getOWLDatatypeDefinitionAxiom( strElement, string ) ) );



            OWLClass test = factory.getOWLClass( IRI.create( tns, "TestType" ) );
            OWLClass testEl = factory.getOWLClass( IRI.create( tns, "TestElement" ) );
            OWLClass main = factory.getOWLClass( IRI.create( tns, "MainElement" ) );

            OWLDataProperty att = factory.getOWLDataProperty( IRI.create( tns, "att" ) );
            OWLDataProperty desc = factory.getOWLDataProperty( IRI.create( tns, "desc" ) );
            OWLDataProperty val = factory.getOWLDataProperty( IRI.create( tns, "value" ) );

            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( att, test ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( att, intx ) ) );

            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( desc, main ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( desc, string ) ) );

            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( val, main ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom(val, string) ) );



            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    test,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLDataAllValuesFrom(
                                    att,
                                    intx),
                            factory.getOWLDataMinCardinality(
                                    0,
                                    att,
                                    intx ),
                            factory.getOWLDataMaxCardinality(
                                    1,
                                    att,
                                    intx )
                    )
            ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom( testEl, test ) ) );


            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    main,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLDataAllValuesFrom(
                                    val,
                                    string ),
                            factory.getOWLDataMinCardinality(
                                    1,
                                    val,
                                    string ),
                            factory.getOWLDataMaxCardinality(
                                    1,
                                    val,
                                    string )
                    )
            )));

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    main,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLDataAllValuesFrom(
                                    desc,
                                    string),
                            factory.getOWLDataMinCardinality(
                                    0,
                                    desc,
                                    string ),
                            factory.getOWLDataMaxCardinality(
                                    1,
                                    desc,
                                    string )
                    )
            )));

            assertEquals( 3, onto.getSubClassAxiomsForSubClass( main ).size() );

        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }


}
