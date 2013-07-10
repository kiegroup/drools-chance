package org.drools.semantics;

public class Literal {

    private Object lit;

    public Literal( Object o ) {
        lit = o;
    }

    public Object getLit() {
        return lit;
    }

    public void setLit( Object lit ) {
        this.lit = lit;
    }

    @Override
    public String toString() {
        return lit != null ? lit.toString() : null;
    }
}
