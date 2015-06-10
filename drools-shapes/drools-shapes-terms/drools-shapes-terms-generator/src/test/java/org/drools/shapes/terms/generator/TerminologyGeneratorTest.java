package org.drools.shapes.terms.generator;

import org.junit.BeforeClass;
import org.junit.Test;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class TerminologyGeneratorTest {

    private static Map<String, CodeSystem> codeSystemMap;

    @BeforeClass
    public static void init() {
        codeSystemMap = doGenerate();
    }

    @Test
        public void testGenerateCodeSystems() {

        assertEquals(1, codeSystemMap.size());

        CodeSystem cs = codeSystemMap.values().iterator().next();

        assertEquals("concept_scheme1", cs.getCodeSystemName());
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

}
