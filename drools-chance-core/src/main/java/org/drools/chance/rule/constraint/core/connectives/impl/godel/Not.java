package org.drools.chance.rule.constraint.core.connectives.impl.godel;

import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.rule.constraint.core.connectives.impl.LogicConnectives;
import org.drools.chance.degree.Degree;
import org.drools.chance.evaluation.Evaluation;

public class Not implements ConnectiveCore {

    private static Not INSTANCE = new Not();
    
    public static Not getInstance() {
        return INSTANCE;
    }

    public Degree eval( Degree deg ) {
        return ( deg.toBoolean() ? deg.False() : deg.True() );
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
