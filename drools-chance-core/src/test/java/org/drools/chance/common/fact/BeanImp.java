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

package org.drools.chance.common.fact;


import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.common.ImperfectField;
import org.drools.chance.common.ImperfectFieldImpl;
import org.drools.chance.common.trait.ImpBean;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.ImpKind;
import org.drools.chance.distribution.ImpType;
import org.drools.chance.distribution.fuzzy.linguistic.LinguisticImperfectField;
import org.drools.chance.distribution.fuzzy.linguistic.ShapedFuzzyPartitionStrategyFactory;
import org.drools.chance.distribution.probability.BasicDistributionStrategyFactory;
import org.drools.chance.distribution.probability.dirichlet.DirichletDistributionStrategyFactory;
import org.drools.chance.distribution.probability.discrete.DiscreteDistributionStrategyFactory;


/**
 * Desired target for the generated Handle class, given the class Bean
 * and assuming its two fields "field" and "age" have been tagged as @Imperfect
 */
public class BeanImp implements ImpBean {


    // an "history field" for every @Imperfect field in the managed bean
    private ImperfectField<String> name_$$Imp = new ImperfectFieldImpl<String>(
            ChanceStrategyFactory.<String>buildStrategies(
                    ImpKind.PROBABILITY,
                    ImpType.DISCRETE,
                    DegreeType.SIMPLE,
                    String.class)
    );



    private ImperfectField<Boolean> flag_$$Imp = new ImperfectFieldImpl<Boolean>(
            ChanceStrategyFactory.<Boolean>buildStrategies(
                    ImpKind.PROBABILITY,
                    ImpType.DISCRETE,
                    DegreeType.SIMPLE,
                    Boolean.class),
            "true/0.66, false/0.34"
    );



    // an "history field" for every @Imperfect field in the managed bean
//	private ImperfectField<Integer> age;
    private ImperfectField<Integer> age_$$Imp = new ImperfectFieldImpl<Integer>(
            ChanceStrategyFactory.<Integer>buildStrategies(
                    ImpKind.PROBABILITY,
                    ImpType.DIRICHLET,
                    DegreeType.SIMPLE,
                    Integer.class),
            "18/0.02, 19/0.01, 20/0.04"
    );





    //    private ImperfectField<Weight> body;
    private ImperfectField<Weight> body_$$Imp = new LinguisticImperfectField<Weight,Double>(
            ChanceStrategyFactory.<Weight>buildStrategies(
                    ImpKind.FUZZINESS,
                    ImpType.LINGUISTIC,
                    DegreeType.SIMPLE,
                    Weight.class),
            ChanceStrategyFactory.<Double>buildStrategies(
                    ImpKind.POSSIBILITY,
                    ImpType.LINGUISTIC,
                    DegreeType.SIMPLE,
                    Double.class),
            null
    );


    private ImperfectField<Cheese> likes_$$Imp = new ImperfectFieldImpl<Cheese>(
            ChanceStrategyFactory.<Cheese>buildStrategies(
                    ImpKind.PROBABILITY,
                    ImpType.BASIC,
                    DegreeType.SIMPLE,
                    Cheese.class),
            "cheddar/0.6"
    );


    //    private ImperfectField<Weight> body;
    private ImperfectField<Price> price_$$Imp = new LinguisticImperfectField<Price,Integer>(
            ChanceStrategyFactory.<Price>buildStrategies(
                    ImpKind.FUZZINESS,
                    ImpType.LINGUISTIC,
                    DegreeType.SIMPLE,
                    Price.class),
            ChanceStrategyFactory.<Integer>buildStrategies(
                    ImpKind.POSSIBILITY,
                    ImpType.LINGUISTIC,
                    DegreeType.SIMPLE,
                    Integer.class),
            null
    );


    private String  name;
    private Boolean flag;
    private Integer age;
    private Weight  body;
    private Double  weight;
    private Cheese  likes;
    private Integer bucks;
    private Price   price;




    // Inherited constructors
    public BeanImp() {

        age_$$Imp.setValue( new DirichletDistributionStrategyFactory<Integer>().buildStrategies( DegreeType.SIMPLE, Integer.class).parse( "18/0.02, 19/0.01, 20/0.04" ),
                false );
        name_$$Imp.setValue( new DiscreteDistributionStrategyFactory<String>().buildStrategies( DegreeType.SIMPLE, String.class).parse( "john/0.3, philip/0.7" ),
                false );
        body_$$Imp.setValue( new ShapedFuzzyPartitionStrategyFactory<Weight>().buildStrategies( DegreeType.SIMPLE, Weight.class).parse( "SLIM/0.5, FAT/0.5" ),
                false );
        likes_$$Imp.setValue( new BasicDistributionStrategyFactory<Cheese>().buildStrategies( DegreeType.SIMPLE, Cheese.class).parse( "cheddar/0.6" ),
                false );

        body_$$Imp.setValue( new ShapedFuzzyPartitionStrategyFactory<Weight>().buildStrategies( DegreeType.SIMPLE, Weight.class ).parse("SLIM/0.6, FAT/0.4"), false );

        weight = 65.0;

        synchFields();
    }




    private void synchFields() {
        if (name != null)
            name_$$Imp.setValue(name);
        if (name_$$Imp != null)
            name = name_$$Imp.getCrisp();

        if (age != null)
            age_$$Imp.setValue(age);
        if (age_$$Imp != null)
            age = age_$$Imp.getCrisp();

        if (flag != null)
            flag_$$Imp.setValue(flag);
        if (flag_$$Imp != null)
            flag = flag_$$Imp.getCrisp();




        if ( weight != null ) {
            Distribution dist = ((LinguisticImperfectField<Weight, Double>) body_$$Imp).fuzzify(weight);
            body_$$Imp.setValue(dist, false);
            body = body_$$Imp.getCrisp();
        } else {

            if ( body != null )
                body_$$Imp.setValue( body );
            if ( body_$$Imp != null ) {
                body = body_$$Imp.getCrisp();
                weight = ((LinguisticImperfectField<Weight,Double>) body_$$Imp).defuzzify().doubleValue();
            }

        }


        if ( bucks != null ) {
            Distribution dist = ((LinguisticImperfectField<Price, Integer>) price_$$Imp).fuzzify( bucks );
            price_$$Imp.setValue(dist, false);
            price = price_$$Imp.getCrisp();
        } else {

            if ( price != null )
                price_$$Imp.setValue( price );
            if ( price_$$Imp != null ) {
                price = price_$$Imp.getCrisp();
                bucks = ((LinguisticImperfectField<Price, Integer>) price_$$Imp).defuzzify().intValue();
            }

        }


        if (likes != null)
            likes_$$Imp.setValue(likes);
        if (likes_$$Imp != null)
            likes = likes_$$Imp.getCrisp();
    }






    public ImperfectField<String> getName() {
        return name_$$Imp;
    }

    public Distribution<String> getNameDistr() {
        return name_$$Imp.getCurrent();
    }

    public String getNameValue() {
        return name;
    }


    public void setName(ImperfectField<String> x) {
        name_$$Imp = x;
        name = name_$$Imp.getCrisp();
    }

    public void updateName(ImperfectField<String> x) {
        name_$$Imp.update( x.getCurrent() );
        name = name_$$Imp.getCrisp();
    }




    public void setNameValue(String val) {
        name_$$Imp.setValue(val,false);
        name = name_$$Imp.getCrisp();
    }

    public void updateNameValue(String val) {
        name_$$Imp.setValue(val,true);
        name = name_$$Imp.getCrisp();
    }


    public void setNameDistr(Distribution<String> field_dist) {
        name_$$Imp.setValue(field_dist,false);
        name = (name_$$Imp.getCrisp());	}

    public void updateNameDistr(Distribution<String> field_bit) {
        name_$$Imp.update(field_bit);
        name = name_$$Imp.getCrisp();
    }




    public ImperfectField<Boolean> getFlag() {
        return flag_$$Imp;
    }

    public Distribution<Boolean> getFlagDistr() {
        return flag_$$Imp.getCurrent();
    }

    public Boolean getFlagValue() {
        return flag;
    }

    public void setFlag(ImperfectField<Boolean> x) {
        flag_$$Imp = x;
        flag = flag_$$Imp.getCrisp();
    }

    public void setFlagDistr(Distribution<Boolean> x) {
        flag_$$Imp.setValue( x, false );
        flag = flag_$$Imp.getCrisp();
    }

    public void setFlagValue(Boolean x) {
        flag_$$Imp.setValue( x, false );
        flag = flag_$$Imp.getCrisp();
    }

    public void updateFlag(ImperfectField<Boolean> x) {
        flag_$$Imp.update( x.getCurrent() );
        flag = flag_$$Imp.getCrisp();
    }

    public void updateFlagDistr(Distribution<Boolean> x) {
        flag_$$Imp.update( x );
        flag = flag_$$Imp.getCrisp();
    }

    public void updateFlagValue(Boolean x) {
        flag_$$Imp.update( x );
        flag = flag_$$Imp.getCrisp();
    }










    // Extended getters and setters for field : age

    public ImperfectField<Integer> getAge() {
        return age_$$Imp;
    }

    public Distribution<Integer> getAgeDistr() {
        return age_$$Imp.getCurrent();
    }

    public Integer getAgeValue() {
        return age;
    }


    public void setAge(ImperfectField<Integer> x) {
        age_$$Imp = x;
        age = age_$$Imp.getCrisp();
    }

    public void updateAge(ImperfectField<Integer> x) {
        age_$$Imp.update( x.getCurrent() );
        age = age_$$Imp.getCrisp();
    }




    public void setAgeValue(Integer val) {
        age_$$Imp.setValue(val,false);
        age = age_$$Imp.getCrisp();
    }

    public void updateAgeValue(Integer val) {
        age_$$Imp.setValue(val,true);
        age = age_$$Imp.getCrisp();
    }


    public void setAgeDistr(Distribution<Integer> field_dist) {
        age_$$Imp.setValue(field_dist,false);
        age = (age_$$Imp.getCrisp());	}

    public void updateAgeDistr(Distribution<Integer> field_bit) {
        age_$$Imp.update(field_bit);
        age = age_$$Imp.getCrisp();
    }








    public Integer getBucks() {
        return bucks;
    }

    public void setBucks(Integer w) {
        Distribution dist = ((LinguisticImperfectField<Price,Integer>) price_$$Imp).fuzzify(w);
        price_$$Imp.setValue(dist,false);
        price = price_$$Imp.getCrisp();

        bucks = w;
    }




    public ImperfectField<Price> getPrice() {
        return price_$$Imp;
    }

    public Distribution<Price> getPriceDistr() {
        return price_$$Imp.getCurrent();
    }

    public Price getPriceValue() {
        return price;
    }

    public void setPrice(ImperfectField<Price> x) {
        price_$$Imp.setValue( x.getCurrent(), false );
        price = price_$$Imp.getCrisp();

        bucks = ((LinguisticImperfectField<Price,Integer>) price_$$Imp).defuzzify().intValue();
    }

    public void setPriceDistr(Distribution<Price> x) {
        price_$$Imp.setValue( x, false );
        price = price_$$Imp.getCrisp();

        bucks = ((LinguisticImperfectField<Price,Integer>) price_$$Imp).defuzzify().intValue();
    }


    public void setPriceValue(Price val) {
        price = val;
        this.price_$$Imp.setValue( val, false);

        bucks = ((LinguisticImperfectField<Price,Integer>) price_$$Imp).defuzzify().intValue();
    }



    public void updatePrice(ImperfectField<Price> price_bit) {
        price_$$Imp.update( price_bit.getCurrent() );
        price = price_$$Imp.getCrisp();

        bucks = ((LinguisticImperfectField<Price,Integer>) price_$$Imp).defuzzify().intValue();
    }

    public void updatePriceValue(Price val) {
        price = val;
        price_$$Imp.setValue(val,true);

        bucks = ((LinguisticImperfectField<Price,Integer>) price_$$Imp).defuzzify().intValue();
    }


    public void updatePriceDistr(Distribution<Price> price_bit) {
        price_$$Imp.update(price_bit);
        price = price_$$Imp.getCrisp();

        bucks = ((LinguisticImperfectField<Price,Integer>) price_$$Imp).defuzzify().intValue();
    }
    
    public void updatePriceValue( Price val, Degree deg, String... args ) {
        price_$$Imp.update( val, deg, args );
        price = price_$$Imp.getCrisp();

        bucks = ((LinguisticImperfectField<Price,Integer>) price_$$Imp).defuzzify().intValue();
    }








    public ImperfectField<Cheese> getLikes() {
        return likes_$$Imp;
    }

    public Distribution<Cheese> getLikesDistr() {
        return likes_$$Imp.getCurrent();
    }

    public Cheese getLikesValue() {
        return likes;
    }


    public void setLikes(ImperfectField<Cheese> x) {
        likes_$$Imp = x;
        likes = likes_$$Imp.getCrisp();
    }

    public void updateLikes(ImperfectField<Cheese> x) {
        likes_$$Imp.update( x.getCurrent() );
        likes = likes_$$Imp.getCrisp();
    }

    public void setLikesValue(Cheese val) {
        likes_$$Imp.setValue(val,false);
        likes = likes_$$Imp.getCrisp();
    }



    public void updateLikesValue(Cheese val) {
        likes_$$Imp.setValue(val,true);
        likes = likes_$$Imp.getCrisp();
    }


    public void setLikesDistr(Distribution<Cheese> field_dist) {
        likes_$$Imp.setValue(field_dist,false);
        likes = (likes_$$Imp.getCrisp());	}

    public void updateLikesDistr(Distribution<Cheese> field_bit) {
        likes_$$Imp.update(field_bit);
        likes = likes_$$Imp.getCrisp();
    }







    // Extended getters and setters for fuzzy field : body


    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double w) {
        Distribution dist = ((LinguisticImperfectField<Weight,Double>) body_$$Imp).fuzzify(w);
        body_$$Imp.setValue(dist,false);
        body = body_$$Imp.getCrisp();

        weight = w;
    }




    public ImperfectField<Weight> getBody() {
        return body_$$Imp;
    }

    public Distribution<Weight> getBodyDistr() {
        return body_$$Imp.getCurrent();
    }

    public Weight getBodyValue() {
        return body;
    }

    public void setBody(ImperfectField<Weight> x) {
        body_$$Imp.setValue( x.getCurrent(), false );
        body = body_$$Imp.getCrisp();

        weight = ((LinguisticImperfectField<Weight,Double>) body_$$Imp).defuzzify().doubleValue();
    }

    public void setBodyDistr(Distribution<Weight> x) {
        body_$$Imp.setValue( x, false );
        body = body_$$Imp.getCrisp();

        weight = ((LinguisticImperfectField<Weight,Double>) body_$$Imp).defuzzify().doubleValue();
    }


    public void setBodyValue(Weight val) {
        body = val;
        this.body_$$Imp.setValue( val, false);

        weight = ((LinguisticImperfectField<Weight,Double>) body_$$Imp).defuzzify().doubleValue();
    }



    public void updateBody(ImperfectField<Weight> body_bit) {
        body_$$Imp.update( body_bit.getCurrent() );
        body = body_$$Imp.getCrisp();

        weight = ((LinguisticImperfectField<Weight,Double>) body_$$Imp).defuzzify().doubleValue();
    }

    public void updateBodyValue(Weight val) {
        body = val;
        body_$$Imp.setValue(val,true);

        weight = ((LinguisticImperfectField<Weight,Double>) body_$$Imp).defuzzify().doubleValue();
    }


    public void updateBodyDistr(Distribution<Weight> body_bit) {
        body_$$Imp.update(body_bit);
        body = body_$$Imp.getCrisp();

        weight = ((LinguisticImperfectField<Weight,Double>) body_$$Imp).defuzzify().doubleValue();
    }






    @Override
    public String toString() {
        return "BeanImp - HC {" +
                "name_$$Imp=" + name_$$Imp +
                ", age_$$Imp=" + age_$$Imp +
                ", body_$$Imp=" + body_$$Imp +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", body=" + body +
                ", weight=" + weight +
                '}';
    }
}
