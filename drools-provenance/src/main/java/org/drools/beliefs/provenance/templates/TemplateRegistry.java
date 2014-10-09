package org.drools.beliefs.provenance.templates;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class TemplateRegistry {

    private SimpleTemplateRegistry registry;

    private static TemplateRegistry instance = new TemplateRegistry();

    public static final String TEMPLATE_PATH = "/displayTemplates";

    public static TemplateRegistry getInstance() {
        return instance;
    }

    protected TemplateRegistry() {
        registry = new SimpleTemplateRegistry();
        prepareTemplates();
    }

    private void prepareTemplates() {
        try {
            String path = TEMPLATE_PATH;
            URL res = TemplateRegistry.class.getResource( path );
            File folder = null;
            try {
                folder = new File( res.toURI() );
            } catch (URISyntaxException e) {
                throw new IllegalStateException("File path is not a valid URI", e);
            }
            if ( folder != null && folder.isDirectory() ) {
                for ( File child : folder.listFiles() ) {
                    InputStream stream = new FileInputStream( child );
                    if ( stream != null ) {
                        String name = child.getPath().substring( child.getPath().lastIndexOf( File.separator ) + 1 );
                        name = name.substring( 0, name.lastIndexOf( '.' ) );
                        registry.addNamedTemplate( name,
                                                   TemplateCompiler.compileTemplate( read( stream ) ) );
                    }
                }
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
