package org.drools.chance.evaluation;


import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;

public class SimpleEvaluationImpl implements SimpleEvaluation {

    private int nodeId;

    private String expression;

    protected Degree degree;
    
    private AggregateEvaluation parent;

    private String label;

    private Evaluation next;

    protected SimpleEvaluationImpl() { };

    public SimpleEvaluationImpl( int nodeId, Degree degree ) {
        this( nodeId, null, degree, null );
    }


    public SimpleEvaluationImpl( int nodeId, String expression, Degree degree ) {
        this( nodeId, expression, degree, null );
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

    @Override
    public boolean isAggregate() {
        return false;
    }

    public Evaluation merge( Evaluation other ) {
        if ( this.degree == null ) {
            this.degree = other.getDegree();
        } else {
            throw new UnsupportedOperationException( "TODO" );
        }
        return this;
    }

    public Evaluation attach( Evaluation other ) {
        other.setNext( this );
        return other;
    }

    public void setExpression( String expression ) {
        this.expression = expression;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
    public boolean isOuter() {
        return false;
    }

    @Override
    public String toString() {
        return getDegree().getValue() + "@" + nodeId + "// SimpleEvaluation{" +
                + nodeId + ") :[ " + expression + "] >> " + degree;
    }
}
