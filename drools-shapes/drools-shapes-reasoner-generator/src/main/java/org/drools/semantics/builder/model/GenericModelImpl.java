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


import org.drools.semantics.builder.DLUtils;

import java.util.*;

public class GenericModelImpl implements OntoModel {


    private String pack;
    
    private String name;

    private boolean flat = false;

    private LinkedHashMap<String, Concept> concepts = new LinkedHashMap<String, Concept>();

    private Set<SubConceptOf> subConcepts = new HashSet<SubConceptOf>();

    private Map<String, Set<PropertyRelation>> properties = new HashMap<String, Set<PropertyRelation>>();


    public String getPackage() {
        return pack;
    }

    public void setPackage(String pack) {
        this.pack = DLUtils.iriToPackage( pack );
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
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
        concepts.put(con.getIri(), con);
    }

    public Concept removeConcept( Concept con ) {
        return concepts.remove( con.getIri() );
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
        return properties.get( iri ).iterator().next();
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
            sb.append("\t").append(con.toFullString()).append("\n");
        }
        return sb.toString();

    }




    public void flatten() {
        if ( ! isFlat() ) {
            for ( String conceptName : concepts.keySet() ) {
                Concept con = concepts.get( conceptName );
                Map<String, PropertyRelation> baseProps = con.getProperties();
                Set<Concept> superConcepts = con.getSuperConcepts();
                for ( Concept sup : superConcepts ) {
                    Map<String,PropertyRelation> inheritedProperties = sup.getProperties();
                    for ( String propKey : inheritedProperties.keySet() ) {
                        if ( ! baseProps.containsKey( propKey ) ) {
                            baseProps.put( propKey, inheritedProperties.get( propKey ) );
                        }
                    }
                }
            }
            flat = true;
        }
    }

    public void elevate() {
        if ( isFlat() ) {
            for ( String conceptName : concepts.keySet() ) {
                Concept con = concepts.get( conceptName );
                Map<String, PropertyRelation> baseProps = con.getProperties();
                Set<Concept> superConcepts = con.getSuperConcepts();
                for ( Concept sup : superConcepts ) {
                    Map<String,PropertyRelation> inheritedProperties = sup.getProperties();
                    for ( String propKey : inheritedProperties.keySet() ) {
                        if ( baseProps.containsKey( propKey ) ) {
                            if ( baseProps.get( propKey ).equals( inheritedProperties.get( propKey ) ) ) {
                                baseProps.remove(propKey);
                            }
                        }
                    }
                }
            }
            flat = true;
        }
    }

    public boolean isFlat() {
        return flat;
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
