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

import org.junit.Ignore;
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
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.w3._2001.xmlschema.Schema;
import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;

import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class SimpleTest {

    @Test
    public void testSimpleType() {

        try {
            Xsd2Owl converter = Xsd2OwlImpl.getInstance();

            URL url = converter.getSchemaURL("org/drools/shapes/xsd/simple.xsd");
            Schema x = converter.parse( url );
            String tns = x.getTargetNamespace() + "#";


            OWLOntology onto = converter.transform( x, url, true, true );

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();

            assertTrue( onto.containsAxiom( factory.getOWLDatatypeDefinitionAxiom(
                    factory.getOWLDatatype( IRI.create( tns, "Decimal" ) ),
                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_DOUBLE ) )
            ) );

        } catch ( Exception e ) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }


    @Test
    public void testSimpleEnum() {

        try {
            Xsd2Owl converter = Xsd2OwlImpl.getInstance();

            URL url = converter.getSchemaURL("org/drools/shapes/xsd/simple.xsd");
            Schema x = converter.parse( url );
            String tns = x.getTargetNamespace() + "#";

            OWLOntology onto = converter.transform( x, url, true, false );

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();

            assertTrue( onto.containsAxiom( factory.getOWLDatatypeDefinitionAxiom(
                    factory.getOWLDatatype( IRI.create( tns, "Enumerated" ) ),
                    factory.getOWLDataOneOf( factory.getOWLLiteral( "A", OWL2Datatype.XSD_STRING ),
                            factory.getOWLLiteral( "B", OWL2Datatype.XSD_STRING ),
                            factory.getOWLLiteral( "C", OWL2Datatype.XSD_STRING )
                    ) )
            ) );

        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
    }


    @Test
    public void testSimpleTypeWithRestriction() {

        try {
            Xsd2Owl converter = Xsd2OwlImpl.getInstance();

            URL url = converter.getSchemaURL("org/drools/shapes/xsd/simple.xsd");
            Schema x = converter.parse( url );
            String tns = x.getTargetNamespace() + "#";

            OWLOntology onto = converter.transform( x, url, true, true );

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();

            OWLClass ts = factory.getOWLClass( IRI.create( tns, "TS" ) );

            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            factory.getOWLDatatype( IRI.create( tns, "value2Type" ) ),
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ),
                                    OWLFacet.LENGTH,
                                    factory.getOWLLiteral( "2", OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ) ) )
                    )
            ));
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            factory.getOWLDatatype( IRI.create( tns, "value2Type" ) ),
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ),
                                    OWLFacet.MAX_LENGTH,
                                    factory.getOWLLiteral( "6", OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ) ) )
                    )
            ));
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            factory.getOWLDatatype( IRI.create( tns, "value2Type" ) ),
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING ),
                                    OWLFacet.PATTERN,
                                    factory.getOWLLiteral( "[1-2][0-9]" ) )
                    )
            ));


            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            factory.getOWLDatatype( IRI.create( tns, "value3Type" ) ),
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_DOUBLE ),
                                    OWLFacet.MIN_INCLUSIVE,
                                    factory.getOWLLiteral( "11.0", OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_DOUBLE ) ) )
                    )
            ));
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            factory.getOWLDatatype( IRI.create( tns, "value3Type" ) ),
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_DOUBLE ),
                                    OWLFacet.MAX_EXCLUSIVE,
                                    factory.getOWLLiteral( "13.0", OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_DOUBLE ) ) )
                    )
            ));


            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            factory.getOWLDatatype( IRI.create( tns, "value1Type" ) ),
                            factory.getOWLDataUnionOf(
                                OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ),
                                OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_STRING )
                            )
                    ) )
            );
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            factory.getOWLDatatype( IRI.create( tns, "value11Type" ) ),
                            factory.getOWLDataUnionOf(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_ANY_URI ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_DECIMAL ),
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_DATE_TIME )
                            )
                    ) )
            );


            Object xyz = onto.getSubClassAxiomsForSubClass( ts );
            assertEquals( 4, onto.getSubClassAxiomsForSubClass( ts ).size() );



        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }




    @Test
    public void testSimpleList() {

        try {
            Xsd2Owl converter = Xsd2OwlImpl.getInstance();

            URL url = converter.getSchemaURL("org/drools/shapes/xsd/simpleList.xsd");
            Schema x = converter.parse( url );
            String tns = x.getTargetNamespace() + "#";

            OWLOntology onto = converter.transform( x, url, true, true );

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();

            OWLDatatype score = factory.getOWLDatatype( IRI.create( tns, "score" ) );
            OWLDatatype hiscore = factory.getOWLDatatype( IRI.create( tns, "highScore" ) );
            OWLDatatype maxscore = factory.getOWLDatatype( IRI.create( tns, "dTwentyType" ) );

            OWLClass die = factory.getOWLClass( IRI.create( tns, "Die" ) );
            OWLClass list = factory.getOWLClass( IRI.create( Namespaces.XSD.toString(), "List" ) );
            OWLClass d6 = factory.getOWLClass( IRI.create( tns, "dSix" ) );
            OWLClass d8 = factory.getOWLClass( IRI.create( tns, "dEight" ) );
            OWLClass d20 = factory.getOWLClass( IRI.create( tns, "dTwenty" ) );

            OWLObjectProperty roll = factory.getOWLObjectProperty( IRI.create( tns, "roll" ) );
            OWLObjectProperty rock = factory.getOWLObjectProperty( IRI.create( tns, "rock" ) );
            OWLObjectProperty troll = factory.getOWLObjectProperty( IRI.create( tns, "troll" ) );

            OWLDataProperty item = factory.getOWLDataProperty( IRI.create( Namespaces.XSD.toString(), "item" ) );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( score ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            score,
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ),
                                    OWLFacet.MIN_INCLUSIVE,
                                    factory.getOWLLiteral( "1", OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ) ) )
                    )
            ));
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            score,
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ),
                                    OWLFacet.MAX_INCLUSIVE,
                                    factory.getOWLLiteral( "6", OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ) ) )
                    )
            ));

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( hiscore ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            hiscore,
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ),
                                    OWLFacet.MIN_INCLUSIVE,
                                    factory.getOWLLiteral( "1", OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ) ) )
                    )
            ));
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            hiscore,
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ),
                                    OWLFacet.MAX_INCLUSIVE,
                                    factory.getOWLLiteral( "8", OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ) ) )
                    )
            ));

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( maxscore ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            maxscore,
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_INT),
                                    OWLFacet.MIN_INCLUSIVE,
                                    factory.getOWLLiteral("1", OWL2DatatypeImpl.getDatatype(OWL2Datatype.XSD_INT)))
                    )
            ));
            assertTrue( onto.containsAxiom(
                    factory.getOWLDatatypeDefinitionAxiom(
                            maxscore,
                            factory.getOWLDatatypeRestriction(
                                    OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ),
                                    OWLFacet.MAX_INCLUSIVE,
                                    factory.getOWLLiteral( "20", OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_INT ) ) )
                    )
            ));

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( list ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( item ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLDataPropertyDomainAxiom(
                            item,
                            list
                    )
            ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLDataPropertyRangeAxiom(
                            item,
                            factory.getOWLDataUnionOf( maxscore, score, hiscore )
                    )
            ) );


            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( d6 ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLSubClassOfAxiom(
                            d6,
                            list
                    )
            ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLSubClassOfAxiom(
                            d6,
                            factory.getOWLDataAllValuesFrom( item, score )
                    )
            ) );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( d8 ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLSubClassOfAxiom(
                            d8,
                            list
                    )
            ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLSubClassOfAxiom(
                            d8,
                            factory.getOWLDataAllValuesFrom( item, hiscore )
                    )
            ) );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( d20 ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLSubClassOfAxiom(
                            d20,
                            list
                    )
            ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLSubClassOfAxiom(
                            d20,
                            factory.getOWLDataAllValuesFrom( item, maxscore )
                    )
            ) );


            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( roll ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLObjectPropertyDomainAxiom(
                            roll,
                            die
                    )
            ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLObjectPropertyRangeAxiom(
                            roll,
                            d6
                    )
            ) );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( rock ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLObjectPropertyDomainAxiom(
                            rock,
                            die
                    )
            ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLObjectPropertyRangeAxiom(
                            rock,
                            d8
                    )
            ) );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( troll ) ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLObjectPropertyDomainAxiom(
                            troll,
                            die
                    )
            ) );
            assertTrue( onto.containsAxiom(
                    factory.getOWLObjectPropertyRangeAxiom(
                            troll,
                            d20
                    )
            ) );

            assertTrue( onto.containsAxiom(
                    factory.getOWLSubClassOfAxiom(
                            die,
                            factory.getOWLObjectIntersectionOf(
                                    factory.getOWLObjectIntersectionOf(
                                            factory.getOWLObjectMinCardinality( 1, rock, d8 ),
                                            factory.getOWLObjectMaxCardinality( 1, rock, d8 ),
                                            factory.getOWLObjectAllValuesFrom( rock, d8 )
                                    ),
                                    factory.getOWLObjectIntersectionOf(
                                            factory.getOWLObjectMinCardinality( 1, roll, d6 ),
                                            factory.getOWLObjectMaxCardinality( 1, roll, d6 ),
                                            factory.getOWLObjectAllValuesFrom( roll, d6 )

                                    ),
                                    factory.getOWLObjectIntersectionOf(
                                            factory.getOWLObjectMinCardinality( 1, troll, d20 ),
                                            factory.getOWLObjectMaxCardinality( 1, troll, d20 ),
                                            factory.getOWLObjectAllValuesFrom( troll, d20 )
                                    )
                            )
                    )
            ) );




        } catch ( Exception e ) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
