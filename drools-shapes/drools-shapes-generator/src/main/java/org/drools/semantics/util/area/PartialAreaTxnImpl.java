package org.drools.semantics.util.area;

import org.drools.core.util.HierarchyEncoder;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.PropertyRelation;

import java.util.Collection;


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
//                        bs = (BitSet) root._getTypeCode().clone();
//                        bs.and( cct._getTypeCode() );
//
//                        if( root._getTypeCode().equals( bs ) ) {
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
