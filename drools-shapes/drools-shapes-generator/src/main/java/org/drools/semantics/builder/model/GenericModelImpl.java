
package org.drools.semantics.builder.model;


import org.drools.core.util.CodedHierarchy;
import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.HierarchyEncoderImpl;
import org.drools.core.util.HierarchySorter;
import org.drools.semantics.util.area.AreaTxn;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericModelImpl implements OntoModel, Cloneable {


    private String defaultPackage;

    private String defaultNamespace;

    private OWLOntology ontology;

    private String name;

    private Mode mode;
    
    private Set<Individual> individuals = new HashSet<Individual>();

    private LinkedHashMap<String, Concept> concepts = new LinkedHashMap<String, Concept>();

    private Set<SubConceptOf> subConcepts = new HashSet<SubConceptOf>();

    private Map<String, Set<PropertyRelation>> properties = new HashMap<String, Set<PropertyRelation>>();

    private Set<String> packageNames = new HashSet<String>();

    private ClassLoader classLoader;

    private HierarchyEncoder<Concept> hierarchyEncoder = new HierarchyEncoderImpl<Concept>();

    private ConceptAreaTxn areaTxn;

    private boolean minimal;

    private boolean standalone;

    protected GenericModelImpl newInstance() {
        return new GenericModelImpl();
    }

    public Object clone() {
        GenericModelImpl twin = newInstance();
        twin.setDefaultPackage( defaultPackage );
        twin.setOntology( ontology );
        twin.setName( name );
        twin.setMode(mode);
        twin.setConcepts( new LinkedHashMap<String, Concept>( concepts ) );
        twin.setSubConcepts( new HashSet<SubConceptOf>( subConcepts ) );
        twin.setProperties( new HashMap<String, Set<PropertyRelation>>( properties ) );
        return twin;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology( OWLOntology ontology ) {
        this.ontology = ontology;
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
            Class klass;
            if ( getClassLoader() != null ) {
                klass = Class.forName( con.getFullyQualifiedName(), true, getClassLoader() );
            } else {
                klass = Class.forName( con.getFullyQualifiedName() );
            }
            con.setResolved( true );
            if ( klass.isInterface() ) {
                con.setResolvedAs( Concept.Resolution.IFACE );
            } else if ( klass.isEnum() ) {
                con.setResolvedAs( Concept.Resolution.ENUM );
            } else {
                con.setResolvedAs( Concept.Resolution.CLASS );
            }
        } catch ( ClassNotFoundException e ) {
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


    public Set<PropertyRelation> getProperties( String domainIri ) {
        return properties.get( domainIri );
    }

    public Set<PropertyRelation> getProperties() {
        HashSet<PropertyRelation> set = new HashSet<PropertyRelation>();
        for ( Set<PropertyRelation> subset : properties.values() ) {
            set.addAll( subset );
        }
        return set;
    }

    public PropertyRelation addProperty( PropertyRelation rel ) {
        Set<PropertyRelation> set = properties.get( rel.getProperty() );
        if ( set == null ) {
            set = new HashSet<PropertyRelation>();
            properties.put( rel.getProperty(), set );
        }

        if ( set.contains( rel ) ) {
            for ( PropertyRelation existing : set ) {
                if ( existing.equals( rel ) ) {
                    return existing;
                }
            }
            throw new IllegalStateException( "Should not be here... Set.contains returned true, but the iteration could not find an equal object" );
        } else {
            set.add( rel );
            return rel;
        }
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


    public CodedHierarchy<Concept> getConceptHierarchy() {
        return hierarchyEncoder;
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
                if ( ! isPropertyAvailableToImpl( rel, con ) ) {
                    return false;
                }
            }
            if ( ! con.getImplementingCon().validate() ) {
                return false;
            }
        }
        return true;
    }

    private boolean isPropertyAvailableToImpl( PropertyRelation rel, Concept con ) {
        if ( con == null ) {
            return false;
        }
        if ( con.getChosenProperties().containsKey( rel.getProperty() ) ) {
            return true;
        } else {
            if ( con.getChosenSuperConcept() != con ) {
                return isPropertyAvailableToImpl( rel, con.getChosenSuperConcept() );
            } else {
//                System.err.print( "Topp reached looking fir " + rel.getProperty() + " in " + con.getIri() );
                return false;
            }
        }
    }

    public void reassignConceptCodes() {
        hierarchyEncoder.clear();
        for ( Concept con : getConcepts() ) {
            con.setTypeCode( hierarchyEncoder.encode( con, con.getSuperConcepts() ) );
        }

        if ( hierarchyEncoder.size() != getConcepts().size() ) {
            StringBuilder sb = new StringBuilder();
            sb.append( " Not all concepts were coded correctly, or some code has been overwritten : \n" );
            for ( Concept con : getConcepts() ) {
                if ( hierarchyEncoder.getCode( con ) == null ) {
                    sb.append( "Unable to find concept code : " ).append( con ).append( "\n" );
                }
            }
            sb.append( " Encoder size " ).append( hierarchyEncoder.size() ).append( " vs expected " ).append( getConcepts().size() );
            throw new IllegalStateException( sb.toString() );
        }
    }


    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void sort() {
        Map<Concept,Collection<Concept>> hier = new HashMap<Concept, Collection<Concept>>();
        for ( Concept c : concepts.values() ) {
            hier.put( c, c.getSuperConcepts() );
        }
        concepts.clear();

        List<Concept> sorted = new HierarchySorter<Concept>().sort( hier );
        for ( Concept c : sorted ) {
            concepts.put( c.getIri(), c );
        }
    }


    public void buildAreaTaxonomy() {
        areaTxn = new ConceptAreaTxn( this );
    }

    public AreaTxn<Concept,PropertyRelation> getAreaTaxonomy() {
        if ( areaTxn == null ) {
            buildAreaTaxonomy();
        }
        return areaTxn;
    }

    @Override
    public boolean isMinimal() {
        return minimal;
    }

    @Override
    public void setMinimal( boolean minimal ) {
        this.minimal = minimal;
    }

    @Override
    public boolean isStandalone() {
        return standalone;
    }

    @Override
    public void setStandalone( boolean standalone ) {
        this.standalone = standalone;
    }
}
