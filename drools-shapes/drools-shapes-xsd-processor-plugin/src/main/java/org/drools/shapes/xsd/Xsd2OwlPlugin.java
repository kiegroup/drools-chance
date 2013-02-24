package org.drools.shapes.xsd;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.w3._2001.xmlschema.Schema;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;

/**
 * Goal
 *
 * @goal generate-owl
 *
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class Xsd2OwlPlugin extends AbstractMojo {

    /**
     * @parameter
     */
    private List<String> schemaLocations;

    public List<String> getSchemaLocations() {
        return schemaLocations;
    }

    public void setSchemaLocations(List<String> schemaLocations) {
        this.schemaLocations = schemaLocations;
    }


    /**
     * @parameter default-value="./target/generated-sources"
     */
    private File outputDirectory;

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }



    /**
     * @parameter default-value="false"
     */
    private boolean checkConsistency;

    public boolean isCheckConsistency() {
        return checkConsistency;
    }

    public void setCheckConsistency(boolean checkConsistency) {
        this.checkConsistency = checkConsistency;
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            for ( String loc : schemaLocations ) {
                System.out.println( ">> Looking  for " + loc );
                Xsd2Owl converter = Xsd2OwlImpl.getInstance();

                URL url = new File( loc ).toURI().toURL();
                System.out.println( "FOUND URL " + url );
                if ( url != null ) {
                    Schema x = converter.parse( url );

                    OWLOntology onto = converter.transform( x, url, false, checkConsistency );

                    if ( ! outputDirectory.exists() ) {
                        outputDirectory.mkdirs();
                    }

                    //TODO Add more formats
                    String src = loc.substring( loc.lastIndexOf( File.separator ) ) + ".ttl";
                    converter.stream( onto,
                            new FileOutputStream( outputDirectory.getPath() + "/" + src ),

                            new TurtleOntologyFormat()
                    );
                } else {
                    System.err.println( "Alas, resource " + loc + " was not found from" + new File(".").getAbsolutePath() );
                }
            }

        } catch ( Exception e ) {
            System.out.println( e.getMessage() );
        }
    }
}



