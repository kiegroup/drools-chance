package org.drools.semantics.util.area;

import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.util.HierarchyEncoder;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class PartialAreaTxnImpl {
    private Collection<AreaNode<Concept,PropertyRelation>> areas;

    private HierarchyEncoder encoderArea;
//
//    public PartialAreaTxnImpl( Collection<AreaNode> areas, HierarchyEncoder encoderArea ) {
//        this.areas = areas;
//        this.encoderArea = encoderArea;
//    }
//
//    public void makePartialAreas() {
//        Iterator<AreaNode> it = areas.iterator();
//        AreaNode area;
//        while ( it.hasNext() ){
//            area = it.next();
//            //area.getRoots().get(0)
//        }
//    }
//
//    public Set<Concept> getOverlappingCodes() {
//        Set<Concept> overlapping = new HashSet<Concept>();
//        Iterator<AreaNode> it = areas.iterator();
//        AreaNode area;
//
//        while (it.hasNext()){
//            area = it.next();
//            System.out.println( "Overlapping in " + area.getNodeName() + ":" );
//
//            Collection<Concept> descs = area.getConHir().lowerDescendants( area.getRootBitSet() );
//            for( Concept cct : descs ) {
//
//                if( cct.getSuperConcepts().size() > 1 ) {
//                    int numParents = 0;
//
//                    for( Concept root : area.getRoots() ) {
//                        BitSet bs = new BitSet();
//                        bs = (BitSet) root.getTypeCode().clone();
//                        bs.and( cct.getTypeCode() );
//
//                        if( root.getTypeCode().equals( bs ) ) {
//                            numParents++;
//                        }
//
//                        if( numParents > 1 ) {
//                            overlapping.add( cct );
//                            break;
//                        }
//                    }
//                }
//            }
//            System.out.println("------------");
//        }
//        return overlapping;
//    }
}
