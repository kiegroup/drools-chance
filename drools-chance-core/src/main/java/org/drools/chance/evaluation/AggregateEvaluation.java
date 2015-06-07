package org.drools.chance.evaluation;


public interface AggregateEvaluation extends Evaluation {

    public void notifyChange( Evaluation child );

    public void addOrReplaceArgument( Evaluation eval, boolean updateIfSameSource );

    public int getArity();

    public int getNumAvailableArguments();

    public boolean hasArgument( Evaluation eval );

    boolean isBeta( int j );

    void setBetaEvaluation( Evaluation eval );

    boolean needsArguments();

}
