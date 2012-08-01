package org.drools.semantics.util.editor;


import java.lang.reflect.Method;

public class RelationDescriptor implements Cloneable {

    public static enum RELTYPE  { OBJECT, DATA };

    private String property;
    private Object subject;
    private Object object;

    private RELTYPE type;
    private Class range;

    private Method setter;
    private Method getter;
    private Method adder;
    private Method remover;

    public RelationDescriptor() {
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getSubject() {
        return subject;
    }

    public void setSubject(Object subject) {
        this.subject = subject;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public RELTYPE getType() {
        return type;
    }

    public void setType(RELTYPE type) {
        this.type = type;
    }

    public Class getRange() {
        return range;
    }

    public void setRange(Class range) {
        this.range = range;
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }

    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    public Method getAdder() {
        return adder;
    }

    public void setAdder(Method adder) {
        this.adder = adder;
    }

    public Method getRemover() {
        return remover;
    }

    public void setRemover(Method remover) {
        this.remover = remover;
    }

    public String toString() {
        return property + "(" + object + ")";
    }

    public String toFullString() {
        return "RelationDescriptor{" +
                "property='" + property + '\'' +
                ", subject=" + subject +
                ", object=" + object +
                ", type=" + type +
                ", range=" + range +
                ", setter=" + setter +
                ", getter=" + getter +
                ", adder=" + adder +
                ", remover=" + remover +
                '}';
    }


    public Object clone() {
        RelationDescriptor other = new RelationDescriptor();
        other.setProperty( getProperty() );
        other.setRange( getRange() );
        other.setType( getType() );
        other.setAdder( getAdder() );
        other.setGetter( getGetter() );
        other.setRemover( getRemover() );
        other.setSetter( getSetter() );
        return other;
    }
}
