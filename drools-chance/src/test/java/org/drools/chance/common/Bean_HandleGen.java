package org.drools.chance.common;


import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.fuzzy.linguistic.LinguisticImperfectField;
import org.drools.chance.distribution.fuzzy.linguistic.ShapedFuzzyPartition;
import org.drools.common.DefaultFactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;


/**
 * Desired target for the generated Handle class, given the class Bean
 * and assuming its two fields "field" and "age" have been tagged as @Imperfect
 */
public class Bean_HandleGen extends DefaultFactHandle {


	// an "history field" for every @Imperfect field in the managed bean
	private IImperfectField<String> field = new ImperfectHistoryField<String>(
												StrategyFactory.<String>buildStrategies(
														"probability",
														"discrete",
                                                        "simple", 
                                                        String.class),
                                                5,
                                                "john/0.3, philip/0.7"
												);

    // an "history field" for every @Imperfect field in the managed bean
	private IImperfectField<Integer> age = new ImperfectHistoryField<Integer>(
												StrategyFactory.<Integer>buildStrategies(
														"probability",
														"dirichlet",
                                                        "simple",
                                                        Integer.class),
                                                3,
                                                "18/0.02, 19/0.01, 20/0.04"
												);

	//private Storico<Integer> field2 = new Storico<Integer>(37,StrategyMetaFactory.generate(meta));




	
	private IImperfectField<Weight> body = new LinguisticImperfectField<Weight,Double>(
			StrategyFactory.<Weight>buildStrategies(
					"fuzzy",
					"linguistic",
                    "simple",
                    Weight.class),
            StrategyFactory.<Double>buildStrategies(
					"possibility",
					"linguistic",
                    "simple",
                    Double.class),
            3,
            "SLIM/0.5, FAT/0.5"
			);
	

	
	


    // Inherited constructors
    public Bean_HandleGen(Bean b) {
        super();
        setObject(b);
        synchFields();
    }


    public Bean_HandleGen(final int id,
                             final Object object,
                             final long recency,
                             final WorkingMemoryEntryPoint wmEntryPoint) {
        super(id,object,recency,wmEntryPoint);
        synchFields();
    }

    public Bean_HandleGen(String externalFormat) {
        super(externalFormat);
        synchFields();
    }

    public Bean_HandleGen(int id,
                             String wmEntryPointId,
                             int identityHashCode,
                             int objectHashCode,
                             long recency,
                             Object object) {
        super(id,wmEntryPointId,identityHashCode,objectHashCode,recency,object);
        synchFields();
    }



    private void synchFields() {
        if (getBean().getField() != null)
			field.setValue(getBean().getField());
        if (field != null)
            getBean().setField(field.getCrisp());

        if (getBean().getAge() != null)
			age.setValue(getBean().getAge());
        if (age != null)
            getBean().setAge(age.getCrisp());


        if (getBean().getWeight() != null) {
            Double w = getBean().getWeight();

            IDistribution dist = ((LinguisticImperfectField<Weight,Double>) body).fuzzify(w);
		    body.setValue(dist,false);
            getBean().setBody(body.getCrisp());

            getBean().setWeight(w);
        } else {
            if (getBean().getBody() != null)
                body.setValue(getBean().getBody());
            if (body != null) {
                getBean().setBody(body.getCrisp());
                getBean().setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify());
            }
        }
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
		return getBean().getField();
	}

	public void setField(String val) {
		getBean().setField(val);
		this.field.setValue(val,false);
	}

    public void updateField(String val) {
		getBean().setField(val);
		this.field.setValue(val,true);
	}




	public void setField(IDistribution<String> field_dist) {
		this.field.setValue(field_dist,true);
		getBean().setField(field.getCrisp());	}

	public void updateField(IDistribution<String> field_bit) {
		this.field.update(field_bit);
		getBean().setField(field.getCrisp());
	}



    // Extended getters and setters for field : age

    public IImperfectField<Integer> getAgeHistory() {
		return age;
	}

	public IDistribution<Integer> getAge() {
		return age.getCurrent();
	}

	public Integer getAgeValue() {
		return getBean().getAge();
	}

	public void setAge(Integer val) {
		getBean().setAge(val);
		this.age.setValue(val,false);
	}

    public void updateAge(Integer val) {
		this.age.setValue(val,true);
        getBean().setAge(this.age.getCrisp());
	}


	public void setAge(IDistribution<Integer> age_dist) {
		this.age.setValue(age_dist,true);
		getBean().setAge(age.getCrisp());	}

	public void updateAge(IDistribution<Integer> age_bit) {
		this.age.update(age_bit);
		getBean().setAge(age.getCrisp());
	}



	
	
	
	
	public Double getWeight() {
		return getBean().getWeight();
	}
	
	public void setWeight(Double w) {
	    IDistribution dist = ((LinguisticImperfectField<Weight,Double>) body).fuzzify(w);
		body.setValue(dist,false);
        getBean().setBody(body.getCrisp());
		
		getBean().setWeight(w);
	}


    public void updateWeight(Double w) {
	    IDistribution dist = ((LinguisticImperfectField<Weight,Double>) body).fuzzify(w);
		body.setValue(dist,true);
        getBean().setBody(body.getCrisp());

		getBean().setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify());
	}
	

	
	
	
	
	
	// Extended getters and setters for field : age

    public IImperfectField<Weight> getBodyHistory() {
		return body;
	}

	public IDistribution<Weight> getBody() {
		return body.getCurrent();
	}

	public Weight getBodyValue() {
		return getBean().getBody();
	}

	public void setBody(Weight val) {
		getBean().setBody(val);
		this.body.setValue(val,false);
		
		getBean().setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify());
	}

    public void updateBody(Weight val) {
		getBean().setBody(val);
		this.body.setValue(val,true);

		getBean().setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify());
	}



	public void setBody(IDistribution<Weight> body_dist) {
		this.body.setValue(body_dist,true);
		getBean().setBody(body.getCrisp());	

        getBean().setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify());
	}


    public void updateBody(IDistribution<Weight> body_bit) {
		this.body.update(body_bit);

        getBean().setWeight(((LinguisticImperfectField<Weight,Double>) body).defuzzify());
    }

	
	
	
	
	
	

    public String toString() {
        return "Bean_Imp : \n"
                + "\t field = " + getField() + "\n"
                + "\t age = " + getAge() + "\n"
                + "\t body = " + getBody() + "\n"
                + "\t weight = " + getWeight() + "\n";
    }



}
