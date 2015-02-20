package org.drools.shapes.terms.evaluator;

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
import org.drools.drools_shapes.terms.Code;
import org.drools.shapes.terms.Terms;
import org.drools.shapes.terms.ValueSetProcessorFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Custom Drools 'inValueSet' operator.
 */
public class ValueSetEvaluator implements EvaluatorDefinition {

    private static final String IN_VALUE_SET_OP = "expresses";

    private Terms valueSetProcessor;

    private ValueSetEval valueSetEval = new ValueSetEval(
    		Operator.addOperatorToRegistry(IN_VALUE_SET_OP, false)
    );
    
    private ValueSetEval valueSetEvalNot = new ValueSetEval(
    		Operator.addOperatorToRegistry(IN_VALUE_SET_OP, true)
    );

    public ValueSetEvaluator() {
        super();
        this.valueSetProcessor = ValueSetProcessorFactory.instance().getValueSetProcessor();
    }

    
    @Override
    public String[] getEvaluatorIds() {
        return new String[]{ IN_VALUE_SET_OP };
    }

    @Override
    public boolean isNegatable() {
        return true;
    }

    @Override
    public Evaluator getEvaluator(ValueType type, String operatorId, boolean isNegated, String parameterText, Target leftTarget, Target rightTarget) {
        return isNegated ? valueSetEvalNot : valueSetEval;
    }

    @Override
    public Evaluator getEvaluator(ValueType type, String operatorId, boolean isNegated, String parameterText) {
    	return isNegated ? valueSetEvalNot : valueSetEval;
    }

    @Override
    public Evaluator getEvaluator(ValueType type, Operator operator, String parameterText) {
    	return operator.isNegated() ? valueSetEvalNot : valueSetEval;
    }

    @Override
    public Evaluator getEvaluator(ValueType type, Operator operator) {
    	return operator.isNegated() ? valueSetEvalNot : valueSetEval;
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

    private class ValueSetEval extends BaseEvaluator {

        public ValueSetEval(Operator operator) {
			super(null, operator);
		}

		@Override
        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor extractor, InternalFactHandle factHandle, FieldValue value) {        	
            Code valueSet = (Code) value.getValue();
            
        	Object target = extractor.getValue( factHandle.getObject() );

            if(target instanceof TraitProxy) {
                target = ((TraitProxy)target).getObject();
            }

            if( target instanceof Code ) {
                Code code;
                code = ((Code)target);
                boolean found = this.matches(code, valueSet);
                return this.getOperator().isNegated() ? !found : found;
            } else {
                return false;
            }

        }

        protected boolean matches(Code code, Code valueSetUri) {
            return valueSetProcessor.isEntityInSet( code, valueSetUri );
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
