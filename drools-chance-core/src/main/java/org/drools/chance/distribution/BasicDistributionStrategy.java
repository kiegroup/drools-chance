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

package org.drools.chance.distribution;

import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.simple.SimpleDegree;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * Strategy and level III factory for discrete probability distributions
 * @param <T>
 */
public class BasicDistributionStrategy<T>  implements IDistributionStrategies<T> {



    private String degreeType;
    private Class<T> domainType;

    private Constructor degreeStringConstr = null;


    public BasicDistributionStrategy(String degreeType, Class<T> domainType){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }


    private Constructor getDegreeStringConstructor() {
        if (degreeStringConstr == null) {
            degreeStringConstr = DegreeTypeRegistry.getSingleInstance().getConstructorByString(degreeType);
        }
        return degreeStringConstr;
    }





    public IDistribution<T> toDistribution(T value) {
        try {
            return new BasicDistribution<T>( value, DegreeTypeRegistry.getSingleInstance().getDegreeClass( degreeType ).newInstance().True() );
        } catch( Exception e ) {
            return new BasicDistribution<T>( value, new SimpleDegree(1.0) );
        }
    }

    public IDistribution<T> toDistribution(T value, String strategy) {
        try {
            return new BasicDistribution<T>( value, DegreeTypeRegistry.getSingleInstance().getDegreeClass( degreeType ).newInstance().True() );
        } catch( Exception e ) {
            return new BasicDistribution<T>( value, new SimpleDegree(1.0) );
        }
    }

    public IDistribution<T> toDistribution(T value, Object... params) {
        try {
            return new BasicDistribution<T>( value, DegreeTypeRegistry.getSingleInstance().getDegreeClass( degreeType ).newInstance().True() );
        } catch( Exception e ) {
            return new BasicDistribution<T>( value, new SimpleDegree(1.0) );
        }
    }



    public IDistribution<T> parse(String distrAsString) {

        int idx = distrAsString.indexOf('/');
        T val;
        IDegree deg;
        try {

            if ( idx < 0 ) {
                val = domainType.getConstructor(String.class).newInstance( distrAsString.trim() );
                deg = DegreeTypeRegistry.getSingleInstance().getDegreeClass( degreeType ).newInstance().True();
            } else {
                val = domainType.getConstructor(String.class).newInstance( distrAsString.substring( 0, idx ) );
                deg = (IDegree) DegreeTypeRegistry.getSingleInstance().getConstructorByString( degreeType ).newInstance( distrAsString.substring( idx + 1 ) );
            }

            return new BasicDistribution<T>( val, deg );

        } catch (NoSuchMethodException nsme) {
            nsme.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
        }

        return null;
    }



    public IDistribution<T> newDistribution() {
        try {
            return new BasicDistribution<T>( null, DegreeTypeRegistry.getSingleInstance().getDegreeClass( degreeType ).newInstance().False() );
        } catch( Exception e ) {
            return new BasicDistribution<T>( null, new SimpleDegree(0.0) );
        }
    }

    public IDistribution<T> newDistribution(Set<T> focalElements) {
        T value = focalElements.iterator().next();
        try {
            return new BasicDistribution<T>( value, DegreeTypeRegistry.getSingleInstance().getDegreeClass( degreeType ).newInstance().True() );
        } catch( Exception e ) {
            return new BasicDistribution<T>( value, new SimpleDegree(1.0) );
        }
    }

    public IDistribution<T> newDistribution(Map<? extends T, ? extends IDegree> elements) {
        T value = elements.keySet().iterator().next();
        IDegree deg = elements.get( value );
        try {
            return new BasicDistribution<T>( value, deg );
        } catch( Exception e ) {
            return new BasicDistribution<T>( value, deg );
        }
    }



    public T toCrispValue(IDistribution<T> dist) {
        return ((BasicDistribution<T>) dist).getValue();
    }

    public T toCrispValue(IDistribution<T> dist, String strategy) {
        return ((BasicDistribution<T>) dist).getValue();
    }

    public T toCrispValue(IDistribution<T> dist, Object... params) {
        return ((BasicDistribution<T>) dist).getValue();
    }



    public T sample(IDistribution<T> dist) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public T sample(IDistribution<T> dist, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public T sample(IDistribution<T> dist, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    public IDistribution<T> merge(IDistribution<T> current, IDistribution<T> newBit) {
        BasicDistribution<T> src = (BasicDistribution<T>) current;
        BasicDistribution<T> bit = (BasicDistribution<T>) newBit;

        T val = src.getValue();
        if ( val == null || ! val.equals( bit.getValue() ) ) {
            src.clear();
            return src;
        }

        IDegree a = src.getDegree(val);
        IDegree b = bit.getDegree( val );
        src.setDegree( a.sum( b.mul( a.True().sub( a ) ) ) );

        return src;
    }

    public IDistribution<T> merge(IDistribution<T> current, IDistribution<T> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<T> merge(IDistribution<T> current, IDistribution<T> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    public IDistribution<T> mergeAsNew(IDistribution<T> current, IDistribution<T> newBit) {
        BasicDistribution<T> src = (BasicDistribution<T>) current;
        BasicDistribution<T> bit = (BasicDistribution<T>) newBit;

        T val = src.getValue();
        if ( val == null || ! val.equals( bit.getValue() ) ) {
            src.clear();
            return src;
        }

        IDegree a = src.getDegree( val );
        IDegree b = bit.getDegree( val );
        IDegree deg = ( a.sum( b ) ).sub( a.mul( b ) );

        BasicDistribution<T> dist = new BasicDistribution<T>( val, deg );

        return dist;
    }

    public IDistribution<T> mergeAsNew(IDistribution<T> current, IDistribution<T> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<T> mergeAsNew(IDistribution<T> current, IDistribution<T> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
