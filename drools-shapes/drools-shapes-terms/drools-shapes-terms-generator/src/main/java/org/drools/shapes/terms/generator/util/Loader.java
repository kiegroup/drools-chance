package org.drools.shapes.terms.generator.util;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

public class Loader {


    public OWLOntology loadOntology( String[] resources ) throws OWLOntologyCreationException, FileNotFoundException {
        OWLOntology ontology = null;
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        for ( String res : resources ) {
            ontology = loadOntologyPiece( res, manager );
        }

        return ontology;
    }

    private OWLOntology loadOntologyPiece( String file, OWLOntologyManager manager ) throws OWLOntologyCreationException, FileNotFoundException {
        InputStream inputStream;

        File res = new File( file );

        try {
            if(! res.exists() ) {
                inputStream = new ClassPathResource( file ).getInputStream();
            } else {
                inputStream =  new FileInputStream( res );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        return manager.loadOntologyFromOntologyDocument( inputStream );
    }

}
