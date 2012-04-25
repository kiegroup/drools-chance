package org.drools.chance.constraints.core.connectives.impl.product;


import org.drools.chance.constraints.core.connectives.ConnectiveCore;
import org.drools.chance.constraints.core.connectives.impl.LogicConnectives;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;

public class Not implements ConnectiveCore {
    
    public static Not getInstance() {
        return null;
    }
    
    public Degree eval(Degree deg) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Degree eval(Degree left, Degree right) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Degree eval(Degree... degs) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Degree eval(Evaluation... degs) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public LogicConnectives getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Degree eval(Object left, Object right) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Degree eval(Object obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Degree eval(Object... objs) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isUnary() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isBinary() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isNary() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
