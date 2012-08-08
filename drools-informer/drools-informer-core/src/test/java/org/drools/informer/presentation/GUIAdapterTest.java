package org.drools.informer.presentation;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.informer.MultipleChoiceQuestion;
import org.drools.informer.Note;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.ConsequenceException;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.Variable;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class GUIAdapterTest {


    private KnowledgeBase knowledgeBase;

    private static final Logger logger = LoggerFactory.getLogger( GUIAdapterTest.class );

    @Before
    public void setUp() throws Exception {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory.newClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET);
        knowledgeBuilder.add(ResourceFactory.newClassPathResource( "org/drools/informer/dynamicMCQ_test.drl" ), ResourceType.DRL);
        if (knowledgeBuilder.hasErrors()) {
            System.out.println( knowledgeBuilder.getErrors().toString() );
            logger.debug( Arrays.toString( knowledgeBuilder.getErrors().toArray() ) );
        }
        assertFalse(knowledgeBuilder.hasErrors());
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
    }


    @Test
    public void testUpdateMCQ() {
        MultipleChoiceQuestion mcq;
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();

        knowledgeSession.fireAllRules();

        mcq = getMCQ( knowledgeSession, "kwestion", "id1" );
        assertEquals( 1, mcq.getNumOfPossibleAnswers() );
        assertEquals( "OldValue", mcq.getPossibleAnswers()[0].getValue() );


        knowledgeSession.insert( "change" );
        knowledgeSession.fireAllRules();

        mcq = getMCQ( knowledgeSession, "kwestion", "id1" );
        assertEquals( 2, mcq.getNumOfPossibleAnswers() );
        assertEquals( "NewValue1", mcq.getPossibleAnswers()[0].getValue() );
        assertEquals( "NewValue2", mcq.getPossibleAnswers()[1].getValue() );


        report(knowledgeSession, System.err);
        
        knowledgeSession.dispose();

    }
    
    private MultipleChoiceQuestion getMCQ( StatefulKnowledgeSession knowledgeSession, String qid, String sid ) {
        QueryResults qr = knowledgeSession.getQueryResults( "getItem", qid, sid, Variable.v );
        assertEquals( 1, qr.size() );
        Object x = qr.iterator().next().get( "$item" );
        assertTrue( x instanceof MultipleChoiceQuestion );

        return (MultipleChoiceQuestion) x;
    }

    private void report( StatefulKnowledgeSession knowledgeSession, PrintStream out ) {
        out.println( "---------------------------------------------------" );
        out.println( knowledgeSession.getObjects().size() );
        out.println( "---------------------------------------------------" );
        for ( Object o : knowledgeSession.getObjects() ) {
            out.println( "\t" + o );
        }
        out.println( "---------------------------------------------------" );
    }


}
