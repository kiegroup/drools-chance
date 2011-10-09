package org.drools.pmml_4_0.predictive.models;


import junit.framework.Assert;
import org.drools.ClassObjectFilter;
import org.drools.definition.type.FactType;
import org.drools.informer.Answer;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.drools.runtime.rule.Variable;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NeuralNetworkTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = false;
    private static final String source1 = "org/drools/pmml_4_0/test_ann_regression.xml";
    private static final String source2 = "org/drools/pmml_4_0/test_ann_iris.xml";
    private static final String source22 = "org/drools/pmml_4_0/test_ann_iris_v2.xml";
    private static final String source23 = "org/drools/pmml_4_0/test_ann_iris_prediction.xml";
    private static final String source3 = "org/drools/pmml_4_0/test_miningSchema.xml";
    private static final String source4 = "org/drools/pmml_4_0/test_ann_HEART.xml";
    private static final String source5 = "org/drools/pmml_4_0/test_ann_mixed_simple.xml";
    private static final String source6 = "org/drools/pmml_4_0/mock_ptsd.pmml";
    private static final String source7 = "org/drools/pmml_4_0/mock_cold.pmml";
    private static final String packageName = "org.drools.pmml_4_0.test";




    @Test
    public void testANN() throws Exception {
        setKSession(getModelSession(source1,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();  //init model
        Assert.assertEquals(33, getNumAssertedSynapses());


        getKSession().getWorkingMemoryEntryPoint("in_Gender").insert("male");
        getKSession().getWorkingMemoryEntryPoint("in_NoOfClaims").insert("3");
        getKSession().getWorkingMemoryEntryPoint("in_Scrambled").insert(7);
        getKSession().getWorkingMemoryEntryPoint("in_Domicile").insert("urban");
        getKSession().getWorkingMemoryEntryPoint("in_AgeOfCar").insert(8.0);

        getKSession().fireAllRules();

        Thread.sleep(200);
        System.err.println(reportWMObjects(getKSession()));



        Assert.assertEquals(2930.0, Math.floor(queryDoubleField("OutAmOfClaims", "NeuralInsurance")));





    }




     @Test
    public void testANNCompilation() throws Exception {
        setKSession(getModelSession(source3,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


    }



    @Test
    public void testCold() throws Exception {
        setKSession(getModelSession(source7,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();  //init model

        getKSession().getWorkingMemoryEntryPoint("in_Temp").insert(28.0);

        getKSession().fireAllRules();

        Thread.sleep(200);
        System.err.println(reportWMObjects(getKSession()));

        Assert.assertEquals( 0.44, queryDoubleField("Cold", "MockCold" ), 1e-6 );

    }


    @Test
    public void testPTSD() throws Exception {
        setKSession(getModelSession(source6,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();  //init model

        getKSession().getWorkingMemoryEntryPoint("in_Gender").insert("male");
        getKSession().getWorkingMemoryEntryPoint("in_Alcohol").insert("yes");
        getKSession().getWorkingMemoryEntryPoint("in_Deployments").insert("1");
//        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(30.2);

        getKSession().fireAllRules();

        Answer ans2 = new Answer(getQId("MockPTSD","Age"),"30.2");
        getKSession().insert(ans2);

        getKSession().fireAllRules();

        Thread.sleep(200);
        System.err.println(reportWMObjects(getKSession()));

        Assert.assertEquals( 0.2802, queryDoubleField("PTSD", "MockPTSD" ) );

    }

    private String getQId(String model, String field) {
        return (String) getKSession().getQueryResults( "getItemId", model+"_"+field, model, Variable.v ).iterator().next().get("$id");

    }

    @Test
    @Ignore
    public void testIris() throws Exception {
        setKSession(getModelSession(source2,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        getKSession().fireAllRules();  //init model
        Assert.assertEquals(21, getNumAssertedSynapses());


        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(2.2);
        getKSession().getWorkingMemoryEntryPoint("in_PetalWidth").insert(4.1);
        getKSession().getWorkingMemoryEntryPoint("in_SepalLength").insert(2.3);
        getKSession().getWorkingMemoryEntryPoint("in_SepalWidth").insert(1.8);
        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));


        Assert.assertEquals(0.001,
                truncN(queryDoubleField("IRIS_MLP_7", "IRIS_MLP"), 3));
        Assert.assertEquals(0.281,
                truncN(queryDoubleField("IRIS_MLP_8", "IRIS_MLP"), 3));
        Assert.assertEquals(0.717,
                truncN(queryDoubleField("IRIS_MLP_9", "IRIS_MLP"), 3));

        Assert.assertEquals("Iris_virginica",
                queryStringField("NSPECIES_Iris_virginica", "IRIS_MLP"));
        Assert.assertEquals("Iris_setosa",
                queryStringField("NSPECIES_Iris_setosa", "IRIS_MLP"));
        Assert.assertEquals("Iris_versicolor",
                queryStringField("NSPECIES_Iris_versicolor", "IRIS_MLP"));


    }





    @Test
    @Ignore
    public void testIris2() throws Exception {
        setKSession(getModelSession(source22,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        getKSession().fireAllRules();  //init model
        Assert.assertEquals(12, getNumAssertedSynapses());


        getKSession().getWorkingMemoryEntryPoint("in_PETALLEN").insert(40);
        getKSession().getWorkingMemoryEntryPoint("in_PETALWID").insert(10);
        getKSession().getWorkingMemoryEntryPoint("in_SEPALLEN").insert(44);
        getKSession().getWorkingMemoryEntryPoint("in_SEPALWID").insert(33);
        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));


        Assert.assertEquals(0.001,
                truncN(queryDoubleField("IRIS_MLP_7", "IRIS_MLP"), 3));
        Assert.assertEquals(0.281,
                truncN(queryDoubleField("IRIS_MLP_8", "IRIS_MLP"), 3));
        Assert.assertEquals(0.717,
                truncN(queryDoubleField("IRIS_MLP_9", "IRIS_MLP"), 3));

        Assert.assertEquals("Iris_virginica",
                queryStringField("NSPECIES_Iris_virginica", "IRIS_MLP"));
        Assert.assertEquals("Iris_setosa",
                queryStringField("NSPECIES_Iris_setosa", "IRIS_MLP"));
        Assert.assertEquals("Iris_versicolor",
                queryStringField("NSPECIES_Iris_versicolor", "IRIS_MLP"));


    }




    @Test
    public void testIris3() throws Exception {
        setKSession(getModelSession(source23,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        getKSession().fireAllRules();  //init model
        Assert.assertEquals(6, getNumAssertedSynapses());


        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(40);
        getKSession().getWorkingMemoryEntryPoint("in_PETALWID").insert(10);
        getKSession().getWorkingMemoryEntryPoint("in_SPECIES").insert("virginica");
        getKSession().getWorkingMemoryEntryPoint("in_SEPALWID").insert(33);
        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));

        Assert.assertEquals(60.0, queryIntegerField("OutSepLen", "Neupre"));

    }





    @Test
    public void testSimpleANN() throws Exception {
        // from mining schema test, simple network with fieldRef as output
        setKSession(getModelSession(source3,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(4);
        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(3.5);
        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Out2"),
                true, false,"IRIS_MLP",1.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Out1"),
                true, false,"IRIS_MLP",0.0);

    }






    @Test
    @Ignore
    public void testHeart() throws Exception {
        setKSession(getModelSession(source4,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        getKSession().fireAllRules();  //init model
        Assert.assertEquals(81, getNumAssertedSynapses());


        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(83.0);
        getKSession().getWorkingMemoryEntryPoint("in_Ca").insert(1.0);
        getKSession().getWorkingMemoryEntryPoint("in_Chol").insert(5.0);
        getKSession().getWorkingMemoryEntryPoint("in_Cp").insert("asympt");
        getKSession().getWorkingMemoryEntryPoint("in_Exang").insert("yes");
        getKSession().getWorkingMemoryEntryPoint("in_Fbs").insert("t");
        getKSession().getWorkingMemoryEntryPoint("in_Oldpeak").insert(1.0);
        getKSession().getWorkingMemoryEntryPoint("in_Restecg").insert("normal");
        getKSession().getWorkingMemoryEntryPoint("in_Sex").insert("male");
        getKSession().getWorkingMemoryEntryPoint("in_Slope").insert("flat");
        getKSession().getWorkingMemoryEntryPoint("in_Thal").insert("normal");
        getKSession().getWorkingMemoryEntryPoint("in_Thalach").insert(3.3);
        getKSession().getWorkingMemoryEntryPoint("in_Trestbps").insert(2.5);


        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));
        Assert.fail("Probabilty feature not yet done");
    }







    @Test
    public void testOverride() throws Exception {
        setKSession(getModelSession(source3,VERBOSE));
        setKbase(getKSession().getKnowledgeBase());

        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(2.2);
        getKSession().fireAllRules();

        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(5);
        getKSession().fireAllRules();

        System.err.println(reportWMObjects(getKSession()));

        FactType out1 = getKbase().getFactType("org.drools.pmml_4_0.test","Out1");
        FactType out2 = getKbase().getFactType("org.drools.pmml_4_0.test","Out2");
        FactType nump = getKbase().getFactType("org.drools.pmml_4_0.test","PetalNumber");

        assertEquals(1,getKSession().getObjects(new ClassObjectFilter(out1.getFactClass())).size());
        assertEquals(1,getKSession().getObjects(new ClassObjectFilter(out2.getFactClass())).size());
        assertEquals(2,getKSession().getObjects(new ClassObjectFilter(nump.getFactClass())).size());



        getKSession().getWorkingMemoryEntryPoint("in_PetalLength").insert(2.5);
        getKSession().getWorkingMemoryEntryPoint("in_PetalNumber").insert(6);
        getKSession().fireAllRules();


        System.err.println(reportWMObjects(getKSession()));

        assertEquals(1,getKSession().getObjects(new ClassObjectFilter(out1.getFactClass())).size());
        assertEquals(1,getKSession().getObjects(new ClassObjectFilter(out2.getFactClass())).size());
        assertEquals(3,getKSession().getObjects(new ClassObjectFilter(nump.getFactClass())).size());


    }




    @Test      @Ignore
    public void testSimpleMixed() throws Exception {
        setKSession(getModelSession(source5, VERBOSE));
        setKbase(getKSession().getKnowledgeBase());


        getKSession().fireAllRules();  //init model


        getKSession().getWorkingMemoryEntryPoint("in_Domicile").insert("urban");
        getKSession().fireAllRules();  //init model


        System.err.println(reportWMObjects(getKSession()));

        fail("TODO");

    }



    private int getNumAssertedSynapses() {
        Class<?> synClass = getKSession().getKnowledgeBase().getFactType(packageName,"Synapse").getFactClass();
        return getKSession().getObjects(new ClassObjectFilter(synClass)).size();
    }



    private double truncN(double x, int numDecimal) {
        return (Math.floor(x * Math.pow(10,numDecimal))) * Math.pow(10,-numDecimal);
    }

}
