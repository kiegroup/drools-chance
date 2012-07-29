package org.drools.scorecards;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;
import org.dmg.pmml_4_1.Attribute;
import org.dmg.pmml_4_1.Characteristic;
import org.dmg.pmml_4_1.Characteristics;
import org.dmg.pmml_4_1.PMML;
import org.dmg.pmml_4_1.Scorecard;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.scorecards.example.Applicant;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class ScorecardReasonCodeTest {
    private static PMML pmmlDocument;
    private static String drl;

    @Before
    public void setUp() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        pmmlDocument = scorecardCompiler.getPMMLDocument();
        drl = scorecardCompiler.getDRL();
    }

    @Test
    public void testPMMLDocument() throws Exception {
        Assert.assertNotNull(pmmlDocument);
        // create a JAXBContext for the PMML class
        JAXBContext ctx = JAXBContext.newInstance(PMML.class);
        Marshaller marshaller = ctx.createMarshaller();
        // the property JAXB_FORMATTED_OUTPUT specifies whether or not the
        // marshalled XML data is formatted with linefeeds and indentation
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // marshal the data in the Java content tree
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(pmmlDocument, stringWriter);
        assertTrue(stringWriter.toString().length() > 0);
        //System.out.println(stringWriter.toString());
    }

    @Test
    public void testAbsenceOfReasonCodes() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_c.xls"));
        PMML pmml = scorecardCompiler.getPMMLDocument();
        for (Object serializable : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                assertFalse(((Scorecard) serializable).isUseReasonCodes());
            }
        }
    }

    @Test
    public void testUseReasonCodes() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                assertTrue(((Scorecard)serializable).isUseReasonCodes());
            }
        }
    }

    @Test
    public void testReasonCodes() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(4, characteristics.getCharacteristics().size());
                        for (Characteristic characteristic : characteristics.getCharacteristics()){
                            for (Attribute attribute : characteristic.getAttributes()){
                                assertNotNull(attribute.getReasonCode());
                            }
                        }
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testMissingReasonCodes() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_reason_error");
        assertEquals(2, scorecardCompiler.getScorecardParseErrors().size());
        assertEquals("$F$15", scorecardCompiler.getScorecardParseErrors().get(0).getErrorLocation());
        assertEquals("$F$24", scorecardCompiler.getScorecardParseErrors().get(1).getErrorLocation());
    }

    @Test
    public void testReasonCodesCombinations() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_char_reasoncode");
        assertEquals(0, scorecardCompiler.getScorecardParseErrors().size());
        String drl = scorecardCompiler.getDRL();
        assertNotNull(drl);
        //System.out.println(drl);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println(error.getMessage());
        }
        assertFalse( kbuilder.hasErrors() );

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List<String> reasonCodeList = new ArrayList<String>();
        //ASSERT AND FIRE
        Applicant applicant = new Applicant();
        applicant.setAge(10);
        session.insert(applicant);
        session.setGlobal("$reasonCodeList",reasonCodeList);
        session.fireAllRules();
        session.dispose();
        //age = 30, validLicence -1
        assertTrue(29 == applicant.getTotalScore());
        //age-reasoncode=AGE02, license-reasoncode=VL002
        assertEquals(2, reasonCodeList.size());
        assertTrue(reasonCodeList.contains("AGE02"));
        assertTrue(reasonCodeList.contains("VL099"));

        session = kbase.newStatefulKnowledgeSession();
        //ASSERT AND FIRE
        applicant = new Applicant();
        applicant.setAge(0);
        applicant.setOccupation("SKYDIVER");
        session.insert(applicant);
        reasonCodeList = new ArrayList<String>();
        session.setGlobal("$reasonCodeList",reasonCodeList);
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertTrue(-1 == applicant.getTotalScore());
        assertEquals(3, reasonCodeList.size());
        //[AGE01, VL002, OCC01]
        assertTrue(reasonCodeList.contains("AGE01"));
        assertTrue(reasonCodeList.contains("VL099"));
        assertTrue(reasonCodeList.contains("OCC99"));

        session = kbase.newStatefulKnowledgeSession();
        reasonCodeList = new ArrayList<String>();
        session.setGlobal("$reasonCodeList",reasonCodeList);
        //ASSERT AND FIRE
        applicant = new Applicant();
        applicant.setAge(20);
        applicant.setOccupation("TEACHER");
        applicant.setResidenceState("AP");
        applicant.setValidLicense(true);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41,(int)applicant.getTotalScore());
        //[OCC02, AGE03, VL001, RS001]
        assertEquals(4, reasonCodeList.size());
        assertTrue(reasonCodeList.contains("OCC99"));
        assertTrue(reasonCodeList.contains("AGE03"));
        assertTrue(reasonCodeList.contains("VL001"));
        assertTrue(reasonCodeList.contains("RS001"));
    }

    @Test
    public void testGlobalInDrl() throws Exception {
        assertNotNull(drl);
        assertEquals(1, StringUtil.countMatches(drl, "global "));
        //System.out.println(drl);
    }

    @Test
    public void testDRLExecution() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println(error.getMessage());
        }
        assertFalse( kbuilder.hasErrors() );

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        List<String> reasonCodeList = new ArrayList<String>();
        //ASSERT AND FIRE
        Applicant applicant = new Applicant();
        applicant.setAge(10);
        session.insert(applicant);
        session.setGlobal("$reasonCodeList",reasonCodeList);
        session.fireAllRules();
        session.dispose();
        //age = 30, validLicence -1
        assertTrue(29 == applicant.getTotalScore());
        //age-reasoncode=AGE02, license-reasoncode=VL002
        assertEquals(2, reasonCodeList.size());
        assertTrue(reasonCodeList.contains("AGE02"));
        assertTrue(reasonCodeList.contains("VL002"));

        session = kbase.newStatefulKnowledgeSession();
        //ASSERT AND FIRE
        applicant = new Applicant();
        applicant.setAge(0);
        applicant.setOccupation("SKYDIVER");
        session.insert(applicant);
        reasonCodeList = new ArrayList<String>();
        session.setGlobal("$reasonCodeList",reasonCodeList);
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertTrue(-1 == applicant.getTotalScore());
        assertEquals(3, reasonCodeList.size());
        //[AGE01, VL002, OCC01]
        assertTrue(reasonCodeList.contains("AGE01"));
        assertTrue(reasonCodeList.contains("VL002"));
        assertTrue(reasonCodeList.contains("OCC01"));

        session = kbase.newStatefulKnowledgeSession();
        reasonCodeList = new ArrayList<String>();
        session.setGlobal("$reasonCodeList",reasonCodeList);
        //ASSERT AND FIRE
        applicant = new Applicant();
        applicant.setAge(20);
        applicant.setOccupation("TEACHER");
        applicant.setResidenceState("AP");
        applicant.setValidLicense(true);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41,(int)applicant.getTotalScore());
        //[OCC02, AGE03, VL001, RS001]
        assertEquals(4, reasonCodeList.size());
        assertTrue(reasonCodeList.contains("OCC02"));
        assertTrue(reasonCodeList.contains("AGE03"));
        assertTrue(reasonCodeList.contains("VL001"));
        assertTrue(reasonCodeList.contains("RS001"));
    }

}
