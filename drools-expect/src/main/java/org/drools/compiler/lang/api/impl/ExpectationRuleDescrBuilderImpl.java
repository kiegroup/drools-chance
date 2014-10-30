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

import org.drools.compiler.lang.api.AnnotationDescrBuilder;
import org.drools.compiler.lang.api.AttributeDescrBuilder;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.ECEConditionalElementDescrBuilder;
import org.drools.compiler.lang.api.ECEDescrBuilder;
import org.drools.compiler.lang.api.ECERuleDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.ExpectationDescr;
import org.drools.compiler.lang.descr.ExpectationRuleDescr;
import org.drools.compiler.lang.descr.RuleDescr;

/**
 * A descr builder for Expectation Rules
 */
public class ExpectationRuleDescrBuilderImpl
        extends BaseDescrBuilderImpl<PackageDescrBuilder, RuleDescr>
        implements ECERuleDescrBuilder {

    public ExpectationRuleDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent,
               new ExpectationRuleDescr() );
    }

    public AnnotationDescrBuilder<RuleDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<RuleDescrBuilder> annotation = new AnnotationDescrBuilderImpl<RuleDescrBuilder>( this,
                                                                                                                name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public AttributeDescrBuilder<RuleDescrBuilder> attribute( String name ) {
        AttributeDescrBuilder<RuleDescrBuilder> attribute = new AttributeDescrBuilderImpl<RuleDescrBuilder>( this,
                                                                                                             name );
        descr.addAttribute( attribute.getDescr() );
        return attribute;
    }

    public RuleDescrBuilder name( String name ) {
        descr.setName( name );
        return this;
    }

    public ECEConditionalElementDescrBuilder<ECERuleDescrBuilder, AndDescr> expectations() {
        ECEConditionalElementDescrBUilderImpl<ECERuleDescrBuilder, AndDescr> exp = new ECEConditionalElementDescrBUilderImpl<ECERuleDescrBuilder, AndDescr>( this, new AndDescr() );
        ((ExpectationRuleDescr) descr).setExpectations( exp.getDescr() );
        return exp;
    }

    public RuleDescrBuilder extendsRule( String name ) {
        descr.setParentName( name );
        return this;
    }

    public RuleDescrBuilder rhs( String rhs ) {
        descr.setConsequence( rhs );
        return this;
    }

    @Override
    public RuleDescrBuilder namedRhs( String name, String rhs ) {
        return null;
    }

    public CEDescrBuilder<RuleDescrBuilder, AndDescr> lhs() {
        CEDescrBuilder<RuleDescrBuilder, AndDescr> ce = new CEDescrBuilderImpl<RuleDescrBuilder, AndDescr>( this,
                                                                                   new AndDescr() );
        descr.setLhs( ce.getDescr() );
        return ce;
    }

    public RuleDescrBuilder attribute( String name,
                                       String value ) {
        descr.addAttribute( new AttributeDescr( name,
                                                value ) );
        return this;
    }

    public RuleDescrBuilder attribute( String name,
                                       String value,
                                       AttributeDescr.Type type ) {
        descr.addAttribute( new AttributeDescr( name,
                                                value,
                                                type ) );
        return this;
    }

    @Override
    public void repairs( String label ) {
        ((ExpectationRuleDescr) descr).repairs( label );
    }
}