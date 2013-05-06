package org.drools.semantics.builder.reasoner;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.factmodel.traits.Thing;
import org.drools.io.impl.ByteArrayResource;
import org.drools.lang.DrlDumper;
import org.drools.lang.api.CEDescrBuilder;
import org.drools.lang.api.DeclareDescrBuilder;
import org.drools.lang.api.DescrFactory;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.api.PatternDescrBuilder;
import org.drools.lang.api.RuleDescrBuilder;
import org.drools.lang.api.TypeDeclarationDescrBuilder;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AnnotatedBaseDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.rule.TypeDeclaration;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class RecognitionRuleBuilder {


    boolean useTMS = true;
    boolean usePropertyReactivity = true;

    int ruleCounter = 0;


    public String createDRL( OWLOntology onto, OntoModel model ) {

        PackageDescrBuilder packBuilder = DescrFactory.newPackage();

        packBuilder.name( model.getDefaultPackage() );

//        packBuilder.newImport().target( Thing.class.getName() ).end();
//        packBuilder.newImport().target( org.w3._2002._07.owl.Thing.class.getName() ).end();

        createDeclares( onto, model, packBuilder );

        createRulesFromDefinitions( new DLogicTransformer( onto ).getDefinitions(), packBuilder, model );

        String drl = new DrlDumper().dump( packBuilder.getDescr() );

        System.out.println( drl );

        validate( drl );

        return drl;
    }

    private void createRulesFromDefinitions( Map<OWLClassExpression, OWLClassExpression> definitions, PackageDescrBuilder packBuilder, OntoModel ontoModel ) {
        for ( OWLClassExpression key : definitions.keySet() ) {
            if ( ! key.isAnonymous() ) {
                processDefinition( key.asOWLClass(), definitions.get( key ), packBuilder, ontoModel );
            }
        }
    }


    private void createDeclares( OWLOntology onto, OntoModel model, PackageDescrBuilder packBuilder ) {

        for ( Concept con : model.getConcepts() ) {

            TypeDeclarationDescrBuilder dek = packBuilder.newDeclare().type();
            dek
                .name( con.getFullyQualifiedName() )
                .newAnnotation( TypeDeclaration.Kind.ID ).value( TypeDeclaration.Kind.TRAIT.name() ).end()
//                .newAnnotation( "propertyReactive" )
            ;
            for ( Concept sup : con.getSuperConcepts() ) {

                dek.superType( sup.getFullyQualifiedName() );
            }
            for ( PropertyRelation prop : con.getProperties().values() ) {
                if ( ! prop.isRestricted() && ! prop.isInherited() ) {
//                    dek.newField( prop.getName() ).type( prop.getTarget().getFullyQualifiedName() ).end();
                    dek.newField( prop.getName() )
                            .type( List.class.getName() )
                            .initialValue( "new java.util.ArrayList()" )
                            .newAnnotation( "unsupportedGenericType" ).value( prop.getTarget().getFullyQualifiedName() ).end()
                            .end();
                }
            }

        }
    }

    private void validate( String drl ) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            throw new IllegalStateException( kBuilder.getErrors().toString() );
        }
    }

    private void processDefinition( OWLClass klass, OWLClassExpression def, PackageDescrBuilder packBuilder, OntoModel model ) {

        BuildStack context = new BuildStack();
        RuleDescrBuilder ruleBuilder = packBuilder.newRule();

        ruleBuilder.name( "Rule " + ruleCounter++ + " >> " + klass.getIRI() );

        processLHS( klass, def, ruleBuilder.lhs(), model, context );

        processRHS( klass, ruleBuilder, model, context );

    }

    private void processRHS( OWLClass klass, RuleDescrBuilder ruleBuilder, OntoModel model, BuildStack context ) {
        ruleBuilder.rhs(
                doDon( klass, ruleBuilder, model, context )
        );
    }

    private String doDon( OWLClass klass, RuleDescrBuilder ruleBuilder, OntoModel model, BuildStack context ) {
        return "" +
                "System.out.println( \"Recognized \" + " + context.getScopedIdentifier() + " + \" as an instance of " + klass.getIRI().getFragment() + " by rule \" + drools.getRule().getName()  ); \n" +
                "" +
                "\tdon" +
                "(" +
                "  " + context.getScopedIdentifier() + ", " +
                model.getConcept( klass.getIRI().toQuotedString() ).getFullyQualifiedName() + ".class, " +
                "  true " +
                "); \n";

    }

    private void processLHS( OWLClass klass, OWLClassExpression def, CEDescrBuilder<RuleDescrBuilder, AndDescr> lhs, OntoModel model, BuildStack context ) {
        processOrExpression( klass, def, lhs.or(), model, context );

        // the NOT would prevent the use of TMS
//        lhs
//            .not()
//                .pattern()
//                    .type( model.getConcept( klass.getIRI().toQuotedString() ).getFullyQualifiedName() )
//                    .bind( context.getScopedIdentifier(), "core", true )
//                .end()
//            .end();
    }

    private void processOrExpression( OWLClass klass, OWLClassExpression expr, CEDescrBuilder<? extends CEDescrBuilder, OrDescr> or, OntoModel model, BuildStack context ) {
        OWLObjectUnionOf intersect = (OWLObjectUnionOf) expr;
        for ( OWLClassExpression arg : intersect.getOperands() ) {
            processAndExpression( klass, arg, or, model, context );
        }
        or.end();
    }

    private void processAndExpression( OWLClass klass, OWLClassExpression expr, CEDescrBuilder<? extends CEDescrBuilder,OrDescr> or, OntoModel model, BuildStack context ) {
        CEDescrBuilder<? extends CEDescrBuilder,AndDescr> and = or.and();

        PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends BaseDescr>> pb = and.pattern();

        initPattern( pb, klass, model, context );


        OWLObjectIntersectionOf intersect = (OWLObjectIntersectionOf) expr;

        for ( OWLClassExpression arg : intersect.getOperands() ) {
            processAtomExpression( arg, and, pb, model, context );
        }

        and.end();
    }

    private void initPattern( PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends BaseDescr>> pb,
                              OWLClass excludedClass,
                              OntoModel model,
                              BuildStack context ) {

        pb.type( getRootClass() )
                .bind( context.getScopedIdentifier(), "core", true )
        ;

        String parent = context.peekParent();

        String source = context.getSource();
        if ( source != null ) {
            pb.constraint( "core memberOf " + source );
        }

        if ( excludedClass != null ) {
            pb.constraint( "this not isA " + model.getConcept( excludedClass.getIRI().toQuotedString() ).getFullyQualifiedName() + ".class" );
        }
    }

    private void processAtomExpression(OWLClassExpression expr,
                                       CEDescrBuilder<? extends CEDescrBuilder, AndDescr> and,
                                       PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends BaseDescr>> pb,
                                       OntoModel model,
                                       BuildStack context) {
        if (!expr.isAnonymous()) {

            OWLClass klass = expr.asOWLClass();
            pb.constraint("this isA " + model.getConcept(klass.getIRI().toQuotedString()).getFullyQualifiedName() + ".class");

        } else if (expr instanceof OWLObjectComplementOf) {

            OWLObjectComplementOf neg = (OWLObjectComplementOf) expr;
            OWLClass klass = neg.getOperand().asOWLClass();
            pb.constraint("this not isA " + model.getConcept(klass.getIRI().toQuotedString()).getFullyQualifiedName() + ".class");

        } else if (expr instanceof OWLObjectSomeValuesFrom) {

            OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) expr;

            PropertyRelation prop = model.getProperty(some.getProperty().asOWLObjectProperty().getIRI().toQuotedString());

            String propKey = context.getPropertyKey(prop.getName());
            if (!context.isPropertyBound(propKey)) {
                context.bindProperty(prop.getName());
//                pb.bind( propKey, "this#" + prop.getDomain().getFullyQualifiedName() + "." + prop.getName(), false );
                pb.bind(propKey, "fields[ \"" + prop.getName() + "\" ]", false);
            }


            context.push();
            context.setSource(propKey);
            processOrExpression(null, some.getFiller(), and.exists().or(), model, context);
            context.resetSource();
            context.pop();

        } else {

            context.push();
            PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends BaseDescr>> sub = and.pattern();
            initPattern(sub, null, model, context);
            context.pop();

        }
    }

    public String getRootClass() {
        return org.w3._2002._07.owl.Thing.class.getName();
    }


    private class BuildStack {

        private int counter = 0;
        private Stack<BuildContext> vars = new Stack<BuildContext>();
        private String source;

        private BuildStack() {
            push();
        }

        public void push() {
            vars.push( new BuildContext( "$x_" + counter++ ) );
        }

        public void pop() {
            vars.pop();
        }

        public String getScopedIdentifier() {
            return vars.peek().getVar();
        }

        public BuildContext getScopedContext() {
            return vars.peek();
        }

        public boolean isPropertyBound( String propKey ) {
            return vars.peek().isPropertyBound(propKey);
        }

        public String bindProperty( String prop ) {
            return vars.peek().bindProperty( prop );
        }

        public String getPropertyKey( String prop ) {
            return vars.peek().getPropertyKey( prop );
        }

        public String peekParent() {
            BuildContext temp = vars.pop();
            BuildContext parent = vars.size() > 0 ? vars.peek() : null;
            vars.push( temp );
            return parent != null ? parent.getVar() : null;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSource() {
            return source;
        }

        public void resetSource() {
            source = null;
        }
    }

    private class BuildContext {
        private String var;
        private Map<String,PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends AnnotatedBaseDescr>>> patterns;
        private Map<String,String> propertyVars = new HashMap<String,String>();

        private BuildContext(String var) {
            this.var = var;
        }

        public String getVar() {
            return var;
        }

        public void addPattern( String type, PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends AnnotatedBaseDescr>> builder ) {
            patterns.put( type, builder );
        }

        public PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends AnnotatedBaseDescr>> getBuilder( String type ) {
            return patterns.get( type );
        }

        public boolean hasBuilder( String key ) {
            return patterns.containsKey( key );
        }

        public String bindProperty( String property ) {
            String propKey = getPropertyKey( property );
            if ( ! propertyVars.containsKey( propKey) ) {
                propertyVars.put( propKey, property );
            }
            return propKey;
        }

        public boolean isPropertyBound( String propKey ) {
            return propertyVars.containsKey( propKey );
        }

        public String getPropertyKey( String prop ) {
            return var + "_" + prop;
        }
    }

}
