package org.drools.semantics.util.area;

import org.drools.semantics.builder.model.Concept;
import org.drools.util.HierarchyEncoder;
import org.drools.util.HierarchyEncoderImpl;
import org.drools.util.HierarchySorter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AreaTxnImpl<C,P> implements AreaTxn<C,P> {

    public static boolean showDebugInfo = false;

    private HierarchyEncoder<Area<C,P>> encoderArea = new HierarchyEncoderImpl<Area<C,P>>();

    private Map<Set<P>, Area<C,P>> areas;

    public abstract List<C> getInElements();

    public abstract Set<P> getInKeys();

    public Collection<Set<P>> getAreaKeys() {
        return areas.keySet();
    }

    public HierarchyEncoder<Area<C,P>> getEncoderArea() {
        return encoderArea;
    }

    //not used
    public Collection<C> getElements( Set<P> keys ) {
        return areas.get( keys ).getElements();
    }

    public boolean hasArea( Set<P> keys ) {
        return areas.containsKey( keys );
    }

    public Area<C,P> getArea( Set<P> keys ) {
        return areas.get( keys );
    }

    public Map<Integer,HierarchyEncoder<Concept>> getPartialArea(Map<Integer, Set<Concept>> areas, Map<Integer, Set<Concept>> roots) {
        HierarchyEncoder<Concept> encoder = null;
        Map<Integer,HierarchyEncoder<Concept>> partialArea = new HashMap<Integer, HierarchyEncoder<Concept>>();

        for( int i = 0; i < areas.size(); i++ ) {
            Set<Concept> area = areas.get( i );
            for( Concept root: roots.get( i ) ) {
                encoder = new HierarchyEncoderImpl<Concept>();
                encoder.encode( root,Collections.EMPTY_LIST );
                for ( Concept subcon: root.getSubConcepts() ) {
                    if ( area.contains( subcon ) ) {
                        encoder.encode( subcon,Arrays.asList( root ) );
                    }
                }
            }
            partialArea.put( i, encoder );
        }
        return partialArea;
    }

    protected abstract Map<Set<P>, Area<C,P>> initNodes();

    public void makeAreaNodes() {
        areas = initNodes();
    }

    public Map<Area<C,P>, Set<C>> makeAreaRoots() {
        if ( areas==null || areas.isEmpty() ) {
            return null;
        }
        Map<Area<C,P>,Set<C>> roots = new HashMap<Area<C,P>, Set<C>>( areas.size() );

        Iterator<Area<C,P>> it = areas.values().iterator();
        Area node = null;
        while( it.hasNext() ) {
            node = it.next();
            Set<C> rb = new HashSet<C>(
                node.getConHir().lowerBorder( node.getRootBitSet() )
            );

            roots.put( node, rb );
            node.setRoots( rb );
        }

        return roots;
    }

    public HierarchyEncoder<Area<C,P>> makeAreaNodeHierarchy() {
        Map<Area<C,P>,Collection<Area<C,P>>> hir = new HashMap<Area<C,P>, Collection<Area<C,P>>>( areas.size() );


        for( Area<C,P> child : areas.values() ) {
            Collection<Area<C,P>> parents = new ArrayList<Area<C,P>>();
            for( Area<C,P> node : areas.values() )  {
                if ( child != node && child.getKeys().containsAll( node.getKeys() ) ) {
                    parents.add( node );
                }
            }
            hir.put( child, parents );
        }
        //Map<AreaNode,Collection<AreaNode>> hir//
        //hir.key is an area node and hir.value is a collection of its parents//

        HierarchySorter<Area<C,P>> hSorter = new HierarchySorter<Area<C,P>>();
        List<Area<C,P>> sorted = hSorter.sort( hir );
        
        if( showDebugInfo ) {
            System.out.println("\nChild-of relations:");
        }
        
        //note: assumed that the root has an empty set as its parents: Collections.EMPTY_LIST
        for( Area<C,P> node : sorted ) {
            node.setAreaCode( encoderArea.encode( node, hir.get( node ) ) );
            if( showDebugInfo ) {
                System.out.println( node + "\n-->\n" + node.getImmediateParents() + "\n%%---%%" );
            }
        }
        return encoderArea;
    }

    protected void buildAreas() {
        makeAreaNodes();
        makeAreaRoots();
        makeAreaNodeHierarchy();
    }

//    public abstract BitSet getBitSet(C c);
//    public abstract int getNumberOfSupperElm(C c);

//    public Map<Area<C,P>, Set<C>> getOverlappingElements() {
//
//        Collection<Area<C,P>> areas;
//        areas = this.areas.values();
//
//        Map<Area<C,P>, Set<C>> overlapping = new HashMap<Area<C, P>, Set<C>>(areas.size());
//        Iterator<Area<C,P>> it = areas.iterator();
//        Area<C,P> area;
//
//        while (it.hasNext()){
//            area = it.next();
//            Set<C> oConcepts = new HashSet<C>();
////            if(showDebugInfo)
//                System.out.println( "Overlapping in " + area.getNodeName() + ":" );
//
//            for( C cct : area.getElements() ) {
//
//                if(getNumberOfSupperElm(cct)>1)
//                {
//                    int numParents = 0;
//
//                    for( C root : area.getRoots() ) {
//                        BitSet bs = new BitSet();
//                        bs = (BitSet)getBitSet(root).clone();
//                        bs.and( getBitSet(cct) );
//
//                        if( getBitSet(root).equals( bs ) ) {
//                            numParents++;
//                        }
//
//                        if( numParents > 1 ) {
//                            oConcepts.add(cct);
////                            if(showDebugInfo)
//                            if(numParents==2)
//                                System.out.println(cct);
////                            break;
//                        }
//                    }
//                    if(numParents>1)
//                        overlapping.put(area, oConcepts );
//                }
//
//            }
//            if(showDebugInfo)
//                System.out.println("------------");
//        }
//        return overlapping;
//    }

    //added by mh
    public Collection<Area<C,P>> getAreas(){
        return areas.values();
    }
}
