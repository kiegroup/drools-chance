package org.drools.shapes.terms.generator;

import edu.mayo.terms_metamodel.terms.ConceptDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static junit.framework.Assert.format;
import static org.junit.Assert.*;

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

        Set<Concept> concepts = codeSystemMap.values().iterator().next().getConcepts();

        assertEquals(2, concepts.size());
    }

    @Test
    public void testGenerateConceptsPopulated() {
        assertEquals(1, codeSystemMap.size());

        Set<Concept> concepts = codeSystemMap.values().iterator().next().getConcepts();

        for( Concept concept : concepts ) {
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
            TerminologyGenerator generator = new TerminologyGenerator();
            OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
            OWLOntology o = owlOntologyManager.loadOntologyFromOntologyDocument( new ClassPathResource( "test.owl" ).getInputStream() );

            OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
            OWLReasoner owler = reasonerFactory.createReasoner( o );

            InferredOntologyGenerator reasoner = new InferredOntologyGenerator( owler );

            reasoner.fillOntology( owlOntologyManager, o );

            owlOntologyManager.saveOntology( o, System.err );
            return generator.traverse( o );
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
