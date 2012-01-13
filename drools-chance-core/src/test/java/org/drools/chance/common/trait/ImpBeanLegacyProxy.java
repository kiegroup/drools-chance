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


import org.drools.chance.common.IImperfectField;
import org.drools.chance.common.fact.Price;
import org.drools.chance.common.fact.Weight;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.fuzzy.linguistic.LinguisticImperfectField;
import org.drools.core.util.Triple;
import org.drools.core.util.TripleStore;
import org.drools.factmodel.traits.TraitProxy;
import org.drools.factmodel.traits.TripleBasedBean;
import org.drools.factmodel.traits.TripleBasedTypes;

public class ImpBeanLegacyProxy extends TraitProxy implements ImpBean {


    private LegacyBean object;

    private TripleStore store;



    public ImpBeanLegacyProxy( LegacyBean x, TripleStore y ) {
        object = x;
        store = y;


        setFields( new ImpBeanLegacyWrapper( x, y ) );

        x.setDynamicProperties( new TripleBasedBean( x,y ) );

        x.setTraitMap( new TripleBasedTypes( x, y ) );

        synchFields();

    }


    public Object getObject() {
        return object;
    }

    public LegacyBean getCore() {
        return object;
    }




    private void synchFields() {

        String coreName = getNameCore();
        IImperfectField<String> name = getName();
        if ( coreName != null ) {
            name.setValue( coreName, false );
        }
        setNameCore( name.getCrisp() );

        Boolean coreFlag = getFlagCore();
        IImperfectField<Boolean> flag = getFlag();
        if ( coreFlag != null ) {
            flag.setValue( coreFlag, false );
        }
        setFlagCore( flag.getCrisp() );

        Integer coreAge = getAgeCore();
        IImperfectField<Integer> age = getAge();
        if ( coreAge != null ) {
            age.setValue( coreAge, false );
        }
        setAgeCore( age.getCrisp() );

        Cheese coreLikes = getLikesCore();
        IImperfectField<Cheese> likes = getLikes();
        if ( coreLikes != null ) {
            likes.setValue( coreLikes, false );
        }
        setLikesCore( likes.getCrisp() );



        LinguisticImperfectField<Weight,Double> bodyImp = (LinguisticImperfectField<Weight,Double>) getBody();
        Double wgt = getWeight();
        if ( wgt != null ) {

            IDistribution dist = bodyImp.fuzzify(wgt);
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

            IDistribution dist = priceImp.fuzzify(pri);
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










    public IImperfectField<String> getName() {
        return (IImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
    }

    public IDistribution<String> getNameDistr() {
        return ((IImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue()).getCurrent();
    }

    public String getNameValue() {
        return getCore().getName();
    }

    public String getNameCore() {
        return getCore().getName();
    }

    public void setName(IImperfectField<String> x) {
        store.put( property( "name_$$Imp", x ) );
        getCore().setName( x.getCrisp() );
    }

    public void setNameDistr(IDistribution<String> x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        getCore().setName( fld.getCrisp() );
    }

    public void setNameValue(String x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        getCore().setName( fld.getCrisp() );
    }

    public void setNameCore( String x ) {
        getCore().setName(x);
    }




    public void updateName(IImperfectField<String> x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        getCore().setName( fld.getCrisp() );
    }

    public void updateNameDistr(IDistribution<String> x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.update( x );
        getCore().setName( fld.getCrisp() );
    }

    public void updateNameValue(String x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_$$Imp" ) ).getValue();
        fld.update( x );
        getCore().setName( fld.getCrisp() );
    }









    public IImperfectField<Boolean> getFlag() {
        return (IImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
    }

    public IDistribution<Boolean> getFlagDistr() {
        return ((IImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue()).getCurrent();
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


    public void setFlag(IImperfectField<Boolean> x) {
        store.put( property( "flag_$$Imp", x ) );
        store.put( property( "flag", x.getCrisp() ) );
    }

    public void setFlagDistr(IDistribution<Boolean> x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void setFlagValue(Boolean x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void setFlagCore( Boolean x ) {
        store.put( property( "flag", x ) );
    }

    public void updateFlag(IImperfectField<Boolean> x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void updateFlagDistr(IDistribution<Boolean> x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void updateFlagValue(Boolean x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "flag", fld.getCrisp() ) );
    }






    public IImperfectField<Integer> getAge() {
        return (IImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
    }

    public IDistribution<Integer> getAgeDistr() {
        return ((IImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue()).getCurrent();
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

    public void setAge(IImperfectField<Integer> x) {
        store.put( property( "age_$$Imp", x ) );
        store.put( property( "age", x.getCrisp() ) );
    }

    public void setAgeDistr(IDistribution<Integer> x) {
        IImperfectField<Integer> fld = (IImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();;
        fld.setValue( x, false );
        store.put( property( "age", fld.getCrisp() ) );
    }

    public void setAgeValue(Integer x) {
        IImperfectField<Integer> fld = (IImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "age", x ) );
    }

    public void setAgeCore( Integer x ) {
        store.put( property( "age", x ) );
    }

    public void updateAgeDistr(IDistribution<Integer> x) {
        IImperfectField<Integer> fld = (IImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "age", fld.getCrisp() ) );
    }

    public void updateAgeValue(Integer x) {
        IImperfectField<Integer> fld = (IImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "age", fld.getCrisp() ) );
    }

    public void updateAge(IImperfectField<Integer> x) {
        IImperfectField<Integer> fld = (IImperfectField<Integer>) store.get( propertyKey( "age_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        store.put( property( "age", fld.getCrisp() ) );
    }





    public IImperfectField<Weight> getBody() {
        return (IImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
    }

    public IDistribution<Weight> getBodyDistr() {
        return ((IImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue()).getCurrent();
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





    public void setBody(IImperfectField<Weight> x) {
        store.put( property( "body_$$Imp", x ) );
        store.put( property( "body", x.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue());
    }

    public void setBodyDistr(IDistribution<Weight> x) {
        IImperfectField<Weight> fld = (IImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "body", fld.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue() );
    }

    public void setBodyValue(Weight x) {
        IImperfectField<Weight> fld = (IImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "body", fld.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue());
    }

    public void setBodyCore( Weight x ) {
        store.put( property( "body", x ) );
    }

    public void updateBodyDistr(IDistribution<Weight> x) {
        IImperfectField<Weight> fld = (IImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "body", fld.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue() );
    }

    public void updateBodyValue(Weight x) {
        IImperfectField<Weight> fld = (IImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "body", fld.getCrisp() ) );

        setWeightCore( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().doubleValue() );
    }

    public void updateBody(IImperfectField<Weight> x) {
        IImperfectField<Weight> fld = (IImperfectField<Weight>) store.get( propertyKey( "body_$$Imp" ) ).getValue();
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





    public IImperfectField<Price> getPrice() {
        return (IImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
    }

    public IDistribution<Price> getPriceDistr() {
        return ((IImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue()).getCurrent();
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

    public void setPrice(IImperfectField<Price> x) {
        store.put( property( "price_$$Imp", x ) );
        store.put( property( "price", x.getCrisp() ) );

        setBucksCore(((LinguisticImperfectField<Price, Integer>) getPrice()).defuzzify().intValue() );
    }

    public void setPriceDistr(IDistribution<Price> x) {
        IImperfectField<Price> fld = (IImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "price", fld.getCrisp() ) );

        setBucksCore(((LinguisticImperfectField<Price, Integer>) getPrice()).defuzzify().intValue() );
    }

    public void setPriceValue(Price x) {
        IImperfectField<Price> fld = (IImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "price", fld.getCrisp() ) );

        setBucksCore( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify().intValue()  );
    }

    public void setPriceCore( Price x ) {
        store.put( property( "price", x ) );
    }

    public void updatePrice(IImperfectField<Price> x) {
        IImperfectField<Price> fld = (IImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
        fld.update( x.getCurrent() );
        store.put( property( "price", fld.getCrisp() ) );

        setBucksCore( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify().intValue() );
    }

    public void updatePriceDistr(IDistribution<Price> x) {
        IImperfectField<Price> fld = (IImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "price", fld.getCrisp() ) );

        setBucksCore( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify().intValue() );
    }

    public void updatePriceValue(Price x) {
        IImperfectField<Price> fld = (IImperfectField<Price>) store.get( propertyKey( "price_$$Imp" ) ).getValue();
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






    public IImperfectField<Cheese> getLikes() {
        return (IImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
    }

    public IDistribution<Cheese> getLikesDistr() {
        return ((IImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue()).getCurrent();
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

    public void setLikes(IImperfectField<Cheese> x) {
        store.put( property( "likes_$$Imp", x ) );
        store.put( property( "likes", x.getCrisp() ) );
    }

    public void setLikesDistr(IDistribution<Cheese> x) {
        IImperfectField<Cheese> fld = (IImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void setLikesValue(Cheese x) {
        IImperfectField<Cheese> fld = (IImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
        fld.setValue( x, false );
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void setLikesCore( Cheese x ) {
        store.put( property( "likes", x ) );
    }

    public void updateLikesDistr(IDistribution<Cheese> x) {
        IImperfectField<Cheese> fld = (IImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void updateLikesValue(Cheese x) {
        IImperfectField<Cheese> fld = (IImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
        fld.update( x );
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void updateLikes(IImperfectField<Cheese> x ) {
        IImperfectField<Cheese> fld = (IImperfectField<Cheese>) store.get( propertyKey( "likes_$$Imp" ) ).getValue();
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