package org.drools.shapes.terms.generator.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.drools.shapes.terms.generator.CodeSystem;
import org.drools.shapes.terms.generator.JavaGenerator;
import org.drools.shapes.terms.generator.TerminologyGenerator;
import org.drools.shapes.terms.generator.util.Loader;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Goal
 *
 * @goal generate-terms
 *
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class TermsGeneratorPlugin extends AbstractMojo {

    private static final List<String> TERMS_METAMODEL_FILES = Arrays.asList(
            "cts2.owl",
            "lmm_l1.owl",
            "skos.owl",
            "terms.owl");

    /**
     * @parameter default-value="false"
     */
    private boolean reason = false;

    public boolean isReason() {
        return reason;
    }

    public void setReason(boolean reason) {
        this.reason = reason;
    }

    /**
     * @parameter
     */
    private List<String> owlFiles;

    public List<String> getOwlFile() {
        return owlFiles;
    }

    public void setOwlFile(List<String> owlFile) {
        this.owlFiles = owlFiles;
    }

    /**
     * @parameter
     */
    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            this.owlFiles.addAll(TERMS_METAMODEL_FILES);

            OWLOntology ontology = Loader.loadOntology( this.owlFiles.toArray( new String[this.owlFiles.size()] ) );

            TerminologyGenerator terminologyGenerator = new TerminologyGenerator( ontology, this.reason );

            Map<String,CodeSystem> codeSystems = terminologyGenerator.traverse( );

            if ( ! outputDirectory.exists() ) {
                outputDirectory.mkdirs();
            }

            new JavaGenerator().generate( codeSystems.values(), packageName, outputDirectory );
        } catch ( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
            throw new MojoExecutionException( e.getMessage() );
        }

    }

}



