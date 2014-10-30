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

package org.drools.compiler.lang.descr;

import it.unibo.deis.lia.org.drools.expectations.Expect;

import java.util.ArrayList;
import java.util.List;

public class ExpectationRuleDescr extends RuleDescr {

    private ConditionalElementDescr expectations;

    private List<String> repairs;

    public ConditionalElementDescr getExpectations() {
        return expectations;
    }

    public void setExpectations( ConditionalElementDescr expectations ) {
        this.expectations = expectations;
    }

    public void repairs( String label ) {
        getRepairs().add( label );
    }

    public List<String> getRepairs() {
        if ( repairs == null ) {
            repairs = new ArrayList();
        }
        return repairs;
    }

    public boolean isExpectationDisabled() {
        AnnotationDescr expect = getAnnotation( Expect.class );
        if ( expect == null ) {
            expect = getAnnotation( Expect.class.getSimpleName() );
        }
        if ( expect != null ) {
            Object val = expect.getValue( "enabled" );
            if ( val != null ) {
                return ! Boolean.parseBoolean( val.toString() );
            }
        }
        return false;
    }
}
