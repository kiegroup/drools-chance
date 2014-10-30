package org.drools.compiler.lang;

import org.drools.compiler.lang.api.ECEPackageDescrBuilder;
import org.drools.compiler.lang.api.ECERuleDescrBuilder;
import org.drools.compiler.lang.api.impl.PackageDescrBuilderImpl;
import org.drools.compiler.lang.api.impl.ExpectationRuleDescrBuilderImpl;

/**
 * A builder implementation for PackageDescrs using a fluent API.
 */
public class ECEPackageDescrBuilderImpl extends PackageDescrBuilderImpl
    implements ECEPackageDescrBuilder {

    private ECEPackageDescrBuilderImpl() {
        super();
    }

    public ECERuleDescrBuilder newExpectationRule() {
        ECERuleDescrBuilder rule = new ExpectationRuleDescrBuilderImpl( this );
        descr.addRule( rule.getDescr() );
        return rule;
    }

    public static ECEPackageDescrBuilder newECEPackage() {
        return new ECEPackageDescrBuilderImpl();
    }
}
