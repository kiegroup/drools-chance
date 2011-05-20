package org.drools.chance.distribution.fuzzy.linguistic;

import de.lab4inf.fuzzy.FuzzySet;

public interface ILinguistic<K> {
		
	public String getLabel();
	
	public FuzzySet getSet();
	
	public ILinguistic parse(String label);

}
