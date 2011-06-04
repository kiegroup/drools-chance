package org.drools.informer;

import org.drools.io.Resource;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/3/11
 * Time: 11:04 PM
 */
public class ResourceHolder {

    private String id;
    private String target;
    private Resource res;


    public ResourceHolder(String id, String target, Resource res) {
        this.id = id;
        this.target = target;
        this.res = res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceHolder that = (ResourceHolder) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Resource getRes() {
        return res;
    }

    public void setRes(Resource res) {
        this.res = res;
    }
}
