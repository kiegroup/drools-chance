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

package org.drools.semantics.builder.model;

import org.drools.semantics.builder.DLUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class XSDModelImpl extends ModelImpl implements XSDModel {


    private Document schema;

    private Map<String,Namespace> namespaces = new HashMap<String,Namespace>();



    XSDModelImpl() {


    }


    @Override
    public void initFromBaseModel(OntoModel base) {
        super.initFromBaseModel(base);

        setNamespace( "xsd", "http://www.w3.org/2001/XMLSchema" );

        schema = new Document();

        Element root = new Element("schema", getNamespace("xsd") );

        root.setAttribute( "elementFormDefault", "qualified" );
        root.setAttribute( "targetNamespace", DLUtils.reverse( this.getPackage() ) );


        schema.addContent(root);
    }

    public Document getXSDSchema() {
        return schema;
    }

    public boolean stream( OutputStream os ) {
        try {
            os.write( serialize( getXSDSchema() ).getBytes() );
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
        return true;
    }

    public Namespace getNamespace(String ns) {
        return namespaces.get( ns );
    }

    public void setNamespace( String prefix, String namespace ) {
        Namespace ns = Namespace.getNamespace(prefix, namespace);
        namespaces.put( prefix, ns );

        if ( schema != null ) {
            schema.getRootElement().addNamespaceDeclaration( ns );
        }
    }


    public void addTrait(String name, Object trait) {
        getXSDSchema().getRootElement().addContent( (Element) trait );
    }



    public Object getTrait(String name) {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    public Set<String> getTraitNames() {
            //TODO
        Set<String> names = new HashSet<String>();


        return names;
    }



    @Override
    protected String traitsToString() {
        return serialize( getXSDSchema() );
    }


    protected String serialize( Document dox ) {
        StringWriter out = new StringWriter();
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat( Format.getPrettyFormat() );
        try {
            outputter.output( dox, out );
        }
            catch (IOException e) {
        System.err.println(e);
        }

        return out.toString();
    }
}
