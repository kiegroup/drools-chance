package org.drools.beliefs.provenance;

import org.kie.internal.io.ResourceFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

import java.io.IOException;
import java.io.InputStream;

public class TemplateManager {

    private static final String SEP = "/";
    
    public static final String BASE_PACK = "test";
    protected static final String RESOURCE_PATH = BASE_PACK.replace( ".", SEP );
    protected static final String TEMPLATE_PATH = SEP + RESOURCE_PATH + SEP + "templates" + SEP;


    protected static final String[] NAMED_TEMPLATES = new String[] {
            "example.mvel"
    };

    public static TemplateRegistry registry;


    static {
        registry = new SimpleTemplateRegistry();
        buildRegistry( registry, NAMED_TEMPLATES );
    }

    public static TemplateRegistry getRegistry() {
        return registry;
    }


    private static void buildRegistry(TemplateRegistry registry, String[] traits) {
        for (String ntempl : traits) {
            try {
                String path = TEMPLATE_PATH + ntempl.replace( "/", SEP );
                InputStream stream = ResourceFactory.newClassPathResource( path, TemplateManager.class ).getInputStream();

                registry.addNamedTemplate( path.substring( path.lastIndexOf( SEP ) + 1, path.lastIndexOf( '.' ) ),
                        TemplateCompiler.compileTemplate(stream) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

















}
