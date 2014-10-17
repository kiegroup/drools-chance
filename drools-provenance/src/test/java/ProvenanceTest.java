import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.drools.beliefs.provenance.Provenance;
import org.drools.beliefs.provenance.ProvenanceBeliefSystem;
import org.drools.beliefs.provenance.ProvenanceHelper;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.drools.core.metadata.Modify;
import org.drools.core.rule.EntryPointId;
import org.jboss.drools.provenance.Assertion;
import org.jboss.drools.provenance.Recognition;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionClock;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.utils.KieHelper;
import org.w3.ns.prov.Activity;
import org.w3.ns.prov.Entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ProvenanceTest {


    @Test
    @Ignore
    public void testMetaCallableWMTasksSetSimpleAttribute() {
        // set a simple attribute
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks.drl", list );
        fail( "TODO" );
    }

    @Test
    @Ignore
    public void testMetaCallableWMTasksSetSimpleAttributeMultipleVersionedTimes() {
        // set a simple attribute multiple times, keep history
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks.drl", list );
        fail( "TODO" );
    }

    @Test
    @Ignore
    public void testMetaCallableWMTasksSetCollectionAsAWhole() {
        // set a list attribute
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks.drl", list );
        fail( "TODO" );
    }

    @Test
    @Ignore
    public void testMetaCallableWMTasksAddItemToCollection() {
        // add an item to a list, versioning it
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks.drl", list );
        fail( "TODO" );
    }

    @Test
    @Ignore
    public void testMetaCallableWMTasksAddItemsToCollection() {
        // add a collection to a list, versioning it
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks.drl", list );
        fail( "TODO" );
    }

    @Test
    @Ignore
    public void testMetaCallableWMTasksRemoveItemFromCollection() {
        // remove an item from a list, versioning it
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks.drl", list );
        fail( "TODO" );
    }

    @Test
    @Ignore
    public void testMetaCallableWMTasksRemoveItemsFromCollection() {
        // remove a collection from a list, versioning it
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks.drl", list );
        fail( "TODO" );
    }


    @Test
    public void testPersistence() throws IOException {
        List list = new ArrayList();
        KieSession ksession = loadProvenanceSession( "testTasks.drl", list );
        ksession.fireAllRules();

        ProtobufMarshaller marshaller = (ProtobufMarshaller) MarshallerFactory.newMarshaller( ksession.getKieBase(),
                                                                                              (ObjectMarshallingStrategy[]) ksession.getEnvironment().get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES ) );
        long time = ksession.<SessionClock>getSessionClock().getCurrentTime();

        // Serialize object
        final byte [] b1;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshall( bos,
                             ksession,
                             time );

        b1 = bos.toByteArray();
        bos.close();
    }







    @Test
    public void testMetaCallableWMTasks() {

        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks.drl", list );

        for ( Object o : kieSession.getObjects() ) {
            System.out.println( o );
        }
        System.out.println( list );
        assertEquals( Arrays.asList( "123", "000" ), list );
    }

    @Test
    public void testConceptualModelBindingWithTraitDRL() {

        ArrayList list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testProvenance.drl", list );

        Provenance provenance = ProvenanceHelper.getProvenance( kieSession );

        for ( Object o : kieSession.getObjects() ) {
            if ( provenance.hasProvenanceFor( o ) ) {
                Collection<? extends Activity> acts = provenance.describeProvenance( o );

                assertEquals( 4, acts.size() );

                for ( Activity act : acts ) {
                    if ( act instanceof Recognition ) {
                        assertEquals( 1, act.getGenerated().size() );
                        Entity rec = act.getGenerated().get( 0 );
                        Entity tgt = rec.getHadPrimarySource().get( 0 );

                        assertEquals( 1, tgt.getDisplaysAs().size() );
                        assertEquals( "Pretty print hello world from IdentifiableEntity_Proxy",
                                      tgt.getDisplaysAs().get( 0 ).getNarrativeText().get( 0 ) );
                    }
                }
            }
        }

        assertEquals( Arrays.asList( "RESULT" ), list );
    }


    private KieSession loadProvenanceSession( String sourceDrl, List list) {
        KieServices kieServices = KieServices.Factory.get();
        Resource traitDRL = kieServices.getResources().newClassPathResource( "tiny_declare.drl" );
        Resource ruleDRL = kieServices.getResources().newClassPathResource( sourceDrl );

        KieHelper kieHelper = validateKieBuilder( traitDRL, ruleDRL );
        KieSession kieSession = kieHelper.build( ProvenanceHelper.getProvenanceEnabledKieBaseConfiguration() ).newKieSession();
        kieSession.setGlobal( "list", list );
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
