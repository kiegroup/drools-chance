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

import de.lab4inf.fuzzy.FuzzySet;
import de.lab4inf.fuzzy.polygons.FuzzyTrapez;
import de.lab4inf.fuzzy.polygons.FuzzyTriangle;
import org.drools.chance.distribution.fuzzy.linguistic.Linguistic;

public enum Price implements Linguistic<Integer> {

	INEXPENSIVE("inexpensive", new FuzzyTrapez(0,0,20,30)),

	CHEAP("cheap", new FuzzyTriangle(20,30,40)),

    REASONABLE("reasonable", new FuzzyTriangle(30,40,50)),

    EXPENSIVE("expensive", new FuzzyTriangle(40,50,60)),

    BLOODY_HELL("bloody_hell", new FuzzyTrapez(50,60,100,100));





	private final String label;
	private final FuzzySet set;



	Price(String lab, FuzzySet set) {
		this.label = lab;
		this.set = set;
	}

	public Linguistic parse(String label) {
		return Price.valueOf(label);
	}
	

	public String getLabel() {
		return label;
	}


	public FuzzySet getSet() {
		return set;
	}




	
	

}
