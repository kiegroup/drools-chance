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
