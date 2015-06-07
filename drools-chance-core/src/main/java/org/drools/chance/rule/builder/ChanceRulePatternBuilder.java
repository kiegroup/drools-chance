/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.chance.rule.builder;

import org.drools.chance.core.util.IntHashMap;
import org.drools.chance.factmodel.Imperfect;
import org.drools.chance.common.ImperfectField;
import org.drools.chance.rule.constraint.ImperfectConstraint;
import org.drools.chance.rule.constraint.InnerOperatorConstraint;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveFactory;
import org.drools.chance.rule.constraint.core.connectives.impl.LogicConnectives;
import org.drools.chance.rule.constraint.core.evaluators.IsAEvaluatorDefinition;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.MVELDumper;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AtomicExprDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.ConnectiveType;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.base.ClassObjectType;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;
import org.drools.core.util.bitmask.OpenBitSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChanceRulePatternBuilder extends PatternBuilder {


    public ChanceRulePatternBuilder() {
        super();
    }



    protected void processConstraintsAndBinds( final RuleBuildContext context,
                                               final PatternDescr patternDescr,
                                               final Pattern pattern ) {

        MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext().setRuleContext(context);
        List constraintDescrs = patternDescr.getConstraint().getDescrs();
        List<? extends BaseDescr> temp = new ArrayList<BaseDescr>( patternDescr.getConstraint().getDescrs() );
        List<BaseDescr> rootConstraintDescrs = new ArrayList<BaseDescr>();

        /*
        IntHashMap<Boolean> impFlags = new IntHashMap<Boolean>();
        IntHashMap<Boolean> posFlags = new IntHashMap<Boolean>();
        */
        OpenBitSet impFlags = new OpenBitSet();
        OpenBitSet posFlags = new OpenBitSet();
        Set<BaseDescr> betaConstraintDescrs = new HashSet<BaseDescr>();

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

            boolean isImperfect = isImperfect( pattern.getObjectType() ) || analyzeConstraintConnective( result, context, pattern );

            if ( isImperfect ) {
                hasImperfectConstraint = true;
                if ( ! isPositional && result != null ) {
                    rootConstraintDescrs.add( result );

                    int index = constraintDescrs.indexOf( b );
                    int k = index;

                    constraintDescrs.remove( index );
                    for ( BaseDescr sub : expand( result ) ) {
                        constraintDescrs.add( index, sub );
                        ++k;
                    }
                }
            } else {
                int index = constraintDescrs.indexOf( b );

                rootConstraintDescrs.add( result );
                if (! isPositional) {
                    constraintDescrs.remove( index );
                    constraintDescrs.add( index, result );
                } else {
                    if ( result.getDescrs().get( 0 ) instanceof BindingDescr ) {
                        constraintDescrs.remove( index );
                        constraintDescrs.add( index, result );
                    }
                }
            }

        }

        for ( int j = 0; j < constraintDescrs.size(); j++ ) {
            BaseDescr d = (BaseDescr) constraintDescrs.get( j );
            if ( analyzeConstraintConnective( d, context, pattern ) ) {
                impFlags.set( j );
            }
            if ( d instanceof ExprConstraintDescr && ((ExprConstraintDescr) d).getType() == ExprConstraintDescr.Type.POSITIONAL ) {
                posFlags.set( j );
            }
        }

        // Add the pattern-level "and"
        if ( hasImperfectConstraint ) {
            ConstraintConnectiveDescr root = new ConstraintConnectiveDescr( ConnectiveType.AND );
            if ( patternDescr.hasAnnotation( Imperfect.class ) ) {
                root.addAnnotation( patternDescr.getAnnotation( Imperfect.class ) );
            }

            for ( BaseDescr rootChild : rootConstraintDescrs ) {
                root.addDescr( rootChild );
            }


            root.addDescr( new OTNPlaceholderDescr() );

            if ( patternDescr.getAnnotation( Imperfect.class.getSimpleName() ) != null ) {
                root.addAnnotation( patternDescr.getAnnotation( Imperfect.class.getSimpleName() ) );
            }

            constraintDescrs.add( root );

            if ( ! impFlags.isEmpty() ) {
                impFlags.set( constraintDescrs.size() - 1 );
            }
            posFlags.reset( constraintDescrs.size() - 1 );
        }


        int index = 0;
        int startNumConstraints = 0;
        int endNumConstraints = 0;

        for ( BaseDescr b : patternDescr.getDescrs() ) {
            boolean isPositional = posFlags.get( index );
            startNumConstraints = endNumConstraints;

            if ( b instanceof BindingDescr ) {
                // it is just a bind, so build it
                buildRuleBindings( context,
                        patternDescr,
                        pattern,
                        (BindingDescr) b );
            } else if ( b instanceof ConstraintConnectiveDescr ) {
                if ( impFlags.get( index ) == true ) {
                    InnerOperatorConstraint oc = buildConnective( context,
                                     patternDescr,
                                     pattern,
                                     (ConstraintConnectiveDescr) b,
                                     mvelCtx,
                                     constraintDescrs,
                                     betaConstraintDescrs,
                                     impFlags );
                    if ( oc.getArity() > 1 ) {
                        pattern.addConstraint( oc );
                    }
                } else {

                    ConstraintConnectiveDescr result = (ConstraintConnectiveDescr) b;
                    if ( result.getDescrs().size() == 1 && result.getDescrs().get( 0 ) instanceof BindingDescr ) {
                        // it is just a bind, so build it
                        buildRuleBindings( context,
                                patternDescr,
                                pattern,
                                (BindingDescr) result.getDescrs().get( 0 ) );
                    } else {
                        ConstraintConnectiveDescr ccd = (ConstraintConnectiveDescr) b;
                        if ( ccd.getDescrs().size() > 1 || ( ccd.getDescrs().size() == 1 && ! constraintDescrs.contains( ccd.getDescrs().get( 0 ) ) ) ) {
                            List<Constraint> ccdConstraints = build( context,
                                                                     patternDescr,
                                                                     pattern,
                                                                     (ConstraintConnectiveDescr) b,
                                                                     mvelCtx );
                            for ( Constraint c : ccdConstraints ) {
                                if ( !pattern.getConstraints().contains( c ) ) {
                                    pattern.addConstraint( c );
                                }
                            }
                        }
                    }
                }
            }  else if ( isPositional ) {
                processPositional(context,
                        patternDescr,
                        pattern,
                        (ExprConstraintDescr) b);
            } else {
                // need to build the actual constraint
                Constraint c = buildCcdDescr( context,
                        patternDescr,
                        pattern,
                        b,
                        new ConstraintConnectiveDescr(  ),
                        mvelCtx);
                pattern.addConstraint( c );
            }

            endNumConstraints = pattern.getConstraints().size();
            if ( endNumConstraints == startNumConstraints + 1 ) {
                // was a constraint created from the
                Constraint last = pattern.getConstraints().get( pattern.getConstraints().size() - 1 );
                if ( last.getType() == Constraint.ConstraintType.BETA ) {
                    betaConstraintDescrs.add( b );
                }
                if ( impFlags.get( index ) && ! ( last instanceof ImperfectConstraint ) ) {
                    impFlags.clear( index );
                    for ( Object d : constraintDescrs ) {
                        if ( d instanceof ConstraintConnectiveDescr ) {
                            ConstraintConnectiveDescr cdd = (ConstraintConnectiveDescr) d;
                            if ( cdd.getDescrs().size() == 1 && cdd.getDescrs().contains( b ) ) {
                                impFlags.clear( constraintDescrs.indexOf( d ) );
                            }
                        }
                    }
                }
            }
            /*
            else if ( endNumConstraints > startNumConstraints ) {
                // More than one constraint from a descr - what to do?
                throw new IllegalStateException( "Multiple constraints generated from the same descriptor " + b );
            }
            */

            index++;
        }

        combineConstraints( context, pattern, mvelCtx );
    }

    private InnerOperatorConstraint buildConnective( RuleBuildContext context,
                                  PatternDescr patternDescr,
                                  Pattern pattern,
                                  ConstraintConnectiveDescr ccd,
                                  MVELDumper.MVELDumperContext mvelCtx,
                                  List constraintDescrs, Set<BaseDescr> betaConstraints,
                                  OpenBitSet impFlags ) {
        InnerOperatorConstraint oc = (InnerOperatorConstraint) ( (ChanceMVELConstraintBuilder) getConstraintBuilder( context ) ).buildOperatorConstraint( ccd, constraintDescrs, betaConstraints, impFlags );
        return oc;
    }


    @Override
    protected void combineConstraints( RuleBuildContext context, Pattern pattern, MVELDumper.MVELDumperContext mvelCtx ) {
        super.combineConstraints( context, pattern, mvelCtx );

        // we need to rebuild boolean op constraints operating on imperfect terms, after the field accessors have been properly rewritten
        int j;
        while ( ( j = findCombinableOperatorConstraint( pattern ) ) >= 0 ) {
            // keep merging until no more possible
            // TODO could probably be optimized, but would need more careful testing
            recombineOperatorConstraint( context, pattern, mvelCtx, j );
        }
    }

    private int findCombinableOperatorConstraint( Pattern pattern ) {
        BitSet impConstraints = new BitSet( pattern.getConstraints().size() );
        BitSet opsConstraints = new BitSet( pattern.getConstraints().size() );

        // find imperfect constraints and operator constraints
        for ( int j = 0; j < pattern.getConstraints().size(); j++ ) {
            if ( pattern.getConstraints().get( j ) instanceof InnerOperatorConstraint ) {
                opsConstraints.set( j );
            }
            if ( pattern.getConstraints().get( j ) instanceof ImperfectConstraint ) {
                impConstraints.set( j );
            }
        }

        // now unmark operator constraints with imperfect arguments
        int operatorIdx = opsConstraints.nextSetBit( 0 );
        while ( operatorIdx >= 0 && operatorIdx != opsConstraints.length() - 1 ) {
            InnerOperatorConstraint opc = (InnerOperatorConstraint) pattern.getConstraints().get( operatorIdx );
            boolean crisp = true;
            for ( int k = 0; k < opc.getArity(); k++ ) {
                if ( impConstraints.get( operatorIdx - k - 1 ) ) {
                    crisp = false;
                    break;
                }
            }
            if ( crisp &&
                 ( opc.getConnective().getType() == LogicConnectives.AND || opc.getConnective().getType() == LogicConnectives.OR ) ) {
                return operatorIdx;
            }
            operatorIdx = opsConstraints.nextSetBit( operatorIdx + 1 );
        }

        return -1;
    }

    private void recombineOperatorConstraint( RuleBuildContext context, Pattern pattern, MVELDumper.MVELDumperContext mvelCtx, int j ) {
        InnerOperatorConstraint opc = (InnerOperatorConstraint) pattern.getConstraints().get( j );

        List<MvelConstraint> args = new ArrayList<MvelConstraint>( opc.getArity() );
        for ( int i = 1; i <= opc.getArity(); i++ ) {
            args.add( (MvelConstraint) pattern.getConstraints().get( j - i ) );
        }

        Constraint combinedConstraint = getCombinedConstraint( context,
                                                               pattern,
                                                               mvelCtx,
                                                               opc.getConnective().getType() == LogicConnectives.AND ? " && " : " || ",
                                                               args );
        if ( combinedConstraint != null ) {
            int idx = pattern.getConstraints().indexOf( opc );
            pattern.addConstraint( idx, combinedConstraint );
            pattern.removeConstraint( opc );
        }

    }


    protected void processDuplicateBindings( boolean isUnification,
                                                 PatternDescr patternDescr,
                                                 Pattern pattern,
                                                 BaseDescr original,
                                                 String leftExpression,
                                                 String rightIdentifier,
                                                 RuleBuildContext context ) {
            MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext().setRuleContext(context);
            if ( isUnification ) {
                String expr = leftExpression + " == " + rightIdentifier;
                ConstraintConnectiveDescr result = parseExpression( context,
                                                                    patternDescr,
                                                                    patternDescr,
                                                                    expr );
                BaseDescr constr = result.getDescrs().get( 0 );
                Constraint constraint = buildCcdDescr( context,
                                                       patternDescr,
                                                       pattern,
                                                       constr,
                                                       result,
                                                       mvelCtx);
                pattern.addConstraint( constraint );
            } else {
                // This declaration already exists, so throw an Exception
                context.addError(new DescrBuildError(context.getParentDescr(),
                        patternDescr,
                        null,
                        "Duplicate declaration for variable '" + leftExpression + "' in the rule '" + context.getRule().getName() + "'"));
            }

        }


    protected static boolean analyzeConstraintConnective( BaseDescr descr, RuleBuildContext context, Pattern pattern ) {
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

            String left = rel.getLeft() instanceof BindingDescr ? ( (BindingDescr) rel.getLeft() ).getExpression() : rel.getLeft().toString();

            InternalReadAccessor extractor = getFieldReadAccessor( context, rel, pattern.getObjectType(), left, null, false );
            if ( extractor != null ) {
                if ( extractor.getExtractToClass().isAssignableFrom( ImperfectField.class ) ) {
                    return ChanceOperators.isImperfect( rel.getOperator() );
                } else {
                    return false;
                }
            }

            if ( analyzeConstraintConnective( rel.getLeft(), context, pattern ) ) {
                return true;
            }
            if ( analyzeConstraintConnective( rel.getRight(), context, pattern ) ) {
                return true;
            }

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
        } else if ( descr instanceof BindingDescr || descr instanceof ExprConstraintDescr ) {
            return false;
        } else {
            throw new UnsupportedOperationException( "Can't analyze " + descr.getClass() + "for imperfection" );
        }
        return false;
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


    @Override
    protected List<Constraint> build( RuleBuildContext context,
                                      PatternDescr patternDescr,
                                      Pattern pattern,
                                      ConstraintConnectiveDescr descr,
                                      MVELDumper.MVELDumperContext mvelCtx ) {
        ensureCrispAccessors( descr, pattern, context );
        return super.build( context, patternDescr, pattern, descr, mvelCtx );
    }

    private void ensureCrispAccessors( BaseDescr descr, Pattern pattern, RuleBuildContext context ) {
        if ( descr instanceof ConstraintConnectiveDescr ) {
            ConstraintConnectiveDescr ccd = (ConstraintConnectiveDescr) descr;
            for ( BaseDescr child : ccd.getDescrs() ) {
                ensureCrispAccessors( child, pattern, context );
            }
        } else if ( descr instanceof RelationalExprDescr ) {
            RelationalExprDescr relDescr = (RelationalExprDescr) descr;
            String[] values = new String[ 2 ];
            PatternBuilder.findExpressionValues( relDescr, values );
            ensureCrispAccessors( context, pattern, relDescr, values[ 0 ] );
        } else if ( descr instanceof AtomicExprDescr ) {
            // not yet supported
        } else if ( descr instanceof OTNPlaceholderDescr ) {
            // nothing to do
        } else {
            throw new UnsupportedOperationException( "What now?" );
        }
    }

    public String ensureCrispAccessors( RuleBuildContext context, Pattern pattern, RelationalExprDescr relDescr, String value1 ) {
        InternalReadAccessor extractor = getFieldReadAccessor( context, relDescr, pattern.getObjectType(), value1, null, false );

        if ( extractor == null ) {
            return null;
        }

        boolean isOperatorImperfect = ChanceOperators.isImperfect( relDescr.getOperatorDescr().getOperator() );

        String expr = "";
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
        }
        return expr;
    }

    @Override
    protected Constraint buildConstraintForPattern( final RuleBuildContext context,
                                                    final Pattern pattern,
                                                    final RelationalExprDescr relDescr,
                                                    String expr,
                                                    String value1,
                                                    String value2,
                                                    boolean isConstant) {

        String newExpr = ensureCrispAccessors( context, pattern, relDescr, value1 );
        if ( newExpr != null ) {
            expr = newExpr;
        }

        return super.buildConstraintForPattern( context, pattern, relDescr, expr, value1, value2, isConstant );
    }

    public static boolean isImperfect( ObjectType objectType ) {
        if ( objectType instanceof ClassObjectType ) {
            Class klass =((ClassObjectType) objectType).getClassType();
            return klass.getAnnotation( Trait.class ) != null;
        } else {
            return false;
        }
    }

}
