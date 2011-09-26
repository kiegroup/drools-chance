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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.*;
import org.drools.core.util.StringUtils;

public class ShapedFuzzyPartitionStrategy<T extends ILinguistic> implements IDistributionStrategies<ILinguistic> {

		
	private String degreeType;
	private Class<T> domainType;

    private Constructor degreeStringConstr = null;
    

    ShapedFuzzyPartitionStrategy(String degreeType, Class<T> domainType){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }

    private Constructor getDegreeStringConstructor() {
        if (degreeStringConstr == null)
            degreeStringConstr = DegreeTypeRegistry.getSingleInstance().getConstructorByString(degreeType);
        return degreeStringConstr;
    }

	
	

    
    
    
    
    
    
    
    


    public IDistribution<ILinguistic> toDistribution(ILinguistic value) {
        IDistribution<ILinguistic> dist = createEmptyPartition();
        ((IDiscretePossibilityDistribution<ILinguistic>) dist).getDistribution().put(value,SimpleDegree.TRUE);
        return dist;
    }

    public IDistribution<ILinguistic> toDistribution(ILinguistic value, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<ILinguistic> toDistribution(ILinguistic value, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }





	public IDistribution<ILinguistic> parse(String distrAsString) {
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
                IDegree deg = (IDegree) getDegreeStringConstructor().newInstance(sub.nextToken().trim());
                part.reshape(label, deg);
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
        return part;
    }







	protected IDistribution<ILinguistic> createEmptyPartition() {
		try {
			ILinguistic[] values = (ILinguistic[]) domainType.getMethod("values").invoke(null);
			ShapedFuzzyPartition ans = new ShapedFuzzyPartition(values);
			return ans;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public IDistribution<ILinguistic> newDistribution() {
        return createEmptyPartition();
    }

    public IDistribution<ILinguistic> newDistribution(Set<ILinguistic> focalElements) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<ILinguistic> newDistribution(Map<? extends ILinguistic, ? extends IDegree> elements) {
        return new ShapedFuzzyPartition(elements);
    }







    public ILinguistic toCrispValue(IDistribution<ILinguistic> dist) {
        return ((IDiscretePossibilityDistribution<ILinguistic>) dist).iterator().next();
    }

    public ILinguistic toCrispValue(IDistribution<ILinguistic> dist, String strategy) {
        return toCrispValue(dist);
    }

    public ILinguistic toCrispValue(IDistribution<ILinguistic> dist, Object... params) {
        return toCrispValue(dist);
    }

    public ILinguistic sample(IDistribution<ILinguistic> dist) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ILinguistic sample(IDistribution<ILinguistic> dist, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ILinguistic sample(IDistribution<ILinguistic> dist, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<ILinguistic> merge(IDistribution<ILinguistic> current, IDistribution<ILinguistic> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<ILinguistic> merge(IDistribution<ILinguistic> current, IDistribution<ILinguistic> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<ILinguistic> merge(IDistribution<ILinguistic> current, IDistribution<ILinguistic> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<ILinguistic> mergeAsNew(IDistribution<ILinguistic> current, IDistribution<ILinguistic> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<ILinguistic> mergeAsNew(IDistribution<ILinguistic> current, IDistribution<ILinguistic> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<ILinguistic> mergeAsNew(IDistribution<ILinguistic> current, IDistribution<ILinguistic> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
