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

public class PropertyRelation extends Relation {

    @Position(3)
    protected String name;

    private Integer minCard = 0;
    private Integer maxCard = null;
    private Concept target = null;

    private boolean restricted = false;
    private PropertyRelation baseProperty;

    public PropertyRelation( String subject, String property, String object, String name ) {
        this.subject = subject;
        this.property = property;
        this.object = object;
        this.name = name;
        baseProperty = this;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getMinCard() {
        return minCard;
    }

    public void setMinCard(Integer minCard) {
        this.minCard = minCard;
    }

    public Integer getMaxCard() {
        return maxCard;
    }

    public void setMaxCard(Integer maxCard) {
        this.maxCard = maxCard;
    }


    public Concept getTarget() {
        return target;
    }

    public void setTarget(Concept target) {
        this.target = target;
    }

    public String toString() {
        return name;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }


    public PropertyRelation getBaseProperty() {
        if ( ! isRestricted() ) {
            return this;
        } else {
            return baseProperty.getBaseProperty();
        }
    }

    public void setBaseProperty(PropertyRelation baseProperty) {
        this.baseProperty = baseProperty;
    }

    public String toFullString() {
        return "Property{" +
                "subject='" + subject + '\'' +
                ", property='" + property + '\'' +
                ", object='" + object + '\'' +
                ", name='" + name + '\'' +
                "," + minCard  +" .. " + maxCard +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyRelation that = (PropertyRelation) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
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
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}


