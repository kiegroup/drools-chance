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
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.model.DRLModel;
import org.drools.semantics.builder.model.JarModel;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.SemanticXSDModel;
import org.drools.semantics.builder.model.compilers.ModelCompiler;
import org.drools.semantics.builder.model.compilers.ModelCompilerFactory;
import org.drools.semantics.builder.model.compilers.XSDModelCompiler;
import org.w3._2002._07.owl.Thing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

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



    private File target;
    private File metainf;
    private File drlDir;


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
     * @parameter default-value="false"
     */
    private boolean generateIndividuals = true;

    public boolean isGenerateIndividuals() {
        return generateIndividuals;
    }

    public void setGenerateIndividuals(boolean generateIndividuals) {
        this.generateIndividuals = generateIndividuals;
    }









    /**
     * @parameter default-value="false"
     */
    private boolean generateDRL = true;

    public boolean isGenerateDRL() {
        return generateDRL;
    }

    public void setGenerateDRL(boolean generateDRL) {
        this.generateDRL = generateDRL;
    }







    public void execute() throws MojoExecutionException {

        boolean exists = initTargetFolders();

        if ( exists ) {
            getLog().info( "Target folder " + target + " exists, skipping generation process" );
            return;
        }


        OntoModel results = processOntology( mode );

        if ( isGenerateDRL() ) {
            generateDRLDeclares(results);
        }

        if ( isGenerateInterfaceJar() || isGenerateInterfaces() ) {
            generateJavaInterfaces( results );
        }




        if ( isGenerateSpecXSDs() ) {
            generateSpecXSDs( results );
        }



        if ( isGenerateDefaultImplClasses() ) {
            generateDefaultImplClasses( results );
        }

        if ( isGenerateIndividuals() ) {
            generateIndividuals( results );
        }

    }




    private void generateIndividuals(OntoModel results) throws MojoExecutionException {
        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xsdModel;


        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
        ((XSDModelCompiler) compiler).setUseImplementation( true );
        xsdModel = (SemanticXSDModel) compiler.compile( results );

        try {
            String classPath = target.getAbsolutePath() + File.separator + "xjc" + File.separator + xsdModel.getDefaultPackage().replace(".", File.separator);
            File f = new File( classPath );
            if ( ! f.exists() ) {
                f.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(  classPath + File.separator + "IndividualFactory.java" );
            xsdModel.streamIndividualFactory( fos );
            fos.flush();
            fos.close();


        } catch (Exception e) {
            throw new MojoExecutionException( e.getMessage() );
        }
    }


    private void generateDefaultImplClasses(OntoModel results) throws MojoExecutionException {
        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xsdModel;

        ((XSDModelCompiler) compiler).setUseImplementation( false );
        ((XSDModelCompiler) compiler).setSchemaMode( XSDModelCompiler.XSDSchemaMode.JAXB );
        xsdModel = (SemanticXSDModel) compiler.compile( results );


        try {
            File fos = new File( metainf.getAbsolutePath() + File.separator + getModelName() + XSDModelCompiler.XSDSchemaMode.JAXB.getFileSuffix() + ".xsd" );
            xsdModel.stream( fos );
        } catch (Exception e) {
            throw new MojoExecutionException( e.getMessage() );
        }


        try {
            File fos = new File( metainf.getAbsolutePath() + File.separator + "bindings.xjb" );
            xsdModel.streamBindings( fos );
        } catch (Exception e) {
            throw new MojoExecutionException( e.getMessage() );
        }


        try {
            FileOutputStream fos = new FileOutputStream( metainf.getAbsolutePath() + File.separator + "empire.annotation.index" );
            xsdModel.streamIndex( fos );
            fos.flush();
            fos.close();
        } catch (Exception e) {
            throw new MojoExecutionException( e.getMessage() );
        }


        // namespace fix. For some reason, hj needs the (local) owl package to be assigned to the default namespace
        try {
            String classPathTemp = target.getAbsolutePath() + File.separator + "xjc" + File.separator + Thing.class.getPackage().getName().replace(".", File.separator);
            File f2 = new File( classPathTemp );
            if ( ! f2.exists() ) {
                f2.mkdirs();
            }

            FileOutputStream fos2 = new FileOutputStream(  classPathTemp + File.separator + "package-info.java" );
            xsdModel.streamNamespaceFix( fos2 );

            fos2.flush();
            fos2.close();
        } catch (Exception e) {

        }

    }

    private void generateSpecXSDs(OntoModel results) throws MojoExecutionException {

        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xsdModel;

        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
        ((XSDModelCompiler) compiler).setUseImplementation( false );
        ((XSDModelCompiler) compiler).setSchemaMode( XSDModelCompiler.XSDSchemaMode.SPEC );
        xsdModel = (SemanticXSDModel) compiler.compile( results );

        try {
            File fos = new File( metainf.getAbsolutePath() + File.separator + getModelName() + XSDModelCompiler.XSDSchemaMode.SPEC.getFileSuffix() + ".xsd" );
            xsdModel.stream( fos );
        } catch (Exception e) {
            throw new MojoExecutionException( e.getMessage() );
        }


        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
        ((XSDModelCompiler) compiler).setUseImplementation( true );
        ((XSDModelCompiler) compiler).setSchemaMode( XSDModelCompiler.XSDSchemaMode.IMPL );
        xsdModel = (SemanticXSDModel) compiler.compile( results );

        try {
            File fos = new File( metainf.getAbsolutePath() + File.separator + getModelName() + XSDModelCompiler.XSDSchemaMode.IMPL.getFileSuffix() + ".xsd" );
            xsdModel.stream( fos );
        } catch (Exception e) {
            throw new MojoExecutionException( e.getMessage() );
        }

        ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( true );
        ((XSDModelCompiler) compiler).setUseImplementation( false );
        ((XSDModelCompiler) compiler).setSchemaMode( XSDModelCompiler.XSDSchemaMode.FULL );
        xsdModel = (SemanticXSDModel) compiler.compile( results );

        try {
            File fos = new File( metainf.getAbsolutePath() + File.separator + getModelName() + XSDModelCompiler.XSDSchemaMode.FULL.getFileSuffix() + ".xsd" );
            xsdModel.stream( fos );
        } catch (Exception e) {
            throw new MojoExecutionException( e.getMessage() );
        }

    }

    private void generateJavaInterfaces( OntoModel results ) throws MojoExecutionException {
        ModelCompiler jcompiler =  ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.JAR );
        JarModel jarModel = (JarModel) jcompiler.compile( results );

        if ( isGenerateInterfaces() ) {
            jarModel.save( target.getAbsolutePath() + File.separator + "java" );
        }

        if ( isGenerateInterfaceJar() ) {
            try {
                FileOutputStream fos = new FileOutputStream( outputDirectory.getAbsolutePath() + File.separator + getModelName() + ".jar" );
                byte[] content = jarModel.buildJar().toByteArray();

                fos.write( content, 0, content.length );
                fos.flush();
                fos.close();
            } catch ( IOException e ) {
                throw new MojoExecutionException( e.getMessage() );
            }
        }

    }


    private boolean initTargetFolders() {
        String targetPath = outputDirectory.getAbsolutePath() + File.separator + "generated-sources" + File.separator;
        target = new File( targetPath );
        boolean exists = target.exists();
        if ( ! exists ) {
            target.mkdirs();
        }

        String metainfPath = "META-INF";
        metainf = new File( target.getAbsolutePath() + File.separator + metainfPath );
        if ( ! metainf.exists() ) {
            metainf.mkdirs();
        }

        String drlDirPath = "DRL";
        drlDir = new File( target.getAbsolutePath() + File.separator + drlDirPath );
        if ( ! drlDir.exists() ) {
            drlDir.mkdirs();
        }

        return exists;
    }




    private void generateDRLDeclares( OntoModel results ) throws MojoExecutionException {
        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.DRL );

        DRLModel drlModel;
        drlModel = (DRLModel) compiler.compile( results );

        try {
            FileOutputStream fos = new FileOutputStream( drlDir.getAbsolutePath() + File.separator + getModelName() +"_trait.drl" );
            drlModel.stream( fos );
            fos.flush();
            fos.close();
        } catch (Exception e) {
            throw new MojoExecutionException( e.getMessage() );
        }
    }


    private OntoModel processOntology( OntoModel.Mode mode ) throws MojoExecutionException {
        File ontoFile = new File( ontology );
        if ( ! ontoFile.exists() ) {
            throw new MojoExecutionException( " Ontology file not found : " + ontology );
        }

        DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();
        if ( ontologyImports == null ) {
            ontologyImports = Collections.emptyList();
        }

        int n = 1 + ontologyImports.size();
        Resource[] res = new Resource[ n ];
        int j = 0;
        for ( String imp : ontologyImports ) {
            res[j++] = ResourceFactory.newFileResource( imp );
        }
        res[j] = ResourceFactory.newFileResource( ontology );

        return factory.buildModel( getModelName(), res, mode );
    }


}
