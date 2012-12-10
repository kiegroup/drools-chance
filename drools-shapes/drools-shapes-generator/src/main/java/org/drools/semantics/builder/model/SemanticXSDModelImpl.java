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

import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.builder.DefaultNodeFactory;
import org.drools.reteoo.builder.NodeFactory;
import org.drools.semantics.builder.DLTemplateManager;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.jdom.Namespace;
import org.mvel2.templates.CompiledTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticXSDModelImpl extends XSDModelImpl implements SemanticXSDModel {


    private String index;

    private Map<String,String> bindings;
    
    private String individualFactory;

    private String namespaceFix;

    private String empireConfig;

    private String persistenceXml;

    public String getBindings( String namespace ) {
        return this.bindings != null && bindings.containsKey( namespace )? bindings.get( namespace ) : "";
    }

    public boolean streamIndividualFactory( OutputStream os ) {
        try {
            os.write( individualFactory.getBytes() );
        } catch (IOException e) {
            return false;
        }
        return true;
    }



    public void setBindings( String namespace, String bindings ) {
        if ( this.bindings == null ) {
            this.bindings = new HashMap<String,String>();
        }
        this.bindings.put( namespace, bindings );
    }

    public String getNamespaceFix() {
        return namespaceFix;
    }

    public void setNamespaceFix(String namespaceFix) {
        this.namespaceFix = namespaceFix;
    }

    public boolean streamBindings( OutputStream os ) {
        try {
            for ( String ns : bindings.keySet() ) {
                os.write( compactXML( getBindings ( ns ) ).getBytes() );
            }
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public boolean streamNamespaceFix( OutputStream os ) {
        try {
            os.write( getNamespaceFix().getBytes() );
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public boolean streamEmpireConfig( OutputStream os ) {
        try {
            os.write( getEmpireConfig().getBytes() );
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }


    private String compactXML( String source ) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {
        DocumentBuilderFactory doxFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = doxFactory.newDocumentBuilder();
        InputSource is = new InputSource( new StringReader( source ) );
        Document dox = builder.parse( is );
        dox.normalize();

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPathExpression xpathExp = xpathFactory.newXPath().compile(
                "//text()[normalize-space(.) = '']");
        NodeList emptyTextNodes = (NodeList)
                xpathExp.evaluate(dox, XPathConstants.NODESET);

        // Remove each empty text node from document.
        for (int i = 0; i < emptyTextNodes.getLength(); i++) {
            Node emptyTextNode = emptyTextNodes.item(i);
            emptyTextNode.getParentNode().removeChild(emptyTextNode);
        }

        TransformerFactory tFactory = TransformerFactory.newInstance();
        tFactory.setAttribute( "indent-number", new Integer(2) );
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
        DOMSource domSrc = new DOMSource( dox );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult( baos );
        transformer.transform( domSrc, result );

        return new String( baos.toByteArray() );
    }

    public boolean streamBindings( File file ) {
        try {
            for ( String ns : namespaces.keySet() ) {
                FileOutputStream os = null;
                if ( "xsd".equals( ns ) ) {
                    continue;
                }
                if ( "owl".equals( ns ) ) {
                    os = new FileOutputStream( file.getParent() + "/global.xjb" );
                } else if ( "tns".equals( ns ) ) {
                    os = new FileOutputStream( file );
                } else {
                    os = new FileOutputStream( file.getAbsolutePath().replace( ".xjb", "_" + ns + ".xjb" ) );
                }

                if ( os != null ) {
                    os.write( compactXML( getBindings( namespaces.get( ns ).getURI() ) ).getBytes() );
                    os.flush();
                    os.close();
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public boolean streamIndex( OutputStream os ) {
        try {
            os.write( index.getBytes() );
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean streamPersistenceXml( OutputStream os ) {
        try {
            os.write( persistenceXml.getBytes() );
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String getIndividualFactory() {
        return individualFactory;
    }

    public void setIndividualFactory(String individualFactory) {
        this.individualFactory = individualFactory;
    }

    public String getEmpireConfig() {
        return empireConfig;
    }

    public void setEmpireConfig(String empireConfig) {
        this.empireConfig = empireConfig;
    }

    public String getPersistenceXml() {
        return persistenceXml;
    }

    public void setPersistenceXml(String persistenceXml) {
        this.persistenceXml = persistenceXml;
    }
}
