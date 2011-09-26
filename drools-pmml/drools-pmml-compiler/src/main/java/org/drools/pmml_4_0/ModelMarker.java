package org.drools.pmml_4_0;


import org.drools.definition.type.Position;
import org.drools.io.Resource;

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
