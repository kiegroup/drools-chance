package org.drools.scorecards;

import org.dmg.pmml_4_1.PMML;
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

public class DrlFromPMMLTest {

    private static String drl;

    @Before
    public void setUp() throws Exception {
        ScorecardCompiler scorecardCompiler = new ScorecardCompiler();
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_c.xls")) ) {
            PMML pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull(pmmlDocument);
            drl = scorecardCompiler.getDRL();
        } else {
            fail("failed to parse scoremodel Excel.");
        }
    }

    @Test
    public void testDrlNoNull() throws Exception {
        assertNotNull(drl);
        assertTrue(drl.length() > 0);
        System.out.println(drl);
    }

    @Test
    public void testPackage() throws Exception {
        assertTrue(drl.contains("package org.drools.scorecards.example"));
    }

    @Test
    public void testRuleCount() throws Exception {
        assertEquals(10, StringUtil.countMatches(drl, "rule \""));
    }

    @Test
    public void testImports() throws Exception {
        assertEquals(1, StringUtil.countMatches(drl, "import "));
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

        //ASSERT AND FIRE
        Applicant applicant = new Applicant();
        applicant.setAge(10);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = 5, age = 25
        assertTrue(30 == applicant.getTotalScore());

        session = kbase.newStatefulKnowledgeSession();
        //ASSERT AND FIRE
        applicant = new Applicant();
        applicant.setAge(0);
        applicant.setOccupation("SKYDIVER");
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10
        assertTrue(0 == applicant.getTotalScore());

        session = kbase.newStatefulKnowledgeSession();
        //ASSERT AND FIRE
        applicant = new Applicant();
        applicant.setAge(20);
        applicant.setOccupation("TEACHER");
        applicant.setResidenceState("AP");
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10
        assertTrue(40 == applicant.getTotalScore());
    }


}
