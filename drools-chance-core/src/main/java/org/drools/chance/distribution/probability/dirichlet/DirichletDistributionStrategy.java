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

package org.drools.chance.distribution.probability.dirichlet;

import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.distribution.DiscreteDomainDistribution;
import org.drools.chance.distribution.DiscreteProbabilityDistribution;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.DistributionStrategies;
import org.drools.chance.core.util.ValueSortedMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * Strategy and level III factory for Dirichlet probability distributions
 * @param <T>
 */
public class DirichletDistributionStrategy<T> implements DistributionStrategies<T> {



    private DegreeType degreeType;
    private Class<T> domainType;

    private Constructor degreeStringConstr = null;



    DirichletDistributionStrategy(DegreeType degreeType, Class<T> domainType){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }


    private Constructor getDegreeStringConstructor() {
    	if (degreeStringConstr == null)
            degreeStringConstr = ChanceDegreeTypeRegistry.getSingleInstance().getConstructorByString(degreeType);
        return degreeStringConstr;
    }



	public Distribution<T> merge(Distribution<T> current,
			Distribution<T> newBit) {
		if (current instanceof DirichletDistribution && newBit instanceof DirichletDistribution) {
            DirichletDistribution<T> curr = (DirichletDistribution<T>) current;
            Map<T,Double> a1 = curr.getAlphaWeights();
            Map<T,Double> a2 = ((DirichletDistribution<T>) newBit).getAlphaWeights();
            double m = curr.getMass();

            Iterator<T> it = new HashSet<T>(a1.keySet()).iterator();
            while (it.hasNext()) {
                T key = it.next();
                if (a2.containsKey(key)) {
                    double x = a2.get(key);
                    a1.put(key,a1.get(key) + x );
                    m += x;
                }
            }
            for (T key : a2.keySet()) {
                if (! a1.containsKey(key)) {
                    double x = a2.get(key);
                    a1.put(key, x );
                    m += x;
                }
            }

            curr.setMass(m);
            return curr;

        } else if ( current instanceof DirichletDistribution && newBit instanceof DiscreteDomainDistribution) {

            DirichletDistribution<T> curr = (DirichletDistribution<T>) current;
            Map<T,Double> a1 = curr.getAlphaWeights();
            Map<T,Degree> a2 = ((DiscreteProbabilityDistribution<T>) newBit).getDistribution();
            double m = curr.getMass();

            for (T key : a1.keySet()) {
                if (a2.containsKey(key)) {
                    double x = a2.get(key).getValue();
                    a1.put(key,a1.get(key) + x );
                    m += x;
                }
            }
            for (T key : a2.keySet()) {
                if (! a1.containsKey(key)) {
                    double x = a2.get(key).getValue();
                    a1.put(key, x );
                    m += x;
                }
            }

            curr.setMass(m);
            return curr;

        } else {
            throw new UnsupportedOperationException("Dirichlet Strategies : unable to merge "
                    + current.getClass().getName() + " with " + newBit.getClass().getName());
        }
	}


	public Distribution<T> merge(Distribution<T> current,
			Distribution<T> newBit, String strategy) {
		return merge(current,newBit);
	}


	public Distribution<T> merge(Distribution<T> current,
			Distribution<T> newBit, Object... params) {
		return merge(current,newBit);
	}










    public Distribution<T> mergeAsNew(Distribution<T> current,
			Distribution<T> newBit) {
		if (current instanceof DirichletDistribution && newBit instanceof DirichletDistribution) {
            DirichletDistribution<T> distr = new DirichletDistribution<T>();
            Map<T,Double> a = distr.getAlphaWeights();
            double m = 0;

            DirichletDistribution<T> curr = (DirichletDistribution<T>) current;
            Map<T,Double> a1 = curr.getAlphaWeights();
            Map<T,Double> a2 = ((DirichletDistribution<T>) newBit).getAlphaWeights();


            for (T key : a1.keySet()) {
                if (a2.containsKey(key)) {
                    double x = a1.get(key) + a2.get(key);
                    a.put(key, x );
                    m += x;
                } else {
                    double x = a1.get(key);
                    a.put(key, x );
                    m += x;
                }
            }
            for (T key : a2.keySet()) {
                if (! a1.containsKey(key)) {
                    double x = a2.get(key);
                    a.put(key, x );
                    m += x;
                }
            }

            distr.setMass(m);
            return distr;

        } else if ( current instanceof DirichletDistribution && newBit instanceof DiscreteDomainDistribution) {

            DirichletDistribution<T> distr = new DirichletDistribution<T>();
            Map<T,Double> a = distr.getAlphaWeights();
            double m = 0;

            DirichletDistribution<T> curr = (DirichletDistribution<T>) current;
            Map<T,Double> a1 = curr.getAlphaWeights();
            Map<T,Degree> a2 = ((DiscreteProbabilityDistribution<T>) newBit).getDistribution();


            for (T key : a1.keySet()) {
                if (a2.containsKey(key)) {
                    double x = a1.get(key) + a2.get(key).getValue();
                    a.put(key, x );
                    m += x;
                } else {
                    double x = a1.get(key);
                    a.put(key, x );
                    m += x;
                }
            }
            for (T key : a2.keySet()) {
                if (! a1.containsKey(key)) {
                    double x = a2.get(key).getValue();
                    a.put(key, x );
                    m += x;
                }
            }

            distr.setMass(m);
            return distr;

        } else {
            throw new UnsupportedOperationException("Dirichlet Strategies : unable to merge "
                    + current.getClass().getName() + " with " + newBit.getClass().getName());
        }
	}


	public Distribution<T> mergeAsNew(Distribution<T> current,
			Distribution<T> newBit, String strategy) {
		return mergeAsNew(current, newBit);
	}


	public Distribution<T> mergeAsNew(Distribution<T> current,
			Distribution<T> newBit, Object... params) {
		return mergeAsNew(current, newBit);
	}

    public Distribution<T> remove(Distribution<T> current, Distribution<T> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> remove(Distribution<T> current, Distribution<T> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> remove(Distribution<T> current, Distribution<T> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> removeAsNew(Distribution<T> current, Distribution<T> newBit) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> removeAsNew(Distribution<T> current, Distribution<T> newBit, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<T> removeAsNew(Distribution<T> current, Distribution<T> newBit, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void normalize(Distribution<T> distr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public Distribution<T> newDistribution() {
		DirichletDistribution<T> dist = new DirichletDistribution<T>();        ;
        return dist;
	}

	public Distribution<T> newDistribution(Set<T> focalElements) {
		DirichletDistribution<T> dist = new DirichletDistribution<T>();
        for (T value : focalElements) {
            dist.getAlphaWeights().put(value,1.0);
        }
        dist.setMass(focalElements.size());
        return dist;
	}

    public Distribution<T> newDistribution(Map<? extends T, ? extends Degree> elements) {
        DirichletDistribution<T> dist = new DirichletDistribution<T>();
        double m = 0;
        for (T value : elements.keySet()) {
            double x = elements.get(value).getValue();
            dist.getAlphaWeights().put(value,x);
            m += x;
        }
        dist.setMass(m);
        return dist;
    }







    public T toCrispValue(Distribution<T> dist) {
        ValueSortedMap<T,Double> aw = ((DirichletDistribution<T>) dist).getAlphaWeights();
		return aw.isEmpty() ? null : (T) aw.keySet().iterator().next();
	}


	public T toCrispValue(Distribution<T> dist, String strategy) {
		return toCrispValue(dist);
	}


	public T toCrispValue(Distribution<T> dist, Object... params) {
		return toCrispValue(dist);
	}





    public T sample(Distribution<T> dist) {
        DirichletDistribution<T> diric = (DirichletDistribution<T>) dist;
        Iterator<T> iter = diric.getSupport().iterator();

        double p = Math.random();
		double acc = 0.0;
		T result = null;

		while ( acc < p ) {
            T elem = iter.next();
			double x = dist.getDegree(elem).getValue();
			result = elem;
			acc += x;
		}
		return result;
    }

    public T sample(Distribution<T> dist, String strategy) {
        return sample(dist);
    }

    public T sample(Distribution<T> dist, Object... params) {
        return sample(dist);
    }








    public Distribution<T> toDistribution(T value) {
		return buildDistributionFromSingleObservation(value,1.0);
	}


	public Distribution<T> toDistribution(T value, String strategy) {
		if ("spike".equals(strategy)) {
            return buildDistributionFromSingleObservation(value,Double.MAX_VALUE);
        }
        return toDistribution(value);
	}

	public Distribution<T> toDistribution(T value, Object... params) {
		return toDistribution(value);
	}

    protected Distribution<T> buildDistributionFromSingleObservation(T value, double wgt) {
        DirichletDistribution<T> dist = new DirichletDistribution<T>();
            dist.getAlphaWeights().put(value,wgt);
            dist.setMass(wgt);
        return dist;
    }







    public Distribution<T> parse(String distrAsString) {
        DirichletDistribution<T> dist = new DirichletDistribution<T>();
        double m = 0;

        StringTokenizer tok = new StringTokenizer(distrAsString,",");

        while (tok.hasMoreElements()) {
            String pair = tok.nextToken().trim();
            StringTokenizer sub = new StringTokenizer(pair,"/");

            try {
                T value = (T) domainType.getConstructor(String.class).newInstance(sub.nextToken().trim());
                double x = Double.valueOf(sub.nextToken().trim());
                dist.getAlphaWeights().put(value,x);
                m += x;
            } catch (NoSuchMethodException nsme) {
                nsme.printStackTrace();
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            } catch (InstantiationException ie) {
                ie.printStackTrace();
            } catch (InvocationTargetException ite) {
                ite.printStackTrace();
            }

        }
        dist.setMass(m);
        return dist;
    }




}
