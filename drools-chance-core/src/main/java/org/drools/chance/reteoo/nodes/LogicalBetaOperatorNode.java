package org.drools.chance.reteoo.nodes;

import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.chance.reteoo.tuples.ImperfectRuleTerminalNodeLeftTuple;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.CompositeEvaluation;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.reteoo.tuples.ImperfectLeftTuple;
import org.drools.chance.reteoo.tuples.ImperfectTuple;
import org.drools.common.*;
import org.drools.reteoo.*;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;


public class LogicalBetaOperatorNode extends LeftTupleSource
        implements
        LeftTupleSinkNode {

    private ConnectiveCore           connective;
    private int                      arity;
    private int[]                    argIndexes;
    private String                   label;


    /** The left input <code>TupleSource</code>. */
    protected LeftTupleSource leftInput;

    private LeftTupleSinkNode previousLeftTupleSinkNode;
    private LeftTupleSinkNode nextLeftTupleSinkNode;
    private boolean           tupleMemoryEnabled;
    private boolean           fromLIA;


    public LogicalBetaOperatorNode( int id, String label, ConnectiveCore conn, int arity, int[] indexes, LeftTupleSource tupleSource, BuildContext context ) {
        super( id,
                context.getPartitionId(),
                context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.connective = conn;
        this.arity = arity;
        this.argIndexes = indexes;
        this.label = label;

        this.leftInput = tupleSource;
        this.fromLIA = tupleSource instanceof LeftInputAdapterNode;

        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();

        initMasks( context, leftInput );
    }

    public int getArity() {
        return arity;
    }

    public ConnectiveCore getConnective() {
        return connective;
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        connective = (ConnectiveCore) in.readObject();
        arity = in.readInt();
        argIndexes = (int[]) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject( connective );
        out.writeInt( arity );
        out.writeObject( argIndexes );
    }

    public void attach() {
        this.leftInput.addTupleSink(this);
    }



    public void networkUpdated(UpdateContext updateContext) {
        updateContext.startVisitNode(leftInput);
        updateContext.endVisit();
        if ( !updateContext.isVisiting( leftInput ) ) {
            leftInput.networkUpdated( updateContext );
        }
    }

    public void attach( BuildContext context ) {
        attach();

        for ( int i = 0, length = context.getWorkingMemories().length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = context.getWorkingMemories()[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                    PropagationContext.RULE_ADDITION,
                    null,
                    null,
                    null );

            this.leftInput.updateSink( this,
                    propagationContext,
                    workingMemory );
        }

    }


    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeTupleSink( (LeftTupleSink) node );
        }
        if ( !this.isInUse() || context.getCleanupAdapter() != null ) {
            context.setCleanupAdapter( null );
        }
        this.leftInput.remove( context,
                builder,
                this,
                workingMemories );
    }

    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return previousLeftTupleSinkNode;
    }

    public void setPreviousLeftTupleSinkNode(LeftTupleSinkNode previousLeftTupleSinkNode) {
        this.previousLeftTupleSinkNode = previousLeftTupleSinkNode;
    }

    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return nextLeftTupleSinkNode;
    }

    public void setNextLeftTupleSinkNode(LeftTupleSinkNode nextLeftTupleSinkNode) {
        this.nextLeftTupleSinkNode = nextLeftTupleSinkNode;
    }



    public String toString() {
        return "[" + getId() +"] Operator " + connective;
    }

    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogicalBetaOperatorNode that = (LogicalBetaOperatorNode) o;

        if (connective != that.connective) return false;
        if (arity != that.arity) return false;

        return true;
    }



    @Override
    public void updateSink( LeftTupleSink sink, PropagationContext context, InternalWorkingMemory workingMemory ) {
        throw new UnsupportedOperationException( "Logical Beta Operator Node Pass-through, not implemented yet!" );
    }

    protected ObjectTypeNode getObjectTypeNode() {
        ObjectTypeNode objectTypeNode = null;
            ObjectSource source = ((LeftInputAdapterNode) this.getLeftTupleSource()).getParentObjectSource();
            while ( source != null ) {
                if ( source instanceof ObjectTypeNode ) {
                    objectTypeNode = (ObjectTypeNode) source;
                    break;
                }
                source = source.getParentObjectSource();
            }
        return objectTypeNode;
    }


    public short getType() {
        return NodeTypeEnums.OperatorNode;
    }

    public void assertLeftTuple( LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory ) {
        this.sink.propagateAssertLeftTuple( leftTuple, context, workingMemory, isLeftTupleMemoryEnabled() );
    }

    public void retractLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        this.sink.propagateRetractLeftTuple( leftTuple, context, workingMemory );
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled( boolean tupleMemoryEnabled ) {
        throw new UnsupportedOperationException( "Logical Beta Operator Node Pass-through, not implemented yet!" );
    }

    public LeftTupleSource getLeftTupleSource() {
        return leftInput;
    }

    public void modifyLeftTuple( LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory ) {
        if ( leftTuple.getFirstChild() == null ) {
            return;
        }
        if ( ! fromLIA ) {
            //TODO FIXME
            if ( leftTuple.getParent() instanceof ImperfectTuple ) {
                Evaluation eval = reevaluate( (ImperfectTuple) leftTuple.getParent(), (ImperfectTuple) leftTuple.getRightParent() );
                if ( ! eval.getDegree().toBoolean() ) {
                    return;
                }
                ((ImperfectTuple) leftTuple.getFirstChild()).addEvaluation( eval );
            }
        }
        this.sink.propagateModifyChildLeftTuple( leftTuple, context, workingMemory, isLeftTupleMemoryEnabled() );
    }



    public LeftTuple createLeftTuple( InternalFactHandle factHandle, LeftTupleSink sink, boolean leftTupleMemoryEnabled ) {
        ImperfectTuple tuple = new ImperfectLeftTuple( factHandle, sink, leftTupleMemoryEnabled );

        tuple.setEvaluation( ((ChanceFactHandle) factHandle ).getCachedEvaluation( argIndexes[0] ) );

        return (LeftTuple) tuple;
    }

    public LeftTuple createLeftTuple( LeftTuple leftTuple, LeftTupleSink sink, boolean leftTupleMemoryEnabled ) {
        ImperfectRuleTerminalNodeLeftTuple tuple = new ImperfectRuleTerminalNodeLeftTuple( leftTuple, sink, leftTupleMemoryEnabled );
//
        //TODO FIXME
        if ( leftTuple instanceof ImperfectTuple ) {
            tuple.setEvaluation( ((ImperfectTuple) leftTuple ).getEvaluation( ) );
        }
//
        return tuple;
//        throw new UnsupportedOperationException( "Not impl yet" );
    }

    public LeftTuple createLeftTuple( LeftTuple leftTuple, RightTuple rightTuple, LeftTupleSink sink ) {
        ImperfectTuple tuple = new ImperfectLeftTuple( leftTuple, rightTuple, sink );
        Evaluation eval = reevaluate( (ImperfectTuple) leftTuple, (ImperfectTuple) rightTuple );
        tuple.setEvaluation( eval );
        return (LeftTuple) tuple;
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTuple currentLeftChild, LeftTuple currentRightChild, LeftTupleSink sink, boolean leftTupleMemoryEnabled) {
        ImperfectTuple tuple = new ImperfectLeftTuple( leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );

        //TODO FIXME
        if ( leftTuple instanceof ImperfectTuple) {
            tuple.setEvaluation( reevaluate( (ImperfectTuple) leftTuple, (ImperfectTuple) rightTuple ) );
        }

        return (LeftTuple) tuple;
    }


    private Evaluation reevaluate( ImperfectTuple left, ImperfectTuple right   ) {
        Degree[] bits = new Degree[arity];
        Evaluation[] children = new Evaluation[arity];

        for ( int j = 0; j < arity - 1; j ++ ) {
            //TODO FIXME NULL CHECKS
            Evaluation arg = left.getCachedEvaluation( this.argIndexes[j] );
            children[ j ] = arg != null ? arg : new MockEvaluation( -1, SimpleDegree.TRUE );
            bits[ j ] = arg != null ? arg.getDegree() : SimpleDegree.TRUE;
        }
        Evaluation rightEval = right.getCachedEvaluation( this.argIndexes[ arity - 1 ] );
        children[ arity - 1 ] = rightEval;
        bits[ arity - 1 ] = rightEval.getDegree();


        Degree res = this.connective.eval( bits );
        //        System.err.println( "LOP" + res );
        return new CompositeEvaluation( this.id,
                connective.toString() + Arrays.toString( children ),
                res,
                connective,
                children,
                label );
    }


}
