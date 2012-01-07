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

        painting.setStartsOn( new Date() );
        painting.setEndsOn( new Date() );
        painting.setHasComment("Some comment on this object");

        Stair stair = factory.createStairImpl();
        stair.setStairLength( 10 );
        painting.setRequiresStair( stair );

        painting.getInvolves().add( factory.createPersonImpl() );

        painting.setInvolvesLabourers( Arrays.asList( ((Labourer) factory.createLabourerImpl() ) ) );

        
        Paint paint = factory.createPaintImpl();

        site = factory.createSiteImpl();
        site.setCenterX( 10 );
        site.setCenterY( 20 );
        site.setRadius( 100 );
        paint.setStoredIn(site);
        painting.setRequiresPaints( Arrays.asList( (Paint) paint ) );

        painting.getRequires().add( new EquipmentImpl() );
        
        
        pers = new LabourerImpl();
//        p.setParticipatesIn( Arrays.asList( (Activity) painting ) );
        painting.setInvolvesLabourers( Arrays.asList( (Labourer) pers ) );
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



    @Test
    public void testFact() {
        assertNotNull( painting );
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

        System.out.println( painting);
        System.out.println( p2 );

        assertEquals( painting, p2 );
        assertEquals( p2, painting );

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
        Object p2 = refreshOnJPA( painting, ((PaintingImpl) painting).getHjid(), em );

        assertNotNull( p2 );
        assertEquals( painting, p2 );
        assertNotSame( painting, p2 );

        em.close();

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

        PersistenceProvider aProvider = Empire.get().persistenceProvider();

        EntityManagerFactory emf = aProvider.createEntityManagerFactory( ObjectFactory.class.getPackage().getName()
                , getTestEMConfigMap() );
        EntityManager em = emf.createEntityManager();

        persist( painting, em );

        Painting p2 = (Painting) refreshOnJPA( painting, painting.getRdfId(), em );

        assertTrue( p2 instanceof Painting );
        assertTrue( p2 instanceof PaintingImpl );
        assertEquals( 10, ((Stair) ((Painting)p2).getRequiresStair()).getStairLength() );

        p2.setHasComment(" Change my mind ");
        ((Painting)p2).getRequiresStair().setStairLength(6);

        assertEquals(p2, painting);
        assertNotSame( painting, p2 );


        em.close();
    }


    @Test
    public void testRuleWithEmpire() {

        Empire.init(new OpenRdfEmpireModule());

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
