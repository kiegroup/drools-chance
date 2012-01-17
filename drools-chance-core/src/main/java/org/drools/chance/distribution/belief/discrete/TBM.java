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

import org.apache.commons.collections15.list.SetUniqueList;
import org.drools.chance.degree.Degree;
import org.drools.chance.distribution.DiscreteProbabilityDistribution;

import java.util.*;


/**
 * TBM
 * TODO
 * @param <T>
 */
public class TBM<T> implements DiscreteProbabilityDistribution<Set<T>> {


    SetUniqueList<T> singletons = SetUniqueList.decorate( new ArrayList<T>() );
    Map<BitSet, Degree> degreeMap = new HashMap<BitSet, Degree>();


    public Degree getMass(T value) {
        BitSet key = new BitSet();
        key.set(singletons.indexOf(value));
        return degreeMap.get( key );
    }


    public Degree getDegree( Set<T> value ) {
        return degreeMap.get( getMask( value ) );
    }

    public Degree get(Set<T> value) {
        return getDegree( value );
    }

    public Number domainSize(){
        return Math.pow(2, singletons.size());
    }


    public Set<Set<T>> getSupport() {
        Set<Set<T>> supp = new HashSet<Set<T>>();
        for ( BitSet key : degreeMap.keySet() ) {
            if ( degreeMap.get( key ).toBoolean() ) {
                supp.add(getSet( key ) );
            }
        }
        return supp;
    }

    public int size() {
        return degreeMap.size();
    }


    TBM() { }


    public Map<BitSet, Degree> getMaskDistribution() {
        return degreeMap;
    }

    public Iterator<BitSet> maskIterator() {
        return degreeMap.keySet().iterator();
    }

    public Map<Set<T>, Degree> getDistribution() {
        Map<Set<T>, Degree> map = new HashMap<Set<T>, Degree>();
        for ( BitSet key : degreeMap.keySet() ) {
            map.put( getSet( key ), degreeMap.get( key ) );
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

    public Set<T> getSet( BitSet key ) {
        Set<T> vals = new HashSet<T>();
        for ( int j = 0; j < key.length(); j++ ) {
            if ( key.get( j ) ) {
                vals.add(singletons.get(j));
            }
        }
        return vals;
    }

    void setDegree( BitSet key, Degree val ) {
        degreeMap.put( key, val );
    }

    void setDegree( Set<T> key, Degree val ) {
        degreeMap.put( getMask( key ), val );
    }

    public void addToDomain( T val ) {
        if ( ! singletons.contains( val ) ) {
            singletons.add( val );
        }
    }


    @Override
    public String toString() {
        return "TBM{" +
                "singletons=" + singletons +
                ", degreeMap=" + degreeMap +
                '}';
    }
}