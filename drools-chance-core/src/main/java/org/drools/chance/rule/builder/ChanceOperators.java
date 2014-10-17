package org.drools.chance.rule.builder;


import org.drools.core.base.evaluators.Operator;

import java.util.Arrays;

public class ChanceOperators {



    public static final String IMPERFECT_MARKER = "~";

    public static final Operator               EQUAL_IMP            = Operator.addOperatorToRegistry( makeImperfect( "==" ), false );

    public static final Operator               NOT_EQUAL_IMP        = Operator.addOperatorToRegistry( makeImperfect( "!=" ), false );

    public static final Operator               LESS_IMP             = Operator.addOperatorToRegistry( makeImperfect( "<"  ), false );

    public static final Operator               LESS_OR_EQUAL_IMP    = Operator.addOperatorToRegistry( makeImperfect( "<=" ), false );

    public static final Operator               GREATER_IMP          = Operator.addOperatorToRegistry( makeImperfect( ">"  ), false );

    public static final Operator               GREATER_OR_EQUAL_IMP = Operator.addOperatorToRegistry( makeImperfect( ">=" ), false );


    public static final String[] standard_imperfectOperators;

    static {
        standard_imperfectOperators = new String[] {
                ChanceOperators.EQUAL_IMP.getOperatorString(),
                ChanceOperators.LESS_IMP.getOperatorString(),
                ChanceOperators.GREATER_IMP.getOperatorString(),
                ChanceOperators.GREATER_OR_EQUAL_IMP.getOperatorString(),
                ChanceOperators.LESS_OR_EQUAL_IMP.getOperatorString(),
                ChanceOperators.NOT_EQUAL_IMP.getOperatorString()
        };
        Arrays.sort( standard_imperfectOperators );
    }

    public static boolean lookup( String imperfectOperator ) {
        return Arrays.binarySearch( standard_imperfectOperators, imperfectOperator ) >= 0;
    }

    public static Operator getOperator( String imperfectOperator ) {
        if ( ChanceOperators.EQUAL_IMP.getOperatorString().equals( imperfectOperator ) ) {
            return EQUAL_IMP;
        }
        if ( ChanceOperators.NOT_EQUAL_IMP.getOperatorString().equals( imperfectOperator ) ) {
            return NOT_EQUAL_IMP;
        }
        if ( ChanceOperators.LESS_IMP.getOperatorString().equals( imperfectOperator ) ) {
            return LESS_IMP;
        }
        if ( ChanceOperators.GREATER_IMP.getOperatorString().equals( imperfectOperator ) ) {
            return GREATER_IMP;
        }
        if ( ChanceOperators.GREATER_OR_EQUAL_IMP.getOperatorString().equals( imperfectOperator ) ) {
            return GREATER_OR_EQUAL_IMP;
        }
        if ( ChanceOperators.LESS_OR_EQUAL_IMP.getOperatorString().equals( imperfectOperator ) ) {
            return LESS_OR_EQUAL_IMP;
        }
        return null;
    }

    public static boolean isImperfect( String op ) {
        return op.startsWith( ChanceOperators.IMPERFECT_MARKER );
    }

    public static boolean isImperfect( Operator op ) {
        return op.getOperatorString().startsWith(ChanceOperators.IMPERFECT_MARKER);
    }

    public static String makeImperfect( String operatorString ) {
        return IMPERFECT_MARKER + operatorString;
    }

    public static String makePerfect(String operator) {
        return operator.replace( ChanceOperators.IMPERFECT_MARKER, "" );
    }

    public static String renameBasic( String alias ) {
        if ( alias.contains( makePerfect( EQUAL_IMP.getOperatorString() ) ) ) {
            return alias.replace( makePerfect( EQUAL_IMP.getOperatorString() ), "equals_i" );
        }
        if ( alias.contains( makePerfect( NOT_EQUAL_IMP.getOperatorString() ) ) ) {
            return alias.replace( makePerfect( NOT_EQUAL_IMP.getOperatorString() ), "diff_i" );
        }
        if ( alias.contains( makePerfect( GREATER_OR_EQUAL_IMP.getOperatorString() ) ) ) {
            return alias.replace( makePerfect( GREATER_OR_EQUAL_IMP.getOperatorString() ), "geq_i" );
        }
        if ( alias.contains( makePerfect( LESS_OR_EQUAL_IMP.getOperatorString() ) ) ) {
            return alias.replace( makePerfect( LESS_OR_EQUAL_IMP.getOperatorString() ), "leq_i" );
        }
        if ( alias.contains( makePerfect( GREATER_IMP.getOperatorString() ) ) ) {
            return alias.replace( makePerfect( GREATER_IMP.getOperatorString() ), "greater_i" );
        }
        if ( alias.contains( makePerfect( LESS_IMP.getOperatorString() ) ) ) {
            return alias.replace( makePerfect( LESS_IMP.getOperatorString() ), "less_i" );
        }
        return null;
    }
}
