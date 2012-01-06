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

package org.drools.semantics.builder.model;

import org.drools.definition.type.Position;

public class SubConceptOf extends Relation {


    @Position(3)
    private boolean enabled;



    public SubConceptOf(String subject, String object) {
        this.subject = subject;
        this.object = object;
        this.enabled = true;
        this.property = "subConceptOf";
    }



    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
            return subject + "subConceptOf" + object;
        }


    public String toFullString() {
        return "SubConceptOf{" +
                "subject='" + subject + '\'' +
                ", property='" + property + '\'' +
                ", object='" + object + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubConceptOf that = (SubConceptOf) o;

        if (enabled != that.enabled) return false;
        if (object != null ? !object.equals(that.object) : that.object != null) return false;
        if (property != null ? !property.equals(that.property) : that.property != null) return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }
}


