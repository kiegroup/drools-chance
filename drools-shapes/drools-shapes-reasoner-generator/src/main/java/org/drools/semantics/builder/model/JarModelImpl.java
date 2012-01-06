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

package org.drools.semantics.builder.model;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarModelImpl extends JavaInterfaceModelImpl implements JarModel {


    private Map<String, Holder> compiledTraits = new HashMap<String, Holder>();


    JarModelImpl() {

    }

    public byte[] getCompiledTrait( String name ) {
        return compiledTraits.containsKey( name ) ?
                compiledTraits.get( name ).getBytes()
                : null;
    }

    public void addCompiledTrait( String name, Holder compiled ) {
        compiledTraits.put( name, compiled );
    }


    public ByteArrayOutputStream buildJar( ) {
        Date now = new Date();
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        try {
            // Open archive file
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            JarOutputStream out = new JarOutputStream(stream, manifest);

            for ( String name : getTraitNames() ) {

                System.out.println("Adding " + name);

                // Add archive entry
                JarEntry jarAdd = new JarEntry( getPackage().replace(".","/") + "/" + name + ".java" );
                jarAdd.setTime( now.getTime() );
                out.putNextEntry(jarAdd);

                // Write file to archive
                out.write( ((String) getTrait(name) ).getBytes() );



                // Add archive entry
                jarAdd = new JarEntry( getPackage().replace(".","/") + "/" + name + ".class" );
                jarAdd.setTime( now.getTime() );
                out.putNextEntry(jarAdd);

                // Write file to archive
                out.write( getCompiledTrait(name) );

            }

            out.close();
            stream.close();
            System.out.println("Adding completed OK");
            return stream;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }

        return null;
    }


}
