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

import java.util.Map;

public class GraphModelCompilerImpl extends ModelCompilerImpl implements GraphModelCompiler {



    protected void setModel(OntoModel model) {
        this.model = (CompiledOntoModel) ModelFactory.newModel(ModelFactory.CompileTarget.GRAPH, model);
    }


    public void setMode(Mode mode) {
        //TODO
    }

    public CompiledOntoModel compile( OntoModel model ) {
        setModel( model );
        GraphModel gModel = (GraphModel) getModel();
        if ( gModel != null ) {
            for ( Concept con : gModel.getConcepts() ) {
                gModel.addTrait(con.getName(), con);
            }

            for ( Concept con : gModel.getConcepts() ) {
                for ( Object sup : con.getSuperConcepts() ) {
                    gModel.addRelationEdge( new SubConceptOf( con.getIri(), ((Concept) sup).getIri() ));
                }
            }

            for ( Concept con : gModel.getConcepts() ) {
                for ( String relIri : con.getProperties().keySet() ) {
                    gModel.addRelationEdge( con.getProperties().get( relIri ) );
                }
            }

        }
        return gModel;
    }

    @Override
    public void compile(String name, Object target, Map<String, Object> params) {
//        System.out.println( "Compile into graph ");
    }


}
