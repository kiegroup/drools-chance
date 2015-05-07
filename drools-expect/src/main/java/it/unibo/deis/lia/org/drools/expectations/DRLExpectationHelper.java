/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unibo.deis.lia.org.drools.expectations;


import it.unibo.deis.lia.org.drools.expectations.model.Expectation;
import it.unibo.deis.lia.org.drools.expectations.model.ExpectationContext;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.ECEPackageDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.ExpectationDescr;
import org.drools.compiler.lang.descr.ExpectationRuleDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.util.StringUtils;

import java.util.*;

public class DRLExpectationHelper {

    private static final    String              VAR_PENDING__EXPECTATION__ACT       = "$___pending_expectation_act";
    public  static final    String              EXP_PACKAGE                         = Expectation.class.getPackage().getName();
    private static final    String              VAR_CONTEXT                         = "$___initial_activation_ID";

    private static final    String              NC_MATCH                            = "__match";
    private static final    String              NC_COMPENSATIONS                    = "__compensations";
    private static final    String              NC_FULFILL                          = "__fulfill";
    private static final    String              NC_VIOL                             = "__viol";
    private static final    String              NC_EXPECT                           = "__expect";

    private                 int                 counter                             = 0;

    private int nextCount() {
        return counter++;
    }

    public String nextLabel(String rule) {
        return "_$exp_"+ escape(rule) +"_" + nextCount();
    }

    private static String escape(String rule) {
        return rule.replace(" ", "_").replace("-", "_").replace(":", "_");
    }

    public void mirrorConditions( ConditionalElementDescr target, ConditionalElementDescr source ) {
        for ( BaseDescr dx : source.getDescrs() ) {
            if ( dx instanceof NamedConsequenceDescr ) {
                continue;
            }
            if ( dx instanceof ConditionalElementDescr ) {
                dx = cleanDescr( dx );
            }
            if ( dx instanceof PatternDescr && Expectation.class.getName().equals( ( (PatternDescr) dx ).getObjectType() ) ) {
                continue;
            }
            target.addDescr(dx);
        }
    }

    private BaseDescr cleanDescr( BaseDescr dx ) {
        if ( ( dx instanceof AndDescr || dx instanceof OrDescr ) ) {
            ConditionalElementDescr ce = (ConditionalElementDescr) dx;
            if ( ce.getDescrs().size() == 1 ) {
                return cleanDescr( ce.getDescrs().get( 0 ) );
            }
        }
        return dx;
    }


    public void buildExpirationRule( PackageDescrBuilder builder, ExpectationRuleDescr rule, ExpectationDescr expectations ) {
        RuleDescr expireRule = expectations.getExpired();
        expireRule.addAnnotation( "Propagation", "EAGER" );
        AndDescr expireLhs = expireRule.getLhs();
        List<BaseDescr> existingDescrs = null;
        if (!expireLhs.getDescrs().isEmpty()) {
            existingDescrs = new ArrayList<BaseDescr>(expireLhs.getDescrs());
            expireLhs.getDescrs().clear();
        }
        mirrorConditions(expireLhs, rule.getLhs());
        if (existingDescrs != null) {
            for (BaseDescr descr: existingDescrs) {
                expireLhs.addDescr(descr);
            }
        }
        expireLhs.addDescr(expectPattern(expectations.getLabel(), extractVars(rule.getLhs(),expectations.getLabel()),false));
        expireRule.setConsequence(insertFailsOnClosure(null,expectations.getLabel()));
        builder.getDescr().addRule(expireRule);
    }

    public void buildExpirationRule( AndDescr trigger, String expLabel, AndDescr and, PackageDescrBuilder packBuilder ) {


        long offset = ExpirationCalc.calcExpirationOffset(trigger, and);

        if( offset < 0 || offset == Long.MAX_VALUE ) {
            return;
        }

        RuleDescrBuilder ruleBuilder = packBuilder.newRule();
            ruleBuilder.name( EXP_PACKAGE + "." + expLabel + " Expire " );

        ruleBuilder.attribute( "duration", "" + offset );

        ruleBuilder.rhs(removePending(expLabel));

        CEDescrBuilder<RuleDescrBuilder,AndDescr> lhs = ruleBuilder.lhs();

        PatternDescrBuilder expect = lhs.pattern();
            expect.type(Expectation.class.getName());
            expect.isQuery(false);
            expect.id(expLabel, false);
            expect.constraint("label == \"" + expLabel + "\"", false);
    }


    public BaseDescr expectPattern( String label, Collection<String> vars, boolean unfulfilledOnly ) {

        StringBuilder sb = new StringBuilder("[ ");
        Iterator<String> iter = vars.iterator();
        while ( iter.hasNext() ) {
            sb.append( iter.next() );
            if ( iter.hasNext() )
                sb.append( ", " );
        }
        sb.append(" ]");

        PatternDescr patternDescr = new PatternDescr( EXP_PACKAGE + ".Expectation", label );
        patternDescr.setQuery(false);

        patternDescr.addConstraint(constraint(VAR_CONTEXT, true, patternDescr));
        patternDescr.addConstraint( constraint( "\"" + label + "\"", true, patternDescr ) );
        patternDescr.addConstraint(constraint(VAR_PENDING__EXPECTATION__ACT, true, patternDescr));
        patternDescr.addConstraint(constraint("$actId", true, patternDescr));
        patternDescr.addConstraint(constraint("tuple.equals( " + sb.toString() + " )", false, patternDescr));

        if ( unfulfilledOnly ) {
            patternDescr.addConstraint( constraint( "fulfilled == false", false, patternDescr ) );
        } else {
            patternDescr.addConstraint( constraint( "active == true", false, patternDescr ) );
        }



        return patternDescr;
    }

    protected ExprConstraintDescr constraint( String epxr, boolean positional, PatternDescr patternDescr ) {
        ExprConstraintDescr constr = new ExprConstraintDescr( epxr );
        constr.setType(positional ? ExprConstraintDescr.Type.POSITIONAL : ExprConstraintDescr.Type.NAMED);
        constr.setPosition(patternDescr.getDescrs().size());
        return constr;

    }


    public String removePending(String expLabel) {
        return "\t modify( " + expLabel + " ) { \n setActive( false ); } \n";
    }


    public String insertExpectation( String label, String ruleName, boolean needsTrim, boolean matchOne ) {
        StringBuilder sb = new StringBuilder( "\t insert( " ).append( Expectations.class.getName() ).append( ".newExpectation( " )
                .append( newline() ).append( '\"' ).append( label ).append( '\"' ).append( ", " )
                .append( newline() ).append( '\"' ).append( ruleName ).append( '\"' ).append( ", " )
                .append( newline() ).append( "drools.getWorkingMemory().getSessionClock().getCurrentTime()" ).append( ", " )
                .append( newline() ).append( "drools.getMatch()" ).append( ", " )
                .append( newline() ).append( Boolean.toString( needsTrim ) ).append( ", " )
                .append( newline() ).append( Boolean.toString( matchOne ) ).append( " ) ); \n" );
        return sb.toString();
    }

    public String insertFulfill( String label, String ruleName ) {
        StringBuilder sb = new StringBuilder( "\t insert( " ).append(Expectations.class.getName()).append( ".newFulfill( " )
                .append(newline()).append( '\"' ).append(label).append( '\"' ).append( ", " )
                .append(newline()).append( '\"' ).append(ruleName).append( '\"' ).append( ", " )
                .append( newline() ).append( "drools.getMatch()" ).append( ", " )
                .append( newline() ).append( VAR_CONTEXT ).append( ", " )
                .append( newline() ).append( VAR_PENDING__EXPECTATION__ACT ).append( " ) ); \n" );
        return sb.toString();
    }

    public String insertViolation( String label, String ruleName ) {
        StringBuilder sb = new StringBuilder( "\t insert( " ).append( Expectations.class.getName() ).append( ".newViolation( " )
                .append( newline() ).append( '\"' ).append( label ).append( '\"' ).append( ", " )
                .append( newline() ).append( '\"' ).append( ruleName ).append( '\"' ).append( ", " )
                .append( newline() ).append( "drools.getMatch()" ).append( ", " )
                .append( newline() ).append( VAR_CONTEXT ).append( ", " )
                .append( newline() ).append( VAR_PENDING__EXPECTATION__ACT ).append( " ) ); \n" );
        return sb.toString();
    }

    public String insertFailsOnClosure( String label, String ruleName ) {
        StringBuilder sb = new StringBuilder("\t insert( " );
        sb.append("new Closure( \"").append(ruleName).append("\" ) ); ");
        return sb.toString();
    }



    public void compensations( ExpectationRuleDescr ruleDescr ) {
        StringBuilder sb = new StringBuilder();
        for ( String violId : ruleDescr.getRepairs() ) {
            sb.append( "\t insert( " ).append( Expectations.class.getName() ).append( ".newCompensation( " )
                    .append( newline() ).append( '\"' ).append( violId ).append( '\"' ).append( ", " )
                    .append( newline() ).append( VAR_CONTEXT ).append( ", " )
                    .append( newline() ).append( "drools.getMatch()" ).append( " ) ); \n" );
        }

        ruleDescr.addNamedConsequences( NC_COMPENSATIONS, sb.toString() );

        NamedConsequenceDescr ncd = new NamedConsequenceDescr();
        ncd.setBreaking( false );
        ncd.setName( NC_COMPENSATIONS );
        ruleDescr.getLhs().addDescr( ncd );
    }


    public String successRHS( String name ) {
        StringBuilder sb = new StringBuilder( "\t insert( " ).append( Expectations.class.getName() ).append( ".newSuccess( " )
                .append( newline() ).append( "$context" ).append( ", " )
                .append( newline() ).append( '\"' ).append( name ).append( '\"' ).append( ", " )
                .append( newline() ).append( "drools.getMatch()" ).append( " ) ); \n" );
        return sb.toString();
    }


    public String failRHS(String name) {
        StringBuilder sb = new StringBuilder( "\t insert( " ).append( Expectations.class.getName() ).append( ".newFailure( " )
                .append( newline() ).append( "$context" ).append( ", " )
                .append( newline() ).append( '\"' ).append( name ).append( '\"' ).append( ", " )
                .append( newline() ).append( "drools.getMatch()" ).append( " ) ); \n" );
        return sb.toString();
    }


    public void buildConformanceRules( ECEPackageDescrBuilder packageDescrBuilder, ExpectationRuleDescr ruleDescr, ECEVisitor visitor ) {
        String name = ruleDescr.getName();
        AndDescr metaRoot = (AndDescr) ruleDescr.getExpectations();

        RuleDescrBuilder successRule = packageDescrBuilder.newRule();
        successRule.newAnnotation( "Propagation( EAGER )" );
        RuleDescrBuilder failRule = packageDescrBuilder.newRule();
        failRule.newAnnotation( "Propagation( EAGER )" );

        CEDescrBuilder sLHSBuilder = successRule.lhs();
        PatternDescrBuilder smaster = sLHSBuilder.pattern();
            smaster.type( ExpectationContext.class.getName() );
            smaster.isQuery( false );
            smaster.constraint( "$context", true );

        CEDescrBuilder fLHSBuilder = failRule.lhs();
        PatternDescrBuilder fmaster = fLHSBuilder.pattern();
            fmaster.type( ExpectationContext.class.getName() );
            fmaster.isQuery( false );
            fmaster.constraint( "$context", true );

        visitor.visit( metaRoot, sLHSBuilder );
        visitor.deMorganize( metaRoot, fLHSBuilder );

        successRule.name( EXP_PACKAGE + "." + name + "_Conformance_Succ" );
            successRule.rhs( successRHS( name ) );
        failRule.name( EXP_PACKAGE + "." + name + "_Conformance_Fail" );
            failRule.rhs( failRHS( name ) );

    }


    private List<String> extractVars(AndDescr lhs, String defLabel) {
        List<String> vars = new LinkedList<String>();
        int patIndex = 0;

        List<BaseDescr> lhsArgs = lhs.getDescrs();
        for ( int j = 0; j < lhsArgs.size() - 1; j++ ) {
            BaseDescr descr = lhsArgs.get( j );
            if ( descr instanceof PatternDescr && ! Expectation.class.getName().equals( ( (PatternDescr) descr ).getObjectType() ) ) {
                String id = ( (PatternDescr) descr ).getIdentifier();
                if ( id != null ) {
                    vars.add( id );
                } else {
                    id = defLabel + "_pat_" + patIndex;
                    ( (PatternDescr) descr ).setIdentifier( id );
                    vars.add( id );
                }
                patIndex++;
            }
        }
        return vars;
    }


    public void injectMainExpectationRuleWithContext( ExpectationRuleDescr xp, ExpectationDescr expectationDescr, boolean inner ) {
        String label = NC_EXPECT;
        String assertion = insertExpectation( expectationDescr.getLabel(), xp.getName(), inner, expectationDescr.isMatchOne() );

        if ( xp.getNamedConsequences().containsKey( label ) ) {
            String ncd = xp.getNamedConsequences().get( label ) + assertion;
            xp.getNamedConsequences().put( label, ncd );
        } else {
            StringBuilder rhs = new StringBuilder()
                    .append( "\t " ).append( "insert( new ").append( ExpectationContext.class.getName() ).append( "( drools.getMatch() ) ); \n" )
                    .append( assertion );

            NamedConsequenceDescr ncd = new NamedConsequenceDescr(  );
            ncd.setBreaking( false );
            ncd.setName( label );

            xp.getLhs().addDescr( ncd );
            xp.addNamedConsequences( label, rhs.toString() );
        }
    }


    public void buildFulfillRule( PackageDescrBuilder builder, ExpectationRuleDescr rule, ExpectationDescr expectations ) {

        RuleDescr fulfillRule = expectations.getFulfill();
        fulfillRule.addAnnotation( "Propagation", "EAGER" );
        boolean addMatchConsequence = ! StringUtils.isEmpty( fulfillRule.getConsequence().toString().trim() );

        AndDescr fulfillLhs = fulfillRule.getLhs();
        mirrorConditions( fulfillLhs, rule.getLhs() );
        mirrorConditions( fulfillLhs, expectations.getExpectLhs() );

        if ( addMatchConsequence ) {
            NamedConsequenceDescr match = new NamedConsequenceDescr(  );
            match.setBreaking( false );
            match.setName( NC_MATCH );
            fulfillLhs.addDescr( match );
        }

        if ( ! rule.isExpectationDisabled() ) {
            fulfillLhs.addDescr( expectPattern( expectations.getLabel(), extractVars( rule.getLhs(), expectations.getLabel() ), false ) );
        }

        if ( ! rule.isExpectationDisabled() ) {
            NamedConsequenceDescr ncd = new NamedConsequenceDescr(  );
            ncd.setBreaking( false );
            ncd.setName( NC_FULFILL );
            fulfillLhs.addDescr( ncd );
        }


        if ( addMatchConsequence ) {
            fulfillRule.addNamedConsequences( NC_MATCH, fulfillRule.getConsequence() );
        }
        if ( ! rule.isExpectationDisabled() ) {
            fulfillRule.addNamedConsequences( NC_FULFILL, insertFulfill( expectations.getLabel(), rule.getName() ) );
        }
        fulfillRule.setConsequence( "" );

        builder.getDescr().addRule( fulfillRule );

    }
    
    public void buildViolationRule( PackageDescrBuilder builder, ExpectationRuleDescr rule, ExpectationDescr expectations ) {

        RuleDescr violRule = expectations.getViolation();
        violRule.addAnnotation( "Propagation", "EAGER" );

        AndDescr violLhs = violRule.getLhs();

        mirrorConditions( violLhs, rule.getLhs() );


        NotDescr not = new NotDescr( expectations.getExpectLhs()  );
        violLhs.addDescr( not );

        if ( ! rule.isExpectationDisabled() ) {
            violLhs.addDescr( expectPattern( expectations.getLabel(), extractVars( rule.getLhs(), expectations.getLabel() ), true ) );
        }

        if ( ! rule.isExpectationDisabled() ) {
            NamedConsequenceDescr ncd = new NamedConsequenceDescr(  );
            ncd.setBreaking( false );
            ncd.setName( NC_VIOL );
            violLhs.addDescr( ncd );

            violRule.addNamedConsequences( NC_VIOL, insertViolation( expectations.getLabel(), rule.getName() ) );
        }

        builder.getDescr().addRule( violRule );

    }


    public boolean isEffective( ExpectationRuleDescr rule ) {
        if ( ! StringUtils.isEmpty( rule.getConsequence().toString().trim() ) ) {
            return true;
        }
        for ( Object cons : rule.getNamedConsequences().values() ) {
            if ( ! StringUtils.isEmpty( cons.toString().trim() ) ) {
                return true;
            }
        }
        return false;
    }


    private String newline() {
        return "\n\t\t\t";
    }
}
