package org.drools.semantics.util.area;

import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.util.HierarchyEncoder;
import org.drools.util.HierarchyEncoderImpl;
import org.drools.util.HierarchySorter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
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

    private HierarchyEncoder<AreaNode<C,P>> encoderArea = new HierarchyEncoderImpl<AreaNode<C,P>>();

    private Map<Set<P>, AreaNode<C,P>> areas;

    public abstract List<C> getInElements();

    public abstract Set<P> getInKeys();



    public Map<Set<P>, AreaNode<C,P>> getAreas() {
        return areas;
    }

    public Collection<Set<P>> getAreaKeys() {
        return areas.keySet();
    }

    public HierarchyEncoder<AreaNode<C,P>> getEncoderArea() {
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


//    //not used
//    public Set<Concept> getArea( Set<P> relations ) {
//
//        Set<Concept> area = new HashSet<Concept>();
//        int eNum = relations.size();
//
//        for( Concept cct : getInElements ) {
//            Set<PropertyRelation> pRel = new HashSet<PropertyRelation>( cct.getProperties().values() );
//            //System.out.println("pRel: "+pRel);
//            if( pRel.size() != eNum )
//                continue;
//            int mNum = 0;
//
//            for( PropertyRelation prRel : relations )
//            {
//                boolean included = false;
//                for( PropertyRelation pr : pRel )
//                {
//                    if( prRel.equals( pr ) ) {
//                        mNum++;
//                        pRel.remove(pr);
//                        included = true;
//                        break;
//                    }
//                }
//                if( ! included )
//                    break;
//            }
//            if( mNum == eNum )
//                area.add(cct);
//        }
//
//        return area;
//    }




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

    public void getOverlappedConcepts() {

    }

    public void getBaseOverlappingRoots() {

    }

    protected abstract Map<Set<P>, AreaNode<C,P>> initNodes();

    public void makeAreaNodes() {
        areas = initNodes();
    }


    public Map<AreaNode<C,P>, Set<C>> makeAreaRoots() {
        if ( areas==null || areas.isEmpty() ) {
            return null;
        }
        Map<AreaNode<C,P>,Set<C>> roots = new HashMap<AreaNode<C,P>, Set<C>>( areas.size() );

        Iterator<AreaNode<C,P>> it = areas.values().iterator();
        AreaNode node = null;
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


    public HierarchyEncoder<AreaNode<C,P>> makeAreaNodeHierarchy() {
        Map<AreaNode<C,P>,Collection<AreaNode<C,P>>> hir = new HashMap<AreaNode<C,P>, Collection<AreaNode<C,P>>>( areas.size() );


        for( AreaNode<C,P> child : areas.values() ) {
            Collection<AreaNode<C,P>> parents = new ArrayList<AreaNode<C,P>>();
            for( AreaNode<C,P> node : areas.values() )  {
                if ( child != node && child.getKeys().containsAll( node.getKeys() ) ) {
                    parents.add( node );
                }
            }
            hir.put( child, parents );
        }
        //Map<AreaNode,Collection<AreaNode>> hir//
        //hir.key is an area node and hir.value is a collection of its parents//
        
        HierarchySorter<AreaNode<C,P>> hSorter = new HierarchySorter<AreaNode<C,P>>();
        List<AreaNode<C,P>> sorted = hSorter.sort( hir );
        
        if( showDebugInfo ) {
            System.out.println("\nChild-of relations:");
        }
        
        //note: assumed that the root has an empty set as its parents: Collections.EMPTY_LIST
        for( AreaNode<C,P> node : sorted ) {
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
}
