package org.drools.semantics.builder.reasoner;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.DrlDumper;
import org.drools.semantics.builder.DLTemplateManager;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.ResourceType;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashMap;
import java.util.Map;

public class TemplateRecognitionRuleBuilder {


    protected boolean useTMS = true;
    protected boolean usePropertyReactivity = false;
    protected boolean debug = true;

    private static DrlDumper dumper = new DrlDumper();
    private static DrlParser parser = new DrlParser();



    public String createDRL( OWLOntology onto, OntoModel model ) {

        Map<OWLClassExpression,OWLClassExpression> definitions = new DLogicTransformer( onto ).getDefinitions();

        Map<String,Object> params = new HashMap<String, Object>();
            params.put( "definitions",  definitions );
            params.put( "debug", debug );
            params.put( "useTMS", useTMS );
            params.put( "usePR", usePropertyReactivity );
            params.put( "context", new DLRecognitionBuildContext() );
            params.put( "rootClass", org.w3._2002._07.owl.Thing.class.getName() );


        CompiledTemplate drlTemplate = DLTemplateManager.getDataModelRegistry( ModelFactory.CompileTarget.RL ).getNamedTemplate( "recognitionRule.drlt" );
        String drl = TemplateRuntime.execute( drlTemplate, model, params ).toString().trim();

        drl = validateAndClean( drl );

        System.out.println( "******************************************************************************" );
        System.out.println( "******************************************************************************" );
        System.out.println( "******************************************************************************" );
        System.out.println( drl );
        System.out.println( "******************************************************************************" );
        System.out.println( "******************************************************************************" );
        System.out.println( "******************************************************************************" );

        return drl;
    }



    private String validateAndClean( String drl ) {

        System.out.println( cleanWhites( drl ) );


        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write( kieServices.getResources().newByteArrayResource( drl.getBytes() )
                           .setSourcePath( "test.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = kieServices.newKieBuilder( kfs );
        kieBuilder.buildAll();
        if ( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) ) {
            throw new IllegalStateException( kieBuilder.getResults().getMessages( Message.Level.ERROR ).toString() );
        }

        try {
            return dumper.dump( parser.parse( false, drl ) );
        } catch ( DroolsParserException e ) {
            // MUST not happen here!
            throw new IllegalStateException( "FATAL : parser could not parse a resource validated by the KnowledgeBuilder" );
        }
    }

    private String cleanWhites( String drl ) {
        return drl.replaceAll( "^ +| +$|( )+", "$1" ).replaceAll( "\\s*\n+\\s*(\\s*\n+\\s*)+", "\n" );
    }




}
