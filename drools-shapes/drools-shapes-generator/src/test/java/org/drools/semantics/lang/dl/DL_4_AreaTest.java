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

package org.drools.semantics.lang.dl;

import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.DLFactoryImpl;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.semantics.util.area.Area;
import org.drools.semantics.util.area.AreaNode;
import org.drools.semantics.util.area.AreaTxn;
import org.drools.semantics.builder.model.ConceptAreaTxn;
import org.drools.util.HierarchyEncoderImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


public class DL_4_AreaTest {

    @Test
    public void testArea1() {
        try {

            Resource res = ResourceFactory.newClassPathResource( "ontologies/specimen.owl" );
            OntoModel model = DLFactoryImpl.getInstance().buildModel( "partest", res, OntoModel.Mode.NONE );

            PropertyRelation drug = model.getProperty( "<http://org.drools/test#drugRel>" );
            PropertyRelation fluid = model.getProperty( "<http://org.drools/test#fluidRel>" );
            PropertyRelation bsub = model.getProperty( "<http://org.drools/test#bodySubRel>" );

            assertNotNull( drug );
            assertNotNull( fluid );
            assertNotNull( bsub );

            Set<PropertyRelation> s0 = Collections.EMPTY_SET;
            Set<PropertyRelation> s1 = new HashSet( Arrays.asList( drug ) );
            Set<PropertyRelation> s2 = new HashSet( Arrays.asList( fluid ) );
            Set<PropertyRelation> s3 = new HashSet( Arrays.asList( bsub ) );
            Set<PropertyRelation> s4 = new HashSet( Arrays.asList( drug, fluid ) );
            Set<PropertyRelation> s5 = new HashSet( Arrays.asList( fluid, bsub ) );
            Set<PropertyRelation> s6 = new HashSet( Arrays.asList( bsub, drug ) );
            Set<PropertyRelation> s7 = new HashSet( Arrays.asList( fluid, bsub, drug ) );

            Concept c00 = model.getConcept( "<http://org.drools/test#specimen>" );
            Concept c01 = model.getConcept( "<http://org.drools/test#fluid_sample>" );
            Concept c02 = model.getConcept( "<http://org.drools/test#drug_specimen>" );
            Concept c03 = model.getConcept( "<http://org.drools/test#acellular_bl_specimen>" );
            Concept c04 = model.getConcept( "<http://org.drools/test#body_subs_sample>" );
            Concept c05 = model.getConcept( "<http://org.drools/test#body_fluid_sample>" );
            Concept c06 = model.getConcept( "<http://org.drools/test#blood_specimen>" );
            Concept c07 = model.getConcept( "<http://org.drools/test#venous_bl_specimen>" );
            Concept c08 = model.getConcept( "<http://org.drools/test#mixed_venous_bl_spec>" );
            Concept c09 = model.getConcept( "<http://org.drools/test#serum_specimen>" );
            Concept c10 = model.getConcept( "<http://org.drools/test#acidified_ser_sample>" );
            Concept c11 = model.getConcept( "<http://org.drools/test#serum_spec_from_bl_prod>" );
            Concept c12 = model.getConcept( "<http://org.drools/test#amniotic_fluid_spec>" );
            Concept c13 = model.getConcept( "<http://org.drools/test#stool_specimen>" );
            Concept c14 = model.getConcept( "<http://org.drools/test#fecal_fluid_sample>" );


            AreaTxn<Concept,PropertyRelation> areaTxn = new ConceptAreaTxn( model );


            System.out.println( areaTxn );

            // Area 0
            Area<Concept,PropertyRelation> n0 = areaTxn.getArea( s0 );
            assertEquals( 3, n0.getElements().size());
            assertTrue( n0.getElements().contains( c00 ) );

            // Area 1
            Area<Concept,PropertyRelation> n1 = areaTxn.getArea( s1 );
            assertEquals( 1, n1.getElements().size() );
            assertTrue( n1.getElements().contains( c02 ) );

            // Area 2
            Area<Concept,PropertyRelation> n2 = areaTxn.getArea( s2 );
            assertEquals( 1, n2.getElements().size() );
            assertTrue( n2.getElements().contains( c01 ) );

            // Area 3
            Area<Concept,PropertyRelation> n3 = areaTxn.getArea( s3 );
            assertEquals( 2, n3.getElements().size() );
            assertTrue( n3.getElements().contains( c04 ) );
            assertTrue( n3.getElements().contains( c13 ) );

            // Area 4
            Area<Concept,PropertyRelation> n4 = areaTxn.getArea( s4 );
            assertEquals( 1, n4.getElements().size() );
            assertTrue( n4.getElements().contains( c03 ) );

            // Area 5
            Area<Concept,PropertyRelation> n5 = areaTxn.getArea( s5 );
            assertEquals( 3, n5.getElements().size() );
            assertTrue( n5.getElements().contains( c05 ) );
            assertTrue( n5.getElements().contains( c14 ) );
            assertTrue( n5.getElements().contains( c12 ) );

            // Area 6
            assertFalse( areaTxn.hasArea( s6 ) );

            // Area 7
            Area<Concept,PropertyRelation> n7 = areaTxn.getArea( s7 );
            assertEquals( 6, n7.getElements().size() );
            assertTrue( n7.getElements().contains( c06 ) );
            assertTrue( n7.getElements().contains( c07 ) );
            assertTrue( n7.getElements().contains( c08 ) );
            assertTrue( n7.getElements().contains( c09 ) );
            assertTrue( n7.getElements().contains( c10 ) );
            assertTrue( n7.getElements().contains( c11 ) );


            // Now check roots
            assertEquals( n1.getRoots(), new HashSet<Concept>( Arrays.asList( c02 ) ) );
            assertEquals( n2.getRoots(), new HashSet<Concept>( Arrays.asList( c01 ) ) );
            assertEquals( n3.getRoots(), new HashSet<Concept>( Arrays.asList( c04 ) ) );
            assertEquals( n4.getRoots(), new HashSet<Concept>( Arrays.asList( c03 ) ) );
            assertEquals( n5.getRoots(), new HashSet<Concept>( Arrays.asList( c05, c14 ) ) );
            assertEquals( n7.getRoots(), new HashSet<Concept>( Arrays.asList( c06, c09 ) ) );



            // Now check hierarchy
            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n1.getAreaCode(), n0.getAreaCode() ) );
            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n2.getAreaCode(), n0.getAreaCode() ) );
            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n3.getAreaCode(), n0.getAreaCode() ) );

            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n4.getAreaCode(), n1.getAreaCode() ) );
            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n4.getAreaCode(), n2.getAreaCode() ) );

            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n5.getAreaCode(), n2.getAreaCode() ) );
            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n5.getAreaCode(), n3.getAreaCode() ) );

            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n7.getAreaCode(), n4.getAreaCode() ) );
            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n7.getAreaCode(), n5.getAreaCode() ) );
            assertTrue( HierarchyEncoderImpl.supersetOrEqualset( n7.getAreaCode(), n0.getAreaCode() ) );




//
//
//            PartialAreaTxnImpl parea = new PartialAreaTxnImpl( area.getAreas(), area.getEncoderArea() );
//            Set<Concept> olpc = parea.getOverlappingCodes();
//
//            List<String> olp = Arrays.asList(
//                    "FecalFluidSample",
//                    "BodyFluidSample",
//                    "BloodSpecimen",
//                    "AcellularBlSpecimen",
//                    "SerumSpecimen",
//                    "SerumSpecFromBlProd" );

//            Assert.assertTrue( olp.size() == olpc.size() );
//            for( Concept cct: olpc ) {
//                Assert.assertTrue( olp.contains( cct.getName() ) );
//            }

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

//    @Test
//    public void testArea2() {
//        try {
//
//            Resource res = ResourceFactory.newClassPathResource( "ontologies/hardware.owl" );
//            OntoModel model = DLFactoryImpl.getInstance().buildModel( "hw", res, OntoModel.Mode.NONE );
//
//            System.out.println( "*********************************************************************************" );
//            System.out.println( "*********************************************************************************" );
//            System.out.println( "*********************************************************************************" );
//            System.out.println( "*********************************************************************************" );
//            System.out.println( "*********************************************************************************" );
//            System.out.println( "*********************************************************************************" );
//
//
//            AreaTxnImpl area = new AreaTxnImpl( model );
//                area.makeAreaNodes();
//
//            System.out.println( area.getAreas() );
//
//                area.makeAreaRoots();
//            PartialAreaTxnImpl parea = new PartialAreaTxnImpl( area.getAreas(), area.getEncoderArea() );
//            Set<Concept> olpc = parea.getOverlappingCodes();
//
//        } catch ( Exception e ) {
//            e.printStackTrace();
//            fail( e.getMessage() );
//        }
//
//    }
}
