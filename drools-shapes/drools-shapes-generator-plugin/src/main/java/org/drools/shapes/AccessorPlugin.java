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

package org.drools.shapes;

import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.DLUtils;
import org.drools.semantics.builder.model.compilers.SemanticXSDModelCompilerImpl;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.util.*;

public class AccessorPlugin extends Plugin {

    public static String uri = "http://jboss.org/drools/drools-chance/drools-shapes/plugins/accessor";




    public String getOptionName() {
        return "Xsem-accessors";
    }

    public List<String> getCustomizationURIs() {
        return Collections.singletonList( uri.toString() );
    }

    public boolean isCustomizationTagName( String nsUri, String localName ) {
        return nsUri.equals( uri ) && ( localName.equals( "property" ) || localName.equals( "accessor" ) || localName.equals( "accessors" )
                                        || localName.equals( "chain" ) || localName.equals( "link" ) );
    }

    public String getUsage() {
        return "  -Xsem-accessors";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        for (ClassOutline co : outline.getClasses() ) {

            co.implClass.direct(  "@javax.xml.bind.annotation.XmlTransient protected Object[] jdoDetachedState;"  );


            CPluginCustomization c = co.target.getCustomizations().find( uri, "accessors" );
            Element accessors = c.element;

            NodeList props = accessors.getElementsByTagNameNS( uri, "property" );
            for ( int j = 0; j < props.getLength(); j++ ) {
                compileProperty( co.implClass, (Element) props.item( j ) );
            }

            NodeList axx = accessors.getElementsByTagNameNS( uri, "accessor" );
            for ( int j = 0; j < axx.getLength(); j++ ) {
                compileAccessor( co.implClass, (Element) axx.item( j ) );
            }

            c.markAsAcknowledged();
        }

        return true;
    }



    private void compileProperty( JDefinedClass implClass, Element item ) {
        Map<String,Object> vars = new HashMap<String, Object>();
        vars.put( "name", item.getAttribute( "name" ) );
        vars.put( "type", item.getAttribute( "type" ) );
        vars.put( "primitive", Boolean.valueOf( item.getAttribute( "primitive" ) ) );

        String code;

        code = SemanticXSDModelCompilerImpl.getTemplatedCode("baseGetterSetter", vars);
        implClass.direct( code );

        code = SemanticXSDModelCompilerImpl.getTemplatedCode("baseAddRemove", vars);
        implClass.direct( code );

        code = SemanticXSDModelCompilerImpl.getTemplatedCode("genericAdd", vars);
        implClass.direct( code );
    }

    private void compileAccessor( JDefinedClass implClass, Element item ) {
        Map<String,Object> vars = new HashMap<String, Object>();
        vars.put( "name", item.getAttribute( "name" ) );
        vars.put( "type", item.getAttribute( "type" ) );
        vars.put( "primitive", Boolean.valueOf(item.getAttribute("primitive")) );
        vars.put( "min", Integer.valueOf(item.getAttribute("min")) );
        vars.put( "max", item.getAttribute( "max" ).equals( "null") ? null : Integer.valueOf( item.getAttribute( "max" ) ) );
        vars.put( "inherited", Boolean.valueOf( item.getAttribute( "max" ) ) );
        vars.put( "base", item.getAttribute("base") );
        vars.put( "baseType", item.getAttribute("baseType") );
        
        Set<List<Link>> chains = new HashSet<List<Link>>();
        NodeList chainsList = item.getElementsByTagNameNS( uri, "chain");
        for ( int j = 0; j < chainsList.getLength(); j++ ) {
            List<Link> chain = new ArrayList<Link>();
                NodeList linkList = ((Element) chainsList.item( j )).getElementsByTagNameNS( uri, "link" );
                for ( int k = 0; k < linkList.getLength(); k++ ) {
                    Element link = (Element) linkList.item( k );
                    chain.add( new Link( link.getTextContent(), link.getAttribute( "type" ) ) );
                }
            chains.add( chain );
        }

        vars.put( "chains", chains );
        vars.put( "isChained", chainsList.getLength() > 0 );

        
        String code;

        if ( chainsList.getLength() == 0 ) {
            code = SemanticXSDModelCompilerImpl.getTemplatedCode("restrictedGetterSetter", vars);
            implClass.direct( code );
        } else {
            code = SemanticXSDModelCompilerImpl.getTemplatedCode("chainGetter", vars);
            implClass.direct( code );
        }

        if ( chainsList.getLength() == 0 ) {
            code = SemanticXSDModelCompilerImpl.getTemplatedCode("restrictedAddRemove", vars);
            implClass.direct( code );
        }


    }



    private static CompiledTemplate readTemplate( String templ ) {
        try {
            InputStream stream = ResourceFactory.newClassPathResource( templ, AccessorPlugin.class).getInputStream();
            return TemplateCompiler.compileTemplate(stream);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }



    public static class Link {
        private String name;
        private String type;

        public Link(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
