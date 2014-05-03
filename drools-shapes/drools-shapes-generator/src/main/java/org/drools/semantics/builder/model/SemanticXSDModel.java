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


import org.jdom.Namespace;
import org.w3c.dom.Document;

import java.io.File;
import java.io.OutputStream;


public interface SemanticXSDModel extends XSDModel {

    public Document getBindings( String namespace );

    public void setBindings( String namespace, Document bindings );

    public boolean streamBindings( OutputStream os );

    public boolean streamBindings( File file );


    public void setIndex( String index );

    public boolean streamIndex( OutputStream fos);

    public String getIndex();


    public void setIndividualFactory( String factory );
    
    public boolean streamIndividualFactory( OutputStream os );

    public String getIndividualFactory();


    public void addNamespacedPackageInfo( Namespace ns, String fix );

    public boolean streamNamespacedPackageInfos( File folder );

    public String getNamespacedPackageInfo( Namespace ns );


    public void setEmpireConfig( String config );

    public boolean streamEmpireConfig( OutputStream fos );

    public String getEmpireConfig();


    public void setPersistenceXml( String config );

    public boolean streamPersistenceXml( OutputStream fos );

    public String getPersistenceXml();

}
