package org.drools.shapes.terms.generator;

import edu.mayo.cts2.terms.TermsNames;
import org.drools.semantics.builder.reasoner.CodeSystem;
import org.drools.semantics.builder.reasoner.ConceptCode;
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
    private static final IRI NOTATION           = IRI.create( TermsNames.NOTATION );
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
                    CodeSystem cs = CodeSystem.build( ind, model );
                    codeSystems.put( cs.getCodeSystemUri(), cs );
                }
            }
        }


        for ( OWLNamedIndividual ind : inds ) {
            Set<OWLClassExpression> types = ind.getTypes( model.getImportsClosure() );
            for ( OWLClassExpression kls : types ) {
                if ( kls.asOWLClass().equals( odf.getOWLClass( CONCEPT ) ) ) {

                    ConceptCode concept = ConceptCode.build( ind, model );

                    for ( OWLObjectPropertyAssertionAxiom dp : model.getObjectPropertyAssertionAxioms( ind ) ) {
                        if ( dp.getProperty().asOWLObjectProperty().getIRI().equals( IRI.create( TermsNames.IN_SCHEME ) ) ) {
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
