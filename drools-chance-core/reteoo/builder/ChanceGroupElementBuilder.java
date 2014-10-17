package org.drools.chance.reteoo.builder;

import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveFactory;
import org.drools.chance.rule.constraint.core.connectives.impl.MvlFamilies;
import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.ImpKind;
import org.drools.chance.distribution.ImpType;
import org.drools.factmodel.AnnotationDefinition;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ReteooComponentFactory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.builder.BuildUtils;
import org.drools.reteoo.builder.GroupElementBuilder;
import org.drools.rule.GroupElement;
import org.drools.rule.RuleConditionElement;

import java.util.Map;

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
            super.build( context, utils, rce );

            ChanceGroupElement cge = (ChanceGroupElement) rce;


            String family   = null;
            ImpKind impKind = null;
            ImpType impType = null;
            DegreeType degT = null;
            String label = null;
            if ( cge.getMetadata() != null ) {
                Map<String,AnnotationDefinition.AnnotationPropertyVal> params = cge.getMetadata().getValues();
                family  = params.containsKey( MvlFamilies.name ) ? MvlFamilies.parse( (String) cge.getMetadata().getValues().get( MvlFamilies.name ).getValue() ).value() : null;
                impKind = params.containsKey( ImpKind.name ) ? ImpKind.parse( (String) cge.getMetadata().getValues().get( ImpKind.name ).getValue() ) : null;
                impType = params.containsKey( ImpType.name ) ? ImpType.parse( (String) cge.getMetadata().getValues().get( ImpType.name ).getValue() ) : null;
                degT    = params.containsKey( DegreeType.name ) ? DegreeType.parse( (String) cge.getMetadata().getValues().get( DegreeType.name ).getValue() ) : null;
                label   = params.containsKey( "label" ) ? (String) cge.getMetadata().getValues().get( "label" ).getValue() : null; 
            }

            ConnectiveFactory factory = ChanceStrategyFactory.getConnectiveFactory( impKind, impType );

            ConnectiveCore and = family != null ? factory.getAnd( family ) : factory.getAnd();
            Degree baseDegree = ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degT, 0 );


            context.setTupleSource( (LeftTupleSource) utils.attachNode( context,
                    ( (ChanceNodeFactory ) context.getComponentFactory().getNodeFactoryService() ).
                            buildLogicalBetaOperatorNode(
                                    context.getNextId(),
                                    label,
                                    and,
                                    ((GroupElement) rce).getChildren().size(),
                                    context.getTupleSource(),
                                    context ) ) );

        }
    }


    // Beware : we extend ANDBuilder, the only difference here is the final operator
    private class ImperfectOrBuilder extends AndBuilder {

        public void build( final BuildContext context,
                           final BuildUtils utils,
                           final RuleConditionElement rce) {
            super.build( context, utils, rce );

            ChanceGroupElement cge = (ChanceGroupElement) rce;


            String family   = null;
            ImpKind impKind = null;
            ImpType impType = null;
            DegreeType degT = null;
            String label    = null;
            if ( cge.getMetadata() != null ) {
                Map<String,AnnotationDefinition.AnnotationPropertyVal> params = cge.getMetadata().getValues();
                family  = params.containsKey( MvlFamilies.name ) ? MvlFamilies.parse( (String) cge.getMetadata().getValues().get( MvlFamilies.name ).getValue() ).value() : null;
                impKind = params.containsKey( ImpKind.name ) ? ImpKind.parse( (String) cge.getMetadata().getValues().get( ImpKind.name ).getValue() ) : null;
                impType = params.containsKey( ImpType.name ) ? ImpType.parse( (String) cge.getMetadata().getValues().get( ImpType.name ).getValue() ) : null;
                degT    = params.containsKey( DegreeType.name ) ? DegreeType.parse( (String) cge.getMetadata().getValues().get( DegreeType.name ).getValue() ) : null;
                label   = params.containsKey( "label" ) ? (String) cge.getMetadata().getValues().get( "label" ).getValue() : null;
            }

            ConnectiveFactory factory = ChanceStrategyFactory.getConnectiveFactory( impKind, impType );

            ConnectiveCore or = family != null ? factory.getOr( family ) : factory.getOr();
            Degree baseDegree = ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degT, 0 );


            context.setTupleSource( (LeftTupleSource) utils.attachNode( context,
                    ( (ChanceNodeFactory ) context.getComponentFactory().getNodeFactoryService() ).
                            buildLogicalBetaOperatorNode(
                                    context.getNextId(),
                                    label,
                                    or,
                                    ((GroupElement) rce).getChildren().size(),
                                    context.getTupleSource(),
                                    context ) ) );

        }

    }


}
