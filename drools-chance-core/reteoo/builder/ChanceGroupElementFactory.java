package org.drools.chance.reteoo.builder;


import org.drools.core.rule.GroupElement;

public class ChanceGroupElementFactory {

    private ChanceGroupElementFactory() {
    }

    public static ChanceGroupElement newAndInstance() {
        return new ChanceGroupElement( GroupElement.AND );
    }

    public static ChanceGroupElement newOrInstance() {
        return new ChanceGroupElement( GroupElement.OR );
    }

    public static ChanceGroupElement newNotInstance() {
        return new ChanceGroupElement( GroupElement.NOT );
    }

    public static ChanceGroupElement newExistsInstance() {
        return new ChanceGroupElement( GroupElement.EXISTS );
    }
}
