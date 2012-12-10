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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public interface SemanticXSDModel extends XSDModel {

    public String getBindings( String namespace );

    public void setBindings( String namespace, String bindings );

    public boolean streamBindings( OutputStream os );

    public boolean streamBindings( File file );

    public void setIndex( String index );

    public boolean streamIndex( OutputStream fos);

    public void setIndividualFactory( String factory );
    
    public boolean streamIndividualFactory( OutputStream os );

    public void setNamespaceFix( String fix );

    public boolean streamNamespaceFix( OutputStream os );

    public void setEmpireConfig( String config );

    public boolean streamEmpireConfig( OutputStream fos );

    public void setPersistenceXml( String config );

    boolean streamPersistenceXml( OutputStream fos );
}
