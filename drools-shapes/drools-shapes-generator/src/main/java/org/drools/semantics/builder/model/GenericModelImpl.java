
package org.drools.semantics.builder.model;


import org.drools.semantics.builder.model.compilers.ModelCompiler;
import org.drools.semantics.utils.NameUtils;
import org.w3._2002._07.owl.Thing;

import java.util.*;

public class GenericModelImpl implements OntoModel, Cloneable {


    private String defaultPackage;

    private String defaultNamespace;

    private String name;

    private Mode mode;
    
    private Set<Individual> individuals = new HashSet<Individual>();

    private LinkedHashMap<String, Concept> concepts = new LinkedHashMap<String, Concept>();

    private Set<SubConceptOf> subConcepts = new HashSet<SubConceptOf>();

    private Map<String, Set<PropertyRelation>> properties = new HashMap<String, Set<PropertyRelation>>();

    private Set<String> packageNames = new HashSet<String>();

    private ClassLoader classLoader;


    protected GenericModelImpl newInstance() {
        return new GenericModelImpl();
    }

    public Object clone() {
        GenericModelImpl twin = newInstance();
        twin.setDefaultPackage( defaultPackage );
        twin.setName( name );
        twin.setMode(mode);
        twin.setConcepts( new LinkedHashMap<String, Concept>( concepts ) );
        twin.setSubConcepts( new HashSet<SubConceptOf>( subConcepts ) );
        twin.setProperties( new HashMap<String, Set<PropertyRelation>>( properties ) );
        return twin;
    }


    public String getDefaultPackage() {
        return defaultPackage;
    }

    public void setDefaultPackage( String pack ) {
        this.defaultPackage = pack;
    }

    public Set<String> getAllPackageNames() {
        return packageNames;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public void setDefaultNamespace(String namespace) {
        this.defaultNamespace = namespace;
    }

    public String getDefaultPackagee() {
        return defaultPackage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Concept> getConcepts() {
        return new ArrayList<Concept>( concepts.values() );
    }

    public Concept getConcept( String id ) {
        return concepts.get( id );
    }

    public void addConcept( Concept con ) {

        try {
            System.out.println( "Trying to resolve " + con.getFullyQualifiedName() );
            Class klass = Class.forName( con.getFullyQualifiedName(), true, getClassLoader() );
            con.setResolved( true );
            if ( klass.isInterface() ) {
                con.setResolvedAs( Concept.Resolution.IFACE );
            } else if ( klass.isEnum() ) {
                con.setResolvedAs( Concept.Resolution.ENUM );
            } else {
                con.setResolvedAs( Concept.Resolution.CLASS );
            }
        } catch (ClassNotFoundException e) {
            con.setResolved( false );
            con.setResolvedAs( Concept.Resolution.NONE );
        }

        concepts.put( con.getIri(), con );
        packageNames.add( con.getPackage() );
    }

    public Concept removeConcept( Concept con ) {
        return concepts.remove( con.getIri() );
    }

    public Set<Individual> getIndividuals() {
        return individuals;
    }

    public void addIndividual( Individual i ) {
        individuals.add( i );
    }

    public Individual removeIndividual( Individual i ) {
        return individuals.remove( i ) ? i : null;
    }

    protected void setConcepts(LinkedHashMap<String, Concept> concepts) {
        this.concepts = concepts;
    }



    public Set<SubConceptOf> getSubConcepts() {
        return subConcepts;
    }

    public void addSubConceptOf( SubConceptOf sub ) {
        subConcepts.add( sub );
    }

    public boolean removeSubConceptOf( SubConceptOf sub ) {
        return subConcepts.remove( sub );
    }

    public SubConceptOf getSubConceptOf( String sub, String sup ) {
        for ( SubConceptOf rel : subConcepts ) {
            if ( rel.getSubject().equals( sub ) ) {
                if ( rel.getObject().equals( sup ) ) {
                    return rel;
                }
            }
        }
        return null;
    }

    protected void setSubConcepts(Set<SubConceptOf> subConcepts) {
        this.subConcepts = subConcepts;
    }


    public Set<PropertyRelation> getProperties() {
        HashSet<PropertyRelation> set = new HashSet<PropertyRelation>();
        for ( Set<PropertyRelation> subset : properties.values() ) {
            set.addAll( subset );
        }
        return set;
    }

    public void addProperty( PropertyRelation rel ) {
        Set<PropertyRelation> set = properties.get( rel.getProperty() );
        if ( set == null ) {
            set = new HashSet<PropertyRelation>();
            properties.put( rel.getProperty(), set );
        }
        set.add( rel );
    }

    public PropertyRelation removeProperty( PropertyRelation rel ) {
        Set<PropertyRelation> set = properties.get( rel.getProperty() );
        if ( set != null ) {
            set.remove( rel );
            return  rel;
        }
        return null;
    }

    public PropertyRelation getProperty( String iri ) {
        Collection<PropertyRelation> props = properties.get( iri );
        return props != null ? props.iterator().next() : null;
    }

    protected void setProperties(Map<String, Set<PropertyRelation>> properties) {
        this.properties = properties;
    }





    @Override
    public String toString() {
        return "Model{\n" +
                "\n\n concepts=\n" + conceptsToString() +
                "\n\n subConcepts=\n" + subConceptsToString() +
                "\n\n properties=\n" + propertiesToString() +
                '}';
    }





    private String propertiesToString() {
        StringBuilder sb = new StringBuilder();
        for ( PropertyRelation prop : getProperties() ) {
            sb.append("\t").append(prop.toFullString()).append("\n");
        }
        return sb.toString();
    }

    private String subConceptsToString() {
        StringBuilder sb = new StringBuilder();
        for ( SubConceptOf sub : subConcepts ) {
            sb.append("\t").append(sub.toFullString()).append("\n");
        }
        return sb.toString();
    }

    private String conceptsToString() {
        StringBuilder sb = new StringBuilder();
        for ( Concept con : getConcepts() ) {
            sb.append("\t").append( con.toFullString() ).append("\n");
        }
        return sb.toString();

    }




    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }


    public boolean isHierarchyConsistent() {
        for ( Concept con : getConcepts() ) {
            for ( PropertyRelation rel : con.getProperties().values() ) {
//                System.out.println( "Looking for property " + rel.getName() + " starting from " + con.getName() );
                if ( ! isPropertyAvailable( rel, con ) ) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isPropertyAvailable( PropertyRelation rel, Concept con ) {
        if ( con == null ) {
            return false;
        }
//        System.out.println( "Is property " + rel.toFullString() + " available to " + con.getName() );
        if ( con.getChosenProperties().containsKey( rel.getProperty() ) ) {
            return true;
        } else {
            if ( con.getChosenSuperConcept() != con ) {
                return isPropertyAvailable( rel, con.getChosenSuperConcept() );
            } else {
//                System.err.print( "Topp reached looking fir " + rel.getProperty() + " in " + con.getIri() );
                return false;
            }
        }
    }


    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void sort() {
        List<Concept> conceptList = new ArrayList<Concept>( getConcepts() );
        Node<Concept> root = new Node<Concept>( null );
        Map<String, Node<Concept>> map = new HashMap<String, Node<Concept>>();
        for ( Concept con : conceptList ) {
            String key = con.getIri();

            Node<Concept> node = map.get( key );
            if ( node == null ) {
                node = new Node( key,
                        con );
                map.put( key,
                        node );
            } else if ( node.getData() == null ) {
                node.setData( con );
            }
            if ( con.getSuperConcepts().isEmpty() ) {
                root.addChild( node );
            } else {
                for ( Concept superCon : con.getSuperConcepts() ) {

                    String superKey = superCon.getIri();

                    Node<Concept> superNode = map.get( superKey );
                    if ( superNode == null ) {
                        superNode = new Node<Concept>( superKey );
                        map.put( superKey,
                                superNode );
                    }
                    superNode.addChild( node );
                }
            }

        }

        Iterator<Node<Concept>> iter = map.values().iterator();
        while ( iter.hasNext() ) {
            Node<Concept> n = iter.next();
            if ( n.getData() == null ) root.addChild( n );

        }

        List<Concept> sortedList = new LinkedList<Concept>();
        root.accept( sortedList );

        concepts.clear();
        for ( Concept c : sortedList ) {
            concepts.put( c.getIri(), c );
        }
    }

    /**
     * Utility class for the sorting algorithm
     *
     * @param <T>
     */
    private static class Node<T> {
        private String        key;
        private T             data;
        private List<Node<T>> children;

        public Node(String key) {
            this.key = key;
            this.children = new LinkedList<Node<T>>();
        }

        public Node(String key,
                    T content) {
            this( key );
            this.data = content;
        }

        public void addChild(Node<T> child) {
            this.children.add( child );
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public String getKey() {
            return key;
        }

        public T getData() {
            return data;
        }

        public void setData(T content) {
            this.data = content;
        }

        public void accept(List<T> list) {
            if ( this.data != null ) {
                if ( list.contains( this.data ) ) {
                    list.remove( this.data );
                }
                list.add( this.data );
            }

            for ( int j = 0; j < children.size(); j++ )
                children.get( j ).accept( list );
        }
    }



}
