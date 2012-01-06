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
import org.drools.semantics.builder.DLUtils;
import org.drools.semantics.builder.model.*;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    private static CompiledTemplate gettt;
    private static CompiledTemplate settt;

    @Override
    public CompiledOntoModel compile(OntoModel model) {

        SemanticXSDModel sxsdModel = (SemanticXSDModel) super.compile(model);
        sxsdModel.setBindings( createBindings( sxsdModel ) );

        return sxsdModel;
    }


    public void setModel(OntoModel model) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.XSDX, model );

        ((XSDModel) getModel()).setNamespace( "tns", DLUtils.reverse( model.getPackage() ) );
    }

    private String createBindings( SemanticXSDModel sxsdModel ) {

        try {

            String template = readFile( "bindings.xjb.template" );
            Map<String,Object> vars = new HashMap<String,Object>();
            vars.put( "package", getModel().getPackage() );
            vars.put( "concepts", getModel().getConcepts() );
            vars.put( "flat", this.getCurrentMode().equals( Mode.FLAT ) );
            vars.put( "properties", propCache );
            vars.put( "modelName", getModel().getName() );
            vars.put( "extra_code", prepareCodeExtensions( sxsdModel ) );
            String bindings = TemplateRuntime.eval( template, vars ).toString();


            System.out.println( vars.get("extra_code") );
            return bindings;
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
            return defaultBindings;
        }
    }

    private Map<String,String> prepareCodeExtensions(SemanticXSDModel sxsdModel) {
        Map<String,String> code = new HashMap<String, String>( sxsdModel.getConcepts().size() );
        for ( Concept con : sxsdModel.getConcepts() ) {
            StringBuilder sb = new StringBuilder("");

            for ( String propKey : con.getProperties().keySet() ) {
                PropertyRelation prop = con.getProperties().get( propKey );
                if ( prop.isRestricted() ) {

                    boolean isCollection = prop.getMaxCard() == null || prop.getMaxCard() > 1;
                    boolean isBaseCollection = prop.getBaseProperty().getMaxCard() == null || prop.getBaseProperty().getMaxCard() > 1;
                    String typeName = DLUtils.map( prop.getTarget().getName(), false) + ( ! prop.getTarget().isPrimitive() ? "__Type" : "" );
                    boolean isSimpleBoolean = prop.getTarget().getName().equals("xsd:boolean") && ! isCollection;
                    boolean isBaseSimpleBoolean = prop.getBaseProperty().getTarget().getName().equals("xsd:boolean") && ! isBaseCollection;

                    String getter = ((isSimpleBoolean && ! isCollection) ? "is" : "get") + DLUtils.compactUpperCase(prop.getName());
                    String setter = "set" + DLUtils.compactUpperCase(prop.getName());
                    String baseGetter = ((isBaseSimpleBoolean && ! isBaseCollection) ? "is" : "get") + DLUtils.compactUpperCase( prop.getBaseProperty().getName() );
                    String baseSetter = "set" + DLUtils.compactUpperCase( prop.getBaseProperty().getName() );

                    Map<String,Object> vars = new HashMap<String, Object>();
                    vars.put( "isCollection", isCollection );
                    vars.put( "typeName", typeName );
                    vars.put( "isSimpleBoolean", isSimpleBoolean );
                    vars.put( "getter", getter );
                    vars.put( "setter", setter );
                    vars.put( "base", prop.getBaseProperty() );
                    vars.put( "isBaseCollection", isBaseCollection );
                    vars.put( "isBaseSimpleBoolean", prop.getTarget().getName().equals("xsd:boolean") && ! isCollection );
                    vars.put( "baseGetter", baseGetter );
                    vars.put( "baseSetter", baseSetter );
                    vars.put( "min", prop.getMinCard() );
                    vars.put( "max", prop.getMaxCard() );


                    String getProperty = TemplateRuntime.execute( getGetterTemplate(), DLUtils.getInstance(), vars ).toString();

                    sb.append( getProperty );

                    String setProperty = TemplateRuntime.execute( getSetterTemplate(), DLUtils.getInstance(), vars ).toString();

                    sb.append( setProperty );

                }
            }
            // TODO The approach I was hoping to adopt does not work with some persistency frameworks. Need to adopt the dual one
            // code.put( con.getName(), sb.toString() );
            code.put( con.getName(), "" );
        }
        return code;
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
        String fullPath = SemanticXSDModelCompiler.class.getPackage().getName().replace(".",File.separator)
                + File.separatorChar
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
}



