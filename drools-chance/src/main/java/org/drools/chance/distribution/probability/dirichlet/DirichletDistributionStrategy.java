package org.drools.chance.distribution.probability.dirichlet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.drools.chance.degree.DegreeTypeRegistry;
import org.drools.chance.degree.IDegree;
import org.drools.chance.distribution.IDiscreteDomainDistribution;
import org.drools.chance.distribution.IDiscreteProbabilityDistribution;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;


/**
 * Strategy and level III factory for Dirichlet probability distributions
 * @param <T>
 */
public class DirichletDistributionStrategy<T>  implements IDistributionStrategies<T> {



    private String degreeType;
    private Class<T> domainType;

    private Constructor degreeStringConstr = null;



    DirichletDistributionStrategy(String degreeType, Class<T> domainType){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }


    private Constructor getDegreeStringConstructor() {
    	if (degreeStringConstr == null)
            degreeStringConstr = DegreeTypeRegistry.getSingleInstance().getConstructorByString(degreeType);
        return degreeStringConstr;
    }



	public IDistribution<T> merge(IDistribution<T> current,
			IDistribution<T> newBit) {
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

        } else if ( current instanceof DirichletDistribution && newBit instanceof IDiscreteDomainDistribution) {

            DirichletDistribution<T> curr = (DirichletDistribution<T>) current;
            Map<T,Double> a1 = curr.getAlphaWeights();
            Map<T,IDegree> a2 = ((IDiscreteProbabilityDistribution<T>) newBit).getDistribution();
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

        } else if ( current instanceof DirichletDistribution && newBit instanceof IDiscreteDomainDistribution) {

            DirichletDistribution<T> distr = new DirichletDistribution<T>();
            Map<T,Double> a = distr.getAlphaWeights();
            double m = 0;

            DirichletDistribution<T> curr = (DirichletDistribution<T>) current;
            Map<T,Double> a1 = curr.getAlphaWeights();
            Map<T,IDegree> a2 = ((IDiscreteProbabilityDistribution<T>) newBit).getDistribution();


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


	public IDistribution<T> mergeAsNew(IDistribution<T> current,
			IDistribution<T> newBit, String strategy) {
		return mergeAsNew(current, newBit);
	}


	public IDistribution<T> mergeAsNew(IDistribution<T> current,
			IDistribution<T> newBit, Object... params) {
		return mergeAsNew(current, newBit);
	}



    public IDistribution<T> newDistribution() {
		DirichletDistribution<T> dist = new DirichletDistribution<T>();        ;
        return dist;
	}

	public IDistribution<T> newDistribution(Set<T> focalElements) {
		DirichletDistribution<T> dist = new DirichletDistribution<T>();
        for (T value : focalElements) {
            dist.getAlphaWeights().put(value,1.0);
        }
        dist.setMass(focalElements.size());
        return dist;
	}

    public IDistribution<T> newDistribution(Map<? extends T, ? extends IDegree> elements) {
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







    public T toCrispValue(IDistribution<T> dist) {
		return ((DirichletDistribution<T>) dist).getAlphaWeights().keySet().iterator().next();
	}


	public T toCrispValue(IDistribution<T> dist, String strategy) {
		return toCrispValue(dist);
	}


	public T toCrispValue(IDistribution<T> dist, Object... params) {
		return toCrispValue(dist);
	}





    public T sample(IDistribution<T> dist) {
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

    public T sample(IDistribution<T> dist, String strategy) {
        return sample(dist);
    }

    public T sample(IDistribution<T> dist, Object... params) {
        return sample(dist);
    }








    public IDistribution<T> toDistribution(T value) {
		return buildDistributionFromSingleObservation(value,1.0);
	}


	public IDistribution<T> toDistribution(T value, String strategy) {
		if ("spike".equals(strategy)) {
            return buildDistributionFromSingleObservation(value,Double.MAX_VALUE);
        }
        return toDistribution(value);
	}

	public IDistribution<T> toDistribution(T value, Object... params) {
		return toDistribution(value);
	}

    protected IDistribution<T> buildDistributionFromSingleObservation(T value, double wgt) {
        DirichletDistribution<T> dist = new DirichletDistribution<T>();
            dist.getAlphaWeights().put(value,wgt);
            dist.setMass(wgt);
        return dist;
    }







    public IDistribution<T> parse(String distrAsString) {
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
