package org.drools.chance.common.alternative;

import java.util.HashMap;

import org.drools.chance.common.ImperfectHistoryField;
import org.drools.common.DefaultFactHandle;

/**
 * Alternative implementation
 * Stores the additional fields and metadata in a map
 * instead of dynamically generated fields
 * @author doncat
 *
 */
@Deprecated
public class GenericHandler extends DefaultFactHandle {

	private HashMap <String,ImperfectHistoryField<?>> dynamicFields = new HashMap <String,ImperfectHistoryField<?>> ();



}
