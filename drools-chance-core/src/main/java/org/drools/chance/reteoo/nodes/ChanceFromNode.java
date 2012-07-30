package org.drools.chance.reteoo.nodes;

import org.drools.base.DroolsQuery;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.CompositeEvaluation;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.chance.evaluation.SimpleEvaluationImpl;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.reteoo.tuples.ImperfectFromNodeLeftTuple;
import org.drools.chance.reteoo.tuples.ImperfectRightTuple;
import org.drools.chance.reteoo.tuples.ImperfectTuple;
import org.drools.chance.rule.constraint.ImperfectAlphaConstraint;
import org.drools.chance.rule.constraint.ImperfectBetaConstraint;
import org.drools.chance.rule.constraint.ImperfectConstraint;
import org.drools.chance.rule.constraint.OperatorConstraint;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.reteoo.*;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.From;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;


public class ChanceFromNode extends FromNode {
    
    private ConnectiveCore and;

    public ChanceFromNode( int id, DataProvider dataProvider, LeftTupleSource tupleSource, AlphaNodeFieldConstraint[] alphaNodeFieldConstraints, BetaConstraints betaConstraints, boolean tupleMemoryEnabled, BuildContext context, From from) {
        super( id, dataProvider, tupleSource, alphaNodeFieldConstraints, betaConstraints, tupleMemoryEnabled, context, from );

        //TODO FIXME consume config!
        and = ChanceStrategyFactory.getConnectiveFactory( null,null ).getAnd();
    }

    @Override
    public LeftTuple createLeftTuple(InternalFactHandle factHandle, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectTuple tup = new ImperfectFromNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled);
            int src = ((LeftInputAdapterNode) this.getLeftTupleSource()).getParentObjectSource().getId();
            tup.addEvaluation( ((ChanceFactHandle) factHandle).getCachedEvaluation( src ) );
        return (LeftTuple) tup;
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectTuple tup = new ImperfectFromNodeLeftTuple(leftTuple, sink, leftTupleMemoryEnabled);
            tup.addEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
        return (LeftTuple) tup;
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink) {
        ImperfectTuple tup = new ImperfectFromNodeLeftTuple(leftTuple, rightTuple, sink);
            tup.addEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
            tup.addEvaluation( ((ImperfectTuple) rightTuple).getEvaluation() );
        return (LeftTuple) tup;
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTuple currentLeftChild, LeftTuple currentRightChild, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectTuple tup = new ImperfectFromNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
            tup.addEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
            tup.addEvaluation( ((ImperfectTuple) rightTuple).getEvaluation() );
        return (LeftTuple) tup;
    }

    protected RightTuple newRightTuple(InternalFactHandle handle, Object o) {
        return new ImperfectRightTuple( handle,
                                        null );
    }


    @Override
    public void modifyLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        super.modifyLeftTuple(leftTuple, context, workingMemory);
    }



    protected void checkConstraintsAndPropagate( final LeftTuple leftTuple,
                                                 final RightTuple rightTuple,
                                                 final PropagationContext context,
                                                 final InternalWorkingMemory workingMemory,
                                                 final FromMemory memory,
                                                 final boolean useLeftMemory ) {
        Stack<Evaluation> results = new Stack<Evaluation>();
        results.push( new SimpleEvaluationImpl( this.id, SimpleDegree.TRUE ) ); // emulate OTN
            //<Evaluation[ this.alphaConstraints.length + this.betaConstraints.getConstraints().size() ];
        int ccounter = 0;
        
        boolean canPropagate = true;
        if ( this.alphaConstraints != null ) {
            // First alpha node filters
            for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {

                AlphaNodeFieldConstraint constraint = this.alphaConstraints[i];
                ChanceFactHandle factHandle = (ChanceFactHandle) rightTuple.getFactHandle();
                if ( constraint instanceof ImperfectConstraint) {
                    Degree degree;
                    if ( constraint instanceof OperatorConstraint ) {
                        OperatorConstraint opc = (OperatorConstraint) constraint;
                        int n = opc.getArity(); 
                        Evaluation[] args = new Evaluation[ n ];
                        for ( int j = 0; j < n; j++ ) {
                            if ( results.isEmpty() ) {
                                args[j] = new MockEvaluation( id, SimpleDegree.TRUE );
                            } else {
                                args[j] = results.pop();
                            }
                        }
                        degree = opc.getConnective().eval( args ); 
                        results.push( new CompositeEvaluation( getId(), degree, opc.getConnective(), args ) );
                    } else {
                        ImperfectAlphaConstraint alpha = (ImperfectAlphaConstraint) constraint;
                        degree = alpha.match( (InternalFactHandle) factHandle,
                                workingMemory,
                                memory.alphaContexts[i] );

                        results.push( new SimpleEvaluationImpl( getId(), constraint.toString(), degree, alpha.getLabel() ) );
                    }
                    canPropagate = canPropagate && degree.toBoolean();
                } else {
                    boolean allowed = constraint.isAllowed( (InternalFactHandle) factHandle, workingMemory, memory.alphaContexts[i] );
                    results.push( new SimpleEvaluationImpl( getId(), constraint.toString(), SimpleDegree.fromBooleanLiteral(allowed) ) );
                    canPropagate = canPropagate && allowed;
                }
                
                ccounter++;
            }            
        }

        BetaNodeFieldConstraint[] constraintList = this.betaConstraints.getConstraints();

        for ( int j = 0; j < constraintList.length; j++ ) {
            Degree degree;
            BetaNodeFieldConstraint con = constraintList[j];

            if ( con instanceof ImperfectBetaConstraint) {
                if ( con instanceof OperatorConstraint ) {
                    OperatorConstraint opc = (OperatorConstraint) con;
                    int n = opc.getArity();
                    Evaluation[] args = new Evaluation[ n ];
                    for ( int k = 0; k < n; k++ ) {
                        args[k] = results.pop();
                    }
                    results.push( new CompositeEvaluation( getId(), opc.getConnective().eval( args ), opc.getConnective(), args ) );
                } else {
                    ImperfectBetaConstraint ibc = (ImperfectBetaConstraint) con;
                    degree = ibc.matchCachedLeft( memory.betaMemory.getContext()[j], rightTuple.getFactHandle()  );
                    results.push( new SimpleEvaluationImpl( ibc.getNodeId(), con.toString(), degree, ibc.getLabel() ) );
                    canPropagate = canPropagate && degree.toBoolean();
                }
                
            } else {
                
                boolean allowed = ((BetaNodeFieldConstraint) con).isAllowedCachedRight( leftTuple, memory.betaMemory.getContext()[j] );
                if ( ! allowed ) {
                    canPropagate = false;
                }
                
            }
            j++;
            ccounter++;
        }
        
        //TODO FIXME Support inner operators
        if ( results.size() > 0 ) {
            Evaluation[] args = results.toArray( new Evaluation[ results.size() ] );
            Evaluation composite = new CompositeEvaluation( this.getId(), and.eval( args ), and, args );
            ((ImperfectRightTuple) rightTuple).addEvaluation( composite );
        } else {
            ((ImperfectRightTuple) rightTuple).addEvaluation( new SimpleEvaluationImpl( this.id, SimpleDegree.TRUE ) );
        }

        if ( canPropagate ) {

            if ( rightTuple.firstChild == null ) {
                // this is a new match, so propagate as assert
                this.sink.propagateAssertLeftTuple( leftTuple,
                        rightTuple,
                        null,
                        null,
                        context,
                        workingMemory,
                        useLeftMemory );
            } else {
                // this is an existing match, so propagate as a modify
                this.sink.propagateModifyChildLeftTuple( rightTuple.firstChild,
                        leftTuple,
                        context,
                        workingMemory,
                        useLeftMemory );
            }
        } else {
            retractMatch( leftTuple,
                    rightTuple,
                    context,
                    workingMemory );
        }
    }
}
