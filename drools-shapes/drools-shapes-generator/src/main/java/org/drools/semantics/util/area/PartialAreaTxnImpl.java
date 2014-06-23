package org.drools.semantics.util.area;

import org.drools.util.HierarchyEncoder;

import java.util.*;


public abstract class PartialAreaTxnImpl <C,P>{

    public static boolean showDebugInfo = false;

    private Collection<AreaNode<C,P>> areas;

    private HierarchyEncoder encoderArea;

    public void initAreas( Collection<AreaNode<C,P>> areas, HierarchyEncoder encoderArea ) {
        this.areas = areas;
        this.encoderArea = encoderArea;
    }

    //must be override in the child class
    public abstract BitSet getBitSet(C c);
    public abstract int getNumberOfSupperElm(C c);

    public Map<AreaNode<C,P>, Set<C>> getOverlappingCodes() {

        Map<AreaNode<C,P>, Set<C>> overlapping = new HashMap<AreaNode<C, P>, Set<C>>(areas.size());
        Iterator<AreaNode<C,P>> it = areas.iterator();
        AreaNode<C,P> area;

        while (it.hasNext()){
            area = it.next();
            if(showDebugInfo)
                System.out.println( "Overlapping in " + area.getNodeName() + ":" );

            for( C cct : area.getElements() ) {

                if(getNumberOfSupperElm(cct)>1)
                {
                    int numParents = 0;
                    Set<C> oRoots = new HashSet<C>();

                    for( C root : area.getRoots() ) {
                        BitSet bs = new BitSet();
                        bs = (BitSet)getBitSet(root).clone();
                        bs.and( getBitSet(cct) );

                        if( getBitSet(root).equals( bs ) ) {
                            numParents++;
                        }

                        if( numParents > 1 ) {
                            oRoots.add(cct);
                            if(showDebugInfo)
                                System.out.println(cct);
                            break;
                        }
                    }
                    overlapping.put(area, oRoots );
                }

            }
            if(showDebugInfo)
                System.out.println("------------");
        }
        return overlapping;
    }

}
