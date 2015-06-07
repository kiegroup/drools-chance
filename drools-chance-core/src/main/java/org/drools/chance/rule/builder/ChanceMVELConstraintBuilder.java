/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.chance.rule.builder;


import org.drools.chance.factmodel.Imperfect;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveFactory;
import org.drools.chance.rule.constraint.core.connectives.impl.AbstractConnective;
import org.drools.chance.rule.constraint.core.connectives.impl.MvlFamilies;
import org.drools.chance.rule.constraint.core.evaluators.ImperfectEvaluator;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.ImpKind;
import org.drools.chance.distribution.ImpType;
import org.drools.chance.rule.constraint.ImperfectEvaluatorConstraint;
import org.drools.chance.rule.constraint.ImperfectMvelConstraint;
import org.drools.chance.rule.constraint.InnerOperatorConstraint;
import org.drools.chance.rule.constraint.core.evaluators.ImperfectEvaluatorWrapper;
import org.drools.chance.rule.constraint.core.evaluators.ImperfectMvelEvaluator;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.compiler.rule.builder.MVELConstraintBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.constraint.EvaluatorConstraint;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.util.bitmask.OpenBitSet;
import org.drools.core.util.index.IndexUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;


public class ChanceMVELConstraintBuilder extends MVELConstraintBuilder {



    public Constraint buildLiteralConstraint(RuleBuildContext context,
                                             Pattern pattern,
                                             ValueType vtype,
                                             FieldValue field,
                                             String expression,
                                             String leftValue,
                                             String operator,
                                             String rightValue,
                                             InternalReadAccessor extractor,
                                             LiteralRestrictionDescr restrictionDescr) {

        boolean isImperfect = ChanceOperators.isImperfect( operator );

        if (USE_MVEL_EXPRESSION) {
            if ( ! isMvelOperator( operator ) ) {
                // custom or complex operator
                Evaluator evaluator = buildLiteralEvaluator( context, extractor, restrictionDescr, vtype );
                if ( isImperfect ) {
                    if ( evaluator instanceof ImperfectEvaluator ) {
                        // imperfect evaluator, to be used imperfectly
                        ImperfectEvaluatorConstraint iec = new ImperfectEvaluatorConstraint( field,
                                                                                             evaluator,
                                                                                             extractor,
                                                                                             extractConstraintLabel( restrictionDescr.getParameters() ) );
                        return iec;
                    } else {
                        // imperfect eval can work in a single
                        return new EvaluatorConstraint( field, evaluator, extractor );
                    }
                } else {
                    if ( evaluator instanceof ImperfectEvaluator ) {
                        //TODO
                        // imperfect evaluator, coerce results into boolean
                        return new EvaluatorConstraint( field, evaluator, extractor );
                    } else {
                        // standard evaluator called in a standard way
                        return new EvaluatorConstraint( field, evaluator, extractor );
                    }

                }

            } else {

                if ( isImperfect ) {

                    ImperfectMvelEvaluator evaluator = new ImperfectMvelEvaluator( vtype,
                            Operator.determineOperator( ChanceOperators.makePerfect( operator ), false ),
                            restrictionDescr.getParameters(),
                            true,
                            rightValue );

                    ImperfectEvaluatorConstraint iec = new ImperfectEvaluatorConstraint( field,
                                                                                         evaluator,
                                                                                         extractor,
                                                                                         extractConstraintLabel( restrictionDescr.getParameters() ) );

                    if ( restrictionDescr.getParameters() != null && restrictionDescr.getParameters().contains( "cut" ) ) {
                        iec.setCutting( true );
                    }
                    return iec;
                } else {

                    String mvelExpr = normalizeMVELLiteralExpression(vtype, field, expression, leftValue, operator, rightValue, restrictionDescr);
                    IndexUtil.ConstraintType constraintType = IndexUtil.ConstraintType.decode(operator);
                    MVELCompilationUnit compilationUnit = buildCompilationUnit( context, pattern, mvelExpr, null );

                    return new MvelConstraint( context.getPkg().getName(), mvelExpr, compilationUnit, constraintType, field, extractor );

                }
            }
        } else {
            throw new UnsupportedOperationException( "Chance Constraint Builder does not support legacy constraints " + expression );
        }
    }


    public Constraint buildVariableConstraint(RuleBuildContext context,
                                              Pattern pattern,
                                              String expression,
                                              Declaration[] declarations,
                                              String leftValue,
                                              OperatorDescr operatorDescr,
                                              String rightValue,
                                              InternalReadAccessor extractor,
                                              Declaration requiredDeclaration,
                                              RelationalExprDescr relDescr ) {

        boolean isImperfect = ChanceOperators.isImperfect( operatorDescr.getOperator() );

        if ( ! isMvelOperator( operatorDescr.getOperator() ) ) {
            EvaluatorDefinition.Target right = getRightTarget( extractor );
            EvaluatorDefinition.Target left = (requiredDeclaration.isPatternDeclaration() && !(Date.class.isAssignableFrom( requiredDeclaration.getExtractor().getExtractToClass() ) || Number.class.isAssignableFrom( requiredDeclaration.getExtractor().getExtractToClass() ))) ? EvaluatorDefinition.Target.HANDLE : EvaluatorDefinition.Target.FACT;
            final Evaluator evaluator = getEvaluator( context,
                                                      relDescr,
                                                      extractor.getValueType(),
                                                      operatorDescr.getOperator(),
                                                      relDescr.isNegated(),
                                                      relDescr.getParametersText(),
                                                      left,
                                                      right );
            if ( isImperfect ) {
                if ( evaluator instanceof ImperfectEvaluator) {
                    ImperfectEvaluatorConstraint iec = new ImperfectEvaluatorConstraint( new Declaration[] { requiredDeclaration },
                                                                                         evaluator,
                                                                                         extractor,
                                                                                         extractConstraintLabel( operatorDescr.getParameters() ) );
                    return iec;
                } else {
                    return new EvaluatorConstraint( new Declaration[] { requiredDeclaration }, evaluator, extractor );
                }
            } else {
                return new EvaluatorConstraint( new Declaration[] { requiredDeclaration }, evaluator, extractor );
            }
        } else {
            if ( isImperfect ) {

                EvaluatorDefinition.Target right = getRightTarget( extractor );
                EvaluatorDefinition.Target left = (requiredDeclaration.isPatternDeclaration() && !(Date.class.isAssignableFrom( requiredDeclaration.getExtractor().getExtractToClass() ) || Number.class.isAssignableFrom( requiredDeclaration.getExtractor().getExtractToClass() ))) ? EvaluatorDefinition.Target.HANDLE : EvaluatorDefinition.Target.FACT;
                final Evaluator evaluator = getEvaluator( context,
                                                          relDescr,
                                                          extractor.getValueType(),
                                                          operatorDescr.getOperator(),
                                                          relDescr.isNegated(),
                                                          relDescr.getParametersText(),
                                                          left,
                                                          right );

                ImperfectEvaluatorConstraint iec = new ImperfectEvaluatorConstraint( new Declaration[] { requiredDeclaration },
                                                                                     evaluator,
                                                                                     extractor,
                                                                                     extractConstraintLabel( operatorDescr.getParameters() ) );
                return iec;

            } else {

                boolean isUnification = requiredDeclaration != null && requiredDeclaration.getPattern().getObjectType().equals( new ClassObjectType( DroolsQuery.class ) ) && Operator.EQUAL.getOperatorString().equals( operatorDescr.getOperator() );
                if (isUnification) {
                    expression = resolveUnificationAmbiguity(expression, declarations, leftValue, rightValue);
                }
                IndexUtil.ConstraintType constraintType = IndexUtil.ConstraintType.decode( operatorDescr.getOperator() );
                MVELCompilationUnit compilationUnit = isUnification ? null : buildCompilationUnit( context, pattern, expression, null );
                return new MvelConstraint( Arrays.asList( context.getPkg().getName() ),
                                           expression,
                                           declarations,
                                           compilationUnit,
                                           constraintType,
                                           requiredDeclaration,
                                           extractor,
                                           isUnification );

            }
        }




    }

    protected Constraint buildOperatorConstraint( ConstraintConnectiveDescr ccd,
                                                  List constraintDescrs, Set<BaseDescr> betaConstraints,
                                                  OpenBitSet impFlags ) {

        AnnotationDescr ann = ccd.getAnnotation( Imperfect.class.getSimpleName() );

        ConnectiveCore cc = AbstractConnective.buildConnective( ann, ccd.getConnective() );

        List<BaseDescr> excluded = new ArrayList<BaseDescr>();
        int effectiveArity = ccd.getDescrs().size();
        for ( BaseDescr child : ccd.getDescrs() ) {
            if ( child instanceof ConstraintConnectiveDescr ) {
                ConstraintConnectiveDescr cccd = (ConstraintConnectiveDescr) child;
                if ( cccd.getDescrs().size() == 1 && cccd.getDescrs().get( 0 ) instanceof BindingDescr ) {
                    effectiveArity--;
                    excluded.add( child );
                }
            }
        }

        InnerOperatorConstraint oc = new InnerOperatorConstraint( effectiveArity, cc, ann != null ? ann.getValueAsString( "label" ) : null );


        for ( int j = 0; j < ccd.getDescrs().size(); j++ ) {
            BaseDescr child = ccd.getDescrs().get( j );
            if ( excluded.contains( child ) ) {
                continue;
            }

            if ( betaConstraints.contains( child ) ||
                 ( child instanceof ConstraintConnectiveDescr
                   && ( (ConstraintConnectiveDescr) child ).getDescrs().size() == 1
                   && betaConstraints.contains( ( (ConstraintConnectiveDescr) child ).getDescrs().get( 0 ) ) ) ) {
                oc.setBeta( j );
            }

            int index = constraintDescrs.indexOf( child );
            if ( index >= 0 && ! impFlags.get( index ) ) {
                oc.makeCrispArgument( ccd.getDescrs().indexOf( child ) );
            }
        }

        return oc;
    }


    public boolean isMvelOperator( String operator ) {
        return super.isMvelOperator( operator ) || ChanceOperators.lookup( operator );
    }

    private String extractConstraintLabel( List<String> parameters ) {
        if ( parameters == null || parameters.size() == 0 ) {
            return null;
        }
        for ( String keyVal : parameters ) {
            StringTokenizer tok = new StringTokenizer( keyVal, "=" );
            String key = tok.nextToken().trim();
            if ( "label".equals( key ) && tok.hasMoreTokens() ) {
                return tok.nextToken().trim();
            }
        }
        return null;
    }


    public Constraint buildMvelConstraint( String packageName,
                                           String expression,
                                           Declaration[] declarations,
                                           MVELCompilationUnit compilationUnit,
                                           boolean isDynamic ) {
        return new ImperfectMvelConstraint( packageName,
                                            expression,
                                            declarations,
                                            compilationUnit,
                                            isDynamic,
                                            null );
    }

    public Constraint buildMvelConstraint( String packageName,
                                           String expression,
                                           Declaration[] declarations,
                                           MVELCompilationUnit compilationUnit,
                                           boolean isDynamic,
                                           PredicateDescr base ) {
        ImperfectMvelConstraint imc = new ImperfectMvelConstraint( packageName,
                                                                   expression,
                                                                   declarations,
                                                                   compilationUnit,
                                                                   isDynamic,
                                                                   extractConstraintLabel( base.getParameters() ) );
        return imc;
    }

    public EvaluatorWrapper wrapEvaluator( Evaluator evaluator, Declaration left, Declaration right ) {
        return new ImperfectEvaluatorWrapper( evaluator, left, right );
    }


}
