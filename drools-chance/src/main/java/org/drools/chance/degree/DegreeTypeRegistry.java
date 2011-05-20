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
