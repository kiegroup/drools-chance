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
package org.drools.informer.domain.questionnaire.framework;

import org.drools.informer.domain.questionnaire.conditions.ConditionClause;
import org.drools.informer.MultipleChoiceQuestion;
import org.drools.informer.domain.questionnaire.conditions.ConditionClause;

/**
 * Holds the actual value and displayed value (representation) for a list item
 * (used in {@link MultipleChoiceQuestion}. Note: representation is optional.
 * 
 * There is an optional {@link ConditionClause} that allows one line of condition
 * so that this list entry is only conditionally displayed. Note: use an Impact 
 * (TohuDataItemObject) to control cases when more than one clause required - the multiple
 * clauses are then on the creation of the Impact.
 *
 * @author Derek Rendall
 */
public class ListEntryTuple {
	private String id;
	private String representation;
	private ConditionClause clause;
	
	public ListEntryTuple(String id) {
		this(id, null, null);
	}
	public ListEntryTuple(String id, ConditionClause clause) {
		this(id, null, clause);
	}
	public ListEntryTuple(String id, String representation) {
		this(id, representation, null);
	}
	
	public ListEntryTuple(String id, String representation, ConditionClause clause) {
		super();
		this.id = id;
		this.representation = representation;
		this.clause = clause;
	}
	
	public String getId() {
		return id;
	}
	
	public String getRepresentation() {
		return representation;
	}
	
	public ConditionClause getConditionClause() {
		return clause;
	}
	
}

