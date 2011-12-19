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


import org.drools.chance.common.*;
import org.drools.chance.common.trait.ImpBean;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.fuzzy.linguistic.LinguisticImperfectField;
import org.drools.chance.distribution.fuzzy.linguistic.ShapedFuzzyPartitionStrategyFactory;
import org.drools.chance.distribution.probability.dirichlet.DirichletDistributionStrategyFactory;
import org.drools.chance.distribution.probability.discrete.DiscreteDistributionStrategyFactory;


/**
 * Desired target for the generated Handle class, given the class Bean
 * and assuming its two fields "field" and "age" have been tagged as @Imperfect
 */
public class BeanImp implements ImpBean {


    // an "history field" for every @Imperfect field in the managed bean
    private IImperfectField<String> name_Dist = new ImperfectField<String>(
            ChanceStrategyFactory.<String>buildStrategies(
                    "probability",
                    "discrete",
                    "simple",
                    String.class)
    );



    private IImperfectField<Boolean> flag_Dist = new ImperfectField<Boolean>(
            ChanceStrategyFactory.<Boolean>buildStrategies(
                    "probability",
                    "discrete",
                    "simple",
                    Boolean.class),
            "true/0.66, false/0.34"
    );



    // an "history field" for every @Imperfect field in the managed bean
//	private IImperfectField<Integer> age;
    private IImperfectField<Integer> age_Dist = new ImperfectHistoryField<Integer>(
            ChanceStrategyFactory.<Integer>buildStrategies(
                    "probability",
                    "dirichlet",
                    "simple",
                    Integer.class),
            Integer.valueOf("3"),
            "18/0.02, 19/0.01, 20/0.04"
    );





    //    private IImperfectField<Weight> body;
    private IImperfectField<Weight> body_Dist = new LinguisticImperfectField<Weight,Double>(
            ChanceStrategyFactory.<Weight>buildStrategies(
                    "fuzzy",
                    "linguistic",
                    "simple",
                    Weight.class),
            ChanceStrategyFactory.<Double>buildStrategies(
                    "possibility",
                    "linguistic",
                    "simple",
                    Double.class),
            Integer.valueOf("3"),
            null
    );


    private String name;
    private boolean flag;
    private Integer age;
    private Weight body;
    private Double weight;




    // Inherited constructors
    public BeanImp() {

        age_Dist.setValue( new DirichletDistributionStrategyFactory<Integer>().buildStrategies( "simple", Integer.class).parse( "18/0.02, 19/0.01, 20/0.04" ),
                false );
        name_Dist.setValue( new DiscreteDistributionStrategyFactory<String>().buildStrategies("simple", String.class).parse("john/0.3, philip/0.7" ),
                false );
        body_Dist.setValue( new ShapedFuzzyPartitionStrategyFactory<Weight>().buildStrategies("simple", Weight.class).parse("SLIM/0.5, FAT/0.5" ),
                false );


        synchFields();
    }




    private void synchFields() {
        if (name != null)
            name_Dist.setValue(name);
        if (name_Dist != null)
            name = name_Dist.getCrisp();

        if (age != null)
            age_Dist.setValue(age);
        if (age_Dist != null)
            age = age_Dist.getCrisp();


        if (weight != null) {
            Double w = weight;

            IDistribution dist = ((LinguisticImperfectField<Weight,Double>) body_Dist).fuzzify(w);
            body_Dist.setValue(dist,false);
            body = body_Dist.getCrisp();

            weight = w;
        } else {
            if (body != null)
                body_Dist.setValue(body);
            if (body_Dist != null) {
                body = body_Dist.getCrisp();
                weight = ((LinguisticImperfectField<Weight,Double>) body_Dist).defuzzify();
            }
        }
    }






    public IImperfectField<String> getName() {
        return name_Dist;
    }

    public IDistribution<String> getNameDistr() {
        return name_Dist.getCurrent();
    }

    public String getNameValue() {
        return name;
    }


    public void setName(IImperfectField<String> x) {
        name_Dist = x;
        name = name_Dist.getCrisp();
    }

    public void updateName(IImperfectField<String> x) {
        name_Dist.update( x.getCurrent() );
        name = name_Dist.getCrisp();
    }




    public void setNameValue(String val) {
        name_Dist.setValue(val,false);
        name = name_Dist.getCrisp();
    }

    public void updateNameValue(String val) {
        name_Dist.setValue(val,true);
        name = name_Dist.getCrisp();
    }


    public void setNameDistr(IDistribution<String> field_dist) {
        name_Dist.setValue(field_dist,false);
        name = (name_Dist.getCrisp());	}

    public void updateNameDistr(IDistribution<String> field_bit) {
        name_Dist.update(field_bit);
        name = name_Dist.getCrisp();
    }




    public IImperfectField<Boolean> getFlag() {
        return flag_Dist;
    }

    public IDistribution<Boolean> getFlagDistr() {
        return flag_Dist.getCurrent();
    }

    public Boolean getFlagValue() {
        return flag;
    }

    public void setFlag(IImperfectField<Boolean> x) {
        flag_Dist = x;
        flag = flag_Dist.getCrisp();
    }

    public void setFlagDistr(IDistribution<Boolean> x) {
        flag_Dist.setValue( x, false );
        flag = flag_Dist.getCrisp();
    }

    public void setFlagValue(Boolean x) {
        flag_Dist.setValue( x, false );
        flag = flag_Dist.getCrisp();
    }

    public void updateFlag(IImperfectField<Boolean> x) {
        flag_Dist.update( x.getCurrent() );
        flag = flag_Dist.getCrisp();
    }

    public void updateFlagDistr(IDistribution<Boolean> x) {
        flag_Dist.update( x );
        flag = flag_Dist.getCrisp();
    }

    public void updateFlagValue(Boolean x) {
        flag_Dist.update( x );
        flag = flag_Dist.getCrisp();
    }






    public IImperfectField<Integer> getAge() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }





    // Extended getters and setters for field : age

    public IDistribution<Integer> getAgeDistr() {
        return age_Dist.getCurrent();
    }


    public Integer getAgeValue() {
        return age;
    }

    public void setAge(IImperfectField<Integer> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setAgeDistr(IDistribution<Integer> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setAgeValue(Integer x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setAge(Integer val) {
        age = val;
        age_Dist.setValue(val,false);
    }

    public void updateAge(Integer val) {
        age_Dist.setValue(val,true);
        age = age_Dist.getCrisp();
    }

    public IImperfectField<Weight> getBody() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void setAge(IDistribution<Integer> age_dist) {
        age_Dist.setValue(age_dist,true);
        age = age_Dist.getCrisp();	}

    public void updateAge(IDistribution<Integer> age_bit) {
        age_Dist.update(age_bit);
        age = age_Dist.getCrisp();
    }







    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double w) {
        IDistribution dist = ((LinguisticImperfectField<Weight,Double>) body_Dist).fuzzify(w);
        body_Dist.setValue(dist,false);
        body = body_Dist.getCrisp();

        weight = w;
    }

    public IImperfectField<Price> getPrice() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Price> getPriceDistr() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Price getPriceValue() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setPrice(IImperfectField<Price> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setPriceDistr(IDistribution<Price> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setPriceValue(Price x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updatePrice(IDistribution<Price> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updatePrice(Price x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Integer getBucks() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setBucks(Integer x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public IImperfectField<Cheese> getLikes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDistribution<Cheese> getLikesDistr() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Cheese getLikesValue() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLikes(IImperfectField<Cheese> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLikesDistr(IDistribution<Cheese> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLikesValue(Cheese x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateLikes(IDistribution<Cheese> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateLikes(Cheese x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void updateWeight(Double w) {
        IDistribution dist = ((LinguisticImperfectField<Weight,Double>) body_Dist).fuzzify(w);
        body_Dist.setValue(dist,false);
        body = body_Dist.getCrisp();

        weight = ((LinguisticImperfectField<Weight, Double>) body_Dist).defuzzify();
    }







    // Extended getters and setters for fuzzy field : body



    public IDistribution<Weight> getBodyDistr() {
        return body_Dist.getCurrent();
    }

    public Weight getBodyValue() {
        return body;
    }

    public void setBody(IImperfectField<Weight> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setBodyDistr(IDistribution<Weight> x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setBodyValue(Weight x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setBody(Weight val) {
        body = val;
        this.body_Dist.setValue(val,false);

        weight = ((LinguisticImperfectField<Weight,Double>) body_Dist).defuzzify();
    }

    public void updateBody(Weight val) {
        body = val;
        this.body_Dist.setValue(val,true);

        weight = ((LinguisticImperfectField<Weight,Double>) body_Dist).defuzzify();
    }



    public void setBody(IDistribution<Weight> body_dist) {
        body_Dist.setValue(body_dist,true);
        body = body_Dist.getCrisp();

        weight = ((LinguisticImperfectField<Weight,Double>) body_Dist).defuzzify();
    }

    public void updateBody(IDistribution<Weight> body_bit) {
        body_Dist.update(body_bit);
        body = body_Dist.getCrisp();


        weight = ((LinguisticImperfectField<Weight,Double>) body_Dist).defuzzify();
    }


    @Override
    public String toString() {
        return "BeanImp{" +
                "name_Dist=" + name_Dist +
                ", age_Dist=" + age_Dist +
                ", body_Dist=" + body_Dist +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", body=" + body +
                ", weight=" + weight +
                '}';
    }
}
