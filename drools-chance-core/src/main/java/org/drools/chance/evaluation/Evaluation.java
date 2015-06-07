package org.drools.chance.evaluation;


import org.drools.chance.degree.Degree;

import java.io.Serializable;

public interface Evaluation extends Serializable {
    
    public int getNodeId();
    
    public Degree getDegree();
    
    public String getExpression();
    
    public String getLabel();
    
    public Evaluation merge( Evaluation other );

    public Evaluation attach( Evaluation other );

    public AggregateEvaluation getParent();
    
    public void setParent( AggregateEvaluation parent );

    public Evaluation lookupLabel( String label );

    public boolean isAggregate();

    public boolean isOuter();

    public Evaluation getNext();

    public void setNext( Evaluation next );

}
