package org.drools.chance.rule.constraint.core.evaluators;


import org.drools.chance.degree.Degree;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.constraint.MvelConditionEvaluator;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolverFactory;

import java.util.Map;

import static org.drools.core.rule.constraint.EvaluatorHelper.valuesAsMap;

public class ImperfectMvelConditionEvaluator extends MvelConditionEvaluator {
    
    public ImperfectMvelConditionEvaluator(ParserConfiguration configuration, String expression, Declaration[] declarations,String conditionClass) {
        super(configuration, expression, declarations,conditionClass);
    }

    public ImperfectMvelConditionEvaluator(MVELCompilationUnit compilationUnit, ParserConfiguration parserConfiguration, ExecutableStatement executableStatement, Declaration[] declarations, String conditionClass) {
        super(compilationUnit, parserConfiguration, executableStatement, declarations, conditionClass);
    }
    
    public Degree match(InternalFactHandle handle, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        return match(executableStatement, handle, workingMemory, leftTuple);
    }

    public Degree match(ExecutableStatement statement, InternalFactHandle handle, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        if (compilationUnit == null) {
            Map<String, Object> vars = valuesAsMap(handle.getObject(), workingMemory, leftTuple, declarations);
            return match(statement, handle, vars);
        }

        VariableResolverFactory factory = compilationUnit.createFactory();
        compilationUnit.updateFactory( null, null, handle,
                leftTuple, null, workingMemory,
                workingMemory.getGlobalResolver(),
                factory );

        InternalKnowledgePackage pkg = workingMemory.getKnowledgeBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return (Degree) MVEL.executeExpression(statement, handle.getObject(), factory);
    }

    private Degree match(ExecutableStatement statement, Object object, Map<String, Object> vars) {
        return vars == null ? (Degree)MVEL.executeExpression(statement, object) : (Degree)MVEL.executeExpression(statement, object, vars);
    }
    
}
