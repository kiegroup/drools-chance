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

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.drools.semantics.builder.model.compilers.SemanticXSDModelCompilerImpl;
import org.drools.semantics.utils.NameUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class KeyPlugin extends Plugin {

    public static String uri = "http://jboss.org/drools/drools-chance/drools-shapes/plugins/key";
    public static String metaURI = "http://jboss.org/drools/drools-chance/drools-shapes/plugins/metadata";

    private static String equalsTempl = "equals";
    private static String hashKyTempl = "hashKy";

    public String getOptionName() {
        return "Xkey-equality";
    }

    public List<String> getCustomizationURIs() {
        return Collections.singletonList( uri.toString() );
    }

    public boolean isCustomizationTagName( String nsUri, String localName ) {
        return nsUri.equals( uri ) && ( localName.equals( "keyId" ) || localName.equals( "key" ) );
    }

    public String getUsage() {
        return "  -Xkey-equality";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
        for (ClassOutline co : outline.getClasses() ) {

            CPluginCustomization c = co.target.getCustomizations().find( uri.toString(), "keyId" );
            CPluginCustomization d = co.target.getCustomizations().find( metaURI.toString(), "type" );
            if( c == null ) {
                continue;
            }

            Element keyed = c.element;
            NodeList keyList = keyed.getChildNodes();
            Key[] keys = new Key[keyList.getLength()];
            for ( int j = 0; j < keyList.getLength(); j++ ) {
                keys[j] = new Key( keyList.item( j ).getTextContent(),
                        NameUtils.map( keyList.item( j ).getAttributes().getNamedItem( "type" ).getTextContent(), true ) );
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
                map.put( "klassName", co.target.shortName );
                map.put( "typeName", d.element.getAttribute( "name" ) );
                map.put( "keys", keys );


            String equals = SemanticXSDModelCompilerImpl.getTemplatedCode( equalsTempl, map);
            String hashKy = SemanticXSDModelCompilerImpl.getTemplatedCode( hashKyTempl, map);

            co.implClass.direct( equals );
            co.implClass.direct( hashKy );

            c.markAsAcknowledged();

        }

        return true;
    }




    public static class Key {
        
        private String name;
        private String type;

        private Key() {
        }

        private Key(String name, String type) {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;

            Key key = (Key) o;

            if (name != null ? !name.equals(key.name) : key.name != null) return false;
            if (type != null ? !type.equals(key.type) : key.type != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Key{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
