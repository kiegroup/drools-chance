package org.drools.chance.reteoo.nodes;


import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.reteoo.ChanceActivation;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.reteoo.tuples.ImperfectRuleTerminalNodeLeftTuple;
import org.drools.chance.reteoo.tuples.ImperfectTuple;
import org.drools.common.AgendaItem;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.*;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public class ChanceRuleTerminalNode extends RuleTerminalNode {


    public ChanceRuleTerminalNode() {
    }

    public ChanceRuleTerminalNode(int id, LeftTupleSource source, Rule rule, GroupElement subrule, int subruleIndex, BuildContext context) {
        super(id, source, rule, subrule, subruleIndex, context);
    }






    @Override
    public LeftTuple createLeftTuple( InternalFactHandle factHandle, LeftTupleSink sink, boolean leftTupleMemoryEnabled ) {
        ImperfectRuleTerminalNodeLeftTuple tuple = new ImperfectRuleTerminalNodeLeftTuple( factHandle, sink, leftTupleMemoryEnabled );

        int src = ( (LeftInputAdapterNode) this.getLeftTupleSource() ).getParentObjectSource().getId();
        tuple.setEvaluation( ((ChanceFactHandle) factHandle ).getCachedEvaluation( src ) );

        return tuple;
    }

    @Override
    public LeftTuple createLeftTuple( LeftTuple leftTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled ) {
        ImperfectRuleTerminalNodeLeftTuple tuple = new ImperfectRuleTerminalNodeLeftTuple(leftTuple, sink, leftTupleMemoryEnabled);

        tuple.setEvaluation( ((ImperfectTuple) leftTuple ).getEvaluation( ) );

        return tuple;
    }

    @Override
    public LeftTuple createLeftTuple( LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink ) {
//        ImperfectRuleTerminalNodeLeftTuple tuple = new ImperfectRuleTerminalNodeLeftTuple(leftTuple, rightTuple, sink);
//
//            tuple.setEvaluation( ((ImperfectTuple) leftTuple ).getEvaluation( ) );
//
//        return tuple;
        throw new IllegalStateException( " Chance rule terminal nodes should not be called this way ! ");
    }

    @Override
    public LeftTuple createLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTuple currentLeftChild, LeftTuple currentRightChild, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
//        ImperfectRuleTerminalNodeLeftTuple tuple = new ImperfectRuleTerminalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
//
//            tuple.setEvaluation( ((ImperfectTuple) leftTuple).getEvaluation( ) );
//
//        return tuple;
        throw new IllegalStateException( " Chance rule terminal nodes should not be called this way ! ");
    }




    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {

        if ( leftTuple instanceof ImperfectTuple ) {
            Evaluation eval = ((ImperfectTuple) leftTuple).getEvaluation();
            if ( eval != null && ! eval.getDegree().toBoolean() ) {
                return;
            }
        }
        super.assertLeftTuple( leftTuple, context, workingMemory );
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        Object o = leftTuple.getObject();
        if ( o instanceof ChanceActivation ) {
            ((ChanceActivation) o ).setEvaluation( ((ImperfectTuple) leftTuple).getEvaluation() );
        }
        super.modifyLeftTuple( leftTuple, context, workingMemory );
    }



//    public LeftTupleSource unwrapTupleSource() {
//        LeftTupleSource src = tupleSource;
//        if ( tupleSource instanceof LogicalBetaOperatorNode ) {
//            return ((LogicalBetaOperatorNode) src).getLeftTupleSource();
//        }
//        if ( tupleSource instanceof FromNode ) {
//            return ((FromNode) src).getLeftTupleSource();
//        }
//        return src;
//    }
}
