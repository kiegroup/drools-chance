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

package org.drools.chance.lang.descr;

import org.drools.lang.descr.TypeFieldDescr;


/**
 * TODO Integrate with TypeFieldDescr,
 *
 */
public class ImperfectTypeFieldDescr extends TypeFieldDescr {

    private int _history;
	private String _name;
    private String _type;


   public ImperfectTypeFieldDescr(int history,String name,String type){
    	_history=history;
    	_name=name;
    	_type=type;
    }


   public String getInternalTypeName(){
	   return _type;
   }




   public String getName(){
	   return _name;
   }

   public int getHistory(){
	   return _history;
   }

}
