package org.drools.chance.constraints.core;

import org.drools.chance.degree.IDegree;

/**
 * Created by IntelliJ IDEA.
 * User: doncat
 * Date: 25/01/11
 * Time: 19.43
 * To change this template use File | Settings | File Templates.
 */
public interface IConstraintCore {

    IDegree eval(Object left,Object right);
    IDegree eval(Object obj);
    IDegree eval(Object... objs);

    boolean isUnary();
    boolean isBinary();
    boolean isNary();


}
