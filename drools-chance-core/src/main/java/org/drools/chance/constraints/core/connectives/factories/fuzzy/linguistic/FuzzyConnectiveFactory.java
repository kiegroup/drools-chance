package org.drools.chance.constraints.core.connectives.factories.fuzzy.linguistic;


import org.drools.chance.constraints.core.connectives.ConnectiveCore;
import org.drools.chance.constraints.core.connectives.ConnectiveFactory;
import org.drools.chance.constraints.core.connectives.impl.LogicConnectives;
import org.drools.chance.constraints.core.connectives.impl.MvlFamilies;
import org.drools.chance.constraints.core.connectives.impl.godel.And;
import org.drools.chance.constraints.core.connectives.impl.godel.Minus;
import org.drools.chance.constraints.core.connectives.impl.godel.Not;
import org.drools.chance.constraints.core.connectives.impl.godel.Or;

import java.util.HashMap;
import java.util.Map;

public class FuzzyConnectiveFactory implements ConnectiveFactory {

    private static Map<String, Class<?>> knownOperatorClasses;

    protected static void addKnownClass( Class<?> k ) {
        knownOperatorClasses.put( k.getName(), k );
    }

    static {
        knownOperatorClasses = new HashMap<String, Class<?>>();

        addKnownClass( org.drools.chance.constraints.core.connectives.impl.godel.And.class );
        addKnownClass( org.drools.chance.constraints.core.connectives.impl.godel.Or.class );
        addKnownClass( org.drools.chance.constraints.core.connectives.impl.godel.Not.class );

        addKnownClass( org.drools.chance.constraints.core.connectives.impl.lukas.And.class );
        addKnownClass( org.drools.chance.constraints.core.connectives.impl.lukas.Or.class );
        addKnownClass( org.drools.chance.constraints.core.connectives.impl.lukas.Not.class );
        addKnownClass( org.drools.chance.constraints.core.connectives.impl.lukas.Xor.class );
        addKnownClass( org.drools.chance.constraints.core.connectives.impl.lukas.Equiv.class );
        addKnownClass( org.drools.chance.constraints.core.connectives.impl.lukas.Implies.class );

        addKnownClass( org.drools.chance.constraints.core.connectives.impl.product.And.class );
        addKnownClass( org.drools.chance.constraints.core.connectives.impl.product.Or.class );
        addKnownClass( org.drools.chance.constraints.core.connectives.impl.product.Not.class );

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
        return And.getInstance();
    }

    public ConnectiveCore getAnd( String type ) {
        return getAnd( type, null );
    }

    public ConnectiveCore getAnd( String type, Object... params ) {
        MvlFamilies family = MvlFamilies.valueOf( type );
        switch ( family ) {
            case GODEL:     return org.drools.chance.constraints.core.connectives.impl.godel.And.getInstance();
            case LUKAS:     return org.drools.chance.constraints.core.connectives.impl.lukas.And.getInstance();
            case PRODUCT:   return org.drools.chance.constraints.core.connectives.impl.product.And.getInstance();
            default:        return And.getInstance();
        }
    }

    public ConnectiveCore getOr() {
        return Or.getInstance();
    }

    public ConnectiveCore getOr( String type ) {
        return getOr( type, null );
    }

    public ConnectiveCore getOr( String type, Object... params ) {
        MvlFamilies family = MvlFamilies.valueOf(type);
        switch ( family ) {
            case GODEL:     return org.drools.chance.constraints.core.connectives.impl.godel.Or.getInstance();
            case LUKAS:     return org.drools.chance.constraints.core.connectives.impl.lukas.Or.getInstance();
            case PRODUCT:   return org.drools.chance.constraints.core.connectives.impl.product.Or.getInstance();
            default:        return Or.getInstance();
        }
    }

    public ConnectiveCore getNot() {
        return Not.getInstance();
    }

    public ConnectiveCore getNot( String type ) {
        return getNot( type, null );
    }

    public ConnectiveCore getNot( String type, Object... params ) {
        MvlFamilies family = MvlFamilies.valueOf(type);
        switch ( family ) {
            case GODEL:     return org.drools.chance.constraints.core.connectives.impl.godel.Not.getInstance();
            case LUKAS:     return org.drools.chance.constraints.core.connectives.impl.lukas.Not.getInstance();
            case PRODUCT:   return org.drools.chance.constraints.core.connectives.impl.product.Not.getInstance();
            default:        return Not.getInstance();
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
            case GODEL:     return org.drools.chance.constraints.core.connectives.impl.godel.Minus.getInstance();
            case LUKAS:     return org.drools.chance.constraints.core.connectives.impl.lukas.Minus.getInstance();
            case PRODUCT:   return org.drools.chance.constraints.core.connectives.impl.product.Minus.getInstance();
            default:        return Minus.getInstance();
        }
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
