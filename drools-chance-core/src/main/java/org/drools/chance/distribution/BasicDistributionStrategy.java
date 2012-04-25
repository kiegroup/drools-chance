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

import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveFactory;
import org.drools.chance.rule.constraint.core.connectives.impl.LogicConnectives;
import org.drools.chance.rule.constraint.core.connectives.impl.MvlFamilies;
import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.degree.simple.SimpleDegree;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;


/**
 * Strategy and level III factory for discrete probability distributions
 * @param <T>
 */
public class BasicDistributionStrategy<T>  implements DistributionStrategies<T> {


    private ConnectiveFactory connFactory;
    private DegreeType degreeType;
    private Class<T> domainType;

    private Constructor degreeStringConstr = null;

    private Degree tru;
    private Degree fal;
    private Degree unk;


    public BasicDistributionStrategy( DegreeType degreeType, Class<T> domainType, ConnectiveFactory connFactory ) {
        this.connFactory = connFactory;
        this.degreeType = degreeType;
        this.domainType = domainType;
        try {
            this.tru = ChanceDegreeTypeRegistry.getSingleInstance().getDegreeClass( degreeType ).newInstance().True();
        } catch( Exception e ) {
            this.tru = SimpleDegree.TRUE;
        }
        try {
            this.fal = ChanceDegreeTypeRegistry.getSingleInstance().getDegreeClass( degreeType ).newInstance().False();
        } catch( Exception e ) {
            this.fal = SimpleDegree.FALSE;
        }
        try {
            this.unk = ChanceDegreeTypeRegistry.getSingleInstance().getDegreeClass( degreeType ).newInstance().Unknown();
        } catch( Exception e ) {
            this.unk = new SimpleDegree(0.5);
        }
    }


    private Constructor getDegreeStringConstructor() {
        if (degreeStringConstr == null) {
            degreeStringConstr = ChanceDegreeTypeRegistry.getSingleInstance().getConstructorByString( degreeType );
        }
        return degreeStringConstr;
    }





    public Distribution<T> toDistribution(T value) {
        return new BasicDistribution<T>( value, tru );
    }

    public Distribution<T> toDistribution(T value, String strategy) {
        return new BasicDistribution<T>( value, tru );
    }

    public Distribution<T> toDistribution(T value, Object... params) {
        return new BasicDistribution<T>( value, (Degree) params[0] );
    }



    public Distribution<T> parse(String distrAsString) {

        int idx = distrAsString.indexOf('/');
        T val;
        Degree deg;
        try {

            if ( idx < 0 ) {
                val = domainType.getConstructor(String.class).newInstance( distrAsString.trim() );
                deg = tru;
            } else {
                val = domainType.getConstructor(String.class).newInstance( distrAsString.substring( 0, idx ) );
                deg = (Degree) ChanceDegreeTypeRegistry.getSingleInstance().getConstructorByString( degreeType ).newInstance( distrAsString.substring( idx + 1 ) );
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



    public Distribution<T> newDistribution() {
        if ( Boolean.class.equals( domainType ) ) {
            return (Distribution<T>) new BasicDistribution<Boolean>( true, unk );
        } else {
            return new BasicDistribution<T>( null, fal );
        }
    }

    public Distribution<T> newDistribution(Set<T> focalElements) {
        T value = focalElements.iterator().next();
        return new BasicDistribution<T>( value, tru );
    }

    public Distribution<T> newDistribution(Map<? extends T, ? extends Degree> elements) {
        T value = elements.keySet().iterator().next();
        Degree deg = elements.get( value );
        try {
            return new BasicDistribution<T>( value, deg );
        } catch( Exception e ) {
            return new BasicDistribution<T>( value, deg );
        }
    }



    public T toCrispValue(Distribution<T> dist) {
        return ((BasicDistribution<T>) dist).getValue();
    }

    public T toCrispValue(Distribution<T> dist, String strategy) {
        return ((BasicDistribution<T>) dist).getValue();
    }

    public T toCrispValue(Distribution<T> dist, Object... params) {
        return ((BasicDistribution<T>) dist).getValue();
    }



    public T sample(Distribution<T> dist) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public T sample(Distribution<T> dist, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public T sample(Distribution<T> dist, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    public Distribution<T> merge(Distribution<T> current, Distribution<T> newBit) {
        return merge( current, newBit, "operator=OR" );
    }

    public Distribution<T> merge(Distribution<T> current, Distribution<T> newBit, String strategy) {
        return merge( current, newBit, new Object[] {strategy} );
    }

    public Distribution<T> merge(Distribution<T> current, Distribution<T> newBit, Object... params) {
        BasicDistribution<T> src = (BasicDistribution<T>) current;
        BasicDistribution<T> bit = (BasicDistribution<T>) newBit;

        T val = src.getValue();
        if ( val == null ) {
            val = ((BasicDistribution<T>) newBit).getValue();
            src.set( val, newBit.getDegree( val ) );
        } else if ( val.equals( bit.getValue() ) ) {
            ConnectiveCore op = null;
            if ( params.length > 0 ) {
                op = parseParams( params );
            } else {
                op = connFactory.getOr();
            }

            Degree c = op.eval( src.getDegree(val), bit.getDegree( val ) );
            src.setDegree( c.getValue() > 0.99 ? c.True() : c );

        } else {

            ConnectiveCore op =  connFactory.getConnective( LogicConnectives.AND, MvlFamilies.PRODUCT.value() );

            Degree a = src.getDegree( val );
            Degree b = bit.getDegree( val );
            Degree c = op.eval( a, b );

            if ( c.equals( c.False() ) ) {
                src.set( ((BasicDistribution<T>) newBit).getValue(), c.True() );
            } else {
                src.setDegree( c );
            }

        }
        return src;
    }

    private ConnectiveCore parseParams(Object[] params) {
        LogicConnectives conn = LogicConnectives.OR;
        String family = MvlFamilies.PRODUCT.value();

        for ( int j = 0; j < params.length; j++ ) {
            String param = (String) params[j];
            int idx = param.indexOf( '=' );
            if ( idx > 0 ) {
                String key = param.substring( 0, idx ).trim();
                if ( "family".equals( key ) ) {
                    family = param.substring( idx + 1 );
                } else if ( "operator".equals( key ) ) {
                    conn = LogicConnectives.valueOf(param.substring(idx + 1));
                }
            }
        }

        return connFactory.getConnective( conn, family );

    }


    public Distribution<T> mergeAsNew(Distribution<T> current, Distribution<T> newBit) {
        BasicDistribution<T> src = (BasicDistribution<T>) current;
        BasicDistribution<T> bit = (BasicDistribution<T>) newBit;

        T val = src.getValue();
        if ( val == null || ! val.equals( bit.getValue() ) ) {
            src.clear();
            return src;
        }

        Degree a = src.getDegree( val );
        Degree b = bit.getDegree( val );
        Degree deg = ( a.sum( b ) ).sub( a.mul( b ) );

        BasicDistribution<T> dist = new BasicDistribution<T>( val, deg );

        return dist;
    }

    public Distribution<T> mergeAsNew(Distribution<T> current, Distribution<T> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> mergeAsNew(Distribution<T> current, Distribution<T> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> remove(Distribution<T> current, Distribution<T> newBit) {
        return  remove( current, newBit, new Object[0] );

    }

//    w -> w/a
//
//    1-a -> (1-a)/(1-b) ---> (1-a) -> 1 - (1-a)/(1-b) ----->  (1-b -1 +a)/(1-b) --> (a-b)/(1-b)

    public Distribution<T> remove(Distribution<T> current, Distribution<T> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> remove(Distribution<T> current, Distribution<T> newBit, Object... params) {
        BasicDistribution<T> src = (BasicDistribution<T>) current;
        BasicDistribution<T> bit = (BasicDistribution<T>) newBit;

        T val = src.getValue();
        if ( val == null ) {

        } else if ( val.equals( bit.getValue() ) ) {

            Degree a = src.getDegree(val);
            Degree b = bit.getDegree( val );
            Degree c;
            if ( a.equals( a.True() ) && b.getValue() > 0 ) {
                a = a.fromConst( 0.99 );
            }
            c = ( a.sub( b ) ).div( a.True().sub( b ) );

            src.setDegree( c );
        } else {
            //do nothing
        }
        return src;
    }

    public Distribution<T> removeAsNew(Distribution<T> current, Distribution<T> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> removeAsNew(Distribution<T> current, Distribution<T> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> removeAsNew(Distribution<T> current, Distribution<T> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
