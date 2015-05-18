package org.drools.shapes.terms.evaluator;

import edu.mayo.terms_metamodel.terms.ConceptDescriptor;
import org.drools.shapes.model.datatypes.CD;
import org.drools.shapes.terms.cts2.Cts2TermsImpl;
import org.drools.shapes.terms.operations.internal.TermsInferenceService;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DenotesEvaluatorImplTest {

    @Test
    public void testInValueSet() {
        DenotesEvaluatorImpl evaluator = new DenotesEvaluatorImpl( this.getTermsInferenceService() );

        ConceptDescriptor left = new CD();
        left.setCode("BILINJ");

        ConceptDescriptor right = new CD();
        right.setValueSet("rimvalueset:EndocervicalRoute");

        assertTrue(evaluator.denotes(left, right, null));
    }

    @Test
    public void testNotInValueSet() {
        DenotesEvaluatorImpl evaluator = new DenotesEvaluatorImpl( this.getTermsInferenceService() );

        ConceptDescriptor left = new CD();
        left.setCode("xxxxx");

        ConceptDescriptor right = new CD();
        right.setValueSet("rimvalueset:EndocervicalRoute");

        assertFalse( evaluator.denotes( left, right, null ) );
    }

    @Test
    public void testEntitySubsumes() {
        DenotesEvaluatorImpl evaluator = new DenotesEvaluatorImpl( this.getTermsInferenceService() );

        ConceptDescriptor left = new CD();
        left.setCode("95653008");
        left.setCodeSystem("sctid");

        ConceptDescriptor right = new CD();
        right.setCode("2704003");
        right.setCodeSystem( "sctid" );

        assertTrue( evaluator.denotes( left, right, null ) );
    }

    @Test
    public void testEntitySelfSubsumes() {
        DenotesEvaluatorImpl evaluator = new DenotesEvaluatorImpl( this.getTermsInferenceService() );

        ConceptDescriptor left = new CD();
        left.setCode("95653008");
        left.setCodeSystem("sctid");

        ConceptDescriptor right = new CD();
        right.setCode("95653008");
        right.setCodeSystem("sctid");

        assertTrue(evaluator.denotes(left, right, null));
    }

    @Test
    public void testEntityNotSubsumes() {
        DenotesEvaluatorImpl evaluator = new DenotesEvaluatorImpl( this.getTermsInferenceService() );

        ConceptDescriptor left = new CD();
        left.setCode("95653008");
        left.setCodeSystem("sctid");

        ConceptDescriptor right = new CD();
        right.setCode("zzzzzzz");
        right.setCodeSystem("sctid");

        assertFalse(evaluator.denotes(left, right, null));
    }

    // TODO:
    // Want to return a stub or mock here eventually, but
    // need to check the actual integration first.
    private TermsInferenceService getTermsInferenceService() {
        return new Cts2TermsImpl();
    }

}
