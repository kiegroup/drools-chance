package org.drools.chance.reteoo.nodes;

import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.reteoo.tuples.ImperfectQueryElementNodeLeftTuple;
import org.drools.chance.reteoo.tuples.ImperfectTuple;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.*;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;


public class ChanceQueryTerminalNode extends QueryTerminalNode {

    public ChanceQueryTerminalNode() {
    }

    public ChanceQueryTerminalNode(int id, LeftTupleSource source, Rule rule, GroupElement subrule, int subruleIndex, BuildContext context) {
        super(id, source, rule, subrule, subruleIndex, context);
    }

    @Override
    public LeftTuple createLeftTuple(InternalFactHandle factHandle, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectQueryElementNodeLeftTuple tup = new ImperfectQueryElementNodeLeftTuple( factHandle, sink, leftTupleMemoryEnabled );
            throw new UnsupportedOperationException("TODO CQT");
//        return tup;
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectQueryElementNodeLeftTuple tup = new ImperfectQueryElementNodeLeftTuple( leftTuple, sink, leftTupleMemoryEnabled );
            tup.addEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
        return tup;
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink) {
        ImperfectQueryElementNodeLeftTuple tup = new ImperfectQueryElementNodeLeftTuple( leftTuple, rightTuple, sink );
//        return tup;
        throw new UnsupportedOperationException("TODO CQT");
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTuple currentLeftChild, LeftTuple currentRightChild, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectQueryElementNodeLeftTuple tup = new ImperfectQueryElementNodeLeftTuple( leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
//        return tup;
        throw new UnsupportedOperationException("TODO CQT");
    }

}
