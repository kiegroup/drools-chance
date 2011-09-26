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

import org.drools.chance.degree.IDegree;
import org.drools.chance.degree.interval.IntervalDegree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.IDiscreteProbabilityDistribution;
import org.drools.chance.utils.ValueSortedMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Discrete probability distribution
 * TODO
 * @param <T>
 */
public class DirichletDistribution<T> implements IDiscreteProbabilityDistribution<T>  {

    private ValueSortedMap<T,Double> alphaWeightMap = new ValueSortedMap<T,Double>();
    private double mass = 0;


    public IDegree getDegree(T value) {
        return new SimpleDegree(getExpectation(value));
    }

    public Number domainSize(){
        return alphaWeightMap.size();
    }


    public int size() {
        return alphaWeightMap.size();
    }


    DirichletDistribution() { }

    /**
     * inner accessor, to be used by strategies
     * @return the value-weight map
     */
    ValueSortedMap<T,Double> getAlphaWeights() {
        return alphaWeightMap;
    }

    /**
     * inner accessor, to be used by strategies
     * @return the total weight
     */
    double getMass() { return mass; }

    /**
     * inner setter, to be used by strategies
     * @param m the new total weight
     */
    void setMass(double m) { mass = m; }





    public Set<T> getSupport() {
        return alphaWeightMap.keySet();
    }


    public Iterator<T> iterator() {
        return alphaWeightMap.keySet().iterator();
    }




    public double getExpectation(T value) {
        if (! alphaWeightMap.containsKey(value)) return 0;
        return mass > 0 ? (alphaWeightMap.get(value) / mass) : 0;
    }

    public double getVariance(T value) {
        if (! alphaWeightMap.containsKey(value)) return 0;
        double alpha = alphaWeightMap.get(value);
        return mass > 0 ?
                (   (alpha*(mass-alpha)) / (mass*mass*(mass+1)) )
                : 0;
    }

    public double getMode(T value) {
        if (! alphaWeightMap.containsKey(value)) return 0;
        double alpha = alphaWeightMap.get(value);
        return mass > 0 ? ((alpha-1)/(mass-size())) : 0;
    }

    public double getCovariance(T value1, T value2) {
        if (! alphaWeightMap.containsKey(value1) || ! alphaWeightMap.containsKey(value2)) return 0;
        double a1 = alphaWeightMap.get(value1);
        double a2 = alphaWeightMap.get(value2);
        return mass > 0 ? ( -(a1*a2) / (mass*mass*(mass+1)) ) : 0;
    }


    /**
     * @return An ordered simple distribution based on the maximum likelihood principle
     */
    public Map<T, IDegree> getDistribution() {
        ValueSortedMap<T,IDegree> vsMap = new ValueSortedMap<T,IDegree>();
        for (T x : alphaWeightMap.keySet())
            vsMap.put(x,new SimpleDegree(getMode(x)));
        return vsMap;
    }


    public double getLikelihood(IDiscreteProbabilityDistribution<T> testDistribution) {
        Map<T,IDegree> distr = testDistribution.getDistribution();
        double A = -lnGamma(mass);
        Iterator<T> iter = alphaWeightMap.keySet().iterator();
        while (iter.hasNext()) {
            T key = iter.next();
            double alpha = alphaWeightMap.get(key);
            double x = distr.containsKey(key) ?
                    distr.get(key).getValue()
                    : 1.0;
            A = A + lnGamma(alphaWeightMap.get(key)) + (alpha-1)*Math.log(x);
        }
        return Math.exp(A);
    }

    public SimpleDegree getLikelihoodDegree(IDiscreteProbabilityDistribution<T> testDistribution) {
        return new SimpleDegree(getLikelihood(testDistribution));
    }

    public IntervalDegree getLikelihoodRange(Collection<T> values, double threshold) {
        throw new UnsupportedOperationException("TODO");
    }


    public String toString() {
        return "(Dirichlet) : {" + serialize() + "}";
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        Iterator<T> iter = alphaWeightMap.keySet().iterator();
            while (iter.hasNext()) {
                T elem = iter.next();
                sb.append(elem).append("/").append(getDegree(elem).getValue());
                if (iter.hasNext())
                    sb.append(", ");
            }
        return sb.toString();
    }



















    /**
       * Uses Lanczos' approx to compute logGamma(x)
       * @param x
       * @return  lnGamma(x)
       */
      protected static double lnGamma(double x) {
          double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
          double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
                           + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
                           +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
          return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
       }
      /**
       * Uses Lanczos' approx to compute Gamma(x)
       * @param x
       * @return  Gamma(x)
       */
       static double gamma(double x) { return Math.exp(lnGamma(x)); }

       protected static double GAMMA = 0.5772156649015328606065120900824024;
       protected static double LN2 = Math.log(2);
       protected static double Kncoe[] = { .30459198558715155634315638246624251,
           .72037977439182833573548891941219706, -.12454959243861367729528855995001087,
           .27769457331927827002810119567456810e-1, -.67762371439822456447373550186163070e-2,
           .17238755142247705209823876688592170e-2, -.44817699064252933515310345718960928e-3,
           .11793660000155572716272710617753373e-3, -.31253894280980134452125172274246963e-4,
           .83173997012173283398932708991137488e-5, -.22191427643780045431149221890172210e-5,
           .59302266729329346291029599913617915e-6, -.15863051191470655433559920279603632e-6,
           .42459203983193603241777510648681429e-7, -.11369129616951114238848106591780146e-7,
           .304502217295931698401459168423403510e-8, -.81568455080753152802915013641723686e-9,
           .21852324749975455125936715817306383e-9, -.58546491441689515680751900276454407e-10,
           .15686348450871204869813586459513648e-10, -.42029496273143231373796179302482033e-11,
           .11261435719264907097227520956710754e-11, -.30174353636860279765375177200637590e-12,
           .80850955256389526647406571868193768e-13, -.21663779809421233144009565199997351e-13,
           .58047634271339391495076374966835526e-14, -.15553767189204733561108869588173845e-14,
           .41676108598040807753707828039353330e-15, -.11167065064221317094734023242188463e-15 } ;

       /**
        * Digamma function approximation (unverified)
        * Source:
        *     http://arXiv.org/abs/math.CA/0403344
        *     http://www.strw.leidenuniv.nl/~mathar/progs/digamma.c
        * @param x
        * @return diGamma(x)
        */
       double diGamma(double x)
       {
          /* force into the interval 1..3 */
          if(x < 0.0)
              return diGamma(1.0-x)+Math.PI/Math.tan(Math.PI*(1.0-x));    /* reflection formula */
          else if(x < 1.0)
              return diGamma(1.0+x)-1.0/x;
          else if (x == 1.0)
              return -GAMMA;
          else if (x == 2.0)
              return 1.0-GAMMA;
          else if (x == 3.0)
              return 1.5-GAMMA;
          else if (x > 3.0)
              /* duplication formula */
              return 0.5*(diGamma(0.5*x)+diGamma(0.5*(x+1.0)))+LN2;
          else
          {

              double Tn_1 = 1.0 ;  /* T_{n-1}(x), started at n=1 */
              double Tn = x - 2.0 ;  /* T_{n}(x) , started at n=1 */
              double resul = Kncoe[0] + Kncoe[1]*Tn ;

              x -= 2.0;

              for(int n = 2; n < Kncoe.length; n++)
              {
                  double Tn1 = 2.0 * x * Tn - Tn_1 ;  /* Chebyshev recursion, Eq. 22.7.4 Abramowitz-Stegun */
                  resul += Kncoe[n]*Tn1;
                  Tn_1 = Tn;
                  Tn = Tn1;
              }
              return resul;
          }
       }



}
