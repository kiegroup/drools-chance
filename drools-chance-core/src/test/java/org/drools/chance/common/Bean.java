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

/**
 * Generic Bean with two fields, provided for reference
 */
@Deprecated
public class Bean {


	private String name;


	private Integer age;


    private Weight body;


	private Double weight;

	public Bean (){
		
	}




	public String getName(){
		return name;
	}

	public void setName(String ob){
		name = ob;
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
                "name='" + name + '\'' +
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

