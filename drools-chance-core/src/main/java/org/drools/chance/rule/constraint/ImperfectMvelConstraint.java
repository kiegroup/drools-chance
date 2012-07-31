package org.drools.chance.rule.constraint;


import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.chance.rule.constraint.core.evaluators.ImperfectMvelConditionEvaluator;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.index.IndexUtil;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.constraint.MvelConditionEvaluator;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableStatement;

public class ImperfectMvelConstraint extends MvelConstraint implements ImperfectAlphaConstraint, ImperfectBetaConstraint {

    private int     refNodeId              = -1;
    private boolean wrapsPerfectConstraint = false;

    private String label;

    private boolean cutting;

    public boolean isCutting() {
        return cutting;
    }

    public void setCutting(boolean cutting) {
        this.cutting = cutting;
    }

    public ImperfectMvelConstraint() {
    }

    public ImperfectMvelConstraint(String packageName, String expression, MVELCompilationUnit compilationUnit, IndexUtil.ConstraintType constraintType, FieldValue fieldValue, InternalReadAccessor extractor) {
        super(packageName, expression, compilationUnit, constraintType, fieldValue, extractor);
    }

    public ImperfectMvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, boolean isDynamic) {
        super(packageName, expression, declarations, compilationUnit, isDynamic);
    }

    public ImperfectMvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, IndexUtil.ConstraintType constraintType, Declaration indexingDeclaration, InternalReadAccessor extractor, boolean isUnification) {
        super(packageName, expression, declarations, compilationUnit, constraintType, indexingDeclaration, extractor, isUnification);
    }


    public Degree match( InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        return match( handle.getObject(), workingMemory, null );
    }


    protected Degree match(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        prepare( object, workingMemory, leftTuple );
        if ( ! wrapsPerfectConstraint ) {
            return ((ImperfectMvelConditionEvaluator) conditionEvaluator).match(object, workingMemory, leftTuple);
        } else {
            return conditionEvaluator.evaluate(object, workingMemory, leftTuple) ? SimpleDegree.TRUE : SimpleDegree.FALSE;
        }
    }

    private void prepare(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        if ( ! jitted ) {
            if ( conditionEvaluator == null) {
                createImperfectMvelConditionEvaluator(workingMemory);
                if ( TEST_JITTING && !isDynamic ) { // Only for test purposes
                    boolean mvelValue = forceJitEvaluator( object, workingMemory, leftTuple );
                }
            }

            if ( ! isDynamic && invocationCounter.getAndIncrement() == JIT_THRESOLD ) {
                jitEvaluator(object, workingMemory, leftTuple);
            }
        }

    }

    protected void createImperfectMvelConditionEvaluator(InternalWorkingMemory workingMemory) {
        if (compilationUnit != null) {
            MVELDialectRuntimeData data = getMVELDialectRuntimeData(workingMemory);
            ExecutableStatement statement = (ExecutableStatement) compilationUnit.getCompiledExpression( data );
            ParserContext context = statement instanceof CompiledExpression ?
                    ((CompiledExpression)statement).getParserContext() :
                    new ParserContext(data.getParserConfiguration());
            if ( statement.getKnownEgressType().isAssignableFrom( Degree.class ) ) {
                conditionEvaluator = new ImperfectMvelConditionEvaluator( compilationUnit, context, statement, getRequiredDeclarations() );
            } else {
                conditionEvaluator = new MvelConditionEvaluator( compilationUnit, context, statement, getRequiredDeclarations() );
                wrapsPerfectConstraint = true;
            }
        } else {
            conditionEvaluator = new ImperfectMvelConditionEvaluator( getParserConfiguration(workingMemory), expression, getRequiredDeclarations() );
        }
    }

    public Degree matchCachedLeft( ContextEntry context, InternalFactHandle handle ) {
        Object o = handle.getObject();
        LeftTuple tup = ((MvelContextEntry) context).getLeftTuple();
        InternalWorkingMemory wm = ((MvelContextEntry) context).getWorkingMemory();

        prepare( o, wm, tup );
        if ( ! wrapsPerfectConstraint ) {
            return ((ImperfectMvelConditionEvaluator) conditionEvaluator).match( o, wm, tup );
        } else {
            return conditionEvaluator.evaluate( o, wm, tup ) ? SimpleDegree.TRUE : SimpleDegree.FALSE;
        }
    }

    public Degree matchCachedRight( LeftTuple tuple, ContextEntry context ) {
        Object o = tuple.getHandle().getObject();
        LeftTuple tup = ((MvelContextEntry) context).getLeftTuple();
        InternalWorkingMemory wm = ((MvelContextEntry) context).getWorkingMemory();

        prepare( o, wm, tup );
        if ( ! wrapsPerfectConstraint ) {
            return ((ImperfectMvelConditionEvaluator) conditionEvaluator).match( o, wm, tup );
        } else {
            return conditionEvaluator.evaluate( o, wm, tup ) ? SimpleDegree.TRUE : SimpleDegree.FALSE;
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

    public void setLabel(String label) {
        this.label = label;
    }
}
