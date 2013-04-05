package org.drools.semantics.util.area;
import com.hp.hpl.jena.shared.uuid.Bits;
import org.drools.semantics.builder.model.Concept;
import org.drools.util.HierarchyEncoder;
import org.drools.util.HierarchyEncoderImpl;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 3/6/13
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartialAreaTxnImpl {
    private Collection<AreaNode> areas;
    private HierarchyEncoder encoder;// = new HierarchyEncoderImpl();
    private HierarchyEncoder encoderArea;// = new HierarchyEncoderImpl();

    public PartialAreaTxnImpl(Collection<AreaNode> areas,HierarchyEncoder encoder, HierarchyEncoder encoderArea ) {
        this.areas = areas;
        this.encoder = encoder;
        this.encoderArea = encoderArea;
    }

    public void makePartialAreas(){
        Iterator<AreaNode> it= areas.iterator();
        AreaNode area;
        while (it.hasNext()){
            area = it.next();
            //area.getRoots().get(0)
        }
    }

    public Set<Concept> getOverlappingCodes(){
        Set<Concept> overlapping = new HashSet<Concept>();
        Iterator<AreaNode> it = areas.iterator();
        AreaNode area;
        while (it.hasNext()){
            area = it.next();
            System.out.println("Overlapping in "+area.getNodeName()+":");
            for(Concept cct :area.getConHir().lowerDescendants(area.getRootBitSet())){
                if(cct.getSuperConcepts().size()>1){
                    int numParents = 0;
                    for(Concept root :area.getRoots()){
                        BitSet bs = new BitSet();
                        bs = (BitSet)root.getTypeCode().clone();
                        bs.and(cct.getTypeCode());
                        if(root.getTypeCode().equals(bs))
                            numParents++;
                        if(numParents>1){
                            overlapping.add(cct);
                            System.out.println("\t"+cct);
                            break;
                        }
                    }
                }
            }
            System.out.println("------------");
        }
        return overlapping;
    }
}
