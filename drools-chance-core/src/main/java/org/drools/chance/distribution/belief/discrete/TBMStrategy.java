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

package org.drools.chance.distribution.belief.discrete;

import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.DistributionStrategies;

import java.lang.reflect.Constructor;
import java.util.*;


/**
 * Strategy and level III factory for TBM probability distributions
 * @param <T>
 */
public class TBMStrategy<T>  implements DistributionStrategies<T> {



    private DegreeType degreeType;
    private Class<T> domainType;

    private Constructor degreeStringConstr = null;



    TBMStrategy(DegreeType degreeType, Class<T> domainType){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }


    private Constructor getDegreeStringConstructor() {
        if (degreeStringConstr == null)
            degreeStringConstr = ChanceDegreeTypeRegistry.getSingleInstance().getConstructorByString( degreeType );
        return degreeStringConstr;
    }



    public Distribution<T> merge(Distribution<T> current,
                                 Distribution<T> newBit) {
        if ( current instanceof TBM && newBit instanceof TBM ) {
            //TODO
            return null;

        } else {
            throw new UnsupportedOperationException("TBM Strategies : unable to merge "
                    + current.getClass().getName() + " with " + newBit.getClass().getName());
        }
    }


    public Distribution<T> merge(Distribution<T> current,
                                 Distribution<T> newBit, String strategy) {
        return merge( current, newBit );
    }


    public Distribution<T> merge(Distribution<T> current,
                                 Distribution<T> newBit, Object... params) {
        return merge( current, newBit );
    }










    public Distribution<T> mergeAsNew(Distribution<T> current,
                                      Distribution<T> newBit) {
        if ( current instanceof TBM && newBit instanceof TBM ) {
            //TODO
            return null;

        } else {
            throw new UnsupportedOperationException("TBM Strategies : unable to merge "
                    + current.getClass().getName() + " with " + newBit.getClass().getName());
        }
    }


    public Distribution<T> mergeAsNew(Distribution<T> current,
                                      Distribution<T> newBit, String strategy) {
        return mergeAsNew(current, newBit);
    }


    public Distribution<T> mergeAsNew(Distribution<T> current,
                                      Distribution<T> newBit, Object... params) {
        return mergeAsNew(current, newBit);
    }

    public Distribution<T> remove(Distribution<T> current, Distribution<T> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> remove(Distribution<T> current, Distribution<T> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> remove(Distribution<T> current, Distribution<T> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

    public void normalize(Distribution<T> distr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public Distribution<T> newDistribution() {
        //TODO
        return null;
    }

    public Distribution<T> newDistribution(Set<T> focalElements) {
        //TODO
        return null;
    }

    public Distribution<T> newDistribution(Map<? extends T, ? extends Degree> elements) {
        //TODO
        return null;
    }







    public T toCrispValue(Distribution<T> dist) {
        //TODO
        return null;
    }


    public T toCrispValue(Distribution<T> dist, String strategy) {
        return toCrispValue(dist);
    }


    public T toCrispValue(Distribution<T> dist, Object... params) {
        return toCrispValue(dist);
    }





    public T sample(Distribution<T> dist) {
        //TODO
        return null;
    }

    public T sample(Distribution<T> dist, String strategy) {
        return sample(dist);
    }

    public T sample(Distribution<T> dist, Object... params) {
        return sample(dist);
    }








    public Distribution<T> toDistribution(T value) {
        return buildDistributionFromSingleObservation(value,1.0);
    }


    public Distribution<T> toDistribution(T value, String strategy) {
        //TODO
        return null;
    }

    public Distribution<T> toDistribution(T value, Object... params) {
        return toDistribution(value);
    }

    protected Distribution<T> buildDistributionFromSingleObservation(T value, double wgt) {
        //TODO
        return null;
    }







    public Distribution<T> parse( String distrAsString ) {
        TBM tbm = new TBM();
        Degree mass = ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 0.0 );
        Degree universe = ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 1.0 );

        int start = distrAsString.indexOf( "{" );
        int end = distrAsString.indexOf( "}" );

        while ( start >= 0 ) {
            Set<T> focalSet = new HashSet<T>();
            String setStr = distrAsString.substring( start + 1, end );

            StringTokenizer tok = new StringTokenizer( setStr, "," );
            while ( tok.hasMoreTokens() ) {
                String elemStr = tok.nextToken();
                try {
                    T elem = (T) domainType.getConstructor( String.class ).newInstance( elemStr.trim() );
                    focalSet.add( elem );
                    tbm.addToDomain( elem );
                } catch ( Exception e ) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            int sep = distrAsString.indexOf( "/", start );
            int next = distrAsString.indexOf( ",", sep );

            String degStr = next > 0 ? distrAsString.substring( sep + 1, next ) : distrAsString.substring( sep + 1 );

            try {
                Degree deg = (Degree) getDegreeStringConstructor().newInstance( degStr.trim() );

                tbm.setDegree( focalSet, deg );
                mass = mass.sum( deg );
            } catch ( Exception e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            start = distrAsString.indexOf( "{", end );
            end = distrAsString.indexOf( "}", start );

        }

        // Assign missing mass to universe
        tbm.setDegree( tbm.universeMask(), universe.sub( mass ) );

        return tbm;
    }




}
