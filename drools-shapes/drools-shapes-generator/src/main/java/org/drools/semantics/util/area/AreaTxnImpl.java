package org.drools.semantics.util.area;
//import org.drools.factmodel.traits.TypeHierarchy;
import org.apache.commons.collections15.collection.CompositeCollection;
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryImpl;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.semantics.util.HierarchySorter;
import org.drools.util.CodedHierarchy;
import org.drools.util.HierarchyEncoder;
import org.drools.util.HierarchyEncoderImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 3/6/13
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class AreaTxnImpl<T> implements AreaTxn<T>{

    public static boolean showDebugInfo = false;

    public List<Concept> getInConcepts() {
        return inConcepts;
    }

    public Set<PropertyRelation> getInProRelations() {
        return inProRelations;
    }

    public OntoModel getInModel() {
        return inModel;
    }

    private List<Concept> inConcepts = null;
    private Set<PropertyRelation> inProRelations = null;
    private OntoModel inModel = null;
    private HierarchyEncoder encoder = new HierarchyEncoderImpl();

    public Collection<AreaNode> getAreas() {
        return areas;
    }

    public HierarchyEncoder getEncoder() {
        return encoder;
    }

    private Collection<AreaNode> areas;
    private HierarchyEncoder encoderArea = new HierarchyEncoderImpl();

    public HierarchyEncoder<AreaNode> getEncoderArea() {
        return encoderArea;
    }

    public AreaTxnImpl(String modelName, String[] owlFileNames) throws IOException {


        if(owlFileNames == null | owlFileNames.length==0)
            throw new IOException("no input file!");

        Resource rss[] = new Resource[owlFileNames.length];
        for(int i=0; i<owlFileNames.length; i++)
        {
            try {
                if((new File(this.getClass().getResource(owlFileNames[i]).toURI())).exists())
                    rss[i] = new ClassPathResource(owlFileNames[i]);
                else
                    throw new IOException("File: <"+owlFileNames[i]+"> is not reachable!");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        OntoModel model = DLFactoryImpl.getInstance().buildModel(
                modelName,
                rss,
                OntoModel.Mode.FLAT,
                DLFactory.liteAxiomGenerators );

        inConcepts = model.getConcepts();
        inProRelations = model.getProperties();
        this.inModel = model;

        //this will generate the bitset code for all the concepts based on their hierarchy map
        generateHierarchyEncoder(inConcepts);


        if(showDebugInfo)
            System.out.print("Model:\n"+ model );

        System.out.println("<PROPERTY RELATIONS>: ");
        //if(showDebugInfo)
        for ( PropertyRelation p : model.getProperties() ) {
            if ( ! p.isRestricted() ) {
                System.out.println( p );
                System.out.println( "\t " + ( p.getDomain().isAnonymous() ? p.getDomain().getSubConcepts() : p.getDomain() ) );
                //ToDo: need to be fixed in OntoModel: cases that DataProperties have no range should be assigned to RootThing
                if(p.getTarget()!=null)
                System.out.println( "\t " + ( p.getTarget().isAnonymous() ? p.getTarget().getSubConcepts() : p.getTarget() ) );
            }
        }
        System.out.println("END OF <PROPERTY RELATIONS>" );

        if(showDebugInfo)
        for ( Concept c : model.getConcepts() ) {
            if ( ! c.isAnonymous() ) {
                System.out.println( c );
            }
        }
    }

    public AreaTxnImpl(List<Concept> inConcepts, Set<PropertyRelation> props) {
        this.inConcepts = inConcepts;
        inProRelations = props;
        generateHierarchyEncoder(inConcepts);
    }

    public AreaTxnImpl(OntoModel inModel) {
        this.inModel = inModel;
        this.inConcepts = inModel.getConcepts();
        this.inProRelations = inModel.getProperties();
        generateHierarchyEncoder(inConcepts);
    }

    public HierarchyEncoder generateHierarchyEncoder(List<Concept> nodes){
        //it is assumed that all the concepts are ordered based on the hierarchy from top to bottom
        nodes.get(0).setTypeCode(encoder.encode(nodes.get(0),Collections.EMPTY_LIST));
        Set<Concept> sc;
        for(int i=1; i<nodes.size(); i++){
            if(!nodes.get(i).isAnonymous()) {
                sc = new HashSet<Concept>(nodes.get(i).getSuperConcepts());
                for(Concept cct:nodes.get(i).getSuperConcepts())
                    if(cct.isAnonymous())
                        sc.remove(cct);
                if(sc.isEmpty())
                    sc = Collections.EMPTY_SET;
                    nodes.get(i).setTypeCode(encoder.encode(nodes.get(i), sc));
//            encoder.encode(nodes.get(i), nodes.get(i).getSuperConcepts());
            }
        }
        return encoder;
    }

    //not used
    public Set<Concept> getArea(Collection<PropertyRelation> relations){

        Set<Concept> area = new HashSet<Concept>();
        int eNum = relations.size();

        for(Concept cct :inConcepts)
        {
            Set<PropertyRelation> pRel = new HashSet<PropertyRelation>(cct.getProperties().values());
            //System.out.println("pRel: "+pRel);
            if(pRel.size()!=eNum)
                continue;
            int mNum = 0;

            for(PropertyRelation prRel :relations)
            {
                boolean included = false;
                for(PropertyRelation pr :pRel)
                {
                    if(prRel.equals(pr)){
                        mNum++;
                        pRel.remove(pr);
                        included = true;
                        break;
                    }
                }
                if(!included)
                    break;
            }
            if(mNum==eNum)
                area.add(cct);
        }

        return area;
    }

    @Override
    public String toString() {
        return super.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public Map<Integer,HierarchyEncoder<Concept>> getPartialArea(Map<Integer, Set<Concept>> areas, Map<Integer, Set<Concept>> roots) {
        HierarchyEncoder<Concept> encoder = null;
        Map<Integer,HierarchyEncoder<Concept>> partialArea = new HashMap<Integer, HierarchyEncoder<Concept>>();

        for(Integer i=0; i<areas.size(); i++){
            Set<Concept> area = areas.get(i);
            for(Concept root: roots.get(i)){
                encoder = new HierarchyEncoderImpl<Concept>();
                encoder.encode(root,Collections.EMPTY_LIST);
                for(Concept subcon: root.getSubConcepts()){
                    if(area.contains(subcon)){
                        encoder.encode(subcon,Arrays.asList(root));
                    }
                }
            }
            partialArea.put(i,encoder);
            System.out.println(i+" partial="+encoder);
        }
        return partialArea;
    }

    public void getOverlappedConcepts() {

    }

    public void getBaseOverlappingRoots() {

    }

    public void makeAreaNodes(){

        Map< Set<PropertyRelation>, AreaNode> areaNodes = new HashMap<Set<PropertyRelation>, AreaNode>();

        for(Concept cct:inConcepts) {
            if(!cct.isAnonymous())
            {
                Set<PropertyRelation> hs = new HashSet<PropertyRelation>(cct.getProperties().values()); //remove unnecessary pp
                //remove all inferred extra properties
                removeUnneccessaryPropertyRelations(hs);
                if(!areaNodes.containsKey(hs)){//it is a new area so create an empty entry for that
                    areaNodes.put(hs, new AreaNode(hs));
                    System.out.println("HashSet added: "+hs);
                }
                //add the concept to the corresponding area
                areaNodes.get(hs).addConcept(cct,encoder.getCode(cct));
            }
        }

        areas = new ArrayList<AreaNode>( areaNodes.values() );
        //return Collections.unmodifiableCollection(areas);
    }

    public Map<AreaNode, Set<Concept>> makeAreaRoots(){
        if(areas==null || areas.size()==0)
            return null;
        Map<AreaNode,Set<Concept>> roots = new HashMap<AreaNode, Set<Concept>>(areas.size());

        Iterator<AreaNode> it = areas.iterator();
        AreaNode node = null;
        while(it.hasNext()){
            node = it.next();
            Set<Concept> rb = new HashSet<Concept>(

            node.getConHir().lowerBorder( node.getRootBitSet() ));

            roots.put(node,rb);
            node.setRoots(rb);
            System.out.println("Roots: "+rb);
        }

        makeAreaNodeHierarchy();
        return roots;
    }

    //todo: code this part to remove all RootThings related properties
    private void removeUnneccessaryPropertyRelations(Set<PropertyRelation> hs){
        //to avoid ConcurrentModificationException
        Set<PropertyRelation> cpy = new HashSet<PropertyRelation>(hs);
        Iterator<PropertyRelation> it = cpy.iterator();

        while(it.hasNext()){
            PropertyRelation pr = it.next();
            if(pr.isRestricted())//?
                    hs.remove(pr);
        }
    }

    private HierarchyEncoder<AreaNode> makeAreaNodeHierarchy(){
        Map<AreaNode,Collection<AreaNode>> hir = new HashMap<AreaNode, Collection<AreaNode>>(areas.size());
        BitSet bs = new BitSet();

        for(AreaNode child:areas){
            Collection<AreaNode> parents = new ArrayList<AreaNode>();
            for(AreaNode node:areas)  {
                if(!child.equals(node)){
                    bs = (BitSet)node.getRootBitSet().clone();
                    bs.and(child.getRootBitSet());
                    if(node.getRootBitSet().equals(bs)){
                        parents.add(node);
                        node.getImmediateChilds().add(child);
                        child.getImmediateParents().add(node);
                    }
                }
            }
            hir.put(child,parents);
        }
        //Map<AreaNode,Collection<AreaNode>> hir//
        //hir.key is an area node and hir.value is a collection of its parents//
        List<AreaNode> sorted;
        HierarchySorter<AreaNode> hSorter = new HierarchySorter<AreaNode>();
        sorted = hSorter.sort(hir);
        if(showDebugInfo)
        System.out.println("\nChild-of relations:");
        //note: assumed that the root has an empty set as its parents: Collections.EMPTY_LIST
        for(AreaNode node:sorted){
            encoderArea.encode(node,hir.get(node));
            if(showDebugInfo)
            System.out.println(node+"\n-->\n"+node.getImmediateParents()+"\n%%---%%");

        }
        return encoderArea;
    }
}
