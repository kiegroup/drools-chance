package org.drools.chance.reteoo.nodes;

import org.drools.chance.rule.constraint.ImperfectBetaConstraint;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.SimpleEvaluationImpl;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.reteoo.tuples.*;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.reteoo.*;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.PropagationContext;


public class ChanceJoinNode extends JoinNode {

    protected int rightSourceId;
    protected int leftSourceId;

    public ChanceJoinNode(int id, LeftTupleSource leftInput, ObjectSource rightInput, BetaConstraints binder, BuildContext context) {
        super( id, leftInput, rightInput, binder, context );
        rightSourceId = rightInput instanceof PropagationQueuingNode ? ((PropagationQueuingNode) rightInput).getParentObjectSource().getId() : rightInput.getId();
        leftSourceId = leftInput instanceof LeftInputAdapterNode ? ((LeftInputAdapterNode) leftInput).getParentObjectSource().getId() : leftInput.getId();
    }


    @Override
    public RightTuple createRightTuple( InternalFactHandle handle, RightTupleSink sink, PropagationContext context ) {
        Evaluation eval = ((ChanceFactHandle) handle).getCachedEvaluation(rightSourceId) ;
        if ( ! this.concurrentRightTupleMemory ) {
            if ( context.getActiveWindowTupleList() == null ) {
                return new ImperfectRightTuple( handle,
                        sink,
                        eval );
            } else {
                return new ImperfectWindowTuple( handle,
                        sink,
                        context.getActiveWindowTupleList(),
                        eval );
            }
        } else {
            return new ImperfectConcurrentRightTuple( handle,
                    sink,
                    eval );
        }
    }

    @Override
    public LeftTuple createLeftTuple( LeftTuple leftTuple, RightTuple rightTuple, LeftTuple currentLeftChild, LeftTuple currentRightChild, LeftTupleSink sink, boolean leftTupleMemoryEnabled ) {
        ImperfectJoinNodeLeftTuple tup = new ImperfectJoinNodeLeftTuple( leftTuple,
                rightTuple,
                currentLeftChild,
                currentRightChild,
                sink,
                leftTupleMemoryEnabled);
        tup.addEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
        tup.addEvaluation( ((ImperfectTuple) rightTuple).getEvaluation() );
        return tup;
    }

    @Override
    public LeftTuple createLeftTuple( LeftTuple leftTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled ) {
        ImperfectJoinNodeLeftTuple tup = new ImperfectJoinNodeLeftTuple( leftTuple,
                sink,
                leftTupleMemoryEnabled );
        throw new UnsupportedOperationException( " Can't propagate yet " );
    }

    @Override
    public LeftTuple createLeftTuple( InternalFactHandle factHandle, LeftTupleSink sink, boolean leftTupleMemoryEnabled ) {
        ImperfectJoinNodeLeftTuple tup = new ImperfectJoinNodeLeftTuple( factHandle,
                sink,
                leftTupleMemoryEnabled );
        Evaluation eval = ((ChanceFactHandle) factHandle).getCachedEvaluation( leftSourceId, true );

        tup.addEvaluation( eval );

        return tup;
    }

    @Override
    public LeftTuple createLeftTuple( LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink ) {
        ImperfectJoinNodeLeftTuple tup = new ImperfectJoinNodeLeftTuple( leftTuple,
                rightTuple,
                sink);
            tup.addEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
            tup.addEvaluation( ((ImperfectTuple) rightTuple).getEvaluation() );
        return tup;
    }

    @Override
    protected void propagateFromRight(RightTuple rightTuple, LeftTuple leftTuple, BetaMemory memory, PropagationContext context, InternalWorkingMemory workingMemory) {

        LinkedList constraintList = this.constraints.getConstraints();
        Iterator iter = constraintList.iterator();
        Object con;
        int j = 0;
        while ( ( con = iter.next() ) != null ) {
            Degree degree;
            con = ( (LinkedListEntry) con ).getObject();

            if ( con instanceof ImperfectBetaConstraint ) {
                ImperfectBetaConstraint ibc = (ImperfectBetaConstraint) con;
                degree = ibc.matchCachedRight( leftTuple, memory.getContext()[j] );
                Evaluation eval = new SimpleEvaluationImpl( ibc.getNodeId(), con.toString(), degree, ibc.getLabel() );
                ((ImperfectRightTuple) rightTuple).addEvaluation( eval );
            } else {
                boolean allowed = ((BetaNodeFieldConstraint) con).isAllowedCachedRight( leftTuple, memory.getContext()[j] );
                if ( ! allowed ) {
                    System.err.println( "Crisp beta cnstraint blocked propagation on ChanceJoinNode propagateFromRight ");
                    return;
                }
            }
            j++;
        }

        this.sink.propagateAssertLeftTuple( leftTuple,
                rightTuple,
                null,
                null,
                context,
                workingMemory,
                true );
    }

    @Override
    protected void propagateFromLeft(RightTuple rightTuple, LeftTuple leftTuple, ContextEntry[] contextEntry, boolean useLeftMemory, PropagationContext context, InternalWorkingMemory workingMemory) {

        LinkedList constraintList = this.constraints.getConstraints();
        Iterator iter = constraintList.iterator();
        Object con;
        int j = 0;
        while ( ( con = iter.next() ) != null ) {
            Degree degree;
            con = ( (LinkedListEntry) con ).getObject();

            if ( con instanceof ImperfectBetaConstraint ) {
                ImperfectBetaConstraint ibc = (ImperfectBetaConstraint) con;
                degree = ibc.matchCachedLeft( contextEntry[j], rightTuple.getFactHandle() );
                Evaluation eval = new SimpleEvaluationImpl( ibc.getNodeId(), con.toString(), degree, ibc.getLabel() );
                ((ImperfectRightTuple) rightTuple).addEvaluation( eval );
            } else {
                boolean allowed = ((BetaNodeFieldConstraint) con).isAllowedCachedLeft( contextEntry[j], rightTuple.getFactHandle() );
                if ( ! allowed ) {
                    System.err.println( "Crisp beta cnstraint blocked propagation on ChanceJoinNode propagateFromRight ");
                    return;
                }
            }
            j++;
        }

        this.sink.propagateAssertLeftTuple( leftTuple,
                rightTuple,
                null,
                null,
                context,
                workingMemory,
                useLeftMemory );

    }
}
