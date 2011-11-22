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
import org.drools.chance.degree.IDegree;
import org.drools.chance.distribution.IDiscreteProbabilityDistribution;
import org.drools.chance.distribution.IProbabilityDistribution;
import org.drools.chance.utils.ValueSortedMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BasicDistribution<T> implements IProbabilityDistribution<T> {

    private IDegree degree;

    private T value;

	public BasicDistribution() {
		super();
	}


    public BasicDistribution( T value, IDegree degree ) {
        this.value = value;
		this.degree = degree;
	}


    public IDegree getDegree( T value ) {
        if ( value == this.value ) {
            return degree;
        } else {
            return Not.getInstance().eval( degree );
        }
    }

    public T getValue() {
        return value;
    }

    public void set( T value, IDegree degree ) {
        this.value = value;
        this.degree = degree;
    }

    public void clear() {
        set(null, null);
    }

    public void setDegree( IDegree deg ) {
        this.degree = deg;
    }


    public Number domainSize() {
        return 1;
    }


    public String toString() {
        return value + "/" + degree;
    }


}
