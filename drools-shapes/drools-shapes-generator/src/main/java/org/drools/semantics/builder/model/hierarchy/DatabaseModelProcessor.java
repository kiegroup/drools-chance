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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatabaseModelProcessor implements ModelHierarchyProcessor {

    public void process( OntoModel model ) {
        Concept thing = model.getConcept( Thing.IRI );
        for ( Concept con : model.getConcepts() ) {

            con.setChosenProperties(new HashMap(con.getProperties()));

            Map<String, PropertyRelation> baseProps = con.getChosenProperties();
            Set<Concept> superConcepts = con.getSuperConcepts();
            for ( Concept sup : superConcepts ) {
                Map<String,PropertyRelation> inheritedProperties = sup.getChosenProperties();
                for ( String propKey : inheritedProperties.keySet() ) {
                    if ( ! baseProps.containsKey( propKey ) ) {
                        PropertyRelation rel = inheritedProperties.get( propKey ).clone();

                        List<PropertyRelation> localRestrictions = new ArrayList<PropertyRelation>( rel.getRestrictedProperties() );
                        for ( PropertyRelation pr : rel.getRestrictedProperties() ) {
                            if ( ! baseProps.containsValue( pr ) ) {
                                localRestrictions.remove( pr );
                            }
                        }

                        Integer min = 0;
                        Integer max = 0;
                        for ( PropertyRelation pr : localRestrictions ) {
                            min = min + pr.getMinCard();
                            max = ( max == null || pr.getMaxCard() == null ) ?
                                  null : max + pr.getMaxCard();
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
                    if ( i <= 1 ) {
                        prop.setSimple( true );
                    }
                }
                if ( prop.isRestricted() ) {
                    con.removeProperty( prop.getProperty() );
                    con.getChosenProperties().remove( prop.getProperty() );
                    model.removeProperty( prop );
                }
            }

            List<Concept> supers = new ArrayList( con.getSuperConcepts() );
            for ( Concept sup : supers ) {
                if ( sup.isAbstrakt() ) {
                    con.getSuperConcepts().remove( sup );
                    con.getProperties().putAll( sup.getProperties() );
                }
            }
        }

        List<Concept> cons = new ArrayList( model.getConcepts() );
        for ( Concept con : cons ) {
            if ( con.isAbstrakt() ) {
                model.removeConcept( con );
            }
        }

    }
}
