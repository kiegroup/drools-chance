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
import org.w3._2002._07.owl.Thing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
     * @parameter default-value="OntoModel.Mode.HIERARCHY.name()"
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
     * @parameter
     */
    private Properties packageNameOverrides;

    public Properties getPackageNameOverrides() {
        return packageNameOverrides;
    }

    public void setPackageNameOverrides( Properties packageNameOverrides ) {
        this.packageNameOverrides = packageNameOverrides;
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
     * @parameter default-value="false"
     */
    private boolean generateRecognitionRules = false;

    public boolean isGenerateRecognitionRules() {
        return generateRecognitionRules;
    }

    public void setGenerateRecognitionRules( boolean generateRecognitionRules ) {
        this.generateRecognitionRules = generateRecognitionRules;
    }


    /**
     * @parameter default-value="false"
     */
    private boolean useEnhancedNames = false;

    public boolean isUseEnhancedNames() {
        return useEnhancedNames;
    }

    public void setUseEnhancedNames( boolean useEnhancedNames ) {
        this.useEnhancedNames = useEnhancedNames;
    }


    /**
     * @parameter
     */
    private RecognitionRuleConfig recognitionRuleConfig = new RecognitionRuleConfig();

    public RecognitionRuleConfig getRecognitionRuleConfig() {
        return recognitionRuleConfig;
    }

    public void setRecognitionRuleConfig( RecognitionRuleConfig recognitionRuleConfig ) {
        this.recognitionRuleConfig = recognitionRuleConfig;
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
     * @parameter default-value="false"
     */
    private boolean generateMetaclasses = false;

    public boolean isGenerateMetaclasses() {
        return generateMetaclasses;
    }

    public void setGenerateMetaclasses( boolean generateMetaclasses ) {
        this.generateMetaclasses = generateMetaclasses;
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
    private List<String> compilationOptions = Collections.emptyList();

    public List<String> getCompilationOptions() {
        return compilationOptions;
    }

    public void setCompilationOptions( List<String> compilationOptions ) {
        this.compilationOptions = compilationOptions;
    }




    public void execute() throws MojoExecutionException {

        File target = new File( outputDirectory.getAbsolutePath() );

        OntoModel results = processOntology( mode );
        results.setMinimal( compilationOptions.contains( "-minimal" ) );
        results.setStandalone( compilationOptions.contains( "-standalone" ) );

        OntoModelCompiler compiler = new OntoModelCompiler( results, target, isUseEnhancedNames() );


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

        if ( isGenerateMetaclasses() ) {
            compiler.streamMetaclasses( this.isGenerateDefaultImplClasses() );
        }

        if ( isGenerateIndividuals() ) {
            compiler.streamIndividualFactory();
        }

        if ( isGenerateRecognitionRules() ) {
            compiler.streamRecognitionRules( recognitionRuleConfig.toProperties() );
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
        InputStream ontologyStream = getResourceStream( ontology );

        DLFactory factory = DLFactoryBuilder.newDLFactoryInstance();
        if ( ontologyImports == null ) {
            ontologyImports = Collections.emptyList();
        }

        int n = 1 + ontologyImports.size();
        Resource[] res = new Resource[ n ];
        int j = 0;
        for ( String imp : ontologyImports ) {
            InputStream importStream = getResourceStream( imp );
            res[ j++ ] = ResourceFactory.newInputStreamResource( importStream );
        }
        res[j] = ResourceFactory.newInputStreamResource( ontologyStream );

        DLFactoryConfiguration conf = new DLFactoryConfiguration();
        conf.setMode( mode );
        conf.setAxiomGens( OntoModelCompiler.AXIOM_INFERENCE.valueOf( axiomInference.toUpperCase() ).getGenerators() );
        conf.setDisableFullReasoner( this.disableFullReasoning );

        Map<String, String> mappings = null;
        if ( packageNameOverrides != null ) {
            mappings = new HashMap<String, String>();
            Enumeration<?> names = packageNameOverrides.propertyNames();
            while ( names.hasMoreElements() ) {
                String ns = (String) names.nextElement();
                mappings.put( ns, packageNameOverrides.getProperty( ns ) );
            }
        }

        return factory.buildModel( getModelName(),
                                   mappings,
                                   res,
                                   conf,
                                   Thread.currentThread().getContextClassLoader() );
    }

    private InputStream getResourceStream( String path ) {
        InputStream ontologyStream = null;
        File ontoFile = new File( path );
        if ( ontoFile.exists() ) {
            try {
                ontologyStream = new FileInputStream( new File( path ) );
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }
        } else {
            try {
                ontologyStream = ResourceFactory.newClassPathResource( path ).getInputStream();
            } catch ( Exception e ) {
                try {
                    InputStream tempStream = ShapeCaster.class.getResourceAsStream( path );

                    byte[] data = new byte[ tempStream.available() ];
                    tempStream.read( data );
                    ontologyStream = new ByteArrayInputStream( data );
                } catch ( Exception e1 ) {
                    e1.printStackTrace();
                }

            }
        }
        return ontologyStream;
    }



    public static class RecognitionRuleConfig {
        protected boolean useTMS                    = true;
        protected boolean usePropertyReactivity     = true;
        protected boolean debug                     = true;
        protected boolean refract                   = true;
        protected boolean useMetaClass              = true;
        protected boolean redeclare                 = false;
        protected String rootClass                  = Thing.class.getCanonicalName();

        protected List<String> definitions          = new ArrayList<String>();
        protected List<String> ignores              = new ArrayList<String>();

        public boolean isUseTMS() {
            return useTMS;
        }

        public void setUseTMS( boolean useTMS ) {
            this.useTMS = useTMS;
        }

        public boolean isUsePropertyReactivity() {
            return usePropertyReactivity;
        }

        public void setUsePropertyReactivity( boolean usePropertyReactivity ) {
            this.usePropertyReactivity = usePropertyReactivity;
        }

        public boolean isDebug() {
            return debug;
        }

        public void setDebug( boolean debug ) {
            this.debug = debug;
        }

        public boolean isRefract() {
            return refract;
        }

        public void setRefract( boolean refract ) {
            this.refract = refract;
        }

        public boolean isUseMetaClass() {
            return useMetaClass;
        }

        public void setUseMetaClass( boolean useMetaClass ) {
            this.useMetaClass = useMetaClass;
        }

        public boolean isRedeclare() {
            return redeclare;
        }

        public void setRedeclare( boolean redeclare ) {
            this.redeclare = redeclare;
        }

        public String getRootClass() {
            return rootClass;
        }

        public void setRootClass( String rootClass ) {
            this.rootClass = rootClass;
        }

        public List<String> getDefinitions() {
            return definitions;
        }

        public void setDefinitions( List<String> definitions ) {
            this.definitions = definitions;
        }

        public List<String> getIgnores() {
            return ignores;
        }

        public void setIgnores( List<String> ignores ) {
            this.ignores = ignores;
        }

        public Properties toProperties() {
            Properties prop = new Properties();
            prop.setProperty( "useTMS", Boolean.toString( useTMS ) );
            prop.setProperty( "usePropertyReactivity", Boolean.toString( usePropertyReactivity ) );
            prop.setProperty( "debug", Boolean.toString( debug ) );
            prop.setProperty( "refract", Boolean.toString( refract ) );
            prop.setProperty( "useMetaClass", Boolean.toString( useMetaClass ) );
            prop.setProperty( "redeclare", Boolean.toString( redeclare ) );
            if ( definitions != null && ! definitions.isEmpty() ) {
                prop.setProperty( "definitions", definitions.toString() );
            }
            if ( ignores != null && ! ignores.isEmpty() ) {
                prop.setProperty( "ignores", ignores.toString() );
            }
            prop.setProperty( "rootClass", rootClass );
            return  prop;
        }
    }

}
