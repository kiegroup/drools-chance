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

package org.drools.chance.rule.constraint.core.evaluators.linguistic;


import de.lab4inf.fuzzy.FuzzySet;
import org.drools.RuntimeDroolsException;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.chance.rule.builder.ChanceOperators;
import org.drools.chance.rule.constraint.core.evaluators.BaseImperfectEvaluator;
import org.drools.chance.degree.Degree;
import org.drools.chance.distribution.fuzzy.linguistic.Linguistic;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.Evaluator;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class IsEvaluatorDefinition implements EvaluatorDefinition {

    public static final Operator IS = Operator.addOperatorToRegistry(
            "is", false);
    public static final Operator IS_IMP = Operator.addOperatorToRegistry(
            ChanceOperators.makeImperfect( IS.getOperatorString() ), false);

    private static final String[] SUPPORTED_IDS = {
            IS.getOperatorString(),
            IS_IMP.getOperatorString()
    };

    private Evaluator[] evaluator;

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, Operator operator) {
        return this.getEvaluator( type, operator.getOperatorString(), operator
                .isNegated(), null );
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, Operator operator,
                                  String parameterText) {
        return this.getEvaluator( type, operator.getOperatorString(), operator
                .isNegated(), parameterText );
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, String operatorId,
                                  boolean isNegated, String parameterText) {
        return getEvaluator(type, operatorId, isNegated, parameterText,
                Target.FACT, Target.FACT);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, String operatorId,
                                  boolean isNegated, String parameterText, Target leftTarget,
                                  Target rightTarget) {
        IsEvaluator evaluator;
        evaluator = new IsEvaluator( type, isNegated, ChanceOperators.isImperfect(operatorId) );

        evaluator.setParameterText( parameterText );
        return evaluator;
    }

    /**
     * @inheridDoc
     */
    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }

    /**
     * @inheridDoc
     */
    public Target getTarget() {
        return Target.FACT;
    }

    /**
     * @inheridDoc
     */
    public boolean isNegatable() {
        return false;
    }

    /**
     * @inheridDoc
     */
    public boolean supportsType(ValueType type) {
        return ( type.equals( ValueType.OBJECT_TYPE ) );
    }

    /**
     * @inheridDoc
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        evaluator = (Evaluator[]) in.readObject();
    }

    /**
     * @inheridDoc
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(evaluator);
    }

    public static class IsEvaluator extends BaseImperfectEvaluator {


        public IsEvaluator( final ValueType type, final boolean isNegated, boolean enableImperfectMode ) {
            super( type, IS, enableImperfectMode );
        }

        @Override
        protected Degree matchValueToValue( Object leftValue, Object rightValue, InternalWorkingMemory workingMemory ) {

            if ( leftValue instanceof Linguistic ) {
                FuzzySet fs1 = ( (Linguistic) leftValue ).getSet();
                FuzzySet fs2 = ( (Linguistic) rightValue ).getSet();

                if ( ! leftValue.getClass().getName().equals( rightValue.getClass().getName() ) ) {
                    throw new RuntimeDroolsException( "Fuzzy Sets from different partitions are being compared " + leftValue.getClass() + " vs " + rightValue.getClass() );
                }

                FuzzySet x = fs1.intersection( fs2 );
                if ( fs1.xmin() >= fs2.xmax() || fs2.xmin() >= fs1.xmax()) {
                    return getBaseDegree().False();
                } else {
                    return getBaseDegree().fromConst( fs1.intersection( fs2 ).supremum() );
                }
            } else if ( leftValue instanceof Double ) {
                FuzzySet fs2 = ( (Linguistic) rightValue ).getSet();
                return getBaseDegree().fromConst( fs2.containment( (Double) leftValue ) );
            }

            return getBaseDegree().False();
        }


        @Override
        public String toString() {
            return "IsEvaluatorDefinition is";

        }

    }

}
