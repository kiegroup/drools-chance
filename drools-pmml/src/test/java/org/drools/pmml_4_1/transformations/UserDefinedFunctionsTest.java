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

package org.drools.pmml_4_1.transformations;


import org.drools.definition.type.FactType;
import org.drools.pmml_4_1.DroolsAbstractPMMLTest;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.KnowledgeContext;
import org.drools.runtime.rule.FactHandle;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;


public class UserDefinedFunctionsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source1  = "org/drools/pmml_4_1/test_user_functions_nested.xml";
    private static final String source2  = "org/drools/pmml_4_1/test_user_functions_complex.xml";
    private static final String source3 = "org/drools/pmml_4_1/test_user_functions_simpleTransformations.xml";
    private static final String packageName = "org.drools.pmml_4_1.test";



    @Test
    public void testFunctions1() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge1" );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 22.0 );
    }


    @Test
    public void testFunctions2() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        FactType userAge2 = getKbase().getFactType( packageName, "UserAge2" );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge2, true, false, null, 0.1 );

    }

    @Test
    public void testFunctions3() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        FactType userAge3 = getKbase().getFactType( packageName, "UserAge3" );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge3, true, false, null, 10 );

    }

    @Test
    public void testFunctions4() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        FactType userAge4 = getKbase().getFactType( packageName, "UserAge4" );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge4, true, false, null, 24 );

    }

    @Test
    public void testFunctions5() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        FactType userAge5 = getKbase().getFactType( packageName, "UserAge5" );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge5, true, false, null, 45.5 );
    }

    @Test
    public void testFunctions6() throws Exception {

        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        FactType userAge6 = getKbase().getFactType( packageName, "UserAge6" );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 10 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge6, true, false, null, 1.0 );

    }

    @Test
    public void testFunctionsNested() throws Exception {

        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge" );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 10.0 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 130.0 );

    }



    @Test
    public void testComplexFunctionsNested() throws Exception {

        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge" );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 10.0 );

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 6270.0 );

    }

    @Test
    public void testComplexFunctionsNested2() throws Exception {

        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        FactType userAge1 = getKbase().getFactType( packageName, "UserAge" );
        FactType userAge2 = getKbase().getFactType( packageName, "UserAgeComplex" );

        FactHandle h = getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 10.0 );

        getKSession().fireAllRules();

        System.err.println( reportWMObjects( getKSession() ) );

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 6270.0 );
        checkFirstDataFieldOfTypeStatus( userAge2, true, false, null, 44.1 );

        System.out.println( "_________________________________________________________________" );

        FactType age = getKbase().getFactType( packageName, "Age" );
        Object aged = getKSession().getObjects( new ClassObjectFilter( age.getFactClass() ) ).iterator().next();

        getKSession().retract( getKSession().getFactHandle( aged ) );
        getKSession().fireAllRules();

        assertEquals( 0, getKSession().getFactCount() );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 20.0 );
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 9570.0 );
        checkFirstDataFieldOfTypeStatus( userAge2, true, false, null, 115.2 );

        getKSession().getWorkingMemoryEntryPoint( "in_Age" ).insert( 30.0 );
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( userAge1, true, false, null, 12870.0 );
        checkFirstDataFieldOfTypeStatus( userAge2, true, false, null, 306.3 );

        System.err.println( reportWMObjects( getKSession() ) );

        assertEquals( 4, getKSession().getFactCount() );

    }





}
