package org.drools.shapes;


import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.apache.commons.lang.StringUtils;
import org.drools.core.metadata.MetadataHolder;
import org.drools.semantics.builder.model.compilers.SemanticXSDModelCompilerImpl;
import org.drools.semantics.utils.NameUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MetaClassPlugin extends MetadataPlugin {

    private static String metaClassTempl = "metaClass";
    private static String metaAttribTempl = "metaAttrib";


    public String getOptionName() {
        return "Xmetaclass";
    }

    public String getUsage() {
        return "  -Xmetaclass";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        for (ClassOutline co : outline.getClasses() ) {

            CPluginCustomization c = co.target.getCustomizations().find( uri.toString(), "type" );
            if( c == null ) {
                continue;
            }


            Element keyed = c.element;
            NodeList propList = keyed.getChildNodes();
            List<String> propNames = new ArrayList<String>();
            List<String> typeNames = new ArrayList<String>();
            List<String> javaTypeNames = new ArrayList<String>();
            List<String> propIris = new ArrayList<String>();
            List<Boolean> simpleFlags = new ArrayList<Boolean>();
            List<Boolean> inheritFlags = new ArrayList<Boolean>();
            for ( int j = 0; j < propList.getLength(); j++ ) {
                Node n = propList.item( j );
                if ( n instanceof Element ) {
                    propNames.add( ((Element) n).getAttribute( "name" ) );
                    typeNames.add( ((Element) n).getAttribute( "type" ) );
                    propIris.add( ((Element) n).getAttribute( "iri" ) );
                    simpleFlags.add( Boolean.valueOf( ( (Element) n ).getAttribute( "simple" ) ) );
                    inheritFlags.add( Boolean.valueOf( ((Element) n).getAttribute( "inherited" ) ) );
                    String javaType = getJavaType( typeNames.get( j ) );
                    javaTypeNames.add( simpleFlags.get( j ) ? javaType : ( List.class.getName() + "<" + javaType + ">" ) );
                }
            }


            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put( "klassName", co.target.shortName );
            map.put( "typeName", keyed.getAttribute( "name" ) );
            map.put( "package", keyed.getAttribute( "package" ) );
            map.put( "supertypeName", keyed.getAttribute( "parent" ) );
            map.put( "supertypePackage", keyed.getAttribute( "parentPackage" ) );
            map.put( "typeIri", keyed.getAttribute( "iri" ) );
            map.put( "propertyIris", propIris );
            map.put( "propertyNames", propNames );
            map.put( "typeNames", typeNames );
            map.put( "javaTypeNames", javaTypeNames );
            map.put( "simpleFlags", simpleFlags );
            map.put( "inheritedFlags", inheritFlags );


            String metaClass = SemanticXSDModelCompilerImpl.getTemplatedCode( metaClassTempl, map );
            String metaAttrib = SemanticXSDModelCompilerImpl.getTemplatedCode( metaAttribTempl, map);

            co.implClass._implements( MetadataHolder.class );
            co.implClass.direct( metaAttrib );

            FileOutputStream fos = null;
            try {
                File metaFile = new File( opt.targetDir.getPath().replace( "xjc", "java" ) +
                                          File.separator +
                                          StringUtils.replace(keyed.getAttribute( "package" ), "\\.", File.separator ) +
                                          File.separator +
                                          keyed.getAttribute( "name" ) +
                                          "_.java" );
                if ( ! metaFile.getParentFile().exists() ) {
                    metaFile.getParentFile().mkdirs();
                }
                fos = new FileOutputStream( metaFile );
                fos.write( metaClass.getBytes() );
            } catch ( Exception e ) {
                e.printStackTrace();
            } finally {
                if ( fos != null ) {
                    try {
                        fos.close();
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
            }

            c.markAsAcknowledged();

        }

        return true;
    }

    private String getJavaType( String name ) {
        // try built ins to resolve xsd:simpletypes. if not, assume a regular java class
        String javaName = NameUtils.builtInTypeToWrappingJavaType( name );
        if ( javaName != null ) {
            return javaName;
        } else {
            return name;
        }
    }

}
