package org.drools.chance.rule.builder;

import org.drools.chance.factmodel.Imperfect;
import org.drools.chance.common.ImperfectField;
import org.drools.chance.core.util.IntHashMap;
import org.drools.chance.reteoo.nodes.ChanceObjectTypeNode;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.*;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.InternalReadAccessor;

import java.util.ArrayList;
import java.util.List;

public class ChanceRulePatternBuilder extends PatternBuilder {


    public ChanceRulePatternBuilder() {
        super();
    }



    protected void processConstraintsAndBinds( final RuleBuildContext context,
                                               final PatternDescr patternDescr,
                                               final Pattern pattern ) {

        List constraints = patternDescr.getConstraint().getDescrs();
        List<? extends BaseDescr> temp = new ArrayList<BaseDescr>( patternDescr.getConstraint().getDescrs() );
        List<ConstraintConnectiveDescr> rootConstraints = new ArrayList<ConstraintConnectiveDescr>();

        IntHashMap<Boolean> impFlags = new IntHashMap<Boolean>();
        IntHashMap<Boolean> posFlags = new IntHashMap<Boolean>();

        boolean hasImperfectConstraint = ( patternDescr.getAnnotation( Imperfect.class.getSimpleName() ) != null );
        for ( BaseDescr b : temp ) {
            String expression;
            boolean isPositional = false;
            if ( b instanceof BindingDescr ) {
                BindingDescr bind = (BindingDescr) b;
                expression = bind.getVariable() + (bind.isUnification() ? " := " : " : ") + bind.getExpression();
            } else if ( b instanceof ExprConstraintDescr ) {
                ExprConstraintDescr descr = (ExprConstraintDescr) b;
                expression = descr.getExpression();
                isPositional = descr.getType() == ExprConstraintDescr.Type.POSITIONAL;
            } else {
                expression = b.getText();
            }


            ConstraintConnectiveDescr result = parseExpression( context,
                    patternDescr,
                    b,
                    expression );

            boolean isImperfect = ChanceObjectTypeNode.isImperfect(pattern.getObjectType()) || analyzeConstraintConnective( result, context, pattern );

            if ( isImperfect ) {
                hasImperfectConstraint = true;
                if ( ! isPositional && result != null ) {
                    rootConstraints.add( result );

                    int index = constraints.indexOf( b );
                    int k = index;
                    impFlags.put( k, true );
                    posFlags.put( k, false );

                    constraints.remove( index );
                    for ( BaseDescr sub : expand( result ) ) {
                        constraints.add( index, sub );
                        ++k;
                        impFlags.put( k, true );
                        posFlags.put( k, false );
                    }
                }
            } else {
                int index = constraints.indexOf( b );
                impFlags.put( index, false );

                rootConstraints.add( result );
                if (! isPositional) {
                    constraints.remove( index );
                    constraints.add( index, result );
                }

                if ( ! isPositional ) {
                    posFlags.put( index, false );
                } else {
                    posFlags.put( index, true );
                }
            }

        }

        // Add the pattern-level "and"
        if ( hasImperfectConstraint ) {
            ConstraintConnectiveDescr root = new ConstraintConnectiveDescr( ConnectiveType.AND );
            // mock "isA" to increase cardinality by 1
            root.addDescr( new RelationalExprDescr( "isA",
                    false,
                    null,
                    new AtomicExprDescr( "this" ),
                    new AtomicExprDescr( patternDescr.getObjectType() ) ) );

            for ( BaseDescr rootChild : rootConstraints ) {
                root.addDescr( rootChild );
            }
            if ( patternDescr.getAnnotation( Imperfect.class.getSimpleName() ) != null ) {
                root.addAnnotation( patternDescr.getAnnotation( Imperfect.class.getSimpleName() ) );
            }
            constraints.add( root );
            impFlags.put( constraints.size() - 1, true );
            posFlags.put( constraints.size() - 1, false );
        }


        int index = 0;
        for ( BaseDescr b : patternDescr.getDescrs() ) {
            boolean isPositional = posFlags.get( index );

                        
            if ( b instanceof BindingDescr ) {
                // it is just a bind, so build it
                buildRuleBindings( context,
                        patternDescr,
                        pattern,
                        (BindingDescr) b );
            } else if ( b instanceof ConstraintConnectiveDescr ) {
                if ( impFlags.get( index ) == true ) {
                    build(context,
                            patternDescr,
                            pattern,
                            (ConstraintConnectiveDescr) b);
                } else {

                    ConstraintConnectiveDescr result = (ConstraintConnectiveDescr) b;
                    if ( result.getDescrs().size() == 1 && result.getDescrs().get( 0 ) instanceof BindingDescr ) {
                        // it is just a bind, so build it
                        buildRuleBindings( context,
                                patternDescr,
                                pattern,
                                (BindingDescr) result.getDescrs().get( 0 ) );
                    } else {


                        super.build(context,
                                patternDescr,
                                pattern,
                                (ConstraintConnectiveDescr) b);
                    }
                }
            }  else if ( isPositional ) {
                processPositional(context,
                        patternDescr,
                        pattern,
                        (ExprConstraintDescr) b);
            } else {
                // need to build the actual constraint
                buildCcdDescr( context,
                        patternDescr,
                        pattern,
                        b );
            }

            index++;
        }

//        combineConstraints(context, pattern);
    }


    protected void processDuplicateBindings( boolean isUnification,
                                                 PatternDescr patternDescr,
                                                 Pattern pattern,
                                                 BaseDescr original,
                                                 String leftExpression,
                                                 String rightIdentifier,
                                                 RuleBuildContext context ) {

            if ( isUnification ) {
                String expr = leftExpression + " == " + rightIdentifier;
                ConstraintConnectiveDescr result = parseExpression( context,
                                                                    patternDescr,
                                                                    patternDescr,
                                                                    expr );
                BaseDescr constr = result.getDescrs().get( 0 );
                buildCcdDescr( context,
                        patternDescr,
                        pattern,
                        constr );
            } else {
                // This declaration already exists, so throw an Exception
                context.addError(new DescrBuildError(context.getParentDescr(),
                        patternDescr,
                        null,
                        "Duplicate declaration for variable '" + leftExpression + "' in the rule '" + context.getRule().getName() + "'"));
            }

        }



    private boolean analyzeConstraintConnective( BaseDescr descr, RuleBuildContext context, Pattern pattern ) {
        if ( descr instanceof ConstraintConnectiveDescr ) {
            ConstraintConnectiveDescr ccd = (ConstraintConnectiveDescr) descr;
            if ( ( (ConstraintConnectiveDescr) descr ).getAnnotation( Imperfect.class.getSimpleName() ) != null ) {
                return true;
            }
            for ( BaseDescr child : ccd.getDescrs() ) {
                if ( analyzeConstraintConnective( child, context, pattern ) ) {
                    return true;
                }
            }
        } else if ( descr instanceof RelationalExprDescr ) {
            RelationalExprDescr rel = (RelationalExprDescr) descr;
            if ( ChanceOperators.isImperfect( rel.getOperator() ) ) {
                return true;
            }

            InternalReadAccessor extractor = getFieldReadAccessor( context, rel, pattern.getObjectType(), rel.getLeft().toString(), null, false );
            if ( extractor != null ) {
                if ( extractor.getExtractToClass().isAssignableFrom( ImperfectField.class ) ) {
                    return true;
                } else {
                    return false;
                }
            }

            if ( analyzeConstraintConnective( rel.getLeft(), context, pattern ) ) { return true; }
            if ( analyzeConstraintConnective( rel.getRight(), context, pattern ) ) { return true; }
        } else if ( descr instanceof AtomicExprDescr ) {
            AtomicExprDescr atom = (AtomicExprDescr) descr;
            if ( atom.isLiteral() ) {
                return false;
            }
            // TODO ?
            if ( pattern.getInnerDeclarations().get( atom.getExpression() )  != null ) {
                Declaration ref = pattern.getInnerDeclarations().get( atom.getExpression() );
                return ref.getExtractor().getExtractToClass().isAssignableFrom( ImperfectField.class );
            }
            return false;
        } else if ( descr instanceof BindingDescr ) {
            return false;
        } else {
            throw new UnsupportedOperationException( "Can't analyze " + descr.getClass() + "for imperfection" );
        }
        return false;
    }


    protected void build( RuleBuildContext context,
                          PatternDescr patternDescr,
                          Pattern pattern,
                          ConstraintConnectiveDescr descr ) {
        pattern.addConstraint( ((ChanceConstraintBuilder) getConstraintBuilder()).buildOperatorConstraint( context, patternDescr, pattern, descr ) );
    }



    private List<BaseDescr> expand( ConstraintConnectiveDescr d ) {
        int N = d.getDescrs().size();
        List<BaseDescr> ret = new ArrayList<BaseDescr>();

        for ( int j = 0; j < N; j++ ) {
            BaseDescr child = d.getDescrs().get(j);
            if ( child instanceof ConstraintConnectiveDescr ) {
                ret.addAll( 0, expand( (ConstraintConnectiveDescr) child) );
            } else {
                ret.add( 0, child );
            }
        }
        ret.add( 0, d );

        return ret;
    }


    protected boolean addConstraintToPattern( final RuleBuildContext context,
                                              final Pattern pattern,
                                              final RelationalExprDescr relDescr,
                                              String expr,
                                              String value1,
                                              String value2,
                                              boolean isConstant) {
        InternalReadAccessor extractor = getFieldReadAccessor( context, relDescr, pattern.getObjectType(), value1, null, false );

        if ( extractor == null ) {
            return false; // impossible to create extractor
        }

        boolean isOperatorImperfect = ChanceOperators.isImperfect( relDescr.getOperatorDescr().getOperator() );

        if ( ! isOperatorImperfect ) {
            // operator works on crisp values. Any imperfect-field expression must be narrowed down to its crisp, certain value

            BaseDescr leftDescr = relDescr.getLeft();
            BaseDescr rightDescr = relDescr.getRight();

            boolean isLeftImperfect = ImperfectField.class.isAssignableFrom( extractor.getExtractToClass() );
            boolean isRightImperfect = false;
            if ( rightDescr instanceof AtomicExprDescr ) {
                AtomicExprDescr right = ((AtomicExprDescr) relDescr.getRight());
                String potentialVar = right.getExpression();
                Declaration decl = context.getDeclarationResolver().getDeclaration(context.getRule(), potentialVar);
                if ( decl != null && decl.getExtractor() != null && ImperfectField.class.isAssignableFrom( decl.getExtractor().getExtractToClass() ) ) {
                    isRightImperfect = true;
                }
            }


            if ( leftDescr instanceof AtomicExprDescr ) {
                AtomicExprDescr left = ((AtomicExprDescr) leftDescr );

                if ( isLeftImperfect ) {
                    value1 = left.getExpression() + ".getCrisp()";
                    left.setExpression( value1 );
                }
                expr = ( (AtomicExprDescr) leftDescr ).getExpression();
            } else if ( leftDescr instanceof BindingDescr ) {
                BindingDescr left = ((BindingDescr) leftDescr);
                if ( isLeftImperfect ) {
                    value1 = left.getExpression() + ".getCrisp()";
                    left.setExpression( value1 );
                }
                expr = ((BindingDescr) leftDescr).getExpression();
            }


            expr += " " + relDescr.getOperator() + " ";

            if ( rightDescr instanceof AtomicExprDescr ) {
                AtomicExprDescr right = ((AtomicExprDescr) rightDescr );

                if ( isRightImperfect ) {
                    right.setExpression( right.getExpression() + ".getCrisp()" );
                }
                expr += right.getExpression();
            } else {
                throw new UnsupportedOperationException( "ChanceRulePatternBuilder can't process right expressions of this type yet " + rightDescr );
            }

            extractor = getFieldReadAccessor( context, relDescr, pattern.getObjectType(), value1, null, false );
            if ( extractor == null ) {
                return false; // impossible to create extractor
            }

        }
        return super.addConstraintToPattern( context, pattern, relDescr, expr, value1, value2, isConstant, extractor );
    }



}
