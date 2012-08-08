package org.drools.chance.rule.constraint.core.evaluators;


import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.chance.degree.Degree;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.constraint.MvelConditionEvaluator;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolverFactory;

import java.util.Map;

import static org.drools.rule.constraint.EvaluatorHelper.valuesAsMap;

public class ImperfectMvelConditionEvaluator extends MvelConditionEvaluator {
    
    public ImperfectMvelConditionEvaluator(ParserConfiguration configuration, String expression, Declaration[] declarations) {
        super(configuration, expression, declarations);
    }

    public ImperfectMvelConditionEvaluator(MVELCompilationUnit compilationUnit, ParserConfiguration parserConfiguration, ExecutableStatement executableStatement, Declaration[] declarations) {
        super(compilationUnit, parserConfiguration, executableStatement, declarations);
    }
    
    public Degree match(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        return match(executableStatement, object, workingMemory, leftTuple);
    }

    public Degree match(ExecutableStatement statement, Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        if (compilationUnit == null) {
            Map<String, Object> vars = valuesAsMap(object, workingMemory, leftTuple, declarations);
            return match(statement, object, vars);
        }

        VariableResolverFactory factory = compilationUnit.createFactory();
        compilationUnit.updateFactory( null, null, object,
                leftTuple, null, workingMemory,
                workingMemory.getGlobalResolver(),
                factory );

        org.drools.rule.Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return (Degree) MVEL.executeExpression(statement, object, factory);
    }

    private Degree match(ExecutableStatement statement, Object object, Map<String, Object> vars) {
        return vars == null ? (Degree)MVEL.executeExpression(statement, object) : (Degree)MVEL.executeExpression(statement, object, vars);
    }
    
}
