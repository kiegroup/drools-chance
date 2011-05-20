package org.drools.chance.constraints.core.connectives;

import org.drools.chance.constraints.core.IConstraintCore;
import org.drools.chance.constraints.core.connectives.impl.LOGICCONNECTIVES;
import org.drools.chance.degree.IDegree;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 1/29/11
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IConnectiveCore extends IConstraintCore {

    IDegree eval(IDegree deg);
    IDegree eval(IDegree left, IDegree right);
    IDegree eval(IDegree... degs);


    LOGICCONNECTIVES getType();

}
