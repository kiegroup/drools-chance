package org.drools.chance.reteoo.nodes;


import org.drools.chance.rule.constraint.ImperfectAlphaConstraint;
import org.drools.chance.rule.constraint.ImperfectConstraint;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.SimpleEvaluationImpl;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.ModifyPreviousTuples;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;

import static org.drools.core.util.BitMaskUtil.intersect;

public class ChanceAlphaNode extends AlphaNode {

    private boolean alwaysPropagate = true;

    public ChanceAlphaNode() {
    }

    public ChanceAlphaNode(int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context) {
        super(id, constraint, objectSource, context);
        if ( constraint instanceof ImperfectConstraint && ((ImperfectConstraint) constraint).isCutting() ) {
            alwaysPropagate = false;
        }
    }

    public boolean isAlwaysPropagate() {
        return alwaysPropagate;
    }

    public void setAlwaysPropagate( boolean alwaysPropagate ) {
        this.alwaysPropagate = alwaysPropagate;
    }

    public void assertObject( final InternalFactHandle factHandle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory ) {
        final AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );

        boolean canPropagate = false;
        AlphaNodeFieldConstraint constraint = getConstraint();

        if ( constraint instanceof ImperfectConstraint ) {

            ImperfectAlphaConstraint alpha = (ImperfectAlphaConstraint) getConstraint();
            Degree degree = alpha.match( factHandle,
                    workingMemory,
                    memory.context );

            ((ChanceFactHandle) factHandle).addEvaluation( this.getId(), new SimpleEvaluationImpl( getId(), constraint.toString(), degree, alpha.getLabel() ) );
            canPropagate = degree.toBoolean();

        } else {


            canPropagate = constraint.isAllowed( factHandle, workingMemory, memory.context );
            ((ChanceFactHandle) factHandle).addEvaluation( this.getId(), new SimpleEvaluationImpl( getId(), constraint.toString(), SimpleDegree.fromBooleanLiteral( canPropagate ) ) );

        }

        if ( alwaysPropagate || canPropagate ) {
            this.sink.propagateAssertObject( factHandle,
                    context,
                    workingMemory );
        }


    }

    public void modifyObject( final InternalFactHandle factHandle,
                              final ModifyPreviousTuples modifyPreviousTuples,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory ) {
        if ( intersect( context.getModificationMask(), inferredMask ) ) {

            final AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );
            AlphaNodeFieldConstraint constraint = getConstraint();
            boolean canPropagate = false;

            if ( constraint instanceof ImperfectConstraint) {
                ImperfectAlphaConstraint alpha = (ImperfectAlphaConstraint) getConstraint();
                Degree degree = alpha.match( factHandle,
                        workingMemory,
                        memory.context );

                ((ChanceFactHandle) factHandle).addEvaluation( this.getId(), new SimpleEvaluationImpl( getId(), alpha.getLabel(), degree ) );
                canPropagate = degree.toBoolean();
            } else {
                canPropagate = constraint.isAllowed( factHandle, workingMemory, memory.context );
                ((ChanceFactHandle) factHandle).addEvaluation( this.getId(), new SimpleEvaluationImpl( getId(), constraint.toString(), SimpleDegree.fromBooleanLiteral( canPropagate ) ) );
            }

            if ( canPropagate ) {
                this.sink.propagateModifyObject( factHandle,
                        modifyPreviousTuples,
                        context,
                        workingMemory );
            }
        } else {
            byPassModifyToBetaNode( factHandle, modifyPreviousTuples,context,workingMemory );
        }
    }

}
