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
