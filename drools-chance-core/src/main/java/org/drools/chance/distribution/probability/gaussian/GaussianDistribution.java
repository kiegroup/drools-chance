package org.drools.chance.distribution.probability.gaussian;

import org.drools.chance.degree.Degree;
import org.drools.chance.distribution.ContinuousProbabilityDistribution;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class GaussianDistribution<T extends Number> implements ContinuousProbabilityDistribution<T> {

    private Degree falze;
    private Double mu;
    private Double sigma;

    public GaussianDistribution( Double mean, Double stdev, Degree f ) {
        mu = mean;
        sigma = stdev;
        falze = f;
    }

    public Double getMu() {
        return mu;
    }

    public void setMu( Double mu ) {
        this.mu = mu;
    }

    public Double getSigma() {
        return sigma;
    }

    public void setSigma( Double sigma ) {
        this.sigma = sigma;
    }

    public Set<T> getSupport() {
        return new RealNumberSet();
    }

    public Degree getDegree( T value ) {
        return falze;
    }

    public Degree get( T object ) {
        return falze;
    }

    public Number domainSize() {
        return Double.POSITIVE_INFINITY;
    }

    public boolean isDiscrete() {
        return false;
    }

    public boolean isNormalized() {
        return true;
    }

    public void setNormalized( boolean normalized ) {

    }

    public int size() {
        return Integer.MAX_VALUE;
    }

    public Iterator<T> iterator() {
        return Collections.EMPTY_SET.iterator();
    }

    public Degree getCumulative( T object ) {
        double d = object.doubleValue();
        return falze.fromConst( Phi( d, mu, sigma ) );
    }

    public static double phi( double x ) {
        return Math.exp( -x * x / 2 ) / Math.sqrt( 2 * Math.PI );
    }

    public static double Phi( double x ) {
        if ( x < -8.0 ) {
            return 0.0;
        }
        if ( x >  8.0 ) {
            return 1.0;
        }
        double sum = 0.0;
        double term = x;
        for ( int i = 3; sum + term != sum; i += 2 ) {
            sum  = sum + term;
            term = term * x * x / i;
        }
        return 0.5 + sum * phi( x );
    }

    public static double Phi( double x, double mu, double sigma ) {
        return Phi( ( x - mu ) / sigma );
    }
}
