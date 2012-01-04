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

        //TODO : Chiamare setValue con boolean

        if ( getNameCore() != null ) {
            getName().setValue( getNameCore(), false );
        }
        setNameCore( getName().getCrisp() );

        if ( getFlagCore() != null ) {
            getFlag().setValue( getFlagCore(), false );
        }
        setFlagCore( getFlag().getCrisp() );

        if ( getAgeCore() != null ) {
            getAge().setValue( getAgeCore(), false );
        }
        setAgeCore( getAge().getCrisp() );

        if ( getLikesCore() != null ) {
            getLikes().setValue( getLikesCore(), false );
        }
        setLikesCore( getLikes().getCrisp() );

    }


















    public IImperfectField<String> getName() {
        return (IImperfectField<String>) store.get( propertyKey( "name_Dist" ) ).getValue();
    }

    public IDistribution<String> getNameDistr() {
        return ((IImperfectField<String>) store.get( propertyKey( "name_Dist" ) ).getValue()).getCurrent();
    }

    public String getNameValue() {
        return getCore().getName();
    }

    public String getNameCore() {
        return getCore().getName();
    }

    public void setName(IImperfectField<String> x) {
        store.put( property( "name_Dist", x ) );
        getCore().setName( x.getCrisp() );
    }

    public void setNameDistr(IDistribution<String> x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_Dist" ) ).getValue();
        fld.setValue( x, false );
        getCore().setName( fld.getCrisp() );
    }

    public void setNameValue(String x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_Dist" ) ).getValue();
        fld.setValue( x, false );
        getCore().setName( fld.getCrisp() );
    }

    public void setNameCore( String x ) {
        getCore().setName(x);
    }




    public void updateName(IImperfectField<String> x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_Dist" ) ).getValue();
        fld.update( x.getCurrent() );
        getCore().setName( fld.getCrisp() );
    }

    public void updateNameDistr(IDistribution<String> x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_Dist" ) ).getValue();
        fld.update( x );
        getCore().setName( fld.getCrisp() );
    }

    public void updateNameValue(String x) {
        IImperfectField<String> fld = (IImperfectField<String>) store.get( propertyKey( "name_Dist" ) ).getValue();
        fld.update( x );
        getCore().setName( fld.getCrisp() );
    }









    public IImperfectField<Boolean> getFlag() {
        return (IImperfectField<Boolean>) store.get( propertyKey( "flag_Dist" ) ).getValue();
    }

    public IDistribution<Boolean> getFlagDistr() {
        return ((IImperfectField<Boolean>) store.get( propertyKey( "flag_Dist" ) ).getValue()).getCurrent();
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
        store.put( property( "flag_Dist", x ) );
        store.put( property( "flag", x.getCrisp() ) );
    }

    public void setFlagDistr(IDistribution<Boolean> x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_Dist" ) ).getValue();
        fld.setValue( x );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void setFlagValue(Boolean x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_Dist" ) ).getValue();
        fld.setValue( x );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void setFlagCore( Boolean x ) {
        store.put( property( "flag", x ) );
    }

    public void updateFlag(IImperfectField<Boolean> x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_Dist" ) ).getValue();
        fld.update( x.getCurrent() );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void updateFlagDistr(IDistribution<Boolean> x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_Dist" ) ).getValue();
        fld.update( x );
        store.put( property( "flag", fld.getCrisp() ) );
    }

    public void updateFlagValue(Boolean x) {
        IImperfectField<Boolean> fld = (IImperfectField<Boolean>) store.get( propertyKey( "flag_Dist" ) ).getValue();
        fld.update( x );
        store.put( property( "flag", fld.getCrisp() ) );
    }






    public IImperfectField<Integer> getAge() {
        return (IImperfectField<Integer>) store.get( propertyKey( "age_Dist" ) ).getValue();
    }

    public IDistribution<Integer> getAgeDistr() {
        return ((IImperfectField<Integer>) store.get( propertyKey( "age_Dist" ) ).getValue()).getCurrent();
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
        store.put( property( "age_Dist", x ) );
        store.put( property( "age", x.getCrisp() ) );
    }

    public void setAgeDistr(IDistribution<Integer> x) {
        IImperfectField<Integer> fld = (IImperfectField<Integer>) store.get( propertyKey( "age_Dist" ) );
        fld.setValue( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "age", fld.getCrisp() ) );
    }

    public void setAgeValue(Integer x) {
        IImperfectField<Integer> fld = (IImperfectField<Integer>) store.get( propertyKey( "age_Dist" ) );
        fld.setValue( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "age", fld.getCrisp() ) );
    }

    public void setAgeCore( Integer x ) {
        store.put( property( "age", x ) );
    }

    public void updateAge(IDistribution<Integer> x) {
        IImperfectField<Integer> fld = (IImperfectField<Integer>) store.get( propertyKey( "age_Dist" ) );
        fld.update( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "age", fld.getCrisp() ) );
    }

    public void updateAge(Integer x) {
        IImperfectField<Integer> fld = (IImperfectField<Integer>) store.get( propertyKey( "age_Dist" ) );
        fld.update( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "age", fld.getCrisp() ) );
    }





    public IImperfectField<Weight> getBody() {
        return (IImperfectField<Weight>) store.get( propertyKey( "body_Dist" ) ).getValue();
    }

    public IDistribution<Weight> getBodyDistr() {
        return ((IImperfectField<Weight>) store.get( propertyKey( "body_Dist" ) ).getValue()).getCurrent();
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
        store.put( property( "body_Dist", x ) );
        store.put( property( "body", x.getCrisp() ) );

        setWeight( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify(), false );
    }

    public void setBodyDistr(IDistribution<Weight> x) {
        IImperfectField<Weight> fld = (IImperfectField<Weight>) store.get( propertyKey( "body_Dist" ) );
        fld.setValue( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "body", fld.getCrisp() ) );

        setWeight( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify(), false );
    }

    public void setBodyValue(Weight x) {
        IImperfectField<Weight> fld = (IImperfectField<Weight>) store.get( propertyKey( "body_Dist" ) );
        fld.setValue( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "body", fld.getCrisp() ) );

        setWeight( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify(), false );
    }

    public void setBodyCore( Weight x ) {
        store.put( property( "body", x ) );
    }

    public void updateBody(IDistribution<Weight> x) {
        IImperfectField<Weight> fld = (IImperfectField<Weight>) store.get( propertyKey( "body_Dist" ) );
        fld.update( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "body", fld.getCrisp() ) );

        setWeight( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify(), false );
    }

    public void updateBody(Weight x) {
        IImperfectField<Weight> fld = (IImperfectField<Weight>) store.get( propertyKey( "body_Dist" ) );
        fld.update( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "body", fld.getCrisp() ) );

        setWeight( ((LinguisticImperfectField<Weight,Double>) getBody()).defuzzify(), false );
    }




    public Double getWeight() {
        return object.getWeight();
    }

    public void setWeight(Double x) {
        setWeight( x, true );
    }

    public void setWeight(Double x, boolean flag) {

        if ( flag ) {
            getBody().setValue( ((LinguisticImperfectField<Weight,Double>) getBody()).fuzzify( x ) );
        }

        setBodyCore( getBody().getCrisp() );

        getCore().setWeight( x );
    }




    public IImperfectField<Price> getPrice() {
        return (IImperfectField<Price>) store.get( propertyKey( "price_Dist" ) ).getValue();
    }

    public IDistribution<Price> getPriceDistr() {
        return ((IImperfectField<Price>) store.get( propertyKey( "price_Dist" ) ).getValue()).getCurrent();
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
        store.put( property( "price_Dist", x ) );
        store.put( property( "price", x.getCrisp() ) );

        setBucks(((LinguisticImperfectField<Price, Integer>) getPrice()).defuzzify(), false);
    }

    public void setPriceDistr(IDistribution<Price> x) {
        IImperfectField<Price> fld = (IImperfectField<Price>) store.get( propertyKey( "price_Dist" ) );
        fld.setValue( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "price", fld.getCrisp() ) );

        setBucks(((LinguisticImperfectField<Price, Integer>) getPrice()).defuzzify(), false);
    }

    public void setPriceValue(Price x) {
        IImperfectField<Price> fld = (IImperfectField<Price>) store.get( propertyKey( "price_Dist" ) );
        fld.setValue( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "price", fld.getCrisp() ) );

        setBucks( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify(), false );
    }

    public void setPriceCore( Price x ) {
        store.put( property( "price", x ) );
    }

    public void updatePrice(IDistribution<Price> x) {
        IImperfectField<Price> fld = (IImperfectField<Price>) store.get( propertyKey( "price_Dist" ) );
        fld.update(x);
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "price", fld.getCrisp() ) );

        setBucks( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify(), false );
    }

    public void updatePrice(Price x) {
        IImperfectField<Price> fld = (IImperfectField<Price>) store.get( propertyKey( "price_Dist" ) );
        fld.update( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "price", fld.getCrisp() ) );

        setBucks( ( (LinguisticImperfectField<Price,Integer>) getPrice()).defuzzify(), false );
    }




    public Integer getBucks() {
        return (Integer) (store.get( propertyKey( "bucks" ) ) ).getValue();
    }

    public void setBucks(Integer x) {
        setBucks( x, true );
    }

    public void setBucks(Integer x, boolean flag) {

        if ( flag ) {
            getPrice().setValue( ((LinguisticImperfectField<Price, Integer>) getPrice()).fuzzify( x ) );
        }

        setPriceCore(getPrice().getCrisp());

        //TODO: Commented out??
        store.put( property( "bucks", x ) );
    }







    public IImperfectField<Cheese> getLikes() {
        return (IImperfectField<Cheese>) store.get( propertyKey( "likes_Dist" ) ).getValue();
    }

    public IDistribution<Cheese> getLikesDistr() {
        return ((IImperfectField<Cheese>) store.get( propertyKey( "likes_Dist" ) ).getValue()).getCurrent();
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
        store.put( property( "likes_Dist", x ) );
        store.put( property( "likes", x.getCrisp() ) );
    }

    public void setLikesDistr(IDistribution<Cheese> x) {
        IImperfectField<Cheese> fld = (IImperfectField<Cheese>) store.get( propertyKey( "likes_Dist" ) );
        fld.setValue( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void setLikesValue(Cheese x) {
        IImperfectField<Cheese> fld = (IImperfectField<Cheese>) store.get( propertyKey( "likes_Dist" ) );
        fld.setValue( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void setLikesCore( Cheese x ) {
        store.put( property( "likes", x ) );
    }

    public void updateLikes(IDistribution<Cheese> x) {
        IImperfectField<Cheese> fld = (IImperfectField<Cheese>) store.get( propertyKey( "likes_Dist" ) );
        fld.update( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
        store.put( property( "likes", fld.getCrisp() ) );
    }

    public void updateLikes(Cheese x) {
        IImperfectField<Cheese> fld = (IImperfectField<Cheese>) store.get( propertyKey( "likes_Dist" ) );
        fld.update( x );
        //TODO :  store.get viene chiamato 2 volte nell'asm , si può migliorare così?
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
