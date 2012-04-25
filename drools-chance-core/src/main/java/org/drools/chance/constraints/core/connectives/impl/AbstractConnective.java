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

package org.drools.chance.constraints.core.connectives.impl;

import org.drools.chance.constraints.core.connectives.ConnectiveCore;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;


public abstract class AbstractConnective implements ConnectiveCore {

    public abstract LogicConnectives getType();

    public abstract Degree eval(Degree deg);
    public abstract Degree eval(Degree left, Degree right);
    public abstract Degree eval(Degree... degs);

    public abstract boolean isUnary();
    public abstract boolean isBinary();
    public abstract boolean isNary();


    public Degree eval(Object left,Object right) {
        return eval( validate( left ), validate( right ) );
    }

    public Degree eval(Object obj) {
        return eval( validate( obj ) );
    }

    public Degree eval(Object... objs) {
        Degree[] args = new Degree[objs.length];
        for ( int j = 0; j < objs.length; j++ ) {
            args[j] = validate( objs[j] );
        }
        return eval( args );
    }

    
    protected Degree validate( Object o ) {
        if ( o instanceof Degree ) {
            return (Degree) o;
        }
        if ( Boolean.class == o.getClass() ) {
            return SimpleDegree.fromBooleanLiteral( (Boolean) o );
        }
        
        throw new UnsupportedOperationException("Trying to use " + o.getClass() + " as Degree");
    }



}
