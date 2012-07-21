package org.drools.scorecards;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;
import org.dmg.pmml_4_1.Attribute;
import org.dmg.pmml_4_1.Characteristics;
import org.dmg.pmml_4_1.DataDictionary;
import org.dmg.pmml_4_1.Header;
import org.dmg.pmml_4_1.MiningSchema;
import org.dmg.pmml_4_1.Output;
import org.dmg.pmml_4_1.PMML;
import org.dmg.pmml_4_1.Scorecard;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class PMMLDocumentTest {

    private static PMML pmmlDocument;

    @Before
    public void setUp() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_c.xls"));
        pmmlDocument = scorecardCompiler.getPMMLDocument();
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
        System.out.println(stringWriter.toString());
    }

    @Test
    public void testHeader() throws Exception {
        Header header = pmmlDocument.getHeader();
        assertNotNull(header);
        assertNotNull(ScorecardPMMLUtils.getExtensionValue(header.getExtensions(), PMMLExtensionNames.SCORECARD_PACKAGE));
        assertNotNull(ScorecardPMMLUtils.getExtensionValue(header.getExtensions(), PMMLExtensionNames.SCORECARD_IMPORTS));
    }

    @Test
    public void testDataDictionary() throws Exception {
        DataDictionary dataDictionary = pmmlDocument.getDataDictionary();
        assertNotNull(dataDictionary);
        assertEquals(3, dataDictionary.getNumberOfFields().intValue());
        assertEquals("age", dataDictionary.getDataFields().get(0).getName());
        assertEquals("occupation",dataDictionary.getDataFields().get(1).getName());
        assertEquals("residenceState", dataDictionary.getDataFields().get(2).getName());
    }

    @Test
    public void testMiningSchema() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof MiningSchema){
                        MiningSchema miningSchema = ((MiningSchema)obj);
                        assertEquals(3, miningSchema.getMiningFields().size());
                        assertEquals("age", miningSchema.getMiningFields().get(0).getName());
                        assertEquals("occupation",miningSchema.getMiningFields().get(1).getName());
                        assertEquals("residenceState", miningSchema.getMiningFields().get(2).getName());
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testCharacteristicsAndAttributes() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(3, characteristics.getCharacteristics().size());
                        assertEquals("AgeScore", characteristics.getCharacteristics().get(0).getName());
                        assertEquals("$B$11", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(0).getExtensions(), "cellRef"));

                        assertEquals("OccupationScore",characteristics.getCharacteristics().get(1).getName());
                        assertEquals("$B$19", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(1).getExtensions(), "cellRef"));

                        assertEquals("ResidenceStateScore",characteristics.getCharacteristics().get(2).getName());
                        assertEquals("$B$25", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(2).getExtensions(), "cellRef"));
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testAgeScoreCharacteristic() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(3, characteristics.getCharacteristics().size());
                        assertEquals("AgeScore", characteristics.getCharacteristics().get(0).getName());
                        assertEquals("$B$11", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(0).getExtensions(), "cellRef"));

                        assertNotNull(characteristics.getCharacteristics().get(0).getAttributes());
                        assertEquals(4, characteristics.getCharacteristics().get(0).getAttributes().size());

                        Attribute attribute = characteristics.getCharacteristics().get(0).getAttributes().get(0);
                        assertEquals("$C$13", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());

                        attribute = characteristics.getCharacteristics().get(0).getAttributes().get(1);
                        assertEquals("$C$14", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getCompoundPredicate());

                        attribute = characteristics.getCharacteristics().get(0).getAttributes().get(2);
                        assertEquals("$C$15", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getCompoundPredicate());

                        attribute = characteristics.getCharacteristics().get(0).getAttributes().get(3);
                        assertEquals("$C$16", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testOccupationScoreCharacteristic() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(3, characteristics.getCharacteristics().size());

                        assertNotNull(characteristics.getCharacteristics().get(1).getAttributes());
                        assertEquals(3, characteristics.getCharacteristics().get(1).getAttributes().size());

                        Attribute attribute = characteristics.getCharacteristics().get(1).getAttributes().get(0);
                        assertEquals("$C$21", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "description"));
                        assertEquals("skydiving is a risky occupation", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "description"));
                        assertNotNull(attribute.getSimplePredicate());

                        attribute = characteristics.getCharacteristics().get(1).getAttributes().get(1);
                        assertEquals("$C$22", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimpleSetPredicate());

                        attribute = characteristics.getCharacteristics().get(1).getAttributes().get(2);
                        assertEquals("$C$23", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testResidenceStateScoreCharacteristic() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(3, characteristics.getCharacteristics().size());

                        assertNotNull(characteristics.getCharacteristics().get(2).getAttributes());
                        assertEquals(3, characteristics.getCharacteristics().get(2).getAttributes().size());

                        Attribute attribute = characteristics.getCharacteristics().get(2).getAttributes().get(0);
                        assertEquals("$C$27", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());

                        attribute = characteristics.getCharacteristics().get(2).getAttributes().get(1);
                        assertEquals("$C$28", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());

                        attribute = characteristics.getCharacteristics().get(2).getAttributes().get(2);
                        assertEquals("$C$29", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testScorecardWithExtensions() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                Scorecard scorecard = (Scorecard)serializable;
                assertEquals("Sample Score",scorecard.getModelName());
                assertNotNull(ScorecardPMMLUtils.getExtension(scorecard.getExtensionsAndCharacteristicsAndMiningSchemas(), PMMLExtensionNames.SCORECARD_OBJECT_CLASS));
                assertNotNull(ScorecardPMMLUtils.getExtension(scorecard.getExtensionsAndCharacteristicsAndMiningSchemas(), PMMLExtensionNames.SCORECARD_BOUND_VAR_NAME));
                return;
            }
        }
        fail();
    }

    @Test
    public void testOutput() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                Scorecard scorecard = (Scorecard)serializable;
                for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if ( obj instanceof Output) {
                        Output output = (Output)obj;
                        assertEquals(1, output.getOutputFields().size());
                        assertNotNull(output.getOutputFields().get(0));
                        assertEquals("totalScore", output.getOutputFields().get(0).getName());
                        assertEquals("Final Score", output.getOutputFields().get(0).getDisplayName());
                        assertEquals("double", output.getOutputFields().get(0).getDataType().value());
                        assertEquals("predictedValue", output.getOutputFields().get(0).getFeature().value());
                        return;
                    }
                }
            }
        }
        fail();
    }
}
