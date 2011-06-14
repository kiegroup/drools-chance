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
