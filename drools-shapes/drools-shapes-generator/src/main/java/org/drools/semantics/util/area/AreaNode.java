package org.drools.semantics.util.area;

import org.drools.util.CodedHierarchy;
import org.drools.util.CodedHierarchyImpl;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AreaNode<C,P> implements Area<C,P> {

    private static Boolean debugMode = true;

    private BitSet rootBitSet = null;
    private CodedHierarchy<C> conHir = null;
    private Set<P> pRelations = Collections.EMPTY_SET;

    private String nodeName = "NotAssigned";
    private boolean hasName = false;

    private Set<C> roots = Collections.EMPTY_SET;
    private Set<AreaNode<C,P>> immediateChilds = new HashSet<AreaNode<C,P>>();
    private Set<AreaNode<C,P>> immediateParents = new HashSet<AreaNode<C,P>>();

    private BitSet areaCode;

    private static int counter = 0;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName( String nodeName ) {
        this.nodeName = nodeName;
        hasName = true;
    }

    public void setRoots( Set<C> roots ) {
        this.roots = roots;
    }

    public Set<C> getRoots() {
        return roots;
    }

    public BitSet getElementRootCode() {
        return rootBitSet;
    }

    public Collection<C> getElements() {
        return Collections.unmodifiableCollection( conHir.getSortedMembers() );
    }

    public CodedHierarchy<C> getConHir() {
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

        AreaNode<C,P> areaNode = (AreaNode<C,P>) o;

        if (!pRelations.equals(areaNode.pRelations)) return false;

        return true;
    }

    public boolean equalsFull(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AreaNode<C,P> an = (AreaNode<C,P>) obj;

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
        builder.append( "\n*******AreaNode*******\n" );
        if( hasName )
            builder.append( "Area Name: "+nodeName+"\n" );
        builder.append( "PropertyRelations:\n" );
        builder.append( pRelations + "\n" );
        builder.append( "AreaTs:\n" );
//        if(rootBitSet!=null)
//            builder.append(conHir.descendants(rootBitSet)+"\n\n");
//        else
        builder.append( conHir.getSortedMembers() + "\n" );
        builder.append( "AreaRoots:\n" + roots + "\n" );
        return builder.toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        AreaNode<C,P> ca = new AreaNode<C,P>( this.pRelations );
        ca.rootBitSet = this.rootBitSet;
        ca.nodeName = this.nodeName;
        ca.conHir = this.conHir;
        ca.nodeName = this.nodeName;
        ca.hasName = this.hasName;
        ca.roots = new HashSet<C>( this.roots );
        ca.immediateChilds = new HashSet<AreaNode<C,P>>( this.immediateChilds );
        ca.immediateParents= new HashSet<AreaNode<C,P>>( this.immediateParents );
        return ca;
    }

    public AreaNode( Set<P> pRelations ) {
        this.pRelations = new HashSet<P>( pRelations );
        conHir = new CodedHierarchyImpl<C>();
        if( debugMode ) {
            setNodeName( Integer.toString( ++counter ) );
        }
    }

    public void addElement(C concept, BitSet code){
        if( conHir == null ) {
            conHir = new CodedHierarchyImpl<C>();
        }

        conHir.addMember( concept, code );
        updateRootBitSet( code );
    }

    public BitSet updateRootBitSet( BitSet code ){
        if ( rootBitSet == null ) {
            rootBitSet = (BitSet) code.clone();
        } else {
            rootBitSet.and( code );
        }
        return rootBitSet;
    }

    public BitSet getRootBitSet() {
        return rootBitSet;
    }

    protected void setRootBitSet( BitSet rootBitSet ) {
        this.rootBitSet = rootBitSet;
    }

    public Set<AreaNode<C,P>> getImmediateChilds() {
        return immediateChilds;
    }

    public void setImmediateChilds( Set<AreaNode<C,P>> immediateChilds ) {
        this.immediateChilds = immediateChilds;
    }

    public Set<AreaNode<C,P>> getImmediateParents() {
        return immediateParents;
    }

    public void setImmediateParents( Set<AreaNode<C,P>> immediateParents ) {
        this.immediateParents = immediateParents;
    }

    public BitSet getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(BitSet areaCode) {
        this.areaCode = areaCode;
    }

    public Set<P> getKeys() {
        return pRelations;
    }
}
