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

package org.drools.chance.distribution.probability.gaussian;

import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.distribution.DiscreteProbabilityDistribution;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.DistributionStrategies;
import org.drools.chance.distribution.probability.discrete.DiscreteDistribution;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * Strategy and level III factory for discrete probability distributions
 */
public class GaussianDistributionStrategy implements DistributionStrategies<Double> {



    private DegreeType degreeType;
    private Class<Double> domainType;

    private Constructor degreeStringConstr = null;



    GaussianDistributionStrategy( DegreeType degreeType, Class<Double> domainType ){
        this.degreeType = degreeType;
        this.domainType = domainType;
    }


    private Constructor getDegreeStringConstructor() {
        if (degreeStringConstr == null) {
            degreeStringConstr = ChanceDegreeTypeRegistry.getSingleInstance().getConstructorByString(degreeType);
        }
        return degreeStringConstr;
    }


    public Distribution<Double> toDistribution( Double value ) {
        return new GaussianDistribution( value.doubleValue(), 1.0, ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 0.0 ) );
    }

    public Distribution<Double> toDistribution( Double value, String strategy ) {
        return new GaussianDistribution( value.doubleValue(), 1.0, ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 0.0 ) );
    }

    public Distribution<Double> toDistribution( Double value, Object... params ) {
        return new GaussianDistribution( value.doubleValue(),
                Double.valueOf( params[ 0 ].toString() ),
                ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 0.0 ) );
    }

    public Distribution<Double> parse( String distrAsString ) {
        if ( distrAsString.startsWith( "N" ) ) {
            StringTokenizer tok = new StringTokenizer( distrAsString.substring( 1 ), "(,) " );
            return new GaussianDistribution( tok.hasMoreTokens() ? Double.valueOf( tok.nextToken() ) : 0.0,
                    tok.hasMoreTokens() ? Double.valueOf( tok.nextToken() ) : 1.0,
                    ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 0.0 ) );
        } else {
            return newDistribution();
        }
    }

    public Distribution<Double> newDistribution() {
        return new GaussianDistribution( 0.0, 1.0, ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 0.0 ) );
    }

    public Distribution<Double> newDistribution( Set<Double> focalElements ) {
        Iterator<Double> iter = focalElements.iterator();
        return new GaussianDistribution( iter.next().doubleValue(), iter.next().doubleValue(), ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degreeType, 0.0 ) );
    }

    public Distribution<Double> newDistribution( Map<? extends Double, ? extends Degree> elements ) {
        throw new UnsupportedOperationException( "Build Gaussian with map " );
    }

    public Double toCrispValue( Distribution<Double> dist ) {
        return ((GaussianDistribution) dist).getMu();
    }

    public Double toCrispValue( Distribution<Double> dist, String strategy ) {
        return ((GaussianDistribution) dist).getMu();
    }

    public Double toCrispValue( Distribution<Double> dist, Object... params ) {
        return ((GaussianDistribution) dist).getMu();
    }



    public Double sample( Distribution<Double> dist ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Double sample( Distribution<Double> dist, String strategy ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Double sample( Distribution<Double> dist, Object... params ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> merge( Distribution<Double> current, Distribution<Double> newBit ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> merge( Distribution<Double> current, Distribution<Double> newBit, String strategy ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> merge( Distribution<Double> current, Distribution<Double> newBit, Object... params ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> mergeAsNew( Distribution<Double> current, Distribution<Double> newBit ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> mergeAsNew( Distribution<Double> current, Distribution<Double> newBit, String strategy ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> mergeAsNew( Distribution<Double> current, Distribution<Double> newBit, Object... params ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> remove( Distribution<Double> current, Distribution<Double> newBit ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> remove( Distribution<Double> current, Distribution<Double> newBit, String strategy ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> remove( Distribution<Double> current, Distribution<Double> newBit, Object... params ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> removeAsNew( Distribution<Double> current, Distribution<Double> newBit ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> removeAsNew( Distribution<Double> current, Distribution<Double> newBit, String strategy ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Distribution<Double> removeAsNew( Distribution<Double> current, Distribution<Double> newBit, Object... params ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void normalize( Distribution<Double> distr ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
