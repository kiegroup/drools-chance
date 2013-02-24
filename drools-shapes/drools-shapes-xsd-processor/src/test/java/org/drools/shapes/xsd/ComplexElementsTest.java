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
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.w3._2001.xmlschema.Schema;
import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;

import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class ComplexElementsTest {

    private static OWLOntology onto;
    private static OWLOntologyManager manager;
    private static OWLDataFactory factory;
    private static String tns;

    @BeforeClass
    public static void parse() {

        Xsd2Owl converter = Xsd2OwlImpl.getInstance();

        URL url = converter.getSchemaURL("org/drools/shapes/xsd/complexElements.xsd");
        Schema x = converter.parse( url );
        tns = x.getTargetNamespace() + "#";

        onto = converter.transform( x, url, true, true );

        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
    }


    @Test
    public void testObjectProperties() {

        try {
            String px = tns;
            OWLClass k = factory.getOWLClass( IRI.create( px, "Source" ) );
            OWLClass x = factory.getOWLClass( IRI.create( px, "Tgt.Test" ) );

            assertEquals( 2, onto.getSubClassAxiomsForSubClass( k ).size() );
            assertEquals( 2, onto.getSubClassAxiomsForSubClass( x ).size() );
            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom( k, factory.getOWLThing() ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom( x, factory.getOWLThing() ) ) );

            OWLObjectProperty p1 = factory.getOWLObjectProperty( IRI.create( px, "field" ) );
            OWLDataProperty p2 = factory.getOWLDataProperty( IRI.create( px, "desc" ) );
            OWLDataProperty p3 = factory.getOWLDataProperty( IRI.create( px, "__mixed" ) );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( x ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( k ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( p1 ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( p2 ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( p3 ) ) );

            Object y = onto.getObjectPropertyDomainAxioms( p1 );

            assertTrue( onto.containsAxiom( factory.getOWLObjectPropertyDomainAxiom( p1, factory.getOWLObjectUnionOf( k, x ) ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( p2, x ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( p3, factory.getOWLObjectUnionOf( k, x ) ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLObjectPropertyRangeAxiom( p1, x ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( p2, OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( p3, OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ) ) );

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    x,

                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLObjectAllValuesFrom(
                                            factory.getOWLObjectProperty( IRI.create( px, "field" ) ),
                                            x ),
                                    factory.getOWLObjectMinCardinality(
                                            0,
                                            factory.getOWLObjectProperty( IRI.create( px, "field" ) ),
                                            x )
                            ),
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            factory.getOWLDataProperty( IRI.create( px, "desc" ) ),
                                            OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_STRING ) ),
                                    factory.getOWLDataMinCardinality(
                                            1,
                                            factory.getOWLDataProperty( IRI.create( px, "desc" ) ),
                                            OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) ),
                                    factory.getOWLDataMaxCardinality(
                                            1,
                                            factory.getOWLDataProperty( IRI.create( px, "desc" ) ),
                                            OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ) )
                            )
                    ))
            ));

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    k,

                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLObjectAllValuesFrom(
                                    factory.getOWLObjectProperty( IRI.create( px, "field" ) ),
                                    x ),
                            factory.getOWLObjectMinCardinality(
                                    0,
                                    factory.getOWLObjectProperty( IRI.create( px, "field" ) ),
                                    x )
                    )
            )
            ));



        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }





    @Test
    public void testAnonRanges() {

        try {
            String px = tns;
            OWLClass k = factory.getOWLClass( IRI.create( px, "Left" ) );
            OWLClass l = factory.getOWLClass( IRI.create( px, "Link" ) );
            OWLClass x = factory.getOWLClass( IRI.create( px, "Tgt.Test" ) );


            OWLObjectProperty p1 = factory.getOWLObjectProperty( IRI.create( px, "link" ) );
            OWLObjectProperty p2 = factory.getOWLObjectProperty( IRI.create( px, "pointer" ) );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( x ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( l ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( k ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( p1 ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( p2 ) ) );


            assertTrue( onto.containsAxiom( factory.getOWLObjectPropertyDomainAxiom( p1, k ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLObjectPropertyRangeAxiom( p1, l ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLObjectPropertyDomainAxiom( p2, l ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLObjectPropertyRangeAxiom( p2, x ) ) );

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    l,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLObjectAllValuesFrom(
                                    p2,
                                    x ),
                            factory.getOWLObjectMinCardinality(
                                    1,
                                    p2,
                                    x )
                    )
            )));
            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    k,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLObjectAllValuesFrom(
                                    p1,
                                    l ),
                            factory.getOWLObjectMinCardinality(
                                    0,
                                    p1,
                                    l )
                    )
            )));


        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }

}
