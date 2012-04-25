package org.drools.chance.constraints.core.evaluators;


import org.drools.chance.degree.Degree;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

public interface ImperfectEvaluator extends Evaluator {

    public Degree match( InternalWorkingMemory workingMemory, InternalReadAccessor extractor, Object object, FieldValue value );

    public Degree match( InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, Object left, InternalReadAccessor rightExtractor, Object right );

    public Degree matchCachedLeft( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, Object right );

    public Degree matchCachedRight( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, Object left );

}
