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

public class IStudent_Impl extends IBreath_Impl implements IStudent_Trait {

    private static IStudent_Impl singleton = null;
    
    protected IStudent_Impl() { }
    
    public static IStudent_Trait newInstance() {
        if (singleton == null) {
            singleton = new IStudent_Impl();
        }
        return singleton;
    }

    
    
    public String attendLesson(String lesson) {
        System.out.println("I'm attending lesson " + lesson);
        return lesson;
    }

    public int sum(int x, int y) {
        return x+y;
    }


    public String breathe() {

        System.out.println(BREATHE + "as a student");
        return BREATHE;

    }

}
