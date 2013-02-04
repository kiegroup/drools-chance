
package org.drools.semantics.builder.model;

import org.drools.core.util.StringUtils;
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


//    private Document schema;

    // map : namespace --> schema
    private Map<Namespace, Document> schemas = new HashMap<Namespace, Document>();

    // map : prefix --> namespace
    protected Map<String,Namespace> prefixMap = new HashMap<String,Namespace>();

    // map : namespace (String) --> prefix
    protected Map<String,String> reversePrefixMap = new HashMap<String,String>();

    // map : namespace --> schema file
    protected Map<Namespace, String> schemaLocations = new HashMap<Namespace,String>();

    private XSDModelCompiler.XSDSchemaMode schemaMode = XSDModelCompiler.XSDSchemaMode.JAXB;


    XSDModelImpl() {

    }


    @Override
    public void initFromBaseModel( OntoModel base ) {
        super.initFromBaseModel(base);

        setNamespace( "xsd", "http://www.w3.org/2001/XMLSchema" );
        setNamespace( "xsi", "http://www.w3.org/2001/XMLSchema-instance" );
        setNamespace( "tns", base.getDefaultNamespace() );
//        setNamespace( "xjc", "http://java.sun.com/xml/ns/jaxb/xjc" );

        schemas.put( getNamespace( "tns" ), initDocument( this.getDefaultNamespace() ) );
    }

    private Document initDocument( String tgtNamespace ) {
        Document dox = new Document();

        Element root = new Element( "schema", getNamespace( "xsd" ) );

        root.setAttribute( "elementFormDefault", "qualified" );
        root.setAttribute( "targetNamespace", tgtNamespace );

        for ( Namespace ns : prefixMap.values() ) {
            root.addNamespaceDeclaration( ns );
        }

        dox.addContent( root );
        return dox;
    }


    public boolean streamAll( OutputStream os ) {
        try {
//            os.write( serialize( getXSDSchema() ).getBytes() );
            os.write( getOWLSchema().getBytes() );
            for ( Document dox : schemas.values() ) {
                os.write( serialize( dox ).getBytes() );
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
        return true;
    }

    public boolean stream( File folder ) {
        try {

            for ( String ns : prefixMap.keySet() ) {
                FileOutputStream os = null;
                File target = null;
                byte[] schemaBytes = null;

                if ( "xsd".equals( ns ) || "xsi".equals( ns ) ) {
                    continue;
                } else if ( "tns".equals( ns ) ) {
                    target = new File( folder.getPath() + File.separator + getDefaultPackage() + ".xsd" );
                    target = checkForSchemaOverride( target, prefixMap.get( ns ) );

                    os = new FileOutputStream( target );
                    schemaBytes = serialize( getXSDSchema( prefixMap.get( "tns" ) ) ).getBytes();
                } else if ( "owl".equals( ns ) ) {
                    target = new File( folder.getPath() + File.separator + "owlThing.xsd" );
                    if ( ! target.exists() ) {
                        os = new FileOutputStream( target );
                        schemaBytes = getOWLSchema().getBytes();
                    }
                } else {
                    String path = folder.getPath() + File.separator + NameUtils.namespaceURIToPackage( prefixMap.get( ns ).getURI() ) + ".xsd";
                    target = new File( path );
                    target = checkForSchemaOverride( target, prefixMap.get( ns ) );


                    if ( schemas.containsKey( prefixMap.get( ns ) ) ) {
                        os = new FileOutputStream( target );
                        schemaBytes = serialize( schemas.get( prefixMap.get( ns ) ) ).getBytes();
                    } else {
                        throw new IllegalStateException( "XSD Model stream : unrecognized namespace " + ns );
//                        os = new FileOutputStream( path );
//                        schemaBytes = serialize( initDocument( namespaces.get( ns ).getURI() ) ).getBytes();
                    }
                }

                if ( os != null ) {
                    schemaLocations.put( prefixMap.get( ns ), target.getPath() );
                    os.write( schemaBytes );
                    os.flush();
                    os.close();
                }
            }

//            FileOutputStream fos = new FileOutputStream( folder.getPath() + File.separator + getDefaultPackage() + ".xsd" );
//            fos.write( serialize( getXSDSchema() ).getBytes() );
//            fos.flush();
//            fos.close();
//
//            FileOutputStream owl = new FileOutputStream( folder.getPath() + File.separator + "owlThing.xsd" );
//            owl.write( getOWLSchema().getBytes() );
//            owl.flush();
//            owl.close();
//
//
//            for ( Namespace ns : subSchemas.keySet() ) {
//                String subFileName = folder.getPath() + File.separator + NameUtils.namespaceURIToPackage( ns.getURI() ) + ".xsd" ;
//                FileOutputStream subFos = new FileOutputStream( subFileName );
//                subFos.write( serialize( subSchemas.get( ns ) ).getBytes() );
//                subFos.flush();
//                subFos.close();
//            }


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private File checkForSchemaOverride( File tgt, Namespace namespace ) {
        int j = 0;
        File target = tgt;
        String path = target.getPath();
        String ns = namespace.getURI();

        while ( target.exists() ) {
            target = new File( path.replace( ".xsd", "_" + ( j++ ) + ".xsd" ) );
            for ( Document dox : schemas.values() ) {
                List<Element> imports = dox.getRootElement().getChildren( "import", NamespaceUtils.getNamespaceByPrefix( "xsd" ) );
                for ( Element el : imports ) {
                    if ( el.getAttributeValue( "namespace" ).equals( ns ) ) {
                        el.setAttribute( "schemaLocation", target.getName() );
                    }
                }
            }
        }

        return target;
    }

    public Map<String, Namespace> getAssignedPrefixes() {
        return prefixMap;
    }

    public Namespace getNamespace(String ns) {
        return prefixMap.get( ns );
    }

    public Collection<Namespace> getNamespaces() {
        return prefixMap.values();
    }


    public void setNamespace( String prefix, String namespace ) {
        namespace = NamespaceUtils.removeLastSeparator( namespace );

        Namespace ns = Namespace.getNamespace( prefix, namespace );
        prefixMap.put( prefix, ns );
        reversePrefixMap.put( namespace, prefix );

        for ( Document dox : schemas.values() ) {
            dox.getRootElement().addNamespaceDeclaration( ns );
        }
    }


    public void addTrait( String name, Object trait ) {

        XSDTypeDescr descr = (XSDTypeDescr) trait;

        String prefix = reversePrefixMap.get( descr.getNamespace() );
        Document schema = getXSDSchema( prefixMap.get( prefix ) );

        schema.getRootElement().addContent( descr.getDeclaration() );
        schema.getRootElement().addContent( descr.getDefinition() );

        // now resolve required imports
        for ( String depNs : descr.getDependencies() ) {
            if ( ! reversePrefixMap.containsKey( depNs ) ) {
                String px = mapNamespaceToPrefix( depNs );
                createXSDSchema( Namespace.getNamespace( px, depNs ) );
            }
            addImport( schema, prefixMap.get( reversePrefixMap.get( depNs ) ) );
        }

    }

    @Deprecated
    public Document getXSDSchema() {
        return schemas.get( getNamespace( "tns" ) );
    }

//    private Document getXSDSchema( String type ) {
//        if ( type.indexOf( ":" ) >= 0 ) {
//            return getXSDSchema();
//        }
//        if ( definesType( getXSDSchema(), type ) ) {
//            return getXSDSchema();
//        }
//        for ( Document sub : subSchemas.values() ) {
//            if ( definesType(sub, type) ) {
//                return sub;
//            }
//        }
//        return getXSDSchema();
//    }

    private Document getXSDSchema( Namespace altNamespace ) {
        if ( schemas.containsKey( altNamespace ) ) {
            return schemas.get( altNamespace );
        } else {
            Document dox = createXSDSchema( altNamespace );
            return dox;
        }
    }

    private Document createXSDSchema( Namespace altNamespace ) {
        Document dox = initDocument( altNamespace.getURI() );
        schemas.put( altNamespace, dox );
        return dox;
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

        dox.getRootElement().addContent( 0, imp );
    }

    private String getSchemaName( Namespace altNamespace ) {
        if ( NamespaceUtils.getNamespaceByPrefix( "owl" ).getURI().equals( altNamespace.getURI() ) ) {
            return "owlThing.xsd";
        } else {
//            String prefix = NamespaceUtils.compareNamespaces( altNamespace.getURI(), getDefaultNamespace() )
//                    ? ""
//                    : ( "_" + altNamespace.getPrefix() );
//            return getName() + prefix + ".xsd";
            return NameUtils.namespaceURIToPackage( altNamespace.getURI() ) + ".xsd";
        }
    }


    public String mapNamespaceToPrefix( String namespace ) {
        namespace = NamespaceUtils.removeLastSeparator( namespace );
        String prefix;
        if ( StringUtils.isEmpty( namespace ) ) {
            prefix = "tns";
        } else if ( NamespaceUtils.compareNamespaces( getDefaultNamespace(), namespace ) ) {
            prefix = "tns";
        } else if ( reversePrefixMap.containsKey( namespace ) ) {
            prefix = reversePrefixMap.get( namespace );
        } else if ( NamespaceUtils.isKnownSchema(namespace) ) {
            prefix = NamespaceUtils.getPrefix( namespace );
            reversePrefixMap.put( namespace, prefix );
            setNamespace( prefix, namespace );
        } else {
            prefix = "ns" + ( reversePrefixMap.size() + 1 );
            reversePrefixMap.put( namespace, prefix );
            setNamespace( prefix, namespace );
        }
        return prefix;
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








    public static class XSDTypeDescr {

        private String name;
        private String namespace;
        private String effectiveName;
        private String effectiveType;

        private Element declaration;
        private Element definition;

        private Set<String> dependencies;

        public XSDTypeDescr( String name, String namespace, String effectiveName, String effectiveType, Element declaration, Element definition, Set<String> dependencies ) {
            this.name = name;
            this.namespace = namespace;
            this.effectiveName = effectiveName;
            this.effectiveType = effectiveType;
            this.declaration = declaration;
            this.definition = definition;
            this.dependencies = dependencies;
        }

        public String getName() {
            return name;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getEffectiveName() {
            return effectiveName;
        }

        public String getEffectiveType() {
            return effectiveType;
        }

        public Element getDeclaration() {
            return declaration;
        }

        public Element getDefinition() {
            return definition;
        }

        public Set<String> getDependencies() {
            return dependencies;
        }
    }

}
