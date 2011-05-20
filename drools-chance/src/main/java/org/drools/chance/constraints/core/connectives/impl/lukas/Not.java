package org.drools.chance.constraints.core.connectives.impl.lukas;

import org.drools.chance.constraints.core.connectives.impl.AbstractConnective;
import org.drools.chance.constraints.core.connectives.impl.LOGICCONNECTIVES;
import org.drools.chance.degree.IDegree;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 1/29/11
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Not extends AbstractConnective {


    private Not() { }

    private static Not instance = new Not();

    public static Not getInstance() {
        return instance;
    }


    public LOGICCONNECTIVES getType() {
        return LOGICCONNECTIVES.NOT;
    }

    public IDegree eval(IDegree deg) {
        return deg.True().sub(deg);
    }

    public IDegree eval(IDegree left, IDegree right) {
        return eval(left);
    }

    public IDegree eval(IDegree... degs) {
        return eval(degs[0]);
    }

    public boolean isUnary() {
        return true;
    }


    public boolean isBinary() {
        return true;
    }


    public boolean isNary() {
        return true;
    }
}
