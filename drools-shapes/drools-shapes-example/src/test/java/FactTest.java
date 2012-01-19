import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.EmpireGenerated;
import com.clarkparsia.empire.EmpireOptions;
import com.clarkparsia.empire.config.ConfigKeys;
import com.clarkparsia.empire.sesametwo.OpenRdfEmpireModule;
import com.clarkparsia.empire.sesametwo.RepositoryDataSourceFactory;
import http.org.drools.conyard.owl.*;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class FactTest {





    private static ObjectFactory factory = new ObjectFactory();
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;

    private static Painting painting;
    private static Person pers;
    private static Site site;




    @BeforeClass
    public static void init() throws ParseException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(factory.getClass().getPackage().getName());

        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

        unmarshaller = jaxbContext.createUnmarshaller();


        initTestData();

    }

    private static void initTestData() {

        painting = new PaintingImpl();

        painting.setStartsOnDateAsActivity( new Date() );
        painting.setEndsOnDateAsActivity( new Date() );
        painting.setHasCommentStringAsActivity("Some comment on this object");

        Stair stair = factory.createStairImpl();
        stair.setStairLengthIntegerAsStair( 10 );
        painting.setRequiresStairAsPainting( stair );

        painting.getInvolves().add( factory.createPersonImpl() );

        painting.getInvolvesLabourersAsPainting().add( factory.createLabourerImpl() );

        Paint paint = factory.createPaintImpl();

        site = factory.createSiteImpl();
        site.setCenterXFloatAsSite( 10 );
        site.setCenterYFloatAsSite( 20 );
        site.setRadiusFloatAsSite( 100 );
        paint.setStoredInSiteAsEquipment(site);
        painting.getRequiresPaintsAsPainting().add( paint );
        painting.addRequires( new EquipmentImpl() );


        pers = new LabourerImpl();


        pers.getParticipatesIn().add( painting );
//        painting.getInvolvesLabourersAsPainting().add( (Labourer) pers  );
    }


    @Test
    public void testAbstractness() {

        try {
            EntityImpl.class.newInstance();
            fail("EntityImpl was expected to be abstract");
        } catch (InstantiationException e) {
            //ok here, it's abstract!
        } catch (IllegalAccessException e) {
            fail( e.getMessage() );
        }

    }


    public void checkPainting( Painting painting ) {
        assertNotNull( painting );
        assertEquals( 3, painting.requires().size() );
        assertEquals( 2, painting.involves().size() );


    }

    @Test
    public void testFact() {
        checkPainting( painting );

        assertEquals( 3, pers.getMakesUseOf().size() );
    }







    private Object refreshOnJaxb( Object o ) throws JAXBException {
        StringWriter writer = new StringWriter();
        marshaller.marshal( o, writer );
        System.err.println( writer.toString() );
        return unmarshaller.unmarshal( new StringReader( writer.toString() ) );
    }


    @Test
    public void testJaxb() throws JAXBException {

        Painting p2 = (Painting) refreshOnJaxb( painting );

        assertEquals( painting, p2 );
        assertEquals( p2, painting );
        checkPainting( p2 );

    }



    private void persist( Object o, EntityManager em ) {
        em.getTransaction().begin();
        em.persist( o );
        em.getTransaction().commit();

        em.clear();

    }

    private Object refreshOnJPA( Object o, Object key, EntityManager em ) {

        Object ans = null;

        em.getTransaction().begin();
        ans = em.find( o.getClass(), key );
        em.getTransaction().commit();


        return ans;
    }

    @Test
    public void testJPA() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( ObjectFactory.class.getPackage().getName() );
        EntityManager em = emf.createEntityManager();

        persist( painting, em );
        Painting p2 = (Painting) refreshOnJPA( painting, ((PaintingImpl) painting).getHjid(), em );

        assertNotNull( p2 );
        assertEquals( painting, p2 );
        assertNotSame( painting, p2 );

        checkPainting( p2 );

        em.close();

    }




    @Test
    public void testEmpireInheritance() {

        Empire.init(new OpenRdfEmpireModule());
        EmpireOptions.STRICT_MODE = false;

        PersistenceProvider aProvider = Empire.get().persistenceProvider();

        EntityManagerFactory emf = aProvider.createEntityManagerFactory( ObjectFactory.class.getPackage().getName()
                , getTestEMConfigMap() );
        EntityManager em = emf.createEntityManager();




        IronInstallation ironi = new IronInstallationImpl();
        Smith smith = new SmithImpl();
        Labourer labrr = new LabourerImpl();
        Guest guest = new GuestImpl();

        ironi.getInvolves().add( smith );
        ironi.getInvolves().add( labrr );
        ironi.getInvolves().add( guest );

        persist( ironi, em );

        System.out.println( "IronI has id " + ironi.getRdfId() );
        System.out.println( "Smith has id " + smith.getRdfId() );
        System.out.println( "Labrr has id " + labrr.getRdfId() );
        System.out.println( "Guest has id " + guest.getRdfId() );



        IronInstallation ironBuild = (IronInstallation) refreshOnJPA( ironi, ironi.getRdfId(), em );

        assertEquals( 3, ironBuild.getInvolves().size() );
        
        boolean x1 = false;
        boolean x2 = false;
        boolean x3 = false;
        boolean x4 = false;
        GuestImpl newGuest = null;

        for( Person p : ironBuild.getInvolves() ) {
            if ( p instanceof Smith ) x1 = true;
            if ( p instanceof Labourer ) x2 = true;
            if ( p instanceof Guest ) x3 = true;

            if ( p instanceof GuestImpl ) {
                x4 = true;
                newGuest = (GuestImpl) p;
            }
        }

        assertTrue( x1 && x2 && x3 && x4 );
        assertNotNull( newGuest );

        newGuest.withOid("abc");
        assertTrue( newGuest.getOid().contains( "abc" ) );
    }











    private static Map<String, String> getTestEMConfigMap() {
        Map<String, String> aMap = new HashMap<String, String>();

        aMap.put(ConfigKeys.FACTORY, RepositoryDataSourceFactory.class.getName());
        aMap.put(RepositoryDataSourceFactory.REPO, "test-repo");
        aMap.put(RepositoryDataSourceFactory.FILES, "");
        aMap.put(RepositoryDataSourceFactory.QUERY_LANG, RepositoryDataSourceFactory.LANG_SERQL);

        return aMap;
    }



    @Test
    public void testEmpire() {
        Empire.init(new OpenRdfEmpireModule());
        EmpireOptions.STRICT_MODE = false;

        PersistenceProvider aProvider = Empire.get().persistenceProvider();

        EntityManagerFactory emf = aProvider.createEntityManagerFactory( ObjectFactory.class.getPackage().getName()
                , getTestEMConfigMap() );
        EntityManager em = emf.createEntityManager();

        persist( painting, em );

        Painting p2 = (Painting) refreshOnJPA( painting, painting.getRdfId(), em );

        assertTrue( p2 instanceof Painting );
        assertTrue( p2 instanceof PaintingImpl );
        assertEquals( 10, ((Stair) ((Painting)p2).getRequiresStairAsPainting()).getStairLengthIntegerAsStair() );

        p2.setHasCommentStringAsActivity(" Change my mind ");
        p2.getRequiresStairAsPainting().setStairLengthIntegerAsStair(6);

        assertEquals(p2, painting);
        assertNotSame( painting, p2 );

        PaintingImpl p3 = new PaintingImpl();

        p3.mergeFrom( p3, p2 );

        assertEquals( p3, painting );

        checkPainting( p2 );
        checkPainting( p3 );

        em.close();
    }


    @Test
    public void testRuleWithEmpire() {

        Empire.init(new OpenRdfEmpireModule());
        EmpireOptions.STRICT_MODE = false;

        PersistenceProvider aProvider = Empire.get().persistenceProvider();

        EntityManagerFactory emf = aProvider.createEntityManagerFactory( ObjectFactory.class.getPackage().getName()
                , getTestEMConfigMap() );
        EntityManager em = emf.createEntityManager();

        persist( painting, em );

        Painting paintingFact = (Painting) refreshOnJPA( painting, painting.getRdfId(), em );




        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( new ClassPathResource( "testFacts.drl" ), ResourceType.DRL );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( builder.getKnowledgePackages() );

        ArrayList ans = new ArrayList();
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
        kSession.setGlobal( "ans", ans );

        kSession.insert(paintingFact);

        kSession.fireAllRules();

        assertEquals( 2, ans.size() );

        System.out.println( ans );

    }

}
