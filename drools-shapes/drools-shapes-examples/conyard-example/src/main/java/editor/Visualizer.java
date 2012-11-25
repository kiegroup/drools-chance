package editor;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import com.clarkparsia.empire.annotation.RdfGenerator;
import com.clarkparsia.openrdf.ExtGraph;
import com.clarkparsia.openrdf.Graphs;
import org.drools.owl.conyard.ConstructionYardImpl;
import org.drools.owl.conyard.Painting;
import org.drools.owl.conyard.PaintingImpl;
import org.drools.owl.conyard.Site;
import org.drools.owl.conyard.SiteImpl;
import org.drools.owl.conyard.Stair;
import org.drools.owl.conyard.StairImpl;
import org.drools.owl.conyard.WeldingTorch;
import org.drools.owl.conyard.WeldingTorchImpl;
import org.drools.semantics.Thing;
import org.drools.semantics.util.editor.FactGraphAnalyzer;
import org.openrdf.rio.RDFFormat;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.DataShapeAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.ToolTipControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Visualizer extends JFrame {

    public static final String graph = "graph";
    public static final String nodes = "graph.nodes";
    public static final String edges = "graph.edges";
    public static final String AGGR = "aggregates";


    private Graph model;
    private Object object;

    public Visualizer( Thing object ) {
        this.object = object;
        model = new FactGraphAnalyzer().analyzeObject( object );

        try {
            org.openrdf.model.Graph tripples = RdfGenerator.asRdf(object);
            ExtGraph ext = Graphs.extend( tripples );
            ext.write( System.out, RDFFormat.TURTLE );
        } catch (InvalidRdfException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public static void main( String[] arg ) {
        Visualizer viz = new Visualizer( createObject() );
        viz.showFrame();
    }


    void showFrame() {
//        UILib.setPlatformLookAndFeel();
        this.getContentPane().add( new JScrollPane( demo( model ) ) );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.pack();
        this.setVisible( true );
    }


    public JComponent demo( final Graph g ) {

        // create a new, empty visualization for our data
        final Visualization vis = new Visualization();
        VisualGraph vg = vis.addGraph( graph, g );
        vis.setValue( edges, null, VisualItem.INTERACTIVE, Boolean.TRUE );

        TupleSet focusGroup = vis.getGroup( Visualization.FOCUS_ITEMS );
        focusGroup.addTupleSetListener( new TupleSetListener() {
            public void tupleSetChanged( TupleSet ts, Tuple[] add, Tuple[] rem )
            {
                for ( int i=0; i<rem.length; ++i ) {
                    if ( vis.isInGroup( (VisualItem) rem[i], nodes ) ) {
                        ( (VisualItem) rem[i] ).setFixed( false );
                    }
                }
                for ( int i=0; i<add.length; ++i ) {
                    ( (VisualItem) add[i] ).setFixed( false );
                    ( (VisualItem) add[i] ).setFixed( true );
                }
                vis.run( "draw" );
            }
        });

        // set up the renderers
        DefaultRendererFactory factory = new DefaultRendererFactory();

        factory.setDefaultRenderer( new ShapeRenderer( 20 ) {
            public void render( Graphics2D graphics2D, VisualItem visualItem ) {
                super.render( graphics2D, visualItem );

                if ( visualItem.getGroup().equals( nodes ) ) {
                    graphics2D.setColor( Color.black );
                    String lab = (String) visualItem.get( "label" );
                    if ( lab != null ) {
                        graphics2D.drawString( lab,
                                (float) visualItem.getBounds().getMinX(),
                                (float) visualItem.getBounds().getCenterY() );
                    }
                }
            }

        } );

        EdgeRenderer edger = new EdgeRenderer( Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_FORWARD ) {

            @Override
            public void render( Graphics2D graphics2D, VisualItem visualItem ) {
                super.render( graphics2D, visualItem );
                graphics2D.setFont( visualItem.getFont() );
                graphics2D.setColor( Color.black );
                String lab = (String) visualItem.get( "label" );
                if ( lab != null ) {
                    graphics2D.drawString( lab, (float) visualItem.getBounds().getMinX(), (float) visualItem.getBounds().getCenterY() );
                }
            }

        };
        edger.setArrowHeadSize( 40, 40 );

        factory.setDefaultEdgeRenderer( edger );


        Renderer polyR = new PolygonRenderer( Constants.POLY_TYPE_CURVE );
        ( (PolygonRenderer) polyR ).setCurveSlack( 0.05f );

        factory.add( "ingroup('aggregates')", polyR );

        vis.setRendererFactory( factory );





        // -- set up the actions ----------------------------------------------

        int maxhops = 20, hops = 5;
        final GraphDistanceFilter filter = new GraphDistanceFilter( graph, hops );

        ActionList draw = new ActionList();
        draw.add( filter );
        draw.add( new ColorAction( nodes, VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255) ) );
        draw.add( new ColorAction( nodes, VisualItem.STROKECOLOR, 0) );
        draw.add( new ColorAction( nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0) ) );
        draw.add( new ColorAction( edges, VisualItem.FILLCOLOR, ColorLib.gray(200) ) );
        draw.add( new ColorAction( edges, VisualItem.STROKECOLOR, ColorLib.gray(100) ) );

        draw.add( new DataShapeAction( nodes, "type" ) );



        ColorAction aStroke = new ColorAction(AGGR, VisualItem.STROKECOLOR);
        aStroke.setDefaultColor(ColorLib.gray(200));
        aStroke.add("_hover", ColorLib.rgb(255,100,100));

        int[] palette = new int[] {
                ColorLib.rgba(255,200,0,25)
        };
        ColorAction aFill = new DataColorAction( AGGR, "id",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette );

        draw.add( aStroke );
        draw.add( aFill );





        ColorAction fill = new ColorAction(nodes,
                VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
        fill.add("_fixed", ColorLib.rgb(255,100,100));
        fill.add("_highlight", ColorLib.rgb(255,200,125));






        AggregateTable at = vis.addAggregates(AGGR);

        at.addColumn( VisualItem.POLYGON, float[].class );
        at.addColumn( "id", int.class );
        // add nodes to aggregates
        Iterator<Node> nodes = vg.nodes();
        Map<Integer, AggregateItem> aggTable = new HashMap<Integer, AggregateItem>();
        while ( nodes.hasNext() ) {
            Node node = nodes.next();
            if ( node.get( "aggId" ) != null ) {
                int aggId = (Integer) node.get( "aggId" );
                if ( ! aggTable.containsKey( aggId ) ) {
                    AggregateItem agg = (AggregateItem) at.addItem();
                    agg.set( "id", aggId );
                    aggTable.put( aggId, agg );
                }
                aggTable.get( aggId ).addItem( (VisualItem) node );
            }
        }

        ForceDirectedLayout fdl = new ForceDirectedLayout( graph );
        ForceSimulator fsim = fdl.getForceSimulator();
        fsim.getForces()[0].setParameter(0, -10f);
        fsim.getForces()[0].setParameter( 1, 250f );
        fsim.getForces()[0].setParameter(2, -0.1f);
        fsim.getForces()[1].setParameter( 0, 0.025f );
        fsim.getForces()[2].setParameter(0, 1e-4f);
        fsim.getForces()[2].setParameter(1, 150f);
//        fsim.getForces()[0].setParameter(6, 2);


        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add( fdl );
        animate.add( new AggregateLayout( AGGR ) );
        animate.add( aFill );
        animate.add( fill );
        animate.add(new RepaintAction());

        ActionList repaint = new ActionList();
        repaint.add( new RepaintAction() );

        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        vis.putAction( "draw", draw );
        vis.putAction( "layout", animate );
        vis.putAction( "repaint", repaint );
        vis.runAfter( "draw", "layout" );


        // --------------------------------------------------------------------
        // STEP 4: set up a display to show the visualization

        Display display = new Display(vis);
        display.setSize(1024,768);
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);

        // main display controls
        display.addControlListener( new FocusControl( 1 ) {
            @Override
            public void itemClicked( VisualItem visualItem, MouseEvent mouseEvent ) {
                if ( visualItem.getGroup().contains( AGGR ) || visualItem.getGroup().contains( edges ) ) {
                    return;
                }
                super.itemClicked( visualItem, mouseEvent );
            }
        });
        display.addControlListener( new DragControl() {
            @Override
            public void itemDragged( VisualItem visualItem, MouseEvent mouseEvent ) {
                if ( visualItem.getGroup().contains( AGGR ) ) {
                    return;
                }
                super.itemDragged( visualItem, mouseEvent );    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        display.addControlListener( new PanControl() {
            @Override
            public void itemDragged( VisualItem visualItem, MouseEvent mouseEvent ) {
                if ( visualItem.getGroup().contains( AGGR ) ) {
                    return;
                }
                super.itemDragged( visualItem, mouseEvent );    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        display.addControlListener( new ZoomControl() {
            @Override
            public void itemDragged( VisualItem visualItem, MouseEvent mouseEvent ) {
                if ( visualItem.getGroup().contains( AGGR ) ) {
                    return;
                }
                super.itemDragged( visualItem, mouseEvent );    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        display.addControlListener( new WheelZoomControl() {
            @Override
            public void itemDragged( VisualItem visualItem, MouseEvent mouseEvent ) {
                if ( visualItem.getGroup().contains( AGGR ) ) {
                    return;
                }
                super.itemDragged( visualItem, mouseEvent );    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        display.addControlListener( new ZoomToFitControl() {
            @Override
            public void itemDragged( VisualItem visualItem, MouseEvent mouseEvent ) {
                if ( visualItem.getGroup().contains( AGGR ) ) {
                    return;
                }
                super.itemDragged( visualItem, mouseEvent );    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        display.addControlListener( new NeighborHighlightControl() {
            @Override
            public void itemDragged( VisualItem visualItem, MouseEvent mouseEvent ) {
                if ( visualItem.getGroup().contains( AGGR ) ) {
                    return;
                }
                super.itemDragged( visualItem, mouseEvent );    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        display.addControlListener( new ToolTipControl( "iri" ) {
            @Override
            public void itemDragged( VisualItem visualItem, MouseEvent mouseEvent ) {
                if ( visualItem.getGroup().contains( AGGR ) ) {
                    return;
                }
                super.itemDragged( visualItem, mouseEvent );    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
//
        EditControlAdapter editor = new EditControlAdapter( g );
        display.addControlListener( editor );


        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);





        // --------------------------------------------------------------------
        // STEP 5: launching the visualization

        // create a panel for editing force values
        final JForcePanel fpanel = new JForcePanel(fsim);

        final JValueSlider slider = new JValueSlider( "Distance", 0, maxhops, hops );
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                filter.setDistance( slider.getValue().intValue() );
                vis.run( "draw" );
            }
        });
        slider.setBackground( Color.WHITE );
        slider.setPreferredSize( new Dimension( 300,30 ) );
        slider.setMaximumSize( new Dimension( 300,30 ) );

        Box cf = new Box(BoxLayout.Y_AXIS);
        cf.add( slider );
        cf.setBorder( BorderFactory.createTitledBorder( "Connectivity Filter" ) );
        fpanel.add( cf );

        fpanel.add( Box.createVerticalGlue() );



        // create a new JSplitPane to present the interface
        JSplitPane split = new JSplitPane();
        split.setLeftComponent( display );
        split.setRightComponent( fpanel );
        split.setOneTouchExpandable( true );
        split.setContinuousLayout( false );
        split.setDividerLocation( 0.75 );

        JPanel main = new JPanel();
        main.setLayout( new BorderLayout() );
        main.add( split, BorderLayout.CENTER );
        main.add( editor.getPanel(), BorderLayout.PAGE_END );



        // position and fix the default focus node
        NodeItem focus = (NodeItem)vg.getNode(0);
        PrefuseLib.setX(focus, null, 400);
        PrefuseLib.setY(focus, null, 250);
        focusGroup.setTuple(focus);

        // now we run our action list and return
        return main;
    }

    private static Thing createObject() {


        Painting paint = new PaintingImpl();
        paint.addEndsOn( new Date() );
        paint.addHasComment( "This is a test !" );

        paint.addOid( "123" );

        Site site = new SiteImpl();
        site.addLocatedAt( new ConstructionYardImpl() );
        site.addHasNote( "Do not come here!" );
        site.addHasNote( "This is dangerous!" );

        ((PaintingImpl) paint).getEndsOn();



        Stair stair = new StairImpl();
        stair.setStairLength( 30 );
        stair.setOid( "st1" );
//        stair.setStoredInSite( site );
        paint.addRequires( stair );

        WeldingTorch torch = new WeldingTorchImpl();
        torch.setStoredInSite( site );
        torch.setOid( "tor2" );
        paint.addRequires( torch );

        return paint;

    }






    /**
     * Layout algorithm that computes a convex hull surrounding
     * aggregate items and saves it in the "_polygon" field.
     */
    static class AggregateLayout extends Layout {

        private int m_margin = 5; // convex hull pixel margin
        private double[] m_pts;   // buffer for computing convex hulls

        public AggregateLayout(String aggrGroup) {
            super(aggrGroup);
        }

        /**
         */
        public void run(double frac) {

            AggregateTable aggr = (AggregateTable)m_vis.getGroup(m_group);

            synchronized (aggr) {
                // do we have any  to process?
                int num = aggr.getTupleCount();
                if ( num == 0 ) return;

                // update buffers
                int maxsz = 0;
                for ( Iterator aggrs = aggr.tuples(); aggrs.hasNext();  )
                    maxsz = Math.max(maxsz, 4*2*
                            ((AggregateItem)aggrs.next()).getAggregateSize());
                if ( m_pts == null || maxsz > m_pts.length ) {
                    m_pts = new double[maxsz];
                }

                // compute and assign convex hull for each aggregate
                Iterator aggrs = m_vis.visibleItems(m_group);
                while ( aggrs.hasNext() ) {
                    AggregateItem aitem = (AggregateItem)aggrs.next();

                    int idx = 0;
                    if ( aitem.getAggregateSize() == 0 ) continue;
                    VisualItem item = null;
                    Iterator iter = aitem.items();
                    while ( iter.hasNext() ) {
                        item = (VisualItem)iter.next();
                        if ( item.isVisible() ) {
                            addPoint(m_pts, idx, item, m_margin);
                            idx += 2*4;
                        }
                    }
                    // if no aggregates are visible, do nothing
                    if ( idx == 0 ) continue;

                    // compute convex hull
                    double[] nhull = GraphicsLib.convexHull( m_pts, idx );

                    // prepare viz attribute array
                    float[]  fhull = (float[])aitem.get(VisualItem.POLYGON);
                    if ( fhull == null || fhull.length < nhull.length )
                        fhull = new float[nhull.length];
                    else if ( fhull.length > nhull.length )
                        fhull[nhull.length] = Float.NaN;

                    // copy hull values
                    for ( int j=0; j<nhull.length; j++ )
                        fhull[j] = (float)nhull[j];
                    aitem.set(VisualItem.POLYGON, fhull);
                    aitem.setValidated(false); // force invalidation
                }
            }
        }

        private static void addPoint(double[] pts, int idx,
                                     VisualItem item, int growth)
        {
            Rectangle2D b = item.getBounds();
            double minX = (b.getMinX())-growth, minY = (b.getMinY())-growth;
            double maxX = (b.getMaxX())+growth, maxY = (b.getMaxY())+growth;
            pts[idx]   = minX; pts[idx+1] = minY;
            pts[idx+2] = minX; pts[idx+3] = maxY;
            pts[idx+4] = maxX; pts[idx+5] = minY;
            pts[idx+6] = maxX; pts[idx+7] = maxY;
        }

    } // end of class AggregateLayout



} 