package org.drools.chance.constraints.core.connectives;

import org.drools.chance.constraints.core.IConstraintCore;

/**
 * Created by IntelliJ IDEA.
 * User: doncat
 * Date: 27/01/11
 * Time: 16.08
 * To change this template use File | Settings | File Templates.
 */
public interface IConnectiveFactory {

    IConstraintCore getAnd();
    IConstraintCore getAnd(String type);
    IConstraintCore getAnd(String type, Object... params);


    IConstraintCore getOr();
    IConstraintCore getOr(String type);
    IConstraintCore getOr(String type, Object... params);


    IConstraintCore getNot();
    IConstraintCore getNot(String type);
    IConstraintCore getNot(String type, Object... params);


    IConstraintCore getXor();
    IConstraintCore getXor(String type);
    IConstraintCore getXor(String type, Object... params);


    IConstraintCore getEquiv();
    IConstraintCore getEquiv(String type);
    IConstraintCore getEquiv(String type, Object... params);


    IConstraintCore getImplies();
    IConstraintCore getImplies(String type);
    IConstraintCore getImplies(String type, Object... params);





}
