package org.drools.semantics.util.area;

import org.drools.factmodel.traits.TypeHierarchy;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.util.CodedHierarchy;
import org.drools.util.CodedHierarchyImpl;
import org.semanticweb.owlapi.model.IRI;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 3/25/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class AreaNode {

    private static Boolean debugMode = true;
    private BitSet rootBitSet = null;
    private CodedHierarchy<Concept> conHir = null;
    private Set<PropertyRelation> pRelations = Collections.EMPTY_SET;
    private String nodeName = "NotAssigned";
    private boolean hasName = false;
    private Set<Concept> roots = Collections.EMPTY_SET;
    private Set<AreaNode> immediateChilds = new HashSet<AreaNode>();
    private Set<AreaNode> immediateParents = new HashSet<AreaNode>();
    private static int counter = 0;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
        hasName = true;
    }

    public void setRoots(Set<Concept> roots) {
        this.roots = roots;
    }

    public Set<Concept> getRoots() {
        return roots;
    }

    public CodedHierarchy<Concept> getConHir() {
        return conHir;
    }

    @Override

    public int hashCode() {
        return pRelations.hashCode();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AreaNode areaNode = (AreaNode) o;

        if (!pRelations.equals(areaNode.pRelations)) return false;

        return true;
    }

    public boolean equalsFull(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AreaNode an = (AreaNode) obj;

        if(!an.rootBitSet.equals(this.rootBitSet))
            return false;
        if(!an.conHir.equals(this.conHir))
            return false;
        if(!an.pRelations.containsAll(this.pRelations))//? maybe equal is better
            return false;

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n*******AreaNode*******\n");
        if(hasName)
            builder.append("Area Name: "+nodeName+"\n");
        builder.append("PropertyRelations:\n");
        builder.append(pRelations+"\n");
        builder.append("AreaConcepts:\n");
//        if(rootBitSet!=null)
//            builder.append(conHir.descendants(rootBitSet)+"\n\n");
//        else
            builder.append(conHir.getSortedMembers()+"\n");
        builder.append("AreaRoots:\n"+roots+"\n");
        return builder.toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        AreaNode ca = new AreaNode(this.pRelations);
        ca.rootBitSet = this.rootBitSet;
        ca.nodeName = this.nodeName;
        ca.conHir = this.conHir;
        ca.nodeName = this.nodeName;
        ca.hasName = this.hasName;
        ca.roots = new HashSet<Concept>(this.roots);
        ca.immediateChilds = new HashSet<AreaNode>(this.immediateChilds);
        ca.immediateParents= new HashSet<AreaNode>(this.immediateParents);
        return ca;
    }

    public AreaNode(Set<PropertyRelation> pRelations) {
        this.pRelations = new HashSet<PropertyRelation>(pRelations);
        conHir = new CodedHierarchyImpl<Concept>();
        if(debugMode)
            setNodeName(Integer.toString(++counter));
    }

    public void addConcept(Concept concept, BitSet code){
        if(conHir==null){
            conHir = new CodedHierarchyImpl<Concept>();
        }

        conHir.addMember(concept,code);
        updateRootBitSet(code);

    }

    public BitSet updateRootBitSet(BitSet code){
        if ( rootBitSet == null ) {
            rootBitSet = (BitSet) code.clone();
        } else {
            rootBitSet.and(code);
        }
        return rootBitSet;
    }

    public BitSet getRootBitSet() {
        return rootBitSet;
    }

    protected void setRootBitSet(BitSet rootBitSet) {
        this.rootBitSet = rootBitSet;
    }

    public Set<AreaNode> getImmediateChilds() {
        return immediateChilds;
    }

    public void setImmediateChilds(Set<AreaNode> immediateChilds) {
        this.immediateChilds = immediateChilds;
    }

    public Set<AreaNode> getImmediateParents() {
        return immediateParents;
    }

    public void setImmediateParents(Set<AreaNode> immediateParents) {
        this.immediateParents = immediateParents;
    }
}
