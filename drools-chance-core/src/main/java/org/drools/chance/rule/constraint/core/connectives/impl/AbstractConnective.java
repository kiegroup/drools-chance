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

package org.drools.chance.rule.constraint.core.connectives.impl;

import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.ImpKind;
import org.drools.chance.distribution.ImpType;
import org.drools.chance.factmodel.Imperfect;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.simple.SimpleDegree;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveFactory;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.ConnectiveType;
import org.drools.core.common.LogicalDependency;
import org.drools.core.factmodel.AnnotationDefinition;


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


    public String toString() {
        return this.getType().toString();
    }


    public static ConnectiveCore buildConnective( AnnotationDescr ann, ConnectiveType lc ) {

        if ( ann != null ) {
            ImpType type = ImpType.parse( (String) ann.getValue( ImpType.name ) );
            ImpKind kind = ImpKind.parse( (String) ann.getValue( ImpKind.name ) );
            DegreeType degree = DegreeType.parse( (String) ann.getValue( DegreeType.name ) );
            MvlFamilies family = MvlFamilies.parse( (String) ann.getValue( MvlFamilies.name ) );
            return buildConnective( lc, type, kind, degree, family );
        } else {
            return buildConnective( lc, null, null, null, null );
        }
    }

    public static ConnectiveCore buildConnective( AnnotationDefinition ann, ConnectiveType lc ) {
        if ( ann != null ) {
            ImpType type = ImpType.parse( (String) ann.getPropertyValue( ImpType.name ) );
            ImpKind kind = ImpKind.parse( (String) ann.getPropertyValue( ImpKind.name ) );
            DegreeType degree = DegreeType.parse( (String) ann.getPropertyValue( DegreeType.name ) );
            MvlFamilies family = MvlFamilies.parse( (String) ann.getPropertyValue( MvlFamilies.name ) );
            return buildConnective( lc, type, kind, degree, family );
        } else {
            return buildConnective( lc, null, null, null, null );
        }
    }

    private static ConnectiveCore buildConnective( ConnectiveType lc, ImpType type, ImpKind kind, DegreeType degree, MvlFamilies family ) {
        ConnectiveFactory factory = ChanceStrategyFactory.getConnectiveFactory( kind, type );
        ConnectiveCore conn;
        switch ( lc ) {
            case INC_AND:
            case AND:       conn = family != null ? factory.getAnd( family.value() ) : factory.getAnd();
                break;
            case INC_OR:
            case OR:        conn = family != null ? factory.getOr( family.value() ) : factory.getOr();
                break;
            case XOR:       conn = family != null ? factory.getXor( family.value() ) : factory.getXor();
                break;
            default:        throw new IllegalStateException( "Unable to find connective for " + lc );
        }
        return conn;
    }

}
