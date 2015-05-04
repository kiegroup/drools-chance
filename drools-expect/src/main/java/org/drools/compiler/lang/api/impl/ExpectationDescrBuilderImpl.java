/*
 * Copyright 2011 JBoss Inc
 *
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

package org.drools.compiler.lang.api.impl;


import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.api.ECEConditionalElementDescrBuilder;
import org.drools.compiler.lang.api.ECEDescrBuilder;
import org.drools.compiler.lang.api.ECERuleDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.ExpectationDescr;
import org.drools.compiler.lang.descr.ExpectationRuleDescr;

public class ExpectationDescrBuilderImpl<P extends ECEConditionalElementDescrBuilder<?,ExpectationDescr>>
        extends BaseDescrBuilderImpl<P, ExpectationDescr>
        implements ECEDescrBuilder<P> {

    protected ExpectationDescrBuilderImpl( final P parent, final ExpectationDescr descr ) {
        super( parent, descr );
    }

    @Override
    public CEDescrBuilder<?, ?> expectLhs() {
        CEDescrBuilder<ECEDescrBuilder<P>, AndDescr> ce = new CEDescrBuilderImpl<ECEDescrBuilder<P>, AndDescr>( this, new AndDescr() );
        descr.setExpectLhs( ce.getDescr() );
        return ce;
    }

    @Override
    public ECEDescrBuilder<P> label( String label ) {
        descr.setLabel( label );
        return this;
    }

    @Override
    public ECEDescrBuilder<P> one() {
        descr.setMatchOne( true );
        return this;
    }

    @Override
    public ECERuleDescrBuilder expire(PackageDescrBuilder packageDescrBuilder, ExpectationRuleDescr parent) {
        ECERuleDescrBuilder ece = new ExpectationRuleDescrBuilderImpl( packageDescrBuilder );
        ece.name(this.getDescr().getLabel()+"__Expire");
        this.getDescr().setExpired((ExpectationRuleDescr)ece.getDescr());
        for ( AnnotationDescr ad : parent.getAnnotations() ) {
            ece.getDescr().addAnnotation( ad );
        }
        return ece;
    }

    @Override
    public ECERuleDescrBuilder fulfill( PackageDescrBuilder packageDescrBuilder, ExpectationRuleDescr parent ) {
        ECERuleDescrBuilder ece = new ExpectationRuleDescrBuilderImpl( packageDescrBuilder );
        this.getDescr().setFulfill( (ExpectationRuleDescr) ece.getDescr() );
        ece.name( this.getDescr().getLabel() + "__Fulfill" );
        for ( AnnotationDescr ad : parent.getAnnotations() ) {
            ece.getDescr().addAnnotation( ad );
        }
        return ece;
    }

    @Override
    public ECERuleDescrBuilder violation( PackageDescrBuilder packageDescrBuilder, ExpectationRuleDescr parent ) {
        ECERuleDescrBuilder ece = new ExpectationRuleDescrBuilderImpl( packageDescrBuilder );
        this.getDescr().setViolation( (ExpectationRuleDescr) ece.getDescr() );
        ece.name( this.getDescr().getLabel() + "__Violation" );
        for ( AnnotationDescr ad : parent.getAnnotations() ) {
            ece.getDescr().addAnnotation( ad );
        }
        return ece;
    }

}