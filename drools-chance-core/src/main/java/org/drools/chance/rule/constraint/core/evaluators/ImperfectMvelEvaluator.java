package org.drools.chance.rule.constraint.core.evaluators;

import org.drools.chance.distribution.ContinuousDomainDistribution;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.DiscreteDomainDistribution;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.rule.builder.ChanceOperators;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
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
        String expr = expression != null ? expression : makeComparisonExpression( getOperator(), rightValue );

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
            Degree x;
            if ( getOperator().equals( Operator.EQUAL ) ||  getOperator().equals( ChanceOperators.EQUAL_IMP )) {
                return negated ? deg.True() : deg.False();
            }
            if ( getOperator().equals( Operator.LESS_OR_EQUAL ) || getOperator().equals( Operator.LESS ) ) {
                x = (( ContinuousDomainDistribution ) leftDist ).getCumulative( rightValue );
            } else {
                x = baseDegree.True().sub( (( ContinuousDomainDistribution ) leftDist ).getCumulative( rightValue ) );
            }
            return negated ? baseDegree.True().sub( x ) : x;
        }
        return deg;
    }

    private String makeComparisonExpression( Operator operator, Object rightValue ) {
        StringBuilder sb = new StringBuilder()
                .append( "this " )
                .append(  ChanceOperators.makePerfect( operator.getOperatorString() ) );
        if ( rightValue instanceof String ) {
            sb.append( "\"" ).append( rightValue.toString() ).append( "\"" );
        } else {
            sb.append( rightValue );
        }
        return sb.toString();
    }


    private Map<String, Object> holder = new HashMap<String,Object>(2);

    @Override
    protected Degree matchValueToValue( Object leftValue, Object object, InternalWorkingMemory workingMemory ) {
        //TODO
        holder.put( "x", leftValue );
        holder.put( "y", object );
        return SimpleDegree.fromBooleanLiteral( MVEL.evalToBoolean(
                "x " + ChanceOperators.makePerfect( this.getOperator().getOperatorString() ) + " y",
                holder ) );

    }


    // will this help?
    protected Degree match( ExecutableStatement statement, Object object, Map<String, Object> vars ) {
        return vars == null ? (Degree) MVEL.executeExpression(statement, object) : (Degree) MVEL.executeExpression(statement, object, vars);
    }


}
