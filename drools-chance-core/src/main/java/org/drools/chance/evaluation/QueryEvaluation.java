package org.drools.chance.evaluation;


import org.drools.chance.degree.Degree;

public class QueryEvaluation implements Evaluation {
    
    private int nodeId;
    
    private Evaluation eval;
    
    private String label;    
    
    private AggregateEvaluation parent;

    private Evaluation next;
    
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

    @Override
    public boolean isAggregate() {
        return false;
    }

    @Override
    public boolean isOuter() {
        return false;
    }

    public Evaluation merge( Evaluation other ) {
        throw new UnsupportedOperationException("Should Query evals support merge?");
//        this.degree = other.getDegree();
    }

    @Override
    public Evaluation attach( Evaluation other ) {
        other.setNext( this );
        return other;
    }

    @Override
    public Evaluation getNext() {
        return next;
    }

    @Override
    public void setNext( Evaluation next ) {
        this.next = next;
    }

    @Override
    public String toString() {
        return getDegree().getValue() + "@" + nodeId + "// SimpleEvaluation{" +
                + nodeId + ") :[ " + eval.getExpression() + "] >> " + eval.getDegree();
    }
}
