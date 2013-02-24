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
import org.semanticweb.owlapi.model.AxiomType;
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

public class AttrGroupTest {

    @Test
    public void testAttGroup() {

        try {

            Xsd2Owl converter = Xsd2OwlImpl.getInstance();

            URL url = converter.getSchemaURL("org/drools/shapes/xsd/attrGroup.xsd");
            Schema x = converter.parse( url );
            String tns = x.getTargetNamespace() + "#";

            OWLOntology onto = converter.transform( x, url, true, true );

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();

            OWLClass ivl = factory.getOWLClass( IRI.create( tns, "IVL_RTO" ) );
            OWLClass xyz = factory.getOWLClass( IRI.create( tns, "XYZ.ABC" ) );
            OWLDatatype dec = factory.getOWLDatatype( IRI.create( tns, "Decimal" ) );
            OWLDatatype cd = factory.getOWLDatatype( IRI.create( tns, "Code" ) );
            OWLDatatype bool = OWL2DatatypeImpl.getDatatype( OWL2Datatype.XSD_BOOLEAN );

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( ivl ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( dec ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( cd ) ) );
            

            OWLDataProperty lowIsInclusive = factory.getOWLDataProperty( IRI.create( tns, "lowIsInclusive" ) );
            OWLDataProperty highIsInclusive = factory.getOWLDataProperty( IRI.create( tns, "highIsInclusive" ) );
            OWLDataProperty highValue = factory.getOWLDataProperty( IRI.create( tns, "highValue" ) );
            OWLDataProperty highUnit = factory.getOWLDataProperty( IRI.create( tns, "highUnit" ) );
            OWLDataProperty numerator = factory.getOWLDataProperty( IRI.create( tns, "numerator" ) );
            OWLDataProperty denominator = factory.getOWLDataProperty( IRI.create( tns, "denominator" ) );
            OWLDataProperty lowNumerator = factory.getOWLDataProperty( IRI.create( tns, "lowNumerator" ) );
            OWLDataProperty lowDenominator = factory.getOWLDataProperty( IRI.create( tns, "lowDenominator" ) );
            OWLDataProperty highNumerator = factory.getOWLDataProperty( IRI.create( tns, "highNumerator" ) );
            OWLDataProperty highDenominator = factory.getOWLDataProperty(IRI.create(tns, "highDenominator"));

            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( lowIsInclusive ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( highIsInclusive ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( highValue ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( highUnit ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( numerator ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( denominator ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( lowNumerator ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( lowDenominator ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( highNumerator ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDeclarationAxiom( highDenominator ) ) );
            
            assertEquals( 6, onto.getAxiomCount( AxiomType.DATA_PROPERTY_DOMAIN ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( lowIsInclusive, ivl ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( highIsInclusive, ivl ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( lowNumerator, factory.getOWLObjectUnionOf( ivl, xyz ) ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( lowDenominator, factory.getOWLObjectUnionOf( ivl, xyz ) ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( highNumerator, ivl  ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyDomainAxiom( highDenominator, ivl ) ) );            
            
            
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( lowIsInclusive, bool ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( highIsInclusive, bool ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( highValue, dec ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( highUnit, cd ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( numerator, dec ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( denominator, dec ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( lowNumerator, dec  ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( lowDenominator, dec ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( highNumerator, dec  ) ) );
            assertTrue( onto.containsAxiom( factory.getOWLDataPropertyRangeAxiom( highDenominator, dec ) ) );

            assertEquals( 6 + 1, onto.getSubClassAxiomsForSubClass( ivl ).size() );
            assertEquals( 2 + 1, onto.getSubClassAxiomsForSubClass( xyz ).size() );

        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

    }


}
