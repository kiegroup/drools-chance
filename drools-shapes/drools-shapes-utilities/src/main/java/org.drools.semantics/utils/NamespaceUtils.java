package org.drools.semantics.utils;


import org.jdom.Namespace;

import java.util.HashMap;
import java.util.Map;

public class NamespaceUtils {

    private static Map<String, Namespace> knownNamespaces;

    static {
        knownNamespaces = new HashMap<String, Namespace>();
        knownNamespaces.put( Namespace.XML_NAMESPACE.getURI(), Namespace.XML_NAMESPACE );
        knownNamespaces.put( Namespace.NO_NAMESPACE.getURI(), Namespace.NO_NAMESPACE );
        knownNamespaces.put( "http://www.w3.org/2002/07/owl", Namespace.getNamespace( "owl", "http://www.w3.org/2002/07/owl" ) );
        knownNamespaces.put( "http://www.w3.org/2001/XMLSchema", Namespace.getNamespace( "xsd", "http://www.w3.org/2001/XMLSchema" ) );
        knownNamespaces.put( "http://www.w3.org/1999/02/22-rdf-syntax-ns", Namespace.getNamespace( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns" ) );
    }

    public static boolean isKnownSchema( String namespace ) {
        return knownNamespaces.containsKey( namespace );
    }

    public static String getPrefix( String namespace ) {
        if ( knownNamespaces.containsKey( namespace ) ) {
            return knownNamespaces.get( namespace ).getPrefix();
        } else {
            return null;
        }
    }

    public static Namespace getNamespaceByPrefix( String prefix ) {
        for ( Namespace ns : knownNamespaces.values() ) {
            if ( ns.getPrefix().equals( prefix ) ) {
                return ns;
            }
        }
        return null;
    }

    public static boolean compareNamespaces( String ns1, String ns2 ) {
        if ( ns1 == null ) {
            return ( ns2 == null );
        }
        if ( ns1 == ns2 ) {
            return true;
        }
        ns1 = removeLastSeparator( ns1 );
        ns2 = removeLastSeparator( ns2 );
        if ( ns1.equals( ns2 ) ) {
            return true;
        }

        return false;
    }

    public static String removeLastSeparator( String ns1 ) {
        if ( ns1.endsWith( "/" ) || ns1.endsWith( "#" ) ) {
            return ns1.substring( 0, ns1.length() - 1 );
        }
        return ns1;
    }


}
