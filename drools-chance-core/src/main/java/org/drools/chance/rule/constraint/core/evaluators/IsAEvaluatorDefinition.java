package org.drools.chance.rule.constraint.core.evaluators;

import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.chance.factmodel.ImperfectTraitProxy;
import org.drools.chance.rule.builder.ChanceOperators;
import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;
import org.drools.common.InternalWorkingMemory;
import org.drools.factmodel.traits.Thing;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.rule.VariableRestriction;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

public class IsAEvaluatorDefinition extends org.drools.base.evaluators.IsAEvaluatorDefinition {

    public static final Operator ISA_IMP = Operator.addOperatorToRegistry(
            ChanceOperators.makeImperfect( "isA" ), false);
    public static final Operator NOT_ISA_IMP = Operator.addOperatorToRegistry(
            ChanceOperators.makeImperfect( "isA" ), true);



    private static final String[] CHANCE_IDS = new String[] { ISA.getOperatorString(), ISA_IMP.getOperatorString() };


    /**
     * @inheridDoc
     */
    public String[] getEvaluatorIds() {
        return CHANCE_IDS;
    }


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
        
        public Degree match( InternalWorkingMemory workingMemory, Object object, String typeName ) {
            TraitableBean core = null;

            if ( object instanceof Thing ) {
                Thing thing = (Thing) object;
                core = (TraitableBean) thing.getCore();
                if ( core.hasTrait( typeName ) ) {
                    Thing proxy = core.getTrait( typeName.toString() );
                    return extractDegree( proxy );
                } else {
                    return ChanceDegreeTypeRegistry.getSingleInstance().getDefaultOne().False();
                }
            } else if ( object instanceof TraitableBean ) {
                core = (TraitableBean) object;
                if ( core.hasTrait( typeName ) ) {
                    Thing proxy = core.getTrait( typeName.toString() );
                    return extractDegree( proxy );
                } else {
                    return ChanceDegreeTypeRegistry.getSingleInstance().getDefaultOne().False();
                }
            } else {
                core = lookForWrapper( object, workingMemory );
                if ( core == null || ! core.hasTrait( typeName ) ) {
                    return ChanceDegreeTypeRegistry.getDefaultOne().False();
                } else {
                    Thing proxy = core.getTrait( typeName.toString() );
                    return extractDegree(proxy);
                }
            }
        }

        public Degree match( InternalWorkingMemory workingMemory, InternalReadAccessor extractor, Object object, FieldValue value ) {
            Object typeName = value.getValue();

            if ( typeName instanceof Class ) {
                typeName = ((Class) typeName).getName();
            }

            return match( workingMemory, object, (String) typeName );
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

        public Degree match( InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, Object left, InternalReadAccessor rightExtractor, Object right ) {
            throw new UnsupportedOperationException( "Not implemented yet" );
        }

        public Degree matchCachedLeft( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, Object right ) {
            throw new UnsupportedOperationException( "Not implemented yet" );
        }

        public Degree matchCachedRight( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, Object left ) {
            throw new UnsupportedOperationException( "Not implemented yet" );
        }
    }
}
