package org.drools.chance.distribution.fuzzy.linguistic;

import org.drools.chance.common.IImperfectField;
import org.drools.chance.common.ImperfectField;
import org.drools.chance.common.ImperfectHistoryField;
import org.drools.chance.common.StrategyFactory;
import org.drools.chance.degree.IDegree;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 2/1/11
 * Time: 11:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinguisticImperfectField<T extends ILinguistic, K extends Number> implements IImperfectField<T> {

    IImperfectField<T> innerField;

    protected IDistributionStrategies<K> subStrats;



    public LinguisticImperfectField(IDistributionStrategies<T> strats, IDistributionStrategies<K> subStrats,
                                    int history, String prior) {
        if (history == 0) {
            innerField = new ImperfectField<T>(strats,prior);
        } else {
            innerField = new ImperfectHistoryField<T>(strats,history,prior);
        }
        this.subStrats = subStrats;
    }

    public void setValue(T value) {
        innerField.setValue(value);
    }

    public void setValue(T value, boolean update) {
        innerField.setValue(value,update);
    }

    public void setValue(IDistribution<T> dist) {
        innerField.setValue(dist);
    }

    public void setValue(IDistribution<T> dist, boolean update) {
        innerField.setValue(dist,update);
    }

    public boolean isSet() {
        return innerField.isSet();
    }

    public T getCrisp() {
        return innerField.getCrisp();
    }

    public IDistribution<T> getPast(int time) throws IndexOutOfBoundsException {
        return innerField.getPast(time);
    }

    public IDistribution<T> getCurrent() {
        return innerField.getCurrent();
    }

    public IDistributionStrategies<T> getStrategies() {
        return innerField.getStrategies();
    }

    public void update(IDistribution<T> fieldBit) {
        innerField.update(fieldBit);
    }

    public void update(T value) {
        innerField.update(value);
    }


    public String toString() {
        return innerField.toString();
    }



    public K defuzzify() {
        return subStrats.toCrispValue(
                ((ShapedFuzzyPartition)innerField.getCurrent()).asInducedPossibilityDistribution());
    }


    public IDistribution<T> fuzzify(Number val) {
        Map<? extends T,? extends IDegree> m = ((ShapedFuzzyPartition) innerField.getCurrent()).fuzzify(val);
        return getStrategies().newDistribution(m);
    }
    
    
}
