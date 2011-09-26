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

package org.drools.chance.common.deprecated;

import java.util.LinkedList;

import org.drools.chance.distribution.IDistribution;


@Deprecated
public class StoricoLink<T> {

		private int _index = -1;
		private int _max;

		private LinkedList<IDistribution<T>> _storico;

		public  StoricoLink(int max){
	    	_storico = new LinkedList<IDistribution<T>>();
	    	_max=max;
		}


		public StoricoLink(int max, IDistribution<T> val0){
	    	_storico = new LinkedList<IDistribution<T>>();
	    	_max = max;
	    	setValue(val0);
		}


		public void setValue(IDistribution<T> dist){
			_index = (_index + 1) % _max;
			if(_storico.size()<_max)
				_storico.add(dist);
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



