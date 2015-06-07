package org.drools.chance.rule.constraint;


import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.evaluation.SimpleEvaluationImpl;
import org.drools.chance.reteoo.ChanceFactHandle;
import org.drools.chance.rule.constraint.core.evaluators.ImperfectMvelConditionEvaluator;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.constraint.MvelConditionEvaluator;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.index.IndexUtil;
import org.mvel2.ParserConfiguration;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableStatement;

import java.util.List;
import java.util.UUID;

public class ImperfectMvelConstraint extends MvelConstraint
        implements ImperfectAlphaConstraint, ImperfectBetaConstraint {

    private int     refNodeId              = -1;
    private boolean wrapsPerfectConstraint = false;

    private String label;

    private boolean cutting;

    public boolean isCutting() {
        return cutting;
    }

    public void setCutting( boolean cutting ) {
        this.cutting = cutting;
    }

    public ImperfectMvelConstraint() {
    }

    public ImperfectMvelConstraint( String packageName,
                                    String expression,
                                    MVELCompilationUnit compilationUnit,
                                    IndexUtil.ConstraintType constraintType,
                                    FieldValue fieldValue,
                                    InternalReadAccessor extractor,
                                    String label ) {
        super( packageName, expression, compilationUnit, constraintType, fieldValue, extractor );
        setLabel( label );
    }

    public ImperfectMvelConstraint( String packageName,
                                    String expression,
                                    Declaration[] declarations,
                                    MVELCompilationUnit compilationUnit,
                                    boolean isDynamic,
                                    String label ) {
        super( packageName, expression, declarations, compilationUnit, isDynamic );
        setLabel( label );
    }

    public ImperfectMvelConstraint( List<String> packageNames,
                                    String expression,
                                    Declaration[] declarations,
                                    MVELCompilationUnit compilationUnit,
                                    IndexUtil.ConstraintType constraintType,
                                    Declaration indexingDeclaration,
                                    InternalReadAccessor extractor,
                                    boolean isUnification,
                                    String label ) {
        super( packageNames, expression, declarations, compilationUnit, constraintType, indexingDeclaration, extractor, isUnification );
        setLabel( label );
    }


    public Degree match( InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        return match( handle, workingMemory, context );
    }


    protected Degree match(InternalFactHandle handle, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        prepare( handle, workingMemory, leftTuple );
        if ( ! wrapsPerfectConstraint ) {
            Degree deg = ((ImperfectMvelConditionEvaluator) conditionEvaluator).match(handle, workingMemory, leftTuple);
            (( ChanceFactHandle ) handle).addEvaluation( getOwningNodeId(),
                                                         new SimpleEvaluationImpl( getOwningNodeId(), deg ) );
            return deg;
        } else {
            return conditionEvaluator.evaluate(handle, workingMemory, leftTuple) ? SimpleDegree.TRUE : SimpleDegree.FALSE;
        }
    }

    private void prepare(InternalFactHandle handle, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        if ( ! jitted ) {
            if ( conditionEvaluator == null) {
                createImperfectMvelConditionEvaluator(workingMemory);
                if ( TEST_JITTING && !isDynamic ) { // Only for test purposes
                    boolean mvelValue = forceJitEvaluator( handle, workingMemory, leftTuple );
                }
            }

            if ( ! isDynamic && invocationCounter.getAndIncrement() == JIT_THRESOLD ) {
                jitEvaluator(handle, workingMemory, leftTuple);
            }
        }

    }

    protected void createImperfectMvelConditionEvaluator(InternalWorkingMemory workingMemory) {
        if (compilationUnit != null) {
            MVELDialectRuntimeData data = getMVELDialectRuntimeData(workingMemory);
            ExecutableStatement statement = (ExecutableStatement) compilationUnit.getCompiledExpression( data );
            ParserConfiguration configuration = statement instanceof CompiledExpression ?
                                ((CompiledExpression)statement).getParserConfiguration() :
                                data.getParserConfiguration();
            if ( statement.getKnownEgressType().isAssignableFrom( Degree.class ) ) {
                conditionEvaluator = new ImperfectMvelConditionEvaluator( compilationUnit, configuration, statement, getRequiredDeclarations(), getAccessedClass());
            } else {
                conditionEvaluator = new MvelConditionEvaluator( compilationUnit, configuration, statement, getRequiredDeclarations(), getAccessedClass() );
                wrapsPerfectConstraint = true;
            }
        } else {
            conditionEvaluator = new ImperfectMvelConditionEvaluator( getParserConfiguration(workingMemory), expression, getRequiredDeclarations(), getAccessedClass() );
        }
    }

    public Degree matchCachedLeft( ContextEntry context, InternalFactHandle handle ) {
        LeftTuple tup = ((MvelContextEntry) context).getLeftTuple();
        InternalWorkingMemory wm = ((MvelContextEntry) context).getWorkingMemory();

        prepare( handle, wm, tup );
        if ( ! wrapsPerfectConstraint ) {
            Degree deg = ((ImperfectMvelConditionEvaluator) conditionEvaluator).match( handle, wm, tup );
            (( ChanceFactHandle ) handle).addEvaluation( getOwningNodeId(),
                                                         new SimpleEvaluationImpl( getOwningNodeId(), deg ) );
            return deg;
        } else {
            return conditionEvaluator.evaluate( handle, wm, tup ) ? SimpleDegree.TRUE : SimpleDegree.FALSE;
        }
    }

    public Degree matchCachedRight( LeftTuple tuple, ContextEntry context ) {
        InternalFactHandle handle = tuple.getHandle();
        LeftTuple tup = ((MvelContextEntry) context).getLeftTuple();
        InternalWorkingMemory wm = ((MvelContextEntry) context).getWorkingMemory();

        prepare( handle, wm, tup );
        if ( ! wrapsPerfectConstraint ) {
            Degree deg = ((ImperfectMvelConditionEvaluator) conditionEvaluator).match( handle, wm, tup );
            (( ChanceFactHandle ) handle).addEvaluation( getOwningNodeId(),
                                                         new SimpleEvaluationImpl( getOwningNodeId(), deg ) );
            return deg;
        } else {
            return conditionEvaluator.evaluate( handle, wm, tup ) ? SimpleDegree.TRUE : SimpleDegree.FALSE;
        }
    }

    public int getNodeId() {
        return refNodeId;
    }

    public void setNodeId( int id ) {
        refNodeId = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label != null ? label : UUID.randomUUID().toString();
    }


}
