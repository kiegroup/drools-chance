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

package org.drools.informer.generator;

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
