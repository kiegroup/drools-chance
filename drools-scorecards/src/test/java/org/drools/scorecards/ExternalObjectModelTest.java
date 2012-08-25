package org.drools.scorecards;

import java.io.StringWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.dmg.pmml_4_1.Extension;
import org.dmg.pmml_4_1.Output;
import org.dmg.pmml_4_1.OutputField;
import org.dmg.pmml_4_1.PMML;
import org.dmg.pmml_4_1.Scorecard;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.*;

public class ExternalObjectModelTest {
    private static String drl;
    private PMML pmmlDocument;

    @Before
    public void setUp() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_externalmodel.xls")) ) {
            pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull(pmmlDocument);
            drl = scorecardCompiler.getDRL(ScorecardCompiler.DrlType.EXTERNAL_OBJECT_MODEL);
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
        System.out.println(stringWriter.toString());
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
        System.out.println(drl);
    }

    @Ignore @Test
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
        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );
        DroolsScorecard scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = 5, age = 25, validLicence -1
        assertEquals(29.0,scorecard.getCalculatedScore());

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "occupation", "SKYDIVER");
        scorecardType.set(scorecard, "age", 0);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertTrue(-1 == scorecard.getCalculatedScore());

        session = kbase.newStatefulKnowledgeSession();
        scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "residenceState", "AP");
        scorecardType.set(scorecard, "occupation", "TEACHER");
        scorecardType.set(scorecard, "age", 20);
        scorecardType.set(scorecard, "validLicense", true);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41.0,scorecard.getCalculatedScore());
    }
}
