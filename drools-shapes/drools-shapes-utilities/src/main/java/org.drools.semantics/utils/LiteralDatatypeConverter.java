package org.drools.semantics.utils;

import org.drools.semantics.Literal;

import java.net.URI;
import java.net.URISyntaxException;

public class LiteralDatatypeConverter {

    public static Literal parseLiteral( String lit ) {
        return new Literal( lit );
    }

    public static String printLiteral( Literal lit ) {
        return lit.toString();
    }

    public static URI parseURI( String uri ) {
        try {
            return new URI( uri );
        } catch ( URISyntaxException e ) {
            throw new UnsupportedOperationException( "Drools Shapes requires xsd:anyURI to be mapped to java.net.URI. Contact the developer if this is considered an improper restriction." );
        }
    }

    public static String printURI( URI uri ) {
        return uri.toString();
    }
}
