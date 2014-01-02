import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.semantics.XMLSerializationHelper;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieSession;
import org.semanticweb.ontologies._2012._1.rule_example.Pattern1Type;
import org.semanticweb.ontologies._2012._1.rule_example.Pattern1TypeImpl;
import org.semanticweb.ontologies._2012._1.rule_merged.IndividualFactory;
import org.semanticweb.ontologies._2012._1.rules.Rule;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class IndividualsTest {


    @Test
    public void testIndividuals() {

        Pattern1Type p1 = new Pattern1TypeImpl();
        Object x = p1.getHasFunctorType();
        Object y = ((Pattern1TypeImpl)p1).getHasFunctorTypeInferred();

        Collection c = IndividualFactory.getIndividuals();
        assertEquals( 21, c.size() );

        KieSession kSession = createSession();

        PackageDescr pack = new PackageDescr();
        kSession.setGlobal( "pack", pack );

        for ( Object o : c ) {
            kSession.insert( o );
        }
        kSession.fireAllRules();

        String drl = new DrlDumper().dump( pack );
        System.err.println( drl );
    }

    private KieSession createSession() {

        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        KieBuilder kieBuilder = kieServices.newKieBuilder( kieFileSystem );

        kieFileSystem.write( kieServices.getResources()
                                     .newClassPathResource( "translators/onto2drl.drl" )
                                     .setSourcePath( "test.drl" )
                                     .setResourceType( org.kie.api.io.ResourceType.DRL ) );
        kieBuilder.buildAll();
        if ( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) ) {
            Assert.fail( kieBuilder.getResults().getMessages( Message.Level.ERROR ).toString() );
        }

        KieBase kBase = kieServices.newKieContainer( kieBuilder.getKieModule().getReleaseId() ).getKieBase();

        return kBase.newKieSession();

    }


    @Test
    public void testNamespaces() {
        Rule r1 = (Rule) IndividualFactory.getNamedIndividuals().get( "rule1" );
        assertNotNull( r1 );

        Marshaller marshaller = XMLSerializationHelper.createMarshaller( IndividualFactory.class.getPackage().getName() );
        try {
            marshaller.marshal( r1, System.err );
        } catch ( JAXBException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testPOMOptions() {

        // The extra options in the pom file should add an explicit toString
        // with more content than the FQN + system hash
        // so let's check it!

        Rule r1 = (Rule) IndividualFactory.getNamedIndividuals().get( "rule1" );
        assertNotNull( r1 );

        String def = r1.getClass().getName() + "@" + Integer.toHexString( System.identityHashCode( r1 ) );
        String str = r1.toString();
        assertNotNull( str );

        System.out.println( str );
        System.out.println( def );

        assertTrue( str.startsWith( def ) );
        assertFalse( str.equals( def ) );

    }

}
