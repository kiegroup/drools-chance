package org.drools.shapes.terms.evaluator;

import com.clarkparsia.empire.annotation.RdfProperty;
import edu.mayo.terms_metamodel.terms.ConceptDescriptor;
import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.rule.VariableRestriction;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.shapes.terms.TermsInferenceServiceFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URI;

/**
 * Custom Drools 'denotes' operator.
 */
public class DenotesEvaluatorDefinition implements EvaluatorDefinition {

    private static final String DENOTES_OP = "denotes";

    private DenotesEval denotesEval = new DenotesEval(
    		Operator.addOperatorToRegistry(DENOTES_OP, false)
    );
    
    private DenotesEval notDenotesEval = new DenotesEval(
    		Operator.addOperatorToRegistry(DENOTES_OP, true)
    );

    public DenotesEvaluatorDefinition() {
        super();
    }

    
    @Override
    public String[] getEvaluatorIds() {
        return new String[]{ DENOTES_OP };
    }

    @Override
    public boolean isNegatable() {
        return true;
    }

    @Override
    public Evaluator getEvaluator(ValueType type, String operatorId, boolean isNegated, String parameterText, Target leftTarget, Target rightTarget) {
        return isNegated ? notDenotesEval : denotesEval;
    }

    @Override
    public Evaluator getEvaluator(ValueType type, String operatorId, boolean isNegated, String parameterText) {
    	return isNegated ? notDenotesEval : denotesEval;
    }

    @Override
    public Evaluator getEvaluator(ValueType type, Operator operator, String parameterText) {
    	return operator.isNegated() ? notDenotesEval : denotesEval;
    }

    @Override
    public Evaluator getEvaluator(ValueType type, Operator operator) {
    	return operator.isNegated() ? notDenotesEval : denotesEval;
    }

    @Override
    public boolean supportsType(ValueType type) {
        return true;
    }

    @Override
    public Target getTarget() {
        return Target.BOTH;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

    private class DenotesEval extends BaseEvaluator {

        private DenotesEvaluatorImpl eval;

        public DenotesEval(Operator operator) {
            super(null, operator);
            eval = new DenotesEvaluatorImpl( TermsInferenceServiceFactory.instance().getValueSetProcessor() );
		}

		@Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor extractor, InternalFactHandle factHandle, FieldValue value) {        	
            Object right = value.getValue();
        	Object left = extractor.getValue( factHandle.getObject() );

            if( left instanceof TraitProxy ) {
                left = ( (TraitProxy) left ).getObject();
            }
            //TODO : these casts are potentially unsafe, but the check should be done at compile time, not at runtime

            boolean answer = eval.denotes( (ConceptDescriptor) left, (ConceptDescriptor) right, getPropertyURI( extractor ) );
            return this.getOperator().isNegated() ? ! answer : answer;
        }

        private String getPropertyURI( InternalReadAccessor extractor ) {
            RdfProperty ann = extractor.getNativeReadMethod().getAnnotation( RdfProperty.class );
            return ann != null ? ann.value() : null;
        }

        @Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, InternalFactHandle left, InternalReadAccessor rightExtractor, InternalFactHandle right) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, InternalFactHandle right) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, InternalFactHandle left) {
            throw new UnsupportedOperationException();
        }
    };

}
