
package org.drools.semantics.builder.model;

import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.model.compilers.XSDModelCompiler;
import org.drools.semantics.utils.NameUtils;
import org.drools.semantics.utils.NamespaceUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

public class XSDModelImpl extends ModelImpl implements XSDModel {


    private Document schema;

    private Map<Namespace, Document> subSchemas = new HashMap<Namespace, Document>();

    protected Map<String,Namespace> namespaces = new HashMap<String,Namespace>();

    private XSDModelCompiler.XSDSchemaMode schemaMode = XSDModelCompiler.XSDSchemaMode.JAXB;


    XSDModelImpl() {

    }


    @Override
    public void initFromBaseModel( OntoModel base ) {
        super.initFromBaseModel(base);

        setNamespace( "xsd", "http://www.w3.org/2001/XMLSchema" );
//        setNamespace( "xjc", "http://java.sun.com/xml/ns/jaxb/xjc" );

        schema = initDocument( this.getDefaultNamespace() );
    }

    private Document initDocument( String tgtNamespace ) {
        Document dox = new Document();

        Element root = new Element("schema", getNamespace("xsd") );

        root.setAttribute( "elementFormDefault", "qualified" );
        root.setAttribute( "targetNamespace", tgtNamespace );

        for ( Namespace ns : namespaces.values() ) {
            root.addNamespaceDeclaration( ns );
        }

        dox.addContent(root);
        return dox;
    }

    public Document getXSDSchema() {
        return schema;
    }

    public boolean stream( OutputStream os ) {
        try {
            os.write( serialize( getXSDSchema() ).getBytes() );
            os.write( getOWLSchema().getBytes() );
            for ( Document dox : subSchemas.values() ) {
                os.write( serialize( dox ).getBytes() );
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
        return true;
    }

    public boolean stream( File file ) {
        try {
            FileOutputStream fos = new FileOutputStream( file );
            fos.write( serialize( getXSDSchema() ).getBytes() );
            fos.flush();
            fos.close();

            FileOutputStream owl = new FileOutputStream( file.getParent() + "/owlThing.xsd" );
            owl.write(getOWLSchema().getBytes());
            owl.flush();
            owl.close();


            for ( Namespace ns : subSchemas.keySet() ) {
                String subFileName = file.getAbsolutePath().replace( ".xsd", "_" + ns.getPrefix() + ".xsd" );
                FileOutputStream subFos = new FileOutputStream( subFileName );
                subFos.write( serialize( subSchemas.get( ns ) ).getBytes() );
                subFos.flush();
                subFos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
        return true;
    }

    public Namespace getNamespace(String ns) {
        return namespaces.get( ns );
    }

    public Collection<Namespace> getNamespaces() {
        return namespaces.values();
    }


    public void setNamespace( String prefix, String namespace ) {
        Namespace ns = Namespace.getNamespace( prefix, namespace );
        namespaces.put( prefix, ns );

        if ( schema != null ) {
            schema.getRootElement().addNamespaceDeclaration( ns );
        }
        for ( Document dox : subSchemas.values() ) {
            dox.getRootElement().addNamespaceDeclaration( ns );
        }
    }


    public void addTrait( String name, Object trait ) {
        Element elx = (Element) trait;
        String type = elx.getAttributeValue( "type" );
        if ( type != null ) {
            boolean mainNamespace = type.startsWith( "tns:" );
            if ( mainNamespace ) {
                getXSDSchema().getRootElement().addContent( (Element) trait );
            } else {
                Namespace altNamespace = namespaces.get( type.substring( 0, type.indexOf( ":" ) ) );
                getXSDSchema( altNamespace ).getRootElement().addContent( (Element) trait );
            }
        } else {
            Element typeDef = (Element) trait;
            Document schema = getXSDSchema( name );
            schema.getRootElement().addContent( typeDef );
            if ( typeDef.getName().equals( "complexType" ) ) {
                Element complexContent = typeDef.getChild( "complexContent", namespaces.get( "xsd" ) );
                if ( complexContent != null ) {
                    Element base = complexContent.getChild( "extension", namespaces.get( "xsd" ) );
                    if ( base != null ) {
                        String sup =  base.getAttributeValue( "base" );
                        if ( sup.indexOf( ":" ) >= 0 ) {
                            String ns = sup.substring( 0, sup.indexOf( ":" ) );
                            String baseNs = namespaces.get( ns ).getURI();
                            String localNs = schema.getRootElement().getAttributeValue( "targetNamespace" );
                            if ( ! localNs.equals( baseNs ) ) {
                                addImport( schema, namespaces.get( ns ) );
                            }

                        }
                    }
                }

            }
        }

    }

    private Document getXSDSchema( String type ) {
        if ( type.indexOf( ":" ) >= 0 ) {
            return getXSDSchema();
        }
        if ( definesType( getXSDSchema(), type ) ) {
            return getXSDSchema();
        }
        for ( Document sub : subSchemas.values() ) {
            if ( definesType(sub, type) ) {
                return sub;
            }
        }
        return getXSDSchema();
//        throw new IllegalStateException( "No schema has been initialized for type " + type );
    }

    private boolean definesType( Document dox, String name ) {
        Element schema = dox.getRootElement();
        List<Element> types = schema.getChildren( "element", Namespace.getNamespace( "xsd", "http://www.w3.org/2001/XMLSchema" ) );
        for ( Element ct : types ) {
            String declaredName = ct.getAttributeValue( "name" );
            if ( declaredName != null && declaredName.equals( name ) ) {
                return true;
            }
        }
        return false;
    }

    private Document getXSDSchema( Namespace altNamespace ) {
        if ( subSchemas.containsKey( altNamespace ) ) {
            return subSchemas.get( altNamespace );
        } else {
                System.out.println( "Need to create a new schema on the fly " + altNamespace );
            Document dox = initDocument( altNamespace.getURI() );
            subSchemas.put( altNamespace, dox );

            addImport( getXSDSchema(), altNamespace );
            return dox;
        }
    }

    private void addImport( Document dox, Namespace altNamespace ) {
        List<Element> imports = dox.getRootElement().getChildren( "import", NamespaceUtils.getNamespaceByPrefix( "xsd" ) );
        for ( Element e : imports ) {
            if ( e.getAttributeValue( "namespace" ).equals( altNamespace.getURI() ) ) {
                return;
            }
        }

        Element imp = new Element( "import", getNamespace( "xsd" ) );
            imp.setAttribute( "namespace", altNamespace.getURI() );
            imp.setAttribute( "schemaLocation", getSchemaName( altNamespace ) );
        System.err.println( "Adding import " + altNamespace + " , just to be sure" );

        dox.getRootElement().addContent( 0, imp );
    }

    private String getSchemaName( Namespace altNamespace ) {
        if ( NamespaceUtils.getNamespaceByPrefix( "owl" ).getURI().equals( altNamespace.getURI() ) ) {
            return "owlThing.xsd";
        } else {
            String prefix = NamespaceUtils.compareNamespaces( altNamespace.getURI(), getDefaultNamespace() )
                    ? ""
                    : ( "_" + altNamespace.getPrefix() );
            return getName() + prefix + ".xsd";
        }
    }


    public Object getTrait(String name) {
        return null;
    }



    public Set<String> getTraitNames() {
        Set<String> names = new HashSet<String>();
        //TODO
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    public String getOWLSchema() {
        InputStream schemaIS = null;
        try {
            schemaIS = ResourceFactory.newClassPathResource("org/drools/semantics/builder/model/compilers/owlThing.xsd").getInputStream();
            byte[] data = new byte[ schemaIS.available() ];
            schemaIS.read( data );
            return new String( data );
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

}
