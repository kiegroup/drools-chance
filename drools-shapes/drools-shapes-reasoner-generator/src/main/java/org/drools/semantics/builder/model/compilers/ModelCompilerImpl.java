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

import org.drools.semantics.builder.DLUtils;
import org.drools.semantics.builder.model.CompiledOntoModel;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;

import java.util.HashMap;
import java.util.Map;

public abstract class ModelCompilerImpl implements ModelCompiler {

    protected CompiledOntoModel model;


    public CompiledOntoModel getModel() {
        return model;
    }

    protected abstract void setModel( OntoModel model );

    public abstract void compile( Concept con, Object target, Map<String,Object> params);

    public CompiledOntoModel compile( OntoModel model ) {

        setModel( model );

        if ( getModel() != null ) {
            for ( Concept con : getModel().getConcepts() ) {
                String name = DLUtils.compactUpperCase( con.getName() );
                Map map = new HashMap();
                map.put( "package", getModel().getPackage() );
                map.put( "iri", con.getIri() );
                map.put( "name", con.getName().substring(con.getName().lastIndexOf(".") + 1) );
                map.put( "superConcepts", con.getSuperConcepts() );
                map.put( "subConcepts", con.getSubConcepts() );
                map.put( "properties", con.getProperties() );
                map.put( "implInterface", con.isResolved() && con.getResolvedAs().equals( Concept.Resolution.IFACE ) ? con.getFullyQualifiedName() : null );
                map.put( "implClass", con.isResolved() && con.getResolvedAs().equals( Concept.Resolution.CLASS )? con.getFullyQualifiedName() : null );

                map.put( "shadowProperties", con.getShadowProperties() );
                if ( con.isAbstrakt() ) {
                    map.put( "abstract", con.isAbstrakt() );
                }
                map.put( "keys", con.getKeys() );
                compile( con, DLUtils.getInstance(), map );
            }
        }
        return getModel();
    }


}
