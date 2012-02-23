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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SemanticXSDModelImpl extends XSDModelImpl implements SemanticXSDModel {


    private String index;

    private String bindings;
    
    private String individualFactory;

    public String getBindings() {
        return this.bindings;
    }

    public boolean streamIndividualFactory( OutputStream os ) {
        try {
            os.write( individualFactory.getBytes() );
        } catch (IOException e) {
            return false;
        }
        return true;
    }



    public void setBindings(String bindings) {
        this.bindings = bindings;
    }

    public boolean streamBindings( OutputStream os ) {
        try {
            os.write( bindings.getBytes() );
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public boolean streamIndex( OutputStream os ) {
        try {
            os.write( index.getBytes() );
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String getIndividualFactory() {
        return individualFactory;
    }

    public void setIndividualFactory(String individualFactory) {
        this.individualFactory = individualFactory;
    }
}
