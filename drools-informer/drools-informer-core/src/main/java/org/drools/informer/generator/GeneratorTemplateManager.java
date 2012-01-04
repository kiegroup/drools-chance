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

package org.drools.informer.generator;


import org.drools.informer.util.TemplateManager;
import org.mvel2.templates.SimpleTemplateRegistry;


public class GeneratorTemplateManager extends TemplateManager {



    // singleton
    private static GeneratorTemplateManager instance;
    public static GeneratorTemplateManager getInstance() {
        if (instance == null) {
            instance = new GeneratorTemplateManager();
        }
        return instance;
    }
    protected GeneratorTemplateManager() {
        setRegistry(new SimpleTemplateRegistry());
        buildRegistry(getRegistry());
    }



    public static final String INFORMER_PACK = "org.drools.informer";
    protected static final String TEMPLATE_PATH = "/org/drools/informer/";

    protected static final String[] NAMED_TEMPLATES = new String[] {

            "generator/templates/detachQuestion.drlt",
            "generator/templates/attachQuestion.drlt"

    };


    @Override
    protected String[] getNamedTemplates() {
        return NAMED_TEMPLATES;
    }

    @Override
    protected String getTemplatePath() {
        return TEMPLATE_PATH;
    }
}
