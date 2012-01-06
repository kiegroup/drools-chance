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

package org.drools.semantics.builder.model.compilers;

import org.drools.semantics.builder.model.*;
import org.drools.semantics.util.SemanticWorkingSetConfigData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorkingSetModelCompilerImpl extends ModelCompilerImpl implements WorkingSetModelCompiler {


    private Map<String, SemanticWorkingSetConfigData> map = new HashMap<String,SemanticWorkingSetConfigData>();

    SemanticWorkingSetConfigData root;


    protected void setModel(OntoModel model) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.WORKSET, model );
        root = ((WorkingSetModel) this.model).getWorkingSet();
        map.put( root.getName(), root );
    }


    public void compile(String name, Object target, Map<String, Object> params) {
        if ( "Thing".equals(name) ) {
            return;
        }

        if ( ! map.containsKey( name ) ) {
            addToValidFacts(root, name);
        }

        SemanticWorkingSetConfigData children = new SemanticWorkingSetConfigData();
        children.setName( name );
        children.setDescription(params.get("package") + name);
        map.put( name, children );
        model.addTrait( name, children );

        Set<Concept> supers = (Set<Concept>) params.get("superConcepts");
        for ( Concept sup : supers  ) {
            SemanticWorkingSetConfigData father = map.get( sup.getName() );
            addToWorkingSets(father, children);

            if ( ! "Thing".equals( father.name ) ) {
                addToValidFacts(father, name);
            }

        }

    }

    private void addToValidFacts( SemanticWorkingSetConfigData father, String name ) {
        String[] ws = father.getValidFacts();
        if ( ws == null ) {
            ws = new String[0];
        }
        String[] newFacts = new String[ws.length+1];
        System.arraycopy(ws, 0, newFacts, 0, ws.length);
        newFacts[ ws.length ] = name;
        father.setValidFacts( newFacts );

    }

    private void addToWorkingSets( SemanticWorkingSetConfigData father, SemanticWorkingSetConfigData children) {
        SemanticWorkingSetConfigData[] ws = father.getWorkingSets();
        if ( ws == null ) {
            ws = new SemanticWorkingSetConfigData[0];
        }
        SemanticWorkingSetConfigData[] newWs = new SemanticWorkingSetConfigData[ws.length+1];
        System.arraycopy(ws, 0, newWs, 0, ws.length);
        newWs[ ws.length ] = children;
        father.setWorkingSets( newWs );
    }

    public void setMode(Mode mode) {
        //TODO
    }
}
