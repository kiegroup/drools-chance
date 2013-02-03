/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License" ); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.semantics.utils;

import javax.xml.ws.Holder;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public final class NameUtils {


    public enum IdentifierType {
        CLASS,
        INTERFACE,
        GETTER,
        SETTER,
        VARIABLE,
        CONSTANT
    };

    public static final String JAXB_URI = "http://java.sun.com/xml/ns/jaxb";


    private static final char[] XML_NAME_PUNCTUATION_CHARS = new char[] {
            /* hyphen                       */ '\u002D',
            /* period                       */ '\u002E',
            /* colon                        */'\u003A',
            /* dot                          */ '\u00B7',
            /* greek ano teleia             */ '\u0387',
            /* arabic end of ayah           */ '\u06DD',
            /* arabic start of rub el hizb  */ '\u06DE',
            /* underscore                   */ '\u005F',
    };

    private static final String XML_NAME_PUNCTUATION_STRING = new String(XML_NAME_PUNCTUATION_CHARS);

    private static final Map<String, String> BUILTIN_DATATYPES_MAP;
    private static final Map<String, Class<?>> HOLDER_TYPES_MAP;

    private static final String NEG = "ObjectComplementOf";


    static {
        BUILTIN_DATATYPES_MAP = new HashMap<String, String>();
        BUILTIN_DATATYPES_MAP.put( "string", "java.lang.String" );
        BUILTIN_DATATYPES_MAP.put( "integer", "java.math.BigInteger" );
        BUILTIN_DATATYPES_MAP.put( "int", "int" );
        BUILTIN_DATATYPES_MAP.put( "long", "long" );
        BUILTIN_DATATYPES_MAP.put( "short", "short" );
        BUILTIN_DATATYPES_MAP.put( "decimal", "java.math.BigDecimal" );
        BUILTIN_DATATYPES_MAP.put( "float", "float" );
        BUILTIN_DATATYPES_MAP.put( "double", "double" );
        BUILTIN_DATATYPES_MAP.put( "boolean", "boolean" );
        BUILTIN_DATATYPES_MAP.put( "byte", "byte" );
        BUILTIN_DATATYPES_MAP.put( "QName", "javax.xml.namespace.QName" );
        BUILTIN_DATATYPES_MAP.put( "dateTime", "javax.xml.datatype.XMLGregorianCalendar" );
        BUILTIN_DATATYPES_MAP.put( "base64Binary", "byte[]" );
        BUILTIN_DATATYPES_MAP.put( "hexBinary", "byte[]" );
        BUILTIN_DATATYPES_MAP.put( "unsignedInt", "long" );
        BUILTIN_DATATYPES_MAP.put( "unsignedShort", "short" );
        BUILTIN_DATATYPES_MAP.put( "unsignedByte", "byte" );
        BUILTIN_DATATYPES_MAP.put( "time", "javax.xml.datatype.XMLGregorianCalendar" );
        BUILTIN_DATATYPES_MAP.put( "date", "javax.xml.datatype.XMLGregorianCalendar" );
        BUILTIN_DATATYPES_MAP.put( "gYear", "javax.xml.datatype.XMLGregorianCalendar" );
        BUILTIN_DATATYPES_MAP.put( "gYearMonth", "javax.xml.datatype.XMLGregorianCalendar" );
        BUILTIN_DATATYPES_MAP.put( "gMonth", "javax.xml.datatype.XMLGregorianCalendar" );
        BUILTIN_DATATYPES_MAP.put( "gMonthDay", "javax.xml.datatype.XMLGregorianCalendar" );
        BUILTIN_DATATYPES_MAP.put( "gDay", "javax.xml.datatype.XMLGregorianCalendar" );
        BUILTIN_DATATYPES_MAP.put( "duration", "javax.xml.datatype.Duration" );
        BUILTIN_DATATYPES_MAP.put( "NOTATION", "javax.xml.namespace.QName" );

        HOLDER_TYPES_MAP = new HashMap<String, Class<?>>();
        HOLDER_TYPES_MAP.put( "int", java.lang.Integer.class );
        HOLDER_TYPES_MAP.put( "long", java.lang.Long.class );
        HOLDER_TYPES_MAP.put( "short", java.lang.Short.class );
        HOLDER_TYPES_MAP.put( "float", java.lang.Float.class );
        HOLDER_TYPES_MAP.put( "double", java.lang.Double.class );
        HOLDER_TYPES_MAP.put( "boolean", java.lang.Boolean.class );
        HOLDER_TYPES_MAP.put( "byte", java.lang.Byte.class );
    }

    /** Use this character as suffix */
    static final char KEYWORD_PREFIX = '_';

    /**
     * These are java keywords as specified at the following URL.
     * http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#229308
     * Note that false, true, and null are not strictly keywords; they are
     * literal values, but for the purposes of this array, they can be treated
     * as literals.
     */
    private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch",
            "char", "class", "const", "continue", "default", "do", "double",
            "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto",
            "if", "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true", "try",
            "void", "volatile", "while"
    ));




    private static NameUtils instance;

    public static NameUtils getInstance() {
        if ( instance == null ) {
            instance = new NameUtils();
        }
        return instance;
    }

    private NameUtils() {

    }


    /**
     * checks if the input string is a valid java keyword.
     *
     * @return boolean true/false
     */
    public static boolean isJavaKeyword(String keyword) {
        return KEYWORDS.contains(keyword);
    }

    /**
     * Turn a java keyword string into a non-Java keyword string. (Right now
     * this simply means appending an underscore.)
     */
    public static String makeNonJavaKeyword(String keyword) {
        return KEYWORD_PREFIX + keyword;
    }






    public static String builtInTypeToJavaType( String type ) {
        return BUILTIN_DATATYPES_MAP.get( type );
    }

    public static Class<?> holderClass( String type ) {
        return HOLDER_TYPES_MAP.get( type );
    }



    public static String buildFQNameFromIri( String iri ) {
        return nameToIdentifier( iri, IdentifierType.VARIABLE );
    }

    public static String buildLowCaseNameFromIri( String iri ) {
        return nameToIdentifier( iri, IdentifierType.VARIABLE );
    }

    public static String buildNameFromIri( String iriStart, String iriFragment ) {
        String iri = iriFragment != null ? iriFragment : iriStart;
        return nameToIdentifier( iri, IdentifierType.CLASS );
    }

    public static String separatingName( String name ) {
        return name.endsWith( "/" ) || name.endsWith( "#" ) ? name : ( name + "#" );
    }


    public static URI packageToNamespaceURI( String pack ) {
        return URI.create( "http://" + pack.replace( ".", "/" ) );
    }


    /**
     * Generates a Java package name from a URI according to the
     * algorithm outlined in JAXB 2.0.
     *
     * @param namespaceURI the namespace URI.
     * @return the package name.
     */
    public static String namespaceURIToPackage( String namespaceURI ) {
        try {
            return nameSpaceURIToPackage( new URI( namespaceURI ) );
        } catch ( URISyntaxException ex ) {
            return null;
        }
    }

    /**
     * Generates a Java package name from a URI according to the
     * algorithm outlined in JAXB 2.0.
     *
     * @param uri the namespace URI.
     * @return the package name.
     */
    public static String nameSpaceURIToPackage( URI uri ) {

        StringBuffer packageName = new StringBuffer();
        String authority = uri.getAuthority();
        if ( authority == null && "urn".equals( uri.getScheme() ) ) {
            authority = uri.getSchemeSpecificPart();
        }

        if ( null != authority && ! "".equals( authority ) ) {
            if ( "urn".equals( uri.getScheme() ) ) {
                packageName.append( authority );
                for ( int i = 0; i < packageName.length(); i++ ) {
                    if ( packageName.charAt( i ) == '-' ) {
                        packageName.setCharAt( i, '.' );
                    }
                }
                authority = packageName.toString();
                packageName.setLength( 0 );

                StringTokenizer st = new StringTokenizer( authority, ":" );
                while ( st.hasMoreTokens() ) {
                    String token = st.nextToken();
                    if ( packageName.length() > 0 ) {
                        packageName.insert( 0, "." );
                        packageName.insert( 0, normalizePackageNamePart( token ) );
                    } else {
                        packageName.insert( 0, token );
                    }
                }
                authority = packageName.toString();
                packageName.setLength( 0 );

            }

            StringTokenizer st = new StringTokenizer( authority, "." );
            if ( st.hasMoreTokens() ) {
                String token = null;
                while ( st.hasMoreTokens() ) {
                    token = st.nextToken();
                    if ( packageName.length() == 0 ) {
                        if ( "www".equals( token ) ) {
                            continue;
                        }
                    } else {
                        packageName.insert( 0, "." );
                    }
                    packageName.insert( 0, normalizePackageNamePart( token ) );
                }

            }
        }

        String path = uri.getPath();
        if ( path == null ) {
            path = "";
        }
        int index = path.lastIndexOf( '.' );
        if ( index < 0 ) {
            index = path.length();
        }
        StringTokenizer st = new StringTokenizer(path.substring( 0, index ), "/" );
        while ( st.hasMoreTokens() ) {
            String token = st.nextToken();
            if ( packageName.length() > 0 ) {
                packageName.append( '.' );
            }
            packageName.append( normalizePackageNamePart( token ) );
        }
        return packageName.toString();
    }

    private static String normalizePackageNamePart( String name ) {
        StringBuffer sname = new StringBuffer( name.toLowerCase() );

        for ( int i = 0; i < sname.length(); i++ ) {
            sname.setCharAt( i, Character.toLowerCase( sname.charAt( i ) ) );
        }

        for ( int i = 0; i < sname.length(); i++ ) {
            if ( ! Character.isJavaIdentifierPart( sname.charAt( i ) ) ) {
                sname.setCharAt( i, '_' );
            }
        }

        if ( isJavaKeyword( sname.toString() ) ) {
            sname.insert( 0, '_' );
        }

        if ( ! Character.isJavaIdentifierStart( sname.charAt( 0 ) ) ) {
            sname.insert( 0, '_' );
        }

        return sname.toString();
    }


    /**
     * Converts an XML name to a Java identifier according to the mapping
     * algorithm outlines in the JAXB specification
     *
     * @param name the XML name
     * @return the Java identifier
     */
    public static String nameToIdentifier( String name, IdentifierType type ) {

        if ( null == name || name.length() == 0 ) {
            return name;
        }

        // algorithm will not change an XML name that is already a legal and
        // conventional (!) Java class, method, or constant identifier

        boolean legalIdentifier = false;
        StringBuffer buf = new StringBuffer( name );
        legalIdentifier = Character.isJavaIdentifierStart( buf.charAt( 0 ) );

        for ( int i = 1; i < name.length() && legalIdentifier; i++ ) {
            legalIdentifier = legalIdentifier && Character.isJavaIdentifierPart( buf.charAt( i ) );
        }

        boolean conventionalIdentifier = isConventionalIdentifier( buf, type );
        if ( legalIdentifier && conventionalIdentifier ) {
            if ( NameUtils.isJavaKeyword( name ) && type == IdentifierType.VARIABLE ) {
                name = normalizePackageNamePart( name.toString() );
            }
            return name;
        }

        // split into words

        List<String> words = new ArrayList<String>();

        StringTokenizer st = new StringTokenizer( name, XML_NAME_PUNCTUATION_STRING );
        while ( st.hasMoreTokens() ) {
            words.add( st.nextToken() );
        }


        for ( int i = 0; i < words.size(); i++ ) {
            splitWord( words, i );
        }

        return makeConventionalIdentifier( words, type );

    }

    private static void splitWord( List<String> words, int listIndex ) {
        String word = words.get( listIndex );
        if ( word.length() <= 1 ) {
            return;
        }
        int index = listIndex + 1;
        StringBuffer sword = new StringBuffer( word );
        int first = 0;
        char firstChar = sword.charAt( first );
        if ( Character.isLowerCase( firstChar ) ) {
            sword.setCharAt( first, Character.toUpperCase( firstChar ) );
        }
        int i = 1;

        while ( i < sword.length() ) {
            if ( Character.isDigit( firstChar ) ) {
                while ( i < sword.length() && Character.isDigit( sword.charAt( i ) ) ) {
                    i++;
                }
            } else if ( isCasedLetter( firstChar ) ) {
                boolean previousIsLower = Character.isLowerCase( firstChar );
                while ( i < sword.length() && isCasedLetter( sword.charAt( i ) ) ) {
                    if ( Character.isUpperCase( sword.charAt( i ) ) && previousIsLower ) {
                        break;
                    }
                    previousIsLower = Character.isLowerCase( sword.charAt( i ) );
                    i++;
                }
            } else {
                // first must be a mark or an uncased letter
                while ( i < sword.length() && ( isMark( sword.charAt( i ) ) || ! isCasedLetter( sword.charAt( i ) ) ) ) {
                    i++;
                }
            }

            // characters from first to i are all either
            // * digits
            // * upper or lower case letters, with only the first one an upper
            // * uncased letters or marks


            String newWord = sword.substring( first, i );
            words.add( index, newWord );
            index++;
            if ( i >= sword.length() ) {
                break;
            } else {
                first = i;
                firstChar = sword.charAt( first );
            }
        }

        if ( index > ( listIndex + 1 ) ) {
            words.remove( listIndex );
        }
    }

    private static boolean isMark( char c ) {
        return Character.isJavaIdentifierPart( c ) && ! Character.isLetter( c ) && ! Character.isDigit( c );
    }

    private static boolean isCasedLetter( char c ) {
        return Character.isUpperCase( c ) || Character.isLowerCase( c );
    }

    private static boolean isConventionalIdentifier( StringBuffer buf, IdentifierType type ) {
        if ( null == buf || buf.length() == 0 ) {
            return false;
        }
        boolean result = false;
        if (IdentifierType.CONSTANT == type) {
            for (int i = 0; i < buf.length(); i++) {
                if (Character.isLowerCase(buf.charAt(i))) {
                    return false;
                }
            }
            result = true;
        } else if (IdentifierType.VARIABLE == type) {
            result = Character.isLowerCase(buf.charAt(0));
        } else {
            int pos = 3;
            if (IdentifierType.GETTER == type
                    && !(buf.length() >= pos
                    && "get".equals(buf.subSequence(0, 3)))) {
                return false;
            } else if (IdentifierType.SETTER == type
                    && !(buf.length() >= pos && "set".equals(buf.subSequence(0, 3)))) {
                return false;
            } else {
                pos = 0;
            }
            result = Character.isUpperCase(buf.charAt(pos));
        }
        return result;
    }

    private static String makeConventionalIdentifier( List<String> words, IdentifierType type ) {
        StringBuffer buf = new StringBuffer();
        boolean firstWord = true;
        if ( IdentifierType.GETTER == type ) {
            buf.append( "get" );
        } else if ( IdentifierType.SETTER == type ) {
            buf.append( "set" );
        }
        for ( String w : words ) {
            int l = buf.length();
            if ( l > 0 && IdentifierType.CONSTANT == type ) {
                buf.append( '_' );
                l++;
            }
            buf.append( w );
            if ( IdentifierType.CONSTANT == type ) {
                for ( int i = l; i < buf.length(); i++ ) {
                    if ( Character.isLowerCase( buf.charAt( i ) ) ) {
                        buf.setCharAt( i, Character.toUpperCase( buf.charAt( i ) ) );
                    }
                }
            } else if ( IdentifierType.VARIABLE == type ) {
                if ( firstWord && Character.isUpperCase( buf.charAt( l ) ) ) {
                    buf.setCharAt( l, Character.toLowerCase( buf.charAt( l ) ) );
                }
            } else {
                if ( firstWord && Character.isLowerCase( buf.charAt( l ) ) ) {
                    buf.setCharAt( l, Character.toUpperCase( buf.charAt( l ) ) );
                }
            }
            firstWord = false;
        }
        return buf.toString();
    }

    public static Class<?> getValidClass( Class<?> cls ) {
        if ( cls.isEnum() ) {
            return cls;
        }
        if ( cls.isArray() ) {
            return cls;
        }

        if ( cls == Object.class || cls == String.class || cls == Holder.class ) {
            cls = null;
        } else if ( cls.isPrimitive() || cls.isAnnotation() ) {
            cls = null;
        } else if ( cls.isInterface() ) {
            return cls;
        }
        if ( cls != null ) {
            if ( cls.getName().equals("javax.xml.ws.wsaddressing.W3CEndpointReference") ) {
                return cls;
            }
            try {
                if ( cls.getConstructor( new Class[0] ) == null ) {
                    cls = null;
                }
            } catch ( NoSuchMethodException ex ) {
                cls = null;
            }
        }
        return cls;
    }


    public static String capitalize( String s ) {
        return Character.toUpperCase(s.charAt(0)) + s.substring( 1 );
    }

    public static String reverse(String aPackage) {


        StringTokenizer tok = new StringTokenizer( aPackage, "." );
        String ans = tok.nextToken();

        while ( tok.hasMoreTokens() ) {
            ans = tok.nextToken() + "." + ans;
        }

        return ans;
    }

    public static String compactUpperCase( String name ) {
        String idName = nameToIdentifier( name, IdentifierType.CLASS );
        StringTokenizer tok = new StringTokenizer( idName, "_" );
        String upName = tok.nextToken();
        while ( tok.hasMoreTokens() ) {
            String word = tok.nextToken();
            upName += "_" + word.substring( 0, 1 ).toUpperCase() + word.substring( 1 );
        }
        return upName;
    }

    public static String getter( String name, String type ) {
        return getter( name, type, null );
    }

    public static String setter( String name ) {
        return "set" + capitalize( name );
    }

    public static String getter( String name, String type, Integer max ) {
        String prefix = ( ( max != null && max == 1 )
                &&
                ( type.equals("xsd:boolean") || type.equals( boolean.class.getName() ) || type.equals( Boolean.class.getName() ) )
        ) ? "is" : "get";
        return prefix + capitalize( name );
    }










    public static String negate( String expr ) {
        if ( expr.startsWith(NEG) ) {
            return expr.substring(NEG.length()+1,expr.length()-1);
        } else {
            return NEG + "(" + expr + ")";
        }
    }










    public static String map( String dataType, boolean box ) {
        if ( "xsd:integer".equals( dataType ) ) {
            return "java.math.BigInteger";
        } else if ( "xsd:int".equals( dataType ) ) {

            return box ? "java.lang.Integer" : "int";

        } else if ( "xsd:string".equals( dataType ) ) {
            return "java.lang.String";
        } else if ( "xsd:dateTime".equals( dataType ) ) {
            return "java.util.Date";
        } else if ( "xsd:date".equals( dataType ) ) {
            return "java.util.Date";
        } else if ( "xsd:time".equals( dataType ) ) {
            return "java.util.Date";
        } else if ( "xsd:long".equals( dataType ) ) {

            return box ? "java.lang.Long" : "long";

        } else if ( "xsd:float".equals( dataType ) ) {

            return box ? "java.lang.Float" : "float";

        } else if ( "xsd:double".equals( dataType ) ) {

            return box ? "java.lang.Double" : "double";

        } else if ( "xsd:short".equals( dataType ) ) {

            return box ? "java.lang.Short" : "short";

        } else if ( "xsd:anySimpleType".equals( dataType ) ) {
            return "java.lang.Object";
        } else if ( "xsd:boolean".equals( dataType ) ) {

            return box ? "java.lang.Boolean" : "boolean" ;

        } else if ( "xsd:byte".equals( dataType ) ) {

            return box ? "java.lang.Byte" : "byte";

        } else if ( "xsd:decimal".equals( dataType ) ) {
            return "java.math.BigDecimal";
        } else if ( "xsd:unsignedByte".equals( dataType ) ) {

            return box ? "java.lang.Short" : "short";

        } else if ( "xsd:unsignedShort".equals( dataType ) ) {

            return box ? "java.lang.Integer" : "int";

        } else if ( "xsd:unsignedInt".equals( dataType ) ) {

            return box ? "java.lang.Long" : "long";

        } else {
            return dataType;
        }

    }

    public static String skolem( String dataType ) {

        if ( "xsd:integer".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:int".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:string".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:dateTime".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:date".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:time".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:long".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:float".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:double".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:short".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:anySimpleType".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:boolean".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:byte".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:decimal".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:unsignedByte".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:unsignedShort".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:unsignedInt".equals( dataType ) ) {
            return "null";
        } else {
            return "new " + dataType + "Impl()";
        }

    }

    public static String nullify( String dataType, boolean box) {
        if ( box ) {
            return "null";
        }

        if ( "xsd:integer".equals( dataType ) ) {
            return "java.math.BigInteger.ZERO";
        } else if ( "xsd:int".equals( dataType ) ) {
            return "0";
        } else if ( "xsd:string".equals( dataType ) ) {
            return "new String()";
        } else if ( "xsd:dateTime".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:date".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:time".equals( dataType ) ) {
            return "0";
        } else if ( "xsd:long".equals( dataType ) ) {
            return "0";
        } else if ( "xsd:float".equals( dataType ) ) {
            return "0.0f";
        } else if ( "xsd:double".equals( dataType ) ) {
            return "0.0";
        } else if ( "xsd:short".equals( dataType ) ) {
            return "0";
        } else if ( "xsd:anySimpleType".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:boolean".equals( dataType ) ) {
            return "false";
        } else if ( "xsd:byte".equals( dataType ) ) {
            return "0";
        } else if ( "xsd:decimal".equals( dataType ) ) {
            return "null";
        } else if ( "xsd:unsignedByte".equals( dataType ) ) {
            return "0";
        } else if ( "xsd:unsignedShort".equals( dataType ) ) {
            return "0";
        } else if ( "xsd:unsignedInt".equals( dataType ) ) {
            return "0";
        } else {
            return "null";
        }

    }







    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static void setDateFormatter( DateFormat format ) {
        formatter = format;
    }

    public static DateFormat getFormatter() {
        return formatter;
    }

    public static String marshalDate( Date d ) {
        return formatter.format( d );
    }

    public static Date unmarshalDate( String d ) {
        try {
            return formatter.parse( d );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date( 0 );
    }


}
