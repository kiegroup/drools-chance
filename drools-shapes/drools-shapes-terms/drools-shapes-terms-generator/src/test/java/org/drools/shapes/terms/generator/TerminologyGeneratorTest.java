package org.drools.shapes.terms.generator;

import cts2.mayo.edu.terms_metamodel.terms.ConceptDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.drools.semantics.builder.reasoner.CodeSystem;
import org.drools.semantics.builder.reasoner.ConceptCode;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.core.io.ClassPathResource;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertNotNull;

public class TerminologyGeneratorTest {

    private static Map<String, CodeSystem> codeSystemMap;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void init() {
        codeSystemMap = doGenerate();
    }

    @Test
    public void testGenerateCodeSystems() {

        assertEquals(1, codeSystemMap.size());

        CodeSystem cs = codeSystemMap.values().iterator().next();

        assertEquals("SCH1", cs.getCodeSystemName());
        assertEquals("http://test/generator#concept_scheme1", cs.getCodeSystemUri());
    }

    @Test
    public void testGenerateConceptsWithReasoning() {
        assertEquals(1, codeSystemMap.size());

        Set<ConceptCode> concepts = codeSystemMap.values().iterator().next().getConcepts();

        assertEquals(2, concepts.size());
    }

    @Test
    public void testGenerateConceptsPopulated() {
        assertEquals(1, codeSystemMap.size());

        Set<ConceptCode> concepts = codeSystemMap.values().iterator().next().getConcepts();

        for( ConceptCode concept : concepts ) {
            assertNotNull( concept.getCode() );
            assertNotNull( concept.getCodeSystem() );
            assertNotNull( concept.getName() );
        }
    }



    @Test
    public void testClassCompilation() {
        try {

            File src = folder.newFolder( "src" );
            File target = folder.newFolder( "output" );

            new JavaGenerator().generate( codeSystemMap.values(), "org.drools.test", src );
            showDirContent( folder );

            List<Diagnostic<? extends JavaFileObject>> diagnostics = doCompile( src, target );

            boolean success = true;
            for ( Diagnostic diag : diagnostics ) {
                System.out.println( "ERROR : " + diag );
                if ( diag.getKind() == Diagnostic.Kind.ERROR ) {
                    success = false;
                }
            }
            assertTrue( success );

            ClassLoader urlKL = new URLClassLoader(
                    new URL[] { target.toURI().toURL() },
                    Thread.currentThread().getContextClassLoader()
            );

            Class scheme = Class.forName( "org.drools.test.SCH1", true, urlKL );

            Field ns = scheme.getField( "codeSystemId" );
            Assert.assertEquals( "0.0.0.0", ns.get( null ) );

            Field code = scheme.getField( "_6789" );
            Object cd = code.get( null );
            Assert.assertTrue( cd instanceof ConceptDescriptor );

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    private List<Diagnostic<? extends JavaFileObject>> doCompile( File source, File target ) throws IOException {
        List<File> list = new LinkedList<File>();

        explore( source, list );

        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = jc.getStandardFileManager( diagnostics, null, null );
        Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles( list );
        List<String> jcOpts = Arrays.asList( "-d", target.getPath() );
        JavaCompiler.CompilationTask task = jc.getTask( null, fileManager, diagnostics, jcOpts, null, compilationUnits );
        task.call();
        return diagnostics.getDiagnostics();
    }


    public static Map<String, CodeSystem> doGenerate() {
        try {
            OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
            OWLOntology o = owlOntologyManager.loadOntologyFromOntologyDocument( new ClassPathResource( "test.owl" ).getInputStream() );

            TerminologyGenerator generator = new TerminologyGenerator( o, true );

            return generator.traverse();
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return null;
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

    private void explore( File dir, List<File> files ) {
        for ( File f : dir.listFiles() ) {
            if ( f.getName().endsWith( ".java" ) ) {
                files.add( f );
            }
            if ( f.isDirectory() ) {
                explore( f, files );
            }
        }
    }

}
