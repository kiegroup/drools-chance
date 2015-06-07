package org.drools.chance.reteoo.builder;

import org.drools.chance.factmodel.Imperfect;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;


public class ChanceGroupElement extends GroupElement {

    private AnnotationDefinition metadata;

    public ChanceGroupElement() {

    }

    public ChanceGroupElement( Type type ) {
        super( type );
    }

    public void initMetadata( AnnotationDescr imp ) {
        if ( imp == null ) { return; }
        if ( Imperfect.class.getSimpleName().equals( imp.getName() ) ) {
            metadata = new AnnotationDefinition( Imperfect.class.getSimpleName() );
            for ( String key : imp.getValues().keySet() ) {
                metadata.getValues().put( key, new AnnotationDefinition.AnnotationPropertyVal( key, null, imp.getValue( key ), null ) );
            }
        }
    }

    public AnnotationDefinition getMetadata() {
        return metadata;
    }

    protected GroupElement clone( boolean deepClone ) {
        ChanceGroupElement cloned = new ChanceGroupElement( this.getType() );
        for ( RuleConditionElement re : getChildren() ) {
            cloned.addChild( deepClone && ( re instanceof GroupElement || re instanceof Pattern ) ? re.clone() : re );
        }

        cloned.metadata = this.metadata;
        return cloned;
    }

    protected void mergeGroupElements( GroupElement parent, GroupElement child ) {
        super.mergeGroupElements( parent, child );
        ((ChanceGroupElement) parent).metadata = ((ChanceGroupElement) child).metadata;
    }
}
