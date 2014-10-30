package org.drools.compiler.builder.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.internal.weaver.KieWeaverService;

public class ExpectWeaverService implements KieWeaverService<ECEPackage> {

    @Override
    public ResourceType getResourceType() {
        return ECE.ECE;
    }

    @Override
    public Class getServiceInterface() {
        return KieWeaverService.class;
    }

    @Override
    public void merge( KieBase kieBase, KiePackage kiePkg, ECEPackage rtPkg ) {
        System.out.println( "Weaver Merge!!" );
    }

    @Override
    public void weave( KieBase kieBase, KiePackage kiePkg, ECEPackage rtPkg ) {
        System.out.println( "Weaver Weave!!" );
    }

}
