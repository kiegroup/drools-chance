package org.drools.chance.reteoo.builder;


import org.drools.base.ValueType;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.reteoo.nodes.*;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.reteoo.*;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.builder.NodeFactory;
import org.drools.rule.From;
import org.drools.rule.GroupElement;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.DataProvider;
import org.drools.spi.ObjectType;

public class ChanceNodeFactory implements NodeFactory {

    public AlphaNode buildAlphaNode( int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context ) {
        return new ChanceAlphaNode( id, constraint, objectSource, context );
    }


    public TerminalNode buildTerminalNode(int id, LeftTupleSource source, Rule rule, GroupElement subrule, int subruleIndex, BuildContext context) {
        return new ChanceRuleTerminalNode( id, source, rule, subrule, subruleIndex, context );
    }

    public ObjectTypeNode buildObjectTypeNode( int id, EntryPointNode objectSource, ObjectType objectType, BuildContext context ) {
        if ( objectType.getValueType().equals( ValueType.TRAIT_TYPE ) ) {
            ChanceTraitObjectTypeNode otn = new ChanceTraitObjectTypeNode( id, objectSource, objectType, context );
            otn.setImperfect( ChanceObjectTypeNode.isImperfect( objectType ) );
            return otn;
        } else {
            ChanceObjectTypeNode otn = new ChanceObjectTypeNode( id, objectSource, objectType, context );
            otn.setImperfect( ChanceObjectTypeNode.isImperfect( objectType ) );
            return otn;
        }


    }

    public JoinNode buildJoinNode( int id, LeftTupleSource leftInput, ObjectSource rightInput, BetaConstraints binder, BuildContext context ) {
        return new ChanceJoinNode( id, leftInput, rightInput, binder, context );

    }

    public TerminalNode buildQueryTerminalNode( int id, LeftTupleSource source, Rule rule, GroupElement subrule, int subruleIndex, BuildContext context ) {
        return new ChanceQueryTerminalNode( id, source, rule, subrule, subruleIndex, context );
    }

    public QueryElementNode buildQueryElementNode( int id, LeftTupleSource tupleSource, QueryElement qe, boolean tupleMemoryEnabled, boolean openQuery, BuildContext context) {
        return new ChanceQueryElementNode( id, tupleSource, qe, tupleMemoryEnabled, openQuery, context );
    }

    public LeftInputAdapterNode buildLeftInputAdapterNode( int id, ObjectSource objectSource, BuildContext context ) {
        // No modification needed for now
        return new LeftInputAdapterNode( id, objectSource, context );
    }

    public BaseNode buildFromNode( int id, DataProvider dataProvider, LeftTupleSource tupleSource, AlphaNodeFieldConstraint[] alphaNodeFieldConstraints, BetaConstraints betaConstraints, boolean tupleMemoryEnabled, BuildContext context, From from ) {
        return new ChanceFromNode( id, dataProvider, tupleSource, alphaNodeFieldConstraints, betaConstraints, tupleMemoryEnabled, context, from );
    }

    public LogicalAplhaOperatorNode buildLogicalAlphaOperatorNode(int id, String label, ConnectiveCore conn, int arity, ObjectSource objectSource, BuildContext context) {
        int[] argIndexes = analyzeAlphaEvaluationSources( context, arity );
        return new LogicalAplhaOperatorNode( id, label, conn, argIndexes, arity, objectSource, context );
    }

    public LogicalBetaOperatorNode buildLogicalBetaOperatorNode( int id, String label, ConnectiveCore conn, int arity, LeftTupleSource tupleSource, BuildContext context ) {
        int[] argIndexes = analyzeBetaEvaluationSources( context, arity );
        return new LogicalBetaOperatorNode( id, label, conn, arity, argIndexes, tupleSource, context );
    }

    public BaseNode buildDelayedEvaluationNode( int nextId, Constraint constraint, ObjectSource objectSource, BuildContext context ) {
        return new DelayedEvaluationNode( nextId, constraint, objectSource, context );
    }



    private int[] analyzeAlphaEvaluationSources( BuildContext context, int arity ) {
        int[] indexes = new int[ arity ];

        ObjectSource src = context.getObjectSource();

        for ( int j = 0; j < arity; j++ ) {
            //            System.out.println( "ChancePatternBuilder : Preparing arg from node " + src);
            indexes[ arity - j - 1 ] = src.getId();
            if ( j != arity-1  ) {
                if ( src instanceof LogicalAplhaOperatorNode ) {
                    src = consume( src );
                } else {
                    src = skip( src );
                }
            }
        }
        return indexes;
    }

    private ObjectSource consume( ObjectSource src ) {
        int subArity = ( (LogicalAplhaOperatorNode) src ).getArity();
        src = skip( src );
        for ( int j = 0; j < subArity; j++ ) {

            if ( src instanceof LogicalAplhaOperatorNode ) {
                src = consume( src );
            } else {
                src = skip( src );
            }
        }
        return src;
    }

    private ObjectSource skip( ObjectSource src ) {
        src = src.getParentObjectSource();
        return src;
    }


    private int[] analyzeBetaEvaluationSources( BuildContext context, int arity ) {
        int[] indexes = new int[ arity ];

        LeftTupleSource src = context.getTupleSource();

        for ( int j = 0; j < arity; j++ ) {
            //            System.out.println( "ChancePatternBuilder : Preparing arg from node " + src);
            if ( src instanceof BetaNode ) {
                indexes[ arity - j - 1 ] = ((BetaNode) src).unwrapRightInput().getId();
            } else if ( src instanceof LeftInputAdapterNode ) {
                indexes[ arity - j - 1 ] = ((LeftInputAdapterNode) src).getParentObjectSource().getId();
            } else if ( src instanceof QueryElementNode ) {
                indexes[ arity - j - 1 ] = src.getId();
            } else if ( src instanceof FromNode ) {
                            indexes[ arity - j - 1 ] = src.getId();
                        }
            if ( j != arity-1 ) {
                if ( src instanceof LogicalBetaOperatorNode ) {
                    src = consumeBeta( src );
                } else {
                    src = skipBeta( src );
                }
            }
        }
        return indexes;
    }

    private LeftTupleSource consumeBeta( LeftTupleSource src ) {
        int subArity = ( (LogicalBetaOperatorNode) src ).getArity();
        src = skipBeta( src );
        for ( int j = 0; j < subArity; j++ ) {

            if ( src instanceof LogicalBetaOperatorNode) {
                src = consumeBeta( src );
            } else {
                src = skipBeta( src );
            }
        }
        return src;
    }

    private LeftTupleSource skipBeta( LeftTupleSource src ) {
        if ( src instanceof BetaNode ) {
            return ((BetaNode) src).getLeftTupleSource();
        } else if ( src instanceof QueryElementNode ) {
            return ((QueryElementNode) src).getLeftTupleSource();
        } else if ( src instanceof FromNode ) {
            return ((FromNode) src).getLeftTupleSource();
        } else {
            throw new UnsupportedOperationException( "Can't navigate this path " );
        }
    }



}
