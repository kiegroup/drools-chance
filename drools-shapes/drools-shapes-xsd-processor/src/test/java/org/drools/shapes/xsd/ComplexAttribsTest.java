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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.w3._2001.xmlschema.Schema;
import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;

import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class ComplexAttribsTest {

    private static OWLOntology onto;
    private static OWLOntologyManager manager;
    private static OWLDataFactory factory;
    private static String tns;

    @BeforeClass
    public static void parse() {
        Xsd2Owl converter = Xsd2OwlImpl.getInstance();

        URL url = converter.getSchemaURL("org/drools/shapes/xsd/complexAttribs.xsd");
        Schema x = converter.parse( url );
        tns = x.getTargetNamespace() + "#";

        onto = converter.transform( x, url, true, true );

        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
    }

    @Test
    public void testComplexTypeDataAttribs() {

        try {

            OWLClass x = factory.getOWLClass(IRI.create(tns, "TestComplex"));

            assertEquals( 5, onto.getSubClassAxiomsForSubClass( x ).size() );

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    x,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLDataAllValuesFrom(
                                    factory.getOWLDataProperty( IRI.create( tns, "field"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ),
                            factory.getOWLDataMinCardinality(
                                    0,
                                    factory.getOWLDataProperty( IRI.create( tns, "field"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ),
                            factory.getOWLDataMaxCardinality(
                                    1,
                                    factory.getOWLDataProperty( IRI.create( tns, "field"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ) )
                )
            ) );
            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    x,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLDataAllValuesFrom(
                                    factory.getOWLDataProperty( IRI.create( tns, "defField"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ),
                            factory.getOWLDataMinCardinality(
                                    0,
                                    factory.getOWLDataProperty( IRI.create( tns, "defField"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ),
                            factory.getOWLDataMaxCardinality(
                                    1,
                                    factory.getOWLDataProperty( IRI.create( tns, "defField"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ) )
                )
            ) );
            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    x,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLDataAllValuesFrom(
                                    factory.getOWLDataProperty( IRI.create( tns, "otherField"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ),
                            factory.getOWLDataMinCardinality(
                                    1,
                                    factory.getOWLDataProperty( IRI.create( tns, "otherField"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ),
                            factory.getOWLDataMaxCardinality(
                                    1,
                                    factory.getOWLDataProperty( IRI.create( tns, "otherField"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ) )
                )
            ) );
            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    x,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLDataAllValuesFrom(
                                    factory.getOWLDataProperty(IRI.create(tns, "noField")),
                                    OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING)),
                            factory.getOWLDataMinCardinality(
                                    0,
                                    factory.getOWLDataProperty( IRI.create( tns, "noField"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ),
                            factory.getOWLDataMaxCardinality(
                                    0,
                                    factory.getOWLDataProperty( IRI.create( tns, "noField"  ) ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ) )
                )
            ) );



        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }

    @Test
    public void testComplexTypeDataSequence() {

        try {

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    factory.getOWLClass( IRI.create( tns, "TestChild" ) ),
                    factory.getOWLClass( IRI.create( tns, "TestComplex" ) ) ) ) );

            assertTrue( onto.containsAxiom(factory.getOWLSubClassOfAxiom(
                    factory.getOWLClass(IRI.create(tns, "TestChild")),

                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            factory.getOWLDataProperty(IRI.create(tns, "baseField1")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING)),
                                    factory.getOWLDataMinCardinality(
                                            0,
                                            factory.getOWLDataProperty(IRI.create(tns, "baseField1")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING)),
                                    factory.getOWLDataMaxCardinality(
                                            1,
                                            factory.getOWLDataProperty(IRI.create(tns, "baseField1")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING))),
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            factory.getOWLDataProperty(IRI.create(tns, "baseField2")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING)),
                                    factory.getOWLDataMinCardinality(
                                            0,
                                            factory.getOWLDataProperty(IRI.create(tns, "baseField2")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING))
                            ),
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            factory.getOWLDataProperty(IRI.create(tns, "baseField3")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING)),

                                    factory.getOWLDataMinCardinality(
                                            2,
                                            factory.getOWLDataProperty(IRI.create(tns, "baseField3")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING)),
                                    factory.getOWLDataMaxCardinality(
                                            3,
                                            factory.getOWLDataProperty(IRI.create(tns, "baseField3")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING))
                            )
                    ))
            ));



        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }


    @Test
    public void testComplexTypeDataChoice() {

        try {

            OWLClass x = factory.getOWLClass( IRI.create( tns, "TestAnotherChild" ) );

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    x,
                    factory.getOWLClass( IRI.create( tns, "TestBase" ) ) ) ) );

            assertTrue(onto.containsAxiom(factory.getOWLSubClassOfAxiom(
                    x,

                    factory.getOWLObjectUnionOf(
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            factory.getOWLDataProperty(IRI.create(tns, "optionField1")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING)),
                                    factory.getOWLDataMinCardinality(
                                            0,
                                            factory.getOWLDataProperty(IRI.create(tns, "optionField1")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING))
                            ),
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            factory.getOWLDataProperty(IRI.create(tns, "optionField2")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING)),
                                    factory.getOWLDataMinCardinality(
                                            0,
                                            factory.getOWLDataProperty(IRI.create(tns, "optionField2")),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING))
                            )
                    ))
            ));



        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }
}
