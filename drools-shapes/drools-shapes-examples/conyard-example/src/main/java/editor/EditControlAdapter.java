package editor;

import org.drools.semantics.Thing;
import org.drools.semantics.util.editor.FactGraphAnalyzer;
import org.drools.semantics.util.editor.ObjectDescriptor;
import org.drools.semantics.util.editor.RelationDescriptor;
import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.data.*;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualItem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.lang.reflect.Method;
import java.util.*;

public class EditControlAdapter extends ControlAdapter {



    private static class EditGUI {

        private JPanel mainPanel = new JPanel( new BorderLayout() );

        private JTextField currentNode;
        private JComboBox availProps;
        private JPanel cards = new JPanel( new CardLayout() );
        private JPanel dataCard = new JPanel();
        private JPanel objectCard = new JPanel();
        private final EditControlAdapter editor;

        private Tuple currentTuple;


        public JPanel getPanel() {
            return mainPanel;
        }

        public EditGUI( final EditControlAdapter editor ) {
            this.editor = editor;

            Box info = new Box(BoxLayout.Y_AXIS);
            info.setBorder( BorderFactory.createTitledBorder( "Property Editor" ) );

            JPanel inner = new JPanel( new GridLayout( 2, 2 ) );

            inner.add( new JLabel( "Current Node :" ) );
            currentNode = new JTextField();
            currentNode.setEditable( false );
            inner.add( currentNode );


            inner.add( new JLabel( "Available Properties " ) );
            availProps = new JComboBox();
            availProps.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println(" Changed avail Proprs " + availProps.getSelectedItem());
//                    objectCard.removeAll();
                    objectCard.add( editor.initObjectNodeEditor(currentTuple, availProps.getSelectedItem()));
//                    ((CardLayout) cards.getLayout()).show( cards, ((ObjectDescriptor) currentTuple.get( "descr" )).getType().name() );
                    cards.repaint();
                }
            });
            inner.add( availProps );

            info.add( inner );


            cards = new JPanel( new CardLayout() );
            cards.add( new JLabel( "Select a node to edit" ), "NONE" );
            cards.add( dataCard, ObjectDescriptor.NODETYPE.DATA.name() );
            cards.add( objectCard, ObjectDescriptor.NODETYPE.OBJECT.name() );



            dataCard.add( new JLabel( "DAT" ) );
            objectCard.add(new JLabel("OBJ"));

            mainPanel.add(info, BorderLayout.PAGE_START);
            mainPanel.add(cards, BorderLayout.CENTER);

            mainPanel.setVisible(true);
        }

        public void disable() {
            currentNode.setText( "(n/a)" );
            currentNode.setEnabled( false );
            availProps.setModel( new DefaultComboBoxModel() );
            availProps.setEnabled( false );
            ((CardLayout) cards.getLayout()).show( cards, "NONE" );
        }

        public void update( Tuple tuple ) {
            currentTuple = tuple;

            ObjectDescriptor od = (ObjectDescriptor) tuple.get( "descr" );
            String label = (String) tuple.get( "label" );

            currentNode.setEnabled( true );
            currentNode.setText( label );


            DefaultComboBoxModel model = new DefaultComboBoxModel();
            if ( od != null ) {
                for ( RelationDescriptor descr : od.getOutRelations() ) {
                    model.addElement( descr );
                }
            }
            availProps.setModel( model );
            availProps.setEnabled( true );

            String type = od.getType().name();
            ((CardLayout) cards.getLayout()).show( cards, type );
            if ( ObjectDescriptor.NODETYPE.DATA.name().equals(type) ) {
                dataCard.removeAll();
                dataCard.add( editor.initDataNodeEditor(tuple) );
            } else if ( ObjectDescriptor.NODETYPE.OBJECT.name().equals(type) ) {
                objectCard.removeAll();
                objectCard.add( editor.initObjectNodeEditor( currentTuple, availProps.getSelectedItem() ) );
            }

        }
    }




    private Map<Object,Node> cache = new HashMap<Object, Node>();
    private EditGUI gui;
    private RelationDescriptor activeProp;

    private Graph g;

    public EditControlAdapter( Graph g ) {
        this.g = g;

        Table table = g.getNodeTable();
        Iterator<Integer> iter = table.iterator();
        while ( iter.hasNext() ) {
            Integer index = iter.next();
            Tuple tup = table.getTuple( index );
            ObjectDescriptor od = ((ObjectDescriptor) tup.get( "descr" ));
            if ( od != null ) {
                Object o = od.getObject();
                if ( o instanceof Thing ) {
                    cache.put( o, (Node) tup.get( "node" ) );
                }
            }
        }


        gui = new EditGUI( this );

    }


    @Override
    public void itemClicked( VisualItem visualItem, MouseEvent mouseEvent ) {
        if ( visualItem.getGroup().equals( Visualizer.nodes ) ) {
            super.itemClicked( visualItem, mouseEvent );

            if ( mouseEvent.getClickCount() >= 2 ) {
                gui.update(visualItem);
//                initNodeEditor( visualItem );
            } else {
                gui.disable();
            }
        } else {
            gui.disable();
            return;
        }

    }




    private JComponent initDataNodeEditor( final Tuple tuple ) {

        JPanel panel = new JPanel( new GridLayout( 2, 2 ) );
        JComponent input;
        JButton ok = new JButton( "Modify" );

        JButton del = new JButton( "Remove" );



        final ObjectDescriptor od = (ObjectDescriptor) tuple.get( "descr" );
        final RelationDescriptor rd = od.getInRelations().get( 0 );

        final Object oldValue = od.getObject();

        Class valueType = od.getInRelations().get( 0 ).getRange();

        if ( String.class.isAssignableFrom( valueType ) ) {
            final JTextField text = new JTextField();
            text.setText( (String) oldValue );

            input = text;
            ok.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setValue( tuple, od, rd, oldValue, text.getText() );
                    gui.disable();
                }
            });
        } else if ( Number.class.isAssignableFrom( valueType ) ) {
            final JSpinner spin = new JSpinner();
            if ( Long.class.isAssignableFrom( valueType ) ) {
                SpinnerNumberModel num = new SpinnerNumberModel(
                        (Number) oldValue, Long.MIN_VALUE, Long.MAX_VALUE, 1 );
                spin.setModel( num );
            } else if ( Integer.class.isAssignableFrom( valueType ) ) {
                SpinnerNumberModel num = new SpinnerNumberModel(
                        (Number) oldValue, Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
                spin.setModel( num );
            } else {
                SpinnerNumberModel num = new SpinnerNumberModel(
                        (Number) oldValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.01 );
                spin.setModel( num );
            }
            //TODO The others!!!!
            input = spin;
            ok.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setValue( tuple, od, rd, oldValue, spin.getValue() );
                    gui.disable();
                }
            });
        } else if ( Boolean.class.isAssignableFrom( valueType ) ) {
            JRadioButton radio = new JRadioButton();

            input = radio;
            ok.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( e.getActionCommand().equals( "Confirm" ) ) {
                        System.out.println( "Changing the value to !" );
                    }
                }
            });

        } else if ( Date.class.isAssignableFrom( valueType ) ) {
            final JSpinner spin = new JSpinner();
            SpinnerDateModel dat = new SpinnerDateModel(
                    (Date) oldValue,
                    new Date(0),
                    null,
                    Calendar.HOUR );
            spin.setModel( dat );
            input = spin;
            ok.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setValue( tuple, od, rd, oldValue, spin.getValue() );
                    gui.disable();
                }
            });
        } else {
            throw new UnsupportedOperationException( "TODO -- Add support for " + valueType );
        }

        input.setPreferredSize( new Dimension( 250, 30 ) );
        input.setMaximumSize( new Dimension( 500,30 ) );

        del.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                VisualItem vis = (VisualItem) tuple;
                Visualization graph = vis.getVisualization();

                Node node = (Node) tuple.get( "node" );
                Edge inArc = (Edge) node.inEdges().next();

                AggregateTable att = (AggregateTable) graph.getGroup( Visualizer.AGGR );
                synchronized ( att ) {
                    System.out.println( " Removing stuff " );
                    removeProperty( (RelationDescriptor) inArc.get( "descr"), inArc, inArc.getSourceNode(), graph );
                    System.out.println( " Removed stuff " );
                }

            }
        });

        panel.add( input );
        panel.add( ok );
        panel.add( del );

        return panel;

    }



    private Component initObjectNodeEditor( final Tuple currentTuple, final Object selectedItem ) {
        System.out.println(" Reset panel for avail Proprs " + selectedItem);
        activeProp = (RelationDescriptor) selectedItem;
        JPanel panel = new JPanel( new GridLayout( 2, 2 ) );

        JButton remp = new JButton( "Remove Property" );
        remp.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println( "Remove current tuple from " + currentTuple.get( "descr" ) + " prop " + activeProp );
                VisualItem vis = (VisualItem) currentTuple;
                Visualization graph = vis.getVisualization();
                Node currNode = (Node) currentTuple.get( "node" );
                Iterator<Edge> out = currNode.outEdges();
                while ( out.hasNext() ) {
                    Edge edge = out.next();
                    RelationDescriptor rel = (RelationDescriptor) edge.get( "descr" );
                    if ( activeProp.equals( rel ) ) {
                        removeProperty( activeProp, edge, currNode, graph );
                        break;
                    }
                }
                gui.disable();
            }
        });

        JButton remm = new JButton( "Remove Node" );
        remm.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println( "Remove NODE " + currentTuple.get( "descr" ) );

                VisualItem vis = (VisualItem) currentTuple;
                Visualization graph = vis.getVisualization();
                Node currNode = (Node) currentTuple.get( "node" );

                Iterator<Edge> in = currNode.inEdges();
                while ( in.hasNext() ) {
                    Edge edge = in.next();
                    removeProperty( (RelationDescriptor) edge.get( "descr" ), edge, currNode, graph );
                }

                gui.disable();
            }
        });



        Box newP = new Box(BoxLayout.Y_AXIS);
        newP.setBorder( BorderFactory.createTitledBorder( "Add new property" ) );

        final JPanel inner = new JPanel(  );
        final JPanel deeper = new JPanel( new BorderLayout() );
        final ObjectDescriptor od = (ObjectDescriptor) currentTuple.get( "descr" );

        final JButton add = new JButton( "Add" );


        final JComboBox propBox = new JComboBox();
        DefaultComboBoxModel props = new DefaultComboBoxModel();
        for ( String p : od.getRelations().keySet() ) {
            props.addElement( p );
        }
        propBox.setModel( props );
        propBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComponent dataEditor = prepareNewSelection(propBox.getSelectedItem(), od);

                deeper.removeAll();
                deeper.add(dataEditor, BorderLayout.CENTER);
                inner.revalidate();
            }
        });

        add.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
            addRelation( (Node) currentTuple, od, od.getRelations().get( propBox.getSelectedItem() ), newValue );
            gui.disable();
            }
        });


        inner.add( propBox );

        inner.add( deeper );
        if ( propBox.getSelectedItem() != null ) {
            deeper.add( prepareNewSelection( propBox.getSelectedItem(), od ), BorderLayout.CENTER );
        }

        inner.add( add );
        newP.add( inner );


        panel.add( remp );
        panel.add( remm );
        panel.add( newP );

        return panel;
    }




    private void addRelation( Node root, ObjectDescriptor od, RelationDescriptor relationDescriptor, Object newValue ) {
        boolean isObject = Thing.class.isAssignableFrom( newValue.getClass() );
        System.out.println( "Adding on " + od + " using " + relationDescriptor + " setting value " + newValue );
        // if single and already set, need to change the previous value!

        if ( relationDescriptor.getSetter() != null ) {
            Iterator<Edge> outs = root.outEdges();
            while ( outs.hasNext() ) {
                Edge out = outs.next();
                RelationDescriptor prev = (RelationDescriptor) out.get( "descr" );
                if ( prev.getProperty().equals( relationDescriptor.getProperty() ) ) {
                    try {
                        prev.getSetter().invoke( prev.getSubject(), newValue );
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    prev.setObject( newValue );
                    Node tgt = out.getTargetNode();
                    tgt.set( "label", newValue.toString() );
                    ((ObjectDescriptor) tgt.get( "descr" )).setLabel(newValue.toString());
                    ((ObjectDescriptor) tgt.get( "descr" )).setObject( newValue );
                    return;
                }
            }
        }


        RelationDescriptor rd = (RelationDescriptor) relationDescriptor.clone();
        rd.setSubject( od.getObject() );
        rd.setObject( newValue );
        rd.setType( isObject ? RelationDescriptor.RELTYPE.OBJECT : RelationDescriptor.RELTYPE.DATA );
        od.addOutRelation( rd );

        try {
            rd.getAdder().invoke( od.getObject(), newValue );
        } catch (Exception e) {
            e.printStackTrace();
        }


        VisualItem viz = (VisualItem) root;
        AggregateTable agg = (AggregateTable) viz.getVisualization().getVisualGroup( Visualizer.AGGR );


        Iterator<Tuple> nodes = g.getNodes().tuples();
        while ( nodes.hasNext() ) {
            Tuple n = nodes.next();
            if ( n.canGet( "descr", ObjectDescriptor.class ) ) {
                ObjectDescriptor ox = (ObjectDescriptor) n.get( "descr" );

                if ( ox.getObject() == newValue ) {
                    Edge arc = g.addEdge( (Node) root.get( "node") , (Node) n.get( "node" ) );
                    arc.set( "label", rd.getProperty() );
                    arc.set( "iri", rd.getProperty() );
                    arc.set( "edge", arc );
                    arc.set( "descr", rd );
                    return;
                }
            }
        }


        synchronized ( agg ) {
            Node target = g.addNode();

            ObjectDescriptor td = new ObjectDescriptor();
              td.addInRelation( rd );
              td.setType( isObject ? ObjectDescriptor.NODETYPE.OBJECT : ObjectDescriptor.NODETYPE.DATA );
              td.setObject( newValue );
              if ( isObject ) {
                  td.setRelations( FactGraphAnalyzer.extractRelations( newValue ) );
              }


            int aggId = isObject? agg.getRowCount() : (Integer) root.get( "aggId" );

            target.set( "aggId", aggId );
            target.set( "label", newValue.toString() );
            target.set( "node", target );
            target.set( "descr", td );
            target.set( "type", isObject ? ObjectDescriptor.NODETYPE.OBJECT : ObjectDescriptor.NODETYPE.DATA );

            VisualItem tiz = viz.getVisualization().getVisualItem( Visualizer.nodes, target );

            if ( isObject ) {
                AggregateItem ai = (AggregateItem) agg.addItem();
                ai.set( "id", aggId );
                ai.addItem( tiz );
            } else {
                agg.addToAggregate( (Integer) root.get( "aggId" ), tiz );
            }

            Edge arc = g.addEdge( (Node) root.get( "node") , target );
            arc.set( "label", rd.getProperty() );
            arc.set( "iri", rd.getProperty() );
            arc.set( "edge", arc );
            arc.set( "descr", rd );

        }


    }



    private Object newValue;

    private JComponent prepareNewSelection( Object selectedItem, ObjectDescriptor od ) {
        RelationDescriptor rd = od.getRelations().get( selectedItem );
        JComponent input = new JLabel( "Not supported yet" );
        if ( rd.getType().equals( RelationDescriptor.RELTYPE.DATA ) ) {

            if ( String.class.isAssignableFrom( rd.getRange() ) ) {
                final JTextField text = new JTextField();
                input = text;
                text.addFocusListener( new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        newValue = text.getText();
                    }
                });
            } else if ( Number.class.isAssignableFrom( rd.getRange() ) ) {
                final JSpinner spin = new JSpinner();
                if ( Long.class.isAssignableFrom( rd.getRange() ) ) {
                    SpinnerNumberModel num = new SpinnerNumberModel(
                            0, Long.MIN_VALUE, Long.MAX_VALUE, 1 );
                    spin.setModel( num );
                } else if ( Integer.class.isAssignableFrom( rd.getRange() ) ) {
                    SpinnerNumberModel num = new SpinnerNumberModel(
                            0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
                    spin.setModel( num );
                } else {
                    SpinnerNumberModel num = new SpinnerNumberModel(
                            0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.01 );
                    spin.setModel( num );
                }
                input = spin;
                spin.addChangeListener( new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        newValue = spin.getValue();
                    }
                });

            } else if ( Date.class.isAssignableFrom( rd.getRange() ) ) {
                final JSpinner spin = new JSpinner();
                SpinnerDateModel dat = new SpinnerDateModel(
                        new Date(),
                        new Date(0),
                        null,
                        Calendar.HOUR );
                spin.setModel( dat );
                input = spin;
                spin.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        newValue = spin.getValue();
                    }
                });

            }

            input.setPreferredSize( new Dimension( 200, 30 ) );
            input.addVetoableChangeListener( new VetoableChangeListener() {
                public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                    // TODO Restrictions might be placed here
                }
            });

        } else {

            final CardLayout cardLayout = new CardLayout();

            final JPanel chooser = new JPanel( );
            final JPanel selector = new JPanel( cardLayout );

            JRadioButton newOb = new JRadioButton( "New Obj", true );
            newOb.addItemListener( new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if ( e.getStateChange() == ItemEvent.SELECTED ) {
                        cardLayout.show( selector, ((JRadioButton) e.getItem()).getText());
                    }
                }
            });
            JRadioButton oldOb = new JRadioButton( "Existing", false );
            oldOb.addItemListener( new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if ( e.getStateChange() == ItemEvent.SELECTED ) {
                        cardLayout.show( selector, ((JRadioButton) e.getItem()).getText());
                    }
                }
            });

            ButtonGroup bg = new ButtonGroup();
            bg.add( newOb );
            bg.add( oldOb );


            final JComboBox objTypes = listCompatibleTypes( rd.getRange() );
            objTypes.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        Class k = Class.forName((String) objTypes.getSelectedItem() + "Impl");
                        newValue = k.getConstructor().newInstance();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            selector.add( objTypes, newOb.getText() );
            try {
                Class k = Class.forName((String) objTypes.getSelectedItem() + "Impl");
                newValue = k.getConstructor().newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }



            final JComboBox oldObjs = getPossibleTargets(rd.getRange(), g);
            oldObjs.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        newValue = oldObjs.getSelectedItem();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            selector.add( oldObjs, oldOb.getText() );
            try {
                newValue = oldObjs.getModel().getElementAt( 0 );
            } catch (Exception ex) {
                ex.printStackTrace();
            }



            chooser.add( newOb );
            chooser.add( oldOb );
            chooser.add( selector );
            input = chooser;

        }
        return input;
    }

    private JComboBox getPossibleTargets(Class range, Graph g) {
        JComboBox box = new JComboBox();
        DefaultComboBoxModel model = new DefaultComboBoxModel();

        Iterator<Tuple> nodes = g.getNodes().tuples();
        while ( nodes.hasNext() ) {
            Tuple n = nodes.next();
            if ( n.canGet( "descr", ObjectDescriptor.class ) ) {
                ObjectDescriptor od = (ObjectDescriptor) n.get( "descr" );

                if ( od.getObject() != null && range.isAssignableFrom( od.getObject().getClass() ) ) {
                    model.addElement( od.getObject() );
                }
            }
        }
        box.setModel( model );
        return box;
    }

    private JComboBox listCompatibleTypes(Class range) {
        JComboBox box = new JComboBox();
        DefaultComboBoxModel model = new DefaultComboBoxModel();

        //TODO: cheating here...

//        Class shadow = null;
//        try {
//            shadow = Class.forName( range.getName() + "$$Shadow" );
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        addSubType( shadow, model);

        box.setModel( model );
        return box;
    }

    private void addSubType(Class ix, DefaultComboBoxModel subs) {
//        if ( ix.getName().endsWith( "$$Shadow") ) {
//            subs.addElement(ix.getName().replace("$$Shadow", ""));
//            for ( Class sx : ix.getInterfaces() ) {
//                addSubType( sx, subs );
//            }
//        }
    }


    private void removeProperty( RelationDescriptor activeProp, Edge edge, Node currNode, Visualization graph ) {
        AggregateTable agg = (AggregateTable) graph.getVisualGroup( Visualizer.AGGR );

        synchronized (agg) {
            RelationDescriptor rel = (RelationDescriptor) edge.get( "descr" );
            try {
                activeProp.getRemover().invoke( activeProp.getSubject(), activeProp.getObject() );
            } catch ( Exception ex ) {
                ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            ((ObjectDescriptor) currNode.get( "descr" )).getOutRelations().remove( rel );
            System.out.println( "After removing " +  ((ObjectDescriptor) currNode.get( "descr" )).getOutRelations() );

            Node target = edge.getTargetNode();

            graph.getSourceData( Visualizer.edges ).removeTuple( (Edge) edge.get("edge") );

            if ( target.getInDegree() == 0 ) {
                System.out.println( "NEed to remove orphan node " + target.get( "label" ) );
                removeCascade( target, graph, agg );
            } else {
                ((ObjectDescriptor) target.get( "descr" )).getInRelations().remove( rel );
            }
        }
    }




    private void removeCascade( Node target, Visualization viz, AggregateTable att ) {
        synchronized ( att ) {
            Iterator<Edge> outs = target.outEdges();
            Collection<Edge> victims = new ArrayList<Edge>();
            VisualItem visualNode = viz.getVisualItem( Visualizer.nodes, target );

            while ( outs.hasNext() ) {
                victims.add( outs.next() );
            }


            for ( Edge out : victims ) {
                Node x = out.getTargetNode();

                viz.getSourceData( Visualizer.edges ).removeTuple( out );

                if ( x.getInDegree() < 1 ) {
                    removeCascade( x, viz, att );
                }

            }

            if ( target.get( "aggId" ) != null ) {
                att.removeFromAggregate( (Integer) target.get( "aggId" ), visualNode );
                if ( ObjectDescriptor.NODETYPE.OBJECT.equals( target.get( "type" ) ) ) {
                    att.removeRow( (Integer) target.get( "aggId" ) );
                }
            }

            System.out.println( "Finally remove orphan node " + target.get( "label" ) );

            viz.getSourceData( Visualizer.nodes ).removeTuple( (Node) target.get( "node" ) );

        }
    }





    private synchronized void setValue( Tuple tuple, ObjectDescriptor target, RelationDescriptor descr, Object oldValue, Object newValue ) {
        try {

            Object obj = descr.getSubject();
            Method setter = descr.getSetter();
            if ( setter != null ) {
                setter.invoke(obj, newValue);
            } else {
                descr.getRemover().invoke(obj, oldValue);
                descr.getAdder().invoke(obj, newValue);
            }
            target.setObject(newValue);
            target.setLabel(newValue.toString());

            tuple.set( "label", newValue.toString() );

        } catch ( Exception exc ) {
            exc.printStackTrace();
        }
    }


    public JPanel getPanel() {
        return gui.getPanel();
    }
}
