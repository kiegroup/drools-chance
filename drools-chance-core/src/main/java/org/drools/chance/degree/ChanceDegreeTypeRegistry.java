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

import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.lpad.LpadDegree;
import org.drools.chance.degree.simple.SimpleDegree;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

public  class ChanceDegreeTypeRegistry {

    private Hashtable<DegreeType,Class<? extends Degree>> degreeTable = new Hashtable(7);

    private static ChanceDegreeTypeRegistry instance = null;
    
    protected static DegreeType defaultDegree = DegreeType.SIMPLE;
    protected static Degree defaultOne = new SimpleDegree( 1.0 );
    protected static Class<? extends Degree> defaultDegreeClass = SimpleDegree.class;


    public static ChanceDegreeTypeRegistry getSingleInstance(){
        if ( instance == null ) {
            
            instance = new ChanceDegreeTypeRegistry();
            
            instance.registerDegreeType( DegreeType.SIMPLE, SimpleDegree.class );
            instance.registerDegreeType( DegreeType.INTERVAL, IntervalDegree.class );
            instance.registerDegreeType( DegreeType.LPAD, LpadDegree.class );
            
        }
        
        return instance;
    }

    public Degree getDefault() {
        return defaultOne;
    }


    public static DegreeType getDefaultDegree() {
        return defaultDegree;
    }

    public static Degree getDefaultOne() {
        return defaultOne;
    }

    public static Class<? extends Degree> getDefaultDegreeClass() {
        return defaultDegreeClass;
    }

    public static void setDefaultDegree( DegreeType defaultDegree ) {
        ChanceDegreeTypeRegistry.defaultDegree = defaultDegree;
        ChanceDegreeTypeRegistry.defaultOne = getSingleInstance().buildDegree( defaultDegree, 1.0 );
        ChanceDegreeTypeRegistry.defaultDegreeClass = defaultOne.getClass();
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
            if ( name == null ) {
                Degree deg = defaultDegreeClass.newInstance();
                deg.setValue( val );
                return deg;
            }


            Class<? extends Degree> degClass = degreeTable.get( name );
            if ( degClass == null ) {
                Degree deg = defaultDegreeClass.newInstance();
                deg.setValue( val );
                return deg;
            }
            return degClass.getConstructor( double.class ).newInstance( val );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Degree buildDegree( DegreeType name, String val ) {
        try {
            return degreeTable.get( name ).getConstructor( String.class ).newInstance( val );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }





}
