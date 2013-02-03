package org.drools.semantics;


import org.w3._2002._07.owl.ThingImpl;

import java.net.URI;
import java.util.List;

public abstract class AbstractObjectGraphVisitor implements ObjectGraphVisitor {

    public Object visit( Thing root ) {
        return visitRoot( root );
    }

    protected abstract Object visitRoot( Thing root );

    protected void visitNode( Thing node ) {

        preVisitNode( node );

        // At the moment, this visitor should be used only with classes extending ThingImpl
        ThingImpl nodeImpl = (ThingImpl) node;

        visitTypeEdge( node, URI.create( nodeImpl.getSemanticTypeName() ) );

        List<String> properties = nodeImpl.getPropertyNames();
        for ( String prop : properties ) {
            List relateds = nodeImpl.get( prop );
            for ( Object tgt : relateds ) {
                visitRelationEdge(node, prop, tgt);
            }
        }

        postVisitNode( node );
    }

    protected abstract void visitRelationEdge( Thing node, String prop, Object tgt );

    protected abstract void visitTypeEdge( Thing node, URI tgt );

    protected abstract void postVisitNode( Thing node );

    protected abstract void preVisitNode( Thing node );

}
