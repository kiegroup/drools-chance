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

public class AbstractJavaModelImpl extends ModelImpl {


    protected static String slash = System.getProperty("file.separator");

    AbstractJavaModelImpl() {

    }


    private Map<String, InterfaceHolder> traits = new HashMap<String, InterfaceHolder>();

    public Map<String, InterfaceHolder> getTraits() {
        return traits;
    }

    public void addTrait( String name, InterfaceHolder trait ) {
        traits.put( name, trait );
    }

    public void addTrait( String name, Object trait ) {
        addTrait( name, (InterfaceHolder) trait );
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


        for ( String key : getTraitNames() ) {
            InterfaceHolder holder = (InterfaceHolder) getTrait( key );

            String path = getPackageDir( targetDirectory, holder.getPack() );

            File f =  new File( path + slash + getFileName( key ) + ".java" );
            if ( ! f.getParentFile().exists() ) {
                f.getParentFile().mkdirs();
            }
            try {
                FileOutputStream fos = new FileOutputStream( f );
                fos.write( holder.getSource().getBytes() );
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

    protected String getFileName( String key ) {
        return key.substring( key.lastIndexOf('.') + 1 );
    }

    private String getPackageDir(String targetDirectory, String pack) {
        String path = targetDirectory + slash + pack.replace(".", "/");
        File dir = new File( path );
        if (! dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }


    public static class InterfaceHolder {
        private String source;
        private String pack;

        public InterfaceHolder(String source, String pack) {
            this.source = source;
            this.pack = pack;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getPack() {
            return pack;
        }

        public void setPack(String pack) {
            this.pack = pack;
        }
    }

}
