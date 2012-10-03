package org.drools.scorecards;

import java.util.List;

import org.dmg.pmml.pmml_4_1.descr.Extension;
import org.dmg.pmml.pmml_4_1.descr.Output;
import org.dmg.pmml.pmml_4_1.descr.OutputField;
import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.dmg.pmml.pmml_4_1.descr.Scorecard;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.scorecards.example.Applicant;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.drools.scorecards.ScorecardCompiler.DrlType.*;

public class ExternalObjectModelTest {
    private static String drl;
    private PMML pmmlDocument;
    private static ScorecardCompiler scorecardCompiler;
    @Before
    public void setUp() throws Exception {
        scorecardCompiler = new ScorecardCompiler(EXTERNAL_OBJECT_MODEL);
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_externalmodel.xls")) ) {
            pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull(pmmlDocument);
            drl = scorecardCompiler.getDRL();
            //System.out.println(drl);
        } else {
            fail("failed to parse scoremodel Excel.");
        }
    }

    @Test
    public void testPMMLNotNull() throws Exception {
        assertNotNull(pmmlDocument);
    }

    @Test
    public void testPMMLToString() throws Exception {
        String pmml = scorecardCompiler.getPMML();
        assertNotNull(pmml);
        assertTrue(pmml.length() > 0);
    }

    @Test
    public void testPMMLCustomOutput() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                Scorecard scorecard = (Scorecard)serializable;
                for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if ( obj instanceof Output) {
                        Output output = (Output)obj;
                        final List<OutputField> outputFields = output.getOutputFields();
                        assertEquals(1, outputFields.size());
                        final OutputField outputField = outputFields.get(0);
                        assertNotNull(outputField);
                        assertEquals("totalScore", outputField.getName());
                        assertEquals("Final Score", outputField.getDisplayName());
                        assertEquals("double", outputField.getDataType().value());
                        assertEquals("predictedValue", outputField.getFeature().value());
                        final Extension extension = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_CLASS);
                        assertNotNull(extension);
                        assertEquals("org.drools.scorecards.example.Applicant",extension.getValue());
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testDrlNoNull() throws Exception {
        assertNotNull(drl);
        assertTrue(drl.length() > 0);
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
        Applicant applicant = new Applicant();
        applicant.setAge(10);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = 0, age = 30, validLicence -1
        assertEquals(29.0,applicant.getTotalScore());

        session = kbase.newStatefulKnowledgeSession();
        applicant = new Applicant();
        applicant.setOccupation("SKYDIVER");
        applicant.setAge(0);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertEquals(-1.0, applicant.getTotalScore());

        session = kbase.newStatefulKnowledgeSession();
        applicant = new Applicant();
        applicant.setResidenceState("AP");
        applicant.setOccupation("TEACHER");
        applicant.setAge(20);
        applicant.setValidLicense(true);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41.0,applicant.getTotalScore());
    }
}
