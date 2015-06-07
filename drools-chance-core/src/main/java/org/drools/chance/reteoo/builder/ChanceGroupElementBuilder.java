package org.drools.chance.reteoo.builder;

import org.drools.chance.rule.constraint.OuterOperatorConstraint;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.rule.constraint.core.connectives.impl.AbstractConnective;
import org.drools.chance.rule.constraint.core.connectives.impl.LogicConnectives;
import org.drools.compiler.lang.descr.ConnectiveType;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.builder.BuildUtils;
import org.drools.core.reteoo.builder.GroupElementBuilder;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.RuleConditionElement;


public class ChanceGroupElementBuilder extends GroupElementBuilder {

    public ChanceGroupElementBuilder() {
        this.geBuilders.put( GroupElement.AND,
                new ImperfectAndBuilder() );
        this.geBuilders.put( GroupElement.OR,
                new ImperfectOrBuilder() );
        this.geBuilders.put( GroupElement.NOT,
                new NotBuilder() );
        this.geBuilders.put( GroupElement.EXISTS,
                new ExistsBuilder() );
    }


    private class ImperfectAndBuilder extends AndBuilder {
        public void build( final BuildContext context,
                           final BuildUtils utils,
                           final RuleConditionElement rce) {

            ChanceGroupElement cge = (ChanceGroupElement) rce;

            if ( ! cge.getChildren().isEmpty() ) {
                int n = cge.getChildren().size();
                    OuterOperatorConstraint ooc = buildOperator( ConnectiveType.AND, cge.getMetadata(), n );
                    Pattern last = findLastPattern( cge );
                if ( n > 1 || ooc.getConnective().getType() == LogicConnectives.NOT ) {
                    last.addConstraint( ooc );
                }
            }

            super.build( context, utils, rce );
        }
    }

    private class ImperfectOrBuilder extends AndBuilder {

        public void build( final BuildContext context,
                           final BuildUtils utils,
                           final RuleConditionElement rce) {

            ChanceGroupElement cge = (ChanceGroupElement) rce;

            if ( ! cge.getChildren().isEmpty() ) {
                int n = cge.getChildren().size();
                OuterOperatorConstraint ooc = buildOperator( ConnectiveType.OR, cge.getMetadata(), n );
                Pattern last = findLastPattern( cge );
                if ( n > 1 || ooc.getConnective().getType() == LogicConnectives.NOT ) {
                    last.addConstraint( ooc );
                }
            }

            super.build( context, utils, rce );

        }

    }


    private Pattern findLastPattern( GroupElement rce ) {
        RuleConditionElement child = rce.getChildren().get( rce.getChildren().size() - 1 );
        if ( child instanceof Pattern ) {
            return (Pattern) child;
        } else if ( child instanceof GroupElement ) {
            return findLastPattern( (GroupElement) child );
        } else if ( child instanceof QueryElement ) {
            return ((QueryElement) child).getResultPattern();
        } else {
            throw new UnsupportedOperationException( "Unable to find appropriate pattern" );
        }
    }

    private OuterOperatorConstraint buildOperator( ConnectiveType op, AnnotationDefinition adef, int arity ) {
        ConnectiveCore conn = AbstractConnective.buildConnective( adef, op );
        String label = adef != null ? (String) adef.getPropertyValue( "label" ) : null;
        return new OuterOperatorConstraint( arity, conn, label );
    }


}
