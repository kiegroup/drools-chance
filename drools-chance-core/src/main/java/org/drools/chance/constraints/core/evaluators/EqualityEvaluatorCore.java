package org.drools.chance.constraints.core.evaluators;

import org.drools.chance.constraints.core.IConstraintCore;
import org.drools.chance.degree.IDegree;


@Deprecated
public class EqualityEvaluatorCore implements IConstraintCore {

    /**
     * Factory instance, for quick generation of degrees of the desired type
     */
    IDegree master;


    public EqualityEvaluatorCore(IDegree sample) {
        master = sample;
    }

    public IDegree eval(Object left, Object right) {
        return left.equals(right) ? master.True() : master.False();
    }

    public IDegree eval(Object obj) {
        throw new UnsupportedOperationException(" == is not unary ");
    }

    public IDegree eval(Object... objs) {
        throw new UnsupportedOperationException(" == is not "+objs.length+"-ary ");
    }





    public boolean isUnary() {
        return false;
    }

    public boolean isBinary() {
        return true;
    }

    public boolean isNary() {
        return false;
    }
}
