package org.drools.chance.reteoo;

import org.drools.chance.ChanceHelper;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.AggregateEvaluation;
import org.drools.chance.evaluation.CompositeEvaluation;
import org.drools.chance.evaluation.Evaluation;
import org.drools.chance.evaluation.MockEvaluation;
import org.drools.chance.evaluation.OuterOperatorEvaluation;
import org.drools.chance.evaluation.SimpleEvaluationImpl;
import org.drools.chance.factmodel.Imperfect;
import org.drools.chance.rule.constraint.OuterOperatorConstraint;
import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DefaultKnowledgeHelper;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ChanceKnowledgeHelper extends DefaultKnowledgeHelper implements ChanceHelper {

    public ChanceKnowledgeHelper( WorkingMemory workingMemory ) {
        super( workingMemory );
    }

    public Degree getDegree() {
        return getEvaluation().getDegree();
    }

    public Degree getDegree( String label ) {
        if ( label == null || label.isEmpty() ) {
            return getDegree();
        }
        return getEvaluation( label ).getDegree();
    }

    public Evaluation getEvaluation() {
        int rtnId = getMatch().getTuple().getSink().getId();
        RuleTerminalNodeLeftTuple leftTuple = (RuleTerminalNodeLeftTuple) getMatch().getTuple();
        List<FactHandle> handles = leftTuple.getFactHandles();

        Evaluation eval = combineLeftDegrees( leftTuple );

        for ( FactHandle handle : handles ) {
            if ( handle instanceof ChanceFactHandle ) {
                ChanceFactHandle impHandle = (ChanceFactHandle) handle;
                impHandle.setEvaluation( rtnId, eval );
            }
        }

        return eval;
    }

    private Evaluation combineLeftDegrees( RuleTerminalNodeLeftTuple leftTuple ) {
        LeftTupleSink tip = leftTuple.getSink();
        BaseNode leftSrc = tip.getLeftTupleSource();

        LeftTuple lt = leftTuple;
        ChanceFactHandle handle;
        List<Evaluation> args = new LinkedList<Evaluation>();
        List<OuterOperatorEvaluation> outers = new LinkedList<OuterOperatorEvaluation>();
        do {
            int nodeId;
            BaseNode rightSource;
            if ( NodeTypeEnums.isBetaNode( leftSrc ) ) {
                BetaNode beta = (BetaNode) leftSrc;
                nodeId = beta.getId();
                rightSource = beta.getRightInput();
                handle = (ChanceFactHandle) lt.getHandle();
            } else if ( NodeTypeEnums.isLIANode( leftSrc ) ) {
                LeftInputAdapterNode lia = (LeftInputAdapterNode) leftSrc;
                nodeId = lia.getObjectSource().getId();
                rightSource = lia.getObjectSource();
                handle = (ChanceFactHandle) lt.getHandle();
            } else if ( NodeTypeEnums.isFromNode( leftSrc ) ) {
                FromNode fromNode = (FromNode) leftSrc;
                nodeId = fromNode.getId();
                rightSource = fromNode;
                handle = (ChanceFactHandle) lt.getHandle();
            } else if ( NodeTypeEnums.isQueryElementNode( leftSrc ) ) {
                handle = (ChanceFactHandle) lt.getParent().getObject();
                DroolsQuery dq = (DroolsQuery) ((InternalFactHandle) handle).getObject();
                QueryTerminalNode qtn = (QueryTerminalNode) dq.getQueryNodeMemory().getQuerySegmentMemory().getTipNode();
                nodeId = 0;
                rightSource = null;
                //return combineLeftDegrees( qtn.getLeftTupleSource() );
            } else {
                throw new UnsupportedOperationException( "Unsupported object source" );
            }


            Evaluation rEval = null;
            if ( NodeTypeEnums.isBetaNode( leftSrc ) || NodeTypeEnums.isFromNode( leftSrc ) ) {
                rEval = handle.getCachedEvaluation( nodeId );
            }
            while ( rEval instanceof OuterOperatorEvaluation ) {
                outers.add( 0, (OuterOperatorEvaluation) rEval );
                rEval = rEval.getNext();
            }
            if ( NodeTypeEnums.isFromNode( leftSrc ) ) {
                rEval = combineFromDegrees( nodeId, (FromNode) leftSrc, handle, rEval );
            } else if ( NodeTypeEnums.isLeftTupleSource( leftSrc ) ) {
                rEval = combineRightDegrees( nodeId, rightSource, handle, rEval );
            } else {
                throw new UnsupportedOperationException( "Not yet implemented! " );
            }

            if ( rEval != null ) {
                args.add( 0, rEval );
            }

            while ( ! outers.isEmpty() && args.size() >= outers.get( 0 ).getArity() ) {
                OuterOperatorEvaluation outer = outers.remove( 0 );
                for ( int j = 0; j < outer.getArity(); j++ ) {
                    outer.addOrReplaceArgument( args.remove( 0 ), false );
                }
                args.add( 0, outer );
            }

            leftSrc = ( (LeftTupleSource) leftSrc ).getLeftTupleSource();
            if ( leftSrc != null ) {
                lt = lt.getLeftParent();
            }
        } while ( leftSrc != null );

        if ( args.isEmpty() ) {
            return new SimpleEvaluationImpl( tip.getId(), ChanceDegreeTypeRegistry.getDefaultOne() );
        } else if ( args.size() == 1 && args.get( 0 ).isAggregate() ) {
            return args.get( 0 );
        } else {
            CompositeEvaluation and = new CompositeEvaluation( tip.getId(),
                                                               null,
                                                               ChanceStrategyFactory.getConnectiveFactory( null, null ).getAnd(),
                                                               args.toArray( new Evaluation[ args.size() ] ) );
            return and;
        }
    }

    private Evaluation combineRightDegrees( int localId, BaseNode rightSource, ChanceFactHandle chandle, Evaluation betas ) {

        AggregateEvaluation rootEval = null;
        AggregateEvaluation currParent = null;

        while ( rightSource != null && rightSource.getType() != NodeTypeEnums.EntryPointNode ) {
            Evaluation constrEval = chandle.getCachedEvaluation( rightSource.getId() );

            if ( constrEval == null ) {
                if ( NodeTypeEnums.isObjectTypeNode( rightSource ) ) {
                    String label = ( ( ClassObjectType ) ( (ObjectTypeNode) rightSource ).getObjectType() ).getClassName();
                    constrEval = new SimpleEvaluationImpl( rightSource.getId(), "this instanceof " + label, ChanceDegreeTypeRegistry.getDefaultOne().True(), label );
                    chandle.addEvaluation( rightSource.getId(), constrEval );
                }
                /*
                else {
                    constrEval = new SimpleEvaluationImpl( rightSource.getId(), ChanceDegreeTypeRegistry.getDefaultOne().True() );
                }
                */
            }

            if ( rootEval == null ) {
                if ( constrEval != null && constrEval.isAggregate() ) {
                    rootEval = (AggregateEvaluation) constrEval;
                    currParent = (AggregateEvaluation) constrEval;
                    if ( ! rootEval.needsArguments() ) {
                        return rootEval;
                    }
                } else {
                    // getDegree has been invoked on a fully boolean pattern, with no ~
                }
            } else {
                if ( constrEval != null && ownsArgument( currParent, constrEval ) ) {
                    currParent.addOrReplaceArgument( constrEval, true );
                    if ( constrEval.isAggregate() ) {
                        currParent = (AggregateEvaluation) constrEval;
                    }
                }

            }

            if ( constrEval != null && constrEval.isAggregate() && ((AggregateEvaluation) constrEval).needsArguments() ) {
                AggregateEvaluation agg = (AggregateEvaluation) constrEval;
                for ( int j = agg.getArity() - 1; j >= 0; j-- ) {
                    if ( agg.isBeta( j ) ) {
                        Evaluation eval = betas;
                        agg.setBetaEvaluation( eval );
                        betas = betas.getNext();
                    }
                }
            }

            if ( rootEval != currParent ) {
                if ( currParent.getNumAvailableArguments() == currParent.getArity() ) {
                    do {
                        currParent = currParent.getParent();
                    } while ( currParent != null && currParent.getNumAvailableArguments() == currParent.getArity() );
                }
            }

            if ( NodeTypeEnums.isObjectSource( rightSource ) ) {
                rightSource = ((ObjectSource) rightSource).getParentObjectSource();
            } else {
                rightSource = null;
            }
        }

        if ( rootEval != null ) {
            chandle.addEvaluation( localId, rootEval );
        }
        return rootEval;
    }


    private Evaluation combineFromDegrees( int localId, FromNode source, ChanceFactHandle chandle, Evaluation evals ) {

        Evaluation rootEval = evals;
        Evaluation constrEval = evals;
        AggregateEvaluation currentParent = null;

        while ( constrEval != null ) {

            if ( currentParent != null && currentParent.needsArguments() && ownsArgument( currentParent, constrEval ) ) {
                currentParent.addOrReplaceArgument( constrEval, false );
                while ( ! currentParent.needsArguments() ) {
                    currentParent = currentParent.getParent();
                }
            }

            if ( constrEval.isAggregate() ) {
                currentParent = (AggregateEvaluation) constrEval;
            }

            constrEval = constrEval.getNext();
        }

        if ( rootEval.isAggregate() && ((AggregateEvaluation) rootEval).needsArguments() ) {
            String label = ((ClassObjectType) source.getObjectType() ).getClassName();
            constrEval = new SimpleEvaluationImpl( source.getId(), "this instanceof " + label, ChanceDegreeTypeRegistry.getDefaultOne().True(), label );
            ( (AggregateEvaluation) rootEval ).addOrReplaceArgument( constrEval, false );
        }

        chandle.addEvaluation( localId, rootEval );
        return rootEval;
    }

    private boolean ownsArgument( AggregateEvaluation currParent, Evaluation constrEval ) {
        if ( currParent.getNumAvailableArguments() < currParent.getArity() ) {
            return true;
        }
        return currParent.hasArgument( constrEval );
    }

    public Evaluation getEvaluation( String label ) {
        if ( label == null || label.isEmpty() ) {
            return getEvaluation();
        }
        return getEvaluation().lookupLabel( label );
    }

}
