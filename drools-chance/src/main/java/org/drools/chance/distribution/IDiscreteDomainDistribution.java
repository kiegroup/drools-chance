package org.drools.chance.distribution;


import org.drools.chance.degree.IDegree;

import java.util.Map;
import java.util.Set;

public interface IDiscreteDomainDistribution<T> extends Iterable<T> {

    
    public Set<T> getSupport();

    public int size();

}
