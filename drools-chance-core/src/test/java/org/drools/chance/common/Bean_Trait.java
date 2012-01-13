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

package org.drools.chance.common;


import org.drools.chance.common.fact.Weight;
import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.fuzzy.linguistic.LinguisticImperfectField;
import org.drools.core.util.TripleStore;
import org.drools.factmodel.traits.TraitProxy;


/**
 * Desired target for the generated Handle class, given the class Bean
 * and assuming its two fields "field" and "age" have been tagged as @Imperfect
 */
@Deprecated
public class Bean_Trait extends TraitProxy {


	// an "history field" for every @Imperfect field in the managed bean
	private IImperfectField<String> field;
//    = new ImperfectField<String>(
//												ChanceStrategyFactory.<String>buildStrategies(
//                                                        "probability",
//                                                        "discrete",
//                                                        "simple",
//                                                        String.class)
//												);

    // an "history field" for every @Imperfect field in the managed bean
	private IImperfectField<Integer> age;
//    private IImperfectField<Integer> age = new ImperfectHistoryField<Integer>(
//															ChanceStrategyFactory.<Integer>buildStrategies(
//                                                                    "probability",
//                                                                    "dirichlet",
//                                                                    "simple",
//                                                                    Integer.class),
//                                                Integer.valueOf("3"),
//                                                "18/0.02, 19/0.01, 20/0.04"
//												);
//




    private IImperfectField<Weight> body;
//	private IImperfectField<Weight> body = new LinguisticImperfectField<Weight,Double>(
//			ChanceStrategyFactory.<Weight>buildStrategies(
//                    "fuzzy",
//                    "linguistic",
//                    "simple",
//                    Weight.class),
//            ChanceStrategyFactory.<Double>buildStrategies(
//                    "possibility",
//                    "linguistic",
//                    "simple",
//                    Double.class),
//            Integer.valueOf("3"),
//            null
//			);


    private Bean object;

    private TripleStore store;


    public Bean getObject() {
        return object;
    }

    public void setObject(Bean object) {
        this.object = object;
    }



    // Inherited constructors
    public Bean_Trait(Bean b, TripleStore store ) {
         this.object = b;
         this.store = store;


        initSoftFields();

//        initAge();

//        age.setValue(age.getStrategies().parse("xxx"));
//
////        age.setValue( new DirichletDistributionStrategyFactory<Integer>().buildStrategies( "simple", Integer.class).parse( "18/0.02, 19/0.01, 20/0.04" ),
////                      false );
////        field.setValue( new DiscreteDistributionStrategyFactory<String>().buildStrategies("simple", String.class).parse("john/0.3, philip/0.7" ),
////                        false );
////        body.setValue( new ShapedFuzzyPartitionStrategyFactory<Weight>().buildStrategies("simple", Weight.class).parse("SLIM/0.5, FAT/0.5" ),
////                       false );


//        synchFields();
    }

    private void initSoftFields() {
        if (! store.contains( propertyKey( "age" ) ) ) {
            store.put( property( "age", 0 ) );
        }

         if (! store.contains( propertyKey( "age_Dist" ) ) ) {
            IImperfectField<Integer> ifield;
            ifield = new ImperfectField<Integer>(ChanceStrategyFactory.<Integer>buildStrategies(
                                                                    "probability",
                                                                    "discrete",
                                                                    "simple",
                                                                    Integer.class)
                                                            );
            store.put( property( "age_Dist", ifield ) );
        }
    }

    private void initAge() {

        if (! store.contains( propertyKey( "age_Dist" ) ) ) {
            IImperfectField<Integer> ifield;
            ifield = new ImperfectField<Integer>(ChanceStrategyFactory.<Integer>buildStrategies(
                                                                    "probability",
                                                                    "discrete",
                                                                    "simple",
                                                                    Integer.class)
                                                            );
            store.put( property( "age_Dist", ifield ) );
        }

    }


    private void synchFields() {
//        if (object.getName() != null)
//			field.setValue(object.getName());
//        if (field != null)
//            object.setName(field.getCrisp());

        if (object.getAge() != null && getAge() != null ) {
			getAge().setValue(object.getAge());
            object.setAge(getAge().getCrisp());
        }


//        if (object.getWeight() != null) {
//            Double w = object.getWeight();
//
//            IDistribution dist = ((LinguisticImperfectField<Weight,Double>) body).fuzzify(w);
//		    body.setValue(dist,false);
//            object.setBody(body.getCrisp());
//
//            object.setWeight(w);
//        } else {
//            if (object.getBody() != null)
//                body.setValue(object.getBody());
//            if (body != null) {
//                object.setBody(body.getCrisp());
//                object.setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify());
//            }
//        }
    }




    // typed getter

	public Bean getBean() {
		return ((Bean)  getObject());
	}



    // Extended getters and setters for field : field


	public IImperfectField<String> getFieldHistory() {
		return field;
	}

	public IDistribution<String> getField() {
		return field.getCurrent();
	}

	public String getFieldValue() {
		return getBean().getName();
	}

	public void setField(String val) {
		getBean().setName(val);
		this.field.setValue(val,false);
	}

    public void updateField(String val) {
		getBean().setName(val);
		this.field.setValue(val,true);
	}




	public void setField(IDistribution<String> field_dist) {
		this.field.setValue(field_dist,true);
		object.setName(field.getCrisp());	}

	public void updateField(IDistribution<String> field_bit) {
		this.field.update(field_bit);
		object.setName(field.getCrisp());
	}



    // Extended getters and setters for field : age

    public IImperfectField<Integer> getAge() {
		return this.age;
	}

	public IDistribution<Integer> getAgeDistr() {
		return age.getCurrent();
	}

	public Integer getAgeValue() {
		return object.getAge();
	}

	public void setAge(Integer val) {
		object.setAge(val);
		this.age.setValue(val,false);
	}

    public void updateAge(Integer val) {
		this.age.setValue(val,true);
        object.setAge(this.age.getCrisp());
	}


	public void setAge(IDistribution<Integer> age_dist) {
		this.age.setValue(age_dist,true);
		object.setAge(age.getCrisp());	}

	public void updateAge(IDistribution<Integer> age_bit) {
		this.age.update(age_bit);
		object.setAge(age.getCrisp());
	}



	
	
	
	
	public Double getWeight() {
		return object.getWeight();
	}

    public void setIntWgt( Integer in ) {

    }
	
	public void setWeight(Double w, boolean x) {
	    IDistribution dist = ((LinguisticImperfectField<Weight,Double>) body).fuzzify(w);
        if ( x ) {
		body.setValue(dist,false);
        }
        object.setBody(body.getCrisp());

	}


    public void updateWeight(Double w) {
	    IDistribution dist = ((LinguisticImperfectField<Weight,Double>) body).fuzzify(w);
		body.setValue(dist,true);
        object.setBody(body.getCrisp());

		object.setWeight(((LinguisticImperfectField<Weight, Double>) body).defuzzify().doubleValue());
	}
	

	
	
	
	
	
	// Extended getters and setters for fuzzy field : body



	public IDistribution<Weight> getBody() {
		return body.getCurrent();
	}

	public Weight getBodyValue() {
		return object.getBody();
	}

	public void setBody(Weight val) {
		object.setBody(val);
		this.body.setValue(val,false);
		
//		object.setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify());setIntWgt( w.intValue() );

        setIntWgt( ( (LinguisticImperfectField<Weight,Double>) getBody()).defuzzify().intValue() );
	}

    public void updateBody(Weight val) {
		object.setBody(val);
		this.body.setValue(val,true);

		object.setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify().doubleValue());
	}



	public void setBody(IDistribution<Weight> body_dist, boolean k) {
		this.body.setValue(body_dist,true);
		object.setBody(body.getCrisp());

        if ( k ) {
        object.setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify().doubleValue());
        } else {
            System.out.println("x");
        }
	}


    private IImperfectField<String> sa;

    public void setX( IImperfectField<String> xyz ) {
        System.out.println(sa);

    }




    public void updateBody(IDistribution<Weight> body_bit) {
		this.body.update(body_bit);
        object.setBody(body.getCrisp());

        object.setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify().doubleValue());
    }




    public IImperfectField<Integer> getAgess() {
        return (IImperfectField<Integer>) store.get( propertyKey( "age" ) );
    }
	

    public void foo( IDistribution<Integer> dist ) {
        ((IImperfectField<Integer>) store.get( propertyKey("age"))).setValue( dist, true );

        getBean().setAge(
                ((IImperfectField<Integer>) store.get(propertyKey("age")).getValue()).getCrisp() );

    }
	

    public String getLiksValue() {
        return (String) store.get( propertyKey( "age") ).getValue();
    }

    public void setLiksvalue( String s ) {
        store.put( property( "age", s ) );
    }


    public void setSelf( String k ) {
        setSelf( k, true );
    }

    private void setSelf(String k, boolean b) {
        if ( b ) {

        }
    }

    public void updateLikes( IDistribution dis ) {



        store.put ( property( "age", age.getCrisp() ) );

    }


    private void setIField( IImperfectField<String> field ) {
        store.put( property( "age", field ) );
        getBean().setName( field.getCrisp() );
    }

	
	

    public String toString() {
        return "BeanImp : \n"
                + "\t field = " + getField() + "\n"
                + "\t age = " + getAge() + "\n"
//                + "\t body = " + getBody() + "\n"
                + "\t weight = " + getWeight() + "\n";
    }









}
