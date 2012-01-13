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
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

public  class DegreeTypeRegistry {

    private Hashtable<DegreeType,Class<? extends Degree>> degreeTable = new Hashtable(7);

    private static DegreeTypeRegistry instance = null;


    public static DegreeTypeRegistry getSingleInstance(){
        if ( instance == null ) {
            instance=new DegreeTypeRegistry();
        }
        return instance;
    }

    public boolean registerDegreeType( DegreeType name,Class degreeType){

        degreeTable.put( name, degreeType);
        return true;

    }


    public Constructor getConstructorByString( DegreeType name ) {
        try {
            return degreeTable.get( name ).getConstructor( String.class );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(-2);
            return null;
        }
    }

    public Class<? extends Degree> getDegreeClass( DegreeType name ) {
        return degreeTable.get( name );
    }


    public Degree buildDegree( DegreeType name, double val ) {
        try {
            return degreeTable.get( name ).getConstructor( double.class ).newInstance( val );
        } catch (NoSuchMethodException e) {

        } catch (InvocationTargetException e) {

        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        }
        return null;
    }

    public Degree buildDegree( DegreeType name, String val ) {
        try {
            return degreeTable.get( name ).getConstructor( String.class ).newInstance( val );
        } catch (NoSuchMethodException e) {

        } catch (InvocationTargetException e) {

        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        }
        return null;
    }



}
