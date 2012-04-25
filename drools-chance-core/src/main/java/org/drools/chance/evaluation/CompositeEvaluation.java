package org.drools.chance.evaluation;

import org.drools.chance.constraints.core.connectives.ConnectiveCore;
import org.drools.chance.degree.Degree;

import java.util.Arrays;

public class CompositeEvaluation extends SimpleEvaluationImpl implements AggregateEvaluation {

    private Evaluation[] children;

    private ConnectiveCore operator;

    public CompositeEvaluation( int nodeId, Degree degree, ConnectiveCore op, Evaluation[] children ) {
        super( nodeId, degree );
        this.operator = op;
        setChildren( children );
    }

    public CompositeEvaluation( int nodeId, String expression, Degree degree, ConnectiveCore op, Evaluation[] children ) {
        super( nodeId, expression, degree );
        this.operator = op;
        setChildren( children );
    }
    
    public CompositeEvaluation( int nodeId, String expression, Degree degree, ConnectiveCore op, Evaluation[] children, String label ) {
        super( nodeId, expression, degree, label );
        this.operator = op;
        setChildren( children );
    }

    public Evaluation[] getChildren() {
        return children;
    }

    public void setChildren(Evaluation[] children) {
        this.children = children;
        for ( Evaluation eval : children ) {
            eval.setParent( this );
        }
    }

    public void notifyChange( Evaluation child ) {
        Degree old = getDegree();
        Degree nuu = operator.eval( children );

        if ( ! nuu.equals( old ) ) {
            setDegree( nuu );
            if ( getParent() != null ) {
                getParent().notifyChange( this );
            }
        }
    }

    @Override
    public String toString() {
        return getDegree().getValue() + "@" + getNodeId() + " // CompositeEvaluation{" +
                "children=" + (children == null ? null : Arrays.asList(children)) +
                "} " + super.toString();
    }


    public void merge( Evaluation other ) {
        setDegree(other.getDegree());
    }

    @Override
    public Evaluation lookupLabel( String label ) {
        Evaluation eval = super.lookupLabel( label );
        if ( eval != null ) {
            return eval;
        } else {
            for ( Evaluation child : children ) {
                eval = child.lookupLabel( label );
                if ( eval != null ) {
                    return eval;
                }
            }
            return null;
        }
    }
}
