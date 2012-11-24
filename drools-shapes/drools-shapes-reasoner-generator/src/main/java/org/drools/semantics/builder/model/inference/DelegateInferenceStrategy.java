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
import org.drools.io.Resource;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.builder.DLFactory;
import org.drools.semantics.utils.NameUtils;
import org.drools.semantics.builder.model.*;
import org.drools.semantics.utils.NamespaceUtils;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.*;

import java.util.*;

public class DelegateInferenceStrategy extends AbstractModelInferenceStrategy {

    private static int counter = 0;

    public static int minCounter = 0;
    public static int maxCounter = 0;

    private OWLReasoner               owler;
    private InferredOntologyGenerator reasoner;

    private Map<OWLClassExpression,OWLClassExpression> aliases;

    private Map<String, Concept> conceptCache = new HashMap<String, Concept>();
    private Map<String, String> individualTypesCache = new HashMap<String, String>();
    private Map<OWLClassExpression, OWLClass> fillerCache = new HashMap<OWLClassExpression, OWLClass>();
    private Map<String, String> props = new HashMap<String, String>();



    private static DLFactory.SupportedReasoners externalReasoner = DLFactory.SupportedReasoners.HERMIT;
    public static void setExternalReasoner( DLFactory.SupportedReasoners newReasoner ) {
        externalReasoner = newReasoner;
    }



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

        register( "http://www.w3.org/2001/XMLSchema#boolean", "xsd:boolean" );

        register( "http://www.w3.org/2001/XMLSchema#decimal", "xsd:decimal" );

        register( "http://www.w3.org/2001/XMLSchema#byte", "xsd:byte" );

        register( "http://www.w3.org/2001/XMLSchema#unsignedByte", "xsd:unsignedByte" );

        register( "http://www.w3.org/2001/XMLSchema#unsignedShort", "xsd:unsignedShort" );

        register( "http://www.w3.org/2001/XMLSchema#unsignedInt", "xsd:unsignedInt" );
    }


    @Override
    protected OntoModel buildProperties( OWLOntology ontoDescr, StatefulKnowledgeSession kSession, Map<InferenceTask, Resource> theory, OntoModel hierarchicalModel ) {

        OWLDataFactory factory = ontoDescr.getOWLOntologyManager().getOWLDataFactory();

        fillPropNamesInDataStructs( ontoDescr );

        // Complete missing domains and ranges for properties. Might be overridden later, if anything can be inferred
        createAndAddBasicProperties( ontoDescr, factory, hierarchicalModel );

        // Apply any cardinality / range restriction
        applyPropertyRestrictions( ontoDescr, hierarchicalModel );

        // Compose property chains
        fixPropertyChains( ontoDescr, hierarchicalModel );

        // Manage inverse relations
        fixInverseRelations( ontoDescr, hierarchicalModel );

        // assign Key properties
        setKeys( ontoDescr, hierarchicalModel );

        fixRootHierarchy( hierarchicalModel );

        return hierarchicalModel;
    }

    @Override
    protected OntoModel buildIndividuals(OWLOntology ontoDescr, StatefulKnowledgeSession kSession, Map<InferenceTask, Resource> theory, OntoModel hierachicalModel) {


        for ( OWLNamedIndividual individual : ontoDescr.getIndividualsInSignature() ) {
            System.out.println( "Found Individual " + individual.getIRI() );

            IRI iri = individual.getIRI();
            
            String typeIri = individualTypesCache.get( iri.toQuotedString() );
            Concept klass = hierachicalModel.getConcept( typeIri );
            if ( klass == null ) {
                System.exit( -1 );
            }
            
            Individual ind = new Individual( iri.getFragment(), iri.toQuotedString(), klass.getFullyQualifiedName() );


            for ( OWLDataPropertyExpression prop : individual.getDataPropertyValues( ontoDescr ).keySet() ) {
                if ( ! prop.isTopEntity() ) {
                    PropertyRelation rel = hierachicalModel.getProperty( prop.asOWLDataProperty().getIRI().toQuotedString() );
                    String propName = rel.getName();
                    Set<OWLLiteral> propValues = individual.getDataPropertyValues( ontoDescr ).get( prop );
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
            for ( OWLObjectPropertyExpression prop : individual.getObjectPropertyValues( ontoDescr ).keySet() ) {
                if ( ! prop.isTopEntity() ) {
                    String propName = hierachicalModel.getProperty( prop.asOWLObjectProperty().getIRI().toQuotedString()).getName();
                    Set<OWLIndividual> propValues = individual.getObjectPropertyValues(ontoDescr).get( prop );
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
            
            hierachicalModel.addIndividual( ind );

        }



        return hierachicalModel;
    }

    private void fixInverseRelations(OWLOntology ontoDescr, OntoModel hierarchicalModel) {
        for ( OWLInverseObjectPropertiesAxiom ax : ontoDescr.getAxioms( AxiomType.INVERSE_OBJECT_PROPERTIES ) ) {
            String fst = ax.getFirstProperty().asOWLObjectProperty().getIRI().toQuotedString();
            if ( ! ax.getSecondProperty().isAnonymous() ) {
                String sec = ax.getSecondProperty().asOWLObjectProperty().getIRI().toQuotedString();

                PropertyRelation first = hierarchicalModel.getProperty( fst );
                PropertyRelation second = hierarchicalModel.getProperty( sec );

                if ( first != null && second != null ) {
                    System.out.println( "INFO :: Marking " + first + " as Inverse" );
                    first.setInverse( true );
                }
            }
        }
    }

    private void setKeys(OWLOntology ontoDescr, OntoModel hierarchicalModel) {
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

    private void fixPropertyChains(OWLOntology ontoDescr, OntoModel hierarchicalModel) {
        ontoDescr.getClassesInSignature();
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


    private void applyPropertyRestrictions( OWLOntology ontoDescr, OntoModel hierarchicalModel ) {

        for ( PropertyRelation prop : hierarchicalModel.getProperties() ) {
            if ( prop.getTarget() == null ) {
                System.err.println("WARNING : Property without target concept " +  prop.getName() );
            }
        }


        Map<String, Set<OWLClassExpression>> supers = new HashMap<String, Set<OWLClassExpression>>();
        for ( OWLClass klass : ontoDescr.getClassesInSignature() ) {
            supers.put( klass.getIRI().toQuotedString(), new HashSet<OWLClassExpression>() );
        }
        for ( OWLClass klass : ontoDescr.getClassesInSignature() ) {
            if ( isDelegating( klass ) ) {
                OWLClassExpression delegate = aliases.get( klass );
                supers.get( delegate.asOWLClass().getIRI().toQuotedString() ).addAll( klass.getSuperClasses( ontoDescr ) );
            } else {
                supers.get( klass.asOWLClass().getIRI().toQuotedString() ).addAll( klass.getSuperClasses( ontoDescr ) );
            }

        }

        for ( Concept con : hierarchicalModel.getConcepts() ) {      //use concepts as they're sorted!


            if ( isDelegating( con.getIri() ) ) {
                continue;
            }

            if ( con == null ) {
                System.err.println("WARNING : Looking for superclasses of an undefined concept");
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

                processSuperClass( sup, con, hierarchicalModel );
            }


//            for ( PropertyRelation prop : con.getProperties().values() ) {
//                if ( prop.isRestricted() && ( prop.getMaxCard() == null || prop.getMaxCard() > 1 ) ) {
//                    prop.setName( prop.getName() + "s" );
//                }
//            }
        }

    }

    private void processSuperClass( OWLClassExpression sup, Concept con, OntoModel hierarchicalModel ) {
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
                tgt = primitives.get( forall.getFiller().asOWLDatatype().getIRI().toQuotedString()  );
                if ( tgt.equals( "xsd:anySimpleType" ) ) {
                    break;
                }
                rel = extractProperty( con, propIri, tgt, null, null, true );
                if ( rel != null ) {
//                    hierarchicalModel.addProperty( rel );
                } else {
                    System.err.println("WARNING : Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case DATA_MIN_CARDINALITY:
                OWLDataMinCardinality min = (OWLDataMinCardinality) sup;
                propIri = min.getProperty().asOWLDataProperty().getIRI().toQuotedString();
                tgt = primitives.get( min.getFiller().asOWLDatatype().getIRI().toQuotedString()  );
                rel = extractProperty( con, propIri, tgt, min.getCardinality(), null, false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    System.err.println("WARNING : Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case DATA_MAX_CARDINALITY:
                OWLDataMaxCardinality max = (OWLDataMaxCardinality) sup;
                propIri = max.getProperty().asOWLDataProperty().getIRI().toQuotedString();
                tgt = primitives.get( max.getFiller().asOWLDatatype().getIRI().toQuotedString()  );
                rel = extractProperty( con, propIri, tgt, null, max.getCardinality(), false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    System.err.println("WARNING : Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case DATA_EXACT_CARDINALITY:
                OWLDataExactCardinality ex = (OWLDataExactCardinality) sup;
                propIri = ex.getProperty().asOWLDataProperty().getIRI().toQuotedString();
                tgt = primitives.get( ex.getFiller().asOWLDatatype().getIRI().toQuotedString()  );
                rel = extractProperty( con, propIri, tgt, ex.getCardinality(), ex.getCardinality(), false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    System.err.println("WARNING : Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case OBJECT_SOME_VALUES_FROM:
                OWLObjectSomeValuesFrom someO = (OWLObjectSomeValuesFrom) sup;
                propIri = someO.getProperty().asOWLObjectProperty().getIRI().toQuotedString();
                if ( filterAliases( someO.getFiller() ).isAnonymous() ) {
                    System.err.println( "WARNING : Complex unaliased restriction " + someO );
                    break;
                }
                tgt = conceptCache.get( filterAliases( someO.getFiller() ).asOWLClass().getIRI().toQuotedString() );
                rel = extractProperty( con, propIri, tgt, 1, null, false );
                if ( rel != null ) {
                    hierarchicalModel.addProperty( rel );
                } else {
                    System.err.println("WARNING : Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case OBJECT_ALL_VALUES_FROM:
                OWLObjectAllValuesFrom forallO = (OWLObjectAllValuesFrom) sup;
                propIri = forallO.getProperty().asOWLObjectProperty().getIRI().toQuotedString();
                if ( filterAliases( forallO.getFiller() ).isAnonymous() ) {
                    System.err.println( "WARNING : Complex unaliased restriction " + forallO );
                    break;
                }
                tgt = conceptCache.get( filterAliases( forallO.getFiller() ).asOWLClass().getIRI().toQuotedString() );
                if ( tgt.equals( "<http://www.w3.org/2002/07/owl#Thing>" ) ) {
                    break;
                }
                rel = extractProperty( con, propIri, tgt, null, null, true );
                if ( rel != null ) {
                } else {
                    System.err.println("WARNING : Could not find property " + propIri + " restricted in class " + con.getIri() );
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
                    System.err.println("WARNING : Could not find property " + propIri + " restricted in class " + con.getIri() );
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
                    System.err.println("WARNING : Could not find property " + propIri + " restricted in class " + con.getIri() );
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
                    System.err.println("WARNING : Could not find property " + propIri + " restricted in class " + con.getIri() );
                }
                break;
            case OBJECT_INTERSECTION_OF:
                OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) sup;
                for ( OWLClassExpression arg : and.asConjunctSet() ) {
                    processSuperClass( arg, con, hierarchicalModel );
                }
            default:
                System.err.println("WARNING : Cannot handle " + sup );
        }

    }


    private String createSuffix(String role, String name, boolean plural) {
        String type = NameUtils.map( name, true );
        if ( type.indexOf(".") >= 0 ) {
            type = type.substring( type.lastIndexOf(".") + 1 );
        }
        return type + ( plural ? ( type.endsWith("s") ? "es" : "s") : "" ); // + "As" + role;
    }

    private PropertyRelation extractProperty( Concept con, String propIri, Concept target, Integer min, Integer max, boolean restrictTarget ) {
        if ( target == null ) {
            System.err.println( "WARNING : Null target for property " + propIri );
        }

        String restrictedSuffix = createSuffix( con.getName(), target.getName(), true );
        String restrictedPropIri = propIri.replace(">", restrictedSuffix + ">");


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
        if ( rel != null ) {
            rel = cloneRel( rel );
        } else {
            rel = con.getProperties().get( restrictedPropIri );

            if ( rel != null ) {
                alreadyRestricted = true;
            } else {
                rel = inheritPropertyCopy( con, con, restrictedPropIri );
                if ( rel != null ) {
                    alreadyRestricted = true;
                } else {
                    rel = inheritPropertyCopy( con, con, propIri );
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
                    || target.getIri().equals("<http://www.w3.org/2000/01/rdf-schema#Literal>")
               ) {
                target = rel.getTarget();
                restrictedSuffix = createSuffix( con.getName(), target.getName(), true );
                restrictedPropIri = propIri.replace (">", restrictedSuffix + ">" );
                dirty = true;
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
                    rel.setName( rel.getBaseProperty().getName() + restrictedSuffix );
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

            return rel;

//            } else {
//                // not really a restriction, so keep the parent's
//                return null;
//            }
        }


        return rel;
    }




    private PropertyRelation inheritPropertyCopy( Concept original, Concept current, String propIri ) {
        PropertyRelation rel;
        for ( Concept sup : current.getSuperConcepts() ) {
//            System.err.println( "Looking for " +propIri + " among the ancestors of " + current.getName() + ", now try " + sup.getName() );
//            String key = propIri.replace( "As"+current.getName(), "As"+sup.getName() );
            String key = propIri;
            rel = sup.getProperties().get( key );
            if ( rel != null ) {
                System.err.println( "Found " +propIri + " in " + sup.getName() );

                return cloneRel( rel );
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
        clonedRel.setMaxCard(rel.getMaxCard() );
        clonedRel.setTarget( rel.getTarget() );
        clonedRel.setDomain( rel.getDomain() );
        clonedRel.setBaseProperty( rel );
        clonedRel.setRestricted( rel.isRestricted() );
        return clonedRel;

    }


    private void createAndAddBasicProperties( OWLOntology ontoDescr, OWLDataFactory factory, OntoModel hierarchicalModel ) {
        int missDomain = 0;
        int missDataRange = 0;
        int missObjectRange = 0;

        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature() ) {
            if ( ! dp.isOWLTopDataProperty() && ! dp.isOWLBottomDataProperty() ) {
                Set<OWLClassExpression> domains = dp.getDomains( ontoDescr.getImportsClosure() );
                if ( domains.isEmpty() ) {
                    domains.add( factory.getOWLThing() );
                    System.err.println( "WARNING Added missing domain for" + dp);
                    missDomain++;
                }
                Set<OWLDataRange> ranges = dp.getRanges( ontoDescr.getImportsClosure() );
                if ( ranges.isEmpty() ) {
                    ranges.add( factory.getRDFPlainLiteral() );
                    System.err.println( "WARNING Added missing range for" + dp);
                    missDataRange++;
                }

                for ( OWLClassExpression domain : domains ) {
                    for ( OWLDataRange range : ranges ) {
                        OWLClassExpression realDom = filterAliases( domain );
                        PropertyRelation rel = new PropertyRelation( realDom.asOWLClass().getIRI().toQuotedString(),
                                dp.getIRI().toQuotedString(),
                                range.asOWLDatatype().getIRI().toQuotedString(),
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


        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature() ) {
            if ( ! op.isOWLTopObjectProperty() && ! op.isOWLBottomObjectProperty() ) {
                Set<OWLClassExpression> domains = op.getDomains( ontoDescr.getImportsClosure() );
                if ( domains.isEmpty() ) {
                    domains.add( factory.getOWLThing() );
                    System.err.println( "WARNING Added missing domain for" + op);
                    missDomain++;
                }
                Set<OWLClassExpression> ranges = op.getRanges( ontoDescr.getImportsClosure() );
                if ( ranges.isEmpty() ) {
                    ranges.add( factory.getOWLThing() );
                    System.err.println( "WARNING Added missing range for" + op);
                    missObjectRange++;
                }

                for ( OWLClassExpression domain : domains ) {
                    for ( OWLClassExpression range : ranges ) {
                        OWLClassExpression realDom = filterAliases( domain );
                        OWLClassExpression realRan = filterAliases( range );

//                        if ( realDom.isAnonymous() || realRan.isAnonymous() ) {
//                            System.out.println( "SOMEthING WENt WRONG" + realDom );
//                        }
                        PropertyRelation rel = new PropertyRelation( realDom.asOWLClass().getIRI().toQuotedString(),
                                op.getIRI().toQuotedString(),
                                realRan.asOWLClass().getIRI().toQuotedString(),
                                props.get( op.getIRI().toQuotedString() ) );

                        Concept con = conceptCache.get( rel.getSubject() );
                        rel.setTarget( conceptCache.get(rel.getObject()) );
                        rel.setDomain( con );

                        con.addProperty( rel.getProperty(), rel.getName(), rel );
                        hierarchicalModel.addProperty( rel );
                    }
                }
            }
        }

        System.err.println("Misses : ");
        System.err.println(missDomain);
        System.err.println(missDataRange);
        System.err.println(missObjectRange);
    }


    private Map<OWLClassExpression,OWLClassExpression> buildAliasesForEquivalentClasses(OWLOntology ontoDescr) {
        Map<OWLClassExpression,OWLClassExpression> aliases = new HashMap<OWLClassExpression,OWLClassExpression>();
        Set<OWLEquivalentClassesAxiom> pool = new HashSet<OWLEquivalentClassesAxiom>();
        Set<OWLEquivalentClassesAxiom> temp = new HashSet<OWLEquivalentClassesAxiom>();


        for ( OWLClass klass : ontoDescr.getClassesInSignature() ) {
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

                if ( aliases.containsValue( first ) ) {
                    // add to existing eqSet, put reversed
                    // A->X,  add X->C          ==> A->X, C->X
                    removed = aliases.put( secnd, first );
                    if ( removed != null ) {
                        System.err.println( "WARNING : DOUBLE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2 );
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( aliases.containsValue( secnd ) ) {
                    // add to existing eqSet, put as is
                    // A->X,  add C->X          ==> A->X, C->X
                    removed = aliases.put( first, secnd );
                    if ( removed != null ) {
                        System.err.println( "WARNING : DOUBLE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2 );
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( aliases.containsKey( first ) ) {
                    // apply transitivity, reversed
                    // A->X,  add A->C          ==> A->X, C->X
                    removed = aliases.put( secnd, aliases.get( first ) );
                    if ( removed != null ) {
                        System.err.println( "WARNING : DOUBLE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2 );
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( aliases.containsKey( secnd ) ) {
                    // apply transitivity, as is
                    // A->X,  add C->A          ==> A->X, C->X
                    removed = aliases.put( first, aliases.get( secnd ) );
                    if ( removed != null ) {
                        System.err.println( "WARNING : DOUBLE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2 );
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( ! first.isAnonymous() ) {
                    removed = aliases.put( secnd, first );
                    if ( removed != null ) {
                        System.err.println( "WARNING : DOUBLE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2 );
                    }

                    stable = false;
                    temp.remove( eq2 );
                } else if ( ! secnd.isAnonymous() ) {
                    removed = aliases.put( first, secnd );
                    if ( removed != null ) {
                        System.err.println( "WARNING : DOUBLE KEY WHILE RESOLVING EQUALITIES" + removed + " for value " + eq2 );
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
            System.err.println( "WARNING : COULD NOT RESOLVE ANON=ANON EQUALITIES " + pool );
            for ( OWLEquivalentClassesAxiom eq2 : pool ) {
                List<OWLClassExpression> l = eq2.getClassExpressionsAsList();
                if ( ! (l.get(0).isAnonymous() && l.get(1).isAnonymous())) {
                    System.err.println( "WARNING : " + l + " EQUALITY WAS NOT RESOLVED" );
                }
            }
        }


        System.out.println("----------------------------------------------------------------------- " + aliases.size());
        for(Map.Entry<OWLClassExpression,OWLClassExpression> entry : aliases.entrySet()) {
            System.out.println( entry.getKey() + " == " + entry.getValue() );
        }
        System.out.println("-----------------------------------------------------------------------");

        return aliases;
    }

    private boolean isAbstract(OWLClassExpression clax) {
        return  clax.asOWLClass().getIRI().toQuotedString().contains("Filler");
    }






    private boolean processComplexDomainAndRanges(OWLOntology ontoDescr, OWLDataFactory factory) {
        boolean dirty = false;
        dirty |= processComplexDataPropertyDomains( ontoDescr, factory );
        dirty |= processComplexObjectPropertyDomains( ontoDescr, factory );
        dirty |= processComplexObjectPropertyRanges( ontoDescr, factory );
        return dirty;
    }


    private boolean processComplexObjectPropertyDomains(OWLOntology ontoDescr, OWLDataFactory factory ) {
        boolean dirty = false;
        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature() ) {
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
                System.err.println(" WARNING : Property " + op + " should have a single domain class, found " + op.getDomains( ontoDescr ) );
            }

            for ( OWLClassExpression dom : op.getDomains( ontoDescr ) ) {
                if ( dom.isAnonymous() ) {
                    OWLClass domain = factory.getOWLClass( IRI.create(
                            NameUtils.separatingName( ontoDescr.getOntologyID().getOntologyIRI().getStart() ) +
                            NameUtils.capitalize( typeName ) +
                            "Domain" ) );
                    OWLAnnotationAssertionAxiom ann = factory.getOWLAnnotationAssertionAxiom( factory.getOWLAnnotationProperty( IRI.create( "http://www.w3.org/2000/01/rdf-schema#comment" ) ),
                            domain.getIRI(),
                            factory.getOWLStringLiteral("abstract") );
                    System.out.println("REPLACED ANON DOMAIN " + op + " with " + domain + ", was " + dom);
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( domain ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( domain, dom ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, ontoDescr.getObjectPropertyDomainAxioms( op ).iterator().next() ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLObjectPropertyDomainAxiom( op, domain ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, ann ) );
                    dirty = true;
                }
            }

        }
        return dirty;
    }


    private boolean processComplexDataPropertyDomains(OWLOntology ontoDescr, OWLDataFactory factory ) {
        boolean dirty = false;
        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature() ) {
            String typeName = NameUtils.buildNameFromIri( dp.getIRI().getStart(), dp.getIRI().getFragment() );

            Set<OWLDataPropertyDomainAxiom> domains = ontoDescr.getDataPropertyDomainAxioms(dp);
            if ( domains.size() > 1 ) {
                Set<OWLClassExpression> domainClasses = new HashSet<OWLClassExpression>();
                for ( OWLDataPropertyDomainAxiom dom : domains ) {
                    domainClasses.add( dom.getDomain() );
                }
                OWLObjectIntersectionOf and = factory.getOWLObjectIntersectionOf( domainClasses );

                for ( OWLDataPropertyDomainAxiom dom : domains ) {
                    ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, dom ) );
                }
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDataPropertyDomainAxiom(dp, and) ) );
                dirty = true;
            }

            if ( dp.getDomains( ontoDescr ).size() > 1 ) {
                System.err.println(" WARNING : Property " + dp + " should have a single domain class, found " + dp.getDomains( ontoDescr ) );
            }

            for ( OWLClassExpression dom : dp.getDomains( ontoDescr ) ) {
                if ( dom.isAnonymous() ) {
                    OWLClass domain = factory.getOWLClass( IRI.create(
                            NameUtils.separatingName( ontoDescr.getOntologyID().getOntologyIRI().getStart() ) +
                            NameUtils.capitalize( typeName ) +
                            "Domain" ) );
                    OWLAnnotationAssertionAxiom ann = factory.getOWLAnnotationAssertionAxiom( factory.getOWLAnnotationProperty( IRI.create( "http://www.w3.org/2000/01/rdf-schema#comment" ) ),
                            domain.getIRI(),
                            factory.getOWLStringLiteral("abstract") );
                    System.out.println("INFO : REPLACED ANON DOMAIN " + dp + " with " + domain + ", was " + dom);
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( domain ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( domain, dom ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, ontoDescr.getDataPropertyDomainAxioms(dp).iterator().next() ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDataPropertyDomainAxiom(dp, domain) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, ann ) );
                    dirty = true;
                }
            }

        }
        return dirty;
    }


    private boolean processComplexObjectPropertyRanges( OWLOntology ontoDescr, OWLDataFactory factory ) {
        System.out.println("Im in");
        boolean dirty = false;
        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature() ) {
            String typeName = NameUtils.buildNameFromIri( op.getIRI().getStart(), op.getIRI().toString() );

            Set<OWLObjectPropertyRangeAxiom> ranges = ontoDescr.getObjectPropertyRangeAxioms(op);
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
                System.err.println(" WARNING : Property " + op + " should have a single range class, found " + op.getRanges(ontoDescr) );
            }

            for ( OWLClassExpression ran : op.getRanges(ontoDescr) ) {
                if ( ran.isAnonymous() ) {
                    OWLClass range = factory.getOWLClass( IRI.create(
                            NameUtils.separatingName( ontoDescr.getOntologyID().getOntologyIRI().getStart() ) +
                            NameUtils.capitalize( typeName ) +
                            "Range" ) );
                    OWLAnnotationAssertionAxiom ann = factory.getOWLAnnotationAssertionAxiom( factory.getOWLAnnotationProperty( IRI.create( "http://www.w3.org/2000/01/rdf-schema#comment" ) ),
                            range.getIRI(),
                            factory.getOWLStringLiteral("abstract") );
                    System.out.println("INFO : REPLACED ANON RANGE " + op + " with " + range + ", was " + ran );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( range ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( range, ran ) ) );
                    ontoDescr.getOWLOntologyManager().applyChange( new RemoveAxiom( ontoDescr, ontoDescr.getObjectPropertyRangeAxioms(op).iterator().next() ) );
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
        for ( OWLNamedIndividual ind : ontoDescr.getIndividualsInSignature() ) {
            
            declareAnonymousIndividualSupertypes( ontoDescr, factory, ind );
            
            Set<OWLClassExpression> types = ind.getTypes( ontoDescr );
            
            types = simplify( types, ontoDescr );
            if ( types.size() > 1 ) {
                OWLObjectIntersectionOf and = factory.getOWLObjectIntersectionOf( types );

                dirty = true;
                System.err.println(" WARNING : Individual " + ind + " got a new combined type " + and );
                OWLClass type = factory.getOWLClass( IRI.create( ind.getIRI().getStart() + NameUtils.compactUpperCase( ind.getIRI().getFragment() ) + "Type" ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLDeclarationAxiom( type ) ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLSubClassOfAxiom( type, and ) ) );
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLClassAssertionAxiom( type, ind ) ) );

                individualTypesCache.put( ind.getIRI().toQuotedString(), type.getIRI().toQuotedString() );
            } else {
                individualTypesCache.put( ind.getIRI().toQuotedString(), types.iterator().next().asOWLClass().getIRI().toQuotedString() );
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
                ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLSubClassOfAxiom( temp, type ) ) );
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


    private void processQuantifiedRestrictions( OWLOntology ontoDescr, OWLDataFactory factory ) {
        // infer domain / range from quantified restrictions...
        for ( OWLClass klassAx : ontoDescr.getClassesInSignature() ) {
            OWLClass klass = klassAx;
//            System.out.println("Taking a look at " + klass);

            for ( OWLClassExpression clax : klass.getSuperClasses( ontoDescr ) ) {
                clax = clax.getNNF();
//                System.out.println("\thas SuperXompakt " + clax);
                processQuantifiedRestrictionsInClass( klass, clax, ontoDescr, factory );
            }

            for ( OWLClassExpression clax : klass.getEquivalentClasses( ontoDescr ) ) {
                clax = clax.getNNF();
//                System.out.println("\thas SuperXompakt " + clax);
                processQuantifiedRestrictionsInClass( klass, clax, ontoDescr, factory );
            }
        }

    }

    private void processQuantifiedRestrictionsInClass(OWLClass klass, OWLClassExpression clax, OWLOntology ontology, OWLDataFactory fac) {
        final OWLClass inKlass = klass;
        final OWLDataFactory factory = fac;
        final OWLOntology ontoDescr = ontology;



        clax.accept( new OWLClassExpressionVisitor() {



            private void process( OWLClassExpression expr ) {
//                System.out.println("\t\t\t Visiting " + expr);

                if ( expr instanceof OWLNaryBooleanClassExpression ) {
                    for (OWLClassExpression clax : ((OWLNaryBooleanClassExpression) expr).getOperandsAsList() ) {
                        process( clax );
                    }
                } else if ( expr instanceof OWLQuantifiedObjectRestriction  ) {

                    OWLQuantifiedObjectRestriction rest = (OWLQuantifiedObjectRestriction) expr;
                    OWLObjectProperty prop = rest.getProperty().asOWLObjectProperty();
                    OWLClassExpression fil = rest.getFiller();
                    if ( fil.isAnonymous() ) {
                        System.out.println("INFO :MARKED ANON FILLER OF" + rest );
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
                            System.out.println("INFO : REUSED FILLER FOR" + fil );
                        }

                        ontoDescr.getOWLOntologyManager().applyChange(new AddAxiom(ontoDescr, factory.getOWLDeclarationAxiom(filler)));
                        ontoDescr.getOWLOntologyManager().applyChange( new AddAxiom( ontoDescr, factory.getOWLEquivalentClassesAxiom( filler, fil ) ) );

                    }

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
                    //                            System.out.println( expr );
                }
                else return;
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
                throw new UnsupportedOperationException();
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



    }


    private void fillPropNamesInDataStructs( OWLOntology ontoDescr ) {
        for ( OWLDataProperty dp : ontoDescr.getDataPropertiesInSignature() ) {
            if ( ! dp.isTopEntity() && ! dp.isBottomEntity() ) {
                String propIri = dp.getIRI().toQuotedString();
                String propName = NameUtils.buildLowCaseNameFromIri( dp.getIRI().getFragment() );
                if ( propName == null ) {
                    propName = NameUtils.buildLowCaseNameFromIri( dp.getIRI().getStart() );
                }
                props.put( propIri, propName );
            }
        }

        for ( OWLObjectProperty op : ontoDescr.getObjectPropertiesInSignature() ) {
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


    private void reportStats(OWLOntology ontoDescr) {

        System.out.println( " *** Stats for ontology  : " + ontoDescr.getOntologyID() );

        System.out.println( " Number of classes " + ontoDescr.getClassesInSignature().size() );
        System.out.println( " \t Number of classes " + ontoDescr.getClassesInSignature().size() );

        System.out.println( " Number of datatypes " + ontoDescr.getDatatypesInSignature().size() );

        System.out.println( " Number of dataProps " + ontoDescr.getDataPropertiesInSignature().size() );
        System.out.println( "\t Number of dataProp domains " + ontoDescr.getAxiomCount( AxiomType.DATA_PROPERTY_DOMAIN ));
        for ( OWLDataProperty p : ontoDescr.getDataPropertiesInSignature() ) {
            int num = ontoDescr.getDataPropertyDomainAxioms( p ).size();
            if ( num != 1 ) {
                System.out.println( "\t\t Domain" + p + " --> " + num );
            } else {
                OWLDataPropertyDomainAxiom dom = ontoDescr.getDataPropertyDomainAxioms( p ).iterator().next();
                if ( ! ( dom.getDomain() instanceof OWLClass ) ) {
                    System.out.println( "\t\t Complex Domain"  + p + " --> " + dom.getDomain() );
                }
            }
        }
        System.out.println( "\t Number of dataProp ranges " + ontoDescr.getAxiomCount( AxiomType.DATA_PROPERTY_RANGE ));
        for ( OWLDataProperty p : ontoDescr.getDataPropertiesInSignature() ) {
            int num = ontoDescr.getDataPropertyRangeAxioms( p ).size();
            if ( num != 1 ) {
                System.out.println( "\t\t Range" + p + " --> " + num );
            } else {
                OWLDataPropertyRangeAxiom range = ontoDescr.getDataPropertyRangeAxioms( p ).iterator().next();
                if ( ! ( range.getRange() instanceof OWLDatatype ) ) {
                    System.out.println( "\t\t Complex Range" + p + " --> " + range.getRange()  );
                }
            }
        }

        System.out.println( " Number of objProps " + ontoDescr.getObjectPropertiesInSignature().size() );
        System.out.println( "\t Number of objProp domains " + ontoDescr.getAxiomCount( AxiomType.OBJECT_PROPERTY_DOMAIN ));
        for ( OWLObjectProperty p : ontoDescr.getObjectPropertiesInSignature() ) {
            int num = ontoDescr.getObjectPropertyDomainAxioms( p ).size();
            if ( num != 1 ) {
                System.out.println( "\t\t Domain" + p + " --> " + num );
            } else {
                OWLObjectPropertyDomainAxiom dom = ontoDescr.getObjectPropertyDomainAxioms( p ).iterator().next();
                if ( ! ( dom.getDomain() instanceof OWLClass ) ) {
                    System.out.println( "\t\t Complex Domain" + p + " --> " + dom.getDomain()  );
                }
            }
        }
        System.out.println( "\t Number of objProp ranges " + ontoDescr.getAxiomCount( AxiomType.OBJECT_PROPERTY_RANGE ));
        for ( OWLObjectProperty p : ontoDescr.getObjectPropertiesInSignature() ) {
            int num = ontoDescr.getObjectPropertyRangeAxioms( p ).size();
            if ( num != 1 ) {
                System.out.println( "\t\t Range" + p + " --> " + num );
            }  else {
                OWLObjectPropertyRangeAxiom range = ontoDescr.getObjectPropertyRangeAxioms( p ).iterator().next();
                if ( ! ( range.getRange() instanceof OWLClass ) ) {
                    System.out.println( "\t\t Complex Domain" + p + " --> " + range.getRange()  );
                }
            }
        }

//        System.exit(0);





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


    @Override
    protected OntoModel buildClassLattice(OWLOntology ontoDescr, StatefulKnowledgeSession kSession, Map<InferenceTask, Resource> theory, OntoModel baseModel) {

        boolean dirty = true;
        OWLDataFactory factory = ontoDescr.getOWLOntologyManager().getOWLDataFactory();
        addResource(kSession, theory.get(InferenceTask.CLASS_LATTICE_PRUNE));
        kSession.setGlobal("latticeModel", baseModel);

        launchReasoner( dirty, kSession, ontoDescr );


        // reify complex domains and ranges
        dirty |= processComplexDomainAndRanges( ontoDescr, factory );

        /************************************************************************************************************************************/

        // check individuals for multiple inheritance
        dirty |= preProcessIndividuals( ontoDescr, ontoDescr.getOWLOntologyManager().getOWLDataFactory() );

        /************************************************************************************************************************************/

        // reify complex restriction fillers
        processQuantifiedRestrictions( ontoDescr, factory );

        /************************************************************************************************************************************/

        // new classes have been added, classify the
        launchReasoner(dirty, kSession, ontoDescr);

        /************************************************************************************************************************************/

        // resolve aliases, choosing delegators
        aliases = buildAliasesForEquivalentClasses( ontoDescr );

        /************************************************************************************************************************************/

        reportStats( ontoDescr );


        // classes are stable now
        addConceptsToModel(kSession, ontoDescr, baseModel);

        // lattice is too. applies aliasing
        addSubConceptsToModel( kSession, ontoDescr, baseModel );


        kSession.fireAllRules();


        return baseModel;
    }

    private void fixRootHierarchy(OntoModel model) {
        Concept thing = model.getConcept( IRI.create( NamespaceUtils.getNamespaceByPrefix( "owl" ).getURI() + "#Thing"  ).toQuotedString() );
        if ( thing.getProperties().size() > 0 ) {
            Concept localRoot = new Concept( IRI.create( model.getDefaultNamespace() + "#Thing" ), "Thing", false );
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
                if ( prop.getDomain() == thing ) {
                    prop.setDomain( localRoot );
                }
                if ( prop.getTarget() == thing ) {
                    prop.setTarget( localRoot );
                }
            }
        }
    }


    private void processSubConceptAxiom( OWLClassExpression subClass, OWLClassExpression superClass, StatefulKnowledgeSession kSession, String thing ) {
        if ( ! superClass.isAnonymous() ) {

            String sub = filterAliases( subClass ).asOWLClass().getIRI().toQuotedString();
            String sup = isDelegating( superClass ) ?
                    thing : filterAliases( superClass ).asOWLClass().getIRI().toQuotedString();

            SubConceptOf subcon = new SubConceptOf( sub, sup );
            kSession.insert(subcon);
        } else if ( superClass instanceof OWLObjectIntersectionOf ) {
            OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) superClass;
            for ( OWLClassExpression ex : and.asConjunctSet() ) {
                processSubConceptAxiom( subClass, ex, kSession, thing );
            }
        }
    }

    private void addSubConceptsToModel(StatefulKnowledgeSession kSession, OWLOntology ontoDescr, OntoModel model) {

        kSession.fireAllRules();

        String thing = ontoDescr.getOWLOntologyManager().getOWLDataFactory().getOWLThing().getIRI().toQuotedString();

        for ( OWLSubClassOfAxiom ax : ontoDescr.getAxioms( AxiomType.SUBCLASS_OF ) ) {
            processSubConceptAxiom( ax.getSubClass(), ax.getSuperClass(), kSession, thing );
        }

        for ( OWLClassExpression delegator : aliases.keySet() ) {
            if ( ! delegator.isAnonymous() ) {
                SubConceptOf subcon = new SubConceptOf( delegator.asOWLClass().getIRI().toQuotedString() , thing );
                kSession.insert(subcon);
            }
        }

        for ( OWLClassExpression delegator : aliases.keySet() ) {
            if ( ! delegator.isAnonymous() ) {
                OWLClassExpression delegate = aliases.get( delegator );
                SubConceptOf subcon = new SubConceptOf( delegate.asOWLClass().getIRI().toQuotedString() , delegator.asOWLClass().getIRI().toQuotedString() );
                conceptCache.get(subcon.getSubject()).getEquivalentConcepts().add( conceptCache.get( subcon.getObject() ) );
                kSession.insert(subcon);
            }
        }

        kSession.fireAllRules();

        System.out.println(" >>>>>>>>>> ");
        for ( Object s : kSession.getObjects(new ClassObjectFilter(SubConceptOf.class))) {
            System.out.println( " >>>> " + s );
            SubConceptOf subConceptOf = (SubConceptOf) s;
            model.addSubConceptOf( subConceptOf );
            conceptCache.get( subConceptOf.getSubject() ).addSuperConcept( conceptCache.get( subConceptOf.getObject() ) );

        }
        System.out.println(" >>>>>>>>>> ");
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


    private void addConceptsToModel(StatefulKnowledgeSession kSession, OWLOntology ontoDescr, OntoModel baseModel) {
        Set<OWLClass> kis = ontoDescr.getClassesInSignature();
        Set dek = ontoDescr.getAxioms( AxiomType.DECLARATION );

        for ( OWLClass con : ontoDescr.getClassesInSignature() ) {
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
//                System.out.println(" ADD concept " + con.getIRI() );
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




    private void launchReasoner( boolean dirty, StatefulKnowledgeSession kSession, OWLOntology ontoDescr ) {
        if ( dirty ) {
            long now = new Date().getTime();
            System.err.println( " START REASONER " );

            initReasoner( kSession, ontoDescr );
//            owler.flush();

            reasoner.fillOntology( ontoDescr.getOWLOntologyManager(), ontoDescr );

            System.err.println( " STOP REASONER : time elapsed >> " + ( new Date().getTime() - now ) );

        } else {
            System.err.println( " REASONER NOT NEEDED" );
        }


    }




    protected void initReasoner( StatefulKnowledgeSession kSession, OWLOntology ontoDescr ) {

        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);


        switch ( externalReasoner ) {
//            case PELLET: reasoner = new InferredOntologyGenerator( PelletReasonerFactory.getInstance().createReasoner( ontoDescr ) ) ;
//                break;
            case HERMIT:
            default:
                OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
                if ( owler == null ) {
                    owler = reasonerFactory.createNonBufferingReasoner( ontoDescr, config );
                    owler.precomputeInferences( InferenceType.CLASS_HIERARCHY,
                                             InferenceType.CLASS_ASSERTIONS,

                                             InferenceType.OBJECT_PROPERTY_ASSERTIONS,
                                             InferenceType.DATA_PROPERTY_ASSERTIONS,

                                             InferenceType.DIFFERENT_INDIVIDUALS,
                                             InferenceType.SAME_INDIVIDUAL,

                                             InferenceType.DISJOINT_CLASSES,

         //                                        InferenceType.DATA_PROPERTY_HIERARCHY,
                                             InferenceType.OBJECT_PROPERTY_HIERARCHY
                     );

                    reasoner = new InferredOntologyGenerator( owler );
                    for ( InferredAxiomGenerator ax : reasoner.getAxiomGenerators() ) {
                        if ( ax instanceof InferredSubDataPropertyAxiomGenerator ) {
                            reasoner.removeGenerator( ax );
                        }
                    }

                }
        }

    }


}
