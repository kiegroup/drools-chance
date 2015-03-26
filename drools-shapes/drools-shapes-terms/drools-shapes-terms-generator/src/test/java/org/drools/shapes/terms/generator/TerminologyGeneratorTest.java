package org.drools.shapes.terms.generator;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class TerminologyGeneratorTest {

    @Test
        public void testGenerateCodeSystems() {
        Map<String, CodeSystem> codeSystemMap = this.doGenerate();
        assertEquals(1, codeSystemMap.size());

        CodeSystem cs = codeSystemMap.values().iterator().next();

        assertEquals("concept_scheme1", cs.getCodeSystemName());
        assertEquals("http://test/generator#concept_scheme1", cs.getCodeSystemUri());
    }

    @Test
    public void testGenerateConceptsWithReasoning() {
        Map<String, CodeSystem> codeSystemMap = this.doGenerate();
        assertEquals(1, codeSystemMap.size());

        Set<Concept> concepts = codeSystemMap.values().iterator().next().getConcepts();

        assertEquals(2, concepts.size());
    }

    @Test
    public void testGenerateConceptsPopulated() {
        Map<String, CodeSystem> codeSystemMap = this.doGenerate();
        assertEquals(1, codeSystemMap.size());

        Set<Concept> concepts = codeSystemMap.values().iterator().next().getConcepts();

        for(Concept concept : concepts) {
            assertNotNull( concept.getCode() );
            assertNotNull( concept.getCodeSystem() );
            assertNotNull( concept.getName() );
        }
    }

    public Map<String, CodeSystem> doGenerate() {
        TerminologyGenerator generator = new TerminologyGenerator();
        try {
            return generator.traverse( generator.load(
                    new ClassPathResource("test.owl").getInputStream() ) );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
