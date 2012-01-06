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
public class DL_1_Tableau_Test extends AbstractReasonerTestBase {





    @Test
    public void testRec1() throws Exception {
        OWLOntology onto = init("DLex5.manchester");
        testRecognition("<_Alias>", 0.45, 0.9);
    }

    @Test
    public void testRec2() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_Klass>", 0.1, 0.55);
    }

    @Test
    public void testRec3() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_Professor>",0.02,0.95);
    }

    @Test
    public void testRec4() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_Worker>",0.3,0.85);
    }

    @Test
    public void testRec5() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_WorkForce>",0.7,0.96);
    }

    @Test
    public void testRec7() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_Automa>",0.0,0.0);
    }

    @Test
    public void testRec8() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_GeneralWorker>",0.7,1.0);
    }

    @Test
    public void testRec9() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_Slave>",0.7,0.96);
    }

    @Test
    public void testRec10() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_Being>",0.6,0.7);
    }

    @Test
    public void testRec11() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_Cyborg>",0.3,0.4);
    }


    @Test
    public void testRec12() throws Exception {
        init("DLex5.manchester");
        testRecognition("<_Maiden>",0.0,0.7);
    }







    @Test
    public void testSub11() throws Exception {
        init("DLex01.manchester");
        testSubsumption("<_Man>", "<_Human>", 1, 1);
    }


    @Test
    public void testSub12() throws Exception {
        init("DLex01.manchester");
        testSubsumption("<_Human>", "<_Man>", 1, 1);
    }

    @Test
    public void testSub21() throws Exception {
        init("DLex01.manchester");
        testSubsumption("<_Human>", "<_Robot>", 0, 0);
    }

    @Test
    public void testSub22() throws Exception {
        init("DLex01.manchester");
        testSubsumption("<_Man>", "<_Robot>", 0, 0);
    }

    @Test
    public void testSub23() throws Exception {
        init("DLex01.manchester");
        testSubsumption("<_Robot>", "<_Human>", 0, 0);
    }

    @Test
    public void testSub24() throws Exception {
        init("DLex01.manchester");
        testSubsumption("<_Robot>", "<_Man>", 0, 0);
    }










    @Test
    public void testSub31() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Man>", "<_Human>", 1, 1);
    }

    @Test
    public void testSub32() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Man>", "<_Living>", 1, 1);
    }

    @Test
    public void testSub33() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Human>", "<_Man>", 0, 1);
    }



    @Test
    public void testSub34() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Superman>", "<_Human>", 1, 1);
    }


    @Test
    public void testSub35() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Earthling>", "<_Alien>", 0, 0);
    }

    @Test
    public void testSub36() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Earthling>", "<_Superman>", 0, 0);
    }

    @Test
    public void testSub37() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Man>", "<_Inhuman>", 0, 0);
    }


    @Test
    public void testSub38() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Inhuman>", "<_Superman>", 0, 0);
    }


    @Test
    public void testSub39() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Human>", "<_Monster>", 0, 1);
    }

    //                     Alive == Human or Living
    @Test
    public void testSub40() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Human>", "<_Alive>", 1, 1);
    }

    @Test
    public void testSub41() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Living>", "<_Alive>", 1, 1);
    }

    @Test
    public void testSub42() throws Exception {
        init("DLex02.manchester");
        testSubsumption("<_Alive>", "<_Human>", 0, 1);
    }









    @Test
    public void testSub51() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Human>", "<_Alive>", 1, 1);
    }

    @Test
    public void testSub52() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Living>", "<_Alive>", 1, 1);
    }

    @Test
    public void testSub53() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Alive>", "<_Human>", 0, 1);
    }

    @Test
    public void testSub54() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Man>", "<_Earthling>", 1, 1);
    }

    @Test
    public void testSub55() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Alien>", "<_Earthling>", 0, 1);
    }

    @Test
    public void testSub56() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Rock>", "<_Human>", 0, 0);
    }

    @Test
    public void testSub57() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Human>", "<_Rock>", 0, 0);
    }

    @Test
    public void testSub60() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Man>", "<_Human>", 1, 1);
    }

    @Test
    public void testSub61() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Man>", "<_Living>", 1, 1);
    }

    @Test
    public void testSub62() throws Exception {
        init("DLex03.manchester");
        testSubsumption("<_Living>", "<_Man>", 0, 1);
    }


















    @Test
    public void testRec71() throws Exception {
        init("DLex04.manchester");
        testRecognition("<_Professor>", 0.02, 0.95);
    }

    @Test
    public void testRec72() throws Exception {
        init("DLex04.manchester");
        testRecognition("<_BeastMaster>", 0.4, 0.95);
    }


    @Test
    public void testRec73() throws Exception {
        init("DLex04.manchester");

        testRecognition("<_Master>", 0, 0.73);
    }



    @Test
    public void testRec74() throws Exception {
        init("DLex04.manchester");

        testRecognition("<_Educatrix>", 0, 0.83);
    }

    @Test
    public void testRec75() throws Exception {
        init("DLex04.manchester");
        testRecognition("<_Maestro>", 0, 0.73);
    }





















    @Test
    public void testSub91() throws Exception {
        init("DLex5.manchester");
        testSubsumption("<_Klass>", "<_Alias>", 0, 0);
    }

    @Test
    public void testSub92() throws Exception {
        init("DLex5.manchester");
        testSubsumption("<_Alias>", "<_Klass>", 0, 0);
    }

    @Test
    public void testSub93() throws Exception {
        init("DLex5.manchester");
        testSubsumption("<_Klass2>", "<_Klass3>", 1, 1);
    }

    @Test
    public void testSub94() throws Exception {
        init("DLex5.manchester");
        testSubsumption("<_Klass3>", "<_Klass2>", 0, 1);
    }



    @Test
    public void testSub95() throws Exception {
        init("DLex5.manchester");
        testSubsumption("<_Klass5>", "<_Klass4>", 1, 1);
    }

    @Test
    public void testSub96() throws Exception {
        init("DLex5.manchester");
        testSubsumption("<_Klass4>", "<_Klass5>", 0, 1);
    }



    @Test
    public void testSub97() throws Exception {
        init("DLex5.manchester");
        testSubsumption("<_Worker>", "<_GeneralWorker>", 1, 1);
    }
    @Test
    public void testSub98() throws Exception {
        init("DLex5.manchester");
        testSubsumption("<_GeneralWorker>", "<_Worker>", 0,1);
    }


    @Test
    public void testSub99() throws Exception {
        init("DLex5.manchester");
        testSubsumption("<_Professor>", "<_Worker>", 0,1);
    }


}