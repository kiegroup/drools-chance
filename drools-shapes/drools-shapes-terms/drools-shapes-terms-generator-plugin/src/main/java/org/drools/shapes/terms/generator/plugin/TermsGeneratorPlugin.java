package org.drools.shapes.terms.generator.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.drools.shapes.terms.generator.TerminologyGenerator;

import java.io.File;
import java.io.FileInputStream;

/**
 * Goal
 *
 * @goal generate-terms
 *
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class TermsGeneratorPlugin extends AbstractMojo {

    /**
     * @parameter
     */
    private String[] owlFiles;

    public String[] getOwlFile() {
        return owlFiles;
    }

    public void setOwlFile(String[] owlFile) {
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
            new TerminologyGenerator().generate(
                    this.owlFiles,
                    this.packageName,
                    this.outputDirectory );
        } catch ( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
            throw new MojoExecutionException( e.getMessage() );
        }
    }
}



