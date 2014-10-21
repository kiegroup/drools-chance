package org.drools.shapes;


import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.apache.commons.lang.StringUtils;
import org.drools.core.metadata.ManyToManyPropertyLiteral;
import org.drools.core.metadata.ManyToOnePropertyLiteral;
import org.drools.core.metadata.MetadataHolder;
import org.drools.core.metadata.OneToManyPropertyLiteral;
import org.drools.core.metadata.OneToOnePropertyLiteral;
import org.drools.core.metadata.ToManyPropertyLiteral;
import org.drools.core.metadata.ToOnePropertyLiteral;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class
        MetaClassPlugin extends MetadataPlugin {

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

        Map<String, Map<String,PropInfo>> propertyCache = new HashMap<String, Map<String, PropInfo>>(  );

        for (ClassOutline co : outline.getClasses() ) {

            CPluginCustomization c = co.target.getCustomizations().find( uri.toString(), "type" );
            if( c == null ) {
                continue;
            }

            Map<String,PropInfo> properties = new HashMap<String, PropInfo>();
            propertyCache.put( co.target.getName(), properties );

            Element keyed = c.element;
            NodeList propList = keyed.getChildNodes();
            for ( int j = 0; j < propList.getLength(); j++ ) {
                Node n = propList.item( j );
                if ( n instanceof Element ) {
                    Element el = (Element) n;

                    PropInfo p = new PropInfo();
                    p.propName = el.getAttribute( "name" );

                    String fqn =  el.getAttribute( "type" );
                    p.typeName = fqn;
                    p.simpleTypeName = fqn.substring( fqn.lastIndexOf( '.' ) + 1 );

                    p.propIri = el.getAttribute( "iri" );
                    p.simple = Boolean.valueOf( ( (Element) n ).getAttribute( "simple" ) );
                    p.primitive = Boolean.valueOf( ( (Element) n ).getAttribute( "primitive" ) );

                    p.inherited = Boolean.valueOf( el.getAttribute( "inherited" ) );

                    String javaType = getJavaType( p.typeName );
                    p.range = javaType;
                    p.javaRangeType = p.simple ? javaType : ( List.class.getName() + "<" + javaType + ">" );

                    p.domain = getJavaType( el.getAttribute( "domain" ) );

                    String inverseName = el.getAttribute( "inverse" ).length() > 0 ? el.getAttribute( "inverse" ) : null;

                    properties.put( p.propName, p );

                    if ( inverseName != null ) {
                        Map<String,PropInfo> foreignProperties = propertyCache.get( p.range );
                        if ( foreignProperties == null ) {
                            foreignProperties = propertyCache.get( p.range + "Impl" );
                        }
                        if ( foreignProperties != null ) {
                            PropInfo inv = null;
                            if ( foreignProperties.containsKey( inverseName ) ) {
                                inv = foreignProperties.get( inverseName );
                            } else {
                                inverseName = inverseName + p.simpleTypeName;
                                inv = foreignProperties.get( inverseName );
                            }
                            if ( inv != null ) {
                                p.inverse = inv;
                                inv.inverse = p;
                            } else {
                                // it may still be a property with same domain and range
                            }
                        }
                    }

                }
            }

        }

        for (ClassOutline co : outline.getClasses() ) {
            CPluginCustomization c = co.target.getCustomizations().find( uri.toString(), "type" );
            if( c == null ) {
                continue;
            }
            Element keyed = c.element;

            Map<String,PropInfo> properties = propertyCache.get( co.target.getName() );
            for ( PropInfo p : properties.values() ) {
                if ( p.inverse == null ) {
                    p.concreteType = p.simple ? ToOnePropertyLiteral.class.getName() : ToManyPropertyLiteral.class.getName();
                } else {
                    if ( p.simple ) {
                        p.concreteType = p.inverse.simple ? OneToOnePropertyLiteral.class.getName() : ManyToOnePropertyLiteral.class.getName();
                    } else {
                        p.concreteType = p.inverse.simple ? OneToManyPropertyLiteral.class.getName() : ManyToManyPropertyLiteral.class.getName();
                    }
                }
            }


            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put( "klassName", co.target.shortName );
            map.put( "typeName", keyed.getAttribute( "name" ) );
            map.put( "package", keyed.getAttribute( "package" ) );
            map.put( "supertypeName", keyed.getAttribute( "parent" ) );
            map.put( "supertypePackage", keyed.getAttribute( "parentPackage" ) );
            map.put( "typeIri", keyed.getAttribute( "iri" ) );

            map.put( "properties", properties.values() );

            String metaClass = SemanticXSDModelCompilerImpl.getTemplatedCode( metaClassTempl, map );
            String metaAttrib = SemanticXSDModelCompilerImpl.getTemplatedCode( metaAttribTempl, map);

            co.implClass._implements( MetadataHolder.class );
            co.implClass.direct( metaAttrib );

            FileOutputStream fos = null;
            try {
                File metaFile = new File( opt.targetDir.getPath().replace( "xjc", "java" ) +
                                          File.separator +
                                          StringUtils.replace(keyed.getAttribute( "package" ), ".", File.separator ) +
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


    public class PropInfo {

        public String propName;
        public String typeName;
        public String javaRangeType;
        public String propIri;
        public boolean simple;
        public String range;
        public String domain;
        public boolean inherited;
        public PropInfo inverse;
        public String concreteType;
        public int position;
        public String simpleTypeName;
        public boolean primitive;
    }
}
