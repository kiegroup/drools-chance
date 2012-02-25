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

package org.drools.chance.distribution.belief;


import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.belief.discrete.TBM;
import org.drools.chance.distribution.belief.discrete.TBMStrategyFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TBMStrategyFactoryTest {


    @BeforeClass
    public static void init() {
        ChanceStrategyFactory.initDefaults();

        String str = "{A,B}/0.3, {A}/0.2, {B,C,D}/0.1, {C,D}/0.35";
        tbm = (TBM<String>) new TBMStrategyFactory<String>().buildStrategies( DegreeType.SIMPLE, String.class ).parse( str );
    }


    private static TBM tbm;



    @Test
    public void testDegree() {

        assertEquals( 0.3, tbm.getDegree( "B", "A" ).getValue(), 1e-16 );
        assertEquals( 0.3, tbm.getDegree( "A", "B" ).getValue(), 1e-16 );

        assertEquals( 0.2, tbm.getDegree( "A" ).getValue(), 1e-16 );

        assertEquals( 0.1, tbm.getDegree( "B", "C", "D" ).getValue(), 1e-16 );
        assertEquals( 0.1, tbm.getDegree( "C", "D", "B" ).getValue(), 1e-16 );
        assertEquals( 0.1, tbm.getDegree( "C", "B", "D" ).getValue(), 1e-16 );

        assertEquals( 0.35, tbm.getDegree( "C", "D" ).getValue(), 1e-16 );
        assertEquals( 0.35, tbm.getDegree( "D", "C" ).getValue(), 1e-16 );

        assertEquals( 5, tbm.size() );
        assertEquals( 4, tbm.universeSize() );
        assertEquals( 16, tbm.domainSize().intValue() );

        System.out.println( tbm );

    }



    @Test
    public void testBelief() {

        assertEquals( 0.5, tbm.getBelief( "B", "A" ).getValue(), 1e-16 );
        assertEquals( 0.5, tbm.getBelief( "A", "B" ).getValue(), 1e-16 );

        assertEquals( 0.2, tbm.getBelief( "A" ).getValue(), 1e-16 );

        assertEquals( 0.45, tbm.getBelief( "B", "C", "D" ).getValue(), 1e-16 );
        assertEquals( 0.45, tbm.getBelief( "C", "D", "B" ).getValue(), 1e-16 );
        assertEquals( 0.45, tbm.getBelief( "C", "B", "D" ).getValue(), 1e-16 );

        assertEquals( 0.35, tbm.getBelief( "C", "D" ).getValue(), 1e-16 );
        assertEquals( 0.35, tbm.getBelief( "D", "C" ).getValue(), 1e-16 );

        assertEquals( 1.0, tbm.getBelief( "D", "C", "B", "A" ).getValue(), 1e-16 );


        System.out.println( tbm );

    }



    @Test
    public void testPlausibility() {

        assertEquals( 0.65, tbm.getPlausibility( "B", "A" ).getValue(), 1e-16 );
        assertEquals( 0.65, tbm.getPlausibility( "A", "B" ).getValue(), 1e-16 );

        assertEquals( 0.55, tbm.getPlausibility( "A" ).getValue(), 1e-16 );

        assertEquals( 0.8, tbm.getPlausibility( "B", "C", "D" ).getValue(), 1e-16 );
        assertEquals( 0.8, tbm.getPlausibility( "C", "D", "B" ).getValue(), 1e-16 );
        assertEquals( 0.8, tbm.getPlausibility( "C", "B", "D" ).getValue(), 1e-16 );

        assertEquals( 0.5, tbm.getPlausibility( "C", "D" ).getValue(), 1e-16 );
        assertEquals( 0.5, tbm.getPlausibility( "D", "C" ).getValue(), 1e-16 );

        assertEquals( 1.0, tbm.getPlausibility( "D", "C", "B", "A" ).getValue(), 1e-16 );


        System.out.println( tbm );

    }




    String str = "{A,B}/0.3, {A}/0.2, {B,C,D}/0.1, {C,D}/0.35";

    @Test
    @Ignore
    public void testCommonality() {

        assertEquals( 0.35, tbm.getCommonality( "B", "A" ).getValue(), 1e-16 );
        assertEquals( 0.35, tbm.getCommonality( "A", "B" ).getValue(), 1e-16 );

        assertEquals( 0.55, tbm.getCommonality( "A" ).getValue(), 1e-16 );

        assertEquals( 0.15, tbm.getCommonality( "B", "C", "D" ).getValue(), 1e-16 );
        assertEquals( 0.15, tbm.getCommonality( "C", "D", "B" ).getValue(), 1e-16 );
        assertEquals( 0.15, tbm.getCommonality( "C", "B", "D" ).getValue(), 1e-16 );

        assertEquals( 0.5, tbm.getCommonality( "C", "D" ).getValue(), 1e-16 );
        assertEquals( 0.5, tbm.getCommonality( "D", "C" ).getValue(), 1e-16 );

        assertEquals( 1.0, tbm.getCommonality( "D", "C", "B", "A" ).getValue(), 1e-16 );


        System.out.println( tbm );

    }



}
