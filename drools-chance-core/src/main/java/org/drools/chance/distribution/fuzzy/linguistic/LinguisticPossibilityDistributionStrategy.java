package org.drools.chance.distribution.fuzzy.linguistic;

import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.IDegree;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 2/1/11
 * Time: 11:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinguisticPossibilityDistributionStrategy<T extends Number> implements IDistributionStrategies<Number> {


    private String degreeType;
	private Class<T> domainType;

    private Constructor degreeStringConstr = null;


    LinguisticPossibilityDistributionStrategy(String degreeType, Class<T> domainType){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }

    private Constructor getDegreeStringConstructor() {
        if (degreeStringConstr == null)
            degreeStringConstr = DegreeTypeRegistry.getSingleInstance().getConstructorByString(degreeType);
        return degreeStringConstr;
    }




    public IDistribution<Number> toDistribution(Number value) {
        return null;
    }

    public IDistribution<Number> toDistribution(Number value, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> toDistribution(Number value, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> parse(String distrAsString) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> newDistribution() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> newDistribution(Set<Number> focalElements) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> newDistribution(Map<? extends Number, ? extends IDegree> elements) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Number toCrispValue(IDistribution<Number> dist) {
        return ((LinguisticPossibilityDistribution) dist).getPartition().defuzzyfy();
    }

    public Number toCrispValue(IDistribution<Number> dist, String strategy) {
        return toCrispValue(dist);
    }

    public Number toCrispValue(IDistribution<Number> dist, Object... params) {
        return toCrispValue(dist);
    }

    public Number sample(IDistribution<Number> dist) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Number sample(IDistribution<Number> dist, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Number sample(IDistribution<Number> dist, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> merge(IDistribution<Number> current, IDistribution<Number> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> merge(IDistribution<Number> current, IDistribution<Number> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> merge(IDistribution<Number> current, IDistribution<Number> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> mergeAsNew(IDistribution<Number> current, IDistribution<Number> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> mergeAsNew(IDistribution<Number> current, IDistribution<Number> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Number> mergeAsNew(IDistribution<Number> current, IDistribution<Number> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
