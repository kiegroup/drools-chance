package org.drools.chance.common;


import java.util.ArrayList;

import org.drools.chance.distribution.IDistribution;
import org.drools.chance.distribution.IDistributionStrategies;


/**
 * This class defines a complex data structure holding the imperfect information
 * regarding a bean's field.
 *
 * There are two dimensions of imperfection: "time" and "space"
 *
 * The former keeps track of the past values of the
 *
 *
 * @param <T>
 *
 * @author sotty, doncat
 */
public class ImperfectHistoryField<T> extends AbstractImperfectField<T> {

    /**
     * current position in the history circular buffer
     */
	private int _index = -1;
    /**
     * maximum size of the history circular buffer
     */
	private int _max;


	private ArrayList<IDistribution<T>> history;

	public ImperfectHistoryField(IDistributionStrategies<T> strategies, int memoryLen){
    	super(strategies);
        _max=memoryLen;
        history = new ArrayList<IDistribution<T>>(memoryLen);        
	}


    public ImperfectHistoryField(IDistributionStrategies<T> strategies, int memoryLen, String val0){
    	super(strategies);
    	_max = memoryLen;
        history = new ArrayList<IDistribution<T>>(memoryLen);
        setValue(strategies.parse(val0));
	}

	public ImperfectHistoryField(IDistributionStrategies<T> strategies, int memoryLen, IDistribution<T> val0){
    	super(strategies);
        _max = memoryLen;
        history = new ArrayList<IDistribution<T>>(memoryLen);
    	setValue(val0,true);
	}


	public void setValue(IDistribution<T> dist, boolean update){
		if (! update) {
			_index = (_index + 1) % _max;
			if(history.size()<_max){
				history.add( _index,dist);

			}
			else
				history.set(_index, dist);
		} else {
            getStrategies().merge(getCurrent(),dist);
		}



	}





	public IDistribution<T> getPast(int time) throws IndexOutOfBoundsException {
		if (time > 0 )
			throw new IndexOutOfBoundsException("Unable to access future values!");

		 int idx=(_index+time) % _max;

		 if (idx < 0) idx = (idx+_max);

		 if (history.size()<=idx)
			 return null;

		 return history.get(idx);
	}


	public IDistribution<T> getCurrent(){
		return history.get(_index);
	}

	public int getSize(){
		return history.size();
	}

    public boolean isSet() {
        return ! history.isEmpty();
    }



}

