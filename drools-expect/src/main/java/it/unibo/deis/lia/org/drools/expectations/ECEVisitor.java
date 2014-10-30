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


import it.unibo.deis.lia.org.drools.expectations.model.Fulfill;
import it.unibo.deis.lia.org.drools.expectations.model.Viol;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.ECEPackageDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.ExpectationDescr;
import org.drools.compiler.lang.descr.ExpectationRuleDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.descr.XorDescr;
import org.drools.core.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ECEVisitor {

    private String packageName;
    private PackageDescr packageDescr;
    private DRLExpectationHelper helper;


    public ECEVisitor( PackageDescr packageDescr ) {
        this.packageName = packageDescr.getName();
        this.packageDescr = packageDescr;
        this.helper = new DRLExpectationHelper();
    }


    public String getPackage() {
        return packageName;
    }

    public void setPackage(String pack) {
        this.packageName = pack;
    }

    public void visit( ECEPackageDescrBuilder packageDescrBuilder ) {
        List<RuleDescr> srcRules = new ArrayList( packageDescr.getRules() );
        for ( RuleDescr rule : srcRules ) {
            if ( rule instanceof ExpectationRuleDescr ) {
                visitECERule( (ExpectationRuleDescr) rule, packageDescrBuilder, false );
            }
        }
    }

    private void visitECERule( ExpectationRuleDescr rule, ECEPackageDescrBuilder packageDescrBuilder, boolean inner ) {
        if ( rule.getExpectations() != null ) {
            visitExpectations( rule.getExpectations(), rule, packageDescrBuilder, inner );
            if ( ! inner && ! rule.isExpectationDisabled() ) {
                helper.buildConformanceRules( packageDescrBuilder, rule, this );
            }
        }
        if ( ! rule.getRepairs().isEmpty() ) {
            helper.compensations( rule );
        }
        if ( ! helper.isEffective( rule ) ) {
            packageDescr.getRules().remove( rule );
        }
    }

    private void visitExpectations( ConditionalElementDescr ce, ExpectationRuleDescr rule, ECEPackageDescrBuilder packageDescrBuilder, boolean inner  ) {
        for ( BaseDescr child : ce.getDescrs() ) {
            if ( child instanceof ConditionalElementDescr ) {
                visitExpectations( (ConditionalElementDescr) child, rule, packageDescrBuilder, inner );
            } else if ( child instanceof ExpectationDescr ) {
                visitExpectation( (ExpectationDescr) child, rule, packageDescrBuilder, inner );
            }
        }
    }

    private void visitExpectation( ExpectationDescr expectationDescr, ExpectationRuleDescr rule, ECEPackageDescrBuilder packageDescrBuilder, boolean inner ) {
        if ( ! expectationDescr.isEmpty() ) {
            if ( ! rule.isExpectationDisabled() ) {
                helper.injectMainExpectationRuleWithContext( rule, expectationDescr, inner );
            }

            if ( expectationDescr != null ) {
                visitExpectation( packageDescrBuilder, rule, expectationDescr );
            }
        }
    }

    private void visitExpectation( PackageDescrBuilder builder, ExpectationRuleDescr rule, ExpectationDescr expectations ) {

        if ( expectations.getFulfill() != null ) {
            helper.buildFulfillRule( builder, rule, expectations );
            visitECERule( (ExpectationRuleDescr) expectations.getFulfill(), (ECEPackageDescrBuilder) builder, true );
        }

        if ( expectations.getViolation() != null ) {
            helper.buildViolationRule( builder, rule, expectations );
            visitECERule( (ExpectationRuleDescr) expectations.getViolation(), (ECEPackageDescrBuilder) builder, true );
        }

        if ( expectations.getLabel() != null && ! rule.isExpectationDisabled() ) {
            helper.buildExpirationRule( rule.getLhs(),
                                        expectations.getLabel(),
                                        expectations.getExpectLhs(),
                                        builder );
        }

    }



    public void deMorganizeExpectation( ExpectationDescr descr, CEDescrBuilder<?, ?> argBuilder) {
        PatternDescrBuilder patternBuilder = argBuilder.pattern();
        patternBuilder.id( descr.getLabel(), false );
        patternBuilder.type( Viol.class.getName() );
        patternBuilder.constraint( "label == \"" + descr.getLabel() + "\"", false );
        patternBuilder.constraint( "expId == $context", false );
        patternBuilder.constraint( "compensated == false",false );
    }

    public void deMorganize( BaseDescr descr, CEDescrBuilder<?,?> argBuilder ) throws RuntimeException {
        // XOR BEFORE OR
        if (descr instanceof AndDescr ) {
            deMorganizeAnd( (AndDescr) descr, (CEDescrBuilder<?, OrDescr>) argBuilder );
        } else if ( descr instanceof XorDescr ) {
            deMorganizeXor( (OrDescr) descr, (CEDescrBuilder<?, AndDescr>) argBuilder );
        } else if ( descr instanceof OrDescr) {
            deMorganizeOr( (OrDescr) descr, (CEDescrBuilder<?, AndDescr>) argBuilder );
        } else if ( descr instanceof ExpectationDescr ) {
            deMorganizeExpectation( (ExpectationDescr) descr, argBuilder );
        } else {
            throw new RuntimeException(descr.getClass().getName() + " is not supported in complex expectation expressions");
        }
    }

    public void deMorganizeAnd( AndDescr and, CEDescrBuilder<?, OrDescr> orBuilder ) throws RuntimeException {
        if ( and.getDescrs().size() == 1 ) {
            deMorganize( and.getDescrs().get( 0 ), orBuilder );
        } else {
            CEDescrBuilder orS = orBuilder.or();
            for ( BaseDescr descr : and.getDescrs() ) {
                deMorganize( descr, orS );
            }
        }
    }

    public void deMorganizeOr( OrDescr or, CEDescrBuilder<?, AndDescr> andBuilder ) throws RuntimeException {
        if ( or.getDescrs().size() == 1 ) {
            deMorganize( or.getDescrs().get( 0 ), andBuilder );
        } else {
            CEDescrBuilder andS = andBuilder.and();
            for ( BaseDescr descr : or.getDescrs() ) {
                deMorganize( descr, andS );
            }
        }
    }

    public void deMorganizeXor( OrDescr or, CEDescrBuilder<?, AndDescr> orBuilder ) throws RuntimeException {
        CEDescrBuilder orF = orBuilder.or();
        for ( BaseDescr descr : or.getDescrs() ) {
            deMorganize( descr, orF );
        }
    }




    public void visitExpectation( ExpectationDescr descr, CEDescrBuilder<?,?> argBuilder ) {
        PatternDescrBuilder patternBuilder = argBuilder.pattern();
        patternBuilder.id( descr.getLabel(), false );
        patternBuilder.type( Fulfill.class.getName() );
        patternBuilder.constraint( "label == \"" + descr.getLabel() + "\"", false );
        patternBuilder.constraint( "expId == $context", false );
    }

    public void visit( BaseDescr descr, CEDescrBuilder<?, ?> argBuilder ) throws RuntimeException {
        if ( descr instanceof OrDescr ) {
            visitOr( (OrDescr) descr, (CEDescrBuilder<?, AndDescr>) argBuilder );
        } else if ( descr instanceof AndDescr ) {
            visitAnd( (AndDescr) descr, (CEDescrBuilder<?, OrDescr>) argBuilder );
        } else if ( descr instanceof XorDescr ) {
            visitFork( (XorDescr) descr, (CEDescrBuilder<?, AndDescr>) argBuilder );
        } else if ( descr instanceof ExpectationDescr ) {
            visitExpectation( (ExpectationDescr) descr, argBuilder );
        }  else {
            throw new RuntimeException(descr.getClass().getName() + " is not supported in complex expectation expressions");
        }
    }


    public void visitAnd( AndDescr and, CEDescrBuilder<?, OrDescr> andBuilder ) throws RuntimeException {
        if ( and.getDescrs().size() == 1 ) {
            visit( and.getDescrs().get( 0 ), andBuilder );
        } else {
            CEDescrBuilder andS = andBuilder.and();
            for ( BaseDescr descr : and.getDescrs() ) {
                visit( descr, andS );
            }
        }
    }

    public void visitOr( OrDescr or, CEDescrBuilder<?, AndDescr> orBuilder ) throws RuntimeException {
        if ( or.getDescrs().size() == 1 ) {
            visit( or.getDescrs().get( 0 ), orBuilder );
        } else {
            CEDescrBuilder orS = orBuilder.or();
            for ( BaseDescr descr : or.getDescrs() ) {
                visit( descr, orS );
            }
        }
    }

    public void visitFork(XorDescr xor, CEDescrBuilder<?, AndDescr> andBuilder) throws RuntimeException {
        CEDescrBuilder andS = andBuilder.and();
        for (BaseDescr descr : xor.getDescrs()) {
            visit(descr, andS);
        }
    }


}
