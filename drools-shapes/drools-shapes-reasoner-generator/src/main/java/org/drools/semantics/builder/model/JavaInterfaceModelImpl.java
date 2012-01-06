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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JavaInterfaceModelImpl extends ModelImpl implements JavaInterfaceModel {



    JavaInterfaceModelImpl() {

    }


    private Map<String, String> traits = new HashMap<String, String>();

    public Map<String, String> getTraits() {
        return traits;
    }

    public void addTrait(String name, String trait) {
        traits.put( name, trait );
    }

    public void addTrait(String name, Object trait) {
        addTrait( name, (String) trait );
    }

    public Object getTrait( String name ) {
        return traits.get( name );
    }

    public Set<String> getTraitNames() {
        return traits.keySet();
    }



    protected String traitsToString() {
        StringBuilder sb = new StringBuilder();
        for ( String name : traits.keySet() ) {
            sb.append("\n\t").append( name ).append("\t --> \n").append( traits.get( name ) ).append("\n");
        }
        return sb.toString();
    }


    public boolean save( String targetDirectory ) {

        String slash = System.getProperty("file.separator");

        String path = targetDirectory + slash + getPackage().replace(".", "/");
        File dir = new File( path );
        if (! dir.exists()) {
            dir.mkdirs();
        }


        for ( String key : getTraitNames() ) {
            File f =  new File( path + slash + key + ".java" );
            System.out.println(f.getAbsolutePath());
            try {
                FileOutputStream fos = new FileOutputStream( f );
                fos.write( getTrait(key).toString().getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        return true;

    }


}
