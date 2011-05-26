/*
 * Copyright 2009 Solnet Solutions Limited (http://www.solnetsolutions.co.nz/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.informer.xml.event;

import org.drools.event.rule.ObjectUpdatedEvent;

/**
 * @author Damon Horrell
 */
public class ObjectUpdatedEventMock extends WorkingMemoryEventMock implements ObjectUpdatedEvent {

	private Object object;

	private Object oldObject;

	public ObjectUpdatedEventMock(String factHandle, Object object, Object oldObject) {
		super(factHandle);
		this.object = object;
		this.oldObject = oldObject;
	}

	/**
	 * @see org.drools.event.rule.ObjectInsertedEvent#getObject()
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @see org.drools.event.rule.ObjectUpdatedEvent#getOldObject()
	 */
	public Object getOldObject() {
		return oldObject;
	}

}
