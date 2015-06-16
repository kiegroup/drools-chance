package org.drools.shapes.terms.generator;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

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

    private OWLOntology model;

    public TerminologyGenerator( OWLOntology o, boolean reason ) {
        this.model = o;
        if( reason ) {
            this.doReason( o );
        }
    }

    public Map<String,CodeSystem> traverse( ) {
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

                    for ( OWLLiteral val : ind.getDataPropertyValues(odf.getOWLDataProperty( NOTATION ), model ) ) {
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

    public void doReason( OWLOntology o ) {
        OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
        OWLReasoner owler = reasonerFactory.createReasoner( o );

        InferredOntologyGenerator reasoner = new InferredOntologyGenerator( owler );

        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();

        reasoner.fillOntology( owlOntologyManager, o );
    }

}
