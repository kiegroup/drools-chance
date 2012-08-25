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

package org.drools.semantics.builder;

import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.model.ModelFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DLTemplateManager {

    private static final String SEP = File.separator;
    
    public static final String BASE_PACK = "org.drools.semantics";
    protected static final String RESOURCE_PATH = BASE_PACK.replace( ".", SEP );
    protected static final String TEMPLATE_PATH = SEP + RESOURCE_PATH + SEP + "templates" + SEP;


    public enum DLFamilies {
        FALC;
    }


    protected static final String[] NAMED_TEMPLATES_TRAITS = new String[] {
            "model/drl/trait.drlt"
    };
    protected static final String[] NAMED_TEMPLATES_JAVA = new String[] {
            "model/java/TraitInterface.template",
            "model/java/ShadowInterface.template",
            "model/java/IndividualFactory.template",
            "model/java/package-info.java.template"

    };

    protected static final String[] ACCESSOR_TEMPLATES_JAVA = new String[] {
            "model/java/baseGetterSetter.template",
            "model/java/baseAddRemove.template",
            "model/java/genericAdd.template",
            "model/java/restrictedGetterSetter.template",
            "model/java/restrictedAddRemove.template",
            "model/java/chainGetter.template",
            "model/java/shadowConstructor.template",
            "model/java/onCycleDetected.template",
            "model/java/equals.template",
            "model/java/hashKy.template",
            "model/java/defaultConstructor.template",
            "model/java/metaDescr.template",
            "model/java/inferredGetter.template"
    };
    protected static final String[] FALC_TABLEAU_TEMPLATES = new String[] {
            "tableau/falc/header.drlt",
            "tableau/falc/and.drlt",
            "tableau/falc/nand.drlt",
            "tableau/falc/or.drlt",
            "tableau/falc/nor.drlt",
            "tableau/falc/exists.drlt",
            "tableau/falc/forall.drlt",
            "tableau/falc/type.drlt",
            "tableau/falc/negtype.drlt"
    };


    public static TemplateRegistry traitRegistry;
    public static TemplateRegistry javaRegistry;
    public static TemplateRegistry tableauRegistry;
    public static TemplateRegistry accessorsRegistry;



    static {
        traitRegistry = new SimpleTemplateRegistry();
        javaRegistry = new SimpleTemplateRegistry();
        tableauRegistry = new SimpleTemplateRegistry();
        accessorsRegistry = new SimpleTemplateRegistry();

        buildRegistry( traitRegistry, NAMED_TEMPLATES_TRAITS );
        buildRegistry( javaRegistry, NAMED_TEMPLATES_JAVA );
        buildRegistry( accessorsRegistry, ACCESSOR_TEMPLATES_JAVA );
        buildRegistry( tableauRegistry, FALC_TABLEAU_TEMPLATES );
    }


    public static TemplateRegistry getDataModelRegistry( ModelFactory.CompileTarget target ) {
        switch ( target ) {
            case XSDX : return accessorsRegistry;
            case JAVA : return javaRegistry;
            case DRL  : return traitRegistry;
            default   : return traitRegistry;
        }
    }

    public static TemplateRegistry getTableauRegistry( DLFamilies target ) {
        return tableauRegistry;
    }



    private static void buildRegistry(TemplateRegistry registry, String[] traits) {
        for (String ntempl : traits) {
            try {
                String path = TEMPLATE_PATH + ntempl.replace( "/", SEP );
                InputStream stream = ResourceFactory.newClassPathResource(path, DLTemplateManager.class).getInputStream();

                registry.addNamedTemplate( path.substring( path.lastIndexOf( File.separator ) + 1 ),
                        TemplateCompiler.compileTemplate(stream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

















}
