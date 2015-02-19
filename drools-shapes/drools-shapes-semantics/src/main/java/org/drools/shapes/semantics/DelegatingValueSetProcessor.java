package org.drools.shapes.semantics;

import org.hl7.v3.CD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Primary
public class DelegatingValueSetProcessor implements ValueSetProcessor {
	
	@Autowired
	private Set<ValueSetProcessor> delegates;

	@Override
	public boolean inValueSet(String valueSetUri, CD code) {
		for(ValueSetProcessor delegate : this.delegates) {
			if(delegate.inValueSet(valueSetUri, code)) {
				return true;
			}
		}
		
		return false;
	}

}
