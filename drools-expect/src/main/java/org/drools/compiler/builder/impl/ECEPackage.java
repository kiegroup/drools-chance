package org.drools.compiler.builder.impl;

import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceTypePackage;

public class ECEPackage implements ResourceTypePackage {

    @Override
    public ResourceType getResourceType() {
        return ECE.ECE;
    }

}
