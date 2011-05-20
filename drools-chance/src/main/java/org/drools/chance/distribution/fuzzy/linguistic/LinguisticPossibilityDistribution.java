package org.drools.chance.distribution.fuzzy.linguistic;

import de.lab4inf.fuzzy.FuzzyAlphaCutPartition;
import de.lab4inf.fuzzy.UniqueFuzzyPartition;
import org.drools.chance.degree.IDegree;
import org.drools.chance.distribution.IContinuousPossibilityDistribution;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 2/1/11
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinguisticPossibilityDistribution<T extends Number>
		implements IContinuousPossibilityDistribution<Number> {

		private FuzzyAlphaCutPartition cutPart;
        private IDegree master;

		public LinguisticPossibilityDistribution(Map<ILinguistic<Number>,IDegree> map) {
            UniqueFuzzyPartition.clearPartitionNames();
			cutPart = new FuzzyAlphaCutPartition(map.keySet().iterator().getClass().getName());
            Iterator<ILinguistic<Number>> iter = map.keySet().iterator();
            while (iter.hasNext()) {
                ILinguistic ling = iter.next();
                cutPart.add(ling.getLabel(),ling.getSet());
                cutPart.set(ling.getLabel(),map.get(ling).getValue());
                if (master == null) master = map.get(ling);
            }
		}


		public IDegree getDegree(Number value) {
			double[] mus = cutPart.fuzzyfy(value.doubleValue());
			double max = mus[0];
			for (int j = 1; j < mus.length; j++)
				max = Math.max(max,mus[j]);
			return master.fromConst(max);
		}


        public Number domainSize() {
            return Double.POSITIVE_INFINITY;
        }


        public FuzzyAlphaCutPartition getPartition() {
            return cutPart;
        }


    }