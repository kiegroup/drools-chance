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

package org.drools.informer.write.questionnaire;


import org.drools.informer.util.TemplateManager;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.mvel2.templates.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class LoaderTemplateManager extends TemplateManager{


    // singleton
    private static LoaderTemplateManager instance;
    public static LoaderTemplateManager getInstance() {
        if (instance == null) {
            instance = new LoaderTemplateManager();
        }
        return instance;
    }
    protected LoaderTemplateManager() {
        setRegistry(new SimpleTemplateRegistry());
        buildRegistry(getRegistry());
    }



    public static final String INFORMER_PACK = "org.drools.informer";
    protected static final String TEMPLATE_PATH = "/org/drools/informer/load/";

    protected static final String[] NAMED_TEMPLATES = new String[] {

            "templates/header.drlt",
            "templates/questionnaire.drlt",
            "templates/remove.drlt",
            "templates/trigger.drlt",
            "templates/include.drlt",
            "templates/globalImpact.drlt",
            "templates/validation.drlt",
            "templates/branching.drlt",


            "templates/pageBits/commonFactCreation.drlt",
            "templates/pageBits/subItems.drlt"

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
