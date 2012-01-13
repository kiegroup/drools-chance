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

import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.IContinuousPossibilityDistribution;
import org.drools.chance.distribution.IDiscretePossibilityDistribution;
import org.drools.chance.utils.ValueSortedMap;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ShapedFuzzyPartition<T extends ILinguistic<Number>> implements IDiscretePossibilityDistribution<ILinguistic<Number>> {

    private ValueSortedMap<ILinguistic<Number>,IDegree> map;



    public ShapedFuzzyPartition(ILinguistic[] values) {
        map = new ValueSortedMap<ILinguistic<Number>, IDegree>();
        for (ILinguistic l : values) {
            map.put(l,SimpleDegree.FALSE);
        }
    }

    public ShapedFuzzyPartition(Map<? extends ILinguistic,? extends IDegree> elements) {
        this.map = new ValueSortedMap();
        for (ILinguistic ling : elements.keySet()) {
            map.put(ling,elements.get(ling));
        }
    }


    public Map<ILinguistic<Number>, IDegree> getDistribution() {
        return map;
    }


    public void reshape(ILinguistic key, IDegree deg) {
        map.put(key, deg);
    }


    public void reshape(String key, IDegree deg) {
        map.put(iterator().next().parse(key), deg);
    }



    public IDegree getDegree(ILinguistic key) {
        return map.get(key);
    }

    public IDegree get(ILinguistic value) {
        return getDegree( value );
    }


    public String toString() {
        return "(Fuzzy Ling) : {" + serialize() + "}";
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        Iterator<ILinguistic<Number>> iter = getSupport().iterator();
        while (iter.hasNext()) {
            ILinguistic elem = iter.next();
            sb.append(elem).append("/").append(getDegree(elem).getValue());
            if (iter.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }



    public Set<ILinguistic<Number>> getSupport() {
        return map.keySet();
    }


    public int size() {
        return getSupport().size();
    }


    public Iterator<ILinguistic<Number>> iterator() {
        return getSupport().iterator();
    }




    public Number domainSize() {
        return size();
    }



    public Map<ILinguistic<Number>,IDegree> fuzzify( Number val ) {
        ValueSortedMap<ILinguistic<Number>,IDegree> vsmap = new ValueSortedMap<ILinguistic<Number>,IDegree>();
        if ( val != null ) {
            IDegree master = getDegree(iterator().next());
            for (ILinguistic ling : getSupport()) {
                vsmap.put(ling,master.fromConst(ling.getSet().containment(val.doubleValue())));
            }
        }
        return vsmap;
    }


    public IContinuousPossibilityDistribution<Number> asInducedPossibilityDistribution() {
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


}
