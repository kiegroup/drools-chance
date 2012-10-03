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

package org.drools.pmml.pmml_4_1.predictive;


import org.drools.pmml.pmml_4_1.DroolsAbstractPMMLTest;
import org.junit.Test;



public class TargetsAndOutputsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml/pmml_4_1/test_target_and_output.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_1.test";




    @Test
    public void testTarget1() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(4);
        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(3.5);
        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));


        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP1"),
                true, false,"IRIS_MLP",2.6);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP2"),
                true, false,"IRIS_MLP",4.4);

    }






@Test
    public void testOutputFeatures() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(4);
        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(3.5);
        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));


        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP1"),
                true, false,"IRIS_MLP",2.6);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP2"),
                true, false,"IRIS_MLP",4.4);

    }






}
