/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.builder.model;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class GraphModelImpl extends ModelImpl implements GraphModel {


    private Graph<Concept, Relation> cgraph = new SparseMultigraph<Concept, Relation>();
    private Set<String> conceptNames = new HashSet<String>();

    GraphModelImpl() {

    }

    public boolean saveAsGraphML(String target) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void display() {



        KKLayout<Concept, Relation> layout = new KKLayout<Concept, Relation>( cgraph );
        layout.setExchangeVertices(true);
        layout.setAdjustForGravity(false);
        layout.setDisconnectedDistanceMultiplier(2.0);
        layout.setLengthFactor(5.0);
        layout.setSize( new Dimension(1800,1000) );
        VisualizationViewer<Concept, Relation> vv = new VisualizationViewer<Concept, Relation>( layout );
        vv.setPreferredSize(new Dimension(1850,1050)); //Sets the viewing area size



        final Stroke sccStroke  = new BasicStroke(5.0f);
        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_BEVEL, 10.0f, null, 0.0f);


        Transformer<Relation, Stroke> edgeStrokeTransformer =
                new Transformer<Relation, Stroke>() {
                    public Stroke transform(Relation rel) {
                        return rel.getProperty().contains("subConceptOf") ? sccStroke : edgeStroke;
                    }
                };
        Transformer<Concept,Paint> vertexPaint = new Transformer<Concept,Paint>() {
            public Paint transform(Concept c) {
                return c.getIri().contains("XMLSchema")  ? Color.GREEN : Color.BLUE;
            }
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
//        EditingModalGraphMouse gm =
//            new EditingModalGraphMouse(vv.getRenderContext(),
//                cgraph.get, cgraph.edgeFactory);
        vv.addKeyListener(gm.getModeKeyListener());

        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }




    public void addRelationEdge( Relation rel ) {
        if ( rel == null ){
            System.err.println( "WARNING : adding null relation/edge to graph");
        }
        Object sub =  getTrait( rel.getSubject() );
        Object obj =  null;
        if ( rel instanceof PropertyRelation ) {
            obj = getTrait( ((PropertyRelation) rel).getTarget().getIri());
        } else {
            obj = getTrait( rel.getObject() );
        }
        if ( obj == null ) {
            // literal aka datatype, most probably
            Concept k = new Concept( rel.getObject(), ((PropertyRelation) rel).getTarget().getName() );
            cgraph.addVertex( k );
            obj = k;
        }
        if ( sub != null && obj != null ) {
            cgraph.addEdge( rel, (Concept) sub, (Concept) obj, EdgeType.DIRECTED );
        } else {
            System.err.println(" WARNING : Broken edge for " + rel.getProperty() + " : sub = " + sub + " | obj = " + obj );
        }

    }



    public void addTrait(String name, Object trait) {
        conceptNames.add( name );

        cgraph.addVertex( (Concept) trait );

    }

    public Object getTrait(String name) {
        Collection<Concept> vertix = cgraph.getVertices();
        for ( Concept con : vertix ) {
            if ( con.getName().equals( name ) || con.getIri().equals( name ) ) {
                return con;
            }
        }
        return null;
    }

    public Set<String> getTraitNames() {
        return conceptNames;
    }


    protected String traitsToString() {
        return cgraph.toString();
    }
}
