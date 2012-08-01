package org.drools.semantics.util.editor;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectDescriptor {

    public static enum NODETYPE { ROOT, DATA, OBJECT }

    private Object object;
    private NODETYPE type;
    private String label;

    private List<RelationDescriptor> outRelations = new ArrayList<RelationDescriptor>();
    private List<RelationDescriptor> inRelations = new ArrayList<RelationDescriptor>();

    private Map<String, RelationDescriptor> relations = new HashMap<String, RelationDescriptor>();

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public NODETYPE getType() {
        return type;
    }

    public void setType(NODETYPE type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<RelationDescriptor> getOutRelations() {
        return outRelations;
    }

    public void setOutRelations(List<RelationDescriptor> outRelations) {
        this.outRelations = outRelations;
    }

    public List<RelationDescriptor> getInRelations() {
        return inRelations;
    }

    public void setInRelations(List<RelationDescriptor> inRelations) {
        this.inRelations = inRelations;
    }

    public void addInRelation( RelationDescriptor descr ) {
        inRelations.add( descr );
    }

    public void addOutRelation( RelationDescriptor descr ) {
        outRelations.add( descr );
    }

    public Map<String, RelationDescriptor> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, RelationDescriptor> relations) {
        this.relations = relations;
    }

    public void addRelation( String name, RelationDescriptor type ) {
        relations.put( name, type );
    }

    @Override
    public String toString() {
        return "ObjectDescriptor{" +
                "label='" + label + '\'' +
                '}';
    }
}
