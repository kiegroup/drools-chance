package org.drools.chance.evaluation;


import org.drools.chance.degree.Degree;

import java.io.Serializable;

public interface Evaluation extends Serializable {
    
    public int getNodeId();
    
    public Degree getDegree();
    
    public String getExpression();
    
    public String getLabel();
    
    public void merge( Evaluation other );
    
    public AggregateEvaluation getParent();
    
    public void setParent( AggregateEvaluation parent );

    public Evaluation lookupLabel( String label );

}
