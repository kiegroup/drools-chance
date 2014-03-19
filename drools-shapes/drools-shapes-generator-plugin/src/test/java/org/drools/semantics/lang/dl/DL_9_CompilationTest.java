/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.lang.dl;

import com.clarkparsia.empire.Empire;
import com.clarkparsia.empire.EmpireOptions;
import com.clarkparsia.empire.config.ConfigKeys;
import com.clarkparsia.empire.config.EmpireConfiguration;
import com.clarkparsia.empire.sesametwo.OpenRdfEmpireModule;
import com.clarkparsia.empire.sesametwo.RepositoryDataSourceFactory;
import com.clarkparsia.empire.util.BeanReflectUtil;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.commons.lang.StringUtils;
import org.drools.io.ResourceFactory;
import org.drools.semantics.UIdAble;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.DLFactoryConfiguration;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.shapes.OntoModelCompiler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import thewebsemantic.binding.Jenabean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;


/**
 * This is a sample class to launch a rule.
 */
@SuppressWarnings("restriction")
public class DL_9_CompilationTest {

    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();


    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    protected OntoModelCompiler compiler;


    @Test
    public void testDiamondOptimizedHierarchyCompilation() {

        OntoModel results = factory.buildModel( "diamond",
                                                ResourceFactory.newClassPathResource( "ontologies/diamondProp2.manchester.owl" ),
                                                DLFactoryConfiguration.newConfiguration( OntoModel.Mode.OPTIMIZED ) );

        assertTrue( results.isHierarchyConsistent() );

        compiler = new OntoModelCompiler( results, folder.getRoot() );

        // ****** Stream the java interfaces
        boolean javaOut = compiler.streamJavaInterfaces( true );

        assertTrue( javaOut );

        // ****** Stream the XSDs, the JaxB customizations abd the persistence configuration
        boolean xsdOut = compiler.streamXSDsWithBindings( true );

        assertTrue( xsdOut );
        File f = new File( compiler.getMetaInfDir() + File.separator + results.getDefaultPackage() + ".xsd" );
        try {
            Document dox = parseXML( f, true );

            NodeList types = dox.getElementsByTagName( "xsd:complexType" );
            assertEquals( 10, types.getLength() );

            NodeList elements = dox.getElementsByTagName( "xsd:element" );
            assertEquals( 12 + 14, elements.getLength() );
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

        showDirContent( folder );

        File b = new File( compiler.getMetaInfDir() + File.separator + results.getDefaultPackage() + ".xjb" );
        try {
            Document dox = parseXML( b, false );

            NodeList types = dox.getElementsByTagName( "bindings" );
            assertEquals( 28, types.getLength() );
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }


        showDirContent( folder );

        // ****** Generate sources
        boolean mojo = compiler.mojo(  Arrays.asList(
                "-extension",
                "-Xjaxbindex",
                "-Xannotate",
                "-Xinheritance",
                "-XtoString",
                "-Xcopyable",
                "-Xmergeable",
                "-Xvalue-constructor",
                "-Xfluent-api",
                "-Xkey-equality",
                "-Xsem-accessors",
                "-Xdefault-constructor",
                "-Xmetadata",
                "-Xinject-code"),
                OntoModelCompiler.MOJO_VARIANTS.JPA2 );

        assertTrue( mojo );

        File klass = new File( compiler.getXjcDir().getPath()
                + File.separator
                + results.getDefaultPackage().replace(".", File.separator)
                + File.separator
                + "BottomImpl.java" );
        printSourceFile( klass, System.out );

        showDirContent( folder );

        // ****** Do compile sources
        List<Diagnostic<? extends JavaFileObject>> diagnostics = compiler.doCompile();

        boolean success = true;
        for ( Diagnostic diag : diagnostics ) {
            System.out.println( "ERROR : " + diag );
            if ( diag.getKind() == Diagnostic.Kind.ERROR ) {
                success = false;
            }
        }
        assertTrue( success );



        showDirContent( folder );


        try {
            parseXML( new File( compiler.getBinDir() + "/META-INF/" + "persistence.xml" ), true );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            ClassLoader urlKL = new URLClassLoader(
                    new URL[] { compiler.getBinDir().toURI().toURL() },
                    Thread.currentThread().getContextClassLoader()
            );

            testPersistenceWithInstance( urlKL, "org.jboss.drools.semantics.diamond2.Bottom", results.getName() );

        } catch ( Exception e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail( e.getMessage() );
        }


    }


    @Test
    public void testIncrementalCompilation() {
        try {

            OntoModel diamond = factory.buildModel( "diamondX",
                                                    ResourceFactory.newClassPathResource( "ontologies/diamondProp.manchester.owl" ),
                                                    DLFactoryConfiguration.newConfiguration( OntoModel.Mode.OPTIMIZED ) );

            compiler = new OntoModelCompiler( diamond, folder.getRoot() );

            List<Diagnostic<? extends JavaFileObject>> diag1 = compiler.compileOnTheFly( OntoModelCompiler.minimalOptions, OntoModelCompiler.MOJO_VARIANTS.JPA2 );

            for ( Diagnostic<?> dx : diag1 ) {
                System.out.println( dx );
                assertFalse( dx.getKind() == Diagnostic.Kind.ERROR );
            }


            showDirContent( folder );

            ClassLoader urlKL = new URLClassLoader(
                    new URL[] { compiler.getBinDir().toURI().toURL() },
                    Thread.currentThread().getContextClassLoader()
            );


            OntoModel results = factory.buildModel( "diamondInc",
                                                    ResourceFactory.newClassPathResource( "ontologies/dependency.test.owl" ),
                                                    DLFactoryConfiguration.newConfiguration( OntoModel.Mode.OPTIMIZED ),
                                                    urlKL );

            System.out.println( results );

            Class bot = Class.forName( "org.jboss.drools.semantics.diamond.BottomImpl", true, urlKL );
            Class botIF = Class.forName( "org.jboss.drools.semantics.diamond.Bottom", true, urlKL );
            Assert.assertNotNull( bot );
            Assert.assertNotNull( botIF );
            Object botInst = bot.newInstance();
            Assert.assertNotNull( botInst );





            OntoModelCompiler compiler2 = new OntoModelCompiler( results, folder.getRoot() );

            compiler2.fixResolvedClasses();

            compiler2.streamJavaInterfaces( false );
            compiler2.streamXSDsWithBindings( true );

            compiler2.mojo( OntoModelCompiler.defaultOptions, OntoModelCompiler.MOJO_VARIANTS.JPA2 );

            showDirContent( folder );

            File unImplBoundLeft = new File( compiler2.getXjcDir() + File.separator +
                                        "org.jboss.drools.semantics.diamond".replace( ".", File.separator ) +
                                        File.separator + "Left.java" );
            assertFalse( unImplBoundLeft.exists() );
            File implBoundLeft = new File( compiler2.getXjcDir() + File.separator +
                                        "org.jboss.drools.semantics.diamond".replace( ".", File.separator ) +
                                        File.separator + "LeftImpl.java" );
            assertTrue( implBoundLeft.exists() );

            File leftInterface = new File( compiler2.getJavaDir() + File.separator +
                    "org.jboss.drools.semantics.diamond".replace( ".", File.separator ) +
                    File.separator + "Left.java" );

            assertTrue( leftInterface.exists() );

            List<Diagnostic<? extends JavaFileObject>> diagnostics = compiler2.doCompile();


            for ( Diagnostic<?> dx : diagnostics ) {
                System.out.println( dx );
                assertFalse( dx.getKind() == Diagnostic.Kind.ERROR );
            }

            showDirContent( folder );

            Document dox = parseXML( new File( compiler2.getBinDir().getPath() + "/META-INF/persistence.xml" ), false );
                XPath xpath = XPathFactory.newInstance().newXPath();
                XPathExpression expr = xpath.compile( "//persistence-unit/@name" );
            assertEquals( "diamondX", (String) expr.evaluate( dox, XPathConstants.STRING ) );


            File YInterface = new File( compiler2.getJavaDir() + File.separator +
                    "org.jboss.drools.semantics.diamond".replace( ".", File.separator ) +
                    File.separator + "X.java" );
            assertTrue( YInterface.exists() );


            Class colf = Class.forName( "some.dependency.test.ChildOfLeftImpl", true, urlKL );
            Assert.assertNotNull( colf );
            Object colfInst = colf.newInstance();

                    List<String> hierarchy = getHierarchy( colf );
            assertTrue( hierarchy.contains( "some.dependency.test.ChildOfLeftImpl" ) );
            assertTrue( hierarchy.contains( "some.dependency.test.org.jboss.drools.semantics.diamond.LeftImpl" ) );
            assertTrue( hierarchy.contains( "org.jboss.drools.semantics.diamond.LeftImpl" ) );
            assertTrue( hierarchy.contains( "org.jboss.drools.semantics.diamond.C0Impl" ) );
            assertTrue( hierarchy.contains( "org.jboss.drools.semantics.diamond.TopImpl" ) );
            assertTrue( hierarchy.contains( "org.w3._2002._07.owl.ThingImpl" ) );

            Set<String> itfHierarchy = getIFHierarchy( colf );

            System.err.println( itfHierarchy.containsAll( Arrays.asList(
                    "org.jboss.drools.semantics.diamond.C1",
                    "org.jboss.drools.semantics.diamond.C0",
                    "some.dependency.test.org.jboss.drools.semantics.diamond.Left",
                    "some.dependency.test.ChildOfLeft",
                    "org.jboss.drools.semantics.diamond.Left",
                    "org.jboss.drools.semantics.diamond.Top",
                    "com.clarkparsia.empire.EmpireGenerated",
                    "org.w3._2002._07.owl.Thing",
                    "java.io.Serializable",
                    "org.drools.semantics.Thing",
                    "com.clarkparsia.empire.SupportsRdfId" ) ) );

            Method getter1 = colf.getMethod( "getAnotherLeftProp" );
                assertNotNull( getter1 );
            Method getter2 = colf.getMethod( "getImportantProp");
                assertNotNull( getter2 );

            for ( Method m : colf.getMethods() ) {
                if ( m.getName().equals( "addImportantProp" ) ) {
                    m.getName();
                }
            }

            Method adder = colf.getMethod( "addImportantProp", botIF );
                assertNotNull( adder );
            adder.invoke( colfInst, botInst );
            List l = (List) getter2.invoke( colfInst );
            assertEquals( 1, l.size() );



            File off = new File( compiler2.getXjcDir() + File.separator +
                    "org.jboss.drools.semantics.diamond".replace( ".", File.separator ) +
                    File.separator + "Left_Off.java" );
            assertFalse( off.exists() );


            testPersistenceWithInstance( urlKL, "org.jboss.drools.semantics.diamond.Bottom", diamond.getName() );
            System.out.println(" Done" );

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

    private Set<String> getIFHierarchy( Class x ) {
        Set<String> l = new HashSet<String>();
        extractInterfaces( x, l );
        return l;
    }

    private void extractInterfaces( Class x, Set<String> l ) {
        for ( Class itf : x.getInterfaces() ) {
            l.add( itf.getName() );
            extractInterfaces( itf, l );
        }
    }

    private List<String> getHierarchy( Class x ) {
        List<String> l = new LinkedList<String>();
        Class k = x;
        while ( ! k.equals( Object.class ) ) {
            l.add( k.getName() );
            k = k.getSuperclass();
        }
        return l;
    }




    private void testPersistenceWithInstance( ClassLoader urlKL, String cName, String pUnit ) {
        Object bot = null;

        try {
            bot = createTestFact( urlKL, cName );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        if ( bot != null ) {
            checkJaxbRefresh( bot, urlKL );

            checkEmpireRefresh( bot, urlKL );

            checkSQLRefresh( bot, urlKL, pUnit );

            checkJenaBeansRefresh( bot, urlKL );
        }
    }

    private void checkJenaBeansRefresh( Object bot, ClassLoader urlk ) {
        ClassLoader oldKL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( urlk );

            OntModel m = ModelFactory.createOntologyModel();
            Jenabean.instance().bind(m);
            Jenabean.instance().writer().saveDeep(bot);

            m.write(System.err);

            Object x = Jenabean.instance().reader().load( bot.getClass(), ((UIdAble) bot).getDyEntryId() );

            try {
                checkEquality( bot, x );
            } catch ( Exception e ) {
                e.printStackTrace();
                Assert.fail( e.getMessage() );
            }

        } finally {
            Thread.currentThread().setContextClassLoader( oldKL );
        }

    }

    private void checkSQLRefresh( Object obj, ClassLoader urlk, String punit ) {
        ClassLoader oldKL = Thread.currentThread().getContextClassLoader();
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            Thread.currentThread().setContextClassLoader( urlk );

            HashMap props = new HashMap();
            props.put( "hibernate.hbm2ddl.auto", "create-drop" );
            emf = Persistence.createEntityManagerFactory(
                    punit, props
            );

            em = emf.createEntityManager();

            checkJPARefresh( obj,
                    ((UIdAble) obj).getDyEntryId(),
                    em );

        } finally {
            Thread.currentThread().setContextClassLoader( oldKL );
            if ( em != null && em.isOpen() ) {
                em.clear();
                em.close();
            }
            if ( emf != null && emf.isOpen() ) {
                emf.close();
            }
        }

    }


    private void checkEmpireRefresh( Object obj, ClassLoader urlk ) {
        File config = new File( compiler.getBinDir() + File.separator + compiler.METAINF + File.separator + "empire.configuration.file" );
        File annox = new File( compiler.getBinDir() + File.separator + compiler.METAINF + File.separator + "empire.annotation.index" );

        ClassLoader oldKL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( urlk );


            checkJPARefresh( obj,
                    ((UIdAble) obj).getRdfId(),
                    initEmpireEM(config, annox, obj.getClass().getPackage().getName()));
        } finally {
            Thread.currentThread().setContextClassLoader( oldKL );
        }

    }


    private void checkJPARefresh(  Object obj, Object key, EntityManager em ) {

        persist( obj, em );

        Object obj2 =  refreshOnJPA( obj, key, em );

        System.out.println( obj2 );

        try {
            checkEquality( obj, obj2 );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

    private void checkEquality(Object obj, Object obj2) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object c0 = obj2.getClass().getMethod( "getC0Prop" ).invoke( obj2 );
        assertTrue( c0 instanceof List && ((List) c0).size() == 2 );

        Object cX = obj2.getClass().getMethod( "getObjPropX" ).invoke( obj2 );
        assertNotNull( cX );
        System.out.println( cX );
        assertTrue( cX.getClass().getName().endsWith( "XImpl" ) );

        Object c2 = obj2.getClass().getMethod( "getC2Prop" ).invoke( obj2 );
        assertTrue( c2 instanceof List
                && ((List) c2).size() == 1
                && ((List) c2).get( 0 ) instanceof String
        );
    }

    private EntityManager initEmpireEM( File config, File annox, String pack ) {
        System.setProperty( "empire.configuration.file", config.getPath() );
        EmpireConfiguration ecfg = new EmpireConfiguration();
        ecfg.getGlobalConfig().put( ConfigKeys.ANNOTATION_INDEX, annox.getPath() );
        Empire.init( ecfg, new OpenRdfEmpireModule() );
        EmpireOptions.STRICT_MODE = false;

        PersistenceProvider aProvider = Empire.get().persistenceProvider();

        EntityManagerFactory emf = aProvider.createEntityManagerFactory( pack, getTestEMConfigMap() );
        EntityManager em = emf.createEntityManager();
        return em;
    }

    private static Map<String, String> getTestEMConfigMap() {
        Map<String, String> aMap = new HashMap<String, String>();

        aMap.put(ConfigKeys.FACTORY, RepositoryDataSourceFactory.class.getName());
        aMap.put(RepositoryDataSourceFactory.REPO, "test-repo");
        aMap.put(RepositoryDataSourceFactory.FILES, "");
        aMap.put(RepositoryDataSourceFactory.QUERY_LANG, RepositoryDataSourceFactory.LANG_SERQL);

        return aMap;
    }

    private void persist( Object o, EntityManager em ) {
        em.getTransaction().begin();
        em.persist( o );
        em.getTransaction().commit();
        em.clear();
    }

    private Object refreshOnJPA( Object o, Object key, EntityManager em ) {
        Object ret = null;
        em.getTransaction().begin();
        ret = em.find( o.getClass(), key );
        em.getTransaction().commit();
        return ret;
    }




    private Object createTestFact( ClassLoader urlKL, String cName ) throws Exception {
        Class bottom = Class.forName( cName, true, urlKL );
        Class bottomImpl = Class.forName( cName + "Impl", true, urlKL );

        Object bot = bottomImpl.newInstance();
        assertTrue( bottom.isAssignableFrom( bot.getClass() ) );

        assertNotNull( bottomImpl.getMethod( "getObjPropX" ) );
        assertNotNull( bottomImpl.getMethod( "getObjPropXs" ) );

        Object ret = bottomImpl.getMethod( "getObjPropXs" ).invoke( bot );
        assertNotNull( ret );
        assertTrue( ret instanceof List );
        assertEquals( 1, ( (List) ret ).size() );

        bottom.getMethod( "addC1Prop", String.class ).invoke( bot, "helloc1" );
        bottom.getMethod( "addC2Prop", String.class ).invoke( bot, "helloc2" );
        bottom.getMethod( "addC0Prop", String.class ).invoke( bot, "helloc0" );
        bottom.getMethod( "addC0Prop", String.class ).invoke( bot, "helloc0_2" );

        return bot;
    }

    private void checkJaxbRefresh( Object obj, ClassLoader urlKL ) {
        try {
            JAXBContext jaxbContext;
            jaxbContext = JAXBContext.newInstance( obj.getClass().getPackage().getName(), urlKL );
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            StringWriter sw = new StringWriter();
            marshaller.marshal( obj, sw );

            System.out.println( sw.toString() );

            jaxbContext = JAXBContext.newInstance( obj.getClass().getPackage().getName(), urlKL );
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Marshaller marshaller2 = jaxbContext.createMarshaller();
            marshaller2.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
            marshaller2.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            Object clone = unmarshaller.unmarshal( new StringReader( sw.toString() ) );

            StringWriter sw2 = new StringWriter();
            marshaller2.marshal( clone, sw2 );
            System.err.println( sw2.toString() );

            assertEquals( sw.toString(), sw2.toString() );
        } catch ( PropertyException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        } catch ( JAXBException e ) {
            fail( e.getMessage() );
        }
    }

    private void printSourceFile( File f, PrintStream out  ) {
        try {
            FileInputStream fis = new FileInputStream( f );
            byte[] buf = new byte[ fis.available() ];
            fis.read( buf );
            out.println(new String(buf));
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
            fail( ioe.getMessage() );
        }
    }

    private Document parseXML( File f, boolean print ) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        InputSource xSource = new InputSource( new FileInputStream( f ) );
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dox = null;

        DocumentBuilder builder = dbf.newDocumentBuilder();
        dox = builder.parse( xSource );
        if ( print ) {
            streamXML( dox, System.out );
        }
        return dox;
    }

    private void streamXML(Document dox, PrintStream out) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform( new DOMSource( dox ), new StreamResult( out ) );
    }


    private void showDirContent(TemporaryFolder folder) {
        showDirContent( folder.getRoot(), 0 );
    }

    private void showDirContent( File file, int i ) {
        System.out.println( tab(i) + " " + file.getName() );
        if ( file.isDirectory() ) {
            for ( File sub : file.listFiles() ) {
                showDirContent( sub, i + 1 );
            }
        }
    }

    private String tab( int n ) {
        return StringUtils.repeat( "\t", n );
    }


}