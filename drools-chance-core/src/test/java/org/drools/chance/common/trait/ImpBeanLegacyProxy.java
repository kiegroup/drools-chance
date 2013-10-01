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

package org.drools.chance.common.trait;


import org.drools.chance.common.ImperfectField;
import org.drools.chance.common.fact.Price;
import org.drools.chance.common.fact.Weight;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.fuzzy.linguistic.LinguisticImperfectField;
import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleFactoryImpl;
import org.drools.core.util.TripleStore;
import org.drools.factmodel.traits.TraitProxy;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.factmodel.traits.TripleBasedBean;
import org.drools.factmodel.traits.TripleBasedTypes;

public class ImpBeanLegacyProxy extends TraitProxy implements ImpBean {


    private LegacyBean object;

    private TripleStore store;
    

    public ImpBeanLegacyProxy( LegacyBean x, TripleStore y, TripleFactory tf ) {
        object = x;
        store = y;

        setTripleFactory( tf );
        setFields( new ImpBeanLegacyWrapper( x, y, tf ) );
        
        x._setDynamicProperties( new TripleBasedBean( x, y, tf ) );

        x._setTraitMap( new TripleBasedTypes( x, y, tf ) );

        synchFields();

    }


    @Override
    public String getTraitName() {
        return ImpBean.class.getName();
    }

    public TraitableBean getObject() {
        return object;
    }

    public LegacyBean getCore() {
        return object;
    }




    private void synchFields() {

        String coreName = getNameCore();
        ImperfectField<String> name = getName();
        if ( coreName != null ) {
            name.setValue( coreName, false );
        }
        setNameCore( name.getCrisp() );

        Boolean coreFlag = getFlagCore();
        ImperfectField<Boolean> flag = getFlag();
        if ( coreFlag != null ) {
            flag.setValue( coreFlag, false );
        }
        setFlagCore( flag.getCrisp() );

        Integer coreAge = getAgeCore();
        ImperfectField<Integer> age = getAge();
        if ( coreAge != null ) {
            age.setValue( coreAge, false );
        }
        setAgeCore( age.getCrisp() );

        Cheese coreLikes = getLikesCore();
        ImperfectField<Cheese> likes = getLikes();
        if ( coreLikes != null ) {
            likes.setValue( coreLikes, false );
        }
        setLikesCore( likes.getCrisp() );



        LinguisticImperfectField<Weight,Double> bodyImp = (LinguisticImperfectField<Weight,Double>) getBody();
        Double wgt = getWeight();
        if ( wgt != null ) {

            Distribution dist = bodyImp.fuzzify(wgt);
            bodyImp.setValue(dist, false);

            setBodyCore( bodyImp.getCrisp() );

        } else {
            Weight bodyVal = getBodyValue();
            if ( bodyVal != null )
                bodyImp.setValue( bodyVal );
            if ( bodyImp != null ) {
                setBodyCore( bodyImp.getCrisp() );
                setWeightCore( ( bodyImp ).defuzzify().doubleValue() );
            }
        }


        LinguisticImperfectField<Price,Integer> priceImp = (LinguisticImperfectField<Price,Integer>) getPrice();
        Integer pri = getBucks();
        if ( pri != null ) {

            Distribution dist = priceImp.fuzzify(pri);
            priceImp.setValue(dist, false);

            setPriceCore( priceImp.getCrisp() );

        } else {
            Price priceVal = getPriceValue();
            if ( priceVal != null )
                priceImp.setValue( priceVal );
            if ( priceImp != null ) {
                setPriceCore( priceImp.getCrisp() );
                setBucksCore( ( priceImp.defuzzify() ).intValue() );
            }
        }



    }










    public ImperfectField<String> getName() {
        return (ImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
    }

    public Distribution<String> getNameDistr() {
        return ((ImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue()).getCurrent();
    }

    public String getNameValue() {
        return getCore().getName();
    }

    public String getNameCore() {
        return getCore().getName();
    }

    public void setName(ImperfectField<String> x) {
        store.put( property( "name_$$Imp", x ) );
        getCore().setName( x.getCrisp() );
    }

    public void setNameDistr(Distribution<String> x) {
        ImperfectField<String> fld = (ImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        getCore().setName( fld.getCrisp() );
    }

    public void setNameValue(String x) {
        ImperfectField<String> fld = (ImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        getCore().setName( fld.getCrisp() );
    }

    public void setNameCore( String x ) {
        getCore().setName(x);
    }




    public void updateName(ImperfectField<String> x) {
        ImperfectField<String> fld = (ImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        getCore().setName( fld.getCrisp() );
    }

    public void updateNameDistr(Distribution<String> x) {
        ImperfectField<String> fld = (ImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.update( x );
        getCore().setName( fld.getCrisp() );
    }

    public void updateNameValue(String x) {
        ImperfectField<String> fld = (ImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.update( x );
        getCore().setName( fld.getCrisp() );
    }









    public ImperfectField<Boolean> getFlag() {
        return (ImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
    }

    public Distribution<Boolean> getFlagDistr() {
        return ((ImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue()).getCurrent();
    }

    public Boolean getFlagValue() {
        Triple t = store.get( propertyKey( "flag") );
        if ( t == null ) {
            return null;
        } else {
            return (Boolean) t.getValue();
        }
    }

    protected Boolean getFlagCore() {
        return (Boolean) store.get( propertyKey( "flag") ).getValue();
    }


    public void setFlag(ImperfectField<Boolean> x) {
        store.put( property( "flag_$$Imp", x ) );
        store.put( property( "flag", x.getCrisp() ) );
    }

    public void setFlagDistr(Distribution<Boolean> x) {
        ImperfectField<Boolean> fld = (ImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void setFlagValue(Boolean x) {
        ImperfectField<Boolean> fld = (ImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void setFlagCore( Boolean x ) {
        store.put( property( "flag", x ) );
    }

    public void updateFlag(ImperfectField<Boolean> x) {
        ImperfectField<Boolean> fld = (ImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void updateFlagDistr(Distribution<Boolean> x) {
        ImperfectField<Boolean> fld = (ImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void updateFlagValue(Boolean x) {
        ImperfectField<Boolean> fld = (ImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "flag", fld.getCrisp() ) );
    }






    public ImperfectField<Integer> getAge() {
        return (ImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
    }

    public Distribution<Integer> getAgeDistr() {
        return ((ImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue()).getCurrent();
    }

    public Integer getAgeValue() {
        Triple t = store.get( propertyKey( "age") );
        if ( t == null ) {
            return null;
        } else {
            return (Integer) t.getValue();
        }
    }

    protected Integer getAgeCore() {
        return (Integer) store.get( propertyKey( "age") ).getValue();
    }

    public void setAge(ImperfectField<Integer> x) {
        store.put( property( "age_$$Imp", x ) );
        store.put( property( "age", x.getCrisp() ) );
    }

    public void setAgeDistr(Distribution<Integer> x) {
        ImperfectField<Integer> fld = (ImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();;
        fld.setValue( x, false );
        store.put( property( "age", fld.getCrisp() ) );
    }

    public void setAgeValue(Integer x) {
        ImperfectField<Integer> fld = (ImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "age", x ) );
    }

    public void setAgeCore( Integer x ) {
        store.put( property( "age", x ) );
    }

    public void updateAgeDistr(Distribution<Integer> x) {
        ImperfectField<Integer> fld = (ImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "age", fld.getCrisp() ) );
    }

    public void updateAgeValue(Integer x) {
        ImperfectField<Integer> fld = (ImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "age", fld.getCrisp() ) );
    }

    public void updateAge(ImperfectField<Integer> x) {
        ImperfectField<Integer> fld = (ImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        store.put( property( "age", fld.getCrisp() ) );
    }





    public ImperfectField<Weight> getBody() {
        return (ImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
    }

    public Distribution<Weight> getBodyDistr() {
        return ((ImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue()).getCurrent();
    }

    public Weight getBodyValue() {
        Triple t = store.get( propertyKey( "body" ) );
        if ( t == null ) {
            return null;
        } else {
            return (Weight) t.getValue();
        }
    }

    protected Weight getBodyCore() {
        return (Weight) store.get( propertyKey( "body") ).getValue();
    }





    public void setBody(ImperfectField<Weight> x) {
        store.put( property( "body_$$Imp", x ) );
        store.put( property( "body", x.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue());
    }

    public void setBodyDistr(Distribution<Weight> x) {
        ImperfectField<Weight> fld = (ImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "body", fld.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue() );
    }

    public void setBodyValue(Weight x) {
        ImperfectField<Weight> fld = (ImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "body", fld.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue());
    }

    public void setBodyCore( Weight x ) {
        store.put( property( "body", x ) );
    }

    public void updateBodyDistr(Distribution<Weight> x) {
        ImperfectField<Weight> fld = (ImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "body", fld.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue() );
    }

    public void updateBodyValue(Weight x) {
        ImperfectField<Weight> fld = (ImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "body", fld.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue() );
    }

    public void updateBody(ImperfectField<Weight> x) {
        ImperfectField<Weight> fld = (ImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        store.put( property( "body", fld.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue() );
    }




    public Double getWeightCore() {
        return getCore().getWeight();
    }

    public void setWeightCore( Double x ) {
        getCore().setWeight( x );
    }


    public Double getWeight() {
        System.out.println( getCore() );
        return getWeightCore();
    }

    public void setWeight(Double w) {
        LinguisticImperfectField<Weight,Double> bodyImp = (LinguisticImperfectField<Weight, Double>) getBody();

        bodyImp.setValue(  bodyImp.fuzzify(w), false );
        setBodyCore( bodyImp.getCrisp() );

        setWeightCore( w );
    }





    public ImperfectField<Price> getPrice() {
        return (ImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
    }

    public Distribution<Price> getPriceDistr() {
        return ((ImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue()).getCurrent();
    }

    public Price getPriceValue() {
        Triple t = store.get( propertyKey( "price" ) );
        if ( t == null ) {
            return null;
        } else {
            return (Price) t.getValue();
        }
    }

    protected Price getPriceCore() {
        return (Price) store.get( propertyKey( "price" ) ).getValue();
    }

    public void setPrice(ImperfectField<Price> x) {
        store.put( property( "price_$$Imp", x ) );
        store.put( property( "price", x.getCrisp() ) );

        setBucksCore(((LinguisticImperfectField<Price, Integer>) getPrice()).defuzzify().intValue() );
    }

    public void setPriceDistr(Distribution<Price> x) {
        ImperfectField<Price> fld = (ImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "price", fld.getCrisp() ) );

        setBucksCore(((LinguisticImperfectField<Price, Integer>) getPrice()).defuzzify().intValue() );
    }

    public void setPriceValue(Price x) {
        ImperfectField<Price> fld = (ImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "price", fld.getCrisp() ) );

        setBucksCore( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify().intValue()  );
    }

    public void setPriceCore( Price x ) {
        store.put( property( "price", x ) );
    }

    public void updatePrice(ImperfectField<Price> x) {
        ImperfectField<Price> fld = (ImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        store.put( property( "price", fld.getCrisp() ) );

        setBucksCore( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify().intValue() );
    }

    public void updatePriceDistr(Distribution<Price> x) {
        ImperfectField<Price> fld = (ImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "price", fld.getCrisp() ) );

        setBucksCore( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify().intValue() );
    }

    public void updatePriceValue(Price x) {
        ImperfectField<Price> fld = (ImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "price", fld.getCrisp() ) );

        setBucksCore( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify().intValue() );
    }



    public Integer getBucksCore() {
        return (Integer) (store.get( propertyKey( "bucks" ) ) ).getValue();
    }

    public void setBucksCore( Integer x ) {
        store.put( property( "bucks", x ) );
    }

    public Integer getBucks() {
        return getBucksCore();
    }

    public void setBucks(Integer x) {
        LinguisticImperfectField<Price,Integer> priceImp = (LinguisticImperfectField<Price, Integer>) getPrice();

        priceImp.setValue( priceImp.fuzzify( x ), false );
        setPriceCore( priceImp.getCrisp() );

        setBucksCore( x );
    }






    public ImperfectField<Cheese> getLikes() {
        return (ImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
    }

    public Distribution<Cheese> getLikesDistr() {
        return ((ImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue()).getCurrent();
    }

    public Cheese getLikesValue() {
        Triple t = store.get( propertyKey( "likes") );
        if ( t == null ) {
            return null;
        } else {
            return (Cheese) t.getValue();
        }
    }

    protected Cheese getLikesCore() {
        return (Cheese) store.get( propertyKey( "likes") ).getValue();
    }

    public void setLikes(ImperfectField<Cheese> x) {
        store.put( property( "likes_$$Imp", x ) );
        store.put( property( "likes", x.getCrisp() ) );
    }

    public void setLikesDistr(Distribution<Cheese> x) {
        ImperfectField<Cheese> fld = (ImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void setLikesValue(Cheese x) {
        ImperfectField<Cheese> fld = (ImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void setLikesCore( Cheese x ) {
        store.put( property( "likes", x ) );
    }

    public void updateLikesDistr(Distribution<Cheese> x) {
        ImperfectField<Cheese> fld = (ImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void updateLikesValue(Cheese x) {
        ImperfectField<Cheese> fld = (ImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void updateLikes(ImperfectField<Cheese> x ) {
        ImperfectField<Cheese> fld = (ImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        store.put( property( "likes", fld.getCrisp() ) );
    }





    //TODO
//    boolean hasKeys = false;
//
//        if ( ! hasKeys ) {
//            buildEqualityMethods( cw, masterName, core.getClassName() );
//        } else {
//            buildKeyedEqualityMethods( cw, trait, masterName, core.getClassName() );
//        }
//
//
//
//
//        buildExtendedMethods( cw, trait, core );


    public String toString() {
        return getFields().entrySet().toString();
    }

}
