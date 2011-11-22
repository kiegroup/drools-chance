package org.drools.chance.common.trait;

import org.drools.chance.common.*;
import org.drools.chance.common.fact.Price;
import org.drools.chance.common.fact.Weight;
import org.drools.chance.distribution.fuzzy.linguistic.LinguisticImperfectField;
import org.drools.core.util.TripleStore;
import org.drools.factmodel.traits.TraitProxy;
import org.drools.factmodel.traits.TripleBasedStruct;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.WriteAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ImpBeanLegacyWrapper extends TripleBasedStruct {

    private LegacyBean object;

    private static InternalReadAccessor name_reader;
    private static WriteAccessor name_writer;

    private static InternalReadAccessor weight_reader;
    private static WriteAccessor weight_writer;




    public ImpBeanLegacyWrapper( LegacyBean core, TripleStore store ) {
        super();
        this.object = core;
        this.store = store;

        initSoftFields();

    }


    public Object remove( Object o ) {
        if ( "name".equals( o ) ) {
            String x = object.getName();
            object.setName( null );
            return x;
        }
        if ( "weight".equals( o ) ) {
            Double d = object.getWeight();
            object.setWeight( null );
            return d;
        }

        if ( "flag".equals( o ) ) {
            Boolean x = (Boolean) store.get( propertyKey( "flag" ) ).getValue();   //TODO : manca il getValue
            store.put( property( "flag", null) );                          //TODO : manca il property
            return x;
        }
        if ( "age".equals( o ) ) {
            Integer x = (Integer) store.get( propertyKey( "age" ) ).getValue();   //TODO : manca il getValue
            store.put( property( "age", null) );                          //TODO : manca il property
            return x;
        }
        if ( "body".equals( o ) ) {
            Weight x = (Weight) store.get( propertyKey( "body" ) ).getValue();   //TODO : manca il getValue
            store.put( property( "body", null) );                          //TODO : manca il property
            return x;
        }
        if ( "price".equals( o ) ) {
            Price x = (Price) store.get( propertyKey( "price" ) ).getValue();   //TODO : manca il getValue
            store.put( property( "price", null) );                          //TODO : manca il property
            return x;
        }
        if ( "bucks".equals( o ) ) {
            Integer x = (Integer) store.get( propertyKey( "bucks" ) ).getValue();   //TODO : manca il getValue
            store.put( property( "bucks", null) );                          //TODO : manca il property
            return x;
        }
        if ( "likes".equals( o ) ) {
            ImpBean.Cheese x = (ImpBean.Cheese) store.get( propertyKey( "likes" ) ).getValue();   //TODO : manca il getValue
            store.put( property( "likes", null) );                          //TODO : manca il property
            return x;
        }

        return super.remove( o );
    }





    private void initSoftFields() {
        if ( ! store.contains( propertyKey( "flag" ) ) ) {
            store.put( property( "flag", null ) );
        }
        if ( ! store.contains( propertyKey( "age" ) ) ) {
            store.put( property( "age", null ) );
        }
        if ( ! store.contains( propertyKey( "body" ) ) ) {
            store.put( property( "body", null ) );
        }
        if ( ! store.contains( propertyKey( "price" ) ) ) {
            store.put( property( "price", null ) );
        }
        if ( ! store.contains( propertyKey( "bucks" ) ) ) {
            store.put( property( "bucks", null ) );
        }
        if ( ! store.contains( propertyKey( "likes" ) ) ) {
            store.put( property( "likes", null ) );
        }


        if ( ! store.contains( propertyKey( "name_Dist" ) ) ) {
            IImperfectField fld = new ImperfectField(
                    ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", String.class )
            );
            store.put( property( "name_Dist", fld ) );
        }
        if ( ! store.contains( propertyKey( "flag_Dist" ) ) ) {
            IImperfectField fld = new ImperfectField(
                    ChanceStrategyFactory.buildStrategies( "probability", "discrete", "simple", Boolean.class )
            );
            store.put( property( "flag_Dist", fld ) );
        }
        if ( ! store.contains( propertyKey( "age_Dist" ) ) ) {
            IImperfectField fld = new ImperfectField(
                    ChanceStrategyFactory.buildStrategies( "probability", "dirichlet", "simple", Integer.class )
            );
            store.put( property( "age_Dist", fld ) );
        }
        if ( ! store.contains( propertyKey( "likes_Dist" ) ) ) {
            IImperfectField fld = new ImperfectField(
                    ChanceStrategyFactory.buildStrategies( "probability", "basic", "simple", String.class )
            );
            store.put( property( "likes_Dist", fld ) );
        }


        if ( ! store.contains( propertyKey( "body_Dist" ) ) ) {
            IImperfectField fld = new LinguisticImperfectField(
                    ChanceStrategyFactory.buildStrategies( "fuzzy", "linguistic", "simple", Weight.class ),
                    ChanceStrategyFactory.buildStrategies( "possibility", "linguistic", "simple", Double.class ),
                    0,
                    null
            );
            store.put( property( "body_Dist", fld ) );
        }

        if ( ! store.contains( propertyKey( "price_Dist" ) ) ) {
            IImperfectField fld = new LinguisticImperfectField(
                    ChanceStrategyFactory.buildStrategies( "fuzzy", "linguistic", "simple", Price.class ),
                    ChanceStrategyFactory.buildStrategies( "possibility", "linguistic", "simple", Integer.class ),
                    0,
                    null
            );
            store.put( property( "price_Dist", fld ) );
        }

    }




    public void clear() {
        object.setName( null );
        object.setWeight( null );

        super.clear();

        clearSoftFields();
    }


    private void clearSoftFields() {
        store.put( property( "flag", null ) );
        store.put( property( "age", null ) );
        store.put( property( "body", null ) );
        store.put( property( "price", null ) );
        store.put( property( "bucks", null ) );
        store.put( property( "likes", null ) );
    }




    public boolean containsValue( Object value ) {

        if ( value == null ) {
            if ( object.getName() == null ) {
                return true;
            }
            if ( object.getWeight() == null ) {
                return true;
            }
        }
        //TODO test : what if value is one of the hard values?

        return super.containsValue( value );
    }


    public boolean containsKey( Object key ) {
        if ( "age".equals( key ) ) {
            return true;
        }
        if ( "weight".equals( key ) ) {
            return true;
        }
        return super.containsKey( key );
    }


    public int size() {
        return super.size() + 2;
    }


    public boolean isEmpty() {
        // false because of the hard fields
        return false && super.isEmpty();
    }


    public Object get( Object key ) {
        if ( "name".equals( key ) ) {
            return object.getName();
        }
        if ( "weight".equals( key ) ) {
            return object.getWeight();
        }

        return super.get( key );

    }


    public Object put( String key, Object value ) {
        if ( "name".equals( key ) ) {
            String old = object.getName();
            object.setName((String) value);
            return old;
        }
        if ( "weight".equals( key ) ) {
            Double old = object.getWeight();
            object.setWeight((Double) value);
            return old;
        }

        return super.put(key, value);

    }

    public Set<Entry<String,Object>> entrySet() {
        Set<Entry<String,Object>> set = new HashSet<Entry<String, Object>>();

        set.add( TraitProxy.buildEntry( "name", object.getName() ) );
        set.add( TraitProxy.buildEntry( "weight", object.getWeight() ) );

        set.addAll( super.entrySet() );
        return set;
    }

    public Set<String> keySet() {
        Set<String> set = new HashSet<String>();

        set.add( "name" );
        set.add( "weight" );

        set.addAll( super.keySet() );
        return set;
    }

    public Collection<Object> values() {
        Collection<Object> set = new ArrayList<Object>();

        set.add( object.getName() );
        set.add( object.getWeight() );

        set.addAll( super.values() );
        return set;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.append("[[").append( entrySet() ).append("]]").toString();
    }

    public int hashCode() {
        return getTriplesForSubject( object ).hashCode();
    }










    protected Object getObject() {
        return object;
    }



}
