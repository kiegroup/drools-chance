package org.drools.chance.reteoo.nodes;

import org.drools.chance.rule.constraint.ImperfectBetaConstraint;
import org.drools.chance.evaluation.DelayedEvaluationImpl;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class DelayedEvaluationNode extends ChanceAlphaNode {


    public DelayedEvaluationNode( int id, Constraint constraint, ObjectSource objectSource, BuildContext context ) {
        super( id, new AlphaReferenceToBetaConstraint( (BetaNodeFieldConstraint) constraint ), objectSource, context );

        if ( constraint instanceof ImperfectBetaConstraint ) {
            ((ImperfectBetaConstraint) constraint).setNodeId( id );
        }

    }


    @Override
    public void assertObject(InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory) {

        ((ChanceFactHandle) factHandle).addEvaluation( this.getId(), new DelayedEvaluationImpl( getId() ) );

        this.sink.propagateAssertObject( factHandle,
                context,
                workingMemory );
    }


    private static class AlphaReferenceToBetaConstraint implements AlphaNodeFieldConstraint {

        private BetaNodeFieldConstraint beta;

        public AlphaReferenceToBetaConstraint( BetaNodeFieldConstraint b ) {
            this.beta = b;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( beta );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            beta = (BetaNodeFieldConstraint) in.readObject();
        }

        public Declaration[] getRequiredDeclarations() {
            return new Declaration[0];
        }

        public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {

        }

        public Constraint clone() {
            return new AlphaReferenceToBetaConstraint( beta );
        }

        public ConstraintType getType() {
            return ConstraintType.ALPHA;
        }

        public boolean isTemporal() {
            return false;
        }

        public ContextEntry createContextEntry() {
            return null;
        }

        public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context) {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AlphaReferenceToBetaConstraint that = (AlphaReferenceToBetaConstraint) o;

            if (beta != null ? !beta.equals(that.beta) : that.beta != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return beta != null ? beta.hashCode() : 0;
        }
    }
}
