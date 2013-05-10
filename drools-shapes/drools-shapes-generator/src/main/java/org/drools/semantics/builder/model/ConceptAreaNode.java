package org.drools.semantics.builder.model;

import org.drools.semantics.util.area.Area;
import org.drools.semantics.util.area.AreaNode;
import org.drools.util.CodedHierarchyImpl;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 5/8/13
 * Time: 5:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptAreaNode extends AreaNode<Concept,PropertyRelation> {


    @Override
    public BitSet getBitSet(Concept o) {
        return o.getTypeCode();
    }

    @Override
    public int getNumberOfSupperElm(Concept o) {
        return o.getSuperConcepts().size();
    }


    @Override
    protected Object clone() {
        ConceptAreaNode ca = new ConceptAreaNode(this.getKeys());
        fillClone( ca );
        return ca;
    }

    public ConceptAreaNode(Set<PropertyRelation> relations) {
        super(relations);
    }

}
