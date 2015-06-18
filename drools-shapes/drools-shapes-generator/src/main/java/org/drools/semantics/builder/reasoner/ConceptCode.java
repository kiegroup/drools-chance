package org.drools.semantics.builder.reasoner;

import edu.mayo.cts2.terms.TermsNames;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

public class ConceptCode {

    private String code;
    private String uri;
    private String codeSystem;
    private String name;

    public ConceptCode() {}

    public ConceptCode( String code, String codeSystem, String name ) {
        this.code = code;
        this.codeSystem = codeSystem;
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }

    @Override
    public String toString() {
        return "Concept{" +
               "code='" + code + '\'' +
               ", codeSystem='" + codeSystem + '\'' +
               ", name='" + name + '\'' +
               '}';
    }

    public static ConceptCode build( OWLNamedIndividual ind, OWLOntology model ) {
        OWLDataFactory odf = model.getOWLOntologyManager().getOWLDataFactory();
        ConceptCode concept = new ConceptCode();
        concept.setUri( ind.getIRI().toString() );

        Set<OWLLiteral> values = ind.getDataPropertyValues( odf.getOWLDataProperty( IRI.create( TermsNames.NOTATION ) ), model );
        for ( OWLLiteral val : values ) {
            concept.setCode( val.getLiteral().toString() );
        }

        String name = concept.getCode();
        if ( name != null && Character.isDigit( name.charAt( 0 ) ) ) {
            name = "_" + name;
        }
        concept.setName( name );

        return concept;
    }
}
