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
package org.drools.informer;

import java.io.Serializable;

/**
 * Base class for all Tohu objects.
 *
 * @author Damon Horrell
 */
public abstract class InformerObject implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;


	private transient boolean active;


    private String type;

    private String context;


    /**
     * Type identifier for this object
     * @return
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    /**
     * Context this item is used in
     * Used when multiple surveys of the same type are required in a single WM
     * @return
     */
    public String getContext() {
        return context;
    }


    public void setContext(String context) {
        this.context = context;
    }

    /**
	 * Unique instance identifier for this object.
	 *
	 * @return
	 */
	public abstract String getId();

	/**
	 * True if this item is active
	 * @return true if this item is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * This is invoked by the Tohu built-in rules. Do not call it directly.
	 *
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

    @Override
    public String toString() {
        return "InformerObject{" +
                "active=" + active +
                ", type='" + type + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
