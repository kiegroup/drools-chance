package org.drools.shapes;


import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.drools.semantics.builder.model.compilers.SemanticXSDModelCompilerImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.util.*;

public class MetadataPlugin extends Plugin {

    public static String uri = "http://jboss.org/drools/drools-chance/drools-shapes/plugins/metadata";

    private static String metaDescrTempl = "metaDescr";


    public String getOptionName() {
        return "Xmetadata";
    }

    public List<String> getCustomizationURIs() {
        return Collections.singletonList(uri.toString());
    }

    public boolean isCustomizationTagName( String nsUri, String localName ) {
        return nsUri.equals( uri ) && (
                localName.equals( "type" )
                        || localName.equals( "property")
                        || localName.equals( "restriction" ) );
    }

    public String getUsage() {
        return "  -Xmetadata";
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
            List<Boolean> simpleFlags = new ArrayList<Boolean>();
            for ( int j = 0; j < propList.getLength(); j++ ) {
                Node n = propList.item( j );
                if ( n instanceof Element ) {
                    propNames.add( ((Element) n).getAttribute( "name" ) );
                    typeNames.add( ((Element) n).getAttribute( "type" ) );
                    simpleFlags.add( Boolean.valueOf( ((Element) n).getAttribute( "simple" ) ) );
                }
            }


            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put( "klassName", co.target.shortName );
            map.put( "typeName", keyed.getAttribute( "name" ) );
            map.put( "package", keyed.getAttribute( "package" ) );
            map.put( "supertypeName", keyed.getAttribute( "parent" ) );
            map.put( "propertyNames", propNames );
            map.put( "typeNames", typeNames );
            map.put( "simpleFlags", simpleFlags );


            String meta = SemanticXSDModelCompilerImpl.getTemplatedCode( metaDescrTempl, map);

            

            co.implClass.direct( meta );

            c.markAsAcknowledged();

        }

        return true;
    }

}
