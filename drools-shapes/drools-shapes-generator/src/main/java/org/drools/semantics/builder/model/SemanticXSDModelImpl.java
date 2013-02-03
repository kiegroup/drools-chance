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
import org.drools.semantics.builder.model.compilers.SemanticXSDModelCompilerImpl;
import org.drools.semantics.utils.NameUtils;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.jdom.Namespace;
import org.mvel2.templates.CompiledTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.misc.Regexp;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticXSDModelImpl extends XSDModelImpl implements SemanticXSDModel {


    private String index;

    private Map<String,Document> bindings = new HashMap<String, Document>(  );
    
    private String individualFactory;

    private Map<Namespace,String> packageInfos;

    private String empireConfig;

    private String persistenceXml;

    public Document getBindings( String namespace ) {
        return bindings.containsKey( namespace )? bindings.get( namespace ) : null;
    }

    public boolean streamIndividualFactory( OutputStream os ) {
        try {
            os.write( individualFactory.getBytes() );
        } catch (IOException e) {
            return false;
        }
        return true;
    }



    public void setBindings( String namespace, Document bindings ) {
        if ( this.bindings == null ) {
            this.bindings = new HashMap<String,Document>( 3 );
        }
        this.bindings.put( namespace, bindings );
    }

    public String getNamespacedPackageInfo( Namespace ns ) {
        return packageInfos.get( ns );
    }

    public void addNamespacedPackageInfo( Namespace ns, String namespaceFix ) {
        if ( packageInfos == null ) {
            packageInfos = new HashMap<Namespace, String>( 3 );
        }
        this.packageInfos.put( ns, namespaceFix );
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



    public boolean streamNamespacedPackageInfos( File folder ) {
        try {
            for ( Namespace ns : packageInfos.keySet() ) {
                String packageName = NameUtils.namespaceURIToPackage( ns.getURI() );
                File out = new File( folder.getPath() + File.separator + packageName.replace( '.', File.separatorChar ) + File.separator + "package-info.java" );
                if ( ! out.exists() ) {
                    String fix = packageInfos.get( ns );
                    if ( ! out.getParentFile().exists() ) {
                        out.getParentFile().mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream( out );
                    fos.write( fix.getBytes() );
                    fos.flush();
                    fos.close();
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean streamEmpireConfig( OutputStream os ) {
        try {
            System.out.println(" EMPIRE CONFIG" + getEmpireConfig() );
            os.write( getEmpireConfig().getBytes() );
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }


    private String compactXML( Document dox ) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {
//        DocumentBuilderFactory doxFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = doxFactory.newDocumentBuilder();
//        InputSource is = new InputSource( new StringReader( source ) );
//        Document dox = builder.parse( is );
//        dox.normalize();

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
        tFactory.setAttribute( "indent-number", new Integer( 2 ) );
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
        DOMSource domSrc = new DOMSource( dox );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult( baos );
        transformer.transform( domSrc, result );

        return new String( baos.toByteArray() );
    }

    public boolean streamBindings( File folder ) {
        try {
            for ( String ns : prefixMap.keySet() ) {
                FileOutputStream os = null;
                Namespace namespace = prefixMap.get( ns );
                if ( "xsd".equals( ns ) || "xsi".equals( ns ) ) {
                    continue;
                }
                if ( "owl".equals( ns ) ) {
                    os = new FileOutputStream( folder + File.separator + "global.xjb" );
                } else {
                    String path = folder.getPath() + File.separator + NameUtils.namespaceURIToPackage( namespace.getURI() ) + ".xjb";
                    File target = new File( path );
                    target = checkForBindingOverride( namespace, target );

                    os = new FileOutputStream( target );
                }

                if ( os != null ) {
                    Document bindings = getBindings( prefixMap.get( ns ).getURI() );
                    System.out.println( compactXML( bindings ) );

                    String tgtSchemaLoc = schemaLocations.get( namespace );

                    checkSchemaLocationOverride( bindings, tgtSchemaLoc );

                    os.write( compactXML( bindings ).getBytes() );
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

    private void checkSchemaLocationOverride( Document bindings, String tgtSchemaLoc ) {
        if ( tgtSchemaLoc != null ) {
            tgtSchemaLoc = tgtSchemaLoc.substring( tgtSchemaLoc.lastIndexOf( File.separator ) + 1 );
            NodeList bx = bindings.getElementsByTagName( "bindings" );
            for ( int j = 0; j < bx.getLength(); j++ ) {
                Element bind = (Element) bx.item( j );
//                        if ( "/xsd:schema".equals( bind.getAttribute( "node" ) ) ) {
                if ( bind.hasAttribute( "schemaLocation" ) ) {
                    String currSchemaLoc = bind.getAttribute( "schemaLocation" );

                    if ( ! currSchemaLoc.equals( tgtSchemaLoc ) ) {
                        bind.setAttribute( "schemaLocation", tgtSchemaLoc );
                    }
                    break;
                }
            }
        }
    }

    private File checkForBindingOverride( Namespace ns, File tgt ) {
        int j = 0;
        File target = tgt;
        String path = target.getPath();

        while ( target.exists() ) {
            target = new File( path.replace( ".xjb", "_" + ( j++ ) + ".xjb" ) );
        }

        return target;
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

    public void setEmpireConfig( String empireConfig ) {
        this.empireConfig = empireConfig;
    }

    public String getPersistenceXml() {
        return persistenceXml;
    }

    public void setPersistenceXml(String persistenceXml) {
        this.persistenceXml = persistenceXml;
    }
}
