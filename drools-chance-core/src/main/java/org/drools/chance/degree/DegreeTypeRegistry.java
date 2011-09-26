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

package org.drools.chance.degree;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: doncat
 * Date: 27/01/11
 * Time: 14.44
 * To change this template use File | Settings | File Templates.
 */
public  class DegreeTypeRegistry {

    private Hashtable<String,Constructor> strConstructorTable = new Hashtable();

    private static DegreeTypeRegistry instance=null;

    private DegreeTypeRegistry (){

    }


    public static DegreeTypeRegistry getSingleInstance(){
        if (instance==null)
         instance=new DegreeTypeRegistry();

            return  instance;




    }

    public boolean registerDegreeType(String name,Class degreeType){

        try {
            strConstructorTable.put(name, degreeType.getConstructor(String.class));
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }

    }


    public Constructor getConstructorByString(String name) {
        return strConstructorTable.get(name);
    }

}
