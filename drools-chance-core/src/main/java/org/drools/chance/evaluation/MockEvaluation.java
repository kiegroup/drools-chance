package org.drools.chance.evaluation;


import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;

public class MockEvaluation extends SimpleEvaluationImpl {

    public static final MockEvaluation tru = new MockEvaluation();

    public static final MockEvaluation mock = new MockEvaluation();

    @Override
    public Degree getDegree() {
        return ChanceDegreeTypeRegistry.getDefaultOne().True();
    }

    @Override
    public String toString() {
        return this == tru ? "[T]" : "[...]";
    }
}
