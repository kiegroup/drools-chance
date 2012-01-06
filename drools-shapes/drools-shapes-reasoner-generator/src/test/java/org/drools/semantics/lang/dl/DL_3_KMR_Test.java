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

package org.drools.semantics.lang.dl;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * This is a sample class to launch a rule.
 */
@SuppressWarnings("restriction")
public class DL_3_KMR_Test extends AbstractReasonerTestBase {



    private String kmrPath = "kmr2/kmr2_miniExample.manchester";




    @Test
    public void testSub1() throws Exception {
        OWLOntology onto = init(kmrPath);
        testSubsumption("<http://www.kmr.org/ontology/AllergyFactType>", "<http://www.kmr.org/ontology/FactType>", 1, 1);
    }

    @Test
    public void testSub2() throws Exception {
        init(kmrPath);
        testSubsumption("<http://www.kmr.org/ontology/FactType>", "<http://www.kmr.org/ontology/AllergyFactType>", 0, 1);
    }

    @Test
    public void testSub3() throws Exception {
        init(kmrPath);
        testSubsumption("<http://www.kmr.org/ontology/PatientFactType>", "<http://www.kmr.org/ontology/FactType>", 1, 1);
    }

    @Test
    public void testSub4() throws Exception {
        init(kmrPath);
        testSubsumption("<http://www.kmr.org/ontology/PatientFactType>", "<http://www.kmr.org/ontology/AllergyFactType>", 0, 1);
    }


    @Test
    public void testSub5() throws Exception {
        init(kmrPath);
        testSubsumption("<http://www.kmr.org/ontology/AllergyFactType>", "<http://www.kmr.org/ontology/PhysicianFactType>", 1, 1);
    }

    @Test
    public void testSub6() throws Exception {
        init(kmrPath);
        testSubsumption("<http://www.kmr.org/ontology/PhysicianFactType>", "<http://www.kmr.org/ontology/FactType>", 1, 1);
    }




}