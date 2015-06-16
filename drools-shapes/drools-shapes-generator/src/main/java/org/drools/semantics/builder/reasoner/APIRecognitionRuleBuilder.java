package org.drools.semantics.builder.reasoner;

import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.api.AccumulateDescrBuilder;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.FieldDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.api.TypeDeclarationDescrBuilder;
import org.drools.compiler.lang.api.impl.PackageDescrBuilderImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.base.evaluators.IsAEvaluatorDefinition;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.semantics.NamedIndividual;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.semantics.utils.NameUtils;
import org.drools.shapes.terms.TermsNames;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLQuantifiedDataRestriction;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.w3._2002._07.owl.Thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIRecognitionRuleBuilder {

    protected Map<OWLClassExpression,OWLClassExpression> definitions;
    protected DLRecognitionBuildContext context = new DLRecognitionBuildContext();

    protected boolean useTMS                    = true;
    protected boolean usePropertyReactivity     = true;
    protected boolean debug                     = true;
    protected boolean refract                   = true;
    protected boolean useMetaClass              = true;
    protected String rootClass                  = Thing.class.getCanonicalName();

    protected boolean redeclare                 = false;
    
    protected OntoModel model;

    public APIRecognitionRuleBuilder( OntoModel model ) {
        this.model = model;
    }

    public String createDRL() {
        definitions = preprocessDefinitions( new DLogicTransformer( model.getOntology() ).getDefinitions() );

        PackageDescr root = visit( definitions );

        String drl = validateAndClean( root );

        System.out.println( "******************************************************************************" );
        System.out.println( "******************************************************************************" );
        System.out.println( "******************************************************************************" );
        System.out.println( drl );
        System.out.println( "******************************************************************************" );
        System.out.println( "******************************************************************************" );
        System.out.println( "******************************************************************************" );

        return drl;
    }

    protected PackageDescr visit( final Map<OWLClassExpression, OWLClassExpression> definitions ) {
        PackageDescrBuilder builder = PackageDescrBuilderImpl.newPackage();

        builder.name( model.getDefaultPackage() );

        builder.newImport().target( NamedIndividual.class.getName() ).end()
                .newImport().target( Traitable.class.getName() ).end()
                .newImport().target( Thing.class.getName() ).end();

        if ( redeclare ) {
            buildDeclarations( builder );
        }

        for ( OWLClassExpression k : definitions.keySet() ) {
            if ( ! k.isAnonymous() ) {
                buildRecognitionRule( k.asOWLClass(), definitions.get( k ), builder.newRule() );
            }
        }

        return builder.getDescr();
    }

    protected void buildRecognitionRule( OWLClass klass, OWLClassExpression defn, RuleDescrBuilder rule ) {
        context.clearBindings();
        String fqn =  model.getConcept( klass.getIRI().toQuotedString() ).getFullyQualifiedName();

        rule.name( "Recognition " + klass.getIRI().toString() );
        rule.attribute( "no-loop", "" );

        StringBuilder rhs = new StringBuilder();
        if ( debug ) {
            rhs.append( "\t" ).append( "System.out.println( \"Recognized \" + " )
                    .append( context.getScopedIdentifier() )
                    .append( " + \" as an instance of " ).append( fqn )
                    .append( " by rule \"  + " ).append( " drools.getRule().getName() " )
            .append( " ); \n" );
        }

        processOr( rule.lhs(), (OWLObjectUnionOf) defn, fqn, null );

        if ( ! useMetaClass ) {
            rhs.append( "\t" ).append( "don( " )
                    .append( context.getScopedIdentifier() ).append( ", " )
                    .append( fqn ).append( ".class" )
                    .append( useTMS ? ", true " : ", false" ).append( " );" ).append( "\n" );
        } else {
            rhs.append( "\t" ).append( fqn ).append( "_" )
                    .append( ".don( " ).append( context.getScopedIdentifier() ).append( " );" ).append( "\n" );
        }
        rule.rhs( rhs.toString() );
    }

    protected void processOr( CEDescrBuilder parent, OWLObjectUnionOf union, String typeName, Object source ) {
        CEDescrBuilder or = parent.or();
        context.clearBindings();
        for ( OWLClassExpression expr : union.getOperandsAsList() ) {
            processAnd( or, (OWLObjectIntersectionOf) expr, typeName, source );
        }
    }

    protected void processAnd( CEDescrBuilder or, OWLObjectIntersectionOf intersection, String typeName, Object source ) {
        CEDescrBuilder and = or.and();
        processArg( and, intersection, typeName, source );
    }

    protected void processArg( CEDescrBuilder parent, OWLClassExpression arg, String typeName, Object source ) {

        PatternDescrBuilder pattern = parent.pattern();

        pattern.type( rootClass ).constraint( context.getScopedIdentifier() + " := core " );

        if ( source != null ) {
            pattern.constraint( "core memberOf " + source );
        }
        if ( refract && ! useTMS ) {
            if ( typeName != null ) {
                pattern.constraint( isA( "this ", false, typeName ) );
            }
        }

        if ( arg instanceof OWLNaryBooleanClassExpression ) {
            for ( OWLClassExpression subArg : ( (OWLNaryBooleanClassExpression) arg ).getOperandsAsList() ) {
                if ( ! subArg.isAnonymous() ) {
                    pattern.constraint( isA( "this ", true, subArg ) );
                } else if ( subArg instanceof OWLObjectSomeValuesFrom && isDenotes( (OWLObjectSomeValuesFrom) subArg ) ) {
                    //,@{ arg.property.IRI.fragment } denotes @code{ String cd = getConcept( arg.filler ); } @{cd}
                } else if ( subArg instanceof OWLObjectComplementOf && ! ( (OWLObjectComplementOf) subArg ).getOperand().isAnonymous() ) {
                    pattern.constraint( isA( "this", false, ( (OWLObjectComplementOf) subArg ).getOperand() ) );
                } else if ( subArg instanceof OWLQuantifiedObjectRestriction || subArg instanceof OWLObjectCardinalityRestriction
                        || subArg instanceof OWLQuantifiedDataRestriction || subArg instanceof OWLDataCardinalityRestriction ) {
                    String constr = propBinding( (OWLQuantifiedRestriction) subArg );
                    if ( constr != null ) {
                        pattern.constraint( constr );
                    }
                } else if ( subArg instanceof OWLObjectOneOf ) {
                    pattern.constraint( oneOf( (OWLObjectOneOf) subArg ) );
                } else if ( subArg instanceof OWLObjectComplementOf && ( (OWLObjectComplementOf) subArg ).getOperand() instanceof OWLQuantifiedObjectRestriction ) {
                    String constr = propBinding( (OWLQuantifiedRestriction) ( (OWLObjectComplementOf) subArg ).getOperand() );
                    if ( constr != null ) {
                        pattern.constraint( constr );
                    }
                }
            }
            for ( OWLClassExpression subArg : ( (OWLNaryBooleanClassExpression) arg ).getOperandsAsList() ) {
                if ( subArg.isAnonymous()
                     && ! ( subArg instanceof OWLObjectComplementOf && ! ( (OWLObjectComplementOf) subArg ).getOperand().isAnonymous() )
                     && ! ( subArg instanceof OWLObjectOneOf ) ) {
                    nestedAtom( parent, subArg );
                }
            }
            context.clearBindings();
        }

    }

    private boolean isDenotes( OWLObjectSomeValuesFrom arg ) {
        if ( ! ( arg.getFiller() instanceof OWLObjectUnionOf ) ) {
            return false;
        }
        List<OWLClassExpression> subArgs = ( (OWLObjectUnionOf) arg.getFiller() ).getOperandsAsList();
        if ( subArgs.isEmpty() || ! ( subArgs.get( 0 ) instanceof OWLObjectSomeValuesFrom ) ) {
            return false;
        }
        OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) subArgs.get( 0 );
        if ( some.getProperty().asOWLObjectProperty().getIRI().equals( TermsNames.EXPRESSES ) ) {
            return true;
        }
        return false;
    }

    protected String isA( String subj, boolean positive, OWLClassExpression arg ) {
        return isA( subj, positive, model.getConcept( arg.asOWLClass().getIRI().toQuotedString() ).getFullyQualifiedName() );
    }

    protected String isA( String subj, boolean positive, String arg ) {
        StringBuilder sb = new StringBuilder();
        sb.append( subj );
        if ( ! positive ) {
            sb.append( " not " );
        }
        sb.append( " " ).append( IsAEvaluatorDefinition.ISA.getOperatorString() ).append( " " );
        sb.append( arg ).append( ".class" );
        return sb.toString();
    }

    protected String propBinding( OWLQuantifiedRestriction arg ) {
        String key = null;
        if ( arg instanceof OWLQuantifiedObjectRestriction ) {
            key = ( (OWLQuantifiedObjectRestriction) arg ).getProperty().asOWLObjectProperty().getIRI().toQuotedString();
        } else if ( arg instanceof OWLQuantifiedDataRestriction ) {
            key = ( (OWLQuantifiedDataRestriction) arg ).getProperty().asOWLDataProperty().getIRI().toQuotedString();
        } else if ( arg instanceof OWLObjectCardinalityRestriction ) {
            key = ( (OWLObjectCardinalityRestriction) arg ).getProperty().asOWLObjectProperty().getIRI().toQuotedString();
        } else if ( arg instanceof OWLDataCardinalityRestriction ) {
            key = ( (OWLDataCardinalityRestriction) arg ).getProperty().asOWLDataProperty().getIRI().toQuotedString();
        }

        String pName = model.getProperty( key ).getName();
        String pKey = context.getPropertyKey( pName );

        StringBuilder sb = new StringBuilder();
        if ( ! context.isPropertyBound( pKey ) ) {
            context.bindProperty( pName );

            String dom = model.getProperty( key ).getDomain().getFullyQualifiedName();
            //sb.append( isA( "this", true, dom ) );
            //sb.append( ", " );
            sb.append( pKey ).append( " : " ).append( "this" ).append( "#" ).append( dom ).append( "." ).append( pName );
            return sb.toString();
        } else {
            return null;
        }
    }

    protected String oneOf( OWLObjectOneOf ones ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "uri.toString() " );
        List<OWLIndividual> individuals = new ArrayList( ones.getIndividuals() );
        int N = individuals.size();
        for ( int j = 0; j < N; j++ ) {
            sb.append( " == " ).append( "\"" ).append( individuals.get( j ).asOWLNamedIndividual().getIRI().toString() ).append( "\"" );
            if ( j != N - 1 ) {
                sb.append( " || " );
            }
        }
        return sb.toString();
    }

    protected void nestedAtom( CEDescrBuilder parent, OWLClassExpression expr ) {
        if ( expr instanceof OWLObjectComplementOf ) {
            negAtom( parent, (OWLObjectComplementOf) expr );
        } else if ( expr instanceof OWLObjectSomeValuesFrom ) {
            someAtom( parent, (OWLObjectSomeValuesFrom) expr );
        } else if ( expr instanceof OWLObjectCardinalityRestriction ) {
            numAtom( parent, (OWLObjectCardinalityRestriction) expr );
        } else if ( expr instanceof OWLDataSomeValuesFrom ) {
            someData( parent, ( OWLDataSomeValuesFrom ) expr );
        } else if ( expr instanceof OWLDataAllValuesFrom ) {
            allData( parent, (OWLDataAllValuesFrom) expr );
        }
    }


    protected void negAtom( CEDescrBuilder parent, OWLObjectComplementOf expr ) {
        OWLClassExpression operand = expr.getOperand();
        OWLQuantifiedRestriction restr = (OWLQuantifiedRestriction) operand;
        String key = ((OWLObjectPropertyExpression) restr.getProperty()).asOWLObjectProperty().getIRI().toQuotedString();
        String pName = model.getProperty( key ).getName();
        processOr( parent.not(), (OWLObjectUnionOf) restr.getFiller(), null, context.getPropertyKey( pName ) );
    }

    protected void someAtom( CEDescrBuilder parent, OWLObjectSomeValuesFrom expr ) {
        String key = expr.getProperty().asOWLObjectProperty().getIRI().toQuotedString();
        String pName = model.getProperty( key ).getName();
        String src = context.getPropertyKey( pName );

        context.push();
        processOr( parent.exists(), (OWLObjectUnionOf) expr.getFiller(), null, src );
        context.pop();
    }

    protected void numAtom( CEDescrBuilder parent, OWLObjectCardinalityRestriction expr ) {
        String key = expr.getProperty().asOWLObjectProperty().getIRI().toQuotedString();
        String pName = model.getProperty( key ).getName();
        String src = context.getPropertyKey( pName );

        context.push();
        AccumulateDescrBuilder acc = parent.accumulate();
        acc.function( "count", "$num", false, "1" );
        if ( expr instanceof OWLObjectMinCardinality ) {
            acc.constraint( "$num" + " >= " + expr.getCardinality() );
        } else if ( expr instanceof OWLObjectMaxCardinality ) {
            acc.constraint( "$num" + " <= " + expr.getCardinality() );
        } else if ( expr instanceof OWLObjectExactCardinality ) {
            acc.constraint( "$num" + " == " + expr.getCardinality() );
        }
        processOr( acc.source(), (OWLObjectUnionOf) expr.getFiller(), null, src );
        context.pop();
    }


    protected void someData( CEDescrBuilder parent, OWLDataSomeValuesFrom expr ) {
        String key = expr.getProperty().asOWLDataProperty().getIRI().toQuotedString();
        String pName = model.getProperty( key ).getName();
        String src = context.getPropertyKey( pName );

        PatternDescrBuilder pattern = parent.exists().pattern().from().expression( src );
        if ( expr.getFiller() instanceof OWLDatatype ) {
            pattern.type( NameUtils.builtInTypeToWrappingJavaType( expr.getFiller().toString() ) );
        } else {
            pattern.type( Object.class.getName() ).constraint( dataExpr( expr.getFiller() ) );
        }
    }

    protected void allData( CEDescrBuilder parent, OWLDataAllValuesFrom expr ) {
        String key = expr.getProperty().asOWLDataProperty().getIRI().toQuotedString();
        String pName = model.getProperty( key ).getName();
        String src = context.getPropertyKey( pName );

        PatternDescrBuilder pattern = parent.forall().pattern().from().expression( src );
        if ( expr.getFiller() instanceof OWLDatatype ) {
            pattern.type( NameUtils.builtInTypeToWrappingJavaType( expr.getFiller().toString() ) );
        } else {
            pattern.type( Object.class.getName() ).constraint( dataExpr( expr.getFiller() ) );
        }
    }

    protected void numData( CEDescrBuilder parent, OWLDataCardinalityRestriction expr ) {
        String key = expr.getProperty().asOWLDataProperty().getIRI().toQuotedString();
        String pName = model.getProperty( key ).getName();
        String src = context.getPropertyKey( pName );

        AccumulateDescrBuilder acc = parent.accumulate();
        acc.function( "count", "$num", false, "1" );
        if ( expr instanceof OWLDataMinCardinality ) {
            acc.constraint( "$num" + " >= " + expr.getCardinality() );
        } else if ( expr instanceof OWLDataMaxCardinality ) {
            acc.constraint( "$num" + " <= " + expr.getCardinality() );
        } else if ( expr instanceof OWLDataExactCardinality ) {
            acc.constraint( "$num" + " == " + expr.getCardinality() );
        }

        PatternDescrBuilder pattern = acc.source().pattern().from().expression( src );
        if ( expr.getFiller() instanceof OWLDatatype ) {
            pattern.type( NameUtils.builtInTypeToWrappingJavaType( expr.getFiller().toString() ) );
        } else {
            pattern.type( Object.class.getName() ).constraint( dataExpr( expr.getFiller() ) );
        }
    }

    protected String dataExpr( OWLDataRange expr ) {
        if ( expr instanceof OWLDataComplementOf ) {
            return "! " + dataExpr( ( (OWLDataComplementOf) expr ).getDataRange() );
        } else if ( expr instanceof OWLDataIntersectionOf ) {
            OWLDataIntersectionOf and = (OWLDataIntersectionOf) expr;
            List<OWLDataRange> args = new ArrayList<OWLDataRange>( and.getOperands() );
            int N = args.size();
            StringBuilder sb = new StringBuilder();
            for ( int j = 0; j < N; j++ ) {
                sb.append( "this instanceof " ).append( dataExpr( args.get( j ) ) );
                if ( j != N - 1 ) {
                    sb.append( " && " );
                }
            }
            return sb.toString();
        } else if ( expr instanceof OWLDataUnionOf ) {
            OWLDataUnionOf or = (OWLDataUnionOf) expr;
            List<OWLDataRange> args = new ArrayList<OWLDataRange>( or.getOperands() );
            int N = args.size();
            StringBuilder sb = new StringBuilder();
            for ( int j = 0; j < N; j++ ) {
                sb.append( "this instanceof " ).append( dataExpr( args.get( j ) ) );
                if ( j != N - 1 ) {
                    sb.append( " || " );
                }
            }
            return sb.toString();
        } else if ( expr instanceof OWLDataOneOf ) {
            OWLDataOneOf ones = (OWLDataOneOf) expr;
            List<OWLLiteral> args = new ArrayList<OWLLiteral>( ones.getValues() );
            int N = args.size();
            StringBuilder sb = new StringBuilder( "this " );
            for ( int j = 0; j < N; j++ ) {
                sb.append( " == " ).append( args.get( j ) );
                if ( j != N - 1 ) {
                    sb.append( " || " );
                }
            }
            return sb.toString();
        } else {
            return NameUtils.builtInTypeToWrappingJavaType( expr.toString() );
        }
    }


    protected void buildDeclarations( PackageDescrBuilder builder ) {
        for ( Concept con : model.getConcepts() ) {
            TypeDeclarationDescrBuilder tdb = builder.newDeclare().type();

            tdb.setTrait( true ).name( con.getFullyQualifiedName() );
            for ( Concept sup : con.getSuperConcepts() ) {
                tdb.superType( sup.getFullyQualifiedName() );
            }
            if ( usePropertyReactivity ) {
                tdb.newAnnotation( PropertyReactive.class.getSimpleName() );
            }

            for ( PropertyRelation prop : con.getProperties().values() ) {
                if ( ! prop.isRestricted() && ! prop.isInherited() ) {
                    FieldDescrBuilder field = tdb.newField( prop.getName() );
                    if ( prop.isSimple() ) {
                        field.type( prop.getTarget().getFullyQualifiedName() );
                    } else {
                        field.type( List.class.getName() ).initialValue( "new " + ArrayList.class.getName() + "()" )
                                .newAnnotation( "genericType" ).value( prop.getTarget().getFullyQualifiedName() );
                    }
                }
            }

        }



    }


    protected Map<OWLClassExpression, OWLClassExpression> preprocessDefinitions( Map<OWLClassExpression, OWLClassExpression> definitions ) {
        Map<OWLClassExpression,OWLClassExpression> filteredDefs = new HashMap<OWLClassExpression, OWLClassExpression>();
        for ( OWLClassExpression key : definitions.keySet() ) {
            boolean filtered = false;
            OWLClassExpression def = definitions.get( key );
            if ( key.asOWLClass().getIRI().getFragment().contains( "Filler" ) ) {
                filtered = true;
            } else {
                if ( def instanceof OWLObjectUnionOf ) {
                    OWLObjectUnionOf or = (OWLObjectUnionOf) def;
                    if ( or.getOperandsAsList().size() == 1 ) {
                        OWLClassExpression inner = or.getOperandsAsList().iterator().next();
                        if ( inner instanceof OWLObjectSomeValuesFrom && ( (OWLObjectSomeValuesFrom) inner ).getProperty().getNamedProperty().getIRI().getFragment().equals( "denotes" ) ) {
                            filtered = true;
                        }
                    }
                }
            }
            if ( ! filtered ) {
                filteredDefs.put( key, def );
            }

        }
        return filteredDefs;
    }


    protected String validateAndClean( PackageDescr root ) {
        String drl = new DrlDumper().dump( root );

        KieHelper kh = new KieHelper(
//                EvaluatorOption.get( "denotes", new EvaluatorDefinition() {
//                @Override
//                public String[] getEvaluatorIds() {
//                    return new String[]{ "denotes" };
//                }
//                }  )
        );
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

        return cleanWhites( drl );
    }



    protected String cleanWhites( String drl ) {
        return drl.replaceAll( "^ +| +$|( )+", "$1" ).replaceAll( "\\s*\n+\\s*(\\s*\n+\\s*)+", "\n" );
    }


    public APIRecognitionRuleBuilder setUseTMS( boolean useTMS ) {
        this.useTMS = useTMS;
        return this;
    }

    public APIRecognitionRuleBuilder setUsePropertyReactivity( boolean usePropertyReactivity ) {
        this.usePropertyReactivity = usePropertyReactivity;
        return this;
    }

    public APIRecognitionRuleBuilder setDebug( boolean debug ) {
        this.debug = debug;
        return this;
    }

    public APIRecognitionRuleBuilder setUseMetaClass( boolean useMetaClass ) {
        this.useMetaClass = useMetaClass;
        return this;
    }

    public APIRecognitionRuleBuilder setRootClass( String rootClass ) {
        this.rootClass = rootClass;
        return this;
    }

    public APIRecognitionRuleBuilder setRedeclare( boolean redeclare ) {
        this.redeclare = redeclare;
        return this;
    }

    public APIRecognitionRuleBuilder setRefract( boolean refract ) {
        this.refract = refract;
        return this;
    }


}
