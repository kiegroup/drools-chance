package org.drools.chance.reteoo.nodes;

import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.SimpleEvaluationImpl;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.rule.constraint.core.evaluators.IsAEvaluatorDefinition;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.factmodel.traits.Trait;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ModifyPreviousTuples;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.TraitObjectTypeNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;


public class ChanceTraitObjectTypeNode extends TraitObjectTypeNode {

    private boolean imperfect = false;

    private IsAEvaluatorDefinition.ImperfectIsAEvaluator isA;

    public ChanceTraitObjectTypeNode( int id, EntryPointNode source, ObjectType objectType, BuildContext context ) {
        super( id, source, objectType, context );
    }

    public boolean isImperfect() {
        return imperfect;
    }

    public void setImperfect(boolean imperfect) {
        this.imperfect = imperfect;
        if ( imperfect == true && isA == null ) {
            isA = new IsAEvaluatorDefinition.ImperfectIsAEvaluator( ValueType.OBJECT_TYPE, false );
        }
    }


    protected Degree match( InternalWorkingMemory wm, Object object ) {
        if ( imperfect ) {
            return isA.match( wm, object, ((ClassObjectType) getObjectType()).getClassType().getName() );
        } else {
            return SimpleDegree.TRUE;
        }
    }


    @Override
    public void assertObject( InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory ) {
        Class<?> klass = ((ClassObjectType) this.getObjectType()).getClassType();
        ((ChanceFactHandle) factHandle).addEvaluation( this.getId(),
                                                       new SimpleEvaluationImpl( this.getId(),
                                                                                 "this isA " + klass.getName(),
                                                                                 match( workingMemory, factHandle.getObject() ),
                                                                                 klass.getSimpleName() ) );
        super.assertObject(factHandle, context, workingMemory);
    }

    @Override
    public void modifyObject( InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory ) {
        Class<?> klass = ((ClassObjectType) this.getObjectType()).getClassType();
        ((ChanceFactHandle) factHandle).addEvaluation( this.getId(),
                new SimpleEvaluationImpl( this.getId(),
                        "this isA " + klass.getName(),
                        match( workingMemory, factHandle.getObject() ),
                        klass.getSimpleName() ) );
        super.modifyObject( factHandle, modifyPreviousTuples, context, workingMemory );    //To change body of overridden methods use File | Settings | File Templates.
    }
}

