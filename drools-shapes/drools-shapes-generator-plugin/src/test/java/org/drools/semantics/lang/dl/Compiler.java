package org.drools.semantics.lang.dl;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.drools.semantics.builder.model.JarModel;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.SemanticXSDModel;
import org.drools.semantics.builder.model.XSDModel;
import org.drools.semantics.builder.model.compilers.ModelCompiler;
import org.drools.semantics.builder.model.compilers.ModelCompilerFactory;
import org.drools.semantics.builder.model.compilers.SemanticXSDModelCompiler;
import org.drools.semantics.builder.model.compilers.XSDModelCompiler;
import org.junit.Assert;
import org.jvnet.hyperjaxb3.maven2.Hyperjaxb3Mojo;
import org.w3._2002._07.owl.Thing;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;


public class Compiler {


    public List<Diagnostic<? extends JavaFileObject>> compileOnTheFly(List<String> options, MOJO_VARIANTS variant) {

        streamJavaInterfaces( false );
        streamXSDs();
        streamBindings( true );
        mojo( options, variant );
        List<Diagnostic<? extends JavaFileObject>> diagnostics = doCompile();

        return diagnostics;
    }


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


    public Compiler( OntoModel model, File rootFolder ) {

        if ( ! rootFolder.exists() ) {
            rootFolder.mkdirs();
        }
        this.folder = rootFolder;
        initDirectories();

        this.model = model;
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


    public boolean streamJavaInterfaces( boolean includeJar ) {
        ModelCompiler jcompiler = ModelCompilerFactory.newModelCompiler(ModelFactory.CompileTarget.JAR);
        JarModel jarModel = (JarModel) jcompiler.compile( model );

        boolean success = jarModel.save( javaDir.getPath() );

        if ( includeJar ) {
            try {
                FileOutputStream fos = new FileOutputStream( binDir.getPath() + File.separator + model.getName() + ".jar" );
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


    public boolean streamXSDs() {
        XSDModelCompiler compiler = (XSDModelCompiler) ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSD );
        XSDModel xModel = (XSDModel) compiler.compile( model );
        boolean success = false;
        try {
            success = xModel.stream( new File( metaInfDir + File.separator + model.getName() + ".xsd") );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean streamBindings( boolean includePersistenceConfiguration ) {
        SemanticXSDModelCompiler xcompiler = (SemanticXSDModelCompiler) ModelCompilerFactory.newModelCompiler( ModelFactory.CompileTarget.XSDX );
        SemanticXSDModel xmlModel = (SemanticXSDModel) xcompiler.compile( model );

        boolean success = false;
        try {
            success = xmlModel.streamBindings( new File( metaInfDir.getPath() + File.separator + "bindings.xjb" ) );

            if ( includePersistenceConfiguration ) {
                success = success && streamPersistenceConfigs( xmlModel );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return success;
    }

    protected boolean streamPersistenceConfigs( SemanticXSDModel xmlModel ) throws IOException {
        boolean success;

        String classPathTemp = xjcDir.getPath() + File.separator + Thing.class.getPackage().getName().replace( ".", File.separator );
        File f2 = new File( classPathTemp );
        if ( ! f2.exists() ) {
            f2.mkdirs();
        }
        FileOutputStream fos2 = new FileOutputStream( classPathTemp + File.separator + "package-info.java" );
        success = xmlModel.streamNamespaceFix(fos2);
        fos2.flush();
        fos2.close();

        FileOutputStream fos = new FileOutputStream( metaInfDir.getPath() + File.separator + "empire.configuration.file" );
        success = success && xmlModel.streamEmpireConfig(fos);
        fos.flush();
        fos.close();

        FileOutputStream fos3 = new FileOutputStream( metaInfDir.getPath() + File.separator + "empire.annotation.index" );
        success = success && xmlModel.streamIndex( fos3 );
        fos3.flush();
        fos3.close();

        FileOutputStream fos4 = new FileOutputStream( metaInfDir.getPath() + File.separator + "persistence-template-hibernate.xml" );
        success = success && xmlModel.streamPersistenceXml(fos4);
        fos4.flush();
        fos4.close();

        return success;
    }


    public List<Diagnostic<? extends JavaFileObject>> doCompile() {
        File srcFolder1 = new File( javaDir.getPath() + File.separator + model.getDefaultPackage().replace( ".", File.separator ) );
        File srcFolder2 = new File( xjcDir.getPath() + File.separator + model.getDefaultPackage().replace(".", File.separator) );


        File srcFolder3 = new File( xjcDir.getPath() + File.separator + Thing.class.getPackage().getName().replace(".", File.separator) );
        File srcFolder4 = new File( xjcDir.getPath() + File.separator + "org.w3._2001.xmlschema".replace(".", File.separator) );
        List<File> list = new LinkedList<File>();

        list.addAll( Arrays.asList( srcFolder1.listFiles( (FilenameFilter) new WildcardFileFilter( "*.java" ) ) ) );
        list.addAll( Arrays.asList( srcFolder2.listFiles( (FilenameFilter) new WildcardFileFilter( "*.java" ) ) ) );
        list.addAll( Arrays.asList( srcFolder3.listFiles( (FilenameFilter) new WildcardFileFilter( "*.java" ) ) ) );
        list.addAll( Arrays.asList( srcFolder4.listFiles( (FilenameFilter) new WildcardFileFilter( "*.java" ) ) ) );

        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = jc.getStandardFileManager( diagnostics, null, null );
        Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles( list );
        List<String> jcOpts = Arrays.asList( "-d", binDir.getPath() );
        JavaCompiler.CompilationTask task = jc.getTask( null, fileManager, diagnostics, jcOpts, null, compilationUnits );
        task.call();

        copyMetaInfResources();

        return diagnostics.getDiagnostics();
    }



    private boolean copyMetaInfResources() {
        boolean success = true;

        success = success && copyMetaInfResources( metaInfDir );
        success = success && copyMetaInfResources( new File( xjcDir + File.separator + METAINF ) );

        return success;
    }


    private boolean copyMetaInfResources( File src ) {
        File tgt = new File( binDir.getPath() + File.separator + METAINF );
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
        System.out.println(" Trying to copy " + f.getName() + " into >>> " + tgtDir.getPath() );
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


    protected boolean streamMockPOM() {
        boolean success = false;

        File pom = new File( folder.getPath() + File.separator + "pom.xml" );
        try {
            FileOutputStream fos = new FileOutputStream( pom );
            byte[] content = ( "" +
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                    "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                    "  <modelVersion>4.0.0</modelVersion>\n" +
                    "  <groupId>org.test</groupId>\n" +
                    "  <artifactId>diamond</artifactId>\n" +
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
            success = streamMockPOM();

            MavenProject mp = new MavenProject();

            Hyperjaxb3Mojo mojo = new Hyperjaxb3Mojo();
            mojo.setVerbose( true );

            mojo.setBindingDirectory( metaInfDir );
            mojo.setSchemaDirectory( metaInfDir );
            mojo.setGenerateDirectory( xjcDir );
            mojo.setExtension( true );
            mojo.variant = variant.getLabel();

            mojo.persistenceXml = new File( metaInfDir + File.separator + "persistence-template-hibernate.xml" );

            mojo.generateEquals = false;
            mojo.generateHashCode = false;
            mojo.setProject( mp );

            mojo.setArgs( args );

            mojo.execute();
            success = true;
        } catch (MojoExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }



    public File getMetaInfDir() {
        return metaInfDir;
    }

    public File getDrlDir() {
        return drlDir;
    }

    public File getJavaDir() {
        return javaDir;
    }

    public File getXjcDir() {
        return xjcDir;
    }

    public File getBinDir() {
        return binDir;
    }



}
