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

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.DLFactoryBuilder;
import org.drools.semantics.builder.model.*;
import org.drools.semantics.builder.model.compilers.ModelCompiler;
import org.drools.semantics.builder.model.compilers.ModelCompilerFactory;
import org.drools.semantics.builder.model.compilers.XSDModelCompiler;
import org.w3._2002._07.owl.Thing;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.Collection;
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



    /**
     * @parameter default-value="false"
     */
    private boolean buildSpecXSDs = false;

    public boolean isBuildSpecXSDs() {
        return buildSpecXSDs;
    }

    public void setBuildSpecXSDs(boolean buildSpecXSDs) {
        this.buildSpecXSDs = buildSpecXSDs;
    }


    /**
     * @parameter default-value="false"
     */
    private boolean useExistingImplementations = false;

    public boolean isUseExistingImplementations() {
        return useExistingImplementations;
    }

    public void setUseExistingImplementations(boolean useExistingImplementations) {
        this.useExistingImplementations = useExistingImplementations;
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

        String slash = System.getProperty("file.separator");
        String target = outputDirectory.getAbsolutePath() + slash + "generated-sources" + slash;
        String metainf = "META-INF" + slash;
        String drlDir = "DRL" + slash;

        File ontoFile = new File( ontology );
        if ( ! ontoFile.exists() ) {
            throw new MojoExecutionException( " File not found : " + ontology );
        }

        if ( new File( target ).exists() ) {
            getLog().info( "Target folder " + target + " exists, skipping generation process" );
            return;
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

        factory.setInferenceStrategy( isDelegateInference() ? DLFactory.INFERENCE_STRATEGY.EXTERNAL : DLFactory.INFERENCE_STRATEGY.INTERNAL );
        OntoModel results = factory.buildModel( getModelName(), res );

        if ( isUseExistingImplementations() ) {
            results.resolve();
        }


        if ( isGenerateDRL() ) {
            ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.DRL );

            DRLModel drlModel;
            drlModel = (DRLModel) compiler.compile( results );

            File dir = new File( target + drlDir );
            if ( ! dir.exists() ) {
                dir.mkdirs();
            }

            try {
                FileOutputStream fos = new FileOutputStream( dir + slash + getModelName() +"_trait.drl" );
                drlModel.stream( fos );
                fos.flush();
                fos.close();
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }

        }


        if ( isGenerateDefaultImplClasses() && isBuildSpecXSDs() ) {

            ModelCompiler.Mode mode = isPreserveInheritanceInImpl() ? ModelCompiler.Mode.HIERARCHY : ModelCompiler.Mode.FLAT;
            if ( isPreserveInheritanceInImpl() ) {
                results.elevate();
            } else {
                results.flatten();
            }


            File dir = new File( target + metainf );
            if ( ! dir.exists() ) {
                dir.mkdirs();
            }

            ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
            compiler.setMode( mode );
            SemanticXSDModel xsdModel;

            ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
            ((XSDModelCompiler) compiler).setUseImplementation( false );
            ((XSDModelCompiler) compiler).setSchemaMode( "_$spec" );
            xsdModel = (SemanticXSDModel) compiler.compile( results );

            try {
                File fos = new File( target + metainf + slash + getModelName() +"_$spec.xsd" );
                xsdModel.stream( fos );
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }



            ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
            ((XSDModelCompiler) compiler).setUseImplementation( true );
            ((XSDModelCompiler) compiler).setSchemaMode( "_$impl" );
            xsdModel = (SemanticXSDModel) compiler.compile( results );

            try {
                File fos = new File( target + metainf + slash + getModelName() +"_$impl.xsd" );
                xsdModel.stream( fos );
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }


            ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( true );
            ((XSDModelCompiler) compiler).setUseImplementation( false );
            ((XSDModelCompiler) compiler).setSchemaMode( "_$full" );
            xsdModel = (SemanticXSDModel) compiler.compile( results );

            try {
                File fos = new File( target + metainf + slash + getModelName() +"_$full.xsd" );
                xsdModel.stream( fos );
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }
        }








        if ( isGenerateInterfaceJar() || isGenerateInterfaces() ) {

            ModelCompiler.Mode mode = isPreserveInheritanceInImpl() ? ModelCompiler.Mode.HIERARCHY : ModelCompiler.Mode.LEVELLED;
            if ( isPreserveInheritanceInImpl() ) {
                results.elevate();
            } else {
                results.raze();
            }

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

            ModelCompiler.Mode mode = isPreserveInheritanceInImpl() ? ModelCompiler.Mode.HIERARCHY : ModelCompiler.Mode.LEVELLED;
            if ( isPreserveInheritanceInImpl() ) {
                results.elevate();
            } else {
                results.raze();
            }

            if ( ! isBuildSpecXSDs() ) {
                File dir = new File( target + metainf  );
                if ( ! dir.exists() ) {
                    dir.mkdirs();
                }
            }

            ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
            compiler.setMode( mode );
            SemanticXSDModel xsdModel;





            ((XSDModelCompiler) compiler).setUseImplementation( false );
            ((XSDModelCompiler) compiler).setSchemaMode( "" );
            xsdModel = (SemanticXSDModel) compiler.compile( results );


            try {
                File fos = new File( target + metainf + slash + getModelName() +".xsd" );
                xsdModel.stream( fos );
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }


            try {
                File fos = new File( target + metainf + slash + "bindings.xjb" );
                xsdModel.streamBindings( fos );
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }


            try {
                FileOutputStream fos = new FileOutputStream( target + metainf + slash + "empire.annotation.index" );
                xsdModel.streamIndex( fos );
                fos.flush();
                fos.close();
            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }


            // namespace fix. For some reason, hj needs the (local) owl package to be assigned to the default namespace
            try {
                String classPathTemp = target + "xjc" + slash + Thing.class.getPackage().getName().replace(".", slash);
                File f2 = new File( classPathTemp );
                if ( ! f2.exists() ) {
                    f2.mkdirs();
                }

                FileOutputStream fos2 = new FileOutputStream(  classPathTemp + slash + "package-info.java" );
                xsdModel.streamNamespaceFix( fos2 );

                fos2.flush();
                fos2.close();
            } catch (Exception e) {

            }




        }

        if ( isGenerateIndividuals() ) {
            ModelCompiler.Mode mode = isPreserveInheritanceInImpl() ? ModelCompiler.Mode.HIERARCHY : ModelCompiler.Mode.LEVELLED;
            ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
            compiler.setMode( mode );
            SemanticXSDModel xsdModel;



            ((XSDModelCompiler) compiler).setTransientPropertiesEnabled( false );
            ((XSDModelCompiler) compiler).setUseImplementation( true );
            xsdModel = (SemanticXSDModel) compiler.compile( results );

            try {
                String classPath = target + "xjc" + slash + xsdModel.getDefaultPackage().replace(".", slash);
                File f = new File( classPath );
                if ( ! f.exists() ) {
                    f.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(  classPath + slash + "IndividualFactory.java" );
                xsdModel.streamIndividualFactory( fos );
                fos.flush();
                fos.close();


            } catch (Exception e) {
                throw new MojoExecutionException( e.getMessage() );
            }

        }

//        private boolean generateTraitDRL = true;



    }




}
