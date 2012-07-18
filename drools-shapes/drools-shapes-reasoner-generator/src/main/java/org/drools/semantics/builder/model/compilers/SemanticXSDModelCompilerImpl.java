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

import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.DLTemplateManager;
import org.drools.semantics.utils.NameUtils;
import org.drools.semantics.builder.model.*;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SemanticXSDModelCompilerImpl extends XSDModelCompilerImpl implements SemanticXSDModelCompiler {


    private static final String defaultBindings = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<bindings xmlns=\"http://java.sun.com/xml/ns/jaxb\"\n" +
            "          xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\"\n" +
            "          xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\"\n" +
            "          xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "          xmlns:inheritance=\"http://jaxb2-commons.dev.java.net/basic/inheritance\"\n" +
            "          xsi:schemaLocation=\"http://java.sun.com/xml/ns/jaxb http://java.sun.com/xml/ns/jaxb/bindingschema_2_0.xsd\"\n" +
            "          version=\"2.1\"\n" +
            "          extensionBindingPrefixes=\"xjc\" >\n" +
            "  <bindings>\n" +
            "    <globalBindings localScoping=\"toplevel\" >\n" +
            "      <serializable/>\n" +
            "      <xjc:simple/>\n" +
            "      <xjc:treatRestrictionLikeNewType/>\n" +
            "    </globalBindings>\n" +
            "\n" +
            "  </bindings>\n" +
            "</bindings>";


    private TemplateRegistry registry = DLTemplateManager.getDataModelRegistry( ModelFactory.CompileTarget.XSDX );

    protected static final String semGetterTemplateName = "semGetter.drlt";
    protected static final String semSetterTemplateName = "semSetter.drlt";
    protected static final String propChainTemplateName = "propChainGetter.drlt";

    private static CompiledTemplate gettt;
    private static CompiledTemplate settt;
    private static CompiledTemplate chant;

    @Override
    public CompiledOntoModel compile(OntoModel model) {

        SemanticXSDModel sxsdModel = (SemanticXSDModel) super.compile(model);
        sxsdModel.setBindings( createBindings( sxsdModel ) );

        sxsdModel.setIndex( createIndex( sxsdModel ) );

        sxsdModel.setIndividualFactory( compileIntoFactory( sxsdModel ) );

        return sxsdModel;
    }



    private String compileIntoFactory(SemanticXSDModel sxsdModel) {
        try {
            Map<String,Object> vars = new HashMap<String, Object>();
            vars.put( "package", sxsdModel.getPackage() );
            vars.put( "individuals", sxsdModel.getIndividuals() );
            String index = getTemplatedCode( "IndividualFactory", vars, ModelFactory.CompileTarget.JAVA );
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
            vars.put( "package", sxsdModel.getPackage() );
            vars.put( "klasses", sxsdModel.getConcepts() );
            String index = TemplateRuntime.eval( template, vars ).toString();
            return index;
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }

    }


    public void setModel(OntoModel model) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.XSDX, model );

        ((XSDModel) getModel()).setNamespace( "tns", model.getNamespace() );
    }

    private String createBindings( SemanticXSDModel sxsdModel ) {

        try {



            String template = readFile( "bindings.xjb.template" );
            Map<String,Object> vars = new HashMap<String,Object>();
            vars.put( "package", getModel().getPackage() );
            vars.put( "namespace", getModel().getNamespace() );
            vars.put( "concepts", getModel().getConcepts() );
            vars.put( "flat", this.getCurrentMode().equals( Mode.FLAT ) || this.getCurrentMode().equals( Mode.LEVELLED ) );
            vars.put( "properties", propCache );
            vars.put( "modelName", getModel().getName() );
            vars.put( "extra_code", prepareCodeExtensions( sxsdModel ) );
            String bindings = TemplateRuntime.eval( template, NameUtils.getInstance(), vars ).toString();


//            System.out.println( vars.get("extra_code") );
            return bindings;
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
            return defaultBindings;
        }
    }

    private Map<String,String> prepareCodeExtensions(SemanticXSDModel sxsdModel) {
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
        return TemplateRuntime.execute (
                DLTemplateManager.getDataModelRegistry( target  ).getNamedTemplate( template + ".template" ),
                NameUtils.getInstance(),
                vars ).toString();

    }
}



