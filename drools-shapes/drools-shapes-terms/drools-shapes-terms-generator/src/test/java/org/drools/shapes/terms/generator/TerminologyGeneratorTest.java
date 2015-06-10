package org.drools.shapes.terms.generator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
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

        for(Concept concept : concepts) {
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
            return generator.traverse( o );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return null;
    }

}
