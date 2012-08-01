
package org.drools.informer.interaction;


import org.drools.io.ResourceFactory;
import org.mvel2.templates.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TemplateBuilder {

    private static TemplateRegistry kmr2Registry;

    protected static Set<String> NAMED_TEMPLATES; 
            
    
    
    

    private static void init() {
        kmr2Registry = new SimpleTemplateRegistry();
        NAMED_TEMPLATES = new HashSet<String>(); 
    }


    public static TemplateRegistry getRegistry() {
        if ( kmr2Registry == null ) {
            init();
        }
        return kmr2Registry;
    }
    
    
    public static void addTemplate( String path ) {
            try {
                InputStream stream = ResourceFactory.newClassPathResource( path, TemplateBuilder.class ).getInputStream();

                getRegistry().addNamedTemplate( path.substring( path.lastIndexOf('/') + 1 ),
                                               TemplateCompiler.compileTemplate(stream) );

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public static String applyTemplate( String template, Map args ) {
        return (String) TemplateRuntime.execute( TemplateCompiler.compileTemplate( template ), args );
    }

    public static String applyNamedTemplate( String templateName, Map args ) {
        CompiledTemplate template = getRegistry().getNamedTemplate( templateName );
        return (String) TemplateRuntime.execute( template, args );
    }

}

