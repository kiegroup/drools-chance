package org.drools.shapes.terms.generator;

import org.drools.shapes.terms.generator.util.Loader;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TerminologyGenerator {

    private static final IRI CONCEPT_SCHEME     = IRI.create( "http://www.w3.org/2004/02/skos/core#ConceptScheme" );
    private static final IRI CONCEPT            = IRI.create( "http://www.w3.org/2004/02/skos/core#Concept" );
    private static final IRI LABEL              = IRI.create( "http://www.w3.org/2004/02/skos/core#prefLabel" );
    private static final IRI NOTATION           = IRI.create( "http://www.w3.org/2004/02/skos/core#notation" );
    private static final IRI OID                = IRI.create( "https://www.hl7.org/oid" );

    public void generate( String[] owl, String packageName, File outputDirectory ) throws OWLOntologyCreationException, FileNotFoundException {

        OWLOntology ontology = Loader.loadOntology( owl );

        Map<String,CodeSystem> codeSystems = traverse( ontology );

        if ( ! outputDirectory.exists() ) {
            outputDirectory.mkdirs();
        }

        new JavaGenerator().generate( codeSystems.values(), packageName, outputDirectory );
    }

    protected Map<String,CodeSystem> traverse( OWLOntology model ) {
        OWLOntologyManager manager = model.getOWLOntologyManager();
        OWLDataFactory odf = manager.getOWLDataFactory();

        Map<String,CodeSystem> codeSystems = new HashMap<String,CodeSystem>();

        Set<OWLNamedIndividual> inds = model.getIndividualsInSignature( true );
        for ( OWLNamedIndividual ind : inds ) {
            Set<OWLClassExpression> types = ind.getTypes( model.getImportsClosure() );
            for ( OWLClassExpression kls : types ) {
                if ( kls.toString().contains( "ConceptScheme" ) ) {
                    CodeSystem cs = new CodeSystem();
                    cs.setCodeSystemUri( ind.getIRI().toString() );

                    for ( OWLLiteral val : ind.getDataPropertyValues( odf.getOWLDataProperty( NOTATION ), model ) ) {
                        cs.setCodeSystemName( val.getLiteral() );
                    }
                    if ( cs.getCodeSystemName() == null ) {
                        Set<OWLAnnotation> labels = ind.getAnnotations( model, odf.getOWLAnnotationProperty( LABEL ) );
                        if ( ! labels.isEmpty() ) {
                            cs.setCodeSystemName( ((OWLLiteral) labels.iterator().next().getValue()).getLiteral() );
                        }
                    }
                    if ( cs.getCodeSystemName() == null ) {
                        cs.setCodeSystemName( ind.getIRI().getFragment().replaceAll( "\\.", "_" ) );
                    }

                    for ( OWLAnnotation val : ind.getAnnotations( model, odf.getOWLAnnotationProperty( OID ) ) ) {
                        cs.setCodeSystemId( ( (OWLLiteral) val.getValue() ).getLiteral() );
                    }
                    if ( cs.getCodeSystemId() == null ) {
                        cs.setCodeSystemId( URI.create( cs.getCodeSystemUri() ).getFragment() );
                    }

                    codeSystems.put( cs.getCodeSystemUri(), cs );
                }
            }
        }


        for ( OWLNamedIndividual ind : inds ) {
            Set<OWLClassExpression> types = ind.getTypes( model.getImportsClosure() );
            for ( OWLClassExpression kls : types ) {
                if ( kls.asOWLClass().equals( odf.getOWLClass( CONCEPT ) ) ) {
                    Concept concept = new Concept();
                    concept.setUri( ind.getIRI().toString() );

                    Set<OWLLiteral> values = ind.getDataPropertyValues( odf.getOWLDataProperty( IRI.create( "http://www.w3.org/2004/02/skos/core#notation" ) ), model );
                    for ( OWLLiteral val : values ) {
                        concept.setCode( val.getLiteral().toString() );
                    }

                    String name = concept.getCode();
                    if ( name != null && Character.isDigit( name.charAt( 0 ) ) ) {
                        name = "_" + name;
                    }
                    concept.setName( name );

                    for ( OWLObjectPropertyAssertionAxiom dp : model.getObjectPropertyAssertionAxioms( ind ) ) {
                        if ( dp.getProperty().asOWLObjectProperty().getIRI().toString().contains( "inScheme" ) ) {
                            OWLIndividual cs = dp.getObject();
                            concept.setCodeSystem( cs.toString() );
                            CodeSystem codeSystem = codeSystems.get( cs.asOWLNamedIndividual().getIRI().toString() );

                            if ( codeSystem == null ) {
                                codeSystem = new CodeSystem();
                                codeSystem.setCodeSystemUri( cs.asOWLNamedIndividual().getIRI().toString() );
                                codeSystem.setCodeSystemName( cs.asOWLNamedIndividual().getIRI().getFragment() );
                                codeSystems.put( codeSystem.getCodeSystemUri(), codeSystem );
                            }

                            codeSystem.getConcepts().add( concept );
                        }
                    }
                }
            }
        }

        return codeSystems;
    }


}
