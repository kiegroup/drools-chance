package org.drools.chance.rule.constraint.core.evaluators;


import org.drools.chance.degree.Degree;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

public interface ImperfectEvaluator extends Evaluator {

    public Degree match( InternalWorkingMemory workingMemory, InternalReadAccessor extractor, InternalFactHandle object, FieldValue value );

    public Degree match( InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, InternalFactHandle left, InternalReadAccessor rightExtractor, InternalFactHandle right );

    public Degree matchCachedLeft( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, InternalFactHandle right );

    public Degree matchCachedRight( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, InternalFactHandle left );

}
