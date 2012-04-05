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

package org.drools.chance.constraints.core.evaluators.linguistic;


import de.lab4inf.fuzzy.FuzzySet;
import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.chance.common.ImperfectField;
import org.drools.chance.constraints.core.connectives.IConnectiveCore;
import org.drools.chance.constraints.core.connectives.impl.godel.And;
import org.drools.chance.constraints.core.connectives.impl.godel.Or;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.fuzzy.linguistic.Linguistic;
import org.drools.chance.distribution.fuzzy.linguistic.ShapedFuzzyPartition;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;


public class IsEvaluatorDefinition implements EvaluatorDefinition {
    public static final Operator IS = Operator.addOperatorToRegistry(
            "is", false);
    private static final String[] SUPPORTED_IDS = { IS
            .getOperatorString() };

    private Evaluator[] evaluator;

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, Operator operator) {
        return this.getEvaluator(type, operator.getOperatorString(), operator
                .isNegated(), null);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, Operator operator,
            String parameterText) {
        return this.getEvaluator(type, operator.getOperatorString(), operator
                .isNegated(), parameterText);
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
        IsEvaluator evaluator = new IsEvaluator( type, isNegated );
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

    public static class IsEvaluator extends BaseEvaluator {

        private IConnectiveCore and = And.getInstance();
        private IConnectiveCore or  = Or.getInstance();

        public void setParameterText( String parameterText ) {

        }

        public IsEvaluator( final ValueType type, final boolean isNegated ) {
            super( type, IS );
        }

        /**
         * @inheridDoc
         */
        public boolean evaluate( InternalWorkingMemory workingMemory,
                InternalReadAccessor extractor, Object object, FieldValue value ) {
            final Object objectValue = extractor
                    .getValue(workingMemory, object);

            return compare( objectValue, value.getValue(), workingMemory );
        }

        public boolean evaluate( InternalWorkingMemory workingMemory,
                                 InternalReadAccessor leftExtractor, Object left,
                                 InternalReadAccessor rightExtractor, Object right ) {
            final Object value1 = leftExtractor.getValue(workingMemory, left);
            final Object value2 = rightExtractor.getValue(workingMemory, right);

            Object source = value1;
            Object target = value2;

            return compare( source, target, workingMemory );
        }


        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                VariableRestriction.VariableContextEntry context, Object right) {

            Object target = right;
            Object source = context.getObject();

            return compare( source, target, workingMemory );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                VariableRestriction.VariableContextEntry context, Object left) {

            Object target = left;
            Object source = context.getObject();

            return compare( source, target, workingMemory );
        }



        private boolean compare( Object source, Object target, InternalWorkingMemory workingMemory ) {
            System.out.println( "IS Compare " + source + " vs " + target );

            if ( ! ( target instanceof Linguistic ) ) {
                throw new UnsupportedOperationException( "IS : Right value must be a Linguistic granule, found " + target );
            }
            FuzzySet ref = ( (Linguistic) target ).getSet();
            
            if ( source instanceof ImperfectField ) {
                source = ((ImperfectField) source).getCurrent();
            }

            if ( source instanceof ShapedFuzzyPartition ) {

                ShapedFuzzyPartition sfp = (ShapedFuzzyPartition) source;
                Iterator<Linguistic> iter = sfp.iterator();
                Degree res = new SimpleDegree( 0 );
                while ( iter.hasNext() ) {
                    Linguistic granule = iter.next();

                    Degree x = new SimpleDegree( ref.intersection( granule.getSet() ).supremum() );
                    Degree y = sfp.getDegree( granule );

                    res = or.eval( res, and.eval( x, y ) );

                    System.out.println( " intersecting " + ref + " with " + granule.getLabel() + " >> " + and.eval( x, y ) );
                }
                

                System.out.println( "PARTITION-SET INTERSECTION " + res );
              
                return true;    
            
            } 
            
            
            /* We should always look for the distribution, otherwise we won't have the shaping degrees */
//            if ( source instanceof Linguistic ) {
//                
//                double sup = ref.intersection( ( (Linguistic) source ).getSet() ).supremum();
//                System.out.println( "SET-SET INTERSECTION " + sup );
//                return true;
//                
//            } 
            
            throw new UnsupportedOperationException( "IS : Left value must be a Fuzzy Set or Partition, found " + source );
            
        }

        @Override
        public String toString() {
            return "IsEvaluatorDefinition is";

        }

    }

}
