package org.drools.chance.reteoo.nodes;

import org.drools.base.InternalViewChangedEventListener;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.CompositeEvaluation;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.chance.evaluation.QueryEvaluation;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.reteoo.tuples.ImperfectQueryElementNodeLeftTuple;
import org.drools.chance.reteoo.tuples.ImperfectRightTuple;
import org.drools.chance.reteoo.tuples.ImperfectTuple;
import org.drools.common.InternalFactHandle;
import org.drools.common.QueryElementFactHandle;
import org.drools.reteoo.*;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.QueryElement;



public class ChanceQueryElementNode extends QueryElementNode {

    private int leftSourceId;


    public ChanceQueryElementNode() {

    }

    public ChanceQueryElementNode( int id,
                                   LeftTupleSource tupleSource,
                                   QueryElement queryElement,
                                   boolean tupleMemoryEnabled,
                                   boolean openQuery,
                                   BuildContext context ) {
        super( id, tupleSource, queryElement, tupleMemoryEnabled, openQuery, context );
    }


    public int getLeftSourceId() {
        return leftSourceId;
    }

    public void setLeftSourceId(int leftSourceId) {
        this.leftSourceId = leftSourceId;
    }



    @Override
    public LeftTuple createLeftTuple(InternalFactHandle factHandle, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectQueryElementNodeLeftTuple tup = new ImperfectQueryElementNodeLeftTuple( factHandle, sink, leftTupleMemoryEnabled );
//        throw new UnsupportedOperationException( "Not yet" );
        int key = ( (LeftInputAdapterNode) this.getLeftTupleSource() ).getParentObjectSource().getId();
        Evaluation eval = ((ChanceFactHandle) factHandle).getCachedEvaluation(key);
        tup.addEvaluation( eval != null ? eval : new MockEvaluation( key, SimpleDegree.TRUE ) );
        return tup;
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectQueryElementNodeLeftTuple tup = new ImperfectQueryElementNodeLeftTuple( leftTuple, sink, leftTupleMemoryEnabled );
        if ( leftTuple instanceof ImperfectTuple ) {
            tup.addEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
        }
//        throw new UnsupportedOperationException( "Not yet" );
        return tup;
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink) {
        ImperfectQueryElementNodeLeftTuple tup = new ImperfectQueryElementNodeLeftTuple( leftTuple, rightTuple, sink );
            tup.addEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
            tup.addEvaluation( ((ImperfectTuple) rightTuple).getEvaluation() );
        return tup;
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTuple currentLeftChild, LeftTuple currentRightChild, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectQueryElementNodeLeftTuple tup = new ImperfectQueryElementNodeLeftTuple( leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
            tup.addEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
            tup.addEvaluation( ((ImperfectTuple) rightTuple).getEvaluation() );
        return tup;
    }


    protected UnificationNodeViewChangedEventListener createCollector( LeftTuple leftTuple, int[] varIndexes, boolean tupleMemoryEnabled ) {
        return new ImperfectUnificationNodeViewChangedEventListener( leftTuple,
                varIndexes,
                this,
                tupleMemoryEnabled );
    }


    public static class ImperfectUnificationNodeViewChangedEventListener
            extends UnificationNodeViewChangedEventListener {

        public ImperfectUnificationNodeViewChangedEventListener(LeftTuple leftTuple, int[] variables, QueryElementNode node, boolean tupleMemoryEnabled) {
            super(leftTuple, variables, node, tupleMemoryEnabled);
        }

        protected RightTuple createResultRightTuple( QueryElementFactHandle resultHandle, LeftTuple resultLeftTuple, boolean open ) {
            Evaluation eval = ( (ImperfectTuple) resultLeftTuple ).getEvaluation();
            Evaluation local = new QueryEvaluation( node.getId(), eval, node.getQueryElement().getQueryName() );
            RightTuple rightTuple = new ImperfectRightTuple( resultHandle, local );
            if ( open ) {
                rightTuple.setLeftTuple( resultLeftTuple );
                resultLeftTuple.setObject( rightTuple );

            }
            return rightTuple;
        }
    }
}
