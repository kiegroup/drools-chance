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

import de.lab4inf.fuzzy.FuzzySet;
import de.lab4inf.fuzzy.polygons.FuzzyTrapez;
import de.lab4inf.fuzzy.polygons.FuzzyTriangle;
import de.lab4inf.fuzzy.pszshape.FuzzyP;
import de.lab4inf.fuzzy.pszshape.FuzzyS;
import org.drools.chance.distribution.fuzzy.linguistic.ILinguistic;

public enum Weight implements ILinguistic<Double> {
	
	SLIM("slim", new FuzzyTriangle(-100,0,100)),
	
	FAT("fat", new FuzzyTriangle(0,100,200));
	
	

	
	private final String label;
	private final FuzzySet set;
	
	
	
	Weight(String lab, FuzzySet set) {
		this.label = lab;
		this.set = set;
	}

	public ILinguistic parse(String label) {
		return Weight.valueOf(label);
	}
	

	public String getLabel() {
		return label;
	}


	public FuzzySet getSet() {
		return set;
	}




	
	

}
