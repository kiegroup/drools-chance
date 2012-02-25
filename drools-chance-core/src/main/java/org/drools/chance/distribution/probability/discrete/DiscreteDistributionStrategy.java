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

import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.DiscreteProbabilityDistribution;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.DistributionStrategies;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * Strategy and level III factory for discrete probability distributions
 * @param <T>
 */
public class DiscreteDistributionStrategy<T>  implements DistributionStrategies<T> {



    private DegreeType degreeType;
    private Class<T> domainType;

    private Constructor degreeStringConstr = null;



    DiscreteDistributionStrategy(DegreeType degreeType, Class<T> domainType){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }


    private Constructor getDegreeStringConstructor() {
        if (degreeStringConstr == null) {
            degreeStringConstr = DegreeTypeRegistry.getSingleInstance().getConstructorByString(degreeType);
        }
        return degreeStringConstr;
    }





    public Distribution<T> merge(Distribution<T> current,
                                  Distribution<T> newBit) {
        //ToDo iterare sull indice piu corto
    	DiscreteProbabilityDistribution<T> currentDD= (DiscreteProbabilityDistribution<T>) current;
    	DiscreteProbabilityDistribution<T> newBitDD= (DiscreteProbabilityDistribution<T>) newBit;
        Map<T,Degree> map = new HashMap<T,Degree>();

         Degree denominator=null;
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
                   Degree temp =  (newBitDD.getDegree(tempT).mul(current.getDegree(tempT))).div(denominator);
                    map.put(tempT,temp);
                }
        }

        currentDD.getDistribution().clear();
        currentDD.getDistribution().putAll(map);
        return currentDD;
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
    	DiscreteProbabilityDistribution<T> currentDD= (DiscreteProbabilityDistribution<T>) current;
    	DiscreteProbabilityDistribution<T> newBitDD= (DiscreteProbabilityDistribution<T>) newBit;
    	DiscreteDistribution<T> ret=new DiscreteDistribution<T>();

        Degree denominator=null;
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
                    Degree temp = (currentDD.getDegree(tempT).mul( newBit.getDegree(tempT))).div(denominator);
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
                    Degree temp = (newBitDD.getDegree(tempT).mul(current.getDegree(tempT))).div(denominator) ;
                    ret.put(tempT, temp);
                }
            }
        }

        return ret;
	}


	public Distribution<T> mergeAsNew(Distribution<T> current,
			Distribution<T> newBit, String strategy) {
		         return this.mergeAsNew(current,newBit);

	}


	public Distribution<T> mergeAsNew(Distribution<T> current,
			Distribution<T> newBit, Object... params) {
        return this.mergeAsNew(current,newBit);

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


    public Distribution<T> newDistribution() {
        if ( Boolean.class.equals( domainType ) ) {
            return createUniformDistribution( (Collection<T>) Arrays.asList( Boolean.TRUE, Boolean.FALSE ) );
        } else {
            return new DiscreteDistribution<T>();
        }


	}

	public Distribution<T> newDistribution(Set<T> focalElements) {
		return createUniformDistribution( focalElements );
	}

    private Distribution<T> createUniformDistribution( Collection<T> focalElements) {
        DiscreteDistribution<T> ret = new DiscreteDistribution<T>();
                for( Iterator<? extends T> currIt = focalElements.iterator(); currIt.hasNext() ; ) {
                    ret.put( currIt.next(), DegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 1.0 / focalElements.size()) );
                }
        return ret;
	}

    public Distribution<T> newDistribution(Map<? extends T, ? extends Degree> elements) {
        DiscreteDistribution<T> ret = new DiscreteDistribution<T>();
        for( Iterator<? extends T> currIt = elements.keySet().iterator(); currIt.hasNext() ;) {
              T temp=currIt.next();
            ret.put(temp,elements.get(temp));
       }
       return ret;
    }

    public T toCrispValue(Distribution<T> dist) {
		return ((DiscreteDistribution<T>) dist).getBest();

	}


	public T toCrispValue(Distribution<T> dist, String strategy) {
		return ((DiscreteDistribution<T>) dist).getBest();
	}


	public T toCrispValue(Distribution<T> dist, Object... params) {
		return ((DiscreteDistribution<T>) dist).getBest();
	}

    public T sample(Distribution<T> dist) {
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

    public T sample(Distribution<T> dist, String strategy) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public T sample(Distribution<T> dist, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Distribution<T> toDistribution(T value) {
    	DiscreteDistribution<T> dist = new DiscreteDistribution<T>();
        dist.put(value, new SimpleDegree(1.0));
		return dist;
	}


	public Distribution<T> toDistribution(T value, String strategy) {
		DiscreteDistribution<T> dist = new DiscreteDistribution<T>();
        dist.put(value, new SimpleDegree(1.0));
		return dist;
	}

	public Distribution<T> toDistribution(T value, Object... params) {
		DiscreteDistribution<T> dist = new DiscreteDistribution<T>();
        dist.put(value, new SimpleDegree(1.0));
		return dist;
	}

    public Distribution<T> parse(String distrAsString) {
        DiscreteDistribution<T> dist = new DiscreteDistribution<T>();

        StringTokenizer tok = new StringTokenizer(distrAsString,",");

        while (tok.hasMoreElements()) {
            String pair = tok.nextToken().trim();
            StringTokenizer sub = new StringTokenizer(pair,"/");

            try {
                T value = (T) domainType.getConstructor( String.class ).newInstance( sub.nextToken().trim() );
                Degree deg = (Degree) getDegreeStringConstructor().newInstance( sub.nextToken().trim() );

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
