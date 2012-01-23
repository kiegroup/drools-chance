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
import org.drools.semantics.builder.model.JarModel;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.SemanticXSDModel;
import org.drools.semantics.builder.model.compilers.ModelCompiler;
import org.drools.semantics.builder.model.compilers.ModelCompilerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
    private boolean delegateInference = true;

    public boolean isDelegateInference() {
        return delegateInference;
    }

    public void setDelegateInference(boolean delegateInference) {
        this.delegateInference = delegateInference;
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
    private boolean preserveInheritanceInImpl = false;

    public boolean isPreserveInheritanceInImpl() {
        return preserveInheritanceInImpl;
    }

    public void setPreserveInheritanceInImpl(boolean preserveInheritanceInImpl) {
        this.preserveInheritanceInImpl = preserveInheritanceInImpl;
    }




    public void execute() throws MojoExecutionException {

        String slash = System.getProperty("file.separator");
        String target = outputDirectory.getAbsolutePath() + slash + "generated-sources" + slash;

        File ontoFile = new File( ontology );
        if ( ! ontoFile.exists() ) {
            throw new MojoExecutionException( " File not found : " + ontology );
        }
        
        if ( new File( target ).exists() ) {
            getLog().info( "Target folder " + target + " exists, skipping generation process" );
            return;
        }

        DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();
        Resource res = ResourceFactory.newFileResource( ontology );
        factory.setInferenceStrategy( isDelegateInference() ? DLFactory.INFERENCE_STRATEGY.EXTERNAL : DLFactory.INFERENCE_STRATEGY.INTERNAL );
        OntoModel results = factory.buildModel( getModelName(), res );


        ModelCompiler.Mode mode = isPreserveInheritanceInImpl() ? ModelCompiler.Mode.HIERARCHY : ModelCompiler.Mode.FLAT;
        if ( isPreserveInheritanceInImpl() ) {
            results.flatten();
        } else {
            results.elevate();
        }

        if ( isGenerateInterfaceJar() || isGenerateInterfaces() ) {

            ModelCompiler jcompiler =  ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.JAR );
            jcompiler.setMode( mode );
            JarModel jarModel = (JarModel) jcompiler.compile( results );

            if ( isGenerateInterfaces() ) {
                jarModel.save( target + "java" );
            }

            if ( isGenerateInterfaceJar() ) {
                try {
                    FileOutputStream fos = new FileOutputStream( outputDirectory.getAbsolutePath() + slash + getModelName() + ".jar" );
                    byte[] content = jarModel.buildJar().toByteArray();

                    fos.write( content, 0, content.length );
                    fos.flush();
                    fos.close();
                } catch ( IOException e ) {
                    throw new MojoExecutionException( e.getMessage() );
                }
            }
        }

        /**************************************************************************************************************/

        if ( isGenerateDefaultImplClasses() ) {
            ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
            compiler.setMode( mode );
            SemanticXSDModel xsdModel = (SemanticXSDModel) compiler.compile( results );

            File dir = new File( target + "/META-INF" );
            if ( ! dir.exists() ) {
                dir.mkdirs();
            }

            try {
                FileOutputStream fos = new FileOutputStream( target + "/META-INF/" + getModelName() +".xsd" );
                xsdModel.stream( fos );
                fos.flush();
                fos.close();
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }


            try {
                FileOutputStream fos = new FileOutputStream( target + "/META-INF/bindings.xjb" );
                xsdModel.streamBindings( fos );
                fos.flush();
                fos.close();
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }


            try {
                FileOutputStream fos = new FileOutputStream( target + "/META-INF/empire.annotation.index" );
                xsdModel.streamIndex( fos );
                fos.flush();
                fos.close();
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }

        }

//        private boolean generateTraitDRL = true;



    }
}
