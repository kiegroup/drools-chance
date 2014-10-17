package org.drools.chance.reteoo.nodes;

import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.CompositeEvaluation;
import org.drools.chance.evaluation.DelayedEvaluationImpl;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.*;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;


//TODO do not extend AlphaNode!
public class LogicalAplhaOperatorNode extends ChanceAlphaNode
        implements
        ObjectSinkNode {

    private ObjectSinkNode           previousRightTupleSinkNode;
    private ObjectSinkNode           nextRightTupleSinkNode;

    private ConnectiveCore           connective;
    private int                      arity;
    private int[]                    argIndexes;

    private String label;


    public LogicalAplhaOperatorNode( int id, String label, ConnectiveCore conn, int[] argIndexes, int arity, ObjectSource objectSource, BuildContext context ) {
        super( id, null, objectSource, context );
        this.connective = conn;
        this.arity = arity;
        this.argIndexes = argIndexes;
        this.label = label;
    }


    public int getArity() {
        return arity;
    }

    public ConnectiveCore getConnective() {
        return connective;
    }

    public short getType() {
        return NodeTypeEnums.OperatorNode;
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

    /*
    * (non-Javadoc)
    *
    * @see org.drools.reteoo.BaseNode#attach()
    */
    public void attach() {
        this.source.addObjectSink( this );
    }

    public void attach( final InternalWorkingMemory[] workingMemories ) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                    PropagationContext.RULE_ADDITION,
                    null,
                    null,
                    null );
            this.source.updateSink( this,
                    propagationContext,
                    workingMemory );
        }
    }

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        ChanceFactHandle chandle = (ChanceFactHandle) factHandle;
        Evaluation agg = reevaluate(chandle);
        chandle.addEvaluation( this.id, agg );

        boolean canPropagate = agg.getDegree().toBoolean();
        if ( canPropagate ) {
            this.sink.propagateAssertObject( factHandle,
                    context,
                    workingMemory );
        }
    }

    public void modifyObject(final InternalFactHandle factHandle,
                             final ModifyPreviousTuples modifyPreviousTuples,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        ChanceFactHandle chandle = (ChanceFactHandle) factHandle;
        Evaluation agg = reevaluate( chandle );
        chandle.addEvaluation( this.id, agg );

        if ( agg.getDegree().toBoolean() ) {
            this.sink.propagateModifyObject(factHandle,
                    modifyPreviousTuples,
                    context,
                    workingMemory);
        }

    }


    private Evaluation reevaluate( ChanceFactHandle chandle ) {
        Degree[] bits = new Degree[arity];
        Evaluation[] children = new Evaluation[arity];

        for ( int j = 0; j < arity; j ++ ) {
            Evaluation arg = chandle.getCachedEvaluation( this.argIndexes[j] );
            children[ j ] = arg != null ? arg : new DelayedEvaluationImpl( this.argIndexes[j] );
            bits[ j ] = arg != null ? arg.getDegree() : SimpleDegree.TRUE;
        }

        Degree res = this.connective.eval( bits );
//        System.err.println( "LOP" + res );
        return new CompositeEvaluation( this.id,
                connective.toString() + Arrays.toString( children ),
                res,
                connective,
                children,
                label );
    }


    public void byPassModifyToBetaNode ( final InternalFactHandle factHandle,
                                         final ModifyPreviousTuples modifyPreviousTuples,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory) {
        sink.byPassModifyToBetaNode( factHandle, modifyPreviousTuples, context, workingMemory );
    }


    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        this.source.updateSink( sink,
                context,
                workingMemory );
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

        LogicalAplhaOperatorNode that = (LogicalAplhaOperatorNode) o;

        if (connective != that.connective) return false;
        if (arity != that.arity) return false;

        return true;
    }

    /**
     * Returns the next node
     * @return
     *      The next ObjectSinkNode
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextRightTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next ObjectSinkNode
     */
    public void setNextObjectSinkNode(final ObjectSinkNode next) {
        this.nextRightTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous ObjectSinkNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousRightTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous ObjectSinkNode
     */
    public void setPreviousObjectSinkNode(final ObjectSinkNode previous) {
        this.previousRightTupleSinkNode = previous;
    }





    public long calculateDeclaredMask(List<String> settableProperties) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getDeclaredMask() {
        return declaredMask;
    }

    @Override
    public void addObjectSink(final ObjectSink objectSink) {
        super.addObjectSink(objectSink);
    }

}
