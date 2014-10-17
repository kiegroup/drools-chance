package org.drools.chance.reteoo.builder;


import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.chance.rule.constraint.OperatorConstraint;
import org.drools.chance.reteoo.nodes.ChanceAlphaNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ReteooComponentFactory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.builder.BuildUtils;
import org.drools.reteoo.builder.PatternBuilder;
import org.drools.rule.Pattern;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;

import java.util.List;


public class ChancePatternBuilder extends PatternBuilder {



    protected void buildAlphaNodeChain( BuildContext context, BuildUtils utils, Pattern pattern, List<AlphaNodeFieldConstraint> alphaConstraints ) {       

        for ( final Constraint constraint : pattern.getConstraints() ) {

            context.pushRuleComponent( constraint );

            if ( constraint.getType().equals( Constraint.ConstraintType.ALPHA ) ) {
                if ( constraint instanceof OperatorConstraint) {
                    OperatorConstraint opc = (OperatorConstraint) constraint;
                    context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                ((ChanceNodeFactory) context.getComponentFactory().getNodeFactoryService()).buildLogicalAlphaOperatorNode(
                                                        context.getNextId(),
                                                        opc.getLabel(),
                                                        opc.getConnective(),
                                                        opc.getArity(),
                                                        context.getObjectSource(),
                                                        context) ) );
                } else {
                    ChanceAlphaNode alpha = (ChanceAlphaNode) context.getComponentFactory().getNodeFactoryService().buildAlphaNode(
                                                        context.getNextId(),
                                                        (AlphaNodeFieldConstraint) constraint,
                                                        context.getObjectSource(),
                                                        context);
                    if ( ((ClassObjectType) pattern.getObjectType()).getClassType().equals( DroolsQuery.class ) ) {
                        alpha.setAlwaysPropagate( false );
                    }
                    context.setObjectSource( (ObjectSource) utils.attachNode( context, alpha ) );                             
                }
            } else {
                context.setObjectSource( (ObjectSource) utils.attachNode( context,
                        ((ChanceNodeFactory) context.getComponentFactory().getNodeFactoryService()).buildDelayedEvaluationNode(
                                context.getNextId(),
                                constraint,
                                context.getObjectSource(),
                                context) ) );
            }

            context.popRuleComponent();
        }
    }




    

}
