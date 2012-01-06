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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DRLModelImpl extends ModelImpl implements DRLModel {



    DRLModelImpl() {

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

    public String getDRL() {
        StringBuilder sb = new StringBuilder();
        for ( String key : getTraitNames() ) {
            sb.append( getTrait(key) ).append( "\n" );
        }
        return sb.toString();
    }


}
