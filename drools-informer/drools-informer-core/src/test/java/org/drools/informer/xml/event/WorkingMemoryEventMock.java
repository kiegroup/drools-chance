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
package org.drools.informer.xml.event;

import org.drools.event.rule.WorkingMemoryEvent;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.PropagationContext;

/**
 * @author Damon Horrell
 */
public class WorkingMemoryEventMock implements WorkingMemoryEvent {

	private String factHandle;

	public WorkingMemoryEventMock(String factHandle) {
		this.factHandle = factHandle;
	}

	public FactHandle getFactHandle() {
		return new FactHandle() {
			public String toExternalForm() {
				return factHandle;
			}
		};
	}

	/**
	 * @see org.drools.event.rule.WorkingMemoryEvent#getPropagationContext()
	 */
	public PropagationContext getPropagationContext() {
		return null;
	}

	/**
	 * @see org.drools.event.KnowledgeRuntimeEvent#getKnowledgeRuntime()
	 */
	public KnowledgeRuntime getKnowledgeRuntime() {
		return null;
	}

}
