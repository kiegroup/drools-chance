package org.drools.shapes;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.DLFactoryConfiguration;
import org.drools.semantics.builder.model.OntoModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Goal which creates various possible fact model representations from an ontology
 *
 * @goal cast
 *
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class ShapeCaster
        extends AbstractMojo
{


    /**
     * @parameter default-value="false"
     */
    private String inheritanceMode = OntoModel.Mode.HIERARCHY.name();
    private OntoModel.Mode mode;

    public String getInheritanceMode() {
        return inheritanceMode;
    }

    public void setInheritanceMode(String inheritanceMode) {
        this.inheritanceMode = inheritanceMode;
        this.mode = OntoModel.Mode.valueOf( inheritanceMode );
    }


    /**
     * @parameter default-value="./target/gen-sources"
     */
    private File outputDirectory;

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @parameter
     */
    private String persistenceTemplate;

    public String getPersistenceTemplate() {
        return persistenceTemplate;
    }

    public void setPersistenceTemplate(String persistenceTemplate) {
        this.persistenceTemplate = persistenceTemplate;
    }


    /**
     * @parameter
     */
    private String ontology;

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    /**
     * @parameter
     */
    private List<String> ontologyImports;

    public List<String> getOntologyImports() {
        return ontologyImports;
    }

    public void setOntologyImports(List<String> ontologyImports) {
        this.ontologyImports = ontologyImports;
    }


    /**
     * @parameter default-value="model"
     */
    private String modelName;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }



    /**
     * @parameter default-value="default"
     */
    private String axiomInference = OntoModelCompiler.AXIOM_INFERENCE.DEFAULT.name();

    public String getAxiomInference() {
        return axiomInference;
    }

    public void setAxiomInference( String axiomInference ) {
        this.axiomInference = axiomInference;
    }

    /**
     * @parameter default-value="false"
     */
    private boolean disableFullReasoning;

    public boolean isDisableFullReasoning() {
        return disableFullReasoning;
    }

    public void setDisableFullReasoning( boolean disableFullReasoning ) {
        this.disableFullReasoning = disableFullReasoning;
    }


    /**
     * @parameter default-value="true"
     */
    private boolean generateInterfaces = true;

    public boolean isGenerateInterfaces() {
        return generateInterfaces;
    }

    public void setGenerateInterfaces(boolean generateInterfaces) {
        this.generateInterfaces = generateInterfaces;
    }

    /**
     * @parameter default-value="false"
     */
    private boolean generateInterfaceJar = false;

    public boolean isGenerateInterfaceJar() {
        return generateInterfaceJar;
    }

    public void setGenerateInterfaceJar(boolean generateInterfaceJar) {
        this.generateInterfaceJar = generateInterfaceJar;
    }

    /**
     * @parameter default-value="true"
     */
    private boolean generateTraitDRL = true;

    public boolean isGenerateTraitDRL() {
        return generateTraitDRL;
    }

    public void setGenerateTraitDRL(boolean generateTraitDRL) {
        this.generateTraitDRL = generateTraitDRL;
    }








    /**
     * @parameter default-value="true"
     */
    private boolean generateDefaultImplClasses = true;

    public boolean isGenerateDefaultImplClasses() {
        return generateDefaultImplClasses;
    }

    public void setGenerateDefaultImplClasses(boolean generateDefaultImplClasses) {
        this.generateDefaultImplClasses = generateDefaultImplClasses;
    }


    /**
     * @parameter default-value="false"
     */
    private boolean generateSpecXSDs = false;

    public boolean isGenerateSpecXSDs() {
        return generateSpecXSDs;
    }

    public void setGenerateSpecXSDs(boolean generateSpecXSDs) {
        this.generateSpecXSDs = generateSpecXSDs;
    }

    /**
     * @parameter default-value="true"
     */
    private boolean generateIndividuals = true;

    public boolean isGenerateIndividuals() {
        return generateIndividuals;
    }

    public void setGenerateIndividuals(boolean generateIndividuals) {
        this.generateIndividuals = generateIndividuals;
    }







    /**
     * @parameter default-value="default"
     */
    private String compilationOptionsPackage = "default";

    public String getCompilationOptionsPackage() {
        return compilationOptionsPackage;
    }

    public void setCompilationOptionsPackage( String compilationOptionsPackage ) {
        this.compilationOptionsPackage = compilationOptionsPackage;
    }

    /**
     * @parameter
     */
    private List<String> compilationOptions;

    public List<String> getCompilationOptions() {
        return compilationOptions;
    }

    public void setCompilationOptions( List<String> compilationOptions ) {
        this.compilationOptions = compilationOptions;
    }




    public void execute() throws MojoExecutionException {

        File target = new File( outputDirectory.getAbsolutePath() );

        OntoModel results = processOntology( mode );
        OntoModelCompiler compiler = new OntoModelCompiler( results, target );


        if ( compiler.existsResult() ) {
            getLog().warn( "Target folder " + target + " seems to already contain generated code, " +
                    "the generation process will be skipped. Please consider using \'clean\' in the build process." );
            return;
        }


        if ( isGenerateTraitDRL() ) {
//            generateDRLDeclares(results);
            compiler.streamDRLDeclares();
        }

        if ( isGenerateInterfaceJar() || isGenerateInterfaces() ) {
//            generateJavaInterfaces( results );
            compiler.streamJavaInterfaces( isGenerateInterfaceJar() );
        }

        if ( isGenerateSpecXSDs() ) {
//            generateSpecXSDs( results );
        }



        if ( isGenerateDefaultImplClasses() ) {
            compiler.streamXSDsWithBindings( true, persistenceTemplate );
        }

        if ( isGenerateIndividuals() ) {
            compiler.streamIndividualFactory();
        }


        OntoModelCompiler.COMPILATION_OPTIONS opts = OntoModelCompiler.COMPILATION_OPTIONS.valueOf( getCompilationOptionsPackage().toUpperCase() );
        if ( opts == null ) {
            opts = OntoModelCompiler.COMPILATION_OPTIONS.DEFAULT;
        }
        Set<String> mergedOptions = new HashSet( opts.getOptions() );
        if ( compilationOptions != null && compilationOptions.size() > 0 ) {
            mergedOptions.addAll( compilationOptions );
        }

        compiler.mojo( new ArrayList( mergedOptions ),
                OntoModelCompiler.MOJO_VARIANTS.JPA2 );

    }





//    private void generateSpecXSDs(OntoModel results) throws MojoExecutionException {
//
//        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
//        SemanticXSDModel xsdModel;
//
//        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
//        ((XSDModelCompiler) compiler).setUseImplementation( false );
//        ((XSDModelCompiler) compiler).setSchemaMode( XSDModelCompiler.XSDSchemaMode.SPEC );
//        xsdModel = (SemanticXSDModel) compiler.compile( results );
//
//        try {
//            File fos = new File( metainf.getAbsolutePath() + File.separator + getModelName() + XSDModelCompiler.XSDSchemaMode.SPEC.getFileSuffix() + ".xsd" );
//            xsdModel.stream( fos );
//        } catch (Exception e) {
//            throw new MojoExecutionException( e.getMessage() );
//        }
//
//
//        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
//        ((XSDModelCompiler) compiler).setUseImplementation( true );
//        ((XSDModelCompiler) compiler).setSchemaMode( XSDModelCompiler.XSDSchemaMode.IMPL );
//        xsdModel = (SemanticXSDModel) compiler.compile( results );
//
//        try {
//            File fos = new File( metainf.getAbsolutePath() + File.separator + getModelName() + XSDModelCompiler.XSDSchemaMode.IMPL.getFileSuffix() + ".xsd" );
//            xsdModel.stream( fos );
//        } catch (Exception e) {
//            throw new MojoExecutionException( e.getMessage() );
//        }
//
//        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( true );
//        ((XSDModelCompiler) compiler).setUseImplementation( false );
//        ((XSDModelCompiler) compiler).setSchemaMode( XSDModelCompiler.XSDSchemaMode.FULL );
//        xsdModel = (SemanticXSDModel) compiler.compile( results );
//
//        try {
//            File fos = new File( metainf.getAbsolutePath() + File.separator + getModelName() + XSDModelCompiler.XSDSchemaMode.FULL.getFileSuffix() + ".xsd" );
//            xsdModel.stream( fos );
//        } catch (Exception e) {
//            throw new MojoExecutionException( e.getMessage() );
//        }
//
//    }


    private OntoModel processOntology( OntoModel.Mode mode ) throws MojoExecutionException {
        InputStream ontologyStream = null;
        File ontoFile = new File( ontology );
        if ( ontoFile.exists() ) {
            try {
                ontologyStream = new FileInputStream( new File( ontology ) );
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }
        } else {
            try {
                ontologyStream = ResourceFactory.newClassPathResource( ontology ).getInputStream();
            } catch ( IOException e ) {
                //
            }
        }

        DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();
        if ( ontologyImports == null ) {
            ontologyImports = Collections.emptyList();
        }

        int n = 1 + ontologyImports.size();
        Resource[] res = new Resource[ n ];
        int j = 0;
        for ( String imp : ontologyImports ) {
            if ( new File( imp ).exists() ) {
                res[j++] = ResourceFactory.newFileResource( imp );
            } else {
                res[j++] = ResourceFactory.newClassPathResource( imp );
            }
        }
        res[j] = ResourceFactory.newInputStreamResource( ontologyStream );

        DLFactoryConfiguration conf = new DLFactoryConfiguration();
        conf.setMode( mode );
        conf.setAxiomGens( OntoModelCompiler.AXIOM_INFERENCE.valueOf( axiomInference.toUpperCase() ).getGenerators() );
        conf.setDisableFullReasoner( this.disableFullReasoning );

        return factory.buildModel( getModelName(),
                                   res,
                                   conf,
                                   Thread.currentThread().getContextClassLoader() );
    }


}
