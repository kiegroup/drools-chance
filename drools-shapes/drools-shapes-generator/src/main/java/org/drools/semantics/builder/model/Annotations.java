package org.drools.semantics.builder.model;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

public class Annotations {

    public static final IRI ATTRIBUTE = IRI.create( "http://drools.org/shapes/attribute" );

    public static boolean hasAnnotation( OWLEntity subj, IRI iri, OWLOntology o ) {
        OWLDataFactory f = o.getOWLOntologyManager().getOWLDataFactory();
        OWLAnnotationProperty oap = f.getOWLAnnotationProperty( iri );
        for ( OWLOntology onto : o.getImportsClosure() ) {
            Set<OWLAnnotation> set = subj.getAnnotations( onto, oap );
            if ( ! set.isEmpty() ) {
                return true;
            }
        }
        return false;
    }
}
