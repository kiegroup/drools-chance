package org.drools.informer.util;


import org.drools.io.ResourceFactory;
import org.mvel2.templates.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class TemplateManager {

    private List<String> errors;
    private TemplateRegistry registry;


    protected abstract String[] getNamedTemplates();
    protected abstract String getTemplatePath();

    public TemplateRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(TemplateRegistry registry) {
        this.registry = registry;
    }

    protected void buildRegistry(TemplateRegistry registry) {
        for (String ntempl : getNamedTemplates()) {
            try {
                String path = getTemplatePath()+ntempl;
                InputStream stream = ResourceFactory.newClassPathResource(path, this.getClass()).getInputStream();

                registry.addNamedTemplate( path.substring(path.lastIndexOf('/') + 1),
                                           TemplateCompiler.compileTemplate(stream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void applyTemplate(String templateName, Object context, Map vars, Formatter fmt) {
        CompiledTemplate template = (CompiledTemplate) getRegistry().getNamedTemplate(templateName);
        try {
            fmt.out().append(TemplateRuntime.execute(template, context, vars).toString());
        } catch (IOException ioe) {
            addError(ioe.getMessage());
        }
    }


    public String applyTemplate(String templateName, Object context, Map vars) {
        CompiledTemplate template = (CompiledTemplate) getRegistry().getNamedTemplate(templateName);
        return (TemplateRuntime.execute(template, context, vars).toString());
    }

    protected void addError(String error) {
        if (errors == null) {
            errors = new LinkedList<String>();
        }
        errors.add(error);
    }


    public List<String> getErrors() {
        return errors;
    }


}
