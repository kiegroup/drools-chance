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

package org.drools.chance.distribution;

import org.drools.chance.constraints.core.connectives.impl.lukas.Not;
import org.drools.chance.degree.Degree;

public class BasicDistribution<T> implements ProbabilityDistribution<T> {

    private Degree degree;

    private T value;

    public BasicDistribution() {
        super();
    }


    public BasicDistribution( T value, Degree degree ) {
        this.value = value;
        this.degree = degree;
    }


    public Degree getDegree( T value ) {
        if ( this.value == null ) {
            return degree.Unknown();
        } else if ( value.equals( this.value ) ) {
            // there was a == here, was it intentional?
            return degree;
        } else {
            return Not.getInstance().eval( degree );
        }
    }

    public Degree get(T value) {
        return getDegree( value );
    }

    public T getValue() {
        return value;
    }

    public void set( T value, Degree degree ) {
        this.value = value;
        this.degree = degree;
    }

    public void clear() {
        set(null, null);
    }

    public void setDegree( Degree deg ) {
        this.degree = deg;
    }


    public Number domainSize() {
        return 1;
    }


    public String toString() {
        return "(Basic) : {" + value + "/" + degree + "}";
    }

}
