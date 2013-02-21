package org.drools.shapes;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.drools.rule.builder.dialect.asm.ClassGenerator;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.DRLModel;
import org.drools.semantics.builder.model.JarModel;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.SemanticXSDModel;
import org.drools.semantics.builder.model.SubConceptOf;
import org.drools.semantics.builder.model.XSDModel;
import org.drools.semantics.builder.model.compilers.ModelCompiler;
import org.drools.semantics.builder.model.compilers.ModelCompilerFactory;
import org.drools.semantics.builder.model.compilers.SemanticXSDModelCompiler;
import org.drools.semantics.builder.model.compilers.XSDModelCompiler;
import org.drools.semantics.utils.NameUtils;
import org.drools.semantics.utils.NamespaceUtils;
import org.jvnet.hyperjaxb3.maven2.Hyperjaxb3Mojo;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.w3._2002._07.owl.Thing;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class OntoModelCompiler {


    public static final List<String> defaultOptions = Arrays.asList(
            "-extension",
            "-Xjaxbindex",
            "-Xannotate",
            "-Xinheritance",
//                                        "-XtoString",
            "-Xcopyable",
            "-Xmergeable",
//                                        "-Xvalue-constructor",
            "-Xfluent-api",
            "-Xkey-equality",
            "-Xsem-accessors",
            "-Xdefault-constructor",
            "-Xmetadata",
            "-XxcludeResolved",
            "-Xinject-code"
    );

    public static final List<String> fullOptions = Arrays.asList(
            "-extension",
            "-Xjaxbindex",
            "-Xannotate",
            "-Xinheritance",
            "-XtoString",
            "-Xcopyable",
            "-Xmergeable",
            "-Xvalue-constructor",
            "-Xfluent-api",
            "-Xkey-equality",
            "-Xsem-accessors",
            "-Xdefault-constructor",
            "-Xmetadata",
            "-XxcludeResolved",
            "-Xinject-code"
    );

    public static final List<String> minimalOptions = Arrays.asList(
            "-extension",
            "-Xjaxbindex",
            "-Xannotate",
            "-Xinheritance",
//                                        "-XtoString",
//                                        "-Xcopyable",
//                                        "-Xmergeable",
//                                        "-Xvalue-constructor",
//                                        "-Xfluent-api",
            "-Xkey-equality",
            "-Xsem-accessors",
            "-Xdefault-constructor",
            "-Xmetadata",
            "-XxcludeResolved",
            "-Xinject-code"
    );


    public enum MOJO_VARIANTS {
        JPA2( "jpa2" );

        private String label;
        MOJO_VARIANTS( String lab ) {
            label = lab;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum COMPILATION_OPTIONS {
        DEFAULT( defaultOptions ), MINIMAL( minimalOptions ), FULL( fullOptions );

        private List<String> options;
        COMPILATION_OPTIONS( List<String> opts ) {
            options = opts;
        }

        public List<String> getOptions() {
            return options;
        }
    }


    public enum AXIOM_INFERENCE {
        LITE( DLFactory.liteAxiomGenerators ), DEFAULT( DLFactory.defaultAxiomGenerators ), FULL( DLFactory.fullAxiomGenerators );

        private List<InferredAxiomGenerator<? extends OWLAxiom>> gens;

        AXIOM_INFERENCE( List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators ) {
            gens = axiomGenerators;
        }

        public List<InferredAxiomGenerator<? extends OWLAxiom>> getGenerators() {
            return gens;
        }
    }

    private File folder;
    private OntoModel model;


    public static final String METAINF = "META-INF";
    public static final String DRL = "DRL";
    public static final String JAVA = "java";
    public static final String XJC = "xjc";
    public static final String CLASSES = "classes";


    public static String mavenTarget = "target";
    public static String mavenSource = mavenTarget + File.separator + "generated-sources";




    public static String metaInfDirName = mavenSource + File.separator + METAINF;
    public static String drlDirName = mavenSource + File.separator + DRL;
    public static String javaDirName = mavenSource + File.separator + JAVA;
    public static String xjcDirName = mavenSource + File.separator + XJC;

    public static String binDirName = mavenTarget + File.separator + CLASSES;


    private File metaInfDir;
    private File drlDir;
    private File javaDir;
    private File xjcDir;

    private File binDir;

    private List<File> preexistingSchemas = new ArrayList<File>();
    private List<File> preesistingBindings = new ArrayList<File>();



    public OntoModelCompiler( OntoModel model, File rootFolder ) {
        if ( ! rootFolder.exists() ) {
            rootFolder.mkdirs();
        }
        this.folder = rootFolder;
        initDirectories();

        this.model = model;

        lookupExistingSchemas();
    }

    private void lookupExistingSchemas() {
        File folder = getMetaInfDir();
        for ( File f : folder.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.endsWith( ".xsd" ) && ! "owlThing.xsd".equals( name );
            }
        } ) ) {
            preexistingSchemas.add( f );
        }
        for ( File f : folder.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.endsWith( ".xjb" ) && ! "global.xjb".equals( name );
            }
        } ) ) {
            preesistingBindings.add( f );
        }

    }

    private void initDirectories() {

        metaInfDir = new File( folder.getPath() + File.separator + metaInfDirName );
        if ( ! metaInfDir.exists() ) {
            metaInfDir.mkdirs();
        }
        drlDir = new File( folder.getPath() + File.separator + drlDirName );
        if ( ! drlDir.exists() ) {
            drlDir.mkdirs();
        }
        javaDir = new File( folder.getPath() + File.separator + javaDirName );
        if ( ! javaDir.exists() ) {
            javaDir.mkdirs();
        }
        xjcDir = new File( folder.getPath() + File.separator + xjcDirName );
        if ( ! xjcDir.exists() ) {
            xjcDir.mkdirs();
        }
        binDir = new File( folder.getPath() + File.separator + binDirName );
        if ( ! binDir.exists() ) {
            binDir.mkdirs();
        }
    }

    public void clearSources() {
        for ( File f : metaInfDir.listFiles() ) {
            f.delete();
        }
    }



    public boolean existsResult() {
        return javaDir.listFiles().length > 0
                || xjcDir.listFiles().length > 0
                || drlDir.listFiles().length > 0;

    }



    public List<Diagnostic<? extends JavaFileObject>> compileOnTheFly(List<String> options, MOJO_VARIANTS variant) {

        streamJavaInterfaces( false );

        streamXSDsWithBindings( true );
        mojo( options, variant );

        List<Diagnostic<? extends JavaFileObject>> diagnostics = doCompile();

        return diagnostics;
    }




    public void fixResolvedClasses() {
        for ( Concept con : model.getConcepts() ) {
            if ( con.getResolvedAs() != Concept.Resolution.NONE ) {
                if ( con.getChosenProperties().size() > 0 ) {
                    // This is very likely an extension/restriction of the original concept, so we need to redefine it
                    String extPack = model.getDefaultPackage() + "." + con.getPackage();
                    model.removeConcept( con );
                    Concept original = con.clone();
                    original.getSubConcepts().clear();
                    original.getSubConcepts().add( con );
                    con.getSuperConcepts().clear();
                    con.addSuperConcept( original );
                    con.setChosenSuperConcept( original );
                    con.setPackage( extPack );

                    URI xuri = NameUtils.packageToNamespaceURI( extPack );
                    con.setNamespace( xuri.toASCIIString() );
                    con.setIri( IRI.create( NameUtils.separatingName( xuri.toASCIIString() ) + con.getName() ) );

                    model.addConcept( con );
                    model.addConcept( original );

                }
            }
        }
    }



    public boolean streamDRLDeclares() throws MojoExecutionException {
        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.DRL );
        boolean success;

        DRLModel drlModel = (DRLModel) compiler.compile( model );

        try {
            FileOutputStream fos = new FileOutputStream( getDrlDir().getPath() + File.separator + model.getName() + "_declare.drl" );
            success = drlModel.stream( fos );
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }
        return success;
    }


    public boolean streamJavaInterfaces( boolean includeJar ) {
        ModelCompiler jcompiler = ModelCompilerFactory.newModelCompiler(ModelFactory.CompileTarget.JAR);
        JarModel jarModel = (JarModel) jcompiler.compile( model );

        boolean success = jarModel.save( getJavaDir().getPath() );

        if ( includeJar ) {
            try {
                FileOutputStream fos = new FileOutputStream( getBinDir().getPath() + File.separator + model.getName() + ".jar" );
                byte[] content = jarModel.buildJar().toByteArray();

                fos.write( content, 0, content.length );
                fos.flush();
                fos.close();
            } catch ( IOException e ) {
                e.printStackTrace();
                return false;
            }
        }
        return success;
    }




    public boolean streamXSDsWithBindings( boolean includePersistenceConfiguration ) {
        SemanticXSDModelCompiler xcompiler = (SemanticXSDModelCompiler) ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xmlModel = (SemanticXSDModel) xcompiler.compile( model );

        boolean success = false;
        try {
            success = xmlModel.stream( getMetaInfDir() );
            success = xmlModel.streamBindings( getMetaInfDir() );

            if ( includePersistenceConfiguration ) {
                success = success && streamPersistenceConfigs( xcompiler, xmlModel );
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return success;
    }




    protected boolean streamPersistenceConfigs( SemanticXSDModelCompiler xcompiler, SemanticXSDModel xmlModel ) throws IOException {
        boolean success;

        xcompiler.mergeNamespacedPackageInfo( xmlModel );
        success = xmlModel.streamNamespacedPackageInfos( getXjcDir() );


        File f2 = new File( getMetaInfDir().getPath() + File.separator + "empire.configuration.file" );
        if ( f2.exists() ) {
            xcompiler.mergeEmpireConfig( f2, xmlModel );
        }
        FileOutputStream fos2 = new FileOutputStream( f2 );
        success = success && xmlModel.streamEmpireConfig( fos2 );
        fos2.flush();
        fos2.close();


        File f3 = new File( getMetaInfDir().getPath() + File.separator + "empire.annotation.index" );
        if ( f3.exists() ) {
            xcompiler.mergeIndex( f3, xmlModel );
        }
        FileOutputStream fos3 = new FileOutputStream( f3 );
        success = success && xmlModel.streamIndex( fos3 );
        fos3.flush();
        fos3.close();


        File f4 = new File( getMetaInfDir().getPath() + File.separator + "persistence-template-hibernate.xml" );
//        File f4 = new File( getXjcDir().getPath() + File.separator + "META-INF" + File.separator + "persistence.xml" );
//        if ( f4.exists() ) {
//            xcompiler.mergePersistenceXml( f4, xmlModel );
//        }
        FileOutputStream fos4 = new FileOutputStream( f4 );
        success = success && xmlModel.streamPersistenceXml( fos4 );
        fos4.flush();
        fos4.close();

        return success;
    }


    public boolean streamIndividualFactory() {
        ModelCompiler compiler = ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xsdModel;

        xsdModel = (SemanticXSDModel) compiler.compile( model );
        boolean success = false;

        try {
            String classPath = getXjcDir().getPath() + File.separator + xsdModel.getDefaultPackage().replace(".", File.separator);
            File f = new File( classPath );
            if ( ! f.exists() ) {
                f.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(  classPath + File.separator + "IndividualFactory.java" );
            success = xsdModel.streamIndividualFactory( fos );
            fos.flush();
            fos.close();


        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
        return success;
    }





    public List<Diagnostic<? extends JavaFileObject>> doCompile() {
        List<File> list = new LinkedList<File>();

//        Set<File> sourceFolders = new HashSet<File>();
//
//
//        for ( String packageName : model.getAllPackageNames() ) {
//            sourceFolders.add(
//                    new File( getXjcDir().getPath() + File.separator + packageName.replace(".", File.separator) )
//            );
//            sourceFolders.add(
//                    new File( getJavaDir().getPath() + File.separator + packageName.replace(".", File.separator) )
//            );
//        }
//
//        for ( File f : sourceFolders ) {
//            System.out.println( "************** COMPILER USING SRC FOLDERS AS " + f.getPath() );
//        }
//
//        sourceFolders.add(
//                new File( getXjcDir().getPath() + File.separator + "org.w3._2001.xmlschema".replace(".", File.separator) )
//        );
//
//
//        for ( File folder : sourceFolders ) {
//            if ( folder.exists() ) {
//                list.addAll( Arrays.asList( folder.listFiles( (FilenameFilter) new WildcardFileFilter( "*.java" ) ) ) );
//            }
//        }


        explore( getJavaDir(), list );
        explore( getXjcDir(), list );

//        for ( File f : list ) {
//            System.out.println( "************** COMPILER USING SRC FILE AS " + f.getPath() );
//        }

        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = jc.getStandardFileManager( diagnostics, null, null );
        Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles( list );
        List<String> jcOpts = Arrays.asList( "-d", getBinDir().getPath() );
        JavaCompiler.CompilationTask task = jc.getTask( null, fileManager, diagnostics, jcOpts, null, compilationUnits );
        task.call();

        copyMetaInfResources();

        return diagnostics.getDiagnostics();
    }

    private void explore( File dir, List<File> files ) {
        for ( File f : dir.listFiles() ) {
            if ( f.getName().endsWith( ".java" ) ) {
                files.add( f );
            }
            if ( f.isDirectory() ) {
//                System.out.println( "Looking for java in " + f.getPath() );
                explore( f, files );
            }
        }
    }


    private boolean copyMetaInfResources() {
        boolean success = true;

        success = success && copyMetaInfResources( getMetaInfDir() );
        success = success && copyMetaInfResources( new File( getXjcDir() + File.separator + METAINF ) );

        return success;
    }


    private boolean copyMetaInfResources( File src ) {
        File tgt = new File( getBinDir().getPath() + File.separator + METAINF );
        if ( ! tgt.exists() ) {
            tgt.mkdir();
        }

        for ( File f : src.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return ! name.contains( "template" );
            }
        }) ) {
            try {
                copyFile( f, tgt );
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void copyFile( File f, File tgtDir ) throws IOException {
//        System.out.println(" Trying to copy " + f.getName() + " into >>> " + tgtDir.getPath() );
        FileInputStream fis = new FileInputStream( f );
        byte[] buf = new byte[ fis.available() ];
        fis.read( buf );

        File tgt = new File( tgtDir.getPath() + File.separator + f.getName() );
        FileOutputStream fos = new FileOutputStream( tgt );
        fos.write( buf );

        fis.close();
        fos.flush();
        fos.close();
    }


    protected boolean streamMockPOM( File pom, String name ) {
        boolean success = false;
        try {
            FileOutputStream fos = new FileOutputStream( pom );
            byte[] content = ( "" +
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                    "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                    "  <modelVersion>4.0.0</modelVersion>\n" +
                    "  <groupId>org.test</groupId>\n" +
                    "  <artifactId>" + name + "</artifactId>\n" +
                    "</project>\n" +
                    "" ).getBytes();

            fos.write( content, 0, content.length );
            fos.flush();
            fos.close();

            success = true;
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return success;
    }


    public boolean mojo( List<String> args, MOJO_VARIANTS variant ) {
        boolean success = false;
        try {
            File pom = new File( folder.getPath() + File.separator + "pom.xml" );
            if ( ! pom.exists() ) {
                success = streamMockPOM( pom, model.getName() );
            }


            MavenProject mp = new MavenProject();

            final Hyperjaxb3Mojo mojo = new Hyperjaxb3Mojo();
            mojo.setVerbose( true );

            mojo.setBindingDirectory( getMetaInfDir() );
            mojo.setSchemaDirectory(getMetaInfDir());

            int j = 0;
            String[] excludedSchemas = new String[ preexistingSchemas.size() ];
            for ( File f : preexistingSchemas ) {
                excludedSchemas[ j++ ] = f.getName();
            }
            mojo.setSchemaExcludes( excludedSchemas );

            int k = 0;
            String[] excludedBindings = new String[ preesistingBindings.size() ];
            for ( File f : preesistingBindings ) {
                excludedBindings[ k++ ] = f.getName();
            }
            mojo.setBindingExcludes( excludedBindings );



            mojo.setGenerateDirectory( getXjcDir() );
            mojo.setExtension(true);
            mojo.variant = variant.getLabel();

            File perx = new File( getBinDir().getPath() + File.separator + "META-INF" + File.separator + "persistence.xml" );
            if ( perx.exists() ) {
                mojo.persistenceXml = perx;
                try {
                    Document dox = parseXML( perx );

                    XPath xpath = XPathFactory.newInstance().newXPath();
                    XPathExpression expr = xpath.compile( "//persistence-unit/@name" );

                    mojo.persistenceUnitName = (String) expr.evaluate( dox, XPathConstants.STRING );

                } catch ( Exception e ) {
                    mojo.persistenceXml = new File( getMetaInfDir() + File.separator + "persistence-template-hibernate.xml" );
                    mojo.persistenceUnitName = model.getName();
                }
            } else {
                mojo.persistenceXml = new File( getMetaInfDir() + File.separator + "persistence-template-hibernate.xml" );
                mojo.persistenceUnitName = model.getName();
            }

            mojo.generateEquals = false;
            mojo.generateHashCode = false;
            mojo.setProject( mp );

            mojo.setArgs( args );

            mojo.setForceRegenerate( true );
            mojo.execute();
            success = true;
        } catch (MojoExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }



    public File getMetaInfDir() {
        if ( ! metaInfDir.exists() ) { metaInfDir.mkdirs(); }
        return metaInfDir;
    }

    public File getDrlDir() {
        return drlDir;
    }

    public File getJavaDir() {
        if ( ! javaDir.exists() ) { javaDir.mkdirs(); }
        return javaDir;
    }

    public File getXjcDir() {
        if ( ! xjcDir.exists() ) { xjcDir.mkdirs(); }
        return xjcDir;
    }

    public File getBinDir() {
        if ( ! binDir.exists() ) { binDir.mkdirs(); }
        return binDir;
    }



    private Document parseXML( File f ) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        InputSource xSource = new InputSource( new FileInputStream( f ) );
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        return builder.parse( xSource );
    }


}
