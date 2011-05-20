

package org.drools.chance.common.deprecated;

import java.util.Vector;

import org.drools.chance.distribution.IDistribution;


@Deprecated
public class StoricoVec<T> {
	private int _index = -1;
	private int _max;

	private Vector<IDistribution<T>> _storico;

	public StoricoVec(int max){
    	_storico = new Vector<IDistribution<T>>(max);
    	_max=max;
	}


	public StoricoVec(int max, IDistribution<T> val0){
    	_storico = new Vector<IDistribution<T>>(max);
    	_storico.setSize(max);
    	_max = max;
    	setValue(val0);
	}


	public void setValue(IDistribution<T> dist){
		_index = (_index + 1) % _max;

		if(_storico.size() < _max ){
			_storico.add( dist );

		}
     else
    	 _storico.set(_index, dist);

	}


	public IDistribution<T> getPast(int time){
		if (time > 0 )
			throw new IndexOutOfBoundsException("Unable to access future values!");

		 int idx=(_index+time) % _max;

		 if (idx < 0) idx = (idx+_max);

		 if (_storico.size()<=idx)
			 return null;

		 return _storico.get(idx);
	}


	public IDistribution<T> getCurrent(){
		return _storico.get(_index);
	}

	public int getSize(){
		return _storico.size();
	}





}