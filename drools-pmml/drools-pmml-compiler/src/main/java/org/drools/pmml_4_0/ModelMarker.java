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

package org.drools.pmml_4_0;


import org.drools.definition.type.Position;

public class ModelMarker {

    @Position(0)
    private String modelName;

    @Position(1)
    private String modelClass;

    @Position(2)
    private String modelUrl;



    public ModelMarker() { }

    public ModelMarker(String modelName, String modelClass) {
        this.modelName = modelName;
        this.modelClass = modelClass;
    }

    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelMarker that = (ModelMarker) o;

        if (modelClass != null ? !modelClass.equals(that.modelClass) : that.modelClass != null) return false;
        if (modelName != null ? !modelName.equals(that.modelName) : that.modelName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = modelName != null ? modelName.hashCode() : 0;
        result = 31 * result + (modelClass != null ? modelClass.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ModelMarker{" +
                ", modelName=" + modelName +
                ", modelClass='" + modelClass + '\'' +
                ", modelUrl='" + modelUrl + '\'' +
                '}';
    }
}
