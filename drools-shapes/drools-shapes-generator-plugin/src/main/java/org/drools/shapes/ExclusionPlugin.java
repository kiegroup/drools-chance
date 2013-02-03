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

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExclusionPlugin extends Plugin {

    public static String uri = "http://jboss.org/drools/drools-chance/drools-shapes/plugins/exclude";

    public String getOptionName() {
        return "XxcludeResolved";
    }

    public List<String> getCustomizationURIs() {
        return Collections.singletonList( uri.toString() );
    }

    public boolean isCustomizationTagName( String nsUri, String localName ) {
        return nsUri.equals( uri ) && ( localName.equals( "xclude" ) );
    }

    public String getUsage() {
        return "  -XxcludeResolved";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        for (ClassOutline co : outline.getClasses() ) {
            CPluginCustomization c = co.target.getCustomizations().find( uri.toString(), "xclude" );
            if ( c != null ) {
                co.implClass.hide();
                c.markAsAcknowledged();
            }
        }
      return true;
    }




}
