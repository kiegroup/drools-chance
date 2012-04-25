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

import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.DiscretePossibilityDistribution;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.DistributionStrategies;
import org.drools.core.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class ShapedFuzzyPartitionStrategy<T extends Linguistic> implements DistributionStrategies<Linguistic> {

		
	private DegreeType degreeType;
	private Class<T> domainType;

    private Constructor degreeStringConstr = null;
    

    ShapedFuzzyPartitionStrategy( DegreeType degreeType, Class<T> domainType ){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }

    private Constructor getDegreeStringConstructor() {
        if ( degreeStringConstr == null ) {
            degreeStringConstr = ChanceDegreeTypeRegistry.getSingleInstance().getConstructorByString( degreeType );
        }
        return degreeStringConstr;
    }

	
	

    
    
    
    
    
    
    
    


    public Distribution<Linguistic> toDistribution(Linguistic value) {
        Distribution<Linguistic> dist = createEmptyPartition();
        if ( value != null ) {
            ((DiscretePossibilityDistribution<Linguistic>) dist).getDistribution().put(value,SimpleDegree.TRUE);
        }
        return dist;
    }

    public Distribution<Linguistic> toDistribution(Linguistic value, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> toDistribution(Linguistic value, Object... params) {
        ShapedFuzzyPartition part = (ShapedFuzzyPartition) createEmptyPartition();
        part.reshape( value, (Degree) params[0] );
        return part;
    }





	public Distribution<Linguistic> parse(String distrAsString) {
		ShapedFuzzyPartition part = (ShapedFuzzyPartition) createEmptyPartition();

        if ( StringUtils.isEmpty( distrAsString ) ) {
            return part;
        }

		StringTokenizer tok = new StringTokenizer(distrAsString,",");

        while (tok.hasMoreElements()) {
            String pair = tok.nextToken().trim();
            StringTokenizer sub = new StringTokenizer(pair,"/");

            try {
            	String label = sub.nextToken().trim();
                Degree deg = (Degree) getDegreeStringConstructor().newInstance(sub.nextToken().trim() );
                part.reshape(label, deg);
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
        return part;
    }







	protected Distribution<Linguistic> createEmptyPartition() {
		try {
			Linguistic[] values = (Linguistic[]) domainType.getMethod("values").invoke(null);
			ShapedFuzzyPartition ans = new ShapedFuzzyPartition(values);
			return ans;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public Distribution<Linguistic> newDistribution() {
        return createEmptyPartition();
    }

    public Distribution<Linguistic> newDistribution(Set<Linguistic> focalElements) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> newDistribution(Map<? extends Linguistic, ? extends Degree> elements) {
        return new ShapedFuzzyPartition(elements);
    }







    public Linguistic toCrispValue(Distribution<Linguistic> dist) {
        DiscretePossibilityDistribution<Linguistic> ldist = (DiscretePossibilityDistribution<Linguistic>) dist;
        if ( ldist.size() == 0 ) {
            return null;
        }
        return ldist.iterator().next();
    }

    public Linguistic toCrispValue(Distribution<Linguistic> dist, String strategy) {
        return toCrispValue(dist);
    }

    public Linguistic toCrispValue(Distribution<Linguistic> dist, Object... params) {
        return toCrispValue(dist);
    }

    public Linguistic sample(Distribution<Linguistic> dist) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Linguistic sample(Distribution<Linguistic> dist, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Linguistic sample(Distribution<Linguistic> dist, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> merge(Distribution<Linguistic> current, Distribution<Linguistic> newBit) {
        ShapedFuzzyPartition part1 = (ShapedFuzzyPartition) current;
        ShapedFuzzyPartition part2 = (ShapedFuzzyPartition) newBit;

        Iterator<Linguistic<Number>> iter = part2.iterator();
        while ( iter.hasNext() ) {
            Linguistic<Number> ling = iter.next();
            Degree deg = part2.getDegree( ling );

            part1.reshape( ling, part1.getDegree( ling ).max( deg ) );
        }

        return part1;

    }

    public Distribution<Linguistic> merge(Distribution<Linguistic> current, Distribution<Linguistic> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> merge(Distribution<Linguistic> current, Distribution<Linguistic> newBit, Object... params) {
        ShapedFuzzyPartition part1 = (ShapedFuzzyPartition) current;
        ShapedFuzzyPartition part2 = (ShapedFuzzyPartition) newBit;

        Iterator<Linguistic<Number>> iter = part2.iterator();
        while ( iter.hasNext() ) {
            Linguistic<Number> ling = iter.next();
            Degree deg1 = part1.getDegree( ling );
            Degree deg2 = part2.getDegree( ling );

            part1.reshape( ling, deg1.max( deg2 ) );
        }
        return part1;
    }

    public Distribution<Linguistic> mergeAsNew(Distribution<Linguistic> current, Distribution<Linguistic> newBit) {
        ShapedFuzzyPartition part1 = (ShapedFuzzyPartition) current;
        ShapedFuzzyPartition part2 = (ShapedFuzzyPartition) newBit;
        ShapedFuzzyPartition ansPt = (ShapedFuzzyPartition) createEmptyPartition();

        Iterator<Linguistic<Number>> iter = ansPt.iterator();
        while ( iter.hasNext() ) {
            Linguistic<Number> ling = iter.next();
            Degree deg1 = part1.getDegree( ling );
            Degree deg2 = part2.getDegree( ling );

            part1.reshape( ling, deg1.max( deg2 ) );
        }

        return ansPt;
    }

    public Distribution<Linguistic> mergeAsNew(Distribution<Linguistic> current, Distribution<Linguistic> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> mergeAsNew(Distribution<Linguistic> current, Distribution<Linguistic> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> remove(Distribution<Linguistic> current, Distribution<Linguistic> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> remove(Distribution<Linguistic> current, Distribution<Linguistic> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> remove(Distribution<Linguistic> current, Distribution<Linguistic> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> removeAsNew(Distribution<Linguistic> current, Distribution<Linguistic> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> removeAsNew(Distribution<Linguistic> current, Distribution<Linguistic> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Linguistic> removeAsNew(Distribution<Linguistic> current, Distribution<Linguistic> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
