/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.chance.rule.constraint.core.evaluators;

import org.drools.chance.factmodel.ImperfectTraitProxy;
import org.drools.chance.rule.builder.ChanceOperators;
import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.rule.VariableRestriction;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;

public class IsAEvaluatorDefinition extends org.drools.core.base.evaluators.IsAEvaluatorDefinition {

    public static final Operator ISA_IMP = Operator.addOperatorToRegistry(
            ChanceOperators.makeImperfect( "isA" ), false);
    public static final Operator NOT_ISA_IMP = Operator.addOperatorToRegistry(
            ChanceOperators.makeImperfect( "isA" ), true);


    private static final String[] SUPPORTED_IDS = new String[] { ISA.getOperatorString(), ISA_IMP.getOperatorString() };

    @Override
    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, String operatorId,
                                  boolean isNegated, String parameterText, Target leftTarget,
                                  Target rightTarget) {

        if ( ChanceOperators.isImperfect(operatorId) ) {
            if ( isNegated ) { throw new UnsupportedOperationException( "Negated isA : Not implemented yet" ); }
            IsAEvaluator evaluator = new ImperfectIsAEvaluator( type, isNegated );
            evaluator.setParameterText( parameterText );
            return evaluator;
        } else {
            IsAEvaluator evaluator = new IsAEvaluator(type, isNegated);
            evaluator.setParameterText( parameterText );
            return evaluator;
        }
    }


    public static class ImperfectIsAEvaluator extends IsAEvaluator implements ImperfectEvaluator {

        public ImperfectIsAEvaluator( ValueType type, boolean negated ) {
            super( type, negated );
        }

        public Degree match( InternalWorkingMemory workingMemory,
                             InternalReadAccessor extractor,
                             InternalFactHandle handle,
                             FieldValue value ) {
            if ( !super.evaluate( workingMemory, extractor, handle, value ) ) {
                return ChanceDegreeTypeRegistry.getSingleInstance().getDefaultOne().False();
            }
            return extractDegree( (Thing) extractor.getValue( workingMemory, handle.getObject() ) );
        }

        private Degree extractDegree( Thing proxy ) {
            if ( proxy == null ) {
                return ChanceDegreeTypeRegistry.getSingleInstance().getDefaultOne().False();
            } else {
                if ( proxy instanceof ImperfectTraitProxy ) {
                    return ( (ImperfectTraitProxy) proxy ).isA();
                } else {
                    return ChanceDegreeTypeRegistry.getSingleInstance().getDefaultOne().True();
                }
            }
        }

        public Degree match( InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, InternalFactHandle left, InternalReadAccessor rightExtractor, InternalFactHandle right ) {
            throw new UnsupportedOperationException( "Not implemented yet" );
        }

        public Degree matchCachedLeft( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, InternalFactHandle right ) {
            throw new UnsupportedOperationException( "Not implemented yet" );
        }

        public Degree matchCachedRight( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, InternalFactHandle left ) {
            throw new UnsupportedOperationException( "Not implemented yet" );
        }
    }
}
