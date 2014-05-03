package org.drools.semantics.util.editor;


import com.clarkparsia.empire.annotation.RdfProperty;
import org.drools.semantics.Thing;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;


public class FactGraphAnalyzer  {


    private int counter = 0;
    private static Map<Class, Map<String,RelationDescriptor>> relCache = new HashMap<Class, Map<String, RelationDescriptor>>();


    public Graph analyzeObject( Thing thing ) {

        Map<Object, Node> cache = new IdentityHashMap<Object, Node>();


        Graph graph = new Graph();

        graph.getNodeTable().addColumn( "id", int.class );
        graph.getNodeTable().addColumn( "rdfId", String.class );
        graph.getNodeTable().addColumn( "aggId", Integer.class );
        graph.getNodeTable().addColumn( "node", Node.class );
        graph.getNodeTable().addColumn( "label", String.class );
        graph.getNodeTable().addColumn( "descr", ObjectDescriptor.class );
        graph.getNodeTable().addColumn( "type", ObjectDescriptor.NODETYPE.class );


        graph.getEdgeTable().addColumn( "label", String.class );
        graph.getEdgeTable().addColumn( "iri", String.class );
        graph.getEdgeTable().addColumn( "edge", Edge.class );
        graph.getEdgeTable().addColumn( "descr", RelationDescriptor.class );

        Node root = graph.addNode();
        cache.put( thing, root );


        ObjectDescriptor x = new ObjectDescriptor();
        x.setObject(thing);
        x.setType( ObjectDescriptor.NODETYPE.OBJECT );
        x.setRelations( extractRelations(thing) );

        root.set( "aggId", counter++ );
        root.set( "rdfId", thing.getRdfId().toString() );
        root.set( "label", thing.getClass().getSimpleName() );
        root.set( "descr", x );
        root.set( "type", ObjectDescriptor.NODETYPE.ROOT );
        root.set( "node", root );

        process(thing, root, graph, cache);


        return graph;
    }

    private void process( Object thing, Node root, Graph graph, Map<Object, Node> cache ) {
        Map<String, Method> properties = new HashMap<String, Method>();

        Method[] methods = thing.getClass().getMethods();
        for ( Method m : methods ) {
            if ( m.getName().startsWith( "get" )  || m.getName().startsWith( "is" ) ) {
                RdfProperty ann = m.getAnnotation( RdfProperty.class );
                if ( ann != null ) {
                    properties.put( ann.value(), m );
                }
            }
        }


        for ( String key : properties.keySet() ) {
            Method m = properties.get( key );
            Object val;
            try {
                val = m.invoke( thing );
            } catch ( IllegalAccessException e ) {
                val = e.getMessage();
            } catch ( InvocationTargetException e ) {
                val = e.getMessage();
            }

            boolean singleValue = false;
            if ( val != null ) {
                List list;
                if ( val instanceof List ) {
                    list = (List) val;
                } else {
                    list = Arrays.asList( val );
                    singleValue = true;
                }
                for ( Object x : list ) {

                    Node tgt;
                    if ( isDataType( x.getClass() ) ) {
                        tgt = addDataRelation( root, key, x, m, graph );
                    } else {
                        tgt = exploreObjectRelation( root, key, x, m, graph, cache );
                    }
                }
            }
        }


    }



    private Node exploreObjectRelation(Node root, String key, Object val, Method getter, Graph graph, Map<Object, Node> cache) {

        RelationDescriptor descr = new RelationDescriptor();
        ObjectDescriptor source = (ObjectDescriptor) root.get( "descr" );

        descr.setSubject( source.getObject() );
        descr.setProperty( key );
        descr.setObject( val );
        descr.setType( RelationDescriptor.RELTYPE.OBJECT );
        descr.setRange( val.getClass() );


        extractMethods( getter, source.getObject().getClass(), descr );

        source.addOutRelation( descr );

        Node target;
        if ( ! cache.containsKey( val ) ) {
            target = graph.addNode();

            ObjectDescriptor od = new ObjectDescriptor();
            od.setObject( val );
            od.setType( ObjectDescriptor.NODETYPE.OBJECT );
            od.setLabel( val.toString() );
            od.setRelations( extractRelations( val ) );

            target.set( "label", val.toString() );
            if ( val instanceof Thing ) {
                target.set( "rdfId", ((Thing) val).getRdfId().toString() );
            }
            int id = counter++;
            target.set( "id", id );
            target.set( "aggId", id );
            target.set( "descr", od );
            target.set( "node", target );
            target.set( "type", ObjectDescriptor.NODETYPE.OBJECT );

            cache.put( val, target );
            process( val, target, graph, cache );
        } else {
            target = cache.get( val );
        }

        ((ObjectDescriptor) target.get( "descr" ) ).addInRelation( descr );


        Edge arc = graph.addEdge( root, target );
        arc.set( "label", key );
        arc.set( "iri", key );
        arc.set( "edge", arc );
        arc.set( "descr", descr );

        return target;
    }

    public static  Map<String, RelationDescriptor> extractRelations( Object val ) {
        if ( ! relCache.containsKey( val.getClass() ) ) {
            Map<String,RelationDescriptor> props = new HashMap<String, RelationDescriptor>();
            String className = val.getClass().getName();
            // remove "Impl"
            String interfaceName = className.substring(0, className.length() - 4);
            Class[] ifx = val.getClass().getInterfaces();

            Map<String,Method> methods = new HashMap<String,Method>();
            for ( Class i : ifx ) {
                if ( i.getName().equals( interfaceName ) ) {
                    collectMethods( i, methods );
                    break;
                }
            }

            for ( Method m : methods.values() ) {
                if ( m.getName().startsWith("get")  || m.getName().startsWith( "is" ) ) {
                    if ( m.getAnnotation( RdfProperty.class ) != null ) {
                        String pName = m.getAnnotation( RdfProperty.class ).value();
                        String stem = m.getName().startsWith("is") ? m.getName().substring(2) : m.getName().substring(3);
                        String adder = "add" + stem;
                        for ( Method n : val.getClass().getMethods() ) {
                            if ( adder.equals( n.getName() ) && ! Object.class.equals( n.getParameterTypes()[0] ) ) {
                                RelationDescriptor rd = new RelationDescriptor();
                                rd.setRange( n.getParameterTypes()[0] );
                                rd.setProperty( pName );
                                rd.setType( isDataType( rd.getRange() ) ? RelationDescriptor.RELTYPE.DATA : RelationDescriptor.RELTYPE.OBJECT );
                                extractMethods( m, val.getClass(), rd );
                                props.put( pName, rd );
                                break;
                            }
                        }
                    }
                }
            }

            relCache.put( val.getClass(), props );
        }
        return relCache.get( val.getClass() );
    }

    private static void collectMethods( Class i, Map<String,Method> methods ) {
        for ( Method m : i.getMethods() ) {
            if ( m.getAnnotation( RdfProperty.class ) != null ) {
                if ( ! methods.containsKey( m.getName() ) ) {
                    methods.put( m.getName(), m );
                }
            }
        }
        for ( Class superIf : i.getInterfaces() ) {
            collectMethods( superIf, methods );
        }
    }


    private Node addDataRelation( Node root, String rel, Object val, Method getter, Graph graph ) {
        Node target = graph.addNode();

        RelationDescriptor descr = new RelationDescriptor();

        ObjectDescriptor source = (ObjectDescriptor) root.get( "descr" );

        descr.setSubject( source.getObject() );
        descr.setProperty( rel );
        descr.setObject( val );
        descr.setType( RelationDescriptor.RELTYPE.DATA );
        descr.setRange( val.getClass() );

        extractMethods( getter, source.getObject().getClass(), descr );

        ObjectDescriptor sod = (ObjectDescriptor) root.get( "descr" );
        sod.addOutRelation( descr );

        ObjectDescriptor od = new ObjectDescriptor();
        od.addInRelation( descr );
        od.setType( ObjectDescriptor.NODETYPE.DATA );
        od.setObject( val );


        target.set( "aggId", root.get( "aggId" ) );
        target.set( "label", val.toString() );
        target.set( "node", target );
        target.set( "descr", od );
        target.set( "type", ObjectDescriptor.NODETYPE.DATA );

        Edge arc = graph.addEdge( root, target );
        arc.set( "label", rel );
        arc.set( "iri", rel );
        arc.set( "edge", arc );
        arc.set( "descr", descr );

        return target;
    }




    private static void extractMethods( Method getter, Class type, RelationDescriptor descr ) {

        String methodStem = getter.getName().substring( getter.getName().startsWith("is") ? 2 : 3 );
        descr.setGetter( getter );
        boolean singleValue = ! Collection.class.isAssignableFrom( getter.getReturnType() );
        Method[] methods = type.getMethods();


        if ( singleValue ) {
            String setterName = "set" + methodStem;
            for ( Method setter : methods ) {
                if ( setter.getName().equals( setterName ) && setter.getParameterTypes().length == 1 && ! Object.class.equals( setter.getParameterTypes()[0] ) ) {
                    descr.setSetter( setter );
                    descr.setRange( setter.getParameterTypes()[0] );
                    break;
                }
            }
        }
        String adderName = "add" + methodStem;
        for ( Method adder : methods ) {
            if ( adder.getName().equals( adderName ) && adder.getParameterTypes().length == 1 && ! Object.class.equals( adder.getParameterTypes()[0] ) ) {
                descr.setAdder( adder );
                descr.setRange( adder.getParameterTypes()[0] );
                break;
            }
        }
        String removerName = "remove" + methodStem;
        try {
            Method remover = type.getMethod( removerName, Object.class );
            descr.setRemover(remover);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }



    private static boolean isDataType( Class<?> returnType ) {
        return Integer.class.equals( returnType ) || int.class.equals( returnType )
                || Byte.class.equals( returnType ) || byte.class.equals( returnType )
                || Short.class.equals( returnType ) || short.class.equals( returnType )
                || Character.class.equals( returnType ) || char.class.equals( returnType )
                || Float.class.equals( returnType ) || float.class.equals( returnType )
                || Double.class.equals( returnType ) || double.class.equals( returnType )
                || BigInteger.class.equals( returnType ) || BigDecimal.class.equals( returnType )
                || Date.class.equals( returnType )
                || String.class.equals( returnType );
    }




}