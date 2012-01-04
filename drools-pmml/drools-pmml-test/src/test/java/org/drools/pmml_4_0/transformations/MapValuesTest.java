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


/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 11/12/10
 * Time: 10:11 PM
 *
 * PMML Test : Focus on the DataDictionary section
 */
public class MapValuesTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml_4_0/test_derived_fields_mapvalues.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";


    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }




    @Test
    public void testMapValues() throws Exception {

//        FactType age = getKbase().getFactType(packageName,"Age");
//        FactType hgt = getKbase().getFactType(packageName,"Height");
//        FactType hair = getKbase().getFactType(packageName,"HairColor");
        FactType out = getKbase().getFactType(packageName,"Mapped");

        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(18);
        getKSession().getWorkingMemoryEntryPoint("in_Height").insert(80.0);
        getKSession().getWorkingMemoryEntryPoint("in_HairColor").insert("red");

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(out, true, false, null, "klass1");

        refreshKSession();




        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(33);
        getKSession().getWorkingMemoryEntryPoint("in_Height").insert(120.0);
        getKSession().getWorkingMemoryEntryPoint("in_HairColor").insert("black");

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(out, true, false, null, "klass2");




    }








}
