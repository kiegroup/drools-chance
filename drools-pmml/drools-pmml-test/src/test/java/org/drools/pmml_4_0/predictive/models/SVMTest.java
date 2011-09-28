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

package org.drools.pmml_4_0.predictive.models;


import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.junit.Ignore;
import org.junit.Test;

public class SVMTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml_4_0/test_svm.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Test
    public void testSVM() throws Exception {
        setKSession(getModelSession(source1,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();  //init model

    }




}
