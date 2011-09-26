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
