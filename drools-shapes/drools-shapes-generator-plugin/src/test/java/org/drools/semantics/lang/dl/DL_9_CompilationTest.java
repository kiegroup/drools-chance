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
import org.apache.commons.lang.StringUtils;
import org.drools.io.ResourceFactory;
import org.drools.semantics.UIdAble;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.shapes.OntoModelCompiler;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;


/**
 * This is a sample class to launch a rule.
 */
@SuppressWarnings("restriction")
public class DL_9_CompilationTest {

    protected DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();


    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    protected OntoModelCompiler compiler;

    @Before
    public void before() {

    }

    @After
    public void after() {
        folder.delete();
    }

    @Test
    public void testDiamondOptimizedHierarchy() {

        OntoModel results = factory.buildModel( "diamond",
                                                ResourceFactory.newClassPathResource( "ontologies/diamondProp.manchester.owl" ),
                                                OntoModel.Mode.OPTIMIZED );

        assertTrue( results.isHierarchyConsistent() );

        compiler = new OntoModelCompiler( results, folder.getRoot() );

        // ****** Stream the java interfaces
        boolean javaOut = compiler.streamJavaInterfaces( true );

        assertTrue( javaOut );

        // ****** Stream the XSDs
        boolean xsdOut = compiler.streamXSDs();

        assertTrue(xsdOut);
        File f = new File( compiler.getMetaInfDir() + File.separator + results.getName() + ".xsd" );
        try {
            Document dox = parseXML( f, true );

            NodeList types = dox.getElementsByTagName( "xsd:complexType" );
            assertEquals( 10, types.getLength() );

            NodeList elements = dox.getElementsByTagName( "xsd:element" );
            assertEquals( 12 + 14, elements.getLength() );
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

        // ****** Stream the JaxB customizations abd the persistence configuration
        boolean xjbOut = compiler.streamBindings( true );

        assertTrue( xjbOut );
        File b = new File( compiler.getMetaInfDir() + File.separator + "bindings.xjb" );
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

//        File klass = new File( compiler.getXjcDir().getPath()
//                + File.separator
//                + results.getDefaultPackage().replace(".", File.separator)
//                + File.separator
//                + "BottomImpl.java" );
//        printSourceFile( klass, System.out );

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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            ClassLoader urlKL = new URLClassLoader(
                    new URL[] { compiler.getBinDir().toURI().toURL() },
                    Thread.currentThread().getContextClassLoader()
            );
            Object bot = createTestFact( results, urlKL );

            checkJaxbRefresh( bot, urlKL );

            checkEmpireRefresh( bot, urlKL );

            checkSQLRefresh(bot, urlKL);

        } catch ( Exception e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail( e.getMessage() );
        }


    }

    private void checkSQLRefresh( Object obj, ClassLoader urlk ) {
        ClassLoader oldKL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( urlk );

            EntityManagerFactory emf = Persistence.createEntityManagerFactory(
                    "org.jboss.drools.semantics.coal:org.jboss.drools.semantics.diamond:org.w3._2002._07.owl"
            );
            EntityManager em = emf.createEntityManager();

            checkJPARefresh( obj,
                             ((UIdAble) obj).getDyEntryId(),
                             em );

        } finally {
            Thread.currentThread().setContextClassLoader( oldKL );
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


    private void checkJPARefresh(Object obj, Object key, EntityManager em) {

        persist( obj, em );

        Object obj2 =  refreshOnJPA( obj, key, em );

        System.out.println( obj2 );

        try {
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

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        em.close();


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




    private Object createTestFact( OntoModel results, ClassLoader urlKL ) throws Exception {
        Class bottom = Class.forName( results.getDefaultPackage() + ".Bottom", true, urlKL );
        Class bottomImpl = Class.forName( results.getDefaultPackage() + ".BottomImpl", true, urlKL );
//        Class oFact = Class.forName( results.getDefaultPackage() + ".ObjectFactory", true, urlKL );

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

    private void checkJaxbRefresh( Object obj, ClassLoader urlKL ) throws Exception {
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