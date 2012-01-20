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
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.DLUtils;
import org.mvel2.templates.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessorPlugin extends Plugin {

    public static String uri = "http://jboss.org/drools/drools-chance/drools-shapes/plugins/accessor";

//    private static CompiledTemplate equalsTempl = readResource("equals.template");
//    private static CompiledTemplate hashKyTempl = readResource("hashKy.template");

    private static TemplateRegistry registry = new SimpleTemplateRegistry();

    {
        registry.addNamedTemplate( "baseAddRemove", readTemplate( "templates/baseAddRemove.template" ) );
//        registry.addNamedTemplate( "semPropertyGetter", readTemplate("templates/semGetter.template") );
//        registry.addNamedTemplate( "semPropertySetter", readTemplate( "templates/semSetter.template" ) );
    }


    public String getOptionName() {
        return "Xsem-accessors";
    }

    public List<String> getCustomizationURIs() {
        return Collections.singletonList( uri.toString() );
    }

    public boolean isCustomizationTagName( String nsUri, String localName ) {
        return nsUri.equals( uri ) && ( localName.equals( "property" ) || localName.equals( "accessor" ) || localName.equals( "accessors" ) );
    }

    public String getUsage() {
        return "  -Xsem-accessors";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        for (ClassOutline co : outline.getClasses() ) {

            System.out.println(">>>>>>> Examine " + co.target.shortName );
            
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
        System.out.println(">>>>>>> Examine prop " + item.getTextContent() );
        Map<String,Object> vars = new HashMap<String, Object>(); 
        vars.put( "name", item.getAttribute( "name" ) );
        vars.put( "type", item.getAttribute( "type" ) );
        vars.put( "primitive", Boolean.valueOf( item.getAttribute( "primitive" ) ) );
        
        String code = TemplateRuntime.execute( registry.getNamedTemplate( "baseAddRemove" ), DLUtils.getInstance(), vars ).toString();
        System.out.println(">>>>>>> Got code" + code );
        implClass.direct( code );
    }

    private void compileAccessor( JDefinedClass implClass, Element item ) {

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



}
