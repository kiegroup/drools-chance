package org.drools.scorecards;

import org.dmg.pmml.pmml_4_1.descr.PMML;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.drools.scorecards.ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES;

public class DrlFromPMMLTest {

    private static String drl;

    @BeforeClass
    public static void setUp() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        if (scorecardCompiler.compileFromExcel(DrlFromPMMLTest.class.getResourceAsStream("/scoremodel_c.xls")) ) {
            PMML pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull(pmmlDocument);
            drl = scorecardCompiler.getDRL();
            System.out.println( drl );
        } else {
            fail("failed to parse scoremodel Excel.");
        }
    }

    @Test
    public void testDrlNoNull() throws Exception {
        assertNotNull(drl);
        assertTrue(drl.length() > 0);
        //System.out.println(drl);
    }

    @Test
    public void testPackage() throws Exception {
        assertTrue(drl.contains("package org.drools.scorecards.example"));
    }

//    @Test
//    public void testRuleCount() throws Exception {
//        assertEquals(13, StringUtil.countMatches(drl, "rule \""));
//    }

//    @Test
//    public void testImports() throws Exception {
//        assertEquals(5, StringUtil.countMatches(drl, "import "));
//    }

    @Test
    public void testDRLExecution() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println( "DRL ERROR >> " + error.getMessage());
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
//        scorecardType.set(scorecard, "age", 10);
//        session.insert( scorecard );
        session.getWorkingMemoryEntryPoint( "in_Age" ).insert( 10.0 );
        session.getWorkingMemoryEntryPoint( "in_ValidLicense" ).insert( false );
        session.fireAllRules();


        //occupation = 5, age = 25, validLicence -1
        assertEquals( 29.0, scorecardType.get( scorecard, "score" ) );
        session.dispose();

        session = kbase.newStatefulKnowledgeSession();
        session.fireAllRules();
        scorecard = session.getObjects( new ClassObjectFilter( scorecardType.getFactClass() ) ).iterator().next();
//        scorecard = scorecardType.newInstance();
//        scorecardType.set(scorecard, "occupation", "SKYDIVER");
//        scorecardType.set(scorecard, "age", 0);
//        session.insert( scorecard );

        session.getWorkingMemoryEntryPoint( "in_Age" ).insert( 0.0 );
        session.getWorkingMemoryEntryPoint( "in_ValidLicense" ).insert( false );
        session.getWorkingMemoryEntryPoint( "in_Occupation" ).insert( "SKYDIVER" );

        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertEquals( -1.0, scorecardType.get( scorecard, "score" ) );


        session = kbase.newStatefulKnowledgeSession();
//        scorecard = scorecardType.newInstance();
        session.fireAllRules();
        scorecard = session.getObjects( new ClassObjectFilter( scorecardType.getFactClass() ) ).iterator().next();

        session.getWorkingMemoryEntryPoint( "in_Age" ).insert( 20.0 );
        session.getWorkingMemoryEntryPoint( "in_ResidenceState" ).insert( "AP" );
        session.getWorkingMemoryEntryPoint( "in_Occupation" ).insert( "TEACHER" );
        session.getWorkingMemoryEntryPoint( "in_ValidLicense" ).insert( true );

//        scorecardType.set(scorecard, "residenceState", "AP");
//        scorecardType.set(scorecard, "occupation", "TEACHER");
//        scorecardType.set(scorecard, "age", 20);
//        scorecardType.set(scorecard, "validLicense", true);
//        session.insert( scorecard );

        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals( 41.0, scorecardType.get(scorecard, "score") );
    }


}