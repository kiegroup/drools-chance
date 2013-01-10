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

import de.lab4inf.fuzzy.FuzzyAlphaCutPartition;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.DistributionStrategies;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

public class LinguisticPossibilityDistributionStrategy<T extends Number> implements DistributionStrategies<Number> {


    private DegreeType degreeType;
	private Class<T> domainType;

    private Constructor degreeStringConstr = null;


    LinguisticPossibilityDistributionStrategy(DegreeType degreeType, Class<T> domainType){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }

    private Constructor getDegreeStringConstructor() {
        if (degreeStringConstr == null)
            degreeStringConstr = ChanceDegreeTypeRegistry.getSingleInstance().getConstructorByString(degreeType);
        return degreeStringConstr;
    }




    public Distribution<Number> toDistribution(Number value) {
        return null;
    }

    public Distribution<Number> toDistribution(Number value, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> toDistribution(Number value, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> parse(String distrAsString) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> newDistribution() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> newDistribution(Set<Number> focalElements) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> newDistribution(Map<? extends Number, ? extends Degree> elements) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Number toCrispValue(Distribution<Number> dist) {
        FuzzyAlphaCutPartition part = ((LinguisticPossibilityDistribution) dist).getPartition();
        Number crisp = part.defuzzyfy();
        return crisp;
    }

    public Number toCrispValue(Distribution<Number> dist, String strategy) {
        return toCrispValue(dist);
    }

    public Number toCrispValue(Distribution<Number> dist, Object... params) {
        return toCrispValue(dist);
    }

    public Number sample(Distribution<Number> dist) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Number sample(Distribution<Number> dist, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Number sample(Distribution<Number> dist, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> merge(Distribution<Number> current, Distribution<Number> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> merge(Distribution<Number> current, Distribution<Number> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> merge(Distribution<Number> current, Distribution<Number> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> mergeAsNew(Distribution<Number> current, Distribution<Number> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> mergeAsNew(Distribution<Number> current, Distribution<Number> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> mergeAsNew(Distribution<Number> current, Distribution<Number> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> remove(Distribution<Number> current, Distribution<Number> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> remove(Distribution<Number> current, Distribution<Number> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> remove(Distribution<Number> current, Distribution<Number> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> removeAsNew(Distribution<Number> current, Distribution<Number> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> removeAsNew(Distribution<Number> current, Distribution<Number> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Number> removeAsNew(Distribution<Number> current, Distribution<Number> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void normalize(Distribution<Number> distr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
