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

package org.drools.semantics.lang;

import org.kie.api.KieServices;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DLReasonerTemplateManager {

    private static final String SEP = File.separator;
    
    public static final String BASE_PACK = "org.drools.semantics.builder";
    protected static final String RESOURCE_PATH = BASE_PACK.replace( ".", SEP );
    protected static final String TEMPLATE_PATH = SEP + RESOURCE_PATH + SEP + "templates" + SEP;


    public enum DLFamilies {
        FALC;
    }


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


    public static TemplateRegistry tableauRegistry;



    static {
        tableauRegistry = new SimpleTemplateRegistry();

        buildRegistry( tableauRegistry, FALC_TABLEAU_TEMPLATES );
    }


    public static TemplateRegistry getTableauRegistry( DLFamilies target ) {
        return tableauRegistry;
    }


    private static void buildRegistry(TemplateRegistry registry, String[] traits) {
        KieServices ks = KieServices.Factory.get();
        for (String ntempl : traits) {
            try {
                String path = TEMPLATE_PATH + ntempl.replace( "/", SEP );
                InputStream stream = ks.getResources().newClassPathResource( path, DLReasonerTemplateManager.class ).getInputStream();

                registry.addNamedTemplate( path.substring( path.lastIndexOf( File.separator ) + 1 ),
                        TemplateCompiler.compileTemplate(stream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

















}
