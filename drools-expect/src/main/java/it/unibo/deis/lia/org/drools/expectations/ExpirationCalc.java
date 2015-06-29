package it.unibo.deis.lia.org.drools.expectations;

import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.DrlExprParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.*;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.base.evaluators.TimeIntervalParser;
import org.kie.internal.builder.conf.LanguageLevelOption;

import java.util.HashSet;
import java.util.Set;

public class ExpirationCalc {

    private static TimeIntervalParser tp = new TimeIntervalParser();

    public static long calcExpirationOffset( AndDescr trigger, BaseDescr ce ) {
        if ( ce instanceof AndDescr ) {
            long offset = Long.MAX_VALUE;
            for ( BaseDescr child : ( (AndDescr) ce ).getDescrs() ) {
                offset = Math.min( offset, calcExpirationOffset( trigger, child ) );
            }
            return offset;
        } else if ( ce instanceof OrDescr ) {
            long offset = 0;
            for (BaseDescr child : ((AndDescr) ce).getDescrs()) {
                offset = Math.max(offset, calcExpirationOffset(trigger, child));
            }
            return offset;
        } else if ( ce instanceof ForallDescr) {
            long offset = 0;
            for (BaseDescr child: ((ForallDescr)ce).getDescrs()) {
                offset = Math.max(offset, calcExpirationOffset(trigger, child));
            }
            return offset;
        } else if ( ce instanceof PatternDescr ) {
            return calcExpirationOffsetForPattern( trigger, (PatternDescr) ce );
        } else if ( ce instanceof NotDescr ) {
            return calcExpirationOffset( trigger, (BaseDescr) ( (NotDescr) ce ).getDescrs().get( 0 ) );
        } else {
            throw new UnsupportedOperationException( "Defensive : unsupported operation" );
        }
    }


    public static long calcExpirationOffsetForPattern( AndDescr trigger, PatternDescr expectationPattern ) {

        Set<String> ids = new HashSet<String>();

        for ( BaseDescr bd : trigger.getDescrs() ) {
            if ( bd instanceof PatternDescr ) {
                PatternDescr pd = (PatternDescr) bd;
                if ( pd.getIdentifier() != null ) {
                    ids.add( pd.getIdentifier() );
                }
            }
        }

        long max = Long.MAX_VALUE;

        for ( BaseDescr bd : expectationPattern.getDescrs() ) {
            if ( bd instanceof ExprConstraintDescr ) {
                ExprConstraintDescr ecd = (ExprConstraintDescr) bd;

                // should be more robust than this
                if ( isTemporal( ecd ) ) {
                    DrlExprParser parser = new DrlExprParser( LanguageLevelOption.DRL6 );
                    ConstraintConnectiveDescr result = parser.parse( ecd.getExpression() );
                    if ( ! parser.hasErrors() ) {
                        for ( BaseDescr descr : result.getDescrs() ) {
                            if ( descr instanceof RelationalExprDescr ) {
                                RelationalExprDescr rel = (RelationalExprDescr) descr;
                                if ( isTemporalOp( rel.getOperator() ) ) {
                                    OperatorDescr op = rel.getOperatorDescr();
                                    // Added to replace commented section that follows
                                    max = Math.min(max, calOffset(op));
                                    /**
                                     * Commented out for the purpose of getting this to work
                                     * when we don't have full event-ization
                                    if ( ids.contains( rel.getRight().toString() ) && "this".equals( rel.getLeft().toString() ) ) {
                                        max = Math.min( max, calOffset( op ) );
                                    } else if ( ids.contains( rel.getLeft().toString() ) && "this".equals( rel.getRight().toString() ) ) {
                                        throw new UnsupportedOperationException( "Defensive : mirror temporal operator!" );
                                    } else {
                                        throw new UnsupportedOperationException( "Defensive : unrelated temporal relationship!" );
                                    }
                                    */
                                }
                            }
                        }
                    }
                }
            }
        }

        return max;
        
    }

    private static long calOffset( OperatorDescr op ) {
        if ( "after".equals( op.getOperator() ) ) {
            if ( op.getParameters() == null || op.getParameters().isEmpty() || op.getParameters().size() == 1 ) {
                return Long.MAX_VALUE;
            } else if ( op.getParameters().size() == 2 ) {
                Long[] range = tp.parse( op.getParametersText() );
                return Math.max( range[ 0 ], range[ 1 ] );
            } else {
                throw new IllegalStateException( "After operator can have 2 param max, found " + op.getParameters() );
            }
        } else if ( "before".equals( op.getOperator() ) ) {
                if ( op.getParameters() == null || op.getParameters().isEmpty() || op.getParameters().size() == 1 ) {
                    return 0;
                } else if ( op.getParameters().size() == 2 ) {
                    Long[] range = tp.parse( op.getParametersText() );
                    return Math.max( 0, - Math.min( - range[ 0 ], - range[ 1 ] ) );
                } else {
                    throw new IllegalStateException( "After operator can have 2 param max, found " + op.getParameters() );
                }
        } else {
            throw new UnsupportedOperationException( "Defensive: NOT YET implemented deadline estimation for operator " + op.getOperator() );
        }
    }

    private static boolean isTemporalOp( String operator ) {
        return ( operator.equals( "before" ) 
                        || operator.equals( "after" ) 
                        || operator.equals( "coincides" )
                        || operator.equals( "during" ) 
                        || operator.equals( "finishes" ) 
                        || operator.equals( "finishedby" ) 
                        || operator.equals( "includes" ) 
                        || operator.equals( "meets" ) 
                        || operator.equals( "metby" ) 
                        || operator.equals( "overlaps" )
                        || operator.equals( "overlappedby" ) 
                        || operator.equals( "starts" ) 
                        || operator.equals( "startedby" ) 
        );
    }

    private static boolean isTemporal( ExprConstraintDescr ecd ) {
        String expr = ecd.getExpression();
        if ( expr == null ) {
            return false;
        }
        boolean ans = ( expr.contains( " before " ) || expr.contains( " before[" )
                        || expr.contains( " after " ) || expr.contains( " after[" )
                        || expr.contains( " coincides " ) || expr.contains( " coincides[" )
                        || expr.contains( " during " ) || expr.contains( " during[" )
                        || expr.contains( " finishes " ) || expr.contains( " finishes[" )
                        || expr.contains( " finishedby " ) || expr.contains( " finishedby[" )
                        || expr.contains( " includes " ) || expr.contains( " includes[" )
                        || expr.contains( " meets " ) || expr.contains( " meets[" )
                        || expr.contains( " metby " ) || expr.contains( " metby[" )
                        || expr.contains( " overlaps " ) || expr.contains( " overlaps[" )
                        || expr.contains( " overlappedby " ) || expr.contains( " overlappedby[" )
                        || expr.contains( " starts " ) || expr.contains( " starts[" )
                        || expr.contains( " startedby " ) || expr.contains( " startedby[" )
        );
        return ans;
    }


}
/*

    public void buildExpirationRule( AndDescr trigger, String expLabel, PatternDescr pattern, PackageDescrBuilder packBuilder ) {
        Set<String> ids = new HashSet<String>();

        for (BaseDescr bd : trigger.getDescrs()) {
            if (bd instanceof PatternDescr) {
                PatternDescr pd = (PatternDescr) bd;
                if (pd.getIdentifier() != null) {
                    ids.add(pd.getIdentifier());
                }
            }
        }


        String id = "$trigger";
        Collection<ExprConstraintDescr> timeConstraints = new ArrayList<ExprConstraintDescr>();
        ExprConstraintDescr timeConst = null;
        for (BaseDescr bd : pattern.getDescrs()) {
            if (bd instanceof ExprConstraintDescr) {
                ExprConstraintDescr ecd = (ExprConstraintDescr) bd;
                if ( isTemporal( ecd ) ) {
                    StringTokenizer tok = new StringTokenizer( ecd.getExpression() );
                    String identf = "";
                    while ( tok.hasMoreTokens() ) {
                        identf = tok.nextToken();
                    }
                    if ( ids.contains( identf ) ) {
                        ExprConstraintDescr revised = new ExprConstraintDescr( ecd.getExpression().replace( identf, id ) );
                        timeConstraints.add( revised );
                    }
                }

            }
        }


        if (timeConstraints.size() == 0) {
            return;
        }

        RuleDescrBuilder ruleBuilder = packBuilder.newRule();
            ruleBuilder.name(EXP_PACKAGE + "." + expLabel);
            ruleBuilder.rhs("");

        CEDescrBuilder<RuleDescrBuilder,AndDescr> lhs = ruleBuilder.lhs();

        PatternDescrBuilder holder = lhs.pattern();
            holder.type(EXP_PACKAGE + ".Pending");
            holder.isQuery(false);
            holder.id(id,false);
            holder.constraint("\"" + expLabel + "\"",true);


        PatternDescrBuilder sat = lhs.pattern( pattern.getObjectType() );
            sat.isQuery(false);
            for ( ExprConstraintDescr ecd : timeConstraints ) {
                sat.constraint( ecd.getExpression(), false );
            }

    }

 */
