package org.drools.beliefs.provenance.templates;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class TemplateRegistry {

    private SimpleTemplateRegistry registry;

    private static TemplateRegistry instance = new TemplateRegistry();

    public static final String TEMPLATE_PATH = "classpath*:/displayTemplates/*";

    public static TemplateRegistry getInstance() {
        return instance;
    }

    protected TemplateRegistry() {
        registry = new SimpleTemplateRegistry();
        prepareTemplates();
    }

    private void prepareTemplates() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            Resource[] resources = resolver.getResources(TEMPLATE_PATH);

            for(Resource resource : resources) {
                String path = resource.getURL().getPath();
                String name = path.substring( path.lastIndexOf( '/' ) + 1 );
                name = name.substring( 0, name.lastIndexOf( '.' ) );
                registry.addNamedTemplate( name,
                                           TemplateCompiler.compileTemplate( read( resource.getInputStream() ) ) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CompiledTemplate getTemplate( String name ) {
        return registry.getNamedTemplate( name );
    }

    public CompiledTemplate compileAndCache( String name, String inline ) {
        if ( ! registry.contains( name ) ) {
            registry.addNamedTemplate( name, TemplateCompiler.compileTemplate( sanitize( inline ) ) );
        }
        return getTemplate( name );
    }


    private String read( InputStream stream ) {
        try {
            byte[] data = new byte[ stream.available() ];
            stream.read( data );
            return sanitize( new String( data ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }

    public static String sanitize( String inline ) {
        return inline.replaceAll( "\\$", "_" );
    }

    public static Map<String, Object> sanitize( Map<String,Object> map ) {
        for ( String key : new ArrayList<String>( map.keySet() ) ) {
            if ( key.contains( "$" ) ) {
                String newKey = sanitize( key );
                map.put( newKey, map.remove( key ) );
            }
        }
        return map;
    }
}
