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

package org.drools.semantics.builder;


import org.drools.semantics.builder.model.OntoModel;
import org.kie.api.io.Resource;
import org.semanticweb.owlapi.model.OWLOntology;

public interface DLFactory {



    public OWLOntology parseOntology( Resource resource );


    public OntoModel buildModel( String name,
                                 Resource res,
                                 DLFactoryConfiguration conf );

    public OntoModel buildModel( String name,
                                 Resource res,
                                 DLFactoryConfiguration conf,
                                 ClassLoader classLoader );


    public OntoModel buildModel( String name,
                                 Resource[] res,
                                 DLFactoryConfiguration conf );

    public OntoModel buildModel( String name,
                                 Resource[] res,
                                 DLFactoryConfiguration conf,
                                 ClassLoader lodaer );


}
