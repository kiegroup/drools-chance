package org.drools.compiler.lang.api;

import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
import org.drools.compiler.lang.descr.ExpectationDescr;

public interface ECEConditionalElementDescrBuilder<P extends DescrBuilder< ? , ? >,T extends AnnotatedBaseDescr>
        extends CEDescrBuilder<P,T> {

    public ECEDescrBuilder expect();
}
