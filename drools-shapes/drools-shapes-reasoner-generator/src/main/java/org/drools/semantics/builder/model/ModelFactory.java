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

public class ModelFactory {


    public enum CompileTarget {
        BASE, JAVA, DRL, JAR, GRAPH, XSD, XSDX, WORKSET;
    }


    public static OntoModel newModel( String name, CompileTarget target) {
        OntoModel model = newModel( target, null );
        model.setName( name );
        return model;
    }

    public static OntoModel newModel( CompileTarget target, OntoModel base  ) {
        switch ( target ) {
            case JAVA   : CompiledOntoModel jmodel = new JavaInterfaceModelImpl();
                            jmodel.initFromBaseModel( base );
                            return jmodel;
            case DRL    : CompiledOntoModel dmodel = new DRLModelImpl();
                            dmodel.initFromBaseModel( base );
                            return dmodel;
            case JAR    : CompiledOntoModel rmodel = new JarModelImpl();
                            rmodel.initFromBaseModel( base );
                            return rmodel;
            case GRAPH  : CompiledOntoModel gmodel = new GraphModelImpl();
                            gmodel.initFromBaseModel( base );
                            return gmodel;
            case XSD    : CompiledOntoModel xmodel = new XSDModelImpl();
                            xmodel.initFromBaseModel( base );
                            return xmodel;
            case XSDX   : CompiledOntoModel smodel = new SemanticXSDModelImpl();
                            smodel.initFromBaseModel( base );
                            return smodel;
            case WORKSET: CompiledOntoModel wmodel = new WorkingSetModelImpl();
                            wmodel.initFromBaseModel( base );
                            return wmodel;
            case BASE:
            default  : return new GenericModelImpl();
        }
    }


}
