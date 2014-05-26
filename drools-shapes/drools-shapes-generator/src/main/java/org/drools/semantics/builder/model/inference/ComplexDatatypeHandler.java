package org.drools.semantics.builder.model.inference;

import org.semanticweb.HermiT.datatypes.DatatypeHandler;
import org.semanticweb.HermiT.datatypes.MalformedLiteralException;
import org.semanticweb.HermiT.datatypes.UnsupportedFacetException;
import org.semanticweb.HermiT.datatypes.ValueSpaceSubset;
import org.semanticweb.HermiT.model.DatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ComplexDatatypeHandler implements DatatypeHandler {
    private String uri;

    public ComplexDatatypeHandler( OWLDatatype datatype, OWLOntology ontoDescr ) {
        uri = datatype.getIRI().toString();
    }

    @Override
    public Set<String> getManagedDatatypeURIs() {
        return Collections.singleton( uri );
    }

    @Override
    public Object parseLiteral( String lexicalForm, String datatypeURI ) throws MalformedLiteralException {
        throw new UnsupportedOperationException( "TODO" );
    }

    @Override
    public void validateDatatypeRestriction( DatatypeRestriction datatypeRestriction ) throws UnsupportedFacetException {

    }

    @Override
    public ValueSpaceSubset createValueSpaceSubset( DatatypeRestriction datatypeRestriction ) {
        ValueSpaceSubset vs = new ValueSpaceSubset() {
            @Override
            public boolean hasCardinalityAtLeast( int number ) {
                return true;
            }

            @Override
            public boolean containsDataValue( Object dataValue ) {
                return true;
            }

            @Override
            public void enumerateDataValues( Collection<Object> dataValues ) {

            }
        };
        return vs;
    }

    @Override
    public ValueSpaceSubset conjoinWithDR( ValueSpaceSubset valueSpaceSubset, DatatypeRestriction datatypeRestriction ) {
        return valueSpaceSubset;
    }

    @Override
    public ValueSpaceSubset conjoinWithDRNegation( ValueSpaceSubset valueSpaceSubset, DatatypeRestriction datatypeRestriction ) {
        return valueSpaceSubset;
    }

    @Override
    public boolean isSubsetOf( String subsetDatatypeURI, String supersetDatatypeURI ) {
        return false;
    }

    @Override
    public boolean isDisjointWith( String datatypeURI1, String datatypeURI2 ) {
        return ! datatypeURI1.equals( datatypeURI2 );
    }
}
