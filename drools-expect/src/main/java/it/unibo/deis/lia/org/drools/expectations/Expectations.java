package it.unibo.deis.lia.org.drools.expectations;

import it.unibo.deis.lia.org.drools.expectations.model.Compensation;
import it.unibo.deis.lia.org.drools.expectations.model.Expectation;
import it.unibo.deis.lia.org.drools.expectations.model.Failure;
import it.unibo.deis.lia.org.drools.expectations.model.Fulfill;
import it.unibo.deis.lia.org.drools.expectations.model.OneShotExpectation;
import it.unibo.deis.lia.org.drools.expectations.model.Success;
import it.unibo.deis.lia.org.drools.expectations.model.Viol;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

public class Expectations {

    public static Expectation newExpectation( String label,
                                              String ruleName,
                                              long now,
                                              Match currentMatch,
                                              boolean trim,
                                              boolean oneShot ) {

        java.util.List __templist = new java.util.ArrayList( currentMatch.getObjects() );
        if ( trim ) {
            __templist.remove( __templist.size() - 1 );
        }
        if ( oneShot ) {
            return new OneShotExpectation(
                    ((org.drools.core.spi.Activation) currentMatch).getActivationNumber(),
                    label,
                    currentMatch,
                    ((org.drools.core.spi.Activation) currentMatch).getActivationNumber(),
                    __templist,
                    now,
                    -1,
                    true,
                    ruleName );
        } else {
            return new Expectation(
                    ((org.drools.core.spi.Activation) currentMatch).getActivationNumber(),
                    label,
                    currentMatch,
                    ((org.drools.core.spi.Activation) currentMatch).getActivationNumber(),
                    __templist,
                    now,
                    -1,
                    true,
                    ruleName );
        }
    }

    public static Fulfill newFulfill( String label,
                                      String ruleName,
                                      Match currentMatch,
                                      long $___initial_activation_ID,
                                      Match $___pending_expectation_act ) {

        java.util.List __exp_fulfill_args = new java.util.ArrayList( currentMatch.getObjects() );
        __exp_fulfill_args.remove( __exp_fulfill_args.size() - 1 );

        return new Fulfill(
                $___initial_activation_ID,
                label,
                ((org.drools.core.spi.Activation) $___pending_expectation_act).getActivationNumber(),
                ((org.drools.core.spi.Activation) currentMatch).getActivationNumber(),
                new java.util.ArrayList( $___pending_expectation_act.getObjects() ),
                __exp_fulfill_args,
                ruleName );
    }

    public static Viol newViolation( String label,
                                     String ruleName,
                                     Match currentMatch,
                                     long $___initial_activation_ID,
                                     Match $___pending_expectation_act ) {

        java.util.List __exp_fulfill_args = new java.util.ArrayList( currentMatch.getObjects() );
        __exp_fulfill_args.remove( __exp_fulfill_args.size() - 1 );

        return new Viol(
                $___initial_activation_ID,
                label,
                ((org.drools.core.spi.Activation) $___pending_expectation_act).getActivationNumber(),
                ((org.drools.core.spi.Activation) currentMatch).getActivationNumber(),
                new java.util.ArrayList( $___pending_expectation_act.getObjects() ),
                false,
                ruleName );
    }

    public static Success newSuccess( long $___initial_activation_ID,
                                      String label,
                                      Match currentMatch ) {
        return new Success(
                $___initial_activation_ID,
                label,
                new java.util.ArrayList( currentMatch.getObjects() ) );
    }

    public static Failure newFailure( long $___initial_activation_ID,
                                      String label,
                                      Match currentMatch ) {
        return new Failure(
                $___initial_activation_ID,
                label,
                new java.util.ArrayList( currentMatch.getObjects() ) );
    }

    public static Compensation newCompensation( String label,
                                                long originId,
                                                Match currentMatch ) {
        java.util.List __comp_args = new java.util.ArrayList( currentMatch.getObjects() );
        __comp_args.remove( __comp_args.size() - 1 );


        return new Compensation( label,
                                 originId,
                                 new java.util.ArrayList( __comp_args ) );
    }


    public static String formatTuple( Match act ) {
        StringBuilder sb = new StringBuilder( "[" );
        for ( FactHandle handle : act.getFactHandles() ) {
            if ( ! ( ((InternalFactHandle) handle).getObject() instanceof Expectation ) ) {
                sb.append( " " ).append( ((InternalFactHandle) handle).getId() ).append( ", " );
            }
        }
        int end = sb.lastIndexOf( "," );
        sb.setCharAt( end, ' ' );
        sb.setCharAt( end + 1, ']' );
        return sb.toString().trim();
    }

}
