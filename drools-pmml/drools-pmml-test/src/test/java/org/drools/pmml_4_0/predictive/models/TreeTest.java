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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TreeTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = false;
    private static final String source1 = "org/drools/pmml_4_0/test_tree.xml";
    private static final String source2 = "org/drools/pmml_4_0/test_tree_iris.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Ignore
    @Test
    public void testTree() throws Exception {
        setKSession(getModelSession(source1,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();  //init model

    }



    @Ignore
    @Test
    public void testWithDistrib() throws Exception {
        setKSession(getModelSession(source2,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();  //init model

    }



}
