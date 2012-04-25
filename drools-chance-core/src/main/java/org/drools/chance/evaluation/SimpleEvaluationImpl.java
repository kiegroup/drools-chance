package org.drools.chance.evaluation;


import org.drools.chance.degree.Degree;

public class SimpleEvaluationImpl implements SimpleEvaluation {

    private int nodeId;

    private String expression;

    private Degree degree;
    
    private AggregateEvaluation parent;

    private String label;
    

    protected SimpleEvaluationImpl() { };

    public SimpleEvaluationImpl( int nodeId, Degree degree ) {
        this.degree = degree;
        this.nodeId = nodeId;
    }


    public SimpleEvaluationImpl( int nodeId, String expression, Degree degree ) {
        this.degree = degree;
        this.expression = expression;
        this.nodeId = nodeId;
    }
    
    public SimpleEvaluationImpl( int nodeId, String expression, Degree degree, String label ) {
        this.degree = degree;
        this.expression = expression;
        this.nodeId = nodeId;
        this.label = label;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getExpression() {
        return expression;
    }

    public AggregateEvaluation getParent() {
        return parent;
    }

    public void setParent( AggregateEvaluation parent ) {
        this.parent = parent;
    }

    public Evaluation lookupLabel( String label ) {
        if( this.label != null && this.label.equals( label ) ) {
            return this;
        }
        return null;
    }

    public void merge( Evaluation other ) {
//        System.err.println( "Simple Evaluation being merged " + this + " with " + other );
        this.degree = other.getDegree();                
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return getDegree().getValue() + "@" + nodeId + "// SimpleEvaluation{" +
                + nodeId + ") :[ " + expression + "] >> " + degree;
    }
}
