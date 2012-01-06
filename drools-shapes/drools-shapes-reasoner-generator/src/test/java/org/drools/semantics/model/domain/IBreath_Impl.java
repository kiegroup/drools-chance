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


public class IBreath_Impl implements IBreath_Trait {


    public static final String BREATHE = "fffff";


    private static IBreath_Impl singleton;

    public static IBreath_Trait newInstance() {
        if (singleton == null) {
            singleton = new IBreath_Impl();
        }
        return singleton;
    }

    protected IBreath_Impl() { }




    public String breathe() {
        System.out.println(BREATHE);
        return BREATHE;
    }

    public String self_breathe(IBreath_Trait self) {
        return "I, " + self.toString() + " breathe " + breathe();
    }







}
