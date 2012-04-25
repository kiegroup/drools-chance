package org.drools.chance.rule.constraint.core.evaluators;

import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.chance.rule.builder.ChanceOperators;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.DiscreteDomainDistribution;
import org.drools.chance.distribution.Distribution;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Declaration;
import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableStatement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ImperfectMvelEvaluator extends BaseImperfectEvaluator {

    protected Declaration[] declarations;
    private String expression;

    protected ImperfectMvelEvaluator( ValueType type, Operator operator ) {
        super( type, operator );
    }

    public ImperfectMvelEvaluator(ValueType type, Operator operator, List<String> parameters, boolean enableImperfection ) {
        super( type, operator, parameters, enableImperfection );
    }
    
    public ImperfectMvelEvaluator(ValueType type, Operator operator, List<String> parameters, boolean enableImperfection, String rightValue ) {
        super( type, operator, parameters, enableImperfection );
        expression = "this " + ChanceOperators.makePerfect(getOperator().getOperatorString()) + " " + rightValue;
    }



    protected Degree matchDistributionToValue( Distribution leftDist, Object rightValue, InternalWorkingMemory workingMemory ) {

        Degree deg = getBaseDegree().False();
        String expr = expression != null ? expression : "this " + ChanceOperators.makePerfect(getOperator().getOperatorString()) + " " + rightValue;

        if ( leftDist.isDiscrete() ) {
            if ( leftDist.domainSize().intValue() == 0 ) {
                return deg;
            }

            DiscreteDomainDistribution discr = (DiscreteDomainDistribution) leftDist;
            if ( getOperator().equals( Operator.EQUAL ) ||  getOperator().equals( ChanceOperators.EQUAL_IMP )) {
                return discr.get( rightValue );
            }

            Iterator iter = discr.iterator();
            while ( iter.hasNext() ) {
                Object left = iter.next();
                Degree m = discr.get( left );
                if ( m.toBoolean() && MVEL.evalToBoolean( expr, left ) ) {
                    deg = or.eval( deg, m );
                }
            }
        } else {
            throw new UnsupportedOperationException( "Unable to match a value with a continuous distribution!" );
        }
        return deg;
    }




    private Map<String, Object> holder = new HashMap<String,Object>(2);

    @Override
    protected Degree matchValueToValue( Object leftValue, Object object, InternalWorkingMemory workingMemory ) {
        //TODO
        holder.put( "x", leftValue );
        holder.put( "y", object );
        return SimpleDegree.fromBooleanLiteral( MVEL.evalToBoolean(
                "x " + ChanceOperators.makePerfect(this.getOperator().getOperatorString()) + " y",
                holder ) );

    }


    // will this help?
    protected Degree match( ExecutableStatement statement, Object object, Map<String, Object> vars ) {
        return vars == null ? (Degree) MVEL.executeExpression(statement, object) : (Degree) MVEL.executeExpression(statement, object, vars);
    }


}
