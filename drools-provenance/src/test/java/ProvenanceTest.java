import com.foo.MySubKlass;
import com.foo.MySubKlassImpl;
import org.drools.beliefs.provenance.IdentifiableEntity;
import org.drools.beliefs.provenance.Provenance;
import org.drools.beliefs.provenance.ProvenanceHelper;
import org.drools.core.ClockType;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.common.*;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.drools.core.metadata.Identifiable;
import org.drools.core.metadata.MetaCallableTask;
import org.drools.core.rule.EntryPointId;
import org.drools.core.time.SessionPseudoClock;
import org.drools.core.util.*;
import org.drools.semantics.Literal;
import org.jboss.drools.provenance.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionClock;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.utils.KieHelper;
import org.test.MyKlass;
import org.test.MyKlassImpl;
import org.test.MyKlass_;
import org.test.MyTargetKlass;
import org.test.MyTargetKlassImpl;
import org.test.MyTargetKlass_;
import org.w3.ns.prov.Activity;
import org.w3.ns.prov.Entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class ProvenanceTest {


    private String path;

    public ProvenanceTest( String path ) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static Collection paths() {
        return Arrays.asList( new String[][]
                                      {
                                              { "tms/" },
                                              { "bs/" }
                                      } );
    }


    @Test
    public void basicECETestFulfill() {
        List list = new ArrayList();
        KieSession kieSession = loadPseudoClockedProvenanceSession("testSimpleECE.ece", list);
        MyKlass mk = (MyKlass) new MyKlassImpl().withDyEntryId( "123-000" );
        mk.setFlag(Boolean.FALSE);
        InternalFactHandle handle = (InternalFactHandle) kieSession.insert(mk);
        kieSession.fireAllRules();
        sleep(kieSession, 10);

        MySubKlass msk = (MySubKlass) new MySubKlassImpl().withDyEntryId("456-000");
        kieSession.insert(msk);
        kieSession.fireAllRules();
        sleep(kieSession, 100);
        kieSession.fireAllRules();
        List<Activity> history = getProvenanceHistory( kieSession, handle.getObject() );
        assertEquals(1, history.size());

        Activity act = history.iterator().next();

        assertTrue(act instanceof Modification);
//        TruthMaintenanceSystem tms = ( (NamedEntryPoint) kieSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
//        assertEquals(2, tms.getEqualityKeyMap().size());
//        assert("fooVal".equals(mk.getProp()));
    }

    @Test
    public void basicECETestViolation() {
        List list = new ArrayList();
        KieSession kieSession = loadPseudoClockedProvenanceSession("testSimpleECE.ece", list);
        MyKlass mk = (MyKlass) new MyKlassImpl().withDyEntryId( "123-000" );
        kieSession.insert(mk);
        kieSession.fireAllRules();
        sleep(kieSession, 10);

        MyKlass tk = (MyKlass) new MyKlassImpl().withDyEntryId("234-000");
        tk.setProp("fooVal");
        kieSession.insert(tk);
        kieSession.fireAllRules();
        sleep(kieSession, 101);

        MySubKlass msk = (MySubKlass) new MySubKlassImpl().withDyEntryId("456-000");
        kieSession.insert(msk);
        kieSession.fireAllRules();
        sleep(kieSession, 100);
        kieSession.fireAllRules();
//        TruthMaintenanceSystem tms = ( (NamedEntryPoint) kieSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
//        assertEquals( 2, tms.getEqualityKeyMap().size() );
        assert("booVal".equals(mk.getProp()));
    }

    private void sleep(KieSession session, long time) {
        SessionPseudoClock clock = (SessionPseudoClock)session.getSessionClock();
        clock.advanceTime(time, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testProvenanceWithStatedObjects() {
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks_simpleSet.drl", list );

        MyKlass mk = (MyKlass) new MyKlassImpl().withDyEntryId( "123-000" );
        kieSession.insert( mk );
        kieSession.fireAllRules();

        TruthMaintenanceSystem tms = ( (NamedEntryPoint) kieSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
        assertEquals( 1, tms.getEqualityKeyMap().size() );
        EqualityKey ek = (EqualityKey) ((ObjectHashMap.ObjectEntry) tms.getEqualityKeyMap().iterator().next()).getValue();

        Object core = ek.getFactHandle().getObject();
        assertSame( mk, core );
    }

    @Test
    public void testMetaCallableWMTasksSetSimpleAttribute() {
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks_simpleSet.drl", list );
        InternalFactHandle handle = (InternalFactHandle) kieSession.insert( new MyKlassImpl().withDyEntryId( "123-000" ) );
        kieSession.fireAllRules();

        System.out.println( list );
        assertEquals( Arrays.asList( "fooVal" ), list );

        List<Activity> history = getProvenanceHistory( kieSession, handle.getObject() );
        assertEquals( 1, history.size() );

        Activity act = history.iterator().next();

        assertTrue( act instanceof Modification );
        assertEquals( 1, act.getGenerated().size() );
        assertTrue( act.getGenerated().iterator().next() instanceof Property );

        Property prop = (Property) act.getGenerated().iterator().next();
        assertEquals( "fooVal", prop.getValue().iterator().next().getLit() );
        assertEquals( ( (Identifiable) handle.getObject() ).getId().toString(),
                      prop.getHadPrimarySource().iterator().next().getIdentifier().iterator().next().toString() );
        assertEquals( "http://www.test.org#prop", prop.getIdentifier().iterator().next().toString() );

    }

    @Test
    public void testMetaCallableWMTasksSetSimpleAttributeMultipleVersionedTimes() {
        // set a simple attribute multiple times, keep history
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks_simpleHistory.drl", list );
        InternalFactHandle handle = (InternalFactHandle) kieSession.insert( new MyKlassImpl().withDyEntryId( "123-000" ) );
        kieSession.fireAllRules();

        System.out.println( list );
        // null is in the list since a high priority rule checks the fact even before the properties are set
        assertEquals( Arrays.asList( null, "fooVal", "barVal" ), list );

        List<Activity> history = getProvenanceHistory( kieSession, handle.getObject() );
        assertEquals( 1, history.size() );

        Activity act = history.get( 0 );
        Property prop = (Property) act.getGenerated().iterator().next();
        assertEquals( "barVal", prop.getValue().iterator().next().getLit() );
        assertEquals( ( (Identifiable) handle.getObject() ).getId().toString(),
                      prop.getHadPrimarySource().iterator().next().getIdentifier().iterator().next().toString() );
        assertEquals( "http://www.test.org#prop", prop.getIdentifier().iterator().next().toString() );

        assertEquals( 1, act.getWasInformedBy().size() );
        Activity prev = act.getWasInformedBy().get( 0 );
        Property prevProp = (Property) prev.getGenerated().iterator().next();
        assertEquals( "fooVal", prevProp.getValue().iterator().next().getLit() );

        assertTrue( act.getEndedAtTime().get( 0 ).getTime() >= prev.getEndedAtTime().get( 0 ).getTime() );
    }



    @Test
    public void testMetaCallableWMTasksSetCollectionAsAWhole() {
        // set a list attribute
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks_collSet.drl", list );
        kieSession.fireAllRules();

        assertEquals( Arrays.asList( "000", "001", "002" ), list );

        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof IdentifiableEntity ) {
                List<Activity> history = getProvenanceHistory( kieSession, o );
                assertEquals( 3, history.size() );
                Collections.sort( history, new Comparator<Activity>() {
                    @Override
                    public int compare( Activity activity, Activity activity2 ) {
                        if ( activity.getEndedAtTime().isEmpty() || activity2.getEndedAtTime().isEmpty() ) {
                            fail( "No date set on activity!" );
                        }
                        return activity.getEndedAtTime().get( 0 ).compareTo( activity2.getEndedAtTime().get( 0 ) );
                    }
                } );
                assertTrue( history.get( 2 ) instanceof Modification );
                Modification mod = (Modification) history.get( 2 );

                if ( ( (IdentifiableEntity) o ).getUri().toString().equals( "123" ) ) {
                    assertTrue( history.get( 2 ) instanceof Setting );
                    assertTrue( searchIdContent( mod, "links" ) );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );

                    assertEquals( "http://www.test.org#links", prop.getIdentifier().get( 0 ).toString() );
                    assertEquals( 3, prop.getValue().size() );
                    for ( Literal lit : prop.getValue() ) {
                        assertTrue( list.contains( lit.toString() ) );
                    }
                } else {
                    assertTrue( searchIdContent( mod, "linkedBy" ) );
                    assertTrue( history.get( 2 ) instanceof Addition );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );

                    assertEquals( "http://www.test.org#linkedBy", prop.getIdentifier().get( 0 ).toString() );
                    assertTrue( prop.getValue().get( 0 ).toString().equals( "123" ) );
                }
            }
        }

    }

    @Test
    public void testMetaCallableWMTasksAddItemToCollection() {
        // add an item to a list, versioning it
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks_collAdd.drl", list );
        kieSession.fireAllRules();

        assertEquals( Arrays.asList( "000", "001" ), list );
        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof MyKlass ) {
                MyKlass my = (MyKlass) o;
                assertEquals( 2, my.getLinks().size() );
                for ( int j = 0; j < 2; j++ ) {
                    assertEquals( j == 0 ? "000" : "001", my.getLinks().get( j ).getId().toString() );
                }
            }
        }


        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof IdentifiableEntity ) {
                List<Activity> history = getProvenanceHistory( kieSession, o );
                assertEquals( 3, history.size() );
                Collections.sort( history, new Comparator<Activity>() {
                    @Override
                    public int compare( Activity activity, Activity activity2 ) {
                        if ( activity.getEndedAtTime().isEmpty() || activity2.getEndedAtTime().isEmpty() ) {
                            fail( "No date set on activity!" );
                        }
                        return activity.getEndedAtTime().get( 0 ).compareTo( activity2.getEndedAtTime().get( 0 ) );
                    }
                } );
                assertTrue( history.get( 2 ) instanceof Addition );
                Modification mod = (Modification) history.get( 2 );



                if ( ( (IdentifiableEntity) o ).getUri().toString().equals( "123" ) ) {
                    assertTrue( searchIdContent( mod, "links" ) );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );

                    assertEquals( "http://www.test.org#links", prop.getIdentifier().get( 0 ).toString() );
                    assertEquals( "001", prop.getValue().get( 0 ).toString() );

                    assertEquals( 1, mod.getWasInformedBy().size() );
                    Activity prev = mod.getWasInformedBy().get( 0 );

                    assertTrue( prev instanceof Addition );
                    assertTrue( searchIdContent( prev, "links" ) );
                    assertEquals( 1, prev.getGenerated().size() );
                    Property prevProp = (Property) prev.getGenerated().get( 0 );
                    assertEquals( "http://www.test.org#links", prevProp.getIdentifier().get( 0 ).toString() );
                    assertEquals( "000", prevProp.getValue().get( 0 ).toString() );

                } else {
                    assertTrue( searchIdContent( mod, "linkedBy" ) );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );

                    assertEquals( "http://www.test.org#linkedBy", prop.getIdentifier().get( 0 ).toString() );
                    assertTrue( prop.getValue().get( 0 ).toString().equals( "123" ) );
                }
            }
        }

    }

    @Test
    public void testMetaCallableWMTasksAddItemsToCollection() {
        // add a collection to a list, versioning it
        // add an item to a list, versioning it
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks_collAddMany.drl", list );
        kieSession.fireAllRules();

        assertEquals( Arrays.asList( "000", "001", "002" ), list );
        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof MyKlass ) {
                MyKlass my = (MyKlass) o;
                assertEquals( 3, my.getLinks().size() );
                for ( int j = 0; j < 3; j++ ) {
                    assertEquals( "00" + j, my.getLinks().get( j ).getId().toString() );
                }
            }
        }

        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof IdentifiableEntity ) {
                List<Activity> history = getProvenanceHistory( kieSession, o );
                assertEquals( 3, history.size() );
                Collections.sort( history, new Comparator<Activity>() {
                    @Override
                    public int compare( Activity activity, Activity activity2 ) {
                        if ( activity.getEndedAtTime().isEmpty() || activity2.getEndedAtTime().isEmpty() ) {
                            fail( "No date set on activity!" );
                        }
                        return activity.getEndedAtTime().get( 0 ).compareTo( activity2.getEndedAtTime().get( 0 ) );
                    }
                } );
                assertTrue( history.get( 2 ) instanceof Addition );
                Modification mod = (Modification) history.get( 2 );



                if ( ( (IdentifiableEntity) o ).getUri().toString().equals( "123" ) ) {
                    assertTrue( searchIdContent( mod, "links" ) );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );
                    assertEquals( 2, prop.getValue().size() );

                    assertEquals( "http://www.test.org#links", prop.getIdentifier().get( 0 ).toString() );
                    assertEquals( "001", prop.getValue().get( 0 ).toString() );
                    assertEquals( "002", prop.getValue().get( 1 ).toString() );

                    assertEquals( 1, mod.getWasInformedBy().size() );
                    Activity prev = mod.getWasInformedBy().get( 0 );

                    assertTrue( prev instanceof Addition );
                    assertTrue( searchIdContent( prev, "links" ) );
                    assertEquals( 1, prev.getGenerated().size() );
                    Property prevProp = (Property) prev.getGenerated().get( 0 );
                    assertEquals( "http://www.test.org#links", prevProp.getIdentifier().get( 0 ).toString() );
                    assertEquals( "000", prevProp.getValue().get( 0 ).toString() );

                } else {
                    assertTrue( searchIdContent( mod, "linkedBy" ) );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );

                    assertEquals( "http://www.test.org#linkedBy", prop.getIdentifier().get( 0 ).toString() );
                    assertTrue( prop.getValue().get( 0 ).toString().equals( "123" ) );
                }
            }
        }
    }

    private boolean searchIdContent( Activity activity, String value ) {
        boolean found = false;
        for ( Literal lit : activity.getIdentifier() ) {
            if ( lit.toString().contains( value ) ) {
                found = true;
            }
        }
        return found;
    }

    @Test
    public void testMetaCallableWMTasksRemoveItemFromCollection() {
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks_collRemove.drl", list );
        kieSession.fireAllRules();

        assertEquals( Arrays.asList( "000" ), list );

        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof MyKlass ) {
                MyKlass my = (MyKlass) o;
                assertEquals( 1, my.getLinks().size() );
                assertEquals( "001", my.getLinks().get( 0 ).getId().toString() );
            }
        }

        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof IdentifiableEntity ) {
                List<Activity> history = getProvenanceHistory( kieSession, o );
                assertEquals( 3, history.size() );
                Collections.sort( history, new Comparator<Activity>() {
                    @Override
                    public int compare( Activity activity, Activity activity2 ) {
                        if ( activity.getEndedAtTime().isEmpty() || activity2.getEndedAtTime().isEmpty() ) {
                            fail( "No date set on activity!" );
                        }
                        return activity.getEndedAtTime().get( 0 ).compareTo( activity2.getEndedAtTime().get( 0 ) );
                    }
                } );
                Modification mod = (Modification) history.get( 2 );



                if ( ( (IdentifiableEntity) o ).getUri().toString().equals( "123" ) ) {
                    assertTrue( history.get( 2 ) instanceof Removal );

                    assertTrue( searchIdContent( mod, "links" ) );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );

                    assertEquals( "http://www.test.org#links", prop.getIdentifier().get( 0 ).toString() );
                    assertEquals( "000", prop.getValue().get( 0 ).toString() );

                    assertEquals( 1, mod.getWasInformedBy().size() );
                    Activity prev = mod.getWasInformedBy().get( 0 );

                    assertTrue( prev instanceof Setting );
                    assertTrue( searchIdContent( prev, "links" ) );
                    assertEquals( 1, prev.getGenerated().size() );
                    Property prevProp = (Property) prev.getGenerated().get( 0 );
                    assertEquals( "http://www.test.org#links", prevProp.getIdentifier().get( 0 ).toString() );
                    assertEquals( "000", prevProp.getValue().get( 0 ).toString() );
                    assertEquals( "001", prevProp.getValue().get( 1 ).toString() );

                } else {
                    if ( searchIdContent( mod, "000" ) ) {
                        assertTrue( history.get( 2 ) instanceof Removal );
                    } else {
                        assertTrue( history.get( 2 ) instanceof Addition );
                    }

                    assertTrue( searchIdContent( mod, "linkedBy" ) );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );

                    assertEquals( "http://www.test.org#linkedBy", prop.getIdentifier().get( 0 ).toString() );
                    assertTrue( prop.getValue().get( 0 ).toString().equals( "123" ) );
                }
            }
        }
    }

    @Test
    public void testMetaCallableWMTasksRemoveItemsFromCollection() {
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testTasks_collRemoveMany.drl", list );
        kieSession.fireAllRules();

        assertEquals( Arrays.asList( 0 ), list );

        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof MyKlass ) {
                MyKlass my = (MyKlass) o;
                assertEquals( 0, my.getLinks().size() );
            }
        }

        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof IdentifiableEntity ) {
                List<Activity> history = getProvenanceHistory( kieSession, o );
                assertEquals( 3, history.size() );
                Collections.sort( history, new Comparator<Activity>() {
                    @Override
                    public int compare( Activity activity, Activity activity2 ) {
                        if ( activity.getEndedAtTime().isEmpty() || activity2.getEndedAtTime().isEmpty() ) {
                            fail( "No date set on activity!" );
                        }
                        return activity.getEndedAtTime().get( 0 ).compareTo( activity2.getEndedAtTime().get( 0 ) );
                    }
                } );
                Modification mod = (Modification) history.get( 2 );



                if ( ( (IdentifiableEntity) o ).getUri().toString().equals( "123" ) ) {
                    assertTrue( history.get( 2 ) instanceof Removal );

                    assertTrue( searchIdContent( mod, "links" ) );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );

                    assertEquals( "http://www.test.org#links", prop.getIdentifier().get( 0 ).toString() );
                    assertEquals( "000", prop.getValue().get( 0 ).toString() );

                    assertEquals( 1, mod.getWasInformedBy().size() );
                    Activity prev = mod.getWasInformedBy().get( 0 );

                    assertTrue( prev instanceof Setting );
                    assertTrue( searchIdContent( prev, "links" ) );
                    assertEquals( 1, prev.getGenerated().size() );
                    Property prevProp = (Property) prev.getGenerated().get( 0 );
                    assertEquals( "http://www.test.org#links", prevProp.getIdentifier().get( 0 ).toString() );
                    assertEquals( "000", prevProp.getValue().get( 0 ).toString() );
                    assertEquals( "001", prevProp.getValue().get( 1 ).toString() );

                } else {
                    assertTrue( history.get( 2 ) instanceof Removal );

                    assertTrue( searchIdContent( mod, "linkedBy" ) );
                    assertEquals( 1, mod.getGenerated().size() );
                    Property prop = (Property) mod.getGenerated().get( 0 );

                    assertEquals( "http://www.test.org#linkedBy", prop.getIdentifier().get( 0 ).toString() );
                    assertTrue( prop.getValue().get( 0 ).toString().equals( "123" ) );
                }
            }
        }
    }




    @Test
    public void testPersistence() throws IOException {
        List list = new ArrayList();
        KieSession ksession = loadProvenanceSession( "testTasks.drl", list );
        ksession.fireAllRules();

        assertEquals( 4, ksession.getObjects().size() );
        TruthMaintenanceSystem tms = ( (NamedEntryPoint) ksession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
        assertEquals( 2, tms.getEqualityKeyMap().size() );

        org.drools.core.util.Iterator<ObjectHashMap.ObjectEntry> it = tms.getEqualityKeyMap().iterator();
        Object o;
        while ( ( o = it.next() ) != null ) {
            EqualityKey k = (EqualityKey) (( ObjectHashMap.ObjectEntry) o ).getKey();
            assertNotNull( k.getBeliefSet() );
            BeliefSet bs = k.getBeliefSet();
            assertNotNull( bs );
            assertTrue( bs.getFactHandle().getObject() instanceof Serializable );
        }


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
            if ( o instanceof MyKlass ) {
                List<MyTargetKlass> links = MyKlass_.links.get( (MyKlass) o );
                assertEquals( 1, links.size() );
                assertTrue( kieSession.getObjects().contains( links.get( 0 ) ) );
            }
            if ( o instanceof MyTargetKlass ) {
                List<MyKlass> linksInv = MyTargetKlass_.linkedBy.get( (MyTargetKlass) o );
                assertEquals( 1, linksInv.size() );
                assertTrue( kieSession.getObjects().contains( linksInv.get( 0 ) ) );
            }
        }
        assertEquals( Arrays.asList( "123", "000" ), list );

        TruthMaintenanceSystem tms = ( (NamedEntryPoint) kieSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
        assertEquals( 2, tms.getEqualityKeyMap().size() );

        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof Entity ) {
                Collection<? extends Activity> acts = ProvenanceHelper.getProvenance( kieSession ).describeProvenance( o );
                // creation, typing and setting of property "links"
                assertEquals( 3, acts.size() );
            }
        }
    }

    @Test
    public void testConceptualModelBindingWithTraitDRL() {

        ArrayList list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testProvenance.drl", list );

        Provenance provenance = ProvenanceHelper.getProvenance( kieSession );
        assertTrue( list.contains( "RESULT" ) );

        assertEquals( 5, list.size() );
        for ( Object o : list ) {
            assertFalse( o instanceof MetaCallableTask );
        }

        for ( Object o : kieSession.getObjects() ) {
            if ( provenance.hasProvenanceFor( o ) ) {
                Collection<? extends Activity> acts = provenance.describeProvenance( o );

                assertEquals( 5, acts.size() );

                for ( Activity act : acts ) {
                    assertEquals( 1, act.getGenerated().size() );

                    if ( act instanceof Recognition ) {
                        Typification rec = (Typification) act.getGenerated().get( 0 );
                        Entity tgt = rec.getHadPrimarySource().get( 0 );

                        if ( rec.getValue().get( 0 ).toString().contains( "MyKlass" ) ) {
                            assertEquals( "123", rec.getHadPrimarySource().get( 0 ).getIdentifier().get( 0 ).toString() );
                        } else if ( rec.getValue().get( 0 ).toString().contains( "MySubKlass" ) ) {
                            assertEquals( 1, act.getDisplaysAs().size() );
                            assertEquals( "Pretty print hello world from IdentifiableEntity_Proxy",
                                          act.getDisplaysAs().get( 0 ).getNarrativeText().get( 0 ) );

                        } else {
                            fail( "Unexpected type " + rec.getValue().get( 0 ).toString() );
                        }
                    } else if ( act instanceof Modification ) {
                        Property mod = (Property) act.getGenerated().get( 0 );
                        if ( mod.getIdentifier().get( 0 ).getLit().toString().contains( "subProp" ) ) {
                            assertEquals( "42", mod.getValue().get( 0 ).toString() );
                            assertEquals( 1, act.getWasInformedBy().size() );

                            Activity prev = act.getWasInformedBy().get( 0 );
                            assertTrue( prev instanceof Modification );
                            assertTrue( prev.getGenerated().get( 0 ) instanceof Property );

                            Property p2 = (Property) prev.getGenerated().get( 0 );
                            assertEquals( "12", p2.getValue().get( 0 ).toString() );
                            assertTrue( p2.getIdentifier().get( 0 ).toString().contains( "subProp" ) );
                        } else if ( mod.getIdentifier().get( 0 ).getLit().toString().contains( "prop" ) ) {
                            assertEquals( "hello", mod.getValue().get( 0 ).toString() );
                        } else {
                            fail( "Unexpected property " + mod.getValue().get( 0 ).toString() );
                        }
                    } else if ( act instanceof Assertion ) {
                        Instance nu = (Instance) act.getGenerated().get( 0 );
                        assertEquals( "123", nu.getIdentifier().get( 0 ).toString() );
                    }

                }
            }
        }


    }

    @Test
    public void testUnaffectedEntity() {

        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession( "testUnaffectedEntity.drl", list );

        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof MyKlass ) {
                String val = MyKlass_.prop.get( (MyKlass) o );
                assertEquals( "hello", val );
            }
        }

        Provenance provenance = ProvenanceHelper.getProvenance( kieSession );
        for ( Object o : kieSession.getObjects() ) {
            if ( provenance.hasProvenanceFor( o ) ) {
                if ( ( (IdentifiableEntity) o ).getUri().toString().equals( "123" ) ) {
                    List<Activity> history = getProvenanceHistory( kieSession, o );
                    assertEquals( 3, history.size() );
                    assertTrue( history.get( 0 ) instanceof Assertion );
                    assertTrue( history.get( 1 ) instanceof Recognition );
                    assertTrue( history.get( 2 ) instanceof Modification );
                } else if ( ( (IdentifiableEntity) o ).getUri().toString().equals( "000" ) ) {
                    List<Activity> history = getProvenanceHistory( kieSession, o );
                    assertEquals( 2, history.size() );
                    assertTrue( history.get( 0 ) instanceof Assertion );
                    assertTrue( history.get( 1 ) instanceof Recognition );
                }
            }
        }

    }



    @Test
    public void testProvenanceModifyWith() {
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession("testTasks_modifyWith.drl", list );

        MyKlass mk = new MyKlassImpl();
        MyTargetKlass mtk = new MyTargetKlassImpl();

        kieSession.insert( mk );
        kieSession.insert( mtk );
        kieSession.fireAllRules();

        TruthMaintenanceSystem tms = ( (NamedEntryPoint) kieSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
        assertEquals( 2, tms.getEqualityKeyMap().size() );

        assertEquals(1, list.size());
    }


    @Test
    public void testProvenanceMultipleDisplaysPerEvidence() {
        KieSession kieSession = loadProvenanceSession("testProvenanceMultipleDisplays.drl", null );

        MyKlass mk = new MyKlassImpl();

        kieSession.insert( mk );
        kieSession.insert( "Test Display" );
        kieSession.fireAllRules();

        TruthMaintenanceSystem tms = ( (NamedEntryPoint) kieSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
        assertEquals( 1, tms.getEqualityKeyMap().size() );

        Provenance provenance = ProvenanceHelper.getProvenance( kieSession );

        Collection<? extends Object> sessionObjects = kieSession.getObjects();

        for ( Object o : sessionObjects ) {
            if ( provenance.hasProvenanceFor( o ) ) {
                Collection<? extends Activity> acts = provenance.describeProvenance( o );

                assertEquals( 1, acts.size() );

                Activity act = acts.iterator().next();

                assertEquals( 1, act.getUsed().size() );

                Entity entity = act.getUsed().get( 0 );

                assertEquals( 2, entity.getDisplaysAs().size() );

                boolean found1 = false;
                boolean found2 = false;

                for( Narrative narrative : entity.getDisplaysAs() ) {
                    assertEquals( 1, narrative.getNarrativeType().size() );
                    assertEquals( 1, narrative.getNarrativeText().size() );

                    if( narrative.getNarrativeType().get( 0 ).equals( "1" ) ) {
                        assertEquals( "Value 1", narrative.getNarrativeText().get( 0 ) );
                        found1 = true;
                    }
                    if( narrative.getNarrativeType().get( 0 ).equals( "2" ) ) {
                        assertEquals( "Value 2", narrative.getNarrativeText().get( 0 ) );
                        found2 = true;
                    }
                }

                assertTrue( "Didn't find display1", found1 );
                assertTrue( "Didn't find display2", found2 );

                return;
            }
        }

        fail( "Displays not found." );
    }

    @Test
    public void testProvenanceCollectionEvidence() {
        KieSession kieSession = loadProvenanceSession("testProvenanceCollection.drl", null );

        MyKlass mk = new MyKlassImpl();
        mk.addProp( "foo" );

        kieSession.insert( mk );
        kieSession.insert( "Test Display" );
        kieSession.fireAllRules();

        TruthMaintenanceSystem tms = ( (NamedEntryPoint) kieSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
        assertEquals( 1, tms.getEqualityKeyMap().size() );

        for ( Object o : kieSession.getObjects() ) {
            if ( o instanceof IdentifiableEntity ) {
                Collection<? extends Activity> acts = ProvenanceHelper.getProvenance( kieSession ).describeProvenance( o );
                // creation, typing and setting of property "links"
                assertEquals( 2, acts.size() );

                Activity act = acts.iterator().next();
                assertEquals( 1, act.getGenerated().size() );
                assertEquals( 1, act.getUsed().size() );
                Entity e = act.getUsed().iterator().next();
                assertEquals( 2, e.getDisplaysAs().size() );
                Narrative n = e.getDisplaysAs().get( 1 );
                assertEquals( Arrays.asList( "foo" ), n.getNarrativeText() );
            }
        }
    }

    @Test
    public void testProvenanceNewWith() {
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession("testTasks_newWith.drl", list );

        MyTargetKlass mtk = new MyTargetKlassImpl();

        kieSession.insert( mtk );
        kieSession.fireAllRules();

        TruthMaintenanceSystem tms = ( (NamedEntryPoint) kieSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
        assertEquals( 2, tms.getEqualityKeyMap().size() );

        assertEquals( 1, list.size() );
    }

    @Test
    public void testInfer() {
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession("testTasks_infer.drl", list );

        MyTargetKlass mtk = new MyTargetKlassImpl();

        InternalFactHandle handle1 = (InternalFactHandle) kieSession.insert( mtk );
        kieSession.fireAllRules();
        assertEquals( 1, list.size() );
        InternalFactHandle handle2 = (InternalFactHandle) kieSession.getFactHandle( list.get( 0 ) );
        InternalFactHandle handle3 = (InternalFactHandle) kieSession.getFactHandle( ((TraitProxy) handle2.getObject() ).getObject() );

        TruthMaintenanceSystem tms = ( (NamedEntryPoint) kieSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();

        assertEquals( 3, kieSession.getObjects().size() );
        assertEquals( 1, tms.getEqualityKeyMap().size() );

        assertNotNull( handle2 );
        assertNull( handle1.getEqualityKey() );
        assertNull( handle2.getEqualityKey() );
        assertNotNull( handle3.getEqualityKey() );

        EqualityKey k3 = handle3.getEqualityKey();
        BeliefSet bs = k3.getBeliefSet();

        assertNotNull( bs );

    }

    @Test
    public void testCastTask() {
        List list = new ArrayList();
        KieSession kieSession = loadProvenanceSession("testTasks_cast.drl", list );

        assertEquals( Arrays.asList( "1" ), list );
    }


    private KieSession loadPseudoClockedProvenanceSession( String sourceDrl, List list ) {
        KieServices kieServices = KieServices.Factory.get();
        Resource axioms = kieServices.getResources().newClassPathResource("it/unibo/deis/lia/org/drools/expectations/expect_axioms.drl");
        Resource traitDRL = kieServices.getResources().newClassPathResource( "org/test/tiny_declare.drl" );
        Resource ruleDRL = kieServices.getResources().newClassPathResource(path + sourceDrl);

        KieHelper kieHelper = validateKieBuilder(traitDRL, ruleDRL, axioms);
        KieBase kieBase = kieHelper.build(ProvenanceHelper.getProvenanceEnabledKieBaseConfiguration(true));
        KieSessionConfiguration sessionConfiguration = kieServices.newKieSessionConfiguration();
        sessionConfiguration.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.toExternalForm()));
        KieSession kieSession = kieBase.newKieSession(sessionConfiguration,null);
        kieSession.setGlobal( "list", list );
        kieSession.fireAllRules();

        return kieSession;

    }

    private KieSession loadProvenanceSession( String sourceDrl, List list ) {
        KieServices kieServices = KieServices.Factory.get();
        Resource traitDRL = kieServices.getResources().newClassPathResource( "org/test/tiny_declare.drl" );
        Resource ruleDRL = kieServices.getResources().newClassPathResource(path + sourceDrl);

        KieHelper kieHelper = validateKieBuilder(traitDRL, ruleDRL);
        KieBase kieBase = kieHelper.build(ProvenanceHelper.getProvenanceEnabledKieBaseConfiguration(true));
        KieSession kieSession = kieBase.newKieSession();
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


    public List<Activity> getProvenanceHistory( KieSession kieSession, Object o ) {
        Provenance provenance = ProvenanceHelper.getProvenance( kieSession );
        return new ArrayList( provenance.describeProvenance( o ) );
    }
}
