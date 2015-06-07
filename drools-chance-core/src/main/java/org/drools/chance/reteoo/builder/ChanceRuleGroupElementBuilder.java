package org.drools.chance.reteoo.builder;

import org.drools.chance.factmodel.Imperfect;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.rule.builder.GroupElementBuilder;
import org.drools.core.rule.GroupElement;


public class ChanceRuleGroupElementBuilder extends GroupElementBuilder {

    protected GroupElement newGroupElementFor( final BaseDescr baseDescr ) {
        Class descr = baseDescr.getClass();
        ChanceGroupElement ge;
        if ( AndDescr.class.isAssignableFrom( descr ) ) {
            ge = ChanceGroupElementFactory.newAndInstance();
        } else if ( OrDescr.class.isAssignableFrom( descr ) ) {
            ge = ChanceGroupElementFactory.newOrInstance();
        } else if ( NotDescr.class.isAssignableFrom( descr ) ) {
            ge = ChanceGroupElementFactory.newNotInstance();
        } else if ( ExistsDescr.class.isAssignableFrom( descr ) ) {
            ge = ChanceGroupElementFactory.newExistsInstance();
        } else {
            throw new UnsupportedOperationException( "BUG: Not able to create a group element for descriptor: " + descr.getName() );
        }

        if ( baseDescr instanceof AnnotatedBaseDescr ) {
            AnnotationDescr meta = ((AnnotatedBaseDescr) baseDescr).getAnnotation( Imperfect.class.getName() );
            ge.initMetadata( meta );
        }

        return ge;
    }

}
