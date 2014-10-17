package org.drools.chance.reteoo.builder;

import org.drools.RuntimeDroolsException;
import org.drools.chance.factmodel.Imperfect;
import org.drools.lang.descr.*;
import org.drools.rule.GroupElement;
import org.drools.rule.builder.GroupElementBuilder;


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
            throw new RuntimeDroolsException( "BUG: Not able to create a group element for descriptor: " + descr.getName() );
        }

        if ( baseDescr instanceof AnnotatedBaseDescr ) {
            AnnotationDescr meta = ((AnnotatedBaseDescr) baseDescr).getAnnotation( Imperfect.class.getSimpleName() );
            ge.initMetadata( meta );
        }

        return ge;
    }

}
