import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.EmpireOptions;
import com.clarkparsia.empire.config.ConfigKeys;
import com.clarkparsia.empire.sesametwo.OpenRdfEmpireModule;
import com.clarkparsia.empire.sesametwo.RepositoryDataSourceFactory;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.NameList;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3._2002._07.owl.ThingImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.drools.owl.conyard.*;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.reflect.Method;
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
    public static void init() throws ParseException, JAXBException, IOException {




        JAXBContext jaxbContext = JAXBContext.newInstance( factory.getClass().getPackage().getName() );

        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );


        unmarshaller = jaxbContext.createUnmarshaller();


    }





    @Before
    public void initTestData() {

        painting = new PaintingImpl();


        painting.setStartsOn(new Date());
        painting.setEndsOn(new Date());
        painting.setHasComment("Some comment on this object");

        Stair stair = new StairImpl();

        stair.setStairLength(10);

        painting.setRequiresStair( stair );

        painting.setOid("oidX");

        painting.addRequiresAlso( stair );

        painting.addInvolves( new PersonImpl() );

        painting.addInvolves( new LabourerImpl() );

        Paint paint = new PaintImpl();


        site = new SiteImpl();

        site.setCenterX(10.0f);
        site.setCenterY(20.0f);
        site.setRadius(100.0f);
        paint.setStoredInSite(site);
        painting.addRequires(paint);
        painting.addRequires( new EquipmentImpl() );


        pers = new LabourerImpl();


        pers.addParticipatesIn( painting );
        painting.addInvolves( pers );


    }




    public void checkPainting( Painting painting ) {
        assertNotNull( painting );

        assertEquals( 3, painting.getRequires().size() );

        assertEquals( 1, painting.getRequiresAlso().size() );
        assertTrue( painting.getRequiresAlso().get( 0 ) instanceof StairImpl );
        assertEquals( 10, (int) ((Stair) painting.getRequiresAlso().get( 0 )).getStairLength() );

        assertEquals( 3, painting.getInvolves().size() );

//        boolean found = false;
//        for ( Person p : painting.getInvolves() ) {
//            if ( p.getParticipatesIn().size() > 0 ) {
//                assertEquals( painting.getOid(), p.getParticipatesIn().get( 0 ).getOid() );
//                found = true;
//            }
//        }
//        assertTrue( found );

    }

    @Test
    public void testFact() {
        checkPainting( painting );

        assertEquals( 3, pers.getMakesUseOf().size() );
    }




    @Test
    public void testJaxb() throws JAXBException {

        Painting p2 = (Painting) refreshOnJaxb( painting );

        assertEquals( painting, p2 );
        assertEquals( p2, painting );
        checkPainting( p2 );

    }

    @Test
    public void testIdRef() throws JAXBException {
        StringWriter writer = new StringWriter();
        marshaller.marshal( painting, writer );
        System.err.println( writer.toString() );
    }

    @Test
    public void testSemanticAccessors() {
        Painting pain = new PaintingImpl();

        Date d = new Date();
        pain.setStartsOn(d);
        assertEquals( d, pain.getStartsOn() );

        Labourer lab = new LabourerImpl();
        lab.setOid("xyz");
        assertEquals("xyz", lab.getOid());
        lab.setOid("abc");
        assertEquals("abc", lab.getOid());

    }


    @Test
    public void testEqualityAndHashCode() {

        Painting px = new PaintingImpl();
        px.setOid( "oidX" );
        px.setHasComment("Some comment on this object");

        assertEquals( px, painting );
        assertFalse( px.hashCode() == painting.hashCode() );



        px.setHasComment("Change value");

        assertFalse( px.equals( painting ) );
        assertFalse( px.hashCode() == painting.hashCode() );


        painting.setDyEntryId( "aid" );
        ((PaintingImpl) painting).setDyReference( false );
        px.setDyEntryId( "aid" );
        ((PaintingImpl) px).setDyReference( false );

        assertEquals( painting, px );
        assertTrue( px.hashCode() == painting.hashCode() );


        px.setDyEntryId( "aid2" );

        assertFalse( px.equals( painting ) );
        assertFalse( px.hashCode() == painting.hashCode() );

    }





    private Object refreshOnJaxb( Object o ) throws JAXBException {
        StringWriter writer;

        writer = new StringWriter();
        marshaller.marshal( o, writer );
        System.err.println( writer.toString() );

        System.err.println( "------------------------------------------" );


        Object ret = unmarshaller.unmarshal( new StringReader( writer.toString() ) );

        writer = new StringWriter();
        marshaller.marshal( ret, writer );
        System.err.println( writer.toString() );

        return ret;
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
        assertEquals(  new Integer(10), p2.getRequiresStair().getStairLength() );

        assertEquals( painting, p2 );
        assertEquals( p2, painting );

        p2.setHasComment(" Change my mind ");
        p2.getRequiresStair().setStairLength(6);

        assertTrue( painting.equals( p2 ) );
        assertNotSame( painting, p2 );

        PaintingImpl p3 = new PaintingImpl();

        p3.mergeFrom( p3, p2 );

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




    @Test
    public void testJPA() throws ClassNotFoundException {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(
                "org.drools.owl.conyard:org.w3._2002._07.owl"
        );
        EntityManager em = emf.createEntityManager();



        persist(painting, em);

        Painting p2 = (Painting) refreshOnJPA( painting, (painting).getDyEntryId(), em );


        checkPainting( p2 );

        em.close();

    }




    @Test
    @Ignore
    public void validateXMLWithSchema() throws SAXException {
        StringWriter writer = new StringWriter();

        try {
            marshaller.marshal( painting, writer );
        } catch (JAXBException e) {
            fail( e.getMessage() );
        }

        String inXSD = "conyard_$impl.xsd";
        String xml = writer.toString();
        System.out.println( xml );

        SchemaFactory factory =
                SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");


        Source schemaFile = null;
        try {
            schemaFile = new StreamSource( new ClassPathResource( inXSD ).getInputStream() );
        } catch ( IOException e ) {
            fail( e.getMessage() );
        }
        Schema schema = factory.newSchema( schemaFile );

        Validator validator = schema.newValidator();

        Source source = new StreamSource( new ByteArrayInputStream( xml.getBytes() ) );

        try {
            validator.validate(source);
        }
        catch ( SAXException ex ) {
            fail( ex.getMessage() );
        } catch ( IOException ex ) {
            fail( ex.getMessage() );
        }

    }


    @Test
    public void testBasicProperties() {
        Empire.init(new OpenRdfEmpireModule());
        EmpireOptions.STRICT_MODE = false;

        PersistenceProvider aProvider = Empire.get().persistenceProvider();

        EntityManagerFactory emf = aProvider.createEntityManagerFactory( ObjectFactory.class.getPackage().getName()
                , getTestEMConfigMap() );
        EntityManager em = emf.createEntityManager();

        Stair st = new StairImpl();
        st.setStairLength( 10 );
        persist( st, em );

        Stair s2 = (Stair) refreshOnJPA( st, st.getRdfId(), em );

        assertEquals( new Integer(10), s2.getStairLength() );


        em.close();
    }

    @Test
    public void testCascadedBasicProperties() {
        Empire.init(new OpenRdfEmpireModule());
        EmpireOptions.STRICT_MODE = false;

        PersistenceProvider aProvider = Empire.get().persistenceProvider();

        EntityManagerFactory emf = aProvider.createEntityManagerFactory( ObjectFactory.class.getPackage().getName()
                , getTestEMConfigMap() );
        EntityManager em = emf.createEntityManager();

        Painting p = new PaintingImpl();
        Stair st = new StairImpl();
        st.setStairLength( 10 );
        p.addRequires( st );

        persist( p, em );

        Painting p2 = (Painting) refreshOnJPA( p, p.getRdfId(), em );

        assertEquals( new Integer(10), p.getRequiresStair().getStairLength() );


        em.close();
    }


    @Test
    public void testXMLNamespaces() {
        StringWriter writer;

        writer = new StringWriter();
        try {
            marshaller.marshal( painting, writer );
        } catch (JAXBException e) {
            fail( e.getMessage() );
        }

        try {
            Document dox = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream( writer.toString().getBytes() ) );
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression xPathExpression = xPath.compile("//namespace::*" );
            NodeList nodeList = (NodeList) xPathExpression.evaluate( dox, XPathConstants.NODESET );
            for ( int j = 0; j < nodeList.getLength(); j++ ) {
                Node n = nodeList.item( j );
                if ( n.getNodeName().equals( "xmlns" ) ) {
                    assertEquals( "http://owl.drools.org/conyard", n.getNodeValue() );
                } else if ( n.getNodeName().equals( "xmlns:xml" ) ) {
                    assertEquals( "http://www.w3.org/XML/1998/namespace", n.getNodeValue() );
                } else if ( n.getNodeName().equals( "xmlns:xsi" ) ) {
                    assertEquals( "http://www.w3.org/2001/XMLSchema-instance", n.getNodeValue() );
                } else {
                    fail( "Unexpected namespace " + n.getNodeName() + " :: " + n.getNodeValue() );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }


    @Test
    public void testJaxbFromStringEmptyContext() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<PaintingImpl xmlns=\"http://owl.drools.org/conyard\">\n" +
                "    <dyReference>false</dyReference>\n" +
                "    <dyEntryId>http://ed4355f5-35ab-43e5-8527-c0e49fd555e4</dyEntryId>\n" +
                "    <involves>\n" +
                "        <dyEntryType>Person</dyEntryType>\n" +
                "        <dyReference>false</dyReference>\n" +
                "        <dyEntryId>http://dc04373e-ac52-41ed-8ab2-165c3642bce8</dyEntryId>\n" +
                "    </involves>\n" +
                "    <involves xsi:type=\"Labourer\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <dyEntryType>Labourer</dyEntryType>\n" +
                "        <dyReference>false</dyReference>\n" +
                "        <dyEntryId>http://c33f8564-9fbf-4366-be28-f4f7a28b7eb5</dyEntryId>\n" +
                "    </involves>\n" +
                "    <involves xsi:type=\"Labourer\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <dyEntryType>Labourer</dyEntryType>\n" +
                "        <dyReference>false</dyReference>\n" +
                "        <dyEntryId>http://1df5204c-52cf-44e3-9c20-9c0c230ac776</dyEntryId>\n" +
                "        <participatesIn xsi:type=\"Painting\">\n" +
                "            <dyEntryType>Painting</dyEntryType>\n" +
                "            <dyReference>true</dyReference>\n" +
                "            <dyEntryId>http://ed4355f5-35ab-43e5-8527-c0e49fd555e4</dyEntryId>\n" +
                "        </participatesIn>\n" +
                "    </involves>\n" +
                "    <hasComment>Some comment on this object</hasComment>\n" +
                "    <startsOn>2012-11-26T18:48:04Z</startsOn>\n" +
                "    <requires xsi:type=\"Stair\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <dyEntryType>Stair</dyEntryType>\n" +
                "        <dyReference>false</dyReference>\n" +
                "        <dyEntryId>http://72eea3a5-f5a1-44b6-879d-df64559b9f92</dyEntryId>\n" +
                "        <stairLength>10</stairLength>\n" +
                "    </requires>\n" +
                "    <requires xsi:type=\"Paint\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <dyEntryType>Paint</dyEntryType>\n" +
                "        <dyReference>false</dyReference>\n" +
                "        <dyEntryId>http://92c40710-2fee-4185-8657-42b1c12b70e2</dyEntryId>\n" +
                "        <storedIn>\n" +
                "            <dyEntryType>Site</dyEntryType>\n" +
                "            <dyReference>false</dyReference>\n" +
                "            <dyEntryId>http://b923f025-57ff-47d5-890c-4be3171822c7</dyEntryId>\n" +
                "            <centerY>20.0</centerY>\n" +
                "            <radius>100.0</radius>\n" +
                "            <centerX>10.0</centerX>\n" +
                "        </storedIn>\n" +
                "    </requires>\n" +
                "    <requires>\n" +
                "        <dyEntryType>Equipment</dyEntryType>\n" +
                "        <dyReference>false</dyReference>\n" +
                "        <dyEntryId>http://5b8edc25-0ffe-403a-b91b-797a857c5f36</dyEntryId>\n" +
                "    </requires>\n" +
                "    <oid>oidX</oid>\n" +
                "    <endsOn>2012-11-26T18:48:04Z</endsOn>\n" +
                "    <requiresAlso xsi:type=\"Stair\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <dyEntryType>Stair</dyEntryType>\n" +
                "        <dyReference>true</dyReference>\n" +
                "        <dyEntryId>http://72eea3a5-f5a1-44b6-879d-df64559b9f92</dyEntryId>\n" +
                "    </requiresAlso>\n" +
                "</PaintingImpl>\n";

        try {
            Object paint2 = unmarshaller.unmarshal( new StringReader( xml ) );
            checkPainting( (Painting) paint2 );
        } catch ( JAXBException jxe ) {
            jxe.printStackTrace();
            fail(jxe.getMessage());
        }
    }

}