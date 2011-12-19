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

package org.drools.chance.distribution.probability.discrete;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.IDiscreteProbabilityDistribution;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;


/**
 * Strategy and level III factory for discrete probability distributions
 * @param <T>
 */
public class DiscreteDistributionStrategy<T>  implements IDistributionStrategies<T> {



    private String degreeType;
    private Class<T> domainType;

    private Constructor degreeStringConstr = null;



    DiscreteDistributionStrategy(String degreeType, Class<T> domainType){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }


    private Constructor getDegreeStringConstructor() {
        if (degreeStringConstr == null) {
            degreeStringConstr = DegreeTypeRegistry.getSingleInstance().getConstructorByString(degreeType);
        }
        return degreeStringConstr;
    }





    public IDistribution<T> merge(IDistribution<T> current,
                                  IDistribution<T> newBit) {
        //ToDo iterare sull indice piu corto
    	IDiscreteProbabilityDistribution<T> currentDD= (IDiscreteProbabilityDistribution<T>) current;
    	IDiscreteProbabilityDistribution<T> newBitDD= (IDiscreteProbabilityDistribution<T>) newBit;
        Map<T,IDegree> map = new HashMap<T,IDegree>();

         IDegree denominator=null;
         int i=0;

        for( Iterator<T> currIt = currentDD.getSupport().iterator(); currIt.hasNext() ;){
            T tempT=currIt.next();
                if (newBitDD.getDistribution().containsKey(tempT)) {

                    if(i==0){
                    denominator=newBitDD.getDegree(tempT).mul(current.getDegree(tempT));
                        i++;
                    }

                    else
                   denominator=denominator.sum(newBitDD.getDegree(tempT).mul(current.getDegree(tempT)));
                }
            }





        for( Iterator<T> currIt = currentDD.getSupport().iterator(); currIt.hasNext() ;){
                T tempT = currIt.next();
                if (newBitDD.getDistribution().containsKey(tempT)) {
                   IDegree temp =  (newBitDD.getDegree(tempT).mul(current.getDegree(tempT))).div(denominator);
                    map.put(tempT,temp);
                }
        }

        currentDD.getDistribution().clear();
        currentDD.getDistribution().putAll(map);
        return currentDD;
    }


	public IDistribution<T> merge(IDistribution<T> current,
			IDistribution<T> newBit, String strategy) {
		return merge(current,newBit);
	}


	public IDistribution<T> merge(IDistribution<T> current,
			IDistribution<T> newBit, Object... params) {
		  return merge(current,newBit);
	}






    public IDistribution<T> mergeAsNew(IDistribution<T> current,
			IDistribution<T> newBit) {
    	IDiscreteProbabilityDistribution<T> currentDD= (IDiscreteProbabilityDistribution<T>) current;
    	IDiscreteProbabilityDistribution<T> newBitDD= (IDiscreteProbabilityDistribution<T>) newBit;
    	DiscreteDistribution<T> ret=new DiscreteDistribution<T>();

        IDegree denominator=null;
        int i=0;

        if (newBitDD.getSupport().size() < currentDD.getSupport().size()) {
            for( Iterator<T> newBitIt = newBitDD.getSupport().iterator(); newBitIt.hasNext() ;){
                T tempT=newBitIt.next();
                if (currentDD.getDistribution().containsKey(tempT)) {
                    if(i==0) {
                    denominator = currentDD.getDegree(tempT).mul(newBit.getDegree(tempT));
                        i++;
                    }
                    else
                      denominator =denominator.sum(currentDD.getDegree(tempT).mul(newBit.getDegree(tempT)));

                }
            }

            for( Iterator<T> newBitIt = newBitDD.getSupport().iterator(); newBitIt.hasNext() ;){
                T tempT = newBitIt.next();
                if (currentDD.getDistribution().containsKey(tempT)) {
                    IDegree temp = (currentDD.getDegree(tempT).mul( newBit.getDegree(tempT))).div(denominator);
                    ret.put(tempT, temp);
                }
            }
        } else {
             i=0;
            for( Iterator<T> currIt = currentDD.getSupport().iterator(); currIt.hasNext() ;){
                T tempT=currIt.next();
                if (newBitDD.getDistribution().containsKey(tempT)) {
                    if(i==0) {
                    denominator = newBitDD.getDegree(tempT).mul(current.getDegree(tempT));
                        i++;
                    }
                    else
                       denominator = denominator.sum(newBitDD.getDegree(tempT).mul(current.getDegree(tempT)));
                }
            }

            for( Iterator<T> currIt = currentDD.getSupport().iterator(); currIt.hasNext() ;){
                T tempT = currIt.next();
                if (newBitDD.getDistribution().containsKey(tempT)) {
                    IDegree temp = (newBitDD.getDegree(tempT).mul(current.getDegree(tempT))).div(denominator) ;
                    ret.put(tempT, temp);
                }
            }
        }

        return ret;
	}


	public IDistribution<T> mergeAsNew(IDistribution<T> current,
			IDistribution<T> newBit, String strategy) {
		         return this.mergeAsNew(current,newBit);

	}


	public IDistribution<T> mergeAsNew(IDistribution<T> current,
			IDistribution<T> newBit, Object... params) {
        return this.mergeAsNew(current,newBit);

	}


    public IDistribution<T> newDistribution() {
        if ( Boolean.class.equals( domainType ) ) {
            return createUniformDistribution( (Collection<T>) Arrays.asList( Boolean.TRUE, Boolean.FALSE ) );
        } else {
            return new DiscreteDistribution<T>();
        }


	}

	public IDistribution<T> newDistribution(Set<T> focalElements) {
		return createUniformDistribution( focalElements );
	}

    private IDistribution<T> createUniformDistribution( Collection<T> focalElements) {
        DiscreteDistribution<T> ret = new DiscreteDistribution<T>();
                for( Iterator<? extends T> currIt = focalElements.iterator(); currIt.hasNext() ; ) {
                    ret.put( currIt.next(), DegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 1.0 / focalElements.size()) );
                }
        return ret;
	}

    public IDistribution<T> newDistribution(Map<? extends T, ? extends IDegree> elements) {
        DiscreteDistribution<T> ret = new DiscreteDistribution<T>();
        for( Iterator<? extends T> currIt = elements.keySet().iterator(); currIt.hasNext() ;) {
              T temp=currIt.next();
            ret.put(temp,elements.get(temp));
       }
       return ret;
    }

    public T toCrispValue(IDistribution<T> dist) {
		return ((DiscreteDistribution<T>) dist).getBest();

	}


	public T toCrispValue(IDistribution<T> dist, String strategy) {
		return ((DiscreteDistribution<T>) dist).getBest();
	}


	public T toCrispValue(IDistribution<T> dist, Object... params) {
		return ((DiscreteDistribution<T>) dist).getBest();
	}

    public T sample(IDistribution<T> dist) {
//       double p = Math.random();
//		double acc = 0.0;
//		T result = null;
//		Iterator<ValueDegreePair<T>> iter = _multipleValue.descendingIterator();
//		while ( acc < p ) {
//			ValueDegreePair<T> pair = iter.next();
//			result = pair.getValue();
//			acc += pair.getDegree().getValue();
//		}
//		return result;
        return null;
    }

    public T sample(IDistribution<T> dist, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public T sample(IDistribution<T> dist, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public IDistribution<T> toDistribution(T value) {
    	DiscreteDistribution<T> dist = new DiscreteDistribution<T>();
        dist.put(value, new SimpleDegree(1.0));
		return dist;
	}


	public IDistribution<T> toDistribution(T value, String strategy) {
		DiscreteDistribution<T> dist = new DiscreteDistribution<T>();
        dist.put(value, new SimpleDegree(1.0));
		return dist;
	}

	public IDistribution<T> toDistribution(T value, Object... params) {
		DiscreteDistribution<T> dist = new DiscreteDistribution<T>();
        dist.put(value, new SimpleDegree(1.0));
		return dist;
	}

    public IDistribution<T> parse(String distrAsString) {
        DiscreteDistribution<T> dist = new DiscreteDistribution<T>();
        double m = 0;



        StringTokenizer tok = new StringTokenizer(distrAsString,",");

        while (tok.hasMoreElements()) {
            String pair = tok.nextToken().trim();
            StringTokenizer sub = new StringTokenizer(pair,"/");

            try {
                T value = (T) domainType.getConstructor(String.class).newInstance(sub.nextToken().trim());
                IDegree deg= (IDegree) getDegreeStringConstructor().newInstance(sub.nextToken().trim());

                dist.put(value,deg);
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
        return dist;
    }




	







}
