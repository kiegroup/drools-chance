package org.drools.shapes.terms.generator.plugin;

import edu.mayo.cts2.terms.TermsComparator;
import edu.mayo.cts2.terms.TermsNames;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.drools.semantics.builder.reasoner.CodeSystem;
import org.drools.shapes.terms.generator.JavaGenerator;
import org.drools.shapes.terms.generator.TerminologyGenerator;
import org.drools.shapes.terms.generator.util.Loader;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
            List<String> allFiles = new LinkedList<String>();

            URL root = TermsGeneratorPlugin.class.getResource( "/" + TermsNames.TERMS_METAMODEL_PACKAGE.replaceAll( "\\.", "/" ) );
            String path = root.getPath().substring( 0, root.getPath().lastIndexOf( "!" ) );
            if ( path.startsWith( "file:" ) ) {
                path = path.substring( "file:".length() );
            }
            JarFile jar = new JarFile( path );
            Enumeration<JarEntry> entries = jar.entries();
            while ( entries.hasMoreElements() ) {
                String name = entries.nextElement().getName();
                if ( name.endsWith( ".owl" ) ) {
                    allFiles.add( name );
                }
            }
            Collections.sort( allFiles, TermsComparator.getInstance() );

            for ( String owl : owlFiles ) {
                allFiles.add( owl );
            }
            OWLOntology ontology = new Loader().loadOntology( allFiles.toArray( new String[allFiles.size()] ) );

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



