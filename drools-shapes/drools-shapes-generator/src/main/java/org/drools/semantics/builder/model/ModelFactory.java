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

import java.util.Collections;
import java.util.Map;

public class ModelFactory {


    public enum CompileTarget {
        BASE, JAVA, DRL, RL, JAR, GRAPH, XSD, XSDX, WORKSET;
    }


    public static OntoModel newModel( String name, OntoModel.Mode mode ) {
        return newModel( name, Collections.EMPTY_MAP, mode );
    }

    public static OntoModel newModel( String name, Map<String,String> packageNameMappings, OntoModel.Mode mode ) {
        OntoModel model = newModel( CompileTarget.BASE, packageNameMappings, null );
        model.setName( name );
        ( (GenericModelImpl) model ).setMode( mode );
        return model;
    }

    public static OntoModel newModel( String name, CompileTarget target ) {
        OntoModel model = newModel( target, null, null );
        model.setName( name );
        return model;
    }

    public static OntoModel newModel( CompileTarget target, OntoModel base  ) {
        return newModel( target, null, base );
    }

    public static OntoModel newModel( CompileTarget target, Map<String,String> nameMappings, OntoModel base  ) {
        switch ( target ) {
            case JAVA   : CompiledOntoModel jmodel = new JavaInterfaceModelImpl();
                            jmodel.initFromBaseModel( base );
                            return jmodel;
            case DRL    : CompiledOntoModel dmodel = new DRLModelImpl();
                            dmodel.initFromBaseModel( base );
                            return dmodel;
            case JAR    : CompiledOntoModel zmodel = new JarModelImpl();
                            zmodel.initFromBaseModel( base );
                            return zmodel;
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
            case RL     : CompiledOntoModel rmodel = new RecognitionRuleModelImpl();
                            rmodel.initFromBaseModel( base );
                            return rmodel;
            case BASE:
            default  : GenericModelImpl model = new GenericModelImpl();
                model.setPackageNameMappings( nameMappings );
                return model;
        }
    }


}
