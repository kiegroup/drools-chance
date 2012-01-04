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

package org.drools.informer.util;

import org.drools.definition.type.Position;


public class Ans {

    @Position(0)
    private String value;

    @Position(1)
    private String type;

    @Position(2)
    private String qId;


    public Ans() { }

    public Ans(String value, String type, String qId) {
        this.value = value;
        this.type = type;
        this.qId = qId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ans ans = (Ans) o;

        if (qId != null ? !qId.equals(ans.qId) : ans.qId != null) return false;
        if (type != null ? !type.equals(ans.type) : ans.type != null) return false;
        if (value != null ? !value.equals(ans.value) : ans.value != null) return false;

        return true;
    }

    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (qId != null ? qId.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Ans{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", qId='" + qId + '\'' +
                '}';
    }
}
