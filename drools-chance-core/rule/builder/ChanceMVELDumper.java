package org.drools.chance.rule.builder;


import org.drools.chance.rule.constraint.core.evaluators.ImperfectEvaluatorWrapper;
import org.drools.compiler.lang.MVELDumper;
import org.drools.compiler.lang.descr.OperatorDescr;

public class ChanceMVELDumper extends MVELDumper {




    
    protected boolean lookupBasicOperator( String op ) {
        return super.lookupBasicOperator( op ) || ChanceOperators.lookup(op);

    }
    

//    //    @Override
//    protected void processConnectiveDescr( StringBuilder sbuilder,
//                                           BaseDescr base,
//                                           int parentPriority,
//                                           boolean isInsideRelCons,
//                                           MVELDumperContext context ) {
//        System.err.println( "Called CMVELDMPR processCCD, should not ");
//        ConstraintConnectiveDescr ccd = (ConstraintConnectiveDescr) base;
//
//        boolean isImperfect = false;
//        for ( BaseDescr child : ccd.getDescrs() ) {
//            if ( child instanceof RelationalExprDescr ) {
//                if ( ChanceOperators.isImperfect( ((RelationalExprDescr) child).getOperator() ) ) {
//                    isImperfect = true;
//                    break;
//                }
//            }
//        }
//
//        if ( ! isImperfect ) {
//            super.processConnectiveDescr( sbuilder, base, parentPriority, isInsideRelCons, context );
//            return;
//        }
//
//        boolean first = true;
//        boolean wrapParenthesis = parentPriority > ccd.getConnective().getPrecedence();
//        if ( wrapParenthesis ) {
//            sbuilder.append( "( " );
//        }
//
//        ImpType type = null;
//        ImpKind kind = null;
//        DegreeType degree = null;
//        MvlFamilies family = null;
//        if ( ccd.getAnnotation( "Imperfect" ) != null ) {
//            AnnotationDescr ann = ccd.getAnnotation( "Imperfect" );
//            type = ImpType.parse( ann.getValue( ImpType.name ) );
//            kind = ImpKind.parse( ann.getValue( ImpKind.name ) );
//            degree = DegreeType.parse( ann.getValue( DegreeType.name ) );
//            family = MvlFamilies.parse( ann.getValue( MvlFamilies.name ) );
//        }
//
//        ConnectiveFactory factory = ChanceStrategyFactory.getConnectiveFactory( kind, type );
//
//        ConnectiveCore conn = null;
//        switch ( ccd.getConnective() ) {
//            case INC_AND:
//            case AND:       conn = family != null ? factory.getAnd( family.value() ) : factory.getAnd();
//                break;
//            case INC_OR:
//            case OR:        conn = family != null ? factory.getOr( family.value() ) : factory.getOr();
//                break;
//            case XOR:       conn = family != null ? factory.getXor( family.value() ) : factory.getXor();
//                break;
//            default:        throw new IllegalStateException( "Unable to find connective for " + ccd.getConnective() );
//        }
//
//        sbuilder.append( "new " + conn.getClass().getName() + "().eval( " );
//        for ( BaseDescr constr : ccd.getDescrs() ) {
//
//            if ( !( constr instanceof BindingDescr) ) {
//
//                if ( first ) {
//                    first = false;
//                } else {
//                    sbuilder.append( ", " );
//                }
//
//                dump( sbuilder,
//                        constr,
//                        ccd.getConnective().getPrecedence(),
//                        isInsideRelCons,
//                        context );
//            }
//
//        }
//
//        if( first == true ) {
//            // means all children were actually only bindings, replace by just true
//            sbuilder.append( ChanceDegreeTypeRegistry.defaultDegreeClass.getName() + ".TRUE" );
//        }
//
//        sbuilder.append( ")" );
//
//        if ( wrapParenthesis ) {
//            sbuilder.append( " )" );
//        }
//
//    }


    public Class<?> getEvaluatorWrapperClass() {
        return ImperfectEvaluatorWrapper.class;
    }


//    protected void rewriteBasicOperator( MVELDumperContext context,
//                                         StringBuilder sbuilder,
//                                         String left,
//                                         OperatorDescr operator,
//                                         String right) {
//
//        rewriteOperator( context, sbuilder, left, operator, right );
//    }


    @Override
    protected void rewriteOperator(MVELDumperContext context, StringBuilder sbuilder, String left, OperatorDescr operator, String right) {

        boolean isImperfect = ChanceOperators.isImperfect( operator.getOperator() );


        String alias = context.createAlias( operator );
        if ( isImperfect ) {
            // MVEL won't consume ope
            alias = ChanceOperators.makePerfect( alias );

            // this is basic operator, needs a better name
            if ( ChanceOperators.lookup( operator.getOperator() ) ) {
                alias = ChanceOperators.renameBasic( alias );
            }
            operator.setAlias( alias );
            context.getAliases().put( alias, operator );
        }
        operator.setLeftString( left );
        operator.setRightString( right );
        sbuilder.append( evaluatorPrefix( operator.isNegated(), isImperfect ) )
                .append( alias )
                .append( isImperfect ? ".match( " : ".evaluate( " )
                .append( left )
                .append( ", " )
                .append( right )
                .append( " )" )
                .append( evaluatorSufix( operator.isNegated() ) );
    }




    protected String evaluatorPrefix( final boolean isNegated, final boolean isImperfect ) {
        if ( ! isImperfect ) {
            return super.evaluatorPrefix( isNegated );
        } else {
            if ( isNegated ) {
//                throw new UnsupportedOperationException("TODO : Negation with imperfaect evals");
//            return "!( ";
                return super.evaluatorPrefix( isNegated );
            }
            return "";
        }
    }

    protected String evaluatorSufix( final boolean isNegated, final boolean isImperfect  ) {
        if ( ! isImperfect ) {
            return super.evaluatorSufix( isNegated );
        } else {
            if ( isNegated ) {
//                throw new UnsupportedOperationException("TODO : Negation with imperfaect evals");
//            return " )";
                return super.evaluatorSufix( isNegated );

            }
            return "";
        }
    }


}
