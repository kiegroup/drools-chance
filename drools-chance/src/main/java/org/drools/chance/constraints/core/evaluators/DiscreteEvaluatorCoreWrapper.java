package org.drools.chance.constraints.core.evaluators;

import org.drools.chance.constraints.core.IConstraintCore;
import org.drools.chance.constraints.core.connectives.IConnectiveCore;
import org.drools.chance.degree.IDegree;
import org.drools.chance.distribution.IDiscreteDomainDistribution;
import org.drools.chance.distribution.IDistribution;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 1/30/11
 * Time: 4:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class DiscreteEvaluatorCoreWrapper implements IConstraintCore {

    private IConstraintCore core;

    private IConnectiveCore and;
    private IConnectiveCore or;

    private IDegree master;


    public DiscreteEvaluatorCoreWrapper(IConstraintCore core, IConnectiveCore and, IConnectiveCore or, IDegree master) {
        this.core = core;
        this.and = and;
        this.or = or;
        this.master = master;
    }

    public IDegree eval(Object left, Object right) {
    	IDistribution dist1 = (IDistribution) left;
    	IDistribution dist2 = (IDistribution) right;
        IDegree acc = master.False();

        Iterator iter1 = ((IDiscreteDomainDistribution) dist1).iterator();
        while (iter1.hasNext()) {
            Iterator iter2 = ((IDiscreteDomainDistribution) dist2).iterator();
            Object x = iter1.next();
            while (iter2.hasNext()) {
                Object y = iter2.next();
                acc = or.eval(acc, and.eval(dist1.getDegree(x),dist2.getDegree(y),core.eval(x,y)));
            }
        }
        return acc;
    }

    public IDegree eval(Object obj) {
        IDistribution dist = (IDistribution) obj;
        IDegree acc = master.False();

        Iterator iter = ((IDiscreteDomainDistribution) dist).iterator();
        while (iter.hasNext()) {
            Object x = iter.next();
            acc = or.eval(acc, and.eval(dist.getDegree(x),core.eval(x)));
        }
        return acc;
    }

    public IDegree eval(Object... objs) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isUnary() {
        return core.isUnary();
    }

    public boolean isBinary() {
        return core.isBinary();
    }

    public boolean isNary() {
        return core.isNary();
    }
}
