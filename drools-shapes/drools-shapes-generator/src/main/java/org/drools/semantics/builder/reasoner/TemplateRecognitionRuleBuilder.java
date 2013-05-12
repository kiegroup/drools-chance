package org.drools.semantics.builder.reasoner;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.io.impl.ByteArrayResource;
import org.drools.lang.DrlDumper;
import org.drools.lang.api.CEDescrBuilder;
import org.drools.lang.api.DescrFactory;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.api.PatternDescrBuilder;
import org.drools.lang.api.RuleDescrBuilder;
import org.drools.lang.api.TypeDeclarationDescrBuilder;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AnnotatedBaseDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.TypeDeclaration;
import org.drools.semantics.builder.DLTemplateManager;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            throw new IllegalStateException( kBuilder.getErrors().toString() );
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
