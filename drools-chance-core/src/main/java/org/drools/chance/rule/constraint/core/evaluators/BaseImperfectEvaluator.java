package org.drools.chance.rule.constraint.core.evaluators;


import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.chance.common.ChanceStrategyFactory;
import org.drools.chance.common.ImperfectField;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveCore;
import org.drools.chance.rule.constraint.core.connectives.ConnectiveFactory;
import org.drools.chance.rule.constraint.core.connectives.impl.MvlFamilies;
import org.drools.chance.degree.ChanceDegreeTypeRegistry;
import org.drools.chance.degree.Degree;
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.DiscreteDomainDistribution;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.ImpKind;
import org.drools.chance.distribution.ImpType;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

import java.util.*;

public abstract class BaseImperfectEvaluator extends BaseEvaluator implements ImperfectEvaluator {

    protected ConnectiveCore and;
    protected ConnectiveCore or;
    protected Degree baseDegree;
    private boolean imperfectOn;

    public void setParameterText( String parameterText ) {

    }


    public BaseImperfectEvaluator( ValueType type, Operator operator ) {
        this( type, operator, true );
    }

    public BaseImperfectEvaluator(  ValueType type, Operator operator, boolean enableImperfection ) {
        this( type, operator, Collections.<String>emptyList(), enableImperfection );
    }

    public BaseImperfectEvaluator(  ValueType type, Operator operator, List<String> parameters, boolean enableImperfection ) {
        super( type, operator );
        imperfectOn = enableImperfection;

        if ( parameters != null && parameters.size() > 0 ) {
            Map<String,String> paramMap = processParameters( parameters );
            initialize( paramMap );
        } else {
            initializeDefaults();
        }
    }

    protected void initializeDefaults() {
        ConnectiveFactory factory = ChanceStrategyFactory.getConnectiveFactory( null, null );

        and = factory.getAnd();
        or  = factory.getOr();
        baseDegree = ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( ChanceDegreeTypeRegistry.getDefaultDegree(), 0 );
    }

    protected void initialize( Map<String, String> paramMap ) {
        
        MvlFamilies mvf = MvlFamilies.parse( paramMap.get( MvlFamilies.name ) );
        ImpKind impKind = ImpKind.parse( paramMap.get( ImpKind.name ) );
        ImpType impType = ImpType.parse( paramMap.get( ImpType.name ) );
        DegreeType degT = DegreeType.parse( paramMap.get( DegreeType.name ) );

        String family = mvf != null ? mvf.value() : null;
        ConnectiveFactory factory = ChanceStrategyFactory.getConnectiveFactory( impKind, impType );
        
        and = family != null ? factory.getAnd( family ) : factory.getAnd();
        or  = family != null ? factory.getOr( family )  : factory.getOr();
        baseDegree = ChanceDegreeTypeRegistry.getSingleInstance().buildDegree( degT, 0 );

    }

    protected Map<String,String> processParameters( List<String> parameters ) {
        Map<String,String> params = new HashMap<String,String>( parameters.size() );
        for ( String p : parameters ) {
            int index = p.indexOf( '=' );
            if ( index >= 0 ) {
                String key = p.substring( 0, index );
                String val = p.substring( index + 1 );
                params.put( key, val );
            } else {
                params.put( p, p );
            }
        }
        return params;
    }


    public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor extractor, Object object, FieldValue value) {
        return match( workingMemory, extractor, object, value ).toBoolean();
    }

    public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, Object left, InternalReadAccessor rightExtractor, Object right) {
        return match( workingMemory, leftExtractor, left, rightExtractor, right ).toBoolean();
    }

    public boolean evaluateCachedLeft( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, Object left ) {
        return matchCachedLeft(workingMemory, context, left).toBoolean();
    }

    public boolean evaluateCachedRight( InternalWorkingMemory workingMemory, VariableRestriction.VariableContextEntry context, Object left ) {
        return matchCachedRight( workingMemory, context, left ).toBoolean();
    }

    public Degree match( InternalWorkingMemory workingMemory,
                         InternalReadAccessor extractor, Object object, FieldValue value ) {
        final Object objectValue = extractor
                .getValue(workingMemory, object);

        return compare( objectValue, value.getValue(), workingMemory );
    }

    public Degree match( InternalWorkingMemory workingMemory,
                         InternalReadAccessor leftExtractor, Object left,
                         InternalReadAccessor rightExtractor, Object right ) {
        final Object value1 = leftExtractor.getValue(workingMemory, left);
        final Object value2 = rightExtractor.getValue(workingMemory, right);

        Object source = value1;
        Object target = value2;

        return compare( source, target, workingMemory );
    }


    public Degree matchCachedLeft( InternalWorkingMemory workingMemory,
                                   VariableRestriction.VariableContextEntry context, Object right ) {

        Object target;
        if ( ! context.isLeftNull() ) {
            target = ( (VariableRestriction.ObjectVariableContextEntry) context).left;
        } else {
            if ( context.getVariableDeclaration() != null ) {
                target = context.getVariableDeclaration().getExtractor().getValue( workingMemory, right );
            } else {
                target = right;
            }
        }
//        Object source = context.getFieldExtractor().getValue( prepareLeftObject( context.getTuple().getHandle() ) );
        Object source = context.getFieldExtractor().getValue( right );

        return compare( source, target, workingMemory );
    }

    public Degree matchCachedRight( InternalWorkingMemory workingMemory,
                                    VariableRestriction.VariableContextEntry context, Object left ) {

        Object source = context.getFieldExtractor().getValue( context.getObject() );
        Object target = left;
        if ( context.getVariableDeclaration() != null ) {
            target = context.getVariableDeclaration().getExtractor().getValue( workingMemory, target );
        }

        return compare( source, target, workingMemory );
    }


    protected Degree compare( Object source, Object target, InternalWorkingMemory workingMemory ) {

        Distribution leftDist = null;
        Distribution rightDist = null;
        Object leftValue = null;
        Object rightValue = null;

        if ( source instanceof ImperfectField ) {
            leftDist = ((ImperfectField) source).getCurrent();
        } else if ( source instanceof Distribution ) {
            leftDist = (Distribution) source;
        } else {
            leftValue = source;
        }

        if ( target instanceof ImperfectField ) {
            rightDist = ((ImperfectField) target).getCurrent();
        } else if ( target instanceof Distribution ) {
            rightDist = (Distribution) target;
        } else {
            rightValue = target;
        }

        Degree result;

        if ( leftDist != null && rightDist != null ) {
            result = matchDistributions( leftDist, rightDist, workingMemory );
        } else if ( leftDist != null && rightDist == null ) {
            result = matchDistributionToValue( leftDist, rightValue, workingMemory );
        } else if ( leftDist == null && rightDist != null ) {
            result = matchValueToDistribution( leftValue, rightDist, workingMemory );
        } else {
            result = matchValueToValue( leftValue, rightValue, workingMemory );
        }


        if ( result != null ) {
            return imperfectOn ? result : result.fromBoolean(result.toBoolean());
        }

        throw new UnsupportedOperationException( "Unable to match " + source + " with " + target );

    }

    protected Degree matchValueToDistribution( Object leftValue, Distribution rightDist, InternalWorkingMemory workingMemory ) {
        Degree deg = getBaseDegree().False();

        if ( rightDist.isDiscrete() ) {
            if ( rightDist.domainSize().intValue() == 0 ) {
                return deg;
            }
            DiscreteDomainDistribution discr = (DiscreteDomainDistribution) rightDist;
            Iterator iter = discr.iterator();
            while ( iter.hasNext() ) {
                Object right = iter.next();
                Degree m = discr.get( right );
                if ( m.toBoolean() ) {
                    deg = or.eval( deg, and.eval( matchValueToValue( leftValue, right, workingMemory ), m ) );
                }
            }
        } else {
            throw new UnsupportedOperationException( "Unable to match a value with a continuous distribution!" );
        }
        return deg;
    }

    protected Degree matchDistributions( Distribution leftDist, Distribution rightDist, InternalWorkingMemory workingMemory ) {
        Degree deg = getBaseDegree().False();

        if ( rightDist.isDiscrete() && leftDist.isDiscrete() ) {
            DiscreteDomainDistribution ldiscr = (DiscreteDomainDistribution) leftDist;
            DiscreteDomainDistribution rdiscr = (DiscreteDomainDistribution) rightDist;
            Iterator liter = ldiscr.iterator();
            Iterator riter = rdiscr.iterator();

            if ( rightDist.domainSize().intValue() == 0 || leftDist.domainSize().intValue() == 0 ) {
                return deg;
            }

            while ( liter.hasNext() ) {
                Object left = liter.next();
                Degree l = ldiscr.get( left );
                if ( l.toBoolean() ) {
                    while ( riter.hasNext() ) {
                        Object right = riter.next();
                        Degree r = rdiscr.get( right );
                        if ( r.toBoolean() ) {
                            Degree comp = matchValueToValue( left, right, workingMemory );
                            if ( comp.toBoolean() ) {
                                deg = or.eval( deg, and.eval( comp, l, r ) );
                            }
                        }
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException( "Unable to match a value with a continuous distribution!" );
        }
        return deg;

    }

    protected Degree matchDistributionToValue( Distribution leftDist, Object rightValue, InternalWorkingMemory workingMemory ) {
        Degree deg = getBaseDegree().False();

        if ( leftDist.isDiscrete() ) {
            if ( leftDist.domainSize().intValue() == 0 ) {
                return deg;
            }
            DiscreteDomainDistribution discr = (DiscreteDomainDistribution) leftDist;
            Iterator iter = discr.iterator();
            while ( iter.hasNext() ) {
                Object left = iter.next();
                Degree m = discr.get( left );
                if ( m.toBoolean() ) {
                    deg = or.eval( deg, and.eval( m, matchValueToValue( left, rightValue, workingMemory ) ) );
                }
            }
        } else {
            throw new UnsupportedOperationException( "Unable to match a value with a continuous distribution!" );
        }
        return deg;
    }

    protected abstract Degree matchValueToValue( Object leftValue, Object rightValue, InternalWorkingMemory workingMemory );


    public Degree getBaseDegree() {
        return baseDegree;
    }
}
