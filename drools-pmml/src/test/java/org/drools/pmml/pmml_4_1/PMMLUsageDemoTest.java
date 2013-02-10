package org.drools.pmml.pmml_4_1;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.Variable;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PMMLUsageDemoTest {

    private StatefulKnowledgeSession kSession;

    private static final String pmmlSource = "org/drools/pmml/pmml_4_1/mock_cold_simple.xml";


    @Before
    public void setupSession() {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( pmmlSource ), ResourceType.PMML );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setEventProcessingMode( EventProcessingOption.STREAM );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( conf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // Create kSession and initialize it
        kSession = kbase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

    }


    @After
    public void disposeSession() {
        if ( kSession != null ) {
            kSession.dispose();
        }
    }


    @Test
    public void invokePmmlWithRawData() {

        // One entry-point per input field
        //      field name "xyz" => entry point name "in_Xyz"
        kSession.getWorkingMemoryEntryPoint( "in_Temp" ).insert( 22.0 );
        kSession.fireAllRules();

        // Query results
        //      output field name   --> query name
        //      model name          --> first arg
        //      value               --> second arg ( Variable.v for output, any value for testing )
        QueryResults qrs = kSession.getQueryResults( "Cold", "MockCold", Variable.v );
        assertTrue( qrs.iterator().hasNext() );
        Object val = qrs.iterator().next().get( "$result" );

        assertEquals( 0.56, val );

        QueryResults qrs2 = kSession.getQueryResults( "Cold", "MockCold", 0.56 );
        assertTrue( qrs2.iterator().hasNext() );

        QueryResults qrs3 = kSession.getQueryResults( "Cold", "MockCold", 0.99 );
        assertFalse( qrs3.iterator().hasNext() );

    }



    @Test
    public void invokePmmlWithTrait() {

        String extraDrl = "package org.drools.pmml.pmml_4_1.test;" +
                "" +
                "import org.drools.factmodel.traits.Entity;" +
                "" +
                "rule \"Init\" " +
                "when " +
                "   $s : String( this == \"trigger\" ) " +
                "then " +
                "   System.out.println( \"Trig\" ); " +
                "   Entity o = new Entity(); " +
                "" +
                // don an object with the default input trait ( modelName + "Input" )
                // both soft and hard fields will be used to feed data into the model
                "" +
                "   MockColdInput input = don( o, MockColdInput.class ); " +
                "   modify( input ) { " +
                "       setTemp( 22.0 );" +
                "   } " +
                "end " +
                "" +
                "" +
                "rule Log when $x : MockColdInput() then System.out.println( \"IN \" + $x ); end " +
                "rule Log2 when $x : Cold() then System.out.println( \"OUT \" + $x ); end "
                ;


        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kSession.getKnowledgeBase() );

        kbuilder.add( ResourceFactory.newByteArrayResource( extraDrl.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kSession.getKnowledgeBase().addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kSession.insert( "trigger" );
        kSession.fireAllRules();

        QueryResults qrs = kSession.getQueryResults( "Cold", "MockCold", Variable.v );
        assertTrue( qrs.iterator().hasNext() );
        Object val = qrs.iterator().next().get( "$result" );

    }

}
