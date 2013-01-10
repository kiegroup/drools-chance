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

import org.drools.chance.degree.Degree;
import org.drools.chance.distribution.DiscreteProbabilityDistribution;
import org.drools.chance.distribution.Distribution;

import java.util.*;


/**
 * TBM
 * TODO
 * @param <T>
 */
public class TBM<T> implements DiscreteProbabilityDistribution<Set<T>> {


    TBM() { }


    List<T> singletons = new ArrayList<T>();
    Map<BitSet, Degree> massDegreeMap = new HashMap<BitSet, Degree>();
    private boolean normalized;





    public Degree get( Set<T> values ) {
        return getDegree( values );
    }

    public Degree get( T... values ) {
        return getDegree( values );
    }

    public Degree getDegree( Set<T> values ) {
        return massDegreeMap.get( getMask( values ) );
    }

    public Degree getDegree( T... vals ) {
        return massDegreeMap.get(getMask(vals));
    }

    public void setDegree( Set<T> key, Degree val ) {
        massDegreeMap.put( getMask( key ), val );
    }

    public void setDegree( Degree val, T... key ) {
        massDegreeMap.put( getMask( key ), val );
    }

    protected void setDegree( BitSet key, Degree val ) {
        massDegreeMap.put( key, val );
    }



    public Distribution<Set<T>> getBasicMassAssignment() {
        return this;
    }

    public Distribution<T> toBayesianMassAssignment() {
        //TODO
        return null;
    }




    public Degree getMass( T... value ) {
        return getMass( getMask( value ) );
    }

    public Degree getMass( Set<T> value ) {
        return getMass( getMask( value ) );
    }

    public Degree getMass( BitSet value ) {
        return massDegreeMap.get( value );
    }


    public Degree getBelief( T... value ) {
        return getBelief( getMask( value ) );
    }

    public Degree getBelief( Set<T> value ) {
        return getBelief( getMask( value ) );
    }

    public Degree getBelief( BitSet ref ) {
        Degree deg = massDegreeMap.values().iterator().next().False();
        for ( BitSet set : this.massDegreeMap.keySet() ) {
            BitSet test = ref.get(0, size());

            test.and(set);

            if ( test.cardinality() == set.cardinality() ) {
                deg = deg.sum( massDegreeMap.get( set ) );
            } else {
            }
        }
        return deg;
    }

    public Degree getPlausibility( T... value ) {
        return getPlausibility( getMask( value ) );
    }

    public Degree getPlausibility( Set<T> value ) {
        return getPlausibility( getMask( value ) );
    }

    public Degree getPlausibility( BitSet ref ) {
        Degree deg = massDegreeMap.values().iterator().next().False();
        for ( BitSet set : this.massDegreeMap.keySet() ) {
            BitSet test = ref.get(0, size());

            if ( test.intersects( set ) ) {
                deg = deg.sum( massDegreeMap.get( set ) );
            } else {
            }
        }
        return deg;
    }


    public Degree getCommonality( T... value ) {
        return getCommonality( getMask( value ) );
    }

    public Degree getCommonality( Set<T> value ) {
        return getCommonality( getMask( value ) );
    }

    public Degree getCommonality( BitSet ref ) {
        Degree deg = massDegreeMap.values().iterator().next().False();
        //TODO
//        for ( BitSet set : this.massDegreeMap.keySet() ) {
//            BitSet test = ref.get(0, size());
//
//            if ( test.intersects( set ) ) {
//                deg = deg.sum( massDegreeMap.get( set ) );
//            } else {
//            }
//        }
        return deg;
    }


    public boolean isNormalized() {
        return normalized;
    }

    public void setNormalized(boolean normalized) {
        this.normalized = normalized;
    }

    public Number domainSize(){
        return Math.pow(2, singletons.size());
    }

    public Number universeSize(){
        return singletons.size();
    }


    public Set<Set<T>> getSupport() {
        Set<Set<T>> supp = new HashSet<Set<T>>();
        for ( BitSet key : massDegreeMap.keySet() ) {
            if ( massDegreeMap.get( key ).toBoolean() ) {
                supp.add(getSet( key ) );
            }
        }
        return supp;
    }

    public int size() {
        return massDegreeMap.size();
    }

    public Set<T> universe() {
        return new HashSet<T>( singletons );
    }

    public BitSet universeMask() {
        BitSet set = new BitSet( );
        set.set( 0, size() );
        return set;
    }

    public Map<BitSet, Degree> getMaskDistribution() {
        return massDegreeMap;
    }

    public Iterator<BitSet> maskIterator() {
        return massDegreeMap.keySet().iterator();
    }

    public Map<Set<T>, Degree> getDistribution() {
        Map<Set<T>, Degree> map = new HashMap<Set<T>, Degree>();
        for ( BitSet key : massDegreeMap.keySet() ) {
            map.put( getSet( key ), massDegreeMap.get( key ) );
        }
        return map;
    }

    public Iterator<Set<T>> iterator() {
        return getDistribution().keySet().iterator();
    }


    public BitSet getMask( Set<T> set ) {
        BitSet key = new BitSet();
        for ( T val : set ) {
            key.set(singletons.indexOf(val));
        }
        return key;
    }


    public BitSet getMask( T... vals ) {
        BitSet key = new BitSet();
        for ( T val : vals) {
            key.set(singletons.indexOf(val));
        }
        return key;
    }


    public Set<T> getSet( BitSet key ) {
        Set<T> vals = new HashSet<T>();
        for ( int j = 0; j < key.length(); j++ ) {
            if ( key.get( j ) ) {
                vals.add(singletons.get(j));
            }
        }
        return vals;
    }



    public void addToDomain( T val ) {
        if ( ! singletons.contains( val ) ) {
            singletons.add( val );
        }
    }


    public boolean isDiscrete() {
        return true;
    }














    @Override
    public String toString() {
        return "TBM{" +
                "singletons=" + singletons +
                ", massDegreeMap=" + massDegreeMap +
                '}';
    }


}