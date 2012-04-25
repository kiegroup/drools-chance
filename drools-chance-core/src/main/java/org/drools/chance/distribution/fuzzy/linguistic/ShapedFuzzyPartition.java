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

package org.drools.chance.distribution.fuzzy.linguistic;

import org.drools.chance.core.util.ValueSortedMap;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.ContinuousPossibilityDistribution;
import org.drools.chance.distribution.DiscretePossibilityDistribution;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ShapedFuzzyPartition<T extends Linguistic<Number>> implements DiscretePossibilityDistribution<Linguistic<Number>> {

    private ValueSortedMap<Linguistic<Number>,Degree> map;



    public ShapedFuzzyPartition(Linguistic[] values) {
        map = new ValueSortedMap<Linguistic<Number>, Degree>();
        for (Linguistic l : values) {
            map.put(l,SimpleDegree.FALSE);
        }
    }

    public ShapedFuzzyPartition(Map<? extends Linguistic,? extends Degree> elements) {
        this.map = new ValueSortedMap();
        for (Linguistic ling : elements.keySet()) {
            map.put(ling,elements.get(ling));
        }
    }


    public Map<Linguistic<Number>, Degree> getDistribution() {
        return map;
    }


    public void reshape(Linguistic key, Degree deg) {
        map.put(key, deg);
    }


    public void reshape(String key, Degree deg) {
        map.put(iterator().next().parse(key), deg);
    }



    public Degree getDegree(Linguistic key) {
        return map.get(key);
    }

    public Degree get(Linguistic value) {
        return getDegree( value );
    }


    public String toString() {
        return "(Fuzzy Ling) : {" + serialize() + "}";
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        Iterator<Linguistic<Number>> iter = getSupport().iterator();
        while (iter.hasNext()) {
            Linguistic elem = iter.next();
            sb.append(elem).append("/").append(getDegree(elem).getValue());
            if (iter.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }



    public Set<Linguistic<Number>> getSupport() {
        return map.keySet();
    }


    public int size() {
        return getSupport().size();
    }


    public Iterator<Linguistic<Number>> iterator() {
        return getSupport().iterator();
    }




    public Number domainSize() {
        return size();
    }



    public Map<Linguistic<Number>,Degree> fuzzify( Number val ) {
        ValueSortedMap<Linguistic<Number>,Degree> vsmap = new ValueSortedMap<Linguistic<Number>,Degree>();
        if ( val != null ) {
            Degree master = getDegree(iterator().next());
            for (Linguistic ling : getSupport()) {
                vsmap.put(ling,master.fromConst(ling.getSet().containment(val.doubleValue())));
            }
        }
        return vsmap;
    }


    public ContinuousPossibilityDistribution<Number> asInducedPossibilityDistribution() {
        return new LinguisticPossibilityDistribution<Number>(map);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShapedFuzzyPartition that = (ShapedFuzzyPartition) o;

        if (map != null ? !map.equals(that.map) : that.map != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return map != null ? map.hashCode() : 0;
    }

    public boolean isDiscrete() {
        return true;
    }

}
