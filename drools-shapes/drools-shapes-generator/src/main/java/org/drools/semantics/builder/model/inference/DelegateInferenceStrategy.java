/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.semantics.builder.model.inference;

//import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import org.apache.commons.collections15.map.MultiKeyMap;
import org.apache.log4j.Logger;
import org.drools.semantics.builder.DLFactoryConfiguration;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.Individual;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.semantics.builder.model.SubConceptOf;
import org.drools.semantics.utils.NameUtils;
import org.drools.semantics.utils.NamespaceUtils;
import org.drools.core.util.HierarchySorter;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLQuantifiedDataRestriction;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DelegateInferenceStrategy extends AbstractModelInferenceStrategy {

    private static Logger logger = Logger.getLogger( DelegateInferenceStrategy.class );

    private int counter = 0;

    public int minCounter = 0;
    public int maxCounter = 0;

    private Map<OWLClassExpression,OWLClassExpression> aliases;
    private Map<OWLClassExpression,Set<OWLClassExpression>> reverseAliases = new HashMap<OWLClassExpression, Set<OWLClassExpression>>();
    private Map<OWLClassExpression,OWLClass> anonNameAliases = new HashMap<OWLClassExpression, OWLClass>();

    private Map<String, Concept> conceptCache = new LinkedHashMap<String, Concept>();
    private Map<String, String> individualTypesCache = new HashMap<String, String>();
    private Map<OWLClassExpression, OWLClass> fillerCache = new HashMap<OWLClassExpression, OWLClass>();
    private Map<String, String> props = new HashMap<String, String>();


    private MultiKeyMap minCards = new MultiKeyMap();
    private MultiKeyMap maxCards = new MultiKeyMap();

    private static void register( String prim, String klass ) {
        IRI i1 = IRI.create( prim );
        Concept con = new Concept( i1, klass, true );
        primitives.put( i1.toQuotedString(), con );
    }



    private static Map<String, Concept> primitives = new HashMap<String, Concept>();

    {

        register( "http://www.w3.org/2001/XMLSchema#string", "xsd:string" );

        register( "http://www.w3.org/2001/XMLSchema#dateTime", "xsd:dateTime" );

        register( "http://www.w3.org/2001/XMLSchema#date", "xsd:date" );

        register( "http://www.w3.org/2001/XMLSchema#time", "xsd:time" );

        register( "http://www.w3.org/2001/XMLSchema#int", "xsd:int" );

        register( "http://www.w3.org/2001/XMLSchema#integer", "xsd:integer" );

        register( "http://www.w3.org/2001/XMLSchema#long", "xsd:long" );

        register( "http://www.w3.org/2001/XMLSchema#float", "xsd:float" );

        register( "http://www.w3.org/2001/XMLSchema#double", "xsd:double" );

        register( "http://www.w3.org/2001/XMLSchema#short", "xsd:short" );

        register( "http://www.w3.org/2000/01/rdf-schema#Literal", "xsd:anySimpleType" );

        register( "http://www.w3.org/2000/01/rdf-schema#XMLLiteral", "xsd:anySimpleType" );

        register( "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral", "xsd:anySimpleType" );

        register( "http://www.w3.org/2001/XMLSchema#boolean", "xsd:boolean" );

        register( "http://www.w3.org/2001/XMLSchema#decimal", "xsd:decimal" );

        register( "http://www.w3.org/2001/XMLSchema#byte", "xsd:byte" );

        register( "http://www.w3.org/2001/XMLSchema#unsignedByte", "xsd:unsignedByte" );

        register( "http://www.w3.org/2001/XMLSchema#unsignedShort", "xsd:unsignedShort" );

        register( "http://www.w3.org/2001/XMLSchema#unsignedInt", "xsd:unsignedInt" );

        register( "http://www.w3.org/2001/XMLSchema#anyURI", "xsd:anyURI" );
    }


    @Override
    protected OntoModel buildProperties( OWLOntology ontoDescr, KieSession kSession, Map<InferenceTask, Resource> theory, OntoModel hierarchicalModel, DLFactoryConfiguration conf ) {

        OWLDataFactory factory = ontoDescr.getOWLOntologyManager().getOWLDataFactory();

        fillPropNamesInDataStructs( ontoDescr );

        // Complete missing domains and ranges for properties. Might be overridden later, if anything can be inferred
        createAndAddBasicProperties( ontoDescr, factory, hierarchicalModel );

        // Apply any cardinality / range restriction
        applyPropertyRestrictions( ontoDescr, hierarchicalModel, factory );

        // Compose property chains
        fixPropertyChains( ontoDescr, hierarchicalModel );

        // Manage inverse relations
        fixInverseRelations( ontoDescr, hierarchicalModel );

        // assign Key properties
        setKeys( ontoDescr, hierarchicalModel );

        fixRootHierarchy( hierarchicalModel );

        validate( hierarchicalModel );

        return hierarchicalModel;
    }

    @Override
    protected OntoModel buildIndividuals(OWLOntology ontoDescr, KieSession kSession, Map<InferenceTask, Resource> theory, OntoModel hierachicalModel, DLFactoryConfiguration conf ) {

        for ( OWLNamedIndividual individual : ontoDescr.getIndividualsInSignature( true ) ) {
            if ( logger.isInfoEnabled() ) { logger.info( "Found Individual " + individual.getIRI() ); };

            IRI iri = individual.getIRI();

            String typeIri = individualTypesCache.get( iri.toQuotedString() );
            Concept klass = hierachicalModel.getConcept( typeIri );
            if ( klass == null ) {
                logger.error( "found individual with no class " + iri );
                System.exit( -1 );
            }

            Individual ind = new Individual( iri.getFragment(), iri.toQuotedString(), klass.getFullyQualifiedName() );


            for ( OWLOntology onto : ontoDescr.getImportsClosure() ) {
                for ( OWLDataPropertyExpression prop : individual.getDataPropertyValues( onto ).keySet() ) {
                    if ( ! prop.isTopEntity() ) {
                        PropertyRelation rel = hierachicalModel.getProperty( prop.asOWLDataProperty().getIRI().toQuotedString() );
                        String propName = rel.getName();
                        Set<OWLLiteral> propValues = individual.getDataPropertyValues( onto ).get( prop );
                        Set<Individual.ValueTypePair> values = new HashSet<Individual.ValueTypePair>();
                        for ( OWLLiteral tgt : propValues ) {
                            String value = null;
                            String typeName = rel.getTarget().getFullyQualifiedName();
                            //TODO improve datatype checking
                            if ( typeName.equals( "xsd:string" )
                                 || typeName.equals( "xsd:dateTime" ) ) {
                                value = "\"" + tgt.getLiteral() + "\"";
                            } else {
                                value = tgt.getLiteral();
                            }
                            Individual.ValueTypePair vtp = new Individual.ValueTypePair( value, typeName );
                            values.add( vtp );
                        }
                        ind.setPropertyValues( propName, values );
                    }
                }

                for ( OWLObjectPropertyExpression prop : individual.getObjectPropertyValues( onto ).keySet() ) {
                    if ( ! prop.isTopEntity() ) {
                        String propName = hierachicalModel.getProperty( prop.asOWLObjectProperty().getIRI().toQuotedString()).getName();
                        Set<OWLIndividual> propValues = individual.getObjectPropertyValues( onto ).get( prop );
                        Set<Individual.ValueTypePair> values = new HashSet<Individual.ValueTypePair>();
                        for ( OWLIndividual tgt : propValues ) {
                            if ( tgt instanceof OWLNamedIndividual ) {
                                values.add( new Individual.ValueTypePair(
                                        ((OWLNamedIndividual) tgt).getIRI().getFragment(),
                                        "object" ) );
                            }
                        }
                        ind.setPropertyValues( propName, values );
                    }
                }
            }
            hierachicalModel.addIndividual( ind );

        }



        return hierachicalModel;
    }

    private void fixInverseRelations( OWLOntology ontoDescr, OntoModel hierarchicalModel ) {
        for ( OWLInverseObjectPropertiesAxiom ax : ontoDescr.getAxioms( AxiomType.INVERSE_OBJECT_PROPERTIES ) ) {
            String fst = ax.getFirstProperty().asOWLObjectProperty().getIRI().toQuotedString();
            if ( ! ax.getSecondProperty().isAnonymous() ) {
                String sec = ax.getSecondProperty().asOWLObjectProperty().getIRI().toQuotedString();

                PropertyRelation first = hierarchicalModel.getProperty( fst );
                PropertyRelation second = hierarchicalModel.getProperty( sec );

                if ( first != null && second != null ) {
                    if ( logger.isInfoEnabled() ) { logger.info(  "Marking " + first + " as Inverse" ); };
                    first.setInverse( true );
                }
            }
        }
    }

    private void setKeys( OWLOntology ontoDescr, OntoModel hierarchicalModel ) {
        for ( OWLHasKeyAxiom hasKey : ontoDescr.getAxioms( AxiomType.HAS_KEY ) ) {
            Concept con = conceptCache.get( hasKey.getClassExpression().asOWLClass().getIRI().toQuotedString() );
            for ( OWLDataPropertyExpression expr : hasKey.getDataPropertyExpressions() ) {
                String propIri = expr.asOWLDataProperty().getIRI().toQuotedString();
                PropertyRelation keyRel = con.getProperties().get( propIri );
                //con.addKey( keyRel.getName() );
                con.addKey( propIri );
            }
            for ( OWLObjectPropertyExpression expr : hasKey.getObjectPropertyExpressions() ) {
                String propIri = expr.asOWLObjectProperty().getIRI().toQuotedString();
                PropertyRelation keyRel = con.getProperties().get( propIri );
//                con.addKey( keyRel.getName() );
                con.addKey( propIri );
            }
        }
    }

    private void fixPropertyChains( OWLOntology ontoDescr, OntoModel hierarchicalModel ) {
        ontoDescr.getClassesInSignature( true );
        for ( OWLSubPropertyChainOfAxiom ax : ontoDescr.getAxioms( AxiomType.SUB_PROPERTY_CHAIN_OF ) ) {
            String propIri = ax.getSuperProperty().asOWLObjectProperty().getIRI().toQuotedString();
            PropertyRelation prop = hierarchicalModel.getProperty( propIri );
            List<PropertyRelation> chain = new ArrayList<PropertyRelation>();
            for ( OWLObjectPropertyExpression link : ax.getPropertyChain() ) {
                chain.add( hierarchicalModel.getProperty( link.asOWLObjectProperty().getIRI().toQuotedString() ) );
            }
            prop.addPropertyChain( chain );
        }
    }


    private void applyPropertyRestrictions( OWLOntology ontoDescr, OntoModel hierarchicalModel, OWLDataFactory factory ) {

        for ( PropertyRelation prop : hierarchicalModel.getProperties() ) {
            if ( prop.getTarget() == null ) {
                logger.warn( "Property without target concept " +  prop.getName() );
            }
        }


        Map<String, Set<OWLClassExpression>> supers = new HashMap<String, Set<OWLClassExpression>>();
        for ( OWLClass klass : ontoDescr.getClassesInSignature( true ) ) {
            supers.put( klass.getIRI().toQuotedString(), new HashSet<OWLClassExpression>() );
        }
        for ( OWLClass klass : ontoDescr.getClassesInSignature( true ) ) {
            if ( isDelegating( klass ) ) {
                OWLClassExpression delegate = aliases.get( klass );
                supers.get( delegate.asOWLClass().getIRI().toQuotedString() ).addAll( klass.getSuperClasses( ontoDescr ) );
            } else {
                Set<OWLClassExpression> sup = supers.get( klass.asOWLClass().getIRI().toQuotedString() );
                Set<OWLClassExpression> ancestors = klass.getSuperClasses( ontoDescr );
                sup.addAll(ancestors);
                for ( OWLClassExpression anc : ancestors ) {
                    if ( reverseAliases.containsKey( anc ) ) {
                        sup.addAll(reverseAliases.get(anc));
                    }
                }
            }
        }


        for ( Concept con : hierarchicalModel.getConcepts() ) {      //use concepts as they're sorted!


            if ( isDelegating( con.getIri() ) ) {
                continue;
            }

            if ( con == null ) {
                logger.warn( "Looking for superclasses of an undefined concept" );
                continue;
            }

            LinkedList<OWLClassExpression> orderedSupers = new LinkedList<OWLClassExpression>();
            // cardinalities should be fixed last
            for ( OWLClassExpression sup : supers.get( con.getIri() ) ) {
                if ( sup instanceof OWLCardinalityRestriction ) {
                    orderedSupers.addLast( sup );
                } else {
                    orderedSupers.addFirst( sup );
                }
            }

            for ( OWLClassExpression sup : orderedSupers ) {
                if ( sup.isClassExpressionLiteral() ) {
                    continue;
                }

                processSuperClass( sup, con, hierarchicalModel, factory );
            }


//            for ( PropertyRelation prop : con.getProperties().values() ) {
//                if ( prop.isRestricted() && ( prop.getMaxCard() == null || prop.getMaxCard() > 1 ) ) {
//                    prop.setName( prop.getName() + "s" );
//                }
//            }
        }

    }

    private void processSuperClass( OWLClassExpression sup, Concept con, OntoModel hierarchicalModel, OWLDataFactory factory ) {
        String propIri;
        PropertyRelation rel;
        Concept tgt;
        switch ( sup.getClassExpressionType() ) {
            case DATA_SOME_VALUES_FROM:
                //check that it's a subclass of the domain. Should have already been done, or it would be an inconsistency
                break;
            case DATA_ALL_VALUES_FROM:
                OWLDataAllValuesFrom forall = (OWLDataAllValuesFrom) sup;
                propIri = forall.getProperty().asOWLDataProperty().getIRI().toQuotedString();
                tgt = primitives.get( dataRangeToDataType( forall.getFiller(), factory ).getIRI().toQuotedString()  );
                if ( tgt.equals( "xsd:anySimpleType" ) ) {
                    break;
                }
                rel = extractProperty( con, propIri, tgt, null, null, true );
                if ( rel != null ) {
//                    hierarchicalModel.addProperty( rel );
                } else {
                    logger.warn( " Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case DATA_MIN_CARDINALITY:
                OWLDataMinCardinality min = (OWLDataMinCardinality) sup;
                propIri = min.getProperty().asOWLDataProperty().getIRI().toQuotedString();
                tgt = primitives.get( dataRangeToDataType( min.getFiller(), factory ).getIRI().toQuotedString()  );
                rel = extractProperty( con, propIri, tgt, min.getCardinality(), null, false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    logger.warn( " Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case DATA_MAX_CARDINALITY:
                OWLDataMaxCardinality max = (OWLDataMaxCardinality) sup;
                propIri = max.getProperty().asOWLDataProperty().getIRI().toQuotedString();
                tgt = primitives.get( dataRangeToDataType( max.getFiller(), factory ).getIRI().toQuotedString()  );
                rel = extractProperty( con, propIri, tgt, null, max.getCardinality(), false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    logger.warn( " Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case DATA_EXACT_CARDINALITY:
                OWLDataExactCardinality ex = (OWLDataExactCardinality) sup;
                propIri = ex.getProperty().asOWLDataProperty().getIRI().toQuotedString();
                tgt = primitives.get( dataRangeToDataType( ex.getFiller(), factory ).getIRI().toQuotedString()  );
                rel = extractProperty( con, propIri, tgt, ex.getCardinality(), ex.getCardinality(), false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    logger.warn( " Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case OBJECT_SOME_VALUES_FROM:
                OWLObjectSomeValuesFrom someO = (OWLObjectSomeValuesFrom) sup;
                propIri = someO.getProperty().asOWLObjectProperty().getIRI().toQuotedString();
                if ( filterAliases( someO.getFiller() ).isAnonymous() ) {
                    logger.warn( ": Complex unaliased restriction " + someO );
                    break;
                }
                tgt = conceptCache.get( filterAliases( someO.getFiller() ).asOWLClass().getIRI().toQuotedString() );
                rel = extractProperty( con, propIri, tgt, 1, null, false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    logger.warn( " Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case OBJECT_ALL_VALUES_FROM:
                OWLObjectAllValuesFrom forallO = (OWLObjectAllValuesFrom) sup;
                propIri = forallO.getProperty().asOWLObjectProperty().getIRI().toQuotedString();
                if ( filterAliases( forallO.getFiller() ).isAnonymous() ) {
                    logger.warn( ": Complex unaliased restriction " + forallO );
                    break;
                }
                tgt = conceptCache.get( filterAliases( forallO.getFiller() ).asOWLClass().getIRI().toQuotedString() );
                if ( tgt.equals( "<http://www.w3.org/2002/07/owl#Thing>" ) ) {
                    break;
                }
                rel = extractProperty( con, propIri, tgt, null, null, true );
                if ( rel != null ) {
                } else {
                    logger.warn( " Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case OBJECT_MIN_CARDINALITY:
                OWLObjectMinCardinality minO = (OWLObjectMinCardinality) sup;
                propIri = minO.getProperty().asOWLObjectProperty().getIRI().toQuotedString();
                tgt = conceptCache.get( filterAliases( minO.getFiller() ).asOWLClass().getIRI().toQuotedString() );
                rel = extractProperty( con, propIri, tgt, minO.getCardinality(), null, false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    logger.warn( " Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case OBJECT_MAX_CARDINALITY:
                OWLObjectMaxCardinality maxO = (OWLObjectMaxCardinality) sup;
                propIri = maxO.getProperty().asOWLObjectProperty().getIRI().toQuotedString();
                tgt = conceptCache.get( filterAliases( maxO.getFiller() ).asOWLClass().getIRI().toQuotedString() );
                rel = extractProperty( con, propIri, tgt, null, maxO.getCardinality(), false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    logger.warn( " Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case OBJECT_EXACT_CARDINALITY:
                OWLObjectExactCardinality exO = (OWLObjectExactCardinality) sup;
                propIri = exO.getProperty().asOWLObjectProperty().getIRI().toQuotedString();
                tgt = conceptCache.get( filterAliases( exO.getFiller() ).asOWLClass().getIRI().toQuotedString() );
                rel = extractProperty( con, propIri, tgt, exO.getCardinality(), exO.getCardinality(), false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    logger.warn( " Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case OBJECT_INTERSECTION_OF:
                OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) sup;
                for ( OWLClassExpression arg : and.asConjunctSet() ) {
                    processSuperClass( arg, con, hierarchicalModel, factory );
                }
                break;
            case OWL_CLASS:
                break;
            default:
                logger.warn( " Cannot handle " + sup );
        }

    }


    private String createSuffix(String role, String name, boolean plural) {
        String type = NameUtils.map( name, true );
        if ( type.indexOf(".") >= 0 ) {
            type = type.substring( type.lastIndexOf(".") + 1 );
        }
        return type + ( plural ? ( type.endsWith( "s" ) ? "es" : "s") : "" ); // + "As" + role;
    }

    private PropertyRelation extractProperty( Concept con, String propIri, Concept target, Integer min, Integer max, boolean restrictTarget ) {
        if ( target == null ) {
            logger.warn( "Null target for property " + propIri );
        }



        String restrictedSuffix = createSuffix( con.getName(), target.getName(), true );
        String restrictedPropIri = propIri.replace( ">", restrictedSuffix + ">" );


//        PropertyRelation rel = con.getProperties().get( propIri );
//        if ( rel == null ) {
//            rel = con.getProperties().get( restrictedPropIri );
//        }
//
//        if ( rel == null ) {
//            rel = inheritPropertyCopy( con, con, propIri );
//            inherited = true;
//        }


        boolean alreadyRestricted = false;
        PropertyRelation rel = con.getProperties().get( propIri );
        PropertyRelation originalProperty;

        if ( rel != null ) {
            originalProperty = rel;
            rel = cloneRel( originalProperty );
        } else {
            rel = con.getProperties().get( restrictedPropIri );

            if ( rel != null ) {
                originalProperty = rel;
                alreadyRestricted = true;
                rel = cloneRel( originalProperty );
            } else {
                PropertyRelation source = inheritPropertyCopy( con, con, restrictedPropIri );

                if ( source != null ) {
                    originalProperty = source;
                    alreadyRestricted = true;
                    rel = cloneRel( originalProperty );
                } else {
                    originalProperty = inheritPropertyCopy( con, con, propIri );
                    rel = cloneRel( originalProperty );
                }
            }
        }






        if ( rel != null ) {
            boolean tgtRestrictionApplied = restrictTarget && ! rel.getTarget().equals( target );
            if ( tgtRestrictionApplied ) {
                rel.restrictTargetTo( target );
            }

            boolean dirty = false;
            if ( target.getIri().equals( "<http://www.w3.org/2002/07/owl#Thing>" )
                    || target.getIri().equals("<http://www.w3.org/2000/01/rdf-schema#Literal>" )
                    || target.getEquivalentConcepts().contains( conceptCache.get( "<http://www.w3.org/2002/07/owl#Thing>" ) )
                    ) {
                target = rel.getTarget();
                restrictedSuffix = createSuffix( con.getName(), target.getName(), true );
                restrictedPropIri = propIri.replace ( ">", restrictedSuffix + ">" );
//                dirty = true;
            }
            if ( ! rel.getTarget().equals( target ) && ! restrictTarget ) {
                //TODO FIXME : check that target is really restrictive!
                if ( ! target.getIri().equals( "<http://www.w3.org/2002/07/owl#Thing>" )
                        && ! target.getIri().equals("<http://www.w3.org/2000/01/rdf-schema#Literal>")
                        ) {
                    rel.setTarget( target );
                    rel.setObject( target.getIri() );
                    dirty = true;
                }
            }
            if ( min != null && min > rel.getMinCard() ) {
                rel.setMinCard( min );
//                if ( min > 1 ) {
                dirty = true;
//                }
            }
            if ( max != null && ( rel.getMaxCard() == null || max < rel.getMaxCard() ) ) {
                rel.setMaxCard( max );
                if ( max == 1 ) {
                    if ( alreadyRestricted ) {
                        con.removeProperty( restrictedPropIri );
                    }
                    restrictedSuffix = createSuffix( con.getName(), target.getName(), false );
                    restrictedPropIri = propIri.replace ( ">", restrictedSuffix + ">" );
                }
                dirty = true;
            }
//            if ( dirty ) {

            if ( dirty ) {
                if ( ! target.isPrimitive() || ! rel.getDomain().equals( con ) ) {
                    rel.setRestricted( true );
                    rel.setName( originalProperty.getBaseProperty().getName() + restrictedSuffix );
                    rel.setProperty( restrictedPropIri );
                } else {
                    if ( rel.getMaxCard() != null && rel.getMaxCard() <= 1 ) {
                        rel.setSimple( true );
                    }
                }

            }

            rel.setSubject( con.getIri() );
            rel.setDomain( con );

            boolean inherited = false;
            for ( Concept sup : con.getSuperConcepts() ) {
                if ( sup.getEffectiveProperties().contains( rel ) ) {
                    inherited = true;
                    rel.setInherited( inherited );
                    break;
                }
            }

            if ( ! rel.mirrors( rel.getBaseProperty() ) ) {
                con.addProperty( rel.getProperty(), rel.getName(), rel );
            }


            if ( dirty ) {
                rel.setBaseProperty( originalProperty );
            } else {
                if ( logger.isDebugEnabled() ) { logger.debug( "No real restriction detected II "+ rel.getName() ); }
                rel = originalProperty;
            }

            rel.getDomain().addProperty( rel.getProperty(), rel.getName(), rel );

            return rel;

        } else {
            if ( logger.isDebugEnabled() ) { logger.debug( "No real restriction detected" ); }
            // rel is null and that is what will be returned
        }

        return rel;
    }




    private PropertyRelation inheritPropertyCopy( Concept original, Concept current, String propIri ) {
        PropertyRelation rel;
        for ( Concept sup : current.getSuperConcepts() ) {
//            String key = propIri.replace( "As"+current.getName(), "As"+sup.getName() );
            String key = propIri;
            rel = sup.getProperties().get( key );
            if ( rel != null ) {
                return rel;
            } else {
                rel = inheritPropertyCopy( original, sup, propIri );
                if ( rel != null ) {
                    return rel;
                }
            }
        }
        return null;

    }

    private PropertyRelation cloneRel( PropertyRelation rel ) {
        PropertyRelation clonedRel = new PropertyRelation( rel.getSubject(), rel.getProperty(), rel.getObject(), rel.getName() );
        clonedRel.setMinCard( rel.getMinCard() );
        clonedRel.setMaxCard( rel.getMaxCard() );
        clonedRel.setTarget( rel.getTarget() );
        clonedRel.setDomain( rel.getDomain() );
        clonedRel.setRestricted( rel.isRestricted() );
        return clonedRel;

    }


    private void createAndAddBasicProperties( OWLOntology ontoDescr, OWLDataFactory factory, OntoModel hierarchicalModel ) {
        int missDomain = 0;
        int missDataRange = 0;
        int missObjectRange = 0;

        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature( true ) ) {
            if ( ! dp.isOWLTopDataProperty() && ! dp.isOWLBottomDataProperty() ) {
                Set<OWLClassExpression> domains = dp.getDomains( ontoDescr.getImportsClosure() );
                if ( domains.isEmpty() ) {
                    domains.add( factory.getOWLThing() );
                    logger.warn( "Added missing domain for" + dp);
                    missDomain++;
                }
                Set<OWLDataRange> ranges = dp.getRanges( ontoDescr.getImportsClosure() );
                if ( ranges.isEmpty() ) {
                    ranges.add( factory.getRDFPlainLiteral() );
                    logger.warn( "Added missing range for" + dp);
                    missDataRange++;
                }

                for ( OWLClassExpression domain : domains ) {
                    for ( OWLDataRange range : ranges ) {
                        OWLClassExpression realDom = filterAliases( domain );
                        OWLDatatype dataRange = dataRangeToDataType( range, factory );
                        PropertyRelation rel = new PropertyRelation( realDom.asOWLClass().getIRI().toQuotedString(),
                                dp.getIRI().toQuotedString(),
                                dataRange.getIRI().toQuotedString(),
                                props.get( dp.getIRI().toQuotedString() ) );



                        Concept con = conceptCache.get( rel.getSubject() );
                        rel.setTarget( primitives.get( rel.getObject() ) );
                        rel.setDomain( con );

                        con.addProperty( rel.getProperty(), rel.getName(), rel );
                        hierarchicalModel.addProperty( rel );
                    }
                }
            }
        }


        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature( true ) ) {
            if ( ! op.isOWLTopObjectProperty() && ! op.isOWLBottomObjectProperty() ) {
                Set<OWLClassExpression> domains = op.getDomains( ontoDescr.getImportsClosure() );
                if ( domains.isEmpty() ) {
                    domains.add( factory.getOWLThing() );
                    logger.warn("Added missing domain for " + op);
                    missDomain++;
                }
                Set<OWLClassExpression> ranges = op.getRanges( ontoDescr.getImportsClosure() );
                if ( ranges.isEmpty() ) {
                    ranges.add( factory.getOWLThing() );
                    logger.warn("Added missing range for " + op);
                    missObjectRange++;
                }

                for ( OWLClassExpression domain : domains ) {
                    for ( OWLClassExpression range : ranges ) {
                        OWLClassExpression realDom = filterAliases( domain );
                        OWLClassExpression realRan = filterAliases( range );

                        if ( realDom.isAnonymous() || realRan.isAnonymous() ) {
                            logger.error("Domain and Range should no be anonymous at this point : DOM " + realDom + " RAN : " + range);
                        }
                        PropertyRelation rel = new PropertyRelation( realDom.asOWLClass().getIRI().toQuotedString(),
                                op.getIRI().toQuotedString(),
                                realRan.asOWLClass().getIRI().toQuotedString(),
                                props.get( op.getIRI().toQuotedString() ) );

                        Concept con = conceptCache.get( rel.getSubject() );
                        rel.setTarget( conceptCache.get( rel.getObject() ) );
                        rel.setDomain( con );

                        con.addProperty( rel.getProperty(), rel.getName(), rel );
                        hierarchicalModel.addProperty( rel );
                    }
                }
            }
        }

        if ( logger.isInfoEnabled() ) {
            logger.info( "Misses : ");
            logger.info( missDomain );
            logger.info( missDataRange );
            logger.info( missObjectRange );
        }
    }

    private OWLDatatype dataRangeToDataType( OWLDataRange range, OWLDataFactory factory ) {
        if ( range.isDatatype() ) {
            return range.asOWLDatatype();
        }
        if ( range instanceof OWLDataOneOf ) {
            OWLDataOneOf oneOf = (OWLDataOneOf) range;
            Iterator<OWLLiteral> literals = oneOf.getValues().iterator();
            OWLDatatype type = literals.next().getDatatype();
            while ( literals.hasNext() ) {
                OWLDatatype x = literals.next().getDatatype();
                if ( ! type.equals( x ) ) {
                    return factory.getTopDatatype();
                }
            }
            return type;
        }
        return range.asOWLDatatype();
    }


    private Map<OWLClassExpression,OWLClassExpression> buildAliasesForEquivalentClasses(OWLOntology ontoDescr) {
        Map<OWLClassExpression,OWLClassExpression> aliases = new HashMap<OWLClassExpression,OWLClassExpression>();
        Set<OWLEquivalentClassesAxiom> pool = new HashSet<OWLEquivalentClassesAxiom>();
        Set<OWLEquivalentClassesAxiom> temp = new HashSet<OWLEquivalentClassesAxiom>();


        for ( OWLClass klass : ontoDescr.getClassesInSignature( true ) ) {
            for ( OWLEquivalentClassesAxiom klassEq : ontoDescr.getEquivalentClassesAxioms( klass ) ) {
                for (OWLEquivalentClassesAxiom eq2 : klassEq.asPairwiseAxioms() ) {
                    pool.add( eq2 );
                }
            }
        }

        boolean stable = false;
        while ( ! stable ) {
            stable = true;
            temp.addAll( pool );
            for ( OWLEquivalentClassesAxiom eq2 : pool ) {

                List<OWLClassExpression> pair = eq2.getClassExpressionsAsList();
                OWLClassExpression first = pair.get( 0 );
                OWLClassExpression secnd = pair.get( 1 );
                OWLClassExpression removed = null;

                if ( aliases.containsValue(first) ) {
                    // add to existing eqSet, put reversed
                    // A->X,  add X->C          ==> A->X, C->X
                    removed = aliases.put( secnd, first );
                    if ( removed != null ) {
                        logger.warn("DUPLICATE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2);
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( aliases.containsValue(secnd) ) {
                    // add to existing eqSet, put as is
                    // A->X,  add C->X          ==> A->X, C->X
                    removed = aliases.put( first, secnd );
                    if ( removed != null ) {
                        logger.warn("DUPLICATE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2);
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( aliases.containsKey(first) ) {
                    // apply transitivity, reversed
                    // A->X,  add A->C          ==> A->X, C->X
                    removed = aliases.put( secnd, aliases.get( first ) );
                    if ( removed != null ) {
                        logger.warn("DUPLICATE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2);
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( aliases.containsKey(secnd) ) {
                    // apply transitivity, as is
                    // A->X,  add C->A          ==> A->X, C->X
                    removed = aliases.put( first, aliases.get( secnd ) );
                    if ( removed != null ) {
                        logger.warn("DUPLICATE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2);
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( ! first.isAnonymous() ) {
                    removed = aliases.put( secnd, first );
                    if ( removed != null ) {
                        logger.warn("DUPLICATE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2);
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( ! secnd.isAnonymous() ) {
                    removed = aliases.put( first, secnd );
                    if ( removed != null ) {
                        logger.warn("DUPLICATE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2);
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else {
                    // both anonymous
                }


            }
            pool.clear();
            pool.addAll(temp);
        }

        if ( ! pool.isEmpty() ) {
            logger.error("COULD NOT RESOLVE ANON=ANON EQUALITIES " + pool);
            for ( OWLEquivalentClassesAxiom eq2 : pool ) {
                List<OWLClassExpression> l = eq2.getClassExpressionsAsList();
                if ( ! (l.get(0).isAnonymous() && l.get(1).isAnonymous())) {
                    logger.error(" EQUALITY WAS NOT RESOLVED " + l);
                }
            }
        }


        if ( logger.isInfoEnabled() ) {
            logger.info("----------------------------------------------------------------------- " + aliases.size());
            for( Map.Entry<OWLClassExpression,OWLClassExpression> entry : aliases.entrySet() ) {
                logger.trace(entry.getKey() + " == " + entry.getValue());
            }
            logger.info("-----------------------------------------------------------------------");
        }


        for ( Map.Entry<OWLClassExpression,OWLClassExpression> entry : aliases.entrySet() ) {
            OWLClassExpression key = entry.getKey();
            OWLClassExpression val = entry.getValue();
            if ( ! reverseAliases.containsKey( val ) ) {
                reverseAliases.put( val, new HashSet<OWLClassExpression>() );
            }
            reverseAliases.get( val ).add( key );
        }
        return aliases;
    }

    private boolean isAbstract(OWLClassExpression clax) {
        return  clax.asOWLClass().getIRI().toQuotedString().contains("Filler");
    }






    private boolean processComplexSuperClassesDomainAndRanges(OWLOntology ontoDescr, OWLDataFactory factory) {
        boolean dirty = false;
        dirty |= processComplexSuperclasses( ontoDescr, factory );
        dirty |= processComplexDataPropertyDomains( ontoDescr, factory );
        dirty |= processComplexObjectPropertyDomains( ontoDescr, factory );
        dirty |= processComplexObjectPropertyRanges( ontoDescr, factory );
        return dirty;
    }


    private boolean processComplexSuperclasses(OWLOntology ontoDescr, OWLDataFactory factory ) {
        boolean dirty = false;
        for ( OWLSubClassOfAxiom sub : ontoDescr.getAxioms(AxiomType.SUBCLASS_OF, true) ) {

            if ( sub.getSuperClass().isAnonymous() ) {
                if ( sub.getSuperClass() instanceof OWLObjectUnionOf ) {
                    Iterator<OWLClassExpression> disjuncts = sub.getSuperClass().asDisjunctSet().iterator();

                    String orNames = asNamedClass( disjuncts.next(), factory, ontoDescr ).getIRI().getFragment();
                    while ( disjuncts.hasNext() ) {
                        OWLClass disjunct = asNamedClass( disjuncts.next(), factory, ontoDescr );
                        orNames += "or" + disjunct.asOWLClass().getIRI().getFragment();
                    }

                    OWLClass unjon = factory.getOWLClass( IRI.create(
                            NameUtils.separatingName( ontoDescr.getOntologyID().getOntologyIRI().toURI().toString() ) +
                                    orNames ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( unjon) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( sub.getSuperClass(), unjon ) ) );
                    dirty = true;
                }

            }

        }
        return dirty;
    }

    private OWLClass asNamedClass( OWLClassExpression owlClass, OWLDataFactory factory, OWLOntology ontoDescr ) {
        if ( ! owlClass.isAnonymous() ) {
            return owlClass.asOWLClass();
        }

        if ( anonNameAliases.containsKey( owlClass ) ) {
            return anonNameAliases.get( owlClass );
        }

        OWLClass alias = factory.getOWLClass( IRI.create(
                NameUtils.separatingName( ontoDescr.getOntologyID().getOntologyIRI().toURI().toString() ) +
                        "Anon" + counter++ ) );

        ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( alias ) ) );
        ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( owlClass, alias ) ) );

        anonNameAliases.put( owlClass, alias );
        return alias;
    }

    private boolean processComplexObjectPropertyDomains(OWLOntology ontoDescr, OWLDataFactory factory ) {
        boolean dirty = false;
        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature( true ) ) {
            String typeName = NameUtils.buildNameFromIri( op.getIRI().getStart(), op.getIRI().getFragment() );

            Set<OWLObjectPropertyDomainAxiom> domains = ontoDescr.getObjectPropertyDomainAxioms( op );
            if ( domains.size() > 1 ) {
                Set<OWLClassExpression> domainClasses = new HashSet<OWLClassExpression>();
                for ( OWLObjectPropertyDomainAxiom dom : domains ) {
                    domainClasses.add( dom.getDomain() );
                }
                OWLObjectIntersectionOf and = factory.getOWLObjectIntersectionOf( domainClasses );

                for ( OWLObjectPropertyDomainAxiom dom : domains ) {
                    ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, dom ) );
                }
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLObjectPropertyDomainAxiom( op, and ) ) );
                dirty = true;
            }

            if ( op.getDomains( ontoDescr ).size() > 1 ) {
                logger.warn( "Property " + op + " should have a single domain class, found " + op.getDomains( ontoDescr ) );
            }

            for ( OWLClassExpression dom : op.getDomains( ontoDescr.getImportsClosure() ) ) {
                if ( dom.isAnonymous() ) {
                    OWLClass domain = factory.getOWLClass( IRI.create(
                            NameUtils.separatingName( ontoDescr.getOntologyID().getOntologyIRI().toURI().toString() ) +
                                    NameUtils.capitalize( typeName ) +
                                    "Domain" ) );
                    OWLAnnotationAssertionAxiom ann = factory.getOWLAnnotationAssertionAxiom( factory.getOWLAnnotationProperty( IRI.create( "http://www.w3.org/2000/01/rdf-schema#comment" ) ),
                            domain.getIRI(),
                            factory.getOWLStringLiteral("abstract") );
                    if ( logger.isDebugEnabled() ) { logger.debug("REPLACED ANON DOMAIN " + op + " with " + domain + ", was " + dom); }
                    logger.warn( "REPLACED ANON DOMAIN " + op + " with " + domain + ", was " + dom );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( domain ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( domain, dom ) ) );

                    OWLOntology defining = lookupDefiningOntology( ontoDescr, op );
                    ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( defining, defining.getObjectPropertyDomainAxioms( op ).iterator().next() ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLObjectPropertyDomainAxiom( op, domain ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, ann ) );
                    dirty = true;
                }
            }

        }
        return dirty;
    }

    private OWLOntology lookupDefiningOntology( OWLOntology ontoDescr, OWLEntity axiom ) {
        List<OWLOntology> imports = ontoDescr.getOWLOntologyManager().getSortedImportsClosure( ontoDescr );
        OWLOntology defining = ontoDescr;
        for ( OWLOntology onto : imports ) {
            if ( onto.containsAxiom( ontoDescr.getOWLOntologyManager().getOWLDataFactory().getOWLDeclarationAxiom( axiom ) , false ) ) {
                defining = onto;
                break;
            }
        }
        return defining;
    }

    private boolean processComplexDataPropertyDomains(OWLOntology ontoDescr, OWLDataFactory factory ) {
        boolean dirty = false;
        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature( true ) ) {
            String typeName = NameUtils.buildNameFromIri( dp.getIRI().getStart(), dp.getIRI().getFragment() );

            OWLOntology defining = lookupDefiningOntology( ontoDescr, dp );
            Set<OWLDataPropertyDomainAxiom> domains = defining.getDataPropertyDomainAxioms( dp );
            if ( domains.size() > 1 ) {
                Set<OWLClassExpression> domainClasses = new HashSet<OWLClassExpression>();
                for ( OWLDataPropertyDomainAxiom dom : domains ) {
                    domainClasses.add( dom.getDomain() );
                }
                OWLObjectIntersectionOf and = factory.getOWLObjectIntersectionOf( domainClasses );

                for ( OWLDataPropertyDomainAxiom dom : domains ) {
                    defining.getOWLOntologyManager().applyChange( new RemoveAxiom( defining, dom ) );
                }
                defining.getOWLOntologyManager().applyChange( new AddAxiom( defining, factory.getOWLDataPropertyDomainAxiom(dp, and) ) );
                dirty = true;
            }

            if ( dp.getDomains( ontoDescr ).size() > 1 ) {
                logger.warn("Property " + dp + " should have a single domain class, found " + dp.getDomains(ontoDescr));
            }

            for ( OWLClassExpression dom : dp.getDomains( defining ) ) {
                if ( dom.isAnonymous() ) {
                    OWLClass domain = factory.getOWLClass( IRI.create(
                            NameUtils.separatingName( ontoDescr.getOntologyID().getOntologyIRI().toURI().toString() ) +
                                    NameUtils.capitalize( typeName ) +
                                    "Domain" ) );
                    OWLAnnotationAssertionAxiom ann = factory.getOWLAnnotationAssertionAxiom( factory.getOWLAnnotationProperty( IRI.create( "http://www.w3.org/2000/01/rdf-schema#comment" ) ),
                            domain.getIRI(),
                            factory.getOWLStringLiteral("abstract") );
                    if ( logger.isDebugEnabled() ) { logger.debug("INFO : REPLACED ANON DOMAIN " + dp + " with " + domain + ", was " + dom); }
                    defining.getOWLOntologyManager().applyChange( new AddAxiom( defining, factory.getOWLDeclarationAxiom( domain ) ) );
                    defining.getOWLOntologyManager().applyChange( new AddAxiom( defining, factory.getOWLEquivalentClassesAxiom( domain, dom ) ) );

                    defining.getOWLOntologyManager().applyChange( new RemoveAxiom( defining, defining.getDataPropertyDomainAxioms( dp ).iterator().next() ) );
                    defining.getOWLOntologyManager().applyChange( new AddAxiom( defining, factory.getOWLDataPropertyDomainAxiom(dp, domain) ) );
                    defining.getOWLOntologyManager().applyChange( new AddAxiom( defining, ann ) );
                    dirty = true;
                }
            }

        }
        return dirty;
    }


    private boolean processComplexObjectPropertyRanges( OWLOntology ontoDescr, OWLDataFactory factory ) {
        boolean dirty = false;
        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature( true ) ) {
            String typeName = NameUtils.buildNameFromIri( op.getIRI().getStart(), op.getIRI().getFragment() );

            Set<OWLObjectPropertyRangeAxiom> ranges = ontoDescr.getObjectPropertyRangeAxioms( op );
            if ( ranges.size() > 1 ) {
                Set<OWLClassExpression> rangeClasses = new HashSet<OWLClassExpression>();
                for ( OWLObjectPropertyRangeAxiom dom : ranges ) {
                    rangeClasses.add( dom.getRange() );
                }
                OWLObjectIntersectionOf and = factory.getOWLObjectIntersectionOf( rangeClasses );

                for ( OWLObjectPropertyRangeAxiom ran : ranges ) {
                    ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, ran ) );
                }
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLObjectPropertyRangeAxiom(op, and) ) );
                dirty = true;
            }

            if ( op.getRanges(ontoDescr).size() > 1 ) {
                logger.warn(" WARNING : Property " + op + " should have a single range class, found " + op.getRanges(ontoDescr));
            }

            for ( OWLClassExpression ran : op.getRanges( ontoDescr.getImportsClosure() ) ) {
                if ( ran.isAnonymous() ) {
                    OWLClass range = factory.getOWLClass( IRI.create(
                            NameUtils.separatingName( ontoDescr.getOntologyID().getOntologyIRI().toURI().toString() ) +
                                    NameUtils.capitalize( typeName ) +
                                    "Range" ) );
                    OWLAnnotationAssertionAxiom ann = factory.getOWLAnnotationAssertionAxiom( factory.getOWLAnnotationProperty( IRI.create( "http://www.w3.org/2000/01/rdf-schema#comment" ) ),
                            range.getIRI(),
                            factory.getOWLLiteral("abstract") );
                    if ( logger.isDebugEnabled() ) { logger.debug(" REPLACED ANON RANGE " + op + " with " + range + ", was " + ran); }
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( range ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( range, ran ) ) );

                    OWLObjectPropertyRangeAxiom rangx = null;
                    Set<OWLOntology> ontologies = ontoDescr.getImportsClosure();
                    for ( OWLOntology onto : ontologies ) {
                        Set<OWLObjectPropertyRangeAxiom> assertedRanges = onto.getObjectPropertyRangeAxioms( op );
                        if ( ! assertedRanges.isEmpty() ) {
                            rangx = assertedRanges.iterator().next();
                            ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, rangx ) );
                        }
                    }
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLObjectPropertyRangeAxiom(op, range) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, ann ) );
                    dirty = true;
                }
            }

        }
        return dirty;
    }



    private boolean preProcessIndividuals( OWLOntology ontoDescr, OWLDataFactory factory ) {
        boolean dirty = false;
        for ( OWLNamedIndividual ind : ontoDescr.getIndividualsInSignature( true ) ) {

            OWLOntology defining = lookupDefiningOntology( ontoDescr, ind );

            declareAnonymousIndividualSupertypes( defining, factory, ind );

            logger.info( "Defining ontology :  " + defining );
            for ( OWLAxiom ax : defining.getAxioms( AxiomType.CLASS_ASSERTION ) ) {
                logger.trace( ax );
            }

            logger.info( "Getting types for individual " + ind.getIRI() );
            Set<OWLClassExpression> types = ind.getTypes( defining );
            logger.info( "Found types in defining ontology" + types );
            if ( types.isEmpty() ) {
                ind.getTypes( ontoDescr.getImportsClosure() );
                logger.info( "Found types in all ontologies " + ontoDescr.getImportsClosure() + " >> " + types );
            }

            types = simplify( types, defining );
            if ( types.size() > 1 ) {
                OWLObjectIntersectionOf and = factory.getOWLObjectIntersectionOf( types );

                dirty = true;
                logger.warn( " Individual " + ind + " got a new combined type " + and );
                OWLClass type = factory.getOWLClass( IRI.create( ind.getIRI().getStart() + NameUtils.compactUpperCase( ind.getIRI().getFragment() ) + "Type" ) );
                defining.getOWLOntologyManager().applyChange( new AddAxiom( defining, factory.getOWLDeclarationAxiom( type ) ) );
                defining.getOWLOntologyManager().applyChange( new AddAxiom( defining, factory.getOWLSubClassOfAxiom( type, and ) ) );
                defining.getOWLOntologyManager().applyChange( new AddAxiom( defining, factory.getOWLClassAssertionAxiom( type, ind ) ) );

                individualTypesCache.put( ind.getIRI().toQuotedString(), type.getIRI().toQuotedString() );
            } else {
                if ( types.iterator().hasNext() ) {
                    individualTypesCache.put( ind.getIRI().toQuotedString(), types.iterator().next().asOWLClass().getIRI().toQuotedString() );
                } else {

                    logger.warn( "WARNING no type detected for individual " + ind.getIRI().toQuotedString() );
                }
            }

        }

        return dirty;
    }

    private void declareAnonymousIndividualSupertypes(OWLOntology ontoDescr, OWLDataFactory factory, OWLNamedIndividual ind ) {
        Set<OWLClassExpression> types = ind.getTypes( ontoDescr );
        int j = 0;
        for ( OWLClassExpression type : types ) {
            j++;
            if ( type.isAnonymous() ) {
                OWLClass temp = factory.getOWLClass( IRI.create( ind.getIRI().getStart() + NameUtils.compactUpperCase( ind.getIRI().getFragment() ) + "RestrictedType" + j ) );
                ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, factory.getOWLClassAssertionAxiom( type, ind ) ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( temp ) ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom(temp, type) ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLClassAssertionAxiom( temp, ind ) ) );
            }
        }

    }


    private Set<OWLClassExpression> simplify(Set<OWLClassExpression> types, OWLOntology ontoDescr) {
        if ( types.size() == 1 ) {
            return types;
        }
        Set<OWLClassExpression> ans = new HashSet<OWLClassExpression>( types );

        for ( OWLClassExpression klass1 : types ) {
            for ( OWLClassExpression klass2 : types ) {
                if ( isSuperClass( ontoDescr, klass1, klass2 ) ) {
                    ans.remove( klass1 );
                }
            }
        }

        return ans;
    }

    private boolean isSuperClass( OWLOntology ontoDescr, Set<OWLClassExpression> supers, OWLClassExpression klass2 ) {
        OWLClass k = klass2.asOWLClass();
        OWLOntology defining = lookupDefiningOntology( ontoDescr, k );
        Set<OWLSubClassOfAxiom> subKlassOfs = defining.getSubClassAxiomsForSubClass( k );
        for ( OWLSubClassOfAxiom sub : subKlassOfs ) {
            if ( supers.contains( sub.getSuperClass() ) ) {
                return true;
            } else {
                if ( ! sub.getSuperClass().isAnonymous() && isSuperClass( ontoDescr, supers, sub.getSuperClass() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSuperClass( OWLOntology ontoDescr, OWLClassExpression klass1, OWLClassExpression klass2 ) {
        Set<OWLSubClassOfAxiom> subKlassOfs = ontoDescr.getSubClassAxiomsForSuperClass( klass1.asOWLClass() );
        for ( OWLSubClassOfAxiom sub : subKlassOfs ) {
            if ( sub.getSubClass().equals( klass2 ) ) {
                return true;
            } else {
                if ( isSuperClass( ontoDescr, sub.getSubClass(), klass2 ) ) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean processQuantifiedRestrictions( OWLOntology ontoDescr, OWLDataFactory factory ) {
        boolean dirty = false;
        // infer domain / range from quantified restrictions...
        for ( OWLClass klassAx : ontoDescr.getClassesInSignature( true ) ) {
            OWLClass klass = klassAx;

            for ( OWLClassExpression clax : klass.getSuperClasses( ontoDescr ) ) {
                clax = clax.getNNF();
                dirty |= processQuantifiedRestrictionsInClass( klass, clax, ontoDescr, factory );
            }

            for ( OWLClassExpression clax : klass.getEquivalentClasses( ontoDescr ) ) {
                clax = clax.getNNF();
                dirty |= processQuantifiedRestrictionsInClass( klass, clax, ontoDescr, factory );
            }
        }
        return dirty;
    }

    private boolean processQuantifiedRestrictionsInClass(OWLClass klass, OWLClassExpression clax, OWLOntology ontology, OWLDataFactory fac) {
        final boolean[] dirty = { false };
        final OWLClass inKlass = klass;
        final OWLDataFactory factory = fac;
        final OWLOntology ontoDescr = ontology;



        clax.accept( new OWLClassExpressionVisitor() {



            private void process( OWLClassExpression expr ) {

                if ( expr instanceof OWLNaryBooleanClassExpression ) {
                    for (OWLClassExpression clax : ((OWLNaryBooleanClassExpression) expr).getOperandsAsList() ) {
                        process( clax );
                    }
                } else if ( expr instanceof OWLQuantifiedObjectRestriction  ) {
                    OWLQuantifiedObjectRestriction rest = (OWLQuantifiedObjectRestriction) expr;
//
                    OWLObjectProperty prop = rest.getProperty().asOWLObjectProperty();
                    OWLClassExpression fil = rest.getFiller();

                    boolean inDomain = checkIsPropertyInDomain( prop, inKlass, expr, ontoDescr );
                    if ( ! inDomain ) {
                        rewirePropertyDomain( prop, expr, ontoDescr );
                    }
                    reifyFiller( prop, fil );

                    process( fil );
                } else if ( expr instanceof OWLObjectCardinalityRestriction ) {
                    OWLObjectCardinalityRestriction rest = (OWLObjectCardinalityRestriction) expr;
//
                    OWLObjectProperty prop = rest.getProperty().asOWLObjectProperty();
                    OWLClassExpression fil = rest.getFiller();

                    boolean inDomain = checkIsPropertyInDomain( prop, inKlass, expr, ontoDescr );
                    if ( ! inDomain ) {
                        rewirePropertyDomain( prop, expr, ontoDescr );
                    }
                    reifyFiller( prop, fil );

                    process( fil );
                } else if (  expr instanceof  OWLQuantifiedDataRestriction ) {



                } else if ( expr instanceof OWLCardinalityRestriction ) {
                    if ( expr instanceof OWLDataMinCardinality ) {
                        minCards.put(inKlass.getIRI().toQuotedString(),
                                ((OWLDataMinCardinality) expr).getProperty().asOWLDataProperty().getIRI().toQuotedString(),
                                ((OWLDataMinCardinality) expr).getCardinality());
                        minCounter++;
                    } else if ( expr instanceof OWLDataMaxCardinality ) {
                        maxCards.put( inKlass.getIRI().toQuotedString(),
                                ((OWLDataMaxCardinality) expr).getProperty().asOWLDataProperty().getIRI().toQuotedString(),
                                ((OWLDataMaxCardinality) expr).getCardinality() );
                        maxCounter++;
                    } else if ( expr instanceof OWLObjectMaxCardinality ) {
                        maxCards.put( inKlass.getIRI().toQuotedString(),
                                ((OWLObjectMaxCardinality) expr).getProperty().asOWLObjectProperty().getIRI().toQuotedString(),
                                ((OWLObjectMaxCardinality) expr).getCardinality() );
                        process( ((OWLObjectMaxCardinality) expr).getFiller() );
                        maxCounter++;
                    } else if ( expr instanceof OWLObjectMinCardinality ) {
                        minCards.put( inKlass.getIRI().toQuotedString(),
                                ((OWLObjectMinCardinality) expr).getProperty().asOWLObjectProperty().getIRI().toQuotedString(),
                                ((OWLObjectMinCardinality) expr).getCardinality() );
                        process( ((OWLObjectMinCardinality) expr).getFiller() );
                        minCounter++;
                    }
                }
                else return;
            }

            private void rewirePropertyDomain( OWLObjectProperty prop, OWLClassExpression expr, OWLOntology ontoDescr ) {
                OWLOntology defining = lookupDefiningOntology( ontoDescr, prop );
                Set<OWLObjectPropertyDomainAxiom> domains = defining.getObjectPropertyDomainAxioms( prop );

                OWLClassExpression propDomain = null;
                for ( OWLObjectPropertyDomainAxiom dox : domains ) {
                    if ( ! dox.getDomain().isAnonymous() ) {
                        propDomain = dox.getDomain();
                    }
                    ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, dox ) );
                }
                OWLClass extDomain = factory.getOWLClass( IRI.create(
                        prop.getIRI().getStart(), NameUtils.capitalize( prop.getIRI().getFragment() ) + "ExtraDomain" + counter++ ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( extDomain ) ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLSubClassOfAxiom( propDomain, extDomain ) ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLSubClassOfAxiom( extDomain, ontoDescr.getOWLOntologyManager().getOWLDataFactory().getOWLThing() ) ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLObjectPropertyDomainAxiom( prop, extDomain ) ) );


                Set<OWLClassExpression> aliases = new HashSet<OWLClassExpression>();
                for ( OWLEquivalentClassesAxiom eq : ontoDescr.getAxioms( AxiomType.EQUIVALENT_CLASSES ) ) {
                    if ( eq.contains( expr ) ) {
                        aliases.addAll( eq.getClassExpressions() );
                    }
                }

                for ( OWLSubClassOfAxiom sub : ontoDescr.getAxioms( AxiomType.SUBCLASS_OF ) ) {
                    if ( aliases.contains( sub.getSuperClass() ) && ! sub.getSubClass().equals( extDomain ) ) {
                        ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, sub )  );
                        ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLSubClassOfAxiom( sub.getSubClass(), extDomain ) )  );
                    }
                }
                dirty[0] = true;
        }

            private boolean checkIsPropertyInDomain( OWLObjectProperty prop, OWLClass inKlass, OWLClassExpression expr, OWLOntology ontoDescr ) {
                Set<OWLClassExpression> domains = new HashSet<OWLClassExpression>();
                OWLOntology defining = lookupDefiningOntology( ontoDescr, prop );
                for ( OWLObjectPropertyDomainAxiom dom : defining.getObjectPropertyDomainAxioms( prop ) ) {
                    domains.add( dom.getDomain() );
                }
                if ( domains.size() == 0 ) {
                    // this property will become part of (Root)Thing
                    return true;
                }
                if ( domains.contains( inKlass ) ) {
                    return true;
                }
                if ( isSuperClass( ontoDescr, domains, inKlass ) ) {
                    return true;
                }

                defining = lookupDefiningOntology( ontoDescr, inKlass );
                for ( OWLEquivalentClassesAxiom eq : defining.getEquivalentClassesAxioms( inKlass ) ) {
                    for ( OWLClassExpression x : eq.getClassExpressions() ) {
                        if ( ! x.isAnonymous() ) {
                            if ( isSuperClass( ontoDescr, domains, x ) ) {
                                return true;
                            }
                        }
                    }
                }

                return false;
            }

            private void reifyFiller( OWLObjectProperty prop, OWLClassExpression fil ) {
                if ( fil.isAnonymous() ) {
                    OWLClass filler = fillerCache.get( fil );

                    if ( filler == null ) {
                        String fillerName =
                                NameUtils.separatingName( ontoDescr.getOntologyID().getOntologyIRI().toString() ) +
                                        NameUtils.capitalize( inKlass.getIRI().getFragment() ) +
                                        NameUtils.capitalize( prop.getIRI().getFragment() ) +
                                        "Filler" +
                                        (counter++);
                        filler = factory.getOWLClass( IRI.create( fillerName ) );
                        // On a second thought, fillers could stay real...
//                            OWLAnnotationAssertionAxiom ann = factory.getOWLAnnotationAssertionAxiom( factory.getOWLAnnotationProperty( IRI.create( "http://www.w3.org/2000/01/rdf-schema#comment" ) ),
//                                    filler.getIRI(),
//                                    factory.getOWLStringLiteral("abstract") );
//                            ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, ann ) );

                        fillerCache.put( fil, filler );
                    } else {
                        if ( logger.isDebugEnabled() )  { logger.debug("REUSED FILLER FOR" + fil); }
                    }

                    dirty[0] = true;
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( filler ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( filler, fil ) ) );


                }
            }

            public void visit(OWLClass ce) {
                process( ce );
            }

            public void visit(OWLObjectIntersectionOf ce) {
                process( ce );
            }

            public void visit(OWLObjectUnionOf ce) {
                process( ce );
            }

            public void visit(OWLObjectComplementOf ce) {
                process( ce );
            }

            public void visit(OWLObjectSomeValuesFrom ce) {
                process(ce);
            }

            public void visit(OWLObjectAllValuesFrom ce) {
                process(ce);
            }

            public void visit(OWLObjectHasValue ce) {
                process(ce);
            }

            public void visit(OWLObjectMinCardinality ce) {
                //                        minCards.put(inKlass, ce.getProperty().asOWLObjectProperty().getIRI().toQuotedString(), ce.getCardinality());
                //                        minCounter++;
                process(ce);
            }

            public void visit(OWLObjectExactCardinality ce) {
                //                        maxCards.put(inKlass, ce.getProperty().asOWLObjectProperty().getIRI().toQuotedString(), ce.getCardinality());
                //                        minCards.put(inKlass, ce.getProperty().asOWLObjectProperty().getIRI().toQuotedString(), ce.getCardinality());
                //                        minCounter++;
                //                        maxCounter++;
                process(ce);
            }

            public void visit(OWLObjectMaxCardinality ce) {
                //                        maxCards.put(inKlass, ce.getProperty().asOWLObjectProperty().getIRI().toQuotedString(), ce.getCardinality());
                //                        maxCounter++;
                process(ce);
            }

            public void visit(OWLObjectHasSelf ce) {
                throw new UnsupportedOperationException();
            }

            public void visit(OWLObjectOneOf ce) {
//                throw new UnsupportedOperationException();
            }

            public void visit(OWLDataSomeValuesFrom ce) {
                process(ce);
            }

            public void visit(OWLDataAllValuesFrom ce) {
                process(ce);
            }

            public void visit(OWLDataHasValue ce) {
                process(ce);
            }

            public void visit(OWLDataMinCardinality ce) {
                //                        minCards.put(inKlass, ce.getProperty().asOWLDataProperty().getIRI().toQuotedString(), ce.getCardinality());
                //                        minCounter++;
                process(ce);
            }

            public void visit(OWLDataExactCardinality ce) {
                //                        minCards.put(inKlass, ce.getProperty().asOWLDataProperty().getIRI().toQuotedString(), ce.getCardinality());
                //                        maxCards.put(inKlass, ce.getProperty().asOWLDataProperty().getIRI().toQuotedString(), ce.getCardinality());
                //                        maxCounter++;
                //                        minCounter++;
                process(ce);
            }

            public void visit(OWLDataMaxCardinality ce) {
                //                        maxCards.put(inKlass, ce.getProperty().asOWLDataProperty().getIRI().toQuotedString(), ce.getCardinality());
                //                        maxCounter++;
                process(ce);
            }
        });


        return dirty[0];
    }


    private void fillPropNamesInDataStructs( OWLOntology ontoDescr ) {
        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature( true ) ) {
            if ( ! dp.isTopEntity() && ! dp.isBottomEntity() ) {
                String propIri = dp.getIRI().toQuotedString();
                String propName = NameUtils.buildLowCaseNameFromIri( dp.getIRI().getFragment() );
                if ( propName == null ) {
                    propName = NameUtils.buildLowCaseNameFromIri( dp.getIRI().getStart() );
                }
                props.put( propIri, propName );
            }
        }

        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature( true ) ) {
            if ( ! op.isTopEntity() && ! op.isBottomEntity() ) {
                String propIri = op.getIRI().toQuotedString();
                String propName = NameUtils.buildLowCaseNameFromIri( op.getIRI().getFragment() );
                if ( propName == null ) {
                    propName = NameUtils.buildLowCaseNameFromIri( op.getIRI().getStart() );
                }
                props.put( propIri, propName );
            }
        }


    }


    private String reportStats( OWLOntology ontoDescr ) {
        StringBuilder sb = new StringBuilder();

        sb.append( " *** Stats for ontology  : " + ontoDescr.getOntologyID() );

        sb.append( " Number of classes " + ontoDescr.getClassesInSignature( true ).size() );
        sb.append( " \t Number of classes " + ontoDescr.getClassesInSignature( true ).size() );

        sb.append( " Number of datatypes " + ontoDescr.getDatatypesInSignature().size() );

        sb.append( " Number of dataProps " + ontoDescr.getDataPropertiesInSignature( true ).size() );
        sb.append( "\t Number of dataProp domains " + ontoDescr.getAxiomCount( AxiomType.DATA_PROPERTY_DOMAIN ));
        for ( OWLDataProperty p : ontoDescr.getDataPropertiesInSignature( true ) ) {
            int num = ontoDescr.getDataPropertyDomainAxioms( p ).size();
            if ( num != 1 ) {
                sb.append( "\t\t Domain" + p + " --> " + num );
            } else {
                OWLDataPropertyDomainAxiom dom = ontoDescr.getDataPropertyDomainAxioms( p ).iterator().next();
                if ( ! ( dom.getDomain() instanceof OWLClass ) ) {
                    sb.append( "\t\t Complex Domain"  + p + " --> " + dom.getDomain() );
                }
            }
        }
        sb.append( "\t Number of dataProp ranges " + ontoDescr.getAxiomCount( AxiomType.DATA_PROPERTY_RANGE ));
        for ( OWLDataProperty p : ontoDescr.getDataPropertiesInSignature( true ) ) {
            int num = ontoDescr.getDataPropertyRangeAxioms( p ).size();
            if ( num != 1 ) {
                sb.append( "\t\t Range" + p + " --> " + num );
            } else {
                OWLDataPropertyRangeAxiom range = ontoDescr.getDataPropertyRangeAxioms( p ).iterator().next();
                if ( ! ( range.getRange() instanceof OWLDatatype ) ) {
                    sb.append( "\t\t Complex Range" + p + " --> " + range.getRange()  );
                }
            }
        }

        sb.append( " Number of objProps " + ontoDescr.getObjectPropertiesInSignature( true ).size() );
        sb.append( "\t Number of objProp domains " + ontoDescr.getAxiomCount( AxiomType.OBJECT_PROPERTY_DOMAIN ));
        for ( OWLObjectProperty p : ontoDescr.getObjectPropertiesInSignature( true ) ) {
            int num = ontoDescr.getObjectPropertyDomainAxioms( p ).size();
            if ( num != 1 ) {
                sb.append( "\t\t Domain" + p + " --> " + num );
            } else {
                OWLObjectPropertyDomainAxiom dom = ontoDescr.getObjectPropertyDomainAxioms( p ).iterator().next();
                if ( ! ( dom.getDomain() instanceof OWLClass ) ) {
                    sb.append( "\t\t Complex Domain" + p + " --> " + dom.getDomain()  );
                }
            }
        }
        sb.append( "\t Number of objProp ranges " + ontoDescr.getAxiomCount( AxiomType.OBJECT_PROPERTY_RANGE ));
        for ( OWLObjectProperty p : ontoDescr.getObjectPropertiesInSignature( true ) ) {
            int num = ontoDescr.getObjectPropertyRangeAxioms( p ).size();
            if ( num != 1 ) {
                sb.append( "\t\t Range" + p + " --> " + num );
            }  else {
                OWLObjectPropertyRangeAxiom range = ontoDescr.getObjectPropertyRangeAxioms( p ).iterator().next();
                if ( ! ( range.getRange() instanceof OWLClass ) ) {
                    sb.append( "\t\t Complex Domain" + p + " --> " + range.getRange()  );
                }
            }
        }

        return sb.toString();
    }







    private OWLProperty lookupDataProperty(String propId, Set<OWLDataProperty> set ) {
        for (OWLDataProperty prop : set ) {
            if ( prop.getIRI().toQuotedString().equals( propId ) ) {
                return prop;
            }
        }
        return null;
    }

    private OWLProperty lookupObjectProperty(String propId, Set<OWLObjectProperty> set ) {
        for (OWLObjectProperty prop : set ) {
            if ( prop.getIRI().toQuotedString().equals( propId ) ) {
                return prop;
            }
        }
        return null;
    }


    protected OntoModel buildClassLattice( OWLOntology ontoDescr,
                                           KieSession kSession,
                                           Map<InferenceTask,
                                                   Resource> theory,
                                           OntoModel baseModel,
                                           DLFactoryConfiguration conf ) {

        boolean dirty = true;
        OWLDataFactory factory = ontoDescr.getOWLOntologyManager().getOWLDataFactory();

        launchReasoner( dirty, kSession, ontoDescr, conf.getAxiomGens() );


        // reify complex superclasses, domains and ranges
        dirty |= processComplexSuperClassesDomainAndRanges( ontoDescr, factory );

        /************************************************************************************************************************************/

        // check individuals for multiple inheritance
        dirty |= preProcessIndividuals( ontoDescr, ontoDescr.getOWLOntologyManager().getOWLDataFactory() );

        /************************************************************************************************************************************/

        // new classes have been added, classify the
        launchReasoner( dirty, kSession, ontoDescr, conf.getAxiomGens() );

        /************************************************************************************************************************************/

        // reify complex restriction fillers
        dirty = processQuantifiedRestrictions( ontoDescr, factory );

        /************************************************************************************************************************************/

        // new classes have been added, classify the
        if ( ! conf.isDisableFullReasoner() ) {
            launchReasoner( dirty, kSession, ontoDescr, conf.getAxiomGens() );
        }

        /************************************************************************************************************************************/

        // resolve aliases, choosing delegators
        aliases = buildAliasesForEquivalentClasses( ontoDescr );

        /************************************************************************************************************************************/

        if ( logger.isInfoEnabled() ) {
            logger.info( reportStats( ontoDescr ) );
        }


        // classes are stable now
        addConceptsToModel( kSession, ontoDescr, baseModel );

        // lattice is too. applies aliasing
        addSubConceptsToModel( ontoDescr, baseModel );


        kSession.fireAllRules();


        return baseModel;
    }


    private void fixRootHierarchy(OntoModel model) {
        Concept thing = model.getConcept( IRI.create( NamespaceUtils.getNamespaceByPrefix( "owl" ).getURI() + "#Thing"  ).toQuotedString() );
        if ( thing.getProperties().size() > 0 ) {
            Concept localRoot = new Concept(
                    IRI.create( NameUtils.separatingName( model.getDefaultNamespace() ) + "RootThing" ),
                    "RootThing",
                    false );
            model.addConcept( localRoot );

            localRoot.addSuperConcept( thing );
            thing.getSubConcepts().add( localRoot );

            for ( String propIri : thing.getProperties().keySet() ) {
                PropertyRelation rel = thing.getProperty( propIri );
                rel.setDomain( localRoot );
                localRoot.addProperty( propIri, rel.getName(), rel );
            }
            thing.getProperties().clear();

            for ( Concept con : model.getConcepts() ) {
                if ( con == localRoot ) {
                    continue;
                }
                if ( con.getSuperConcepts().contains( thing ) ) {
                    con.getSuperConcepts().remove( thing );
                    con.getSuperConcepts().add( localRoot );
                }
                if ( thing.getSubConcepts().contains( con ) ) {
                    thing.getSubConcepts().remove( con );
                    localRoot.getSubConcepts().add( con );
                }
            }

            for ( PropertyRelation prop : model.getProperties() ) {
                fixProperty( prop, thing, localRoot );
            }
        }
    }

    private void fixProperty( PropertyRelation prop, Concept thing, Concept localRoot ) {
        if ( prop.getDomain() == thing ) {
            prop.setDomain( localRoot );
        }
        if ( prop.getTarget() == thing ) {
            prop.setTarget( localRoot );
        }
        for ( PropertyRelation sub : prop.getRestrictedProperties() ) {
            fixProperty( sub, thing, localRoot );
        }
    }


    private void processSubConceptAxiom( OWLClassExpression subClass, OWLClassExpression superClass, Map<String, Collection<String>> supers, String thing ) {
        if ( ! superClass.isAnonymous() || superClass instanceof OWLObjectUnionOf ) {

            String sub = filterAliases( subClass ).asOWLClass().getIRI().toQuotedString();
            String sup = isDelegating( superClass ) ?
                    thing : filterAliases( superClass ).asOWLClass().getIRI().toQuotedString();

            addSuper( sub, sup, supers );
        } else if ( superClass instanceof OWLObjectIntersectionOf ) {
            OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) superClass;
            for ( OWLClassExpression ex : and.asConjunctSet() ) {
                processSubConceptAxiom( subClass, ex, supers, thing );
            }
        }
    }

    private void addSuper(String sub, String sup, Map<String, Collection<String>> supers) {
        if ( sub.equals( sup ) ) {
            return;
        }
        Collection<String> ancestors = supers.get( sub );
        if ( ancestors == null ) {
            ancestors = new HashSet<String>();
            supers.put( sub, ancestors );
        }
        ancestors.add( sup );
    }

    private void addSubConceptsToModel( OWLOntology ontoDescr, OntoModel model) {

        Map<String,Collection<String>> supers = new HashMap<String, Collection<String>>();

        String thing = ontoDescr.getOWLOntologyManager().getOWLDataFactory().getOWLThing().getIRI().toQuotedString();
        supers.put( thing, Collections.EMPTY_SET );

        for ( OWLSubClassOfAxiom ax : ontoDescr.getAxioms( AxiomType.SUBCLASS_OF ) ) {
            processSubConceptAxiom( ax.getSubClass(), ax.getSuperClass(), supers, thing );
        }
        for ( OWLEquivalentClassesAxiom ax : ontoDescr.getAxioms( AxiomType.EQUIVALENT_CLASSES ) ) {
            processSubConceptAxiom( ax.getClassExpressionsAsList().get( 0 ), filterAliases( ax.getClassExpressionsAsList().get( 1 ) ), supers, thing );
        }

        for ( OWLClassExpression delegator : aliases.keySet() ) {
            if ( ! delegator.isAnonymous() ) {
                addSuper( delegator.asOWLClass().getIRI().toQuotedString(), thing, supers );
            }
        }

        for ( OWLClassExpression delegator : aliases.keySet() ) {
            if ( ! delegator.isAnonymous() ) {
                OWLClassExpression delegate = aliases.get( delegator );
                String sub = delegate.asOWLClass().getIRI().toQuotedString();
                String sup = delegator.asOWLClass().getIRI().toQuotedString();
                conceptCache.get( sub ).getEquivalentConcepts().add( conceptCache.get( sup ) );
                addSuper(sub, sup, supers);
            }
        }

        HierarchySorter<String> sorter = new HierarchySorter<String>();
        List<String> sortedCons = sorter.sort( supers );

        ArrayList missing = new ArrayList( conceptCache.keySet() );
        missing.removeAll( supers.keySet() );

        LinkedHashMap<String,Concept> sortedCache = new LinkedHashMap<String, Concept>();
        for ( String con : sortedCons ) {
            sortedCache.put( con, conceptCache.get( con ) );
        }
        conceptCache.clear();
        conceptCache = sortedCache;


        reduceTransitiveInheritance( sortedCache, supers );


        for ( String con : supers.keySet() ) {
            Collection<String> parents = supers.get( con );
            for ( String sup : parents ) {
                SubConceptOf subConceptOf = new SubConceptOf( con, sup );
                model.addSubConceptOf( subConceptOf );
                conceptCache.get( subConceptOf.getSubject() ).addSuperConcept( conceptCache.get( subConceptOf.getObject() ) );
            }
        }
    }

    private void reduceTransitiveInheritance(LinkedHashMap<String, Concept> sortedCache, Map<String, Collection<String>> supers) {
        Map<String, Collection<String>> taboos = new HashMap<String, Collection<String>>();
        for ( String con : sortedCache.keySet() ) {
            Collection<String> ancestors = supers.get( con );
            Set<String> taboo = new HashSet<String>();
            for ( String anc : ancestors ) {
                if ( taboos.containsKey( anc ) ) {
                    taboo.addAll( taboos.get( anc ) );
                }
            }
            ancestors.removeAll( taboo );
            taboo.addAll( ancestors );
            taboos.put( con, taboo );
        }
    }

    private boolean isDelegating( OWLClassExpression superClass ) {
        return aliases.containsKey( superClass );
    }

    private boolean isDelegating( String classIri ) {
        for ( OWLClassExpression klass : aliases.keySet() ) {
            if ( ! klass.isAnonymous() && klass.asOWLClass().getIRI().toQuotedString().equals( classIri ) ) {
                return true;
            }
        }
        return false;
    }

    private OWLClassExpression filterAliases( OWLClassExpression klass ) {
        if ( aliases.containsKey( klass ) ) {
            return aliases.get( klass );
        } else return klass;
    }


    private void addConceptsToModel(KieSession kSession, OWLOntology ontoDescr, OntoModel baseModel) {
        Set<OWLClass> kis = ontoDescr.getClassesInSignature( true );
        Set dek = ontoDescr.getAxioms( AxiomType.DECLARATION );

        for ( OWLClass con : ontoDescr.getClassesInSignature( true ) ) {
            if ( baseModel.getConcept( con.getIRI().toQuotedString()) == null ) {
                Concept concept =  new Concept(
                        con.getIRI(),
                        NameUtils.buildNameFromIri( con.getIRI().getStart(), con.getIRI().getFragment() ),
                        con.isOWLDatatype() );

                for ( OWLAnnotation ann : con.getAnnotations( ontoDescr ) ) {
                    if ( ann.getProperty().isComment() && ann.getValue() instanceof OWLLiteral ) {
                        OWLLiteral lit = (OWLLiteral) ann.getValue();
                        if ( lit.getLiteral().trim().equals( "abstract" ) ) {
                            concept.setAbstrakt( true );
                        }
                    }
                }

                if ( concept.getName().endsWith( "Range" ) || concept.getName().endsWith( "Domain" ) || concept.getName().matches( "\\S*Filler\\d+" ) ) {
                    concept.setAnonymous( true );
                }

                baseModel.addConcept( concept );
                kSession.insert(concept);
                conceptCache.put(con.getIRI().toQuotedString(), concept);
            }
        }
    }


    private void addDataRange(Map<OWLProperty, Set<OWLDataRange>> dataRanges, OWLDataProperty dp, OWLDataRange owlData, OWLDataFactory factory ) {
        Set<OWLDataRange> set = dataRanges.get( dp );
        if ( set == null ) {
            set = new HashSet<OWLDataRange>();
            dataRanges.put( dp, set );
        }
        set.add( owlData );
        if ( set.size() >= 2 && set.contains( factory.getTopDatatype() ) ) {
            set.remove( factory.getTopDatatype() );
        }
    }

    private void addRange(Map<OWLProperty, Set<OWLClassExpression>> ranges, OWLProperty rp, OWLClassExpression owlKlass, OWLDataFactory factory ) {
        Set<OWLClassExpression> set = ranges.get( rp );
        if ( set == null ) {
            set = new HashSet<OWLClassExpression>();
            ranges.put( rp, set );
        }
        set.add( owlKlass );
        if ( set.size() >= 2 && set.contains( factory.getOWLThing() ) ) {
            set.remove( factory.getOWLThing() );
        }

    }

    private void addDomain(Map<OWLProperty, Set<OWLClassExpression>> domains, OWLProperty dp, OWLClassExpression owlKlass, OWLDataFactory factory) {
        Set<OWLClassExpression> set = domains.get( dp );
        if ( set == null ) {
            set = new HashSet<OWLClassExpression>();
            domains.put( dp, set );
        }

        set.add( owlKlass );
        if ( set.size() >= 2 && set.contains( factory.getOWLThing() ) ) {
            set.remove( factory.getOWLThing() );
        }
    }



    private void setDomain(Map<OWLProperty, Set<OWLClassExpression>> domains, OWLProperty dp, OWLClassExpression owlKlass, OWLDataFactory factory ) {
        Set<OWLClassExpression> set = domains.get( dp );
        if ( set == null ) {
            set = new HashSet<OWLClassExpression>();
            domains.put( dp, set );
        } else {
            set.clear();
            set.add( owlKlass );
        }
    }

    private void setRange(Map<OWLProperty, Set<OWLClassExpression>> ranges, OWLProperty rp, OWLClassExpression owlKlass, OWLDataFactory factory ) {
        Set<OWLClassExpression> set = ranges.get( rp );
        if ( set == null ) {
            set = new HashSet<OWLClassExpression>();
            ranges.put( rp, set );
        } else {
            set.clear();
            set.add( owlKlass );
        }

    }






    private boolean validate( OntoModel model ) {

        for ( PropertyRelation rel : model.getProperties() ) {
            if ( ! rel.isRestricted() ) {
                if ( rel != rel.getBaseProperty() ) {
                    throw new IllegalStateException( "Property is not restricted, but not a base property either " + rel + " >> base " + rel.getBaseProperty() );
                }
            }
            for ( PropertyRelation rest : rel.getRestrictedProperties() ) {
                checkForRestriction( rest, rel, rel.getBaseProperty() );
            }
        }
        return true;
    }

    private void checkForRestriction( PropertyRelation restricted, PropertyRelation parent, PropertyRelation base ) {
        if ( restricted.getBaseProperty() != base ) {
            throw new IllegalStateException( "Inconsistent base property for" + restricted + " >> base " + restricted.getBaseProperty() + " , expected " + base );
        }
        if ( restricted.getImmediateBaseProperty() != parent ) {
            throw new IllegalStateException( "Inconsistent parent property for" + restricted + " >> parent  " + restricted.getImmediateBaseProperty() + " , expected " + parent );
        }
        int diff = diff( restricted, parent );
        if ( 0 == diff ) {
            throw new IllegalStateException( "Inconsistent restriction for " + restricted + " >> parent  " + parent  );
        }
        for ( PropertyRelation subRestr : restricted.getRestrictedProperties() ) {
            checkForRestriction( subRestr, restricted, base );
        }
    }

    private enum DIFF_BY {
        DOMAIN( (byte) 1 ), RANGE( (byte) 2 ), MIN( (byte) 4 ), MAX( (byte) 8 );

        DIFF_BY( byte x ) { bit = x; }

        private byte bit;

        public byte getBit() { return bit; }
    }

    private int diff( PropertyRelation restricted, PropertyRelation parent ) {
        int diff = 0;
        if ( ! restricted.getDomain().equals( parent.getDomain() ) ) {
            diff |= DIFF_BY.DOMAIN.getBit();
        }
        if ( ! restricted.getTarget().equals( parent.getTarget() ) ) {
            diff |= DIFF_BY.RANGE.getBit();
        }
        if ( restricted.getMinCard() == null && parent.getMinCard() != null
                || ! restricted.getMinCard().equals( parent.getMinCard() ) ) {
            diff |= DIFF_BY.MIN.getBit();
        }
        if ( restricted.getMaxCard() == null && parent.getMaxCard() != null
                || restricted.getMaxCard() != null && parent.getMaxCard() == null
                || ( restricted.getMaxCard() != null && ! restricted.getMaxCard().equals( parent.getMaxCard() ) )
                ) {
            diff |= DIFF_BY.MAX.getBit();
        }
        return diff;
    }







    private void launchReasoner( boolean dirty,
                                 KieSession kSession,
                                 OWLOntology ontoDescr,
                                 List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators ) {
        if ( dirty ) {
            long now = new Date().getTime();
            if ( logger.isInfoEnabled() ) {
                logger.info( " START REASONER " );
            }

            InferredOntologyGenerator reasoner = initReasoner( kSession, ontoDescr, axiomGenerators );

            reasoner.fillOntology( ontoDescr.getOWLOntologyManager(), ontoDescr );

            if ( logger.isInfoEnabled() ) {
                logger.info( " STOP REASONER : time elapsed >> " + ( new Date().getTime() - now ) );
            }

        } else {
            if ( logger.isInfoEnabled() ) {
                logger.info( " REASONER NOT NEEDED" );
            }
        }


    }




    protected InferredOntologyGenerator initReasoner( KieSession kSession,
                                                      OWLOntology ontoDescr,
                                                      List<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators ) {

        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);


        OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
//                if ( owler == null ) {
        OWLReasoner owler = reasonerFactory.createReasoner( ontoDescr, config );
        owler.precomputeInferences(
                InferenceType.CLASS_HIERARCHY,
                InferenceType.CLASS_ASSERTIONS,

                InferenceType.OBJECT_PROPERTY_ASSERTIONS,
                InferenceType.DATA_PROPERTY_ASSERTIONS,

                InferenceType.DIFFERENT_INDIVIDUALS,
                InferenceType.SAME_INDIVIDUAL,

                InferenceType.DISJOINT_CLASSES,

                //                                        InferenceType.DATA_PROPERTY_HIERARCHY,
                InferenceType.OBJECT_PROPERTY_HIERARCHY
        );


        if ( ! owler.isConsistent() ) {
            throw new RuntimeException( "Inconsistent ontology " );
        }

        return new InferredOntologyGenerator( owler, axiomGenerators );

    }


}
