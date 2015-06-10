package org.drools.shapes.terms.generator.util;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class Loader {


    public static OWLOntology loadOntology( String[] resources ) throws OWLOntologyCreationException, FileNotFoundException {
        OWLOntology ontology = null;
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        for ( String res : resources ) {
            ontology = loadOntologyPiece( res, manager );
        }

        return ontology;
    }

    private static OWLOntology loadOntologyPiece( String file, OWLOntologyManager manager ) throws OWLOntologyCreationException, FileNotFoundException {
        File res = new File( file );

        try {
            return manager.loadOntologyFromOntologyDocument(
                    new FileInputStream( res )
            );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return null;
    }

}
