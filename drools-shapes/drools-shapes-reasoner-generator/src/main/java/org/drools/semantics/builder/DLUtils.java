/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.builder;

import org.semanticweb.owlapi.model.IRI;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class DLUtils {

    private static DLUtils instance;

    public static DLUtils getInstance() {
        if ( instance == null ) {
            instance = new DLUtils();
        }
        return instance;
    }

    private DLUtils() {

    }


    private static String delims = ":/#!.<> _?";
    private static final String NEG = "ObjectComplementOf";


    public static String iriToPackage( String iri ) {
        StringTokenizer tok = new StringTokenizer( iri, delims );
        String pack = "";
        while ( tok.hasMoreTokens() ) {
            pack += "." + tok.nextToken();
        }
        return pack.substring(1);
    }


    public static String compactUpperCase(String s) {
//        System.out.println("Try to normalize " + s);
        java.util.StringTokenizer tok = new java.util.StringTokenizer(s);
        StringBuilder sb = new StringBuilder();

        while (tok.hasMoreTokens())
            sb.append(capitalize(tok.nextToken()));

        return sb.toString();
    }

    public static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }





    public static String negate( String expr ) {
        if ( expr.startsWith(NEG) ) {
            return expr.substring(NEG.length()+1,expr.length()-1);
        } else {
            return NEG + "(" + expr + ")";
        }
    }


    public static String buildNameFromIri( IRI iri ) {
        if ( iri.getFragment() != null ) {
            return iri.getFragment();
        }
        return buildNameFromIri( iri.toQuotedString() );
    }

    public static String buildLowCaseNameFromIri( IRI iri ) {
        String name = buildNameFromIri( iri );
        return name.substring(0,1).toLowerCase() + name.substring(1);
    }

    public static String buildNameFromIri(String iri) {

        iri = iri.substring( 1, iri.length() - 1 );
        StringTokenizer tok = new StringTokenizer( iri, delims );
        String name = "";
        while ( tok.hasMoreTokens() ) {
            name = tok.nextToken();
        }
        return compactUpperCase( name );
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

    public static String reverse(String aPackage) {


        StringTokenizer tok = new StringTokenizer( aPackage, "." );
        String ans = tok.nextToken();

        while ( tok.hasMoreTokens() ) {
            ans = tok.nextToken() + "." + ans;
        }

        return ans;
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
                return "new " + dataType.replace("__Type","Impl") + "()";
            }
    
        }
    
    
    
    
    
    



    private static DateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss.SSS Z");

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
