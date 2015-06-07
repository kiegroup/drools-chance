package org.drools.chance.evaluation;

import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.degree.Degree;

import java.util.Arrays;

public class CompositeEvaluation extends SimpleEvaluationImpl implements AggregateEvaluation {

    private Evaluation[] children;
    private int arity;
    private int numAvailableArguments;

    private ConnectiveCore operator;

    public CompositeEvaluation( int nodeId, Degree degree, ConnectiveCore op, Evaluation[] children ) {
        this( nodeId, null, degree, op, children, null );
    }

    public CompositeEvaluation( int nodeId, String expression, Degree degree, ConnectiveCore op, Evaluation[] children ) {
        this( nodeId, expression, degree, op, children, null );
    }
    
    public CompositeEvaluation( int nodeId, String expression, Degree degree, ConnectiveCore op, Evaluation[] children, String label ) {
        super( nodeId,
               expression,
               degree,
               label );
        this.operator = op;
        this.arity = children.length;
        setChildren( children );
        if ( arity == numAvailableArguments ) {
            setDegree( operator.eval( children ) );
        }
    }

    public Evaluation[] getChildren() {
        return children;
    }

    public Degree getDegree() {
        if ( degree == null ) {
            degree = operator.eval( children );
        }
        return degree;
    }


    public void setChildren( Evaluation[] children ) {
        this.children = children;
        for ( int j = 0; j < arity; j++ ) {
            if ( children[ j ] != null ) {
                children[ j ].setParent( this );
                numAvailableArguments++;
            }
        }
    }

    public void notifyChange( Evaluation child ) {
        if ( getNumAvailableArguments() != getArity() ) {
            return;
        }

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
    public void addOrReplaceArgument( Evaluation eval, boolean updateIfsameSource ) {
        //FIXME Can likely be optimized
        for ( int j = arity - 1; j >= 0; j-- ) {
            Evaluation child = children[ j ];
            if ( child == null || ( updateIfsameSource && child.getNodeId() == eval.getNodeId() ) ) {
                children[ j ] = eval;
                eval.setParent( this );
                if ( child == null ) {
                    numAvailableArguments++;
                }
                return;
            }
        }
    }

    @Override
    public void setBetaEvaluation( Evaluation eval ) {
        for ( int j = arity - 1; j >= 0; j-- ) {
            Evaluation child = children[ j ];
            if ( child == MockEvaluation.mock  ) {
                children[ j ] = eval;
                eval.setParent( this );
                numAvailableArguments++;
                return;
            }
        }
    }

    public void setArgumentPlaceHolder( int j, MockEvaluation mock ) {
        children[ j ] = mock;
        if ( mock == MockEvaluation.tru ) {
            numAvailableArguments++;
        }
    }


    @Override
    public String toString() {
        return  ( degree != null ? degree.getValue() : "n/a " ) +
                "@" + getNodeId() + " // CompositeEvaluation{" +
                "children=" + (children == null ? "[]" : Arrays.toString(children)) +
                "} " ;
    }


    public Evaluation merge( Evaluation other ) {
        setDegree( other.getDegree() );
        return this;
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

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public int getNumAvailableArguments() {
        return numAvailableArguments;
    }

    @Override
    public boolean hasArgument( Evaluation eval ) {
        for ( int j = 0; j < arity; j++ ) {
            if ( children[ j ] != null && children[ j ].getNodeId() == eval.getNodeId() ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAggregate() {
        return true;
    }

    @Override
    public boolean isBeta( int j ) {
        return children[ j ] == MockEvaluation.mock;
    }

    @Override
    public boolean needsArguments() {
        return numAvailableArguments < arity;
    }
}
