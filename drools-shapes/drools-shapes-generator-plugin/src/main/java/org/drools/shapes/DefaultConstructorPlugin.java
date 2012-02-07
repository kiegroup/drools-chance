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
import com.sun.codemodel.JType;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.model.compilers.SemanticXSDModelCompilerImpl;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.util.*;

public class DefaultConstructorPlugin extends Plugin {

    public static String uri = "http://jboss.org/drools/drools-chance/drools-shapes/plugins/constructor";


    public String getOptionName() {
        return "Xdefault-constructor";
    }

    public List<String> getCustomizationURIs() {
        return Collections.singletonList( uri.toString() );
    }

    public boolean isCustomizationTagName( String nsUri, String localName ) {
        return nsUri.equals( uri ) && ( localName.equals( "default" ) );
    }

    public String getUsage() {
        return "  -Xdefault-constructor";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        for (ClassOutline co : outline.getClasses() ) {

            CPluginCustomization c = co.target.getCustomizations().find( uri, "default" );
            if ( co.implClass.getConstructor( new JType[0]) == null ) {
                createDefaultConstructor( co.implClass );
            }
            c.markAsAcknowledged();
        }

        return true;
    }



    private void createDefaultConstructor(JDefinedClass implClass) {

        Map<String,Object> vars = new HashMap<String, Object>();
        vars.put( "name", implClass.name().substring( 0, implClass.name().length() - 4 ) ); //remove "Impl"

        String constructor = SemanticXSDModelCompilerImpl.getTemplatedCode("defaultConstructor", vars);

        implClass.direct( constructor );
    }



}
