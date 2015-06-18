package org.drools.semantics.builder.reasoner;

import edu.mayo.cts2.terms.TermsNames;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class CodeSystem {

    private Set<ConceptCode> concepts = new HashSet<ConceptCode>();

    private String codeSystemName;

    private String codeSystemUri;

    private String codeSystemId;

    public CodeSystem() {
        super();
    }

    public CodeSystem(String codeSystemName, String codeSystemUri) {
        this.codeSystemName = codeSystemName;
        this.codeSystemUri = codeSystemUri;
    }

    public CodeSystem(Set<ConceptCode> concepts, String codeSystemName, String codeSystemUri) {
        this.concepts = concepts;
        this.codeSystemName = codeSystemName;
        this.codeSystemUri = codeSystemUri;
    }

    public Set<ConceptCode> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<ConceptCode> concepts) {
        this.concepts = concepts;
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public void setCodeSystemName(String codeSystemName) {
        this.codeSystemName = codeSystemName;
    }

    public String getCodeSystemUri() {
        return codeSystemUri;
    }

    public void setCodeSystemUri(String codeSystemUri) {
        this.codeSystemUri = codeSystemUri;
    }

    public String getCodeSystemId() {
        return codeSystemId;
    }

    public void setCodeSystemId( String codeSystemId ) {
        this.codeSystemId = codeSystemId;
    }

    @Override
    public String toString() {
        return "CodeSystem{ " +
               "Uri = '" + codeSystemUri + "( " + concepts.size() + " )" +
               '}';
    }

    public static CodeSystem build( OWLNamedIndividual ind, OWLOntology model ) {
        OWLDataFactory odf = model.getOWLOntologyManager().getOWLDataFactory();
        CodeSystem cs = new CodeSystem();
        cs.setCodeSystemUri( ind.getIRI().toString() );

        for ( OWLLiteral val : ind.getDataPropertyValues( odf.getOWLDataProperty( IRI.create( TermsNames.NOTATION ) ), model ) ) {
            cs.setCodeSystemName( val.getLiteral() );
        }
        if ( cs.getCodeSystemName() == null ) {
            Set<OWLAnnotation> labels = ind.getAnnotations( model, odf.getOWLAnnotationProperty( IRI.create( TermsNames.LABEL ) ) );
            if ( ! labels.isEmpty() ) {
                cs.setCodeSystemName( ((OWLLiteral) labels.iterator().next().getValue()).getLiteral() );
            }
        }
        if ( cs.getCodeSystemName() == null ) {
            cs.setCodeSystemName( ind.getIRI().getFragment().replaceAll( "\\.", "_" ) );
        }

        for ( OWLAnnotation val : ind.getAnnotations( model, odf.getOWLAnnotationProperty( IRI.create( TermsNames.OID ) ) ) ) {
            cs.setCodeSystemId( ( (OWLLiteral) val.getValue() ).getLiteral() );
        }
        if ( cs.getCodeSystemId() == null ) {
            cs.setCodeSystemId( URI.create( cs.getCodeSystemUri() ).getFragment() );
        }

        return cs;
    }
}
