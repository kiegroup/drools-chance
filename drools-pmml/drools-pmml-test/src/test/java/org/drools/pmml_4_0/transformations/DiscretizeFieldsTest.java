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

package org.drools.pmml_4_0.transformations;


import org.drools.definition.type.FactType;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class DiscretizeFieldsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml_4_0/test_derived_fields_discretize.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";


    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }




    @Test
    public void testDiscretize() throws Exception {

        FactType age = getKbase().getFactType(packageName,"Age");
        FactType cat = getKbase().getFactType(packageName,"AgeCategories");

        assertNotNull(getKSession().getWorkingMemoryEntryPoint("in_Age"));


        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(-1);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, true, null, -1);
        checkFirstDataFieldOfTypeStatus(cat, true, false,null, "infant");


        this.refreshKSession();


        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(1);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 1);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "infant");


        this.refreshKSession();


        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(9);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 9);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "young");




        this.refreshKSession();


        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(30);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 30);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "mature");




        this.refreshKSession();


        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(90);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 90);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "ancient");



        this.refreshKSession();


        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(3000);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 3000);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "ancient");



        this.refreshKSession();


        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(19);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 19);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "ancient");




    }








}
