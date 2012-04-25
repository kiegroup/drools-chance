package org.drools.chance.evaluation;


import org.drools.chance.degree.Degree;

public class QueryEvaluation implements Evaluation {
    
    private int nodeId;
    
    private Evaluation eval;
    
    private String label;    
    
    private AggregateEvaluation parent;
    
    public QueryEvaluation( int id, Evaluation eval, String label ) {
        this.nodeId = id;
        this.eval = eval;
        this.label = label;
    }

    public int getNodeId() {
        return nodeId;
    }

    public Degree getDegree() {
        return eval.getDegree();
    }

    public String getExpression() {
        return label;
    }

    public String getLabel() {
        return label;
    }

    public AggregateEvaluation getParent() {
        return parent;
    }

    public void setParent(AggregateEvaluation parent) {
        this.parent = parent;
    }

    public Evaluation lookupLabel( String label ) {
        if( this.label != null && this.label.equals( label ) ) {
            return this;
        }
        return null;
    }

    public void merge( Evaluation other ) {
        throw new UnsupportedOperationException("Should Query evals support merge?");
//        this.degree = other.getDegree();
    }

    @Override
    public String toString() {
        return getDegree().getValue() + "@" + nodeId + "// SimpleEvaluation{" +
                + nodeId + ") :[ " + eval.getExpression() + "] >> " + eval.getDegree();
    }
}
