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

package org.drools.chance.rule.constraint.core.connectives.factories.probability.discrete;

import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveFactory;
import org.drools.chance.rule.constraint.core.connectives.impl.LogicConnectives;
import org.drools.chance.rule.constraint.core.connectives.impl.MvlFamilies;
import org.drools.chance.rule.constraint.core.connectives.impl.godel.And;
import org.drools.chance.rule.constraint.core.connectives.impl.lukas.Xor;
import org.drools.chance.rule.constraint.core.connectives.impl.product.Minus;
import org.drools.chance.rule.constraint.core.connectives.impl.lukas.Equiv;
import org.drools.chance.rule.constraint.core.connectives.impl.lukas.Implies;
import org.drools.chance.rule.constraint.core.connectives.impl.lukas.Or;
import org.drools.chance.rule.constraint.core.connectives.impl.product.Not;

import java.util.HashMap;
import java.util.Map;


public class DiscreteProbabilityConnectiveFactory implements ConnectiveFactory {


    private static Map<String, Class<?>> knownOperatorClasses;

    protected static void addKnownClass( Class<?> k ) {
        knownOperatorClasses.put( k.getName(), k );
    }

    static {
        knownOperatorClasses = new HashMap<String, Class<?>>();

        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.godel.And.class );
        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.godel.Or.class );
        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.godel.Not.class );

        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.lukas.And.class );
        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.lukas.Or.class );
        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.lukas.Not.class );
        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.lukas.Xor.class );
        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.lukas.Equiv.class );
        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.lukas.Implies.class );

        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.product.And.class );
        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.product.Or.class );
        addKnownClass( org.drools.chance.rule.constraint.core.connectives.impl.product.Not.class );

    }


    public Map<String, Class<?>> getKnownOperatorClasses() {
        return knownOperatorClasses;
    }

    public ConnectiveCore getConnective(LogicConnectives conn, String type, Object... params) {
        switch ( conn ) {
            case AND: return getAnd( type, params );
            case OR : return getOr( type, params );
            case EQ : return getEquiv( type, params );
            case NOT: return getNot( type, params );
            case XOR: return getXor( type, params );
            case IMPL:return getImplies( type, params );
            default : return getAnd();
        }
    }

    public ConnectiveCore getAnd() {
        return org.drools.chance.rule.constraint.core.connectives.impl.product.And.getInstance();
    }

    public ConnectiveCore getAnd(String type) {
        MvlFamilies family = MvlFamilies.valueOf(type);
        switch ( family ) {
            case GODEL:     return org.drools.chance.rule.constraint.core.connectives.impl.godel.And.getInstance();
            case LUKAS:     return org.drools.chance.rule.constraint.core.connectives.impl.lukas.And.getInstance();
            case PRODUCT:   return org.drools.chance.rule.constraint.core.connectives.impl.product.And.getInstance();
            default:        return org.drools.chance.rule.constraint.core.connectives.impl.product.And.getInstance();
        }
    }

    public ConnectiveCore getAnd(String type, Object... params) {
        return getAnd(type);
    }

    public ConnectiveCore getOr() {
        return org.drools.chance.rule.constraint.core.connectives.impl.product.Or.getInstance();
    }

    public ConnectiveCore getOr(String type) {
        MvlFamilies family = MvlFamilies.valueOf(type);
        switch ( family ) {
            case GODEL:     return org.drools.chance.rule.constraint.core.connectives.impl.godel.Or.getInstance();
            case LUKAS:     return org.drools.chance.rule.constraint.core.connectives.impl.lukas.Or.getInstance();
            case PRODUCT:   return org.drools.chance.rule.constraint.core.connectives.impl.product.Or.getInstance();
            default:        return org.drools.chance.rule.constraint.core.connectives.impl.product.Or.getInstance();
        }
    }


    public ConnectiveCore getMinus() {
        return Minus.getInstance();
    }

    public ConnectiveCore getMinus( String type ) {
        return getMinus( type, null );
    }

    public ConnectiveCore getMinus( String type, Object... params ) {
        MvlFamilies family = MvlFamilies.valueOf(type);
        switch ( family ) {
            case GODEL:     return org.drools.chance.rule.constraint.core.connectives.impl.godel.Minus.getInstance();
            case LUKAS:     return org.drools.chance.rule.constraint.core.connectives.impl.lukas.Minus.getInstance();
            case PRODUCT:   return org.drools.chance.rule.constraint.core.connectives.impl.product.Minus.getInstance();
            default:        return org.drools.chance.rule.constraint.core.connectives.impl.product.Minus.getInstance();
        }
    }

    public ConnectiveCore getOr(String type, Object... params) {
        return getOr(type);
    }

    public ConnectiveCore getNot() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getNot(String type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getNot(String type, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getXor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getXor(String type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getXor(String type, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getEquiv() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getEquiv(String type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getEquiv(String type, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getImplies() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getImplies(String type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConnectiveCore getImplies(String type, Object... params) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
