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

package org.drools.chance.degree;

@Deprecated
public class ValueDegreePair<T> implements Comparable<ValueDegreePair<T>> {
    private T value;
    private IDegree degree;

    public ValueDegreePair(T value, IDegree degree) {
        this.value = value;
        this.degree = degree;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public IDegree getDegree() {
        return degree;
    }

    public void setDegree(IDegree degree) {
        this.degree = degree;
    }


	public int compareTo(ValueDegreePair<T> o) {
		return this.getDegree().compareTo(o.getDegree());
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueDegreePair that = (ValueDegreePair) o;

        if (degree != null ? !degree.equals(that.degree) : that.degree != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (degree != null ? degree.hashCode() : 0);
        return result;
    }
}