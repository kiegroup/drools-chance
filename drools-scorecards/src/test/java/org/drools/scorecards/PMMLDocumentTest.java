package org.drools.scorecards;

import java.io.StringWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;
import org.dmg.pmml_4_1.DataDictionary;
import org.dmg.pmml_4_1.Extension;
import org.dmg.pmml_4_1.Header;
import org.dmg.pmml_4_1.PMML;
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
        // create a JAXBContext for the Scorecard class
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

}
