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

package org.drools.compiler.lang;


import it.unibo.deis.lia.org.drools.expectations.model.Expectation;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.lang.api.*;
import org.drools.compiler.lang.api.impl.ExpectationDescrBuilderImpl;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExpectationRuleDescr;
import it.unibo.deis.lia.org.drools.expectations.DRLExpectationHelper;
import org.drools.compiler.lang.descr.PatternDescr;

import java.util.List;

public class DRLExpectationParser  {


    // NEED ACCESS TO THE MAIN PARSER STATE
    // PLUS : match(), label() and lhsPattern() methods now in package scope

    private DRL6Parser parser;
    private DRLExpectationHelper expectHelper;
    private TokenStream                     input;
    private RecognizerSharedState           state;
    private ParserHelper helper;



    public DRLExpectationParser() {
        this.expectHelper = new DRLExpectationHelper();
    }

    public void initialize( DRL6Parser parser, TokenStream input, RecognizerSharedState state, ParserHelper helper ) {
        this.parser = parser;
        this.state = state;
        this.helper = helper;
        this.input = input;
    }



    /**
     * expectationExtension := expectations
     * @param rule
     * @return
     */
    public void parseExpectations( ECEPackageDescrBuilder packageDescr, ECERuleDescrBuilder rule ) throws RecognitionException {
        try {
            expectations( packageDescr, rule, rule.expectations() );
        } catch ( RecognitionException re ) {
            return;
        }

    }


    /**
     * expectations := expectationOr
     *
     * @return
     */
    private void expectations( ECEPackageDescrBuilder packageDescr, ECERuleDescrBuilder rule, ECEConditionalElementDescrBuilder expectationRoot ) throws RecognitionException {
        while ( isExpectationNext() ) {
            try {

                expectationOr( packageDescr, rule, expectationRoot );

            } catch ( RecognitionException e ) {
                e.printStackTrace();
            }

            if ( state.failed ) {
                return;
            }
        }
    }


    /**
     * expectationOr := expectationAnd ( COMMA expectationAnd) *
     *
     *
     *
     *
     * @param rule
     */
    private void expectationOr( ECEPackageDescrBuilder packageDescr, ECERuleDescrBuilder rule, ECEConditionalElementDescrBuilder<?, ?> and ) throws RecognitionException {
        ECEConditionalElementDescrBuilder eceOr = (ECEConditionalElementDescrBuilder) and.or();
        
        expectationAnd( packageDescr, rule, eceOr );
        if ( state.failed ) return;

        if ( helper.validateIdentifierKey( DroolsSoftKeywords.OR ) ) {
            while ( helper.validateIdentifierKey( DroolsSoftKeywords.OR ) ) {

                parser.match(input,
                        DRL6Lexer.ID,
                        DroolsSoftKeywords.OR,
                        null,
                        DroolsEditorType.KEYWORD);

                if ( state.failed ) return;
                if ( state.backtracking == 0 && input.LA( 1 ) != DRL6Lexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                }

                expectationAnd( packageDescr, rule, eceOr );
                if ( state.failed ) return;
            }
        }

    }

    /**
     * expectationAnd := expectation ( COMMA, expectation )*
     *
     *
     *
     *
     *
     * @param rule
     */
    private void expectationAnd( ECEPackageDescrBuilder packageDescr, ECERuleDescrBuilder rule, ECEConditionalElementDescrBuilder<?, AndDescr> or ) throws RecognitionException {
        ECEConditionalElementDescrBuilder eceAnd = (ECEConditionalElementDescrBuilder) or.and();

        expectation(packageDescr, rule, eceAnd);

        if ( state.failed ) return;

        if ( helper.validateIdentifierKey( DroolsSoftKeywords.AND ) ) {
            while ( helper.validateIdentifierKey( DroolsSoftKeywords.AND ) ) {

                parser.match(input,
                        DRL6Lexer.ID,
                        DroolsSoftKeywords.AND,
                        null,
                        DroolsEditorType.KEYWORD);

                if ( state.failed ) return;

                if ( state.backtracking == 0 && input.LA( 1 ) != DRL6Lexer.EOF ) {
                    helper.emit( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                }

                expectation(packageDescr, rule, eceAnd);

                if ( state.failed ) return;
            }
        }

    }


    /**
     * expectation := label? 'expect' ( 'one' | 'not' )? label? lhsPattern onFulfill? onViolation?
     *
     *
     *
     *
     * @param rule
     *@param and  @return
     */
    private String expectation( ECEPackageDescrBuilder packageDescr, ECERuleDescrBuilder rule, ECEConditionalElementDescrBuilder<?, AndDescr> and ) {
        ECEDescrBuilder expectDescr = and.expect();
        
        String expLabel = "";
        try {

            if ( and != null ) {
                if (input.LA( 1 ) == DRL6Lexer.ID && input.LA( 2 ) == DRL6Lexer.COLON) {
                    expLabel = parser.label(DroolsEditorType.IDENTIFIER_PATTERN);
                } else {
                    expLabel = expectHelper.nextLabel( rule.getDescr().getName().trim() );
                }
            }
            expectDescr.label( expLabel );

            // consume "expect" keyword
            Token t = parser.match(input,
                    DRL6Lexer.ID,
                    ExpectationSoftKeywords.EXPECT,
                    null,
                    DroolsEditorType.KEYWORD);

            boolean negated = false;
            if (helper.validateIdentifierKey(ExpectationSoftKeywords.ONE)) {
                parser.match(input,
                        DRL6Lexer.ID,
                        ExpectationSoftKeywords.ONE,
                        null,
                        DroolsEditorType.KEYWORD);
                expectDescr.one();
            } else {
                if (helper.validateIdentifierKey( DroolsSoftKeywords.NOT )) {
                    parser.match(input,
                            DRL6Lexer.ID,
                            DroolsSoftKeywords.NOT,
                            null,
                            DroolsEditorType.KEYWORD);
                    negated = true;
                }
            }

            String label = null;
            boolean contained = false;
            if ( input.LA( 1 ) == DRL6Lexer.ID && input.LA( 2 ) == DRL6Lexer.COLON && !helper.validateCEKeyword( 1 ) ) {
                label = parser.label(DroolsEditorType.IDENTIFIER_PATTERN);
                if ( state.failed ) return null;
            } else if (helper.validateCEKeyword(1)) {
                if (helper.validateIdentifierKey( DroolsSoftKeywords.FORALL)) {
                    contained = true;
                }
            }

            if ( state.backtracking == 0 ) {
                if (contained) {
                    parser.lhsForall(expectDescr.expectLhs());
                } else {
                    parser.lhsPattern(negated ? expectDescr.expectLhs().not().pattern() : expectDescr.expectLhs().pattern(), label, false);
                }
            } else {
                parser.lhsPattern(null, label, false);
            }

            failsOn(packageDescr, expectDescr, (ExpectationRuleDescr) rule.getDescr() );

            onFulfill( packageDescr, expectDescr, (ExpectationRuleDescr) rule.getDescr() );

            onViolation( packageDescr, expectDescr, (ExpectationRuleDescr) rule.getDescr() );

        } catch (RecognitionException e) {
            e.printStackTrace();
        }

        return expLabel;

    }

    public void failsOn( ECEPackageDescrBuilder packageDescr, ECEDescrBuilder expect, ExpectationRuleDescr parentRule ) throws RecognitionException {
        if ( helper.validateIdentifierKey( ExpectationSoftKeywords.FAILSON)) {
            // consume "failsOn" keyword
            parser.match(input,
                    DRL6Lexer.ID,
                    ExpectationSoftKeywords.FAILSON,
                    null,
                    DroolsEditorType.KEYWORD);
            if ( state.failed ) return;
            String label = null;
            ECERuleDescrBuilder expireRule = expect.failsOn(packageDescr, parentRule);
            parser.lhsPattern(expireRule.lhs().pattern(), label, false);
        }
    }


    /**
     * onFulfill := 'onFulfill' curlyRHS
     *
     * @throws RecognitionException
     * @param expect
     */
    public void onFulfill( ECEPackageDescrBuilder packageDescr, ECEDescrBuilder expect, ExpectationRuleDescr parentRule ) throws RecognitionException {
        boolean hasExplicitFulfill = false;
        if ( helper.validateIdentifierKey( ExpectationSoftKeywords.ONFULFILL ) ) {

            parser.match( input,
                    DRL6Lexer.ID,
                    ExpectationSoftKeywords.ONFULFILL,
                    null,
                    DroolsEditorType.KEYWORD );
            if ( state.failed ) return;
            hasExplicitFulfill = true;

            curlyRhs( packageDescr, expect.fulfill( packageDescr, parentRule ) );
        } else {
            expect.fulfill( packageDescr, parentRule );
        }
    }


    /**
     * onViolation := 'onViolation' curlyRHS?
     *
     *
     * @param expect
     * @throws RecognitionException
     */
    public void onViolation( ECEPackageDescrBuilder packageDescr, ECEDescrBuilder expect, ExpectationRuleDescr parentRule ) throws RecognitionException {
           // build violation expect
        boolean hasExplicitViolation = false;
        if ( helper.validateIdentifierKey( ExpectationSoftKeywords.ONVIOLATION ) ) {

            parser.match( input,
                    DRL6Lexer.ID,
                    ExpectationSoftKeywords.ONVIOLATION,
                    null,
                    DroolsEditorType.KEYWORD );
            if ( state.failed ) return;

            hasExplicitViolation = true;

            curlyRhs( packageDescr, expect.violation( packageDescr, parentRule ) );
        } else {
            expect.violation( packageDescr, parentRule );
        }
    }



    /**
     * curlyRHS ;= LEFT_CURLY ( expectations | compensations | ~RIGHT_CURLY )* RIGHT_CURLY
     *
     * @param rule
     * @return
     * @throws RecognitionException
     */
    private String curlyRhs( ECEPackageDescrBuilder packageDescr, ECERuleDescrBuilder rule ) throws RecognitionException {

        try {

            Token t = parser.match(input,
                    DRL6Lexer.LEFT_CURLY,
                    null,
                    null,
                    DroolsEditorType.SYMBOL);
            if ( state.failed ) return "";

            StringBuilder chunk = new StringBuilder();
            String xrhs = "";


            if ( state.backtracking == 0 ) {
                rule.getDescr().setConsequenceLocation( t.getLine(),
                        t.getCharPositionInLine() );
                helper.emit( Location.LOCATION_RHS );
            }

            Token tok;
            while ( input.LA( 1 ) != DRL6Lexer.EOF && input.LA( 1 ) != DRL6Lexer.RIGHT_CURLY ) {

                if ( isExpectationNext() ) {
                    expectations( packageDescr, rule, rule.expectations() );
                } else if ( isRepairNext() ) {
                    compensations( rule );
                } else {
                    tok = input.LT(1);
                    input.consume();
                    chunk.append( tok.getText() );
                }

            }

            if ( state.backtracking == 0 ) {
                rule.rhs( chunk.toString() );
            }

            parser.match(input,
                    DRL6Lexer.RIGHT_CURLY,
                    null,
                    null,
                    DroolsEditorType.SYMBOL);

            return xrhs;

        } catch ( RecognitionException re ) {
            parser.reportError(re);
        }
        return "";
    }


    /**
     * compensations := compensation*
     *
     * @param rule
     */
    private void compensations( ECERuleDescrBuilder rule) {
        while ( helper.validateIdentifierKey( ExpectationSoftKeywords.REPAIR ) ) {
            compensation(rule);
        }
    }

    /**
     * compensation := 'repair' ID
     *
     * @param ruleDescrBuilder
     */
    private void compensation( ECERuleDescrBuilder ruleDescrBuilder ) {
        try {
            parser.match(input,
                    DRL6Lexer.ID,
                    ExpectationSoftKeywords.REPAIR,
                    null,
                    DroolsEditorType.KEYWORD);

            Token violId = parser.match(input,
                    DRL6Lexer.ID,
                    null,
                    null,
                    DroolsEditorType.IDENTIFIER_PATTERN);

            ruleDescrBuilder.repairs( violId.getText() );

            parser.match(input,
                    DRL6Lexer.SEMICOLON,
                    null,
                    null,
                    DroolsEditorType.SYMBOL);

        } catch (RecognitionException re) {
            re.printStackTrace();
        }
    }





    boolean isExpectationNext() {
        return helper.validateIdentifierKey( ExpectationSoftKeywords.EXPECT ) || (input.LA( 1 ) == DRL6Lexer.ID && input.LA( 2 ) == DRL6Lexer.COLON);
    }

    boolean isRepairNext() {
        return helper.validateIdentifierKey( ExpectationSoftKeywords.REPAIR );
    }






}
