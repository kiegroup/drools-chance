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
