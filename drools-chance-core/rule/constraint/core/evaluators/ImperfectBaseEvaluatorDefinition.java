package org.drools.chance.rule.constraint.core.evaluators;

import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.chance.rule.builder.ChanceOperators;
import org.drools.spi.Evaluator;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ImperfectBaseEvaluatorDefinition implements EvaluatorDefinition {


    
    public String[] getEvaluatorIds() {
        return ChanceOperators.standard_imperfectOperators;
    }


    public Evaluator getEvaluator( ValueType type, String operatorId, boolean isNegated, String parameterText) {
        return new ImperfectMvelEvaluator( type, ChanceOperators.getOperator(operatorId), parse( parameterText ), true );
    }

    private List<String> parse( String parameterText ) {
        List<String> ret = new ArrayList<String>();
        if ( parameterText != null ) {
            StringTokenizer tok = new StringTokenizer( parameterText, ",;" );
            while ( tok.hasMoreTokens() ) {
                ret.add( tok.nextToken() );
            }
        }
        return ret;
    }


    public boolean isNegatable() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    public Evaluator getEvaluator( ValueType type, String operatorId, boolean isNegated, String parameterText, Target leftTarget, Target rightTarget) {
        return getEvaluator( type, operatorId, isNegated, parameterText );
    }

    public Evaluator getEvaluator( ValueType type, Operator operator, String parameterText ) {
        return getEvaluator( type, operator.getOperatorString(), false, parameterText );
    }

    public Evaluator getEvaluator( ValueType type, Operator operator) {
        return getEvaluator( type, operator.getOperatorString(), false, null );
    }

    
    
    public boolean supportsType( ValueType type ) {
        return true; 
    }

    public Target getTarget() {
        return Target.FACT;
    }

    
    
    public void writeExternal(ObjectOutput out) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
