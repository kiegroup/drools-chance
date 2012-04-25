package org.drools.chance.rule.constraint;


import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.chance.rule.constraint.core.evaluators.ImperfectMvelConditionEvaluator;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
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

    public ImperfectMvelConstraint() {
    }

    public ImperfectMvelConstraint(String packageName, String expression, MVELCompilationUnit compilationUnit, boolean isIndexable, FieldValue fieldValue, InternalReadAccessor extractor) {
        super(packageName, expression, compilationUnit, isIndexable, fieldValue, extractor);
    }

    public ImperfectMvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, boolean isDynamic) {
        super(packageName, expression, declarations, compilationUnit, isDynamic);
    }

    public ImperfectMvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, boolean isIndexable, Declaration indexingDeclaration, InternalReadAccessor extractor, boolean isUnification) {
        super(packageName, expression, declarations, compilationUnit, isIndexable, indexingDeclaration, extractor, isUnification);
    }


    public Degree match( InternalFactHandle handle, InternalWorkingMemory workingMemory, ContextEntry context ) {
        return match( handle.getObject(), workingMemory, null );
    }


    protected Degree match(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
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
        if ( ! wrapsPerfectConstraint ) {
            return ((ImperfectMvelConditionEvaluator) conditionEvaluator).match(object, workingMemory, leftTuple);
        } else {
            return conditionEvaluator.evaluate(object, workingMemory, leftTuple) ? SimpleDegree.TRUE : SimpleDegree.FALSE;
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
                conditionEvaluator = new ImperfectMvelConditionEvaluator( compilationUnit, context, statement, declarations );
            } else {
                conditionEvaluator = new MvelConditionEvaluator( compilationUnit, context, statement, declarations );
                wrapsPerfectConstraint = true;
            }
        } else {
            conditionEvaluator = new ImperfectMvelConditionEvaluator( getParserConfiguration(workingMemory), expression, declarations );
        }
    }

    public Degree matchCachedLeft( ContextEntry context, InternalFactHandle handle ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Degree matchCachedRight( LeftTuple context, ContextEntry tuple ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
