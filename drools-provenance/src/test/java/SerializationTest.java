import org.drools.beliefs.provenance.ProvenanceHelper;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class SerializationTest {

    @Test
    public void testRunAllWithNoExceptionsStoreAndRetrieve() throws Exception {
        KieSession session = loadProvenanceSession( "serialization/testSerialization.drl" );

        assertEquals(1, session.fireAllRules());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        KieServices.Factory
                .get()
                .getMarshallers()
                .newMarshaller(session.getKieBase()).marshall(baos, session);

        KieSession session2 = KieServices.Factory
                .get()
                .getMarshallers()
                .newMarshaller(session.getKieBase()).unmarshall(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(0, session2.fireAllRules());
    }

    private KieSession loadProvenanceSession( String sourceDrl ) {
        KieServices kieServices = KieServices.Factory.get();
        Resource traitDRL = kieServices.getResources().newClassPathResource( "org/test/tiny_declare.drl" );
        Resource ruleDRL = kieServices.getResources().newClassPathResource( sourceDrl );

        KieHelper kieHelper = validateKieBuilder( traitDRL, ruleDRL );
        KieSession kieSession = kieHelper.build( ProvenanceHelper.getProvenanceEnabledKieBaseConfiguration() ).newKieSession();
        kieSession.fireAllRules();

        return kieSession;
    }


    private KieHelper validateKieBuilder( Resource... resources ) {
        KieHelper helper = new KieHelper();
        for ( Resource resource : resources ) {
            helper.addResource( resource );
        }

        Results res = helper.verify();
        System.err.println( res.getMessages() );
        assertFalse( helper.verify().hasMessages( Message.Level.ERROR ) );

        return helper;
    }

}
