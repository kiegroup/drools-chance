package org.drools.scorecards;

import junit.framework.Assert;
import org.dmg.pmml.pmml_4_1.descr.Attribute;
import org.dmg.pmml.pmml_4_1.descr.Characteristic;
import org.dmg.pmml.pmml_4_1.descr.Characteristics;
import org.dmg.pmml.pmml_4_1.descr.PMML;
import org.dmg.pmml.pmml_4_1.descr.Scorecard;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.pmml.pmml_4_1.PMML4Compiler;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.drools.scorecards.ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES;

public class ReasonCodeTest {
    private static PMML pmmlDocument;
    private static String drl;
    private static ScorecardCompiler scorecardCompiler;

    @BeforeClass
    public static void setUp() throws Exception {
        scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        boolean compileResult = scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"));
        if (!compileResult) {
            for(ScorecardError error : scorecardCompiler.getScorecardParseErrors()){
                System.out.println("setup :"+error.getErrorLocation()+"->"+error.getErrorMessage());
            }
        }
        System.out.println(scorecardCompiler.getPMML());
        drl = scorecardCompiler.getDRL();
        Assert.assertNotNull(drl);
        assertTrue(drl.length() > 0);
        //System.out.println(drl);
    }

    @Test
    public void testPMMLDocument() throws Exception {
        pmmlDocument = scorecardCompiler.getPMMLDocument();
        Assert.assertNotNull(pmmlDocument);

        String pmml = scorecardCompiler.getPMML();
        Assert.assertNotNull(pmml);
        assertTrue(pmml.length() > 0);
        //System.out.println(pmml);
    }

    @Test
    public void testAbsenceOfReasonCodes() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
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
                assertEquals(100.0, ((Scorecard)serializable).getInitialScore());
                assertEquals("pointsBelow",((Scorecard)serializable).getReasonCodeAlgorithm());
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
                            for (Attribute
                                    attribute : characteristic.getAttributes()){
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
    public void testBaselineScores() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(4, characteristics.getCharacteristics().size());
                        assertEquals(10.0, characteristics.getCharacteristics().get(0).getBaselineScore());
                        assertEquals(99.0, characteristics.getCharacteristics().get(1).getBaselineScore());
                        assertEquals(12.0, characteristics.getCharacteristics().get(2).getBaselineScore());
                        assertEquals(0.0, characteristics.getCharacteristics().get(3).getBaselineScore());
                        assertEquals(25.0, ((Scorecard)serializable).getBaselineScore());
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
        assertEquals(3, scorecardCompiler.getScorecardParseErrors().size());
        assertEquals("$F$13", scorecardCompiler.getScorecardParseErrors().get(0).getErrorLocation());
        assertEquals("$F$22", scorecardCompiler.getScorecardParseErrors().get(1).getErrorLocation());
    }

    @Test
    public void testMissingBaselineScores() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_reason_error");
        assertEquals(3, scorecardCompiler.getScorecardParseErrors().size());
        assertEquals("$D$30", scorecardCompiler.getScorecardParseErrors().get(2).getErrorLocation());
    }

    @Test
    public void testReasonCodesCombinations() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_reasoncodes.xls"), "scorecards_char_reasoncode");
        assertEquals(0, scorecardCompiler.getScorecardParseErrors().size());
        String drl = scorecardCompiler.getDRL();
        assertNotNull(drl);
        System.out.println(drl);
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
        session.fireAllRules();

        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","ScoreCard" );

        Object scorecard = session.getObjects( new ClassObjectFilter( scorecardType.getFactClass() ) ).iterator().next();
        session.getWorkingMemoryEntryPoint( "in_Age" ).insert( 10.0 );
        session.getWorkingMemoryEntryPoint( "in_ValidLicense" ).insert( false );
        session.fireAllRules();

        assertEquals( 29.0, scorecardType.get( scorecard, "score" ) );
        Map codes = (Map) scorecardType.get( scorecard, "ranking" );
        Iterator code = codes.keySet().iterator();

        //age-reasoncode=AGE02, license-reasoncode=VL099 inherited
        assertEquals(2, codes.size());
        assertEquals("VL099", code.next());
        assertEquals("AGE02", code.next());


        session.getWorkingMemoryEntryPoint( "in_Age" ).insert( 0.0 );
        session.getWorkingMemoryEntryPoint( "in_Occupation" ).insert( "SKYDIVER" );
        session.fireAllRules();

        scorecard = session.getObjects( new ClassObjectFilter( scorecardType.getFactClass() ) ).iterator().next();
        assertEquals( -1.0, scorecardType.get( scorecard, "score" ) );
        codes = (Map) scorecardType.get( scorecard, "ranking" );
        code = codes.keySet().iterator();

        System.out.println( codes );
        assertEquals(3, codes.size());
        assertEquals("OCC99", code.next());
        assertEquals("VL099", code.next());
        assertEquals("AGE01", code.next());


        session.getWorkingMemoryEntryPoint( "in_Age" ).insert( 20.0 );
        session.getWorkingMemoryEntryPoint( "in_Occupation" ).insert( "TEACHER" );
        session.getWorkingMemoryEntryPoint( "in_ResidenceState" ).insert( "AP" );
        session.getWorkingMemoryEntryPoint( "in_ValidLicense" ).insert( true );
        session.fireAllRules();

        scorecard = session.getObjects( new ClassObjectFilter( scorecardType.getFactClass() ) ).iterator().next();
        assertEquals( 41.0, scorecardType.get( scorecard, "score" ) );
        codes = (Map) scorecardType.get( scorecard, "ranking" );
        code = codes.keySet().iterator();

        System.out.println( codes );
        assertEquals(4, codes.size());
        assertEquals("RS001", code.next());
        assertEquals("OCC99", code.next());
        assertEquals("VL001", code.next());
        assertEquals("AGE03", code.next());

    }

    @Test
    public void testDRLExecution() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println(error.getMessage());
        }
        assertFalse(kbuilder.hasErrors());

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.fireAllRules();

        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","ScoreCard" );

        Object scorecard = session.getObjects( new ClassObjectFilter( scorecardType.getFactClass() ) ).iterator().next();
        session.getWorkingMemoryEntryPoint( "in_Age" ).insert( 10.0 );
        session.getWorkingMemoryEntryPoint( "in_ValidLicense" ).insert( false );
        session.fireAllRules();

        assertEquals( 129.0, scorecardType.get( scorecard, "score" ) );
        Map codes = (Map) scorecardType.get( scorecard, "ranking" );
        Iterator code = codes.keySet().iterator();

        //age-reasoncode=AGE02, license-reasoncode=VL099 inherited
        assertEquals(2, codes.size());
        assertEquals("VL002", code.next());
        assertEquals("AGE02", code.next());


        session.getWorkingMemoryEntryPoint( "in_Age" ).insert( 0.0 );
        session.getWorkingMemoryEntryPoint( "in_Occupation" ).insert( "SKYDIVER" );
        session.fireAllRules();

        scorecard = session.getObjects( new ClassObjectFilter( scorecardType.getFactClass() ) ).iterator().next();
        assertEquals( 99.0, scorecardType.get( scorecard, "score" ) );
        codes = (Map) scorecardType.get( scorecard, "ranking" );
        code = codes.keySet().iterator();

        System.out.println( codes );
        assertEquals(3, codes.size());
        assertEquals("OCC01", code.next());
        assertEquals("VL002", code.next());
        assertEquals("AGE01", code.next());


        session.getWorkingMemoryEntryPoint( "in_Age" ).insert( 20.0 );
        session.getWorkingMemoryEntryPoint( "in_Occupation" ).insert( "TEACHER" );
        session.getWorkingMemoryEntryPoint( "in_ResidenceState" ).insert( "AP" );
        session.getWorkingMemoryEntryPoint( "in_ValidLicense" ).insert( true );
        session.fireAllRules();

        scorecard = session.getObjects( new ClassObjectFilter( scorecardType.getFactClass() ) ).iterator().next();
        assertEquals( 141.0, scorecardType.get( scorecard, "score" ) );
        codes = (Map) scorecardType.get( scorecard, "ranking" );
        code = codes.keySet().iterator();

        System.out.println( codes );
        assertEquals(4, codes.size());
        assertEquals("OCC02", code.next());
        assertEquals("RS001", code.next());
        assertEquals("VL001", code.next());
        assertEquals("AGE03", code.next());

    }

}
