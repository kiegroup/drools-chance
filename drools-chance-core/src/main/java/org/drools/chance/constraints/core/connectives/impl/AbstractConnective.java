package org.drools.chance.constraints.core.connectives.impl;

import org.drools.chance.constraints.core.connectives.IConnectiveCore;
import org.drools.chance.degree.IDegree;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 1/29/11
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractConnective implements IConnectiveCore {

    public abstract LOGICCONNECTIVES getType();

    public abstract IDegree eval(IDegree deg);
    public abstract IDegree eval(IDegree left, IDegree right);
    public abstract IDegree eval(IDegree... degs);

    public abstract boolean isUnary();
    public abstract boolean isBinary();
    public abstract boolean isNary();


    public IDegree eval(Object left,Object right) {
        if (left instanceof IDegree && right instanceof IDegree) {
            return eval((IDegree) left, (IDegree) right);
        } else {
            throw new UnsupportedOperationException("Trying to aggregate " + left.getClass()
                        + " and " + right.getClass() + " into a Degree" );
        }
    }

    public IDegree eval(Object obj) {
        if (obj instanceof IDegree) {
            return eval((IDegree) obj);
        } else {
            throw new UnsupportedOperationException("Trying to use " + obj.getClass() + " as Degree");
        }
    }

    public IDegree eval(Object... objs) {
        for (Object o : objs) {
            if (! (o instanceof IDegree))
                throw new UnsupportedOperationException("Trying to use " + o.getClass() + " as Degree");
        }
        return eval((IDegree[]) objs);
    }




}
