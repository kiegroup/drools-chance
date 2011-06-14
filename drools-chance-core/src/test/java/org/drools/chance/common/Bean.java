package org.drools.chance.common;


/**
 * Generic Bean with two fields, provided for reference
 */
public class Bean {



	private String _field;

	private Integer age;
	
	
	private Weight body;
	
	private Double weight;
	
	

	public Bean (){
		
	}



	public String getField(){
		return _field;
	}

	public void setField(String ob){
		_field=ob;
	}


	public Integer getAge(){
		return age;
	}

	public void setAge(Integer x){
		age=x;
	}


		
	
    @Override
    public String toString() {
        return "Bean{" +
                "_field='" + _field + '\'' +
                ", age=" + age +
                ", body=" + body +
                ", weight=" + weight +
                '}';
    }



	public Weight getBody() {
		return body;
	}



	public void setBody(Weight body) {
		this.body = body;
	}



	public Double getWeight() {
		return weight;
	}



	public void setWeight(Double weight) {
		this.weight = weight;
	}
}

