/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.lang;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.ECEPackageDescrBuilder;
import org.drools.compiler.lang.api.ECERuleDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.api.PackageDescrBuilder;

public class ECEParser extends DRL6Parser {

    private DRLExpectationParser expectParser;

    public ECEParser( TokenStream input ) {
        super( input );
        this.helper = new ECEParserHelper( input, state );
        this.expectParser = new DRLExpectationParser();
        this.expectParser.initialize( this,
                                      input,
                                      state,
                                      helper );

    }

    /* ------------------------------------------------------------------------------------------------
     *                         RULE STATEMENT
     * ------------------------------------------------------------------------------------------------ */


    public ECEPackageDescrBuilder compile() throws RecognitionException {
        ECEPackageDescrBuilder builder = ECEPackageDescrBuilderImpl.newECEPackage();
        compilationUnit( builder );
        return builder;
    }



    public RuleDescrBuilder ruleHeader(RuleDescrBuilder rule) throws RecognitionException {
        try {
            // 'rule'
            match(input,
                  DRL6Lexer.ID,
                  DroolsSoftKeywords.RULE,
                  null,
                  DroolsEditorType.KEYWORD);
            if (state.failed)
                return null;

            if (helper.validateIdentifierKey(DroolsSoftKeywords.WHEN) ||
                helper.validateIdentifierKey(DroolsSoftKeywords.THEN) ||
                helper.validateIdentifierKey(DroolsSoftKeywords.END)) {
                failMissingTokenException();
                return null; // in case it is backtracking
            }

            String name = stringId();
            if (state.failed)
                return null;
            if (state.backtracking == 0) {
                rule.name(name);
                helper.setParaphrasesValue(DroolsParaphraseTypes.RULE,
                                           "\"" + name + "\"");
                helper.emit(Location.LOCATION_RULE_HEADER);
            }

            if (helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)) {
                // 'extends'
                match(input,
                      DRL6Lexer.ID,
                      DroolsSoftKeywords.EXTENDS,
                      null,
                      DroolsEditorType.KEYWORD);
                if (state.failed)
                    return null;

                String parent = stringId();
                if (state.backtracking == 0)
                    rule.extendsRule(parent);
                if (state.failed)
                    return null;
            }

            if (state.backtracking == 0 && input.LA(1) != DRL6Lexer.EOF) {
                helper.emit(Location.LOCATION_RULE_HEADER);
            }

            while (input.LA(1) == DRL6Lexer.AT) {
                // annotation*
                annotation(rule);
                if (state.failed)
                    return null;
            }

            attributes(rule);

        } catch (RecognitionException re) {
            reportError(re);
        }
        return rule;
    }


    /**
     * rule := rulePremise ( ruleConsequence | ruleExpectations )
     *
     * @return
     * @throws RecognitionException
     */
    public RuleDescr rule( PackageDescrBuilder pkg ) throws RecognitionException {
        boolean isExpectationRule = speculateExpectationRule();

        if ( isExpectationRule ) {

            ECERuleDescrBuilder rule = null;
            if ( state.backtracking == 0 ) {
                rule = helper.start( pkg,
                                     ECERuleDescrBuilder.class,
                                     null );
            }

            ruleHeader( rule );

            if ( helper.validateIdentifierKey( DroolsSoftKeywords.WHEN ) ) {
                rulePremise( rule );
            } else {
                // creates an empty LHS
                rule.lhs();
            }
            ruleExpectations( (ECEPackageDescrBuilder) pkg, rule );

            return (rule != null) ? rule.getDescr() : null;
        } else {
            return super.rule( pkg );
        }
    }

    private void rulePremise( RuleDescrBuilder rule ) throws RecognitionException {
        super.lhs( rule );
    }



    private boolean speculateExpectationRule(  ) {
//        state.backtracking++;
//        int start = input.mark();
//        try {
//            ruleHeader( null );
//            rulePremise( null );
//            ruleExpectations( null );
//        } catch ( Exception re ) {
//            re.printStackTrace();
//        }
//        boolean success = !state.failed;
//        input.rewind( start );
//        state.backtracking--;
//        state.failed = false;
//        return success;
        return true;
    }



    /**
     * ruleExpectations := CHECK expectations END
     *
     * @return
     * @throws RecognitionException
     */
    public ECERuleDescrBuilder ruleExpectations( ECEPackageDescrBuilder packageDescr, ECERuleDescrBuilder rule ) throws RecognitionException {
        try {

            match( input,
                   DRL6Lexer.ID,
                   DroolsSoftKeywords.THEN,
                   null,
                   DroolsEditorType.KEYWORD );

            expectParser.parseExpectations( packageDescr, rule );

            remainingConsequence( rule );

            match( input,
                   DRL6Lexer.ID,
                   DroolsSoftKeywords.END,
                   null,
                   DroolsEditorType.KEYWORD );
            if ( state.failed ) return null;

        } catch ( RecognitionException re ) {
            reportError( re );
        } finally {
            if ( state.backtracking == 0 ) {
                helper.end( RuleDescrBuilder.class,
                            rule );
            }
        }
        return rule;
    }

    private void remainingConsequence( ECERuleDescrBuilder rule ) {
        int first = input.index();

        String chunk = getConsequenceCode(first);

        // remove the "then" keyword and any subsequent spaces and line breaks
        // keep indentation of 1st non-blank line
        chunk = chunk.replaceFirst("^then\\s*\\r?\\n?",
                                   "");
        rule.rhs(chunk);
    }


}
