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

package org.drools.pmml.pmml_4_1.informer;


import org.drools.ClassObjectFilter;
import org.drools.definition.type.FactType;
import org.drools.informer.*;
import org.drools.pmml.pmml_4_1.DroolsAbstractPMMLTest;
import org.drools.runtime.rule.Variable;
import org.junit.After;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;



public class QuestionnaireTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml/pmml_4_1/test_miningSchema.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_1/test_ann_iris_prediction.xml";
    private static final String sourceMix = "org/drools/pmml/pmml_4_1/test_ann_mixed_inputs.xml";
    private static final String source99 = "org/drools/pmml/pmml_4_1/test_ann_iris.xml";


    private static final String packageName = "org.drools.pmml.pmml_4_1.test";


    @After
    public void tearDown() {
        getKSession().dispose();
    }

    @Test
    public void testUnquestionable() {
        setKSession( getModelSession( source99, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        getKSession().fireAllRules();

        System.err.println( reportWMObjects( getKSession() ) );

        assertEquals( 0, getKSession().getObjects( new ClassObjectFilter( Questionnaire.class ) ).size() );
    }

    @Test
    public void testQuestionable() {
        setKSession( getModelSession( source, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        getKSession().fireAllRules();

        System.err.println( reportWMObjects( getKSession() ) );

        Collection questionnaires = getKSession().getObjects( new ClassObjectFilter( Questionnaire.class ) );
        assertEquals( 1, questionnaires.size() );
        Questionnaire quest = (Questionnaire) questionnaires.iterator().next();
        assertEquals( "Test_MLP", quest.getType() );
        assertEquals( 2, quest.getNumAvailableItems() );
        assertEquals( 2, quest.getItemList().size() );
        assertEquals( quest.getId(), quest.getContext() );
    }


    @Test
    public void testOverride() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint("in_Feat1").insert(2.2);
        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint("in_Feat2").insert(5);
        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));


        FactType feat1 = getKbase().getFactType("org.drools.pmml.pmml_4_1.test","Feat1");
        FactType feat2 = getKbase().getFactType("org.drools.pmml.pmml_4_1.test","Feat2");


        Collection c = getKSession().getObjects(new ClassObjectFilter(DomainModelAssociation.class));
        Iterator iter = c.iterator();
        assertEquals(2, c.size());
        DomainModelAssociation dma1 = (DomainModelAssociation) iter.next();
        if (dma1.getObject().getClass().equals(feat2.getFactClass())) {
            assertEquals(5,feat2.get(dma1.getObject(),"value"));
        } else if (dma1.getObject().getClass().equals(feat1.getFactClass())) {
            assertEquals(2.2,feat1.get(dma1.getObject(),"value"));
        }

        DomainModelAssociation dma2 = (DomainModelAssociation) iter.next();
        if (dma2.getObject().getClass().equals(feat2.getFactClass())) {
            assertEquals(5,feat2.get(dma2.getObject(),"value"));
        } else if (dma2.getObject().getClass().equals(feat1.getFactClass())) {
            assertEquals(2.2,feat1.get(dma2.getObject(),"value"));
        }


        getKSession().getWorkingMemoryEntryPoint("in_Feat1").insert(2.5);
        getKSession().fireAllRules();

        System.out.println( "--------------------------------------------------------------------");

        getKSession().getWorkingMemoryEntryPoint("in_Feat2").insert(6);
        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));


        c = getKSession().getObjects(new ClassObjectFilter(DomainModelAssociation.class));
        iter = c.iterator();
        assertEquals(2, c.size());
        while (iter.hasNext()) {
            DomainModelAssociation dma = (DomainModelAssociation) iter.next();

            if (dma.getObject().getClass().equals(feat2.getFactClass())) {
                assertEquals(6,feat2.get(dma.getObject(),"value"));
            } else if (dma.getObject().getClass().equals(feat1.getFactClass())) {
                Object val = feat1.get(dma.getObject(),"value");
                System.out.println("Check " + val);
                assertTrue((val.equals(new Double(2.2)) || val.equals(new Double(2.5))));
            }
        }





    }







    @Test
    public void testMixModel() throws Exception {
        setKSession(getModelSession(new String[] {sourceMix},VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();



        Answer ans1 = new Answer( getQId( "Mixed", "Gender" ), "male" );

        getKSession().insert(ans1);
        getKSession().getWorkingMemoryEntryPoint("in_Domicile").insert("rural");

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_0"),true,false,"Mixed",1.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_1"),true,false,"Mixed",0.0);

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_6"),true,false,"Mixed",0.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_7"),true,false,"Mixed",1.0);



        Answer ans2 = new Answer(getQId("Mixed","Scrambled"),"7");
        getKSession().insert(ans2);


        Answer ans3 = new Answer(getQId("Mixed","NoOfClaims"),"1");
        getKSession().insert(ans3);

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_5"),true,false,"Mixed",7.0);

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_2"),true,false,"Mixed",0.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_3"),true,false,"Mixed",1.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_4"),true,false,"Mixed",0.0);



        Answer ans4 = new Answer(getQId("Mixed","AgeOfCar"),"-3");
        getKSession().insert(ans4);

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_8"),false,false,"Mixed",-3.0);


        Answer ans5 = new Answer(getQId("Mixed","AgeOfCar"),"4.0");
        getKSession().insert(ans5);

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Mixed_8"),true,false,"Mixed",4.0);

        System.err.println(reportWMObjects(getKSession()));


        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Out_Claims"),true,false,"Mixed",95.0);

    }






    @Test
    public void testGenerateInputsByAnswer() throws Exception {
        setKSession(getModelSession( source, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        getKSession().fireAllRules();

        System.err.println( reportWMObjects( getKSession() ) );

        String qid1 = getQId( "Test_MLP", "Feat1" );
        String qid2 = getQId( "Test_MLP", "Feat2" );

        Answer ans1 = new Answer( qid1, "2.5" );
        Answer ans2 = new Answer( qid2, "5" );

        getKSession().insert( ans1 );
        getKSession().insert( ans2 );

        getKSession().fireAllRules();

        FactType feat1 = getKbase().getFactType( "org.drools.pmml.pmml_4_1.test", "Feat1" );
        FactType feat2 = getKbase().getFactType( "org.drools.pmml.pmml_4_1.test", "Feat2" );

        Collection c = getKSession().getObjects( new ClassObjectFilter( feat2.getFactClass() ) );
        assertEquals( 1, c.size() );

        Iterator i2 = c.iterator();
        while ( i2.hasNext() ) {
            Object o = i2.next();
            if ( feat2.get( o, "context" ) != null )
                assertEquals( 5, feat2.get( o, "value" ) );
        }


        Collection d = getKSession().getObjects(new ClassObjectFilter( feat1.getFactClass() ) );
        assertEquals( 1, d.size( ) );

        Iterator i1 = d.iterator();
        while ( i1.hasNext() ) {
            Object o = i1.next();
            if ( feat1.get( o,"context" ) != null )
                assertEquals( 2.5, feat1.get( o, "value" ) );
        }


        System.err.println( reportWMObjects( getKSession() ) );

    }






    @Test
    public void testAnswerUpdate() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        FactType feat1 = getKbase().getFactType("org.drools.pmml.pmml_4_1.test","Feat1");
        FactType feat2 = getKbase().getFactType("org.drools.pmml.pmml_4_1.test","Feat2");

        getKSession().fireAllRules();

        String qid1 = getQId("Test_MLP","Feat1");
        String qid2 = getQId("Test_MLP","Feat2");

        Answer ans1 = new Answer(qid1,"2.5");
        Answer ans2 = new Answer(qid2,"-7");

        getKSession().insert(ans1);
        getKSession().insert(ans2);

        getKSession().fireAllRules();


        Collection c = getKSession().getObjects(new ClassObjectFilter(feat2.getFactClass()));
        assertEquals(1,c.size());

        Iterator i2 = c.iterator();
        while (i2.hasNext()) {
            Object o = i2.next();
            if (feat2.get(o,"context") != null)
                assertEquals(5, feat2.get(o,"value"));               // due to invalid as missing, and missing replaced by 5
        }



        System.out.println("\n\n\n\n\n\n\n\n\n\n");

        Answer ans3 = new Answer(qid2,"6");


        getKSession().insert(ans3);


        getKSession().fireAllRules();
        System.err.println(reportWMObjects(getKSession()));

        c = getKSession().getObjects(new ClassObjectFilter(feat2.getFactClass()));
        assertEquals(1,c.size());

        i2 = c.iterator();
        while (i2.hasNext()) {
            Object o = i2.next();
            if (feat2.get(o,"context") != null)
                assertEquals(6, feat2.get(o,"value"));
        }





    }





    @Test
    public void testInvalidValues() throws Exception {
        setKSession( getModelSession( new String[] { source2 }, VERBOSE ) );
        setKbase( getKSession().getKnowledgeBase() );

        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint( "in_PetalNum" ).insert( 5 );

        getKSession().fireAllRules();

        System.err.println( reportWMObjects( getKSession() ) );


        FactType type = getKbase().getFactType( packageName, "PetalNum" );
        checkFirstDataFieldOfTypeStatus( type, false, false, "Neuiris", 5 );

        assertEquals( 4, getKSession().getObjects(new ClassObjectFilter( InvalidAnswer.class ) ).size() );

        assertEquals( 0, getKSession().getObjects( new ClassObjectFilter( Note.class ) ).size() );

    }




    private String getQId( String model, String field ) {
        // ref : getItemId( String $type, String $context, String $id )

        String questId = (String) getKSession().getQueryResults( "getItemId", model, Variable.v, Variable.v ).iterator().next().get( "$id" );
        return (String) getKSession().getQueryResults( "getItemId", model+"_"+field, questId, Variable.v ).iterator().next().get( "$id" );

    }














    @Test
    public void testMultipleModels() throws Exception {
        setKSession( getModelSession( new String[] { source, source2 }, true ) );
        setKbase(getKSession().getKnowledgeBase());
        FactType petalNumType = getKbase().getFactType( packageName, "PetalNum" );
        FactType out = getKbase().getFactType( packageName, "OutSepLen" );
        FactType sepalType = getKbase().getFactType( packageName, "SepalLen" );

        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint( "in_Feat2" ).insert( 4 );
        getKSession().getWorkingMemoryEntryPoint( "in_PetalNum" ).insert( 4 );

        getKSession().getWorkingMemoryEntryPoint( "in_PetalWid" ).insert( 1 );
        getKSession().getWorkingMemoryEntryPoint( "in_SepalWid" ).insert( 30 );
        getKSession().getWorkingMemoryEntryPoint( "in_Species" ).insert( "virginica" );

        // one question for each model is not answered
        getKSession().fireAllRules();


        checkFirstDataFieldOfTypeStatus( petalNumType, false, false, "Neuiris", 4 );
        assertEquals( 2, getKSession().getObjects( new ClassObjectFilter( InvalidAnswer.class ) ).size() );
        assertEquals( 2, getKSession().getObjects( new ClassObjectFilter( Note.class ) ).size() );



        System.out.println("\n\n\n\n\n\n\n\n Before 2 \n");


        Answer ans1 = new Answer(getQId("Neuiris","PetalNum"),"40");
        getKSession().insert(ans1);
        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));


        checkFirstDataFieldOfTypeStatus(petalNumType,true,false,"Neuiris",40);

        assertEquals( 1, getKSession().getObjects(new ClassObjectFilter(InvalidAnswer.class)).size());
        assertEquals( 4,getKSession().getObjects(new ClassObjectFilter(Note.class)).size());

        checkFirstDataFieldOfTypeStatus(out,true,false,"Neuris",42);
        checkFirstDataFieldOfTypeStatus(sepalType,true,false,"Neuiris",42);




        System.out.println("\n\n\n\n\n\n\n\n Before 3 \n");


        Answer ans2 = new Answer(getQId("Neuiris","PetalNum"),"-7");
        getKSession().insert(ans2);

        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));


        checkFirstDataFieldOfTypeStatus(petalNumType,true,false,"Neuiris",40);

        assertEquals( 2, getKSession().getObjects(new ClassObjectFilter(InvalidAnswer.class)).size());
        assertEquals( 4,getKSession().getObjects(new ClassObjectFilter(Note.class)).size());

        checkFirstDataFieldOfTypeStatus(out,true,false,"Neuiris",42);
        assertEquals(1, getKSession().getObjects(new ClassObjectFilter(sepalType.getFactClass())).size());





        System.out.println("\n\n\n\n\n\n\n\n Now 3 \n");
//
//
//
//
//
        Answer ans3 = new Answer(getQId("Neuiris","PetalNum"),"101");
        getKSession().insert(ans3);

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(petalNumType,true,false,"Neuiris",101);

        assertEquals( 1, getKSession().getObjects(new ClassObjectFilter(InvalidAnswer.class)).size());
        assertEquals( 4,getKSession().getObjects(new ClassObjectFilter(Note.class)).size());

        checkFirstDataFieldOfTypeStatus(out,true,false,"Neuiris",23);
        checkFirstDataFieldOfTypeStatus(sepalType,true,false,"Neuiris",23);

//               System.err.println(reportWMObjects(getKSession()));

//               System.out.println("\n\n\n\n\n\n\n\n Now 4 \n");
//

    }




    @Test
    public void testModelProgress() throws Exception {


        setKSession(getModelSession(new String[] {sourceMix},true));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();

        FactType progressType = getKbase().getFactType( "org.drools.informer", "ProgressStatus" );


        Answer ans1 = new Answer( getQId( "Mixed", "Gender" ), "male" );

        getKSession().insert(ans1);
        getKSession().fireAllRules();

        Object progress = getKSession().getObjects( new ClassObjectFilter( progressType.getFactClass()) ).iterator().next();
        Integer progressNum = (Integer) progressType.get( progress, "percentage" );

        System.out.println( progressNum );
        assertEquals( 20, progressNum.intValue() );


        System.out.println("---------------------------------------------------------------------------------------");

        getKSession().getWorkingMemoryEntryPoint("in_Domicile").insert("rural");

        getKSession().fireAllRules();


        progress = getKSession().getObjects( new ClassObjectFilter( progressType.getFactClass()) ).iterator().next();
        progressNum = (Integer) progressType.get( progress, "percentage" );

        assertEquals( 40, progressNum.intValue() );

        System.out.println("---------------------------------------------------------------------------------------");


        Answer ans2 = new Answer(getQId("Mixed","Scrambled"),"7");
        getKSession().insert(ans2);


        Answer ans3 = new Answer(getQId("Mixed","NoOfClaims"),"1");
        getKSession().insert(ans3);

        getKSession().fireAllRules();


        progress = getKSession().getObjects( new ClassObjectFilter( progressType.getFactClass()) ).iterator().next();
        progressNum = (Integer) progressType.get( progress, "percentage" );

        assertEquals( 80, progressNum.intValue() );


        Answer ans4 = new Answer(getQId("Mixed","AgeOfCar"),"-3");
        getKSession().insert(ans4);

        getKSession().fireAllRules();


        progress = getKSession().getObjects( new ClassObjectFilter( progressType.getFactClass()) ).iterator().next();
        progressNum = (Integer) progressType.get( progress, "percentage" );

        System.err.println(reportWMObjects(getKSession()));

        assertEquals( 80, progressNum.intValue() );



        Answer ans5 = new Answer(getQId("Mixed","AgeOfCar"),"4.0");
        getKSession().insert(ans5);

        getKSession().fireAllRules();


        progress = getKSession().getObjects( new ClassObjectFilter( progressType.getFactClass()) ).iterator().next();
        progressNum = (Integer) progressType.get( progress, "percentage" );

        assertEquals( 100, progressNum.intValue() );


        System.err.println(reportWMObjects(getKSession()));



    }





}
