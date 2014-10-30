package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.api.ECEConditionalElementDescrBuilder;
import org.drools.compiler.lang.api.ECEDescrBuilder;
import org.drools.compiler.lang.api.ECERuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.ExpectationDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.core.rule.ConditionalElement;

public class ECEConditionalElementDescrBUilderImpl<P extends DescrBuilder< ? , ? >,T extends AnnotatedBaseDescr>
        extends CEDescrBuilderImpl<P,T>
        implements ECEConditionalElementDescrBuilder<P,T> {

    public ECEConditionalElementDescrBUilderImpl( P parent, T descr ) {
        super( parent, descr );
    }

    public CEDescrBuilder<CEDescrBuilder<P, T>, OrDescr> or() {
        OrDescr orDescr = new OrDescr();
        ((ConditionalElementDescr) descr).addDescr( orDescr );
        return new ECEConditionalElementDescrBUilderImpl<CEDescrBuilder<P,T>, OrDescr>( this, orDescr );
    }

    public CEDescrBuilder<CEDescrBuilder<P, T>, AndDescr> and() {
        AndDescr andDescr = new AndDescr();
        ((ConditionalElementDescr) descr).addDescr( andDescr );
        return new ECEConditionalElementDescrBUilderImpl<CEDescrBuilder<P,T>, AndDescr>( this, andDescr );
    }

    @Override
    public ECEDescrBuilder expect() {
        ExpectationDescr expectationDescr = new ExpectationDescr();
        ((ConditionalElementDescr) descr).addDescr( expectationDescr );
        return new ExpectationDescrBuilderImpl( this, expectationDescr );
    }
}
