package org.drools.semantics.builder;


import org.drools.io.Resource;
import org.semanticweb.owlapi.model.OWLOntology;

public interface DLReasonerBuilder {

    public OWLOntology parseOntology( Resource resource );

    public String buildTableauRules( OWLOntology ontologyDescr, Resource[] visitor );

}
