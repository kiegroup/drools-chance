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

package org.drools.semantics.builder.model.compilers;

import org.drools.semantics.builder.DLTemplateManager;
import org.drools.semantics.builder.model.CompiledOntoModel;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.semantics.builder.model.SemanticXSDModel;
import org.drools.semantics.builder.model.XSDModel;
import org.drools.semantics.utils.NameUtils;
import org.drools.semantics.utils.NamespaceUtils;
import org.jdom.Namespace;
import org.kie.internal.io.ResourceFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticXSDModelCompilerImpl extends XSDModelCompilerImpl implements SemanticXSDModelCompiler {


    private TemplateRegistry registry = DLTemplateManager.getDataModelRegistry( ModelFactory.CompileTarget.XSDX );

    protected static final String semGetterTemplateName = "semGetter.drlt";
    protected static final String semSetterTemplateName = "semSetter.drlt";
    protected static final String propChainTemplateName = "propChainGetter.drlt";

    private static CompiledTemplate gettt;
    private static CompiledTemplate settt;
    private static CompiledTemplate chant;


    @Override
    public CompiledOntoModel compile( OntoModel model ) {

        SemanticXSDModel sxsdModel = (SemanticXSDModel) super.compile( model );

        for ( Namespace ns : sxsdModel.getNamespaces() ) {
            sxsdModel.setBindings( ns.getURI(), createBindings( ns.getURI(), sxsdModel ) );
        }

        mergeNamespacedPackageInfo( sxsdModel );

        sxsdModel.setIndex( createIndex( sxsdModel ) );

        sxsdModel.setIndividualFactory( compileIntoFactory( sxsdModel ) );

        sxsdModel.setEmpireConfig( createEmpireConfig( Arrays.asList( sxsdModel.getName() ) ) );

        sxsdModel.setPersistenceXml( createPersistenceXml() );

        return sxsdModel;
    }




    private String createPersistenceXml() {
        try {
            String template = readFile( "persistence-template-hibernate.xml.template" );
            Map<String,Object> vars = new HashMap<String, Object>();
            String index = TemplateRuntime.eval( template, vars ).toString();
            return index;
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }

    private String createEmpireConfig( Collection<String> models ) {
        try {
            String template = readFile( "empire.configuration.file.template" );
            Map<String,Object> vars = new HashMap<String, Object>();
            vars.put( "index", "empire.annotation.index" );
            vars.put( "models", models );
            vars.put( "datasources", Arrays.asList( "sesame", "jena" ) );
            String index = TemplateRuntime.eval( template, vars ).toString();
            return index;
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }


    private String createIndex( SemanticXSDModel sxsdModel ) {
        try {
            String template = readFile( "empire.annotation.index.template" );
            Map<String,Object> vars = new HashMap<String, Object>();
            vars.put( "klasses", sxsdModel.getConcepts() );
            String index = TemplateRuntime.eval( template, vars ).toString();
            return index;
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }

    }

    private String createNamespacedPackageInfo( Namespace ns, String packageName, Map<String,Namespace> prefixMap ) {
        HashMap map = new HashMap();
        map.put( "namespace", ns.getURI() );
        map.put( "pack", packageName );
        map.put( "prefixMap", prefixMap );
        String fix = SemanticXSDModelCompilerImpl.getTemplatedCode( "package-info.java", map, ModelFactory.CompileTarget.JAVA );
        return fix;
    }




    private String compileIntoFactory( SemanticXSDModel sxsdModel ) {
        try {
            Map<String,Object> vars = new HashMap<String, Object>();
            vars.put( "package", sxsdModel.getDefaultPackage() );
            vars.put( "individuals", sxsdModel.getIndividuals() );
            String index = getTemplatedCode( "IndividualFactory", vars, ModelFactory.CompileTarget.JAVA );
            return index;
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }


    public ModelFactory.CompileTarget getCompileTarget() {
        return ModelFactory.CompileTarget.XSDX;
    }

    private Document createBindings( String ns, SemanticXSDModel sxsdModel ) {

        if ( "http://www.w3.org/2002/07/owl".equals( ns ) ) {
            return getGlobalBindings();
        }

        String prefix = ((XSDModel) getModel()).mapNamespaceToPrefix( ns );
        try {
            String template = readFile( "bindings.xjb.template" );
            Collection<Concept> cons = filterConceptsByNS( getModel().getConcepts(), ns );
            cons = filterUnneedecConcepts( cons, model );

            Map<String,Object> vars = new HashMap<String,Object>();
            vars.put( "package", NameUtils.namespaceURIToPackage( ns ) );
            vars.put( "namespace", ns );
            vars.put( "concepts", cons );
            vars.put( "flat", getModel().getMode() != OntoModel.Mode.HIERARCHY );
            vars.put( "properties", propCache );
            vars.put( "modelName", getModel().getName() );
            vars.put( "schemaLocation", NameUtils.namespaceURIToPackage( ns ) );
//            vars.put( "schemaLocation", getModel().getName() + ( prefix == null ? "" : ( "_" + prefix ) ) );
            vars.put( "extra_code", prepareCodeExtensions( sxsdModel ) );

            String bindings = TemplateRuntime.eval( template, NameUtils.getInstance(), vars ).toString();

            DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
            Document dox = df.newDocumentBuilder().parse( new InputSource( new StringReader( bindings ) ) );
            dox.normalizeDocument();

            return dox;
        } catch ( Exception ioe ) {
            ioe.printStackTrace();
            return null;
        }

    }

    private Collection<Concept> filterUnneedecConcepts( Collection<Concept> cons, CompiledOntoModel model ) {
        ArrayList<Concept> filtered = new ArrayList<Concept>( cons.size() );
        for ( Concept c : cons ) {
            if ( model.getMode() != OntoModel.Mode.FLAT && model.getMode() != OntoModel.Mode.NONE && model.getMode() != OntoModel.Mode.DATABASE ) {
                filtered.add( c );
            } else {
                if ( ! c.isAbstrakt() ) {
                    filtered.add( c );
                }
            }
        }
        return filtered;
    }


    public void mergeNamespacedPackageInfo( SemanticXSDModel model ) {
        for ( Namespace ns : model.getNamespaces() ) {
            String info = createNamespacedPackageInfo( ns, NameUtils.namespaceURIToPackage( ns.getURI() ), model.getAssignedPrefixes() );
            model.addNamespacedPackageInfo( ns, info );
        }
    }

    public void mergeEmpireConfig( File preexistingEmpireConfig, SemanticXSDModel model )  {
        try {
            FileInputStream bis = new FileInputStream( preexistingEmpireConfig );
            byte[] an = new byte[ bis.available() ];
            bis.read(  an  );


            Set<String> matchSet = new HashSet<String>();
            Pattern regex = Pattern.compile( "\\d.name = (.+)-sesame-data-source", Pattern.MULTILINE );

            Matcher regexMatcher;
            regexMatcher = regex.matcher( new String( an ) );
            while ( regexMatcher.find() ) {
                matchSet.add( regexMatcher.group( 1 ) );
            }
            regexMatcher = regex.matcher( model.getEmpireConfig() );
            while ( regexMatcher.find() ) {
                matchSet.add( regexMatcher.group( 1 ) );
            }

            String config = createEmpireConfig( matchSet );

            model.setEmpireConfig( config );

        } catch ( FileNotFoundException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch ( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void mergeIndex( File preexistingIndex, SemanticXSDModel model ) {
        FileInputStream bis = null;
        try {
            bis = new FileInputStream( preexistingIndex );
            byte[] an = new byte[ bis.available() ];
            bis.read(  an  );

            String old = new String( an );
            String nju = model.getIndex();
            Set<String> oldTypes = new HashSet<String>();
            int j = old.indexOf( "javax.persistence.NamedQuery=" ) -1;

            StringBuilder newIndex = new StringBuilder();
            newIndex.append( old.substring( 0, j ) );

            StringTokenizer tok = new StringTokenizer( old, "=,\n" );
            while ( tok.hasMoreTokens() ) {
                oldTypes.add( tok.nextToken() );
            }
            tok = new StringTokenizer( nju, "=,\n" );
            while ( tok.hasMoreTokens() ) {
                String t = tok.nextToken();
                if ( ! oldTypes.contains( t ) ) {
                    newIndex.append( "," ).append( tok.nextToken() );
                }
            }

            newIndex.append( old.substring( j ) );

//            System.out.println( "OLD INDEX " + old );
//            System.out.println( "NEW INDEX " + newIndex );

            model.setIndex( newIndex.toString() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
   }

//    public void mergePersistenceXml( File preexistingPersistenceXml, SemanticXSDModel model ) {
//        FileInputStream bis = null;
//        try {
//            bis = new FileInputStream( preexistingPersistenceXml );
//            byte[] an = new byte[ bis.available() ];
//            bis.read(  an  );
//
//            String old = new String( an );
//
//            System.out.println( "OLD PERX " + old );
//            System.out.println( "NEW PERX " + model.getPersistenceXml() );
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        }
//
//        throw new UnsupportedOperationException( "TODO" );
//    }









    private Collection<Concept> filterConceptsByNS( List<Concept> concepts, String ns ) {
        List<Concept> filtered = new ArrayList<Concept>();
        for ( Concept con : concepts ) {
            if ( NamespaceUtils.compareNamespaces(con.getNamespace(), ns) ) {
                filtered.add( con );
            }
        }
        return filtered;
    }

    private Map<String,String> prepareCodeExtensions( SemanticXSDModel sxsdModel ) {
        Map<String,String> code = new HashMap<String, String>( sxsdModel.getConcepts().size() );

        List<Concept> filteredConcepts = sxsdModel.getConcepts();

        for ( Concept con : filteredConcepts ) {
            StringBuilder sb = new StringBuilder("");

            Map<String, Set<PropertyRelation>> restrictions = buildRestrictionMatrix( con.getProperties().values() );

            for ( String propKey : con.getProperties().keySet() ) {
                PropertyRelation prop = con.getProperties().get( propKey );
                if ( ! prop.isRestricted() && ! prop.isTransient() ) {


                    String setter = prop.getSetter();
                    String adder = prop.getSetter().replace( "set", "add" );
                    String toggler = prop.getSetter().replace( "set", "remove" );

                    Map<String,Object> vars = new HashMap<String, Object>();
                    vars.put( "typeName", prop.getTypeName() );
                    vars.put( "isSimpleBoolean", prop.isSimpleBoolean() );
                    vars.put( "isCollection", prop.isCollection() );

                    vars.put( "name", prop.getName() );
                    vars.put( "getter", prop.getGetter() );
                    vars.put( "setter", setter );
                    vars.put( "adder", adder );
                    vars.put( "toggler", toggler );

                    vars.put( "min", prop.getMinCard() );
                    vars.put( "max", prop.getMaxCard() );

                    Set<PropertyRelation> restrs = restrictions.get( prop.getName() );
                    vars.put( "restrictions", restrs != null ? restrs : Collections.emptySet() );


//                    String getProperty = TemplateRuntime.execute( getGetterTemplate(), NameUtils.getInstance(), vars ).toString();
//
//                    sb.append( getProperty );
//
//                    String setProperty = TemplateRuntime.execute( getSetterTemplate(), NameUtils.getInstance(), vars ).toString();
//
//                    sb.append( setProperty );

                } else {
                    if ( prop.isChain() ) {

                        Map<String,Object> vars = new HashMap<String, Object>();
                        vars.put( "typeName", prop.getTypeName() );
                        vars.put( "isSimpleBoolean", prop.isSimpleBoolean() );
                        vars.put( "isCollection", prop.isCollection() );
                        vars.put( "getter", prop.getGetter() );
                        vars.put( "min", prop.getMinCard() );
                        vars.put( "max", prop.getMaxCard() );

                        vars.put( "chains", prop.getChains() );

//                        String getChain = TemplateRuntime.execute( getChainTemplate(), NameUtils.getInstance(), vars ).toString();
//
//                        sb.append( getChain );
                    }
                }
            }
            code.put( con.getName(), sb.toString() );
            //code.put( con.getName(), "" );
        }
        return code;
    }

    private Map<String, Set<PropertyRelation>> buildRestrictionMatrix( Collection<PropertyRelation> rels ) {
        Map<String, Set<PropertyRelation>> matrix = new HashMap<String, Set<PropertyRelation>>();
        for ( PropertyRelation rel : rels ) {
            if ( rel.isRestricted() ) {
                Set<PropertyRelation> others = matrix.get( rel.getBaseProperty().getName() );
                if ( others == null ) {
                    others = new HashSet<PropertyRelation>();
                    matrix.put( rel.getBaseProperty().getName(), others );
                }
                others.add( rel );
            }
        }
        return matrix;
    }


    protected CompiledTemplate getChainTemplate() {
        if ( chant == null ) {
            chant = DLTemplateManager.getDataModelRegistry( ModelFactory.CompileTarget.XSDX ).getNamedTemplate( propChainTemplateName );
        }
        return chant;
    }

    protected CompiledTemplate getGetterTemplate() {
        if ( gettt == null ) {
            gettt = DLTemplateManager.getDataModelRegistry( ModelFactory.CompileTarget.XSDX ).getNamedTemplate( semGetterTemplateName );
        }
        return gettt;
    }

    protected CompiledTemplate getSetterTemplate() {
        if ( settt == null ) {
            settt = DLTemplateManager.getDataModelRegistry( ModelFactory.CompileTarget.XSDX ).getNamedTemplate( semSetterTemplateName );
        }
        return settt;
    }

    private static String readFile(String name) throws IOException {
        String fullPath = SemanticXSDModelCompiler.class.getPackage().getName().replace( ".", "/" )
                + "/"
                + name;

        InputStream stream = ResourceFactory.newClassPathResource( fullPath ).getInputStream();
        try {
            byte[] data = new byte[ stream.available() ];
            stream.read(data);
            return new String( data );
        }
        finally {
            stream.close();
        }
    }

    public static String getTemplatedCode( String template, Map<String, Object> vars ) {
        return getTemplatedCode( template, vars, ModelFactory.CompileTarget.XSDX );
    }

    public static String getTemplatedCode( String template, Map<String, Object> vars, ModelFactory.CompileTarget target ) {
        return (
                DLTemplateManager.getDataModelRegistry( target  ).getNamedTemplate( template + ".template" ),
                NameUtils.getInstance(),
                vars ).toString();

    }

    public Document getGlobalBindings() {
        InputStream bindingsIS = null;
        try {
            bindingsIS = ResourceFactory.newClassPathResource("org/drools/semantics/builder/model/compilers/global.xjb").getInputStream();
            byte[] data = new byte[ bindingsIS.available() ];
            bindingsIS.read( data );
            DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
            return df.newDocumentBuilder().parse( new ByteArrayInputStream( data ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
}



