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

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.ECEPackageDescrBuilder;
import org.drools.compiler.lang.api.ECERuleDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.kie.internal.builder.conf.LanguageLevelOption;

/**
 * This is a class to hold all the helper functions/methods used
 * by the DRL parser
 */
public class ECEParserHelper extends ParserHelper {

    private RecognizerSharedState state;

    public ECEParserHelper( TokenStream input,
                            RecognizerSharedState state ) {
        super( input, state, LanguageLevelOption.DRL6 );
        this.state = state;
    }


    @SuppressWarnings("unchecked")
    public <T extends DescrBuilder< ? , ? >> T start( DescrBuilder< ? , ? > ctxBuilder,
                                                      Class<T> clazz,
                                                      String param ) {
        if ( state.backtracking == 0 ) {
            if ( ECERuleDescrBuilder.class.isAssignableFrom( clazz ) ) {
                ECERuleDescrBuilder rule = ctxBuilder == null ?
                                           newExpectationBuilder( ECEPackageDescrBuilderImpl.newECEPackage() ) :
                                           newExpectationBuilder( (ECEPackageDescrBuilder) ctxBuilder );
                pushParaphrases( DroolsParaphraseTypes.RULE );
                beginSentence( DroolsSentenceType.RULE );
                setStart( rule );
                return (T) rule;
            } else {
                return super.start( ctxBuilder, clazz, param );
            }
        }
        return null;
    }

    private ECERuleDescrBuilder newExpectationBuilder( ECEPackageDescrBuilder pdBuilder ) {
        return pdBuilder.newExpectationRule();
    }

}
