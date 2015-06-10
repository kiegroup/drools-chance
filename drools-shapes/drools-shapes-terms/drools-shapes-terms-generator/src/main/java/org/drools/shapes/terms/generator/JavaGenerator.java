package org.drools.shapes.terms.generator;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class JavaGenerator {

    public void generate(Collection<CodeSystem> codeSystems, String packageName, File outputDir) {
        outputDir.mkdirs();

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        this.generateConcepts(codeSystems, packageName, outputDir, ve);
        this.generateVocabularyCatalog(packageName, outputDir, ve);
        this.generateCodeFactory(packageName, outputDir, ve);
    }

    protected void generateConcepts(Collection<CodeSystem> codeSystems, String packageName, File outputDir, VelocityEngine ve) {
        Template template = ve.getTemplate("templates/concepts-java.vm");

        for(CodeSystem codeSystem : codeSystems) {
            if ( ! codeSystem.getConcepts().isEmpty() ) {
                String className = getClassName( codeSystem.getCodeSystemName() );

                VelocityContext context = new VelocityContext();
                context.put( "codeSystem", codeSystem );
                context.put( "className", className );
                context.put( "packageName", packageName );
                context.put( "JavaGenerator", JavaGenerator.class );

                this.writeToFile( template, context, outputDir, packageName, className );
            }
        }
    }

    protected void generateVocabularyCatalog(String packageName, File outputDir, VelocityEngine ve) {
        Template template = ve.getTemplate("templates/vocabularycatalog-java.vm");

        VelocityContext context = new VelocityContext();
        context.put("packageName", packageName);

        this.writeToFile(template, context, outputDir, packageName, "VocabularyCatalog");
    }

    protected void generateCodeFactory(String packageName, File outputDir, VelocityEngine ve) {
        Template template = ve.getTemplate("templates/codefactory-java.vm");

        VelocityContext context = new VelocityContext();
        context.put("packageName", packageName);

        this.writeToFile(template, context, outputDir, packageName, "CodeFactory");
    }

    private void writeToFile(
            Template template,
            VelocityContext context,
            File outputDir,
            String packageName,
            String className) {
        File outputFile = createJavaFile(outputDir, packageName, className);

        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFile);
            template.merge( context, writer );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private File createJavaFile(File outputDir, String packageName, String className) {
        File packageDir = new File(outputDir, packageName.replace('.', File.separatorChar));
        packageDir.mkdirs();

        File outputFile = new File(packageDir, className + ".java");
        try {
            if(! outputFile.createNewFile()){
                throw new RuntimeException("Could not create file at: " + outputFile.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputFile;
    }

    protected static String getClassName(String codeSystemName) {
        return StringUtils.capitalise( codeSystemName.replaceAll("[^a-zA-Z0-9]", "_")  );
    }

    public static String getPropertyName(String propertyName) {
        propertyName = fixSpecialCharacters( propertyName );
        return StringUtils.capitalise( propertyName.replaceAll("[^a-zA-Z0-9]", "_")  );
    }

    private static String fixSpecialCharacters( String s ) {
        s = s.replaceAll( ">", "_GT_" );
        s = s.replaceAll( "<", "_LT_" );
        return s;
    }

}
