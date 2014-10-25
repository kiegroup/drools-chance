/*
 * Copyright 2013 JBoss Inc
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

package org.drools.semantics.builder.model.hierarchy;


import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.w3._2002._07.owl.Thing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatabaseModelProcessor implements ModelHierarchyProcessor {

    public void process( OntoModel model ) {
        Concept thing = model.getConcept( Thing.IRI );


        for ( Concept con : model.getConcepts() ) {
            for ( PropertyRelation prop : con.getAvailableProperties() ) {
                if ( prop.getTarget().isAbstrakt() || prop.getTarget().isAnonymous() ) {
                    con.removeProperty( prop.getProperty() );

                    Concept tgt = prop.getTarget();
                        for ( Concept sub : flatten( tgt.getSubConcepts() ) ) {
                            String subName = prop.getBaseProperty().getName() + sub.getName();
                            String subPropKey = prop.getProperty().replace( prop.getName(), subName );
                            PropertyRelation copy = prop.clone();

                            copy.setTarget( sub );
                            copy.setDomain( con );
                            copy.setSubject( con.getIri() );
                            copy.setObject( sub.getIri() );
                            copy.setName( subName );
                            copy.setProperty( subPropKey );
                            copy.setBaseProperty( copy );
                            copy.setRestricted( false );

                            if ( prop.getInverse() != null ) {
                                if ( ! ( prop.getInverse().getTarget().isAbstrakt() || prop.getInverse().getTarget().isAnonymous() ) ) {
                                    PropertyRelation inverse = prop.getInverse();
                                    PropertyRelation localInverse = inverse.clone();
                                    for ( PropertyRelation restrictedRel : inverse.getRestrictedProperties() ) {
                                        if ( restrictedRel.getTarget().equals( con ) && restrictedRel.getDomain().equals( sub ) ) {
                                            localInverse.setMaxCard( restrictedRel.getMaxCard() );
                                            localInverse.setMinCard( restrictedRel.getMinCard() );
                                            localInverse.setSimple( localInverse.getMaxCard() != null && localInverse.getMaxCard() == 1 );
                                            localInverse.setFunctional( restrictedRel.isFunctional() );
                                            localInverse.getRestrictedProperties().clear();
                                        }
                                    }

                                    localInverse.setBaseProperty( localInverse );
                                    localInverse.setDomain( sub );
                                    sub.addProperty( localInverse.getProperty(), localInverse.getName(), localInverse );

                                    localInverse.setInverse( copy );
                                    copy.setInverse( localInverse );
                                }
                            }

                            con.addProperty( copy.getProperty(), copy.getName(), copy );
                            model.addProperty( copy );
                    }
                }
            }

        }


        for ( Concept con : model.getConcepts() ) {
            for ( PropertyRelation prop : con.getAvailableProperties() ) {
                if ( ! ( con.isAbstrakt() || con.isAnonymous() ) && ( prop.getTarget().isAbstrakt() || prop.getTarget().isAnonymous() ) ) {
                    throw new IllegalStateException( "Con " + con.getName() + " property " + prop.getName() + " anonymous range could not be normalized " + prop.getTarget() );
                }
            }
            for ( PropertyRelation prop : con.getChosenProperties().values() ) {
                if ( ! ( con.isAbstrakt() || con.isAnonymous() ) && prop.getTarget().isAbstrakt() || prop.getTarget().isAnonymous() ) {
                    throw new IllegalStateException( "Con " + con.getName() + " property " + prop.getName() + " anonymous range could not be normalized " + prop.getTarget() );
                }
            }
            for ( PropertyRelation prop : con.getChosenProperties().values() ) {
                if ( ! ( con.isAbstrakt() || con.isAnonymous() ) && prop.getDomain().isAbstrakt() || prop.getDomain().isAnonymous() ) {
                    throw new IllegalStateException( "Con " + con.getName() + " property " + prop.getName() + " anonymous domain could not be normalized " + prop.getTarget() );
                }
            }
        }



        for ( Concept con : model.getConcepts() ) {

            con.setChosenProperties( new HashMap( con.getProperties() ) );

            Map<String, PropertyRelation> baseProps = con.getChosenProperties();
            Set<Concept> superConcepts = con.getSuperConcepts();
            for ( Concept sup : superConcepts ) {
                Map<String,PropertyRelation> inheritedProperties = sup.getChosenProperties();
                for ( String propKey : inheritedProperties.keySet() ) {
                    if ( ! baseProps.containsKey( propKey ) ) {
                        PropertyRelation rel = inheritedProperties.get( propKey ).clone();

                        List<PropertyRelation> localRestrictions = new ArrayList<PropertyRelation>( rel.getRestrictedProperties() );

                        Integer min = localRestrictions.isEmpty() ? rel.getMinCard() : 0;
                        Integer max;
                        if ( localRestrictions.isEmpty() ) {
                            max = rel.getMaxCard();
                        } else {
                            max = 0;
                        }
                        for ( PropertyRelation pr : localRestrictions ) {
                            min = min + pr.getMinCard();
                            max = ( max == null || pr.getMaxCard() == null ) ?
                                  null : max + pr.getMaxCard();
                        }

                        if ( ! con.isAnonymous() && rel.getDomain().isAnonymous() ) {
                            rel.setDomain( con );
                        }

                        rel.setMinCard( min );
                        rel.setMaxCard( max );

                        baseProps.put( propKey, rel );
                    }
                }
            }

            for ( PropertyRelation prop : baseProps.values() ) {
                if ( prop.getMaxCard() != null && prop.getMaxCard() <= 1 ) {
                    prop.setSimple( true );
                }
            }

            if ( con != thing ) {
                con.addSuperConcept( thing );
                con.setChosenSuperConcept( thing );
                thing.getChosenSubConcepts().add( con );
            }
        }



        for ( Concept con : model.getConcepts() ) {
            Collection<PropertyRelation> props = new ArrayList( con.getProperties().values() );
            for ( PropertyRelation prop : props ) {
                if ( ! prop.getRestrictedProperties().isEmpty() ) {
                    Integer i = 0;
                    for ( PropertyRelation restr : prop.getRestrictedProperties() ) {
                        if ( restr.getMaxCard() == null ) {
                            i = null;
                            break;
                        }
                        i = Math.max( i, restr.getMaxCard() );
                    }
                    prop.setMaxCard( i );
                    if ( i != null && i <= 1 ) {
                        prop.setSimple( true );
                    }
                }
                if ( prop.isRestricted() ) {
                    con.removeProperty( prop.getProperty() );
                    con.getChosenProperties().remove( prop.getProperty() );
                    model.removeProperty( prop );
                }
            }
        }

        for ( Concept con : model.getConcepts() ) {
            List<Concept> supers = new ArrayList( con.getSuperConcepts() );
            for ( Concept sup : supers ) {
                if ( sup.isAbstrakt() ) {
                    con.getSuperConcepts().remove( sup );
                    for ( String propKey : sup.getProperties().keySet() ) {
                        if ( ! con.getProperties().containsKey( propKey ) ) {
                            PropertyRelation prop = sup.getProperty( propKey );
                            prop = prop.clone();
                            if ( prop.getDomain().isAnonymous() ) {
                                prop.setDomain( con );
                                prop.setBaseProperty( prop );
                                if ( ! prop.getRestrictedProperties().isEmpty() ) {
                                    Integer i = 0;
                                    for ( PropertyRelation restr : prop.getRestrictedProperties() ) {
                                        if ( restr != prop ) {
                                            if ( restr.getMaxCard() == null ) {
                                                i = null;
                                                break;
                                            }
                                            i = Math.max( i, restr.getMaxCard() );
                                        }
                                    }
                                    prop.setMaxCard( i );
                                    if ( i != null && i <= 1 ) {
                                        prop.setSimple( true );
                                    }
                                }

                            } else {
                                // Can this ever happen?
                                throw new UnsupportedOperationException( "TODO..." );
                            }

                            con.addProperty( propKey, prop.getName(), prop );
                        }
                    }

                }
            }
        }

        List<Concept> cons = new ArrayList( model.getConcepts() );
        for ( Concept con : cons ) {
            if ( con.isAbstrakt() || con.isAnonymous() ) {
                model.removeConcept( con );

                if ( ! con.getSubConcepts().isEmpty() ) {
                    for ( Concept child : new HashSet<Concept>( con.getSubConcepts() ) ) {
                        for ( Concept grandpa : new HashSet<Concept>( con.getSuperConcepts() ) ) {
                            grandpa.getSubConcepts().remove( con );
                            child.getSuperConcepts().remove( con );
                            grandpa.getSubConcepts().add( child );
                            child.addSuperConcept( grandpa );
                        }
                    }
                }
            }
        }

    }


    private Set<Concept> flatten( Set<Concept> subConcepts ) {
        Set<Concept> subs = new HashSet<Concept>();
        for ( Concept sub : subConcepts ) {
            if ( sub.isAnonymous() || sub.isAbstrakt() ) {
                subs.addAll( flatten( sub.getSubConcepts() ) );
            } else {
                subs.add( sub );
            }
        }
        return subs;
    }
}
