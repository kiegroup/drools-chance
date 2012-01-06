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

package org.drools.semantics.model.domain;

public class IWorker_Impl implements IWorker_Trait {

    
    private static IWorker_Impl singleton = null;
    
    protected IWorker_Impl() { }
    
    public static IWorker_Trait newInstance() {
        if (singleton == null) {
            singleton = new IWorker_Impl();
        }
        return singleton;
    }



    public String toil() {
        System.out.println("I toil");
        return "toil";
    }

    public int sum(int x, int y) {
        return x+y+1;
    }


}
