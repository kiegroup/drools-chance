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

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.w3._2001.xmlschema.Schema;
import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;

import java.net.URL;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class ElementRefsTest {


    @Test
    public void testElementRefs() {

        OWLOntology onto;
        OWLOntologyManager manager;
        OWLDataFactory factory;
        String tns;


        Xsd2Owl converter = Xsd2OwlImpl.getInstance();

        URL url = converter.getSchemaURL("org/drools/shapes/xsd/elementRefs.xsd");
        Schema x = converter.parse( url );
        tns = x.getTargetNamespace() + "#";

        onto = converter.transform( x, url, true, true );

        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();


        try {
            String px = tns;
            OWLClass left = factory.getOWLClass( IRI.create( px, "Left" ) );
            OWLClass link = factory.getOWLClass( IRI.create( px, "Link" ) );
            OWLDatatype simpleElement = factory.getOWLDatatype( IRI.create( px, "SimpleElement" ) );
            OWLDatatype strElement = factory.getOWLDatatype( IRI.create( px, "StrElement" ) );
            OWLClass mainElement = factory.getOWLClass( IRI.create( px, "MainElement" ) );
            OWLClass testElement = factory.getOWLClass( IRI.create( px, "TestElement" ) );

            OWLDataProperty simpleElementProp = factory.getOWLDataProperty( IRI.create( px, "SimpleElement" ) );
            OWLDataProperty strElementProp = factory.getOWLDataProperty( IRI.create( px, "StrElement" ) );
            OWLObjectProperty mainElementProp = factory.getOWLObjectProperty( IRI.create( px, "MainElement" ) );
            OWLObjectProperty testElementProp = factory.getOWLObjectProperty( IRI.create( px, "TestElement" ) );
            OWLObjectProperty linkProp = factory.getOWLObjectProperty( IRI.create( px, "link" ) );


            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( simpleElement ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( strElement ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( mainElement ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( testElement ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( left ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( link ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( simpleElementProp ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( strElementProp ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( mainElementProp ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( testElementProp ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( linkProp ) ) );

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    link,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            simpleElementProp,
                                            simpleElement ),
                                    factory.getOWLDataMinCardinality(
                                            2,
                                            simpleElementProp,
                                            simpleElement ),
                                    factory.getOWLDataMaxCardinality(
                                            4,
                                            simpleElementProp,
                                            simpleElement )
                            ),
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            strElementProp,
                                            strElement ),
                                    factory.getOWLDataMinCardinality(
                                            2,
                                            strElementProp,
                                            strElement ),
                                    factory.getOWLDataMaxCardinality(
                                            4,
                                            strElementProp,
                                            strElement )
                            )
                    ))
            ));

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    left,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLObjectAllValuesFrom(
                                            mainElementProp,
                                            mainElement ),
                                    factory.getOWLObjectMinCardinality(
                                            1,
                                            mainElementProp,
                                            mainElement ),
                                    factory.getOWLObjectMaxCardinality(
                                            1,
                                            mainElementProp,
                                            mainElement )
                            ),
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLObjectAllValuesFrom(
                                            testElementProp,
                                            testElement ),
                                    factory.getOWLObjectMinCardinality(
                                            1,
                                            testElementProp,
                                            testElement ),
                                    factory.getOWLObjectMaxCardinality(
                                            1,
                                            testElementProp,
                                            testElement )
                            ),
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLObjectAllValuesFrom(
                                            linkProp,
                                            link ),
                                    factory.getOWLObjectMinCardinality(
                                            0,
                                            linkProp,
                                            link )
                            ),
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            strElementProp,
                                            strElement ),
                                    factory.getOWLDataMinCardinality(
                                            1,
                                            strElementProp,
                                            strElement ),
                                    factory.getOWLDataMaxCardinality(
                                            1,
                                            strElementProp,
                                            strElement )
                            )
                    ))
            ));


        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }



    @Test
    public void testGroupRefs() {

        OWLOntology onto;
        OWLOntologyManager manager;
        OWLDataFactory factory;
        String tns;


        Xsd2Owl converter = Xsd2OwlImpl.getInstance();

        URL url = converter.getSchemaURL("org/drools/shapes/xsd/groupRefs.xsd");
        Schema x = converter.parse( url );
        tns = x.getTargetNamespace() + "#";

        onto = converter.transform( x, url, true, true );

        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();


        try {
            String px = tns;
            OWLClass k = factory.getOWLClass( IRI.create( px, "Test" ) );
            OWLDatatype string = OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING );

            OWLObjectProperty p1 = factory.getOWLObjectProperty( IRI.create( px, "field" ) );
            OWLDataProperty p2 = factory.getOWLDataProperty( IRI.create( px, "desc" ) );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( k ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( p1 ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( p2 ) ) );


            assertTrue( onto.containsAxiom( factory.getOWLObjectPropertyDomainAxiom( p1, k ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLObjectPropertyRangeAxiom( p1, k ) ) );

            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( p2, k ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( p2, string ) ) );

            assertTrue( onto.containsAxiom( factory.getOWLSubClassOfAxiom(
                    k,
                    factory.getOWLObjectIntersectionOf(
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLObjectAllValuesFrom(
                                            p1,
                                            k ),
                                    factory.getOWLObjectMinCardinality(
                                            0,
                                            p1,
                                            k )
                            ),
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLDataAllValuesFrom(
                                            p2,
                                            string ),
                                    factory.getOWLDataMinCardinality(
                                            1,
                                            p2,
                                            string ),
                                    factory.getOWLDataMaxCardinality(
                                            1,
                                            p2,
                                            string )
                            )
                    )
            )
            ));

        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }


}
