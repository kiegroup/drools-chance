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

package org.drools.chance.rule.constraint.core.evaluators;


import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.DiscreteDomainDistribution;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.rule.builder.ChanceOperators;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction;
import org.drools.spi.Evaluator;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;


public class HoldsEvaluatorDefinition implements EvaluatorDefinition {

    public static final Operator HOLDS = Operator.addOperatorToRegistry(
            "holds", false);
    public static final Operator HOLDS_IMP = Operator.addOperatorToRegistry(
            ChanceOperators.makeImperfect( HOLDS.getOperatorString() ), false);
    public static final Operator NEG_HOLDS = Operator.addOperatorToRegistry(
            "holds", true);
    public static final Operator NEG_HOLDS_IMP = Operator.addOperatorToRegistry(
            ChanceOperators.makeImperfect( HOLDS.getOperatorString() ), true);

    private static final String[] SUPPORTED_IDS = {
            HOLDS.getOperatorString(),
            HOLDS_IMP.getOperatorString(),
            NEG_HOLDS.getOperatorString(),
            NEG_HOLDS_IMP.getOperatorString()
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
        HoldsEvaluator evaluator;
        if ( parameterText != null ) {
            evaluator = new HoldsEvaluator( type, isNegated, splitParams(parameterText), ChanceOperators.isImperfect(operatorId) );
        } else {
            evaluator = new HoldsEvaluator( type, isNegated, ChanceOperators.isImperfect(operatorId) );
        }

       
        return evaluator;
    }

    private List<String> splitParams( String parameterText ) {
        StringTokenizer tok = new StringTokenizer( parameterText, "," );
        List<String> params = new LinkedList<String>();
            while ( tok.hasMoreTokens() ) {
                params.add( tok.nextToken() );
            }
        return params;
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

    public static class HoldsEvaluator extends BaseImperfectEvaluator {

        public HoldsEvaluator( final ValueType type, final boolean isNegated, boolean enableImperfectMode ) {
            super( type, HOLDS, isNegated, enableImperfectMode );
        }

        public HoldsEvaluator( ValueType type, boolean negated, List<String> parameters, boolean enableImperfection ) {
            super( type, HOLDS, negated, parameters, enableImperfection );   
        }


        protected Degree matchDistributionToValue( Distribution leftDist, Object rightValue, InternalWorkingMemory workingMemory ) {
            Degree deg = getBaseDegree().False();

            if ( leftDist.isDiscrete() && leftDist.domainSize().intValue() == 1 ) {
                DiscreteDomainDistribution discr = (DiscreteDomainDistribution) leftDist;
                deg = discr.get( rightValue );
                return matchValueToValue( deg,  rightValue, workingMemory );
            }
            return deg;
        }

        @Override
        protected Degree matchValueToValue( Object leftValue, Object rightValue, InternalWorkingMemory workingMemory ) {

            Degree deg;

            if ( leftValue instanceof Degree ) {
                deg = (Degree) leftValue;
            } else if ( leftValue instanceof Boolean ) {
                deg = baseDegree.fromBoolean(((Boolean) leftValue));
            } else {
                throw new IllegalStateException( "Holds is a unary evaluator applicable to Degrees only ");
            }
            
            if ( negated ) {
                deg = not.eval( deg );
            }
            return deg;
        }


        @Override
        public String toString() {
            return "HoldsEvaluatorDefinition holds";

        }

    }

}
